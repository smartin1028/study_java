"""
Oracle DB 연결 유틸리티 테스트

oracledb 의 AsyncConnectionPool, AsyncConnection 을 목킹하여
실제 Oracle DB 없이 OracleDB 클래스의 동작을 검증한다.

--- Java 비교: 테스트 더블 ---
  Java(Mockito)                      Python(unittest.mock)
  ────────────────────────────────────────────────────────
  @Mock OracleDB db;                 db = MagicMock() / AsyncMock()
  @InjectMocks TestTarget target;    (직접 주입)
  when(mock.method()).thenReturn(x)  mock.method.return_value = x
  verify(mock).method()              mock.method.assert_called_once()
  verify(mock, never()).method()     mock.method.assert_not_called()
  verify(mock, times(2)).method()    mock.method.assert_called()  # await_count == N
  any()                              mock.ANY  (assert_called_with 전용)

--- Java 비교: AsyncMock vs Mock ----
  Mockito 는 sync/async 메서드를 구분하지 않고 모든 호출을 검증할 수 있다.
  Python 에서는 await 이 필요한 메서드는 AsyncMock, 그렇지 않은 메서드는
  Mock 또는 MagicMock 을 사용해야 한다.

  oracledb Thin 모드 기준:
    await 필요: pool.acquire(), cursor.execute(), cursor.fetchall(),
               conn.commit(), conn.rollback(), pool.close(), pool.release()
    await 불필요: oracledb.create_pool_async(), conn.cursor()
    → 전자는 AsyncMock, 후자는 Mock

--- Java 비교: @pytest.fixture vs @BeforeEach ---
  Java(JUnit5):
      @BeforeEach
      void setUp() { cfg = new OracleConfig(...); }
      // 테스트마다 새로 실행됨. 필드로 공유.

  Python(pytest):
      @pytest.fixture
      def cfg(): return OracleConfig(...)
      // 테스트 함수 파라미터로 주입(DI). scope="function" 이 기본.

--- Java 비교: patch() == Mockito.mockStatic / mock + DI ---
  Java: try (var mocked = Mockito.mockStatic(OracleDB.class)) { ... }
  Python: with patch("database.oracle.oracledb.create_pool_async") as mock:
              mock.return_value = AsyncMock()
  patch() 은 문자열 경로로 대상을 지정하며, 블록 안에서만 치환된다.
  Java 의 MockedStatic 과 유사하지만, Python 은 import 경로 기반이다.
"""

import pytest
from unittest.mock import AsyncMock, MagicMock, Mock, patch

from database.oracle import OracleConfig, OracleDB


# ============================================================
# Fixture / 헬퍼
# ============================================================

@pytest.fixture
def cfg():
    """기본 OracleConfig"""
    return OracleConfig(
        host="localhost",
        port=1521,
        service_name="XEPDB1",
        user="app_user",
        password="secret",
    )


@pytest.fixture
def cfg_with_ro():
    """읽기 전용 계정이 포함된 OracleConfig"""
    return OracleConfig(
        host="db.example.com",
        port=1521,
        service_name="ORCLPDB1",
        user="app_user",
        password="secret",
        ro_user="app_readonly",
        ro_password="ro_secret",
    )


# ---- Java 비교: 테스트 헬퍼 메서드 ----
# Java: private HikariDataSource createMockPool() { ... }
# Python: 모듈 레벨 함수. _ 접두사는 "이 모듈 안에서만 쓴다"는 관례.
# Mock 객체 구성 방식은 Mockito 의 when().thenReturn() 과 유사하지만,
# Python 은 객체에 직접 속성을 할당하는 방식(mock.return_value = ...).
def _mock_pool() -> AsyncMock:
    """기본 AsyncConnectionPool Mock"""
    pool = AsyncMock()
    conn = AsyncMock()
    cursor = AsyncMock()
    # ---- Java: ResultSetMetaData.getColumnName() ----
    # cursor.description = [(컬럼명,), ...] 형태로 컬럼 정보 제공
    cursor.description = [("ID",), ("NAME",)]
    cursor.fetchall.return_value = [(1, "Alice"), (2, "Bob")]
    cursor.rowcount = 2
    # ---- 주의: conn.cursor() 는 await 이 아님 → Mock 사용 ----
    conn.cursor = Mock(return_value=cursor)
    pool.acquire.return_value = conn
    return pool


