# Java → Python 테스트 패턴 비교

Java(JUnit5 + Mockito + Spring)에 익숙한 개발자를 위한 pytest 가이드.

---

## 1. 테스트 구조

| 개념 | Java | Python |
|---|---|---|
| 테스트 클래스 | `class XxxTest { }` | 모듈(파일) 자체가 테스트 묶음 |
| 테스트 메서드 | `@Test void testXxx()` | `def test_xxx():` |
| 픽스처 (setup) | `@BeforeEach void setUp()` | `@pytest.fixture` (yield 전) |
| 픽스처 (teardown) | `@AfterEach void tearDown()` | `@pytest.fixture` (yield 후) |
| 전체 생명주기 | `@BeforeAll` / `@AfterAll` | `@pytest.fixture(scope="session")` |
| DI | `@Autowired` / 생성자 주입 | 함수 파라미터로 fixture 주입 |

### Java
```java
class UserServiceTest {
    @Mock UserRepository repo;
    @InjectMocks UserService service;

    @BeforeEach
    void setUp() { service = new UserService(repo); }

    @Test
    void testFindById() {
        when(repo.findById(1)).thenReturn(Optional.of(new User("Alice")));
        User user = service.findById(1);
        assertEquals("Alice", user.getName());
    }
}
```

### Python
```python
@pytest.fixture
def repo():
    return AsyncMock()

@pytest.fixture
def service(repo):
    return UserService(repo)

def test_find_by_id(service, repo):
    repo.find_by_id.return_value = User("Alice")
    user = service.find_by_id(1)
    assert user.name == "Alice"
```

---

## 2. Mock (테스트 더블)

| 개념 | Java (Mockito) | Python (unittest.mock) |
|---|---|---|
| Mock 생성 | `@Mock` / `mock(Foo.class)` | `Mock()` / `AsyncMock()` |
| Stub 반환값 | `when(m.method()).thenReturn(x)` | `m.method.return_value = x` |
| Stub 예외 | `when(m.method()).thenThrow(ex)` | `m.method.side_effect = ex` |
| 호출 검증 | `verify(m).method()` | `m.method.assert_called_once()` |
| await 호출 검증 | (Mockito 구분 없음) | `m.method.assert_awaited_once()` |
| 호출 없음 검증 | `verify(m, never()).method()` | `m.method.assert_not_called()` |
| 인자 검증 | `verify(m).method(eq("x"))` | `m.method.assert_called_with("x")` |
| 정적 메서드 Mock | `Mockito.mockStatic(Cls.class)` | `with patch("pkg.mod.Cls.method"):` |

### Java
```java
try (var mocked = mockStatic(OracleDB.class)) {
    mocked.when(() -> OracleDB.createPoolAsync(any()))
           .thenReturn(mockPool);
    db.start();
    verify(mocked).createPoolAsync(any());
}
```

### Python
```python
with patch("database.oracle.oracledb.create_pool_async") as mock_create:
    mock_create.return_value = AsyncMock()
    await db.start()
    mock_create.assert_called_once()
```

### AsyncMock vs Mock

oracledb Thin 모드 기준으로 어떤 메서드가 awaitable 한지 구분해야 한다.

| 메서드 | await 필요 | Mock 타입 |
|---|---|---|
| `pool.acquire()` | O | `AsyncMock` |
| `pool.close()` | O | `AsyncMock` |
| `pool.release()` | O | `AsyncMock` |
| `cursor.execute()` | O | `AsyncMock` |
| `cursor.fetchall()` | O | `AsyncMock` |
| `conn.commit()` | O | `AsyncMock` |
| `conn.rollback()` | O | `AsyncMock` |
| `oracledb.create_pool_async()` | X | `Mock` |
| `conn.cursor()` | X | `Mock` |

검증 메서드도 await 여부에 따라 다르다:
- await 호출 → `assert_awaited_once()`, `assert_awaited_with()`
- 일반 호출 → `assert_called_once()`, `assert_called_with()`

---

## 3. 리소스 관리 (try-with-resources vs context manager)

### Java
```java
try (var conn = pool.acquire()) {
    // use conn
}  // AutoCloseable.close() 자동 호출
```

### Python
```python
async with db.connection() as conn:
    # use conn
# __aexit__() 에서 pool.release(conn) 자동 호출
```

---

## 4. 예외 검증

### Java
```java
assertThrows(RuntimeException.class, () -> {
    db.connection();
});

assertThrows(ValueError.class, () -> {
    throw new ValueError("test error");
});
```

### Python
```python
with pytest.raises(RuntimeError, match="start"):
    async with db.connection():
        pass

with pytest.raises(ValueError, match="test error"):
    raise ValueError("test error")
```

---

## 5. 조건부 테스트

### Java
```java
@DisabledIf("!dbConfigured")
@Test void testNeedsDb() { }

@BeforeAll
static void check() {
    Assumptions.assumeTrue(password != null);
}
```

