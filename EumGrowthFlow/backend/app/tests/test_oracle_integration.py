"""
Oracle DB 통합 테스트 (실제 DB 연결)

.env 파일에 설정된 Oracle 접속 정보로 실제 DB 에 연결하여 테스트한다.
Oracle DB 가 가용하지 않은 경우 모든 테스트를 자동으로 건너뛴다.

--- Java 비교: 테스트 구조 ---
  Java(JUnit5)                          Python(pytest)
  ─────────────────────────────────────────────────────
  @BeforeEach void setUp()              @pytest_asyncio.fixture
                                        async def db(): ...
  @Test void testXxx()                  @pytest.mark.asyncio
                                        async def test_xxx(db): ...
  assertThrows(Exception.class, ...)    with pytest.raises(Exception): ...
  @DisabledIf("!dbConfigured")          @pytest.mark.skipif(not configured, ...)
  Assumptions.assumeTrue(cond)          pytest.skip("reason")
  fail("msg")                           assert False, "msg"

--- Java 비교: 비동기 ---
  Java에서는 CompletableFuture 나 @Async 로 비동기를 다루지만,
  Python 에서는 async/await 키워드가 언어 차원에서 지원된다.
  pytest-asyncio 플러그인이 async def test_* 함수를 자동으로 이벤트루프에서 실행해준다.

실행 전 .env 설정 예:
    ORACLE_HOST=localhost
    ORACLE_PORT=1521
    ORACLE_SERVICE_NAME=XEPDB1
    ORACLE_USER=app_user
    ORACLE_PASSWORD=your_password
    ORACLE_RO_USER=app_readonly      # (선택) RW 계정과 달라야 함
    ORACLE_RO_PASSWORD=ro_password   # (선택)

실행:
    pytest tests/test_oracle_integration.py -v -s
"""

import asyncio
import sys

import pytest
import pytest_asyncio

from config import settings
from database.oracle import OracleConfig, OracleDB


# ============================================================
# 사전 체크 (환경변수만 확인)
# ============================================================

# ---- Java 비교: 설정 읽기 ----
# Java: oracle.host=... 를 @Value 또는 Properties 로 읽어 OracleConfig POJO 에 매핑
# Python: dataclass 로 동일한 역할. settings.oracle_host 는 .env 파일에서 자동 로드됨.

def _get_config() -> OracleConfig:
    return OracleConfig(
        host=settings.oracle_host,
        port=settings.oracle_port,
        service_name=settings.oracle_service_name,
        user=settings.oracle_user,
        password=settings.oracle_password,
        ro_user=settings.oracle_ro_user,
        ro_password=settings.oracle_ro_password,
    )


_config = _get_config()
_DB_CONFIGURED = bool(_config.password)

if not _DB_CONFIGURED:
    print(
        "\n[Oracle 통합 테스트] ORACLE_PASSWORD 미설정 — 건너뜁니다.",
        file=sys.stderr,
    )
else:
    ro_info = f" ro_user={_config.ro_user}" if _config.has_readonly_user else ""
    print(
        f"\n[Oracle 통합 테스트] 설정됨: "
        f"{_config.host}:{_config.port}/{_config.service_name} "
        f"user={_config.user}{ro_info}",
        file=sys.stderr,
    )


# ---- Java 비교: 조건부 테스트 건너뛰기 ----
# Java(JUnit5): @DisabledIf("!dbConfigured") 또는
#   @BeforeAll static void check() { Assumptions.assumeTrue(password != null); }
# Python(pytest): 모듈 레벨 pytestmark 로 일괄 적용.
#   개별 테스트에만 적용하려면 @pytest.mark.skipif(...) 를 함수 위에 붙인다.
pytestmark = pytest.mark.skipif(
    not _DB_CONFIGURED,
    reason="Oracle DB 설정이 .env 에 없음 (ORACLE_PASSWORD)",
)


# ============================================================
# 각 테스트마다 새 DB 인스턴스 생성
# ============================================================