# ============================================================
# OracleConfig 테스트
# ============================================================

def test_config_dsn():
    """DSN 이 host:port/service_name 형식인지 검증"""
    c = OracleConfig(host="db1", port=1521, service_name="XEPDB1")
    assert c.dsn == "db1:1521/XEPDB1"


def test_config_has_readonly_user_true(cfg_with_ro):
    """ro_user, ro_password 가 모두 있으면 True"""
    assert cfg_with_ro.has_readonly_user is True


def test_config_has_readonly_user_false(cfg):
    """ro_user 가 비어있으면 False"""
    assert cfg.has_readonly_user is False


def test_config_has_readonly_user_partial():
    """ro_user 만 있고 password 가 없으면 False"""
    c = OracleConfig(ro_user="ro", ro_password="")
    assert c.has_readonly_user is False


# ============================================================
# OracleDB 생명주기 (start / close) 테스트
# ============================================================

# ---- Java 비교: @Test + Mockito ----
# Java:
#   @Test void testStartCreatesPool() {
#       try (var mocked = mockStatic(OracleDB.class)) {
#           when(OracleDB.createPoolAsync(...)).thenReturn(mockPool);
#           db.start();
#           verify(OracleDB).createPoolAsync(...);
#       }
#   }
# Python: with patch(...) as mock_create: 블록 안에서만 목이 유효.

@pytest.mark.asyncio
async def test_start_creates_pool(cfg):
    """start() 가 oracledb.create_pool_async 를 호출하는지 검증"""
    db = OracleDB(cfg)
    with patch("database.oracle.oracledb.create_pool_async") as mock_create:
        mock_create.return_value = AsyncMock()
        await db.start()
        mock_create.assert_called_once()
        assert db.is_ready is True


@pytest.mark.asyncio
async def test_start_creates_ro_pool_when_configured(cfg_with_ro):
    """읽기 전용 계정이 설정된 경우 별도 풀이 생성되는지 검증"""
    db = OracleDB(cfg_with_ro)
    with patch("database.oracle.oracledb.create_pool_async") as mock_create:
        mock_create.return_value = AsyncMock()
        await db.start()
        # ---- Java: verify(mock, times(2)).createPoolAsync(...) ----
        assert mock_create.call_count == 2


@pytest.mark.asyncio
async def test_start_no_ro_pool_without_ro_user(cfg):
    """읽기 전용 계정이 없으면 풀 하나만 생성되는지 검증"""
    db = OracleDB(cfg)
    with patch("database.oracle.oracledb.create_pool_async") as mock_create:
        mock_create.return_value = AsyncMock()
        await db.start()
        assert mock_create.call_count == 1


@pytest.mark.asyncio
async def test_close_pools(cfg):
    """
    close() 가 모든 풀을 종료하는지 검증

    ---- Java 비교: verify + await ----
    Java: verify(mockPool).close();  // 반환 타입만 맞으면 await 여부 무관
    Python: AsyncMock 은 await 호출만 추적한다.
            assert_awaited_once() ≈ verify(mock).method()
            assert_called_once()  ≈ verify(mock).method() (await 아닌 경우)
    """
    db = OracleDB(cfg)
    db._pool = AsyncMock()
    db._ro_pool = AsyncMock()
    await db.close()
    db._pool.close.assert_awaited_once()
    db._ro_pool.close.assert_awaited_once()


# ============================================================
# connection() 컨텍스트 매니저 테스트
# ============================================================

@pytest.mark.asyncio
async def test_connection_yields_and_releases(cfg):
    """
    connection() 이 연결을 획득하고 반환하는지 검증

    ---- Java 비교: AutoCloseable + verify ----
    Java:
        try (var conn = pool.acquire()) { /* use conn */ }
        verify(pool).release(conn);
    Python:
        async with db.connection() as c:
            ...
        db._pool.release.assert_awaited_once_with(conn)
    """
    db = OracleDB(cfg)
    db._pool = _mock_pool()
    conn = await db._pool.acquire()

    async with db.connection() as c:
        assert c is conn
    db._pool.release.assert_awaited_once_with(conn)


