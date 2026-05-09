"""
Provider 단위 테스트

OllamaProvider 와 DeepSeekProvider 의 generate() 메서드를
httpx.AsyncClient 를 목킹하여 HTTP 호출 없이 검증한다.
"""

import pytest
from unittest.mock import AsyncMock, Mock
from providers.ollama import OllamaProvider
from providers.deepseek import DeepSeekProvider
from providers.openai import OpenAIProvider


# ============================================================
# OllamaProvider 테스트
# ============================================================

@pytest.fixture
def ollama_provider():
    """OllamaProvider 테스트용 인스턴스"""
    return OllamaProvider(
        base_url="http://localhost:11434",
        default_model="deepseek-r1:1.5b",
    )


def _mock_ollama_response(model: str, response_text: str) -> Mock:
    """Ollama API 응답을 흉내내는 Mock 객체 생성"""
    mock = Mock()
    mock.json.return_value = {
        "model": model,
        "response": response_text,
        "done": True,
    }
    mock.raise_for_status.return_value = None
    return mock


@pytest.mark.asyncio
async def test_ollama_generate_success(ollama_provider):
    """정상적인 Ollama API 호출 시 응답이 올바르게 반환되는지 검증"""
    ollama_provider._client = AsyncMock()
    ollama_provider._client.post.return_value = _mock_ollama_response(
        "deepseek-r1:1.5b", "Hello, world!"
    )

    result = await ollama_provider.generate([{"role": "user", "content": "Hello"}])
    assert result.response == "Hello, world!"
    assert result.model == "deepseek-r1:1.5b"


@pytest.mark.asyncio
async def test_ollama_generate_model_override(ollama_provider):
    """model 파라미터 전달 시 해당 모델이 API 요청에 포함되는지 검증"""
    ollama_provider._client = AsyncMock()
    ollama_provider._client.post.return_value = _mock_ollama_response(
        "custom-model", "ok"
    )

    result = await ollama_provider.generate([{"role": "user", "content": "Hi"}], model="custom-model")
    assert result.model == "custom-model"
    call_kwargs = ollama_provider._client.post.call_args.kwargs
    assert call_kwargs["json"]["model"] == "custom-model"


@pytest.mark.asyncio
async def test_ollama_generate_default_model(ollama_provider):
    """model 미지정 시 기본 모델이 API 요청에 사용되는지 검증"""
    ollama_provider._client = AsyncMock()
    ollama_provider._client.post.return_value = _mock_ollama_response(
        "deepseek-r1:1.5b", "default model response"
    )

    result = await ollama_provider.generate([{"role": "user", "content": "Hello"}])
    assert result.model == "deepseek-r1:1.5b"
    call_kwargs = ollama_provider._client.post.call_args.kwargs
    assert call_kwargs["json"]["model"] == "deepseek-r1:1.5b"


@pytest.mark.asyncio
async def test_ollama_generate_connection_error(ollama_provider):
    """Ollama 서버 연결 실패 시 ConnectionError 가 전파되는지 검증"""
    ollama_provider._client = AsyncMock()
    ollama_provider._client.post.side_effect = ConnectionError("Connection refused")

    with pytest.raises(ConnectionError, match="Connection refused"):
        await ollama_provider.generate([{"role": "user", "content": "Hello"}])


# ============================================================
# DeepSeekProvider 테스트
# ============================================================

@pytest.fixture
def deepseek_provider():
    """DeepSeekProvider 테스트용 인스턴스"""
    return DeepSeekProvider(
        api_key="sk-test-key",
        default_model="deepseek-v4-flash",
    )


def _mock_deepseek_response(model: str, content: str) -> Mock:
    """DeepSeek API (OpenAI 호환) 응답을 흉내내는 Mock 객체 생성"""
    mock = Mock()
    mock.json.return_value = {
        "id": "chatcmpl-test",
        "model": model,
        "choices": [
            {
                "index": 0,
                "message": {
                    "role": "assistant",
                    "content": content,
                },
                "finish_reason": "stop",
            }
        ],
    }
    mock.raise_for_status.return_value = None
    return mock