# ---- Java 비교: Fixture == @BeforeEach + @AfterEach + DI ----
# Java(JUnit5):
#   @BeforeEach
#   void setUp() { db = new OracleDB(config); db.start(); }
#   @AfterEach
#   void tearDown() { db.close(); }
#   // 테스트 메서드 파라미터로 주입 불가 → 필드로 공유
#
# Python(pytest):
#   @pytest_asyncio.fixture 로 정의한 함수는 각 테스트 함수의 파라미터로
#   주입(DI)된다. yield 전이 @BeforeEach, 후가 @AfterEach 역할.
#   scope 기본값은 "function" → 테스트마다 새로 생성됨.
#   (scope="session" 이면 @BeforeAll + @AfterAll 과 동일)
#
# 주의: pytest-asyncio strict 모드에서는 @pytest.fixture 가 아닌
#       @pytest_asyncio.fixture 를 사용해야 async fixture 가 동작한다.
@pytest_asyncio.fixture
async def db():
    """각 테스트 함수마다 새로운 OracleDB 인스턴스를 생성/해제"""
    _db = OracleDB(_config)
    try:
        await _db.start()
    except Exception as e:
        # ---- Java 비교: Assumptions.assumeTrue(false) 와 동일 ----
        # DB 연결 자체가 실패하면 테스트를 FAIL 이 아닌 SKIP 처리
        pytest.skip(f"Oracle DB 연결 실패: {e}")
    try:
        yield _db  # --- 여기서 제어가 테스트 함수로 넘어간다 ---
    finally:
        # --- 테스트 종료 후 (또는 예외 발생 시) 반드시 실행 ---
        # Java: try-finally 또는 @AfterEach 와 동일한 보장
        await _db.close()


# ============================================================
# 테스트
# ============================================================

# ---- Java 비교: @Test 와 async ----
# Java: @Test void testXxx() — CompletableFuture.get() 으로 블로킹
# Python: @pytest.mark.asyncio + async def → pytest 가 이벤트루프를 관리하며 실행

@pytest.mark.asyncio
async def test_ping_database(db):
    """SELECT sysdate FROM dual 로 DB 응답 확인"""
    rows = await db.fetch_all("SELECT sysdate FROM dual")
    assert len(rows) == 1
    assert "sysdate" in rows[0]


@pytest.mark.asyncio
async def test_connection_context_manager(db):
    """
    connection() 컨텍스트 매니저 정상 동작 확인

    ---- Java 비교: 컨텍스트 매니저 == try-with-resources ----
    Java: try (var conn = pool.acquire()) { ... }  // AutoCloseable
    Python: async with db.connection() as conn:   // __aenter__ / __aexit__
        ...
    블록을 벗어나면 자동으로 pool.release(conn) 이 호출된다.
    """
    async with db.connection() as conn:
        cursor = conn.cursor()
        await cursor.execute("SELECT 1 FROM dual")
        rows = await cursor.fetchall()
        assert rows[0][0] == 1


@pytest.mark.asyncio
async def test_readonly_connection_select(db):
    """읽기 전용 연결로 SELECT 정상 실행"""
    async with db.connection(readonly=True) as conn:
        cursor = conn.cursor()
        await cursor.execute("SELECT 1 FROM dual")
        rows = await cursor.fetchall()
        assert rows[0][0] == 1