@pytest.mark.asyncio
async def test_connection_readonly_uses_ro_pool(cfg_with_ro):
    """readonly=True 일 때 읽기 전용 풀을 사용하는지 검증"""
    db = OracleDB(cfg_with_ro)
    rw_pool = _mock_pool()
    ro_pool = _mock_pool()
    db._pool = rw_pool
    db._ro_pool = ro_pool
    ro_conn = await ro_pool.acquire()

    async with db.connection(readonly=True) as c:
        assert c is ro_conn
    # 기본 풀은 사용되지 않아야 함
    # ---- Java: verify(rwPool, never()).acquire() ----
    rw_pool.acquire.assert_not_awaited()


@pytest.mark.asyncio
async def test_connection_readonly_fallback_set_transaction(cfg):
    """
    읽기 전용 계정이 없으면 SET TRANSACTION READ ONLY 가 실행되는지 검증

    ---- 배경: Oracle 읽기 전용 두 가지 전략 ----
    1. 별도 RO 계정 풀 사용 → SELECT 만 허용된 DB 유저로 연결
    2. SET TRANSACTION READ ONLY → 같은 계정이지만 트랜잭션을 읽기 전용으로 설정
    RO 계정이 설정되지 않은 경우 2번 전략으로 폴백한다.
    """
    db = OracleDB(cfg)
    db._pool = _mock_pool()
    conn = await db._pool.acquire()
    cursor = conn.cursor.return_value

    async with db.connection(readonly=True):
        pass
    # ---- Java: verify(cursor).execute("SET TRANSACTION READ ONLY") ----
    # assert_any_call 은 여러 호출 중 하나라도 매칭되면 통과
    cursor.execute.assert_any_call("SET TRANSACTION READ ONLY")
    cursor.execute.assert_any_call("COMMIT")


@pytest.mark.asyncio
async def test_connection_rollback_on_error(cfg):
    """
    예외 발생 시 rollback 이 호출되는지 검증

    ---- Java 비교: assertThrows + verify ----
    Java:
        assertThrows(ValueError.class, () -> {
            try (var c = db.connection()) {
                throw new ValueError("test error");
            }
        });
        verify(conn).rollback();
    """
    db = OracleDB(cfg)
    db._pool = _mock_pool()
    conn = await db._pool.acquire()

    with pytest.raises(ValueError, match="test error"):
        async with db.connection() as c:
            assert c is conn
            raise ValueError("test error")
    conn.rollback.assert_awaited_once()


@pytest.mark.asyncio
async def test_connection_before_start_raises(cfg):
    """
    start() 전에 connection() 호출 시 RuntimeError

    ---- Java 비교: IllegalStateException ----
    Java: if (!initialized) throw new IllegalStateException("start() first");
    Python: RuntimeError 가 Java 의 IllegalStateException 에 해당.
    """
    db = OracleDB(cfg)
    with pytest.raises(RuntimeError, match="start"):
        async with db.connection():
            pass


# ============================================================
# fetch_all / execute 헬퍼 메서드 테스트
# ============================================================

@pytest.mark.asyncio
async def test_fetch_all_returns_dict_list(cfg):
    """
    fetch_all 이 dict 리스트를 반환하는지 검증

    ---- Java 비교: RowMapper / ResultSetExtractor ----
    Java(Spring JdbcTemplate):
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    Python: cursor.description 에서 컬럼명 추출 → dict(zip(columns, row))
            OracleDB.fetch_all() 내부에서 이 변환을 수행한다.
    """
    db = OracleDB(cfg)
    db._pool = _mock_pool()

    rows = await db.fetch_all("SELECT id, name FROM users")
    assert rows == [{"id": 1, "name": "Alice"}, {"id": 2, "name": "Bob"}]