### Python
```python
# 모듈 전체에 적용
pytestmark = pytest.mark.skipif(
    not configured, reason="DB 설정 없음"
)

# 개별 테스트에 적용
@pytest.mark.skipif(not configured, reason="...")
def test_needs_db(): ...

# 테스트 도중 동적 스킵
def test_something():
    if not connected:
        pytest.skip("DB 연결 실패")
```

---

## 6. 비동기

### Java
```java
@Test
void testAsync() throws Exception {
    CompletableFuture<String> f = service.fetchAsync();
    String result = f.get(5, TimeUnit.SECONDS);
    assertEquals("ok", result);
}
```

### Python
```python
@pytest.mark.asyncio  # pytest-asyncio 가 이벤트루프 관리
async def test_async():
    result = await service.fetch()
    assert result == "ok"
```

pytest-asyncio strict 모드 주의사항:
- async fixture 는 `@pytest_asyncio.fixture` 사용 (일반 `@pytest.fixture` X)
- 모든 테스트는 `@pytest.mark.asyncio` 필요

---

## 7. 병렬 실행

### Java
```java
var futures = IntStream.range(0, 5)
    .mapToObj(i -> CompletableFuture.supplyAsync(() -> query()))
    .toList();
CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
List<String> results = futures.stream().map(CompletableFuture::join).toList();
```

### Python
```python
async def query():
    return await db.fetch_all("SELECT 1 FROM dual")

tasks = [query() for _ in range(5)]
results = await asyncio.gather(*tasks)
```

---

## 8. DB ResultSet → Map/DTO

### Java (Spring JdbcTemplate)
```java
List<Map<String, Object>> rows = jdbcTemplate.queryForList(
    "SELECT id, name FROM users"
);
```

### Java (MyBatis)
```java
@Select("SELECT id, name FROM users")
List<User> findAll();
```

### Python (oracledb 직접 사용)
```python
cursor = conn.cursor()
await cursor.execute("SELECT id, name FROM users")
rows = await cursor.fetchall()
# cursor.description = [("ID",), ("NAME",)]
columns = [col[0].lower() for col in cursor.description]
result = [dict(zip(columns, row)) for row in rows]
# → [{"id": 1, "name": "Alice"}, ...]
```

---

## 9. 설정 파일 읽기

| 개념 | Java | Python |
|---|---|---|
| 설정 파일 | `application.yml` / `.properties` | `.env` |
| 설정 클래스 | `@ConfigurationProperties` POJO | `dataclass` |
| 값 주입 | `@Value("${oracle.host}")` | `settings.oracle_host` |
| 라이브러리 | Spring Boot | `python-dotenv` |

### Java
```java
@ConfigurationProperties(prefix = "oracle")
public record OracleConfig(
    String host,
    int port,
    String serviceName
) {}
```

### Python
```python
from dataclasses import dataclass

@dataclass
class OracleConfig:
    host: str = "localhost"
    port: int = 1521
    service_name: str = "XEPDB1"
```

---

## 10. 커넥션 풀

### Java (HikariCP)
```java
HikariConfig config = new HikariConfig();
config.setJdbcUrl("jdbc:oracle:thin:@localhost:1521/XEPDB1");
config.setUsername("app_user");
config.setPassword("secret");
config.setMinimumIdle(1);
config.setMaximumPoolSize(5);
HikariDataSource ds = new HikariDataSource(config);
```

### Python (oracledb)
```python
pool = oracledb.create_pool_async(
    user="app_user",
    password="secret",
    dsn="localhost:1521/XEPDB1",
    min=1,
    max=5,
    increment=1,
)
```

---

## 11. Fixture scope 비교

| pytest scope | Java (JUnit5) |
|---|---|
| `function` (기본) | `@BeforeEach` / `@AfterEach` |
| `class` | `@BeforeEach` / `@AfterEach` (단일 클래스 내) |
| `module` | `@BeforeAll` (JUnit5 에는 없음, TestNG 의 `@BeforeTest`) |
| `session` | `@BeforeAll` / `@AfterAll` |

```python
# function scope: 테스트 함수마다 새 인스턴스
@pytest.fixture
def db():
    db = create_db()
    yield db
    db.close()

# session scope: 한 번만 생성하고 모든 테스트가 공유
@pytest.fixture(scope="session")
def db():
    db = create_db()
    yield db
    db.close()
```

---

## 12. 간단한 테스트 예제 10선

### 12-1. 기본 assert

```python
def test_basic():
    assert 1 + 1 == 2
    assert "hello".upper() == "HELLO"

def test_list_dict():
    assert len([1, 2, 3]) == 3
    assert {"a": 1}["a"] == 1
    assert "hello" in ["hello", "world"]
```

### 12-2. 예외 검증

```python
import pytest

def test_zero_division():
    with pytest.raises(ZeroDivisionError):
        1 / 0

def test_value_error_message():
    with pytest.raises(ValueError, match="invalid.*id"):
        raise ValueError("invalid user id: -1")
```

### 12-3. parametrize (여러 입력값 반복)