@pytest.mark.asyncio
async def test_deepseek_generate_success(deepseek_provider):
    """정상적인 DeepSeek API 호출 시 응답이 올바르게 파싱되는지 검증"""
    deepseek_provider._client = AsyncMock()
    deepseek_provider._client.post.return_value = _mock_deepseek_response(
        "deepseek-v4-flash", "안녕하세요! 무엇을 도와드릴까요?"
    )

    result = await deepseek_provider.generate([{"role": "user", "content": "안녕하세요"}])
    assert result.response == "안녕하세요! 무엇을 도와드릴까요?"
    assert result.model == "deepseek-v4-flash"


@pytest.mark.asyncio
async def test_deepseek_generate_model_override(deepseek_provider):
    """model 파라미터로 deepseek-v4-pro 지정 시 API 요청에 반영되는지 검증"""
    deepseek_provider._client = AsyncMock()
    deepseek_provider._client.post.return_value = _mock_deepseek_response(
        "deepseek-v4-pro", "복잡한 분석 결과..."
    )

    result = await deepseek_provider.generate([{"role": "user", "content": "분석해줘"}], model="deepseek-v4-pro")
    assert result.model == "deepseek-v4-pro"
    call_kwargs = deepseek_provider._client.post.call_args.kwargs
    assert call_kwargs["json"]["model"] == "deepseek-v4-pro"


@pytest.mark.asyncio
async def test_deepseek_generate_default_model(deepseek_provider):
    """model 미지정 시 기본 모델(deepseek-v4-flash)이 사용되는지 검증"""
    deepseek_provider._client = AsyncMock()
    deepseek_provider._client.post.return_value = _mock_deepseek_response(
        "deepseek-v4-flash", "기본 모델 응답"
    )

    result = await deepseek_provider.generate([{"role": "user", "content": "테스트"}])
    assert result.model == "deepseek-v4-flash"
    call_kwargs = deepseek_provider._client.post.call_args.kwargs
    assert call_kwargs["json"]["model"] == "deepseek-v4-flash"


@pytest.mark.asyncio
async def test_deepseek_messages_format(deepseek_provider):
    """messages 배열이 API 로 그대로 전달되는지 검증"""
    deepseek_provider._client = AsyncMock()
    deepseek_provider._client.post.return_value = _mock_deepseek_response(
        "deepseek-v4-flash", "응답"
    )

    test_messages = [
        {"role": "user", "content": "이전 질문"},
        {"role": "assistant", "content": "이전 답변"},
        {"role": "user", "content": "새 질문"},
    ]
    await deepseek_provider.generate(test_messages)
    call_kwargs = deepseek_provider._client.post.call_args.kwargs
    messages = call_kwargs["json"]["messages"]
    assert messages == test_messages


@pytest.mark.asyncio
async def test_deepseek_auth_header(deepseek_provider):
    """API 키가 Authorization 헤더에 Bearer 형식으로 포함되는지 검증"""
    deepseek_provider._client = AsyncMock()
    deepseek_provider._client.post.return_value = _mock_deepseek_response(
        "deepseek-v4-flash", "응답"
    )

    await deepseek_provider.generate([{"role": "user", "content": "테스트"}])
    call_kwargs = deepseek_provider._client.post.call_args.kwargs
    assert call_kwargs["headers"]["Authorization"] == "Bearer sk-test-key"


@pytest.mark.asyncio
async def test_deepseek_generate_connection_error(deepseek_provider):
    """DeepSeek API 연결 실패 시 ConnectionError 가 전파되는지 검증"""
    deepseek_provider._client = AsyncMock()
    deepseek_provider._client.post.side_effect = ConnectionError("Connection refused")

    with pytest.raises(ConnectionError, match="Connection refused"):
        await deepseek_provider.generate([{"role": "user", "content": "Hello"}])


# ============================================================
# OpenAIProvider 테스트
# ============================================================

@pytest.fixture
def openai_provider():
    """OpenAIProvider 테스트용 인스턴스"""
    return OpenAIProvider(
        api_key="sk-test-openai-key",
        default_model="gpt-4.1-mini",
    )


