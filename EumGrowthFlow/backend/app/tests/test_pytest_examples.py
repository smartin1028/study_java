"""
pytest 기초 예제 테스트 (10가지 패턴)

실제로 실행 가능한 테스트 모음.
Java(JUnit5 + Mockito)와 비교하며 익히는 Python 테스트.

실행:
    pytest tests/test_pytest_examples.py -v
"""

import logging
from unittest.mock import AsyncMock, MagicMock, Mock, patch

import pytest


# ============================================================
# 1. 기본 assert
# ============================================================

def test_basic_arithmetic():
    assert 1 + 1 == 2


def test_string_operations():
    assert "hello".upper() == "HELLO"
    assert "hello,world,python".split(",") == ["hello", "world", "python"]


def test_list_and_dict():
    assert len([1, 2, 3]) == 3
    assert {"a": 1}["a"] == 1
    assert "hello" in ["hello", "world"]
    assert 5 not in [1, 2, 3]


# ============================================================
# 2. 예외 검증
# ============================================================

def test_zero_division_raises():
    with pytest.raises(ZeroDivisionError):
        _ = 1 / 0


def test_value_error_with_message():
    with pytest.raises(ValueError, match="invalid.*id"):
        raise ValueError("invalid user id: -1")


# ============================================================
# 3. parametrize (여러 입력값 반복)
# ============================================================

@pytest.mark.parametrize("a,b,expected", [
    (1, 2, 3),
    (0, 0, 0),
    (-1, 1, 0),
    (100, 200, 300),
])
def test_add(a: int, b: int, expected: int):
    assert a + b == expected


@pytest.mark.parametrize("text,expected", [
    ("hello", 5),
    ("", 0),
    ("안녕하세요", 5),
    ("a b c", 5),
])
def test_string_length(text: str, expected: int):
    assert len(text) == expected


# ============================================================
# 4. Mock return_value / side_effect
# ============================================================

def test_mock_return_value():
    service = Mock()
    service.find_user.return_value = {"name": "Alice"}

    result = service.find_user(1)
    assert result == {"name": "Alice"}
    service.find_user.assert_called_once_with(1)


def test_mock_side_effect_sequence():
    service = Mock()
    service.get_count.side_effect = [1, 2, 3]

    assert service.get_count() == 1
    assert service.get_count() == 2
    assert service.get_count() == 3


def test_mock_side_effect_exception():
    service = Mock()
    service.connect.side_effect = ConnectionError("timeout")

    with pytest.raises(ConnectionError, match="timeout"):
        service.connect()


# ============================================================
# 5. fixture 기본 패턴
# ============================================================

@pytest.fixture
def user_data() -> dict:
    return {"id": 1, "name": "dwsamba"}


def test_user_fixture(user_data: dict):
    assert user_data["id"] == 1
    assert user_data["name"] == "dwsamba"


@pytest.fixture
def enriched(user_data: dict) -> dict:
    user_data["role"] = "admin"
    return user_data


def test_enriched_fixture(enriched: dict):
    assert enriched["role"] == "admin"
    assert enriched["name"] == "dwsamba"


# ============================================================
# 6. tmp_path (임시 파일)
# ============================================================

def test_write_temp_file(tmp_path):
    file = tmp_path / "test.txt"
    file.write_text("hello, world")

    assert file.exists()
    assert file.read_text() == "hello, world"


def test_temp_directory_structure(tmp_path):
    sub = tmp_path / "sub" / "deep"
    sub.mkdir(parents=True)
    (sub / "a.txt").write_text("aaa")
    (sub / "b.txt").write_text("bbb")

    files = sorted(p.name for p in sub.iterdir())
    assert files == ["a.txt", "b.txt"]


# ============================================================
# 7. caplog (로그 캡처)
# ============================================================

def test_log_output(caplog):
    logger = logging.getLogger("myapp")
    logger.setLevel(logging.INFO)

    logger.info("user login: %s", "dwsamba")
    logger.warning("retry attempt %d", 3)

    assert "user login: dwsamba" in caplog.text
    assert "retry attempt 3" in caplog.text
    assert caplog.records[0].levelname == "INFO"
    assert caplog.records[1].levelname == "WARNING"


# ============================================================
# 8. conftest 에서 제공하는 fixture 사용
# ============================================================

def test_conftest_fixture(shared_config: dict):
    assert shared_config["app_name"] == "EumGrowthFlow"
    assert shared_config["version"] == "0.1.0"


# ============================================================
# 9. 비동기 Mock 검증
# ============================================================

@pytest.mark.asyncio
async def test_async_mock():
    repo = AsyncMock()
    repo.find_all.return_value = [{"id": 1}, {"id": 2}]

    rows = await repo.find_all()
    assert len(rows) == 2
    repo.find_all.assert_awaited_once()


@pytest.mark.asyncio
async def test_async_mock_error():
    repo = AsyncMock()
    repo.find_all.side_effect = TimeoutError("db timeout")

    with pytest.raises(TimeoutError, match="db timeout"):
        await repo.find_all()


# ============================================================
# 10. patch 으로 외부 의존성 치환
# ============================================================

@patch("httpx.AsyncClient.post")
@pytest.mark.asyncio
async def test_patch_httpx(mock_post):
    mock_response = MagicMock()
    mock_response.status_code = 200
    mock_response.json.return_value = {
        "choices": [{"message": {"content": "Hello"}}]
    }
    mock_post.return_value = mock_response

    import httpx
    async with httpx.AsyncClient() as client:
        resp = await client.post("https://api.example.com/v1/chat",
                                 json={"messages": [{"role": "user", "content": "hi"}]})
        assert resp.status_code == 200
        assert resp.json()["choices"][0]["message"]["content"] == "Hello"

    mock_post.assert_called_once()


def test_patch_context_manager():
    mock_obj = Mock()
    mock_obj.process.return_value = "OK"

    with patch("unittest.mock.Mock", return_value=mock_obj) as mock_cls:
        instance = mock_cls()
        result = instance.process()

        assert result == "OK"
        mock_cls.assert_called_once()