@pytest.mark.asyncio
async def test_fetch_all_with_params(cfg):
    """
    파라미터가 쿼리에 바인딩되는지 검증

    ---- Java 비교: PreparedStatement ----
    Java: pstmt.setInt(1, 1); pstmt.setString(2, "Alice");
    Python(oracledb): cursor.execute(sql, {"uid": 1}) — 이름 기반 바인딩
    """
    db = OracleDB(cfg)
    db._pool = _mock_pool()
    conn = await db._pool.acquire()

    await db.fetch_all("SELECT * FROM users WHERE id = :uid", {"uid": 1})
    cursor = conn.cursor.return_value
    cursor.execute.assert_called_with(
        "SELECT * FROM users WHERE id = :uid", {"uid": 1}
    )


@pytest.mark.asyncio
async def test_fetch_all_readonly_uses_ro(cfg_with_ro):
    """fetch_all(readonly=True) 가 읽기 전용 풀을 사용하는지 검증"""
    db = OracleDB(cfg_with_ro)
    rw_pool = _mock_pool()
    ro_pool = _mock_pool()
    db._pool = rw_pool
    db._ro_pool = ro_pool

    await db.fetch_all("SELECT 1 FROM dual", readonly=True)
    rw_pool.acquire.assert_not_awaited()
    ro_pool.acquire.assert_awaited()


@pytest.mark.asyncio
async def test_execute_commits_and_returns_rowcount(cfg):
    """
    execute 가 commit 후 rowcount 를 반환하는지 검증

    ---- Java 비교: JdbcTemplate.update() ----
    Java: int affected = jdbcTemplate.update("INSERT INTO ...", params);
          // Spring 이 내부적으로 commit + rowcount 반환
    Python: OracleDB.execute() 가 동일한 역할을 수행.
    """
    db = OracleDB(cfg)
    db._pool = _mock_pool()
    conn = await db._pool.acquire()

    affected = await db.execute("INSERT INTO users VALUES (:id, :name)", {"id": 3, "name": "Charlie"})
    assert affected == 2  # rowcount from mock
    conn.commit.assert_awaited_once()


@pytest.mark.asyncio
async def test_execute_readonly_raises(cfg_with_ro):
    """execute 는 읽기 전용 풀을 사용하지 않고 기본 풀을 사용하는지 검증"""
    db = OracleDB(cfg_with_ro)
    rw_pool = _mock_pool()
    ro_pool = _mock_pool()
    db._pool = rw_pool
    db._ro_pool = ro_pool

    await db.execute("INSERT INTO users VALUES (1, 'test')")
    ro_pool.acquire.assert_not_awaited()
    rw_pool.acquire.assert_awaited()


# ============================================================
# 실제 Oracle DB 연결이 없을 때의 안전성 검증
# ============================================================

@pytest.mark.asyncio
async def test_oracledb_integration_mocked():
    """
    oracledb.create_pool_async 를 목킹하여 실제 DB 없이
    from_settings() + start() + fetch_all() 전체 흐름 검증

    ---- Java 비교: 통합 테스트를 위한 Mock ----
    Java(Spring):
        @MockBean OracleDataSource dataSource;
        @Test void testFullFlow() { ... }
    Python: patch() 으로 oracledb.create_pool_async 자체를 치환하여
            전체 흐름을 실제 DB 연결 없이 검증한다.
    """
    with patch("database.oracle.oracledb.create_pool_async") as mock_create:
        pool = _mock_pool()
        mock_create.return_value = pool

        db = OracleDB.from_settings()
        await db.start()

        rows = await db.fetch_all("SELECT sysdate FROM dual")
        assert len(rows) == 2
        assert rows[0]["name"] == "Alice"

        await db.close()
        pool.close.assert_awaited_once()


def test_oracle_config_defaults():
    """
    기본값으로 생성한 OracleConfig 필드 검증

    ---- Java 비교: Lombok @Builder.Default / 기본 생성자 ----
    Java: OracleConfig config = new OracleConfig();  // 필드 기본값
    Python: OracleConfig() — dataclass 의 필드 기본값이 사용됨.
    """
    c = OracleConfig()
    assert c.host == "localhost"
    assert c.port == 1521
    assert c.service_name == "XEPDB1"
    assert c.user == "app_user"
    assert c.pool_min == 1
    assert c.pool_max == 5
    assert c.has_readonly_user is False