def _mock_openai_response(model: str, content: str) -> Mock:
    """OpenAI API 응답을 흉내내는 Mock 객체 생성"""
    mock = Mock()
    mock.json.return_value = {
        "id": "chatcmpl-test-openai",
        "model": model,
        "choices": [
            {
                "index": 0,
                "message": {
                    "role": "assistant",
                    "content": content,
                },
                "finish_reason": "stop",
            }
        ],
    }
    mock.raise_for_status.return_value = None
    return mock


@pytest.mark.asyncio
async def test_openai_generate_success(openai_provider):
    """정상적인 OpenAI API 호출 시 응답이 올바르게 파싱되는지 검증"""
    openai_provider._client = AsyncMock()
    openai_provider._client.post.return_value = _mock_openai_response(
        "gpt-4.1-mini", "Hello! How can I help you today?"
    )

    result = await openai_provider.generate([{"role": "user", "content": "Hello"}])
    assert result.response == "Hello! How can I help you today?"
    assert result.model == "gpt-4.1-mini"


@pytest.mark.asyncio
async def test_openai_generate_model_override(openai_provider):
    """model 파라미터로 gpt-4.1 지정 시 API 요청에 반영되는지 검증"""
    openai_provider._client = AsyncMock()
    openai_provider._client.post.return_value = _mock_openai_response(
        "gpt-4.1", "상세한 분석 결과..."
    )

    result = await openai_provider.generate([{"role": "user", "content": "분석해줘"}], model="gpt-4.1")
    assert result.model == "gpt-4.1"
    call_kwargs = openai_provider._client.post.call_args.kwargs
    assert call_kwargs["json"]["model"] == "gpt-4.1"


@pytest.mark.asyncio
async def test_openai_generate_default_model(openai_provider):
    """model 미지정 시 기본 모델(gpt-4.1-mini)이 사용되는지 검증"""
    openai_provider._client = AsyncMock()
    openai_provider._client.post.return_value = _mock_openai_response(
        "gpt-4.1-mini", "기본 모델 응답"
    )

    result = await openai_provider.generate([{"role": "user", "content": "테스트"}])
    assert result.model == "gpt-4.1-mini"
    call_kwargs = openai_provider._client.post.call_args.kwargs
    assert call_kwargs["json"]["model"] == "gpt-4.1-mini"


@pytest.mark.asyncio
async def test_openai_messages_format(openai_provider):
    """messages 배열이 API 로 그대로 전달되는지 검증"""
    openai_provider._client = AsyncMock()
    openai_provider._client.post.return_value = _mock_openai_response(
        "gpt-4.1-mini", "응답"
    )

    test_messages = [
        {"role": "user", "content": "이전 질문"},
        {"role": "assistant", "content": "이전 답변"},
        {"role": "user", "content": "새 질문"},
    ]
    await openai_provider.generate(test_messages)
    call_kwargs = openai_provider._client.post.call_args.kwargs
    messages = call_kwargs["json"]["messages"]
    assert messages == test_messages


@pytest.mark.asyncio
async def test_openai_auth_header(openai_provider):
    """API 키가 Authorization 헤더에 Bearer 형식으로 포함되는지 검증"""
    openai_provider._client = AsyncMock()
    openai_provider._client.post.return_value = _mock_openai_response(
        "gpt-4.1-mini", "응답"
    )

    await openai_provider.generate([{"role": "user", "content": "테스트"}])
    call_kwargs = openai_provider._client.post.call_args.kwargs
    assert call_kwargs["headers"]["Authorization"] == "Bearer sk-test-openai-key"


@pytest.mark.asyncio
async def test_openai_generate_connection_error(openai_provider):
    """OpenAI API 연결 실패 시 ConnectionError 가 전파되는지 검증"""
    openai_provider._client = AsyncMock()
    openai_provider._client.post.side_effect = ConnectionError("Connection refused")

    with pytest.raises(ConnectionError, match="Connection refused"):
        await openai_provider.generate([{"role": "user", "content": "Hello"}])