```python
# Java: @ParameterizedTest + @CsvSource 와 동일
@pytest.mark.parametrize("a,b,expected", [
    (1, 2, 3),
    (0, 0, 0),
    (-1, 1, 0),
    (100, 200, 300),
])
def test_add(a, b, expected):
    assert a + b == expected

# 여러 파라미터 조합
@pytest.mark.parametrize("op", ["+", "-", "*"])
@pytest.mark.parametrize("a,b", [(1, 2), (3, 4)])
def test_operations(a, b, op):
    result = eval(f"{a} {op} {b}")
    assert isinstance(result, int)
```

### 12-4. Mock return_value / side_effect

```python
from unittest.mock import Mock

def test_mock_return():
    service = Mock()
    service.find_user.return_value = {"name": "Alice"}

    assert service.find_user(1) == {"name": "Alice"}
    service.find_user.assert_called_once_with(1)

def test_mock_side_effect():
    service = Mock()
    # 매 호출마다 다른 값 반환
    service.get_count.side_effect = [1, 2, 3]

    assert service.get_count() == 1
    assert service.get_count() == 2
    assert service.get_count() == 3

def test_mock_side_effect_exception():
    service = Mock()
    service.connect.side_effect = ConnectionError("timeout")

    with pytest.raises(ConnectionError, match="timeout"):
        service.connect()
```

### 12-5. fixture 기본 패턴

```python
import pytest

# scope="function" (기본값): 테스트마다 새로 생성
@pytest.fixture
def user_data():
    return {"id": 1, "name": "dwsamba"}

def test_user_fixture(user_data):
    assert user_data["id"] == 1

# fixture 가 fixture 에 의존
@pytest.fixture
def enriched(user_data):
    user_data["role"] = "admin"
    return user_data

def test_enriched(enriched):
    assert enriched["role"] == "admin"
```

### 12-6. tmp_path (임시 파일)

```python
def test_write_file(tmp_path):
    # Java: @TempDir Path tmpDir; 와 동일
    file = tmp_path / "test.txt"
    file.write_text("hello")

    assert file.exists()
    assert file.read_text() == "hello"

def test_temp_dir_structure(tmp_path):
    sub = tmp_path / "sub" / "deep"
    sub.mkdir(parents=True)
    (sub / "a.txt").write_text("a")
    (sub / "b.txt").write_text("b")

    assert len(list(sub.iterdir())) == 2
```

### 12-7. caplog (로그 캡처)

```python
import logging

def test_log_output(caplog):
    # Java: @Rule LoggerRule 또는 logback-test.xml 의 in-memory appender
    logger = logging.getLogger("myapp")
    logger.setLevel(logging.INFO)

    logger.info("user login: %s", "dwsamba")
    logger.warning("retry attempt %d", 3)

    assert "user login: dwsamba" in caplog.text
    assert "retry attempt 3" in caplog.text
    assert caplog.records[0].levelname == "INFO"
    assert caplog.records[1].levelname == "WARNING"
```

### 12-8. conftest.py (공유 fixture)

```python
# conftest.py — 같은 디렉토리(또는 상위)의 모든 테스트에 자동 적용
# Java: @SpringBootTest + @TestConfiguration 과 유사한 중앙 설정

import pytest

@pytest.fixture(scope="session")
def app():
    """전체 테스트 세션에서 한 번만 생성"""
    app = create_app()
    app.start()
    yield app
    app.stop()

@pytest.fixture
def client(app):
    """세션 app 을 공유받아 테스트마다 새 클라이언트 생성"""
    return app.test_client()
```

### 12-9. 비동기 Mock 검증

```python
import pytest
from unittest.mock import AsyncMock

@pytest.mark.asyncio
async def test_async_mock():
    repo = AsyncMock()
    repo.find_all.return_value = [{"id": 1}, {"id": 2}]

    rows = await repo.find_all()
    assert len(rows) == 2
    # await 호출 검증은 assert_awaited_*
    repo.find_all.assert_awaited_once()

@pytest.mark.asyncio
async def test_async_error():
    repo = AsyncMock()
    repo.find_all.side_effect = TimeoutError("db timeout")

    with pytest.raises(TimeoutError, match="db timeout"):
        await repo.find_all()
```

### 12-10. patch 으로 외부 의존성 치환

```python
from unittest.mock import patch, MagicMock

# 외부 API 호출을 Mock 으로 대체
@patch("providers.openai.httpx.AsyncClient.post")
@pytest.mark.asyncio
async def test_openai_call(mock_post):
    # 응답 구조를 Mock 으로 구성
    mock_response = MagicMock()
    mock_response.status_code = 200
    mock_response.json.return_value = {
        "choices": [{"message": {"content": "Hello"}}]
    }
    mock_post.return_value = mock_response

    result = await call_llm([{"role": "user", "content": "hi"}])
    assert result == "Hello"
    mock_post.assert_called_once()

# 컨텍스트 매니저 방식
def test_with_context_manager():
    with patch("database.oracle.oracledb.create_pool_async") as mock_create:
        mock_create.return_value = AsyncMock()
        # 이 블록 안에서만 patch 적용
        db = OracleDB.from_settings()
        await db.start()
        mock_create.assert_called_once()
```
