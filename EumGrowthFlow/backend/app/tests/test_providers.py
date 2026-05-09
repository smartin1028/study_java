import pytest
from unittest.mock import AsyncMock, Mock
from providers.ollama import OllamaProvider


@pytest.fixture
def ollama_provider():
    return OllamaProvider(
        base_url="http://localhost:11434",
        default_model="deepseek-r1:1.5b",
    )


def _mock_response(model: str, response_text: str) -> Mock:
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
    ollama_provider._client = AsyncMock()
    ollama_provider._client.post.return_value = _mock_response(
        "deepseek-r1:1.5b", "Hello, world!"
    )

    result = await ollama_provider.generate("Hello")
    assert result.response == "Hello, world!"
    assert result.model == "deepseek-r1:1.5b"


@pytest.mark.asyncio
async def test_ollama_generate_model_override(ollama_provider):
    ollama_provider._client = AsyncMock()
    ollama_provider._client.post.return_value = _mock_response(
        "custom-model", "ok"
    )

    result = await ollama_provider.generate("Hi", model="custom-model")
    assert result.model == "custom-model"
    call_kwargs = ollama_provider._client.post.call_args.kwargs
    assert call_kwargs["json"]["model"] == "custom-model"


@pytest.mark.asyncio
async def test_ollama_generate_default_model(ollama_provider):
    ollama_provider._client = AsyncMock()
    ollama_provider._client.post.return_value = _mock_response(
        "deepseek-r1:1.5b", "default model response"
    )

    result = await ollama_provider.generate("Hello")
    assert result.model == "deepseek-r1:1.5b"
    call_kwargs = ollama_provider._client.post.call_args.kwargs
    assert call_kwargs["json"]["model"] == "deepseek-r1:1.5b"


@pytest.mark.asyncio
async def test_ollama_generate_connection_error(ollama_provider):
    ollama_provider._client = AsyncMock()
    ollama_provider._client.post.side_effect = ConnectionError("Connection refused")

    with pytest.raises(ConnectionError, match="Connection refused"):
        await ollama_provider.generate("Hello")