# ---- Java 비교: assertThrows ----
# Java(JUnit5):
#   assertThrows(Exception.class, () -> conn.execute("INSERT ..."));
# Python(pytest):
#   with pytest.raises(Exception):
#       await conn.execute("INSERT ...")
# 컨텍스트 매니저 블록 안에서 예외가 발생해야 통과, 없으면 FAIL.
#
# @pytest.mark.asyncio
# async def test_readonly_connection_prevents_write(db):
#     """
#     읽기 전용 연결에서 DML 시도 시 오류 발생 확인
#
#     ---- 배경 지식: Oracle SET TRANSACTION READ ONLY ----
#     DDL(CREATE/DROP)은 암시적 커밋을 발생시키므로 읽기 전용 트랜잭션의
#     제약을 받지 않는다. 따라서 DML(INSERT)로 테스트해야 한다.
#     """
#     table = "test_tmp_rw"
#     # 먼저 일반 연결로 테이블 생성
#     async with db.connection() as conn:
#         try:
#             await conn.execute(
#                 f"CREATE TABLE {table} (id NUMBER, name VARCHAR2(100))"
#             )
#             await conn.commit()
#         except Exception:
#             pass
#     try:
#         async with db.connection(readonly=True) as conn:
#             with pytest.raises(Exception):
#                 await conn.execute(
#                     f"INSERT INTO {table} VALUES (1, 'ro_should_fail')"
#                 )
#     finally:
#         async with db.connection() as conn:
#             await conn.execute(f"DROP TABLE {table} PURGE")
#             await conn.commit()
#
#
# @pytest.mark.asyncio
# async def test_readwrite_connection_can_write(db):
#     """일반 연결로 DDL/DML 실행 가능 확인"""
#     table = "test_tmp_rw"
#     async with db.connection() as conn:
#         try:
#             await conn.execute(
#                 f"CREATE TABLE {table} (id NUMBER, name VARCHAR2(100))"
#             )
#             await conn.execute(
#                 f"INSERT INTO {table} VALUES (1, 'integration_test')"
#             )
#             await conn.commit()
#             cursor = conn.cursor()
#             await cursor.execute(f"SELECT name FROM {table} WHERE id = 1")
#             row = await cursor.fetchone()
#             assert row[0] == "integration_test"
#         finally:
#             await conn.execute(f"DROP TABLE {table} PURGE")
#             await conn.commit()
#
#
# @pytest.mark.asyncio
# async def test_fetch_all_with_params(db):
#     """
#     파라미터 바인딩 포함 fetch_all 검증
#
#     ---- Java 비교: PreparedStatement vs 바인딩 ----
#     Java(JDBC):
#         PreparedStatement pstmt = conn.prepareStatement(
#             "SELECT :val1 + :val2 AS result FROM dual");
#         pstmt.setInt(1, 10);
#         pstmt.setInt(2, 20);
#         ResultSet rs = pstmt.executeQuery();
#
#     Python(oracledb):
#         await cursor.execute(sql, {"val1": 10, "val2": 20})
#         # 이름 기반 바인딩. dict 대신 list 를 넘기면 위치 기반.
#
#     ---- Java 비교: ResultSet → dict ----
#     Java에서는 ResultSet → DTO 매핑 코드를 수동으로 작성하거나
#     MyBatis/JPA 에 위임한다.
#     Python 에서는 cursor.description 으로 컬럼명을 얻어 dict 로 변환한다.
#     (OracleDB.fetch_all() 에서 일괄 처리)
#     """
#     rows = await db.fetch_all(
#         "SELECT :val1 + :val2 AS result FROM dual",
#         {"val1": 10, "val2": 20},
#     )
#     assert rows[0]["result"] == 30
#
#
# @pytest.mark.asyncio
# async def test_execute_and_fetch_all_roundtrip(db):
#     """INSERT → SELECT 데이터 왕복 검증"""
#     table = "test_roundtrip"
#     async with db.connection() as conn:
#         try:
#             await conn.execute(
#                 f"CREATE TABLE {table} (id NUMBER, msg VARCHAR2(200))"
#             )
#             await conn.commit()
#
#             await db.execute(
#                 f"INSERT INTO {table} VALUES (:id, :msg)",
#                 {"id": 1, "msg": "hello oracle"},
#             )
#             rows = await db.fetch_all(
#                 f"SELECT msg FROM {table} WHERE id = 1"
#             )
#             assert rows[0]["msg"] == "hello oracle"
#         finally:
#             await conn.execute(f"DROP TABLE {table} PURGE")
#             await conn.commit()
#

@pytest.mark.asyncio
async def test_pool_reuses_connections(db):
    """
    커넥션 풀 재사용 (3회 반복)

    ---- Java 비교: HikariCP / UCP ----
    Java: HikariDataSource 로 풀 생성, getConnection() 으로 획득.
    Python(oracledb): create_pool_async() 로 풀 생성, pool.acquire() 로 획득.
    둘 다 내부적으로 커넥션을 재사용하며, 풀 설정(min/max) 개념이 동일하다.
    """
    for _ in range(3):
        rows = await db.fetch_all("SELECT 1 FROM dual")
        assert rows[0]["1"] == 1


@pytest.mark.asyncio
async def test_concurrent_connections(db):
    """
    동시 5개 연결 획득

    ---- Java 비교: 병렬 처리 ----
    Java:
        var futures = IntStream.range(0, 5)
            .mapToObj(i -> CompletableFuture.supplyAsync(() -> query()))
            .toList();
        CompletableFuture.allOf(futures.toArray(...)).join();

    Python:
        tasks = [query() for _ in range(5)]  # 코루틴 리스트 생성
        results = await asyncio.gather(*tasks)  # 동시 실행 + 결과 수집
        # asyncio.gather() ≈ CompletableFuture.allOf() + get()
    """
    async def query():
        rows = await db.fetch_all("SELECT sysdate FROM dual")
        return len(rows)

    tasks = [query() for _ in range(5)]
    results = await asyncio.gather(*tasks)
    assert results == [1, 1, 1, 1, 1]
