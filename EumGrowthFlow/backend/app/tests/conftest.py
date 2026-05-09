import os

os.environ.setdefault("LLM_BEARER_TOKEN", "test_token")
os.environ.setdefault("LLM_PROVIDER", "ollama")

import pytest
from unittest.mock import AsyncMock
from fastapi.testclient import TestClient
from models import LLMResponse
from providers import AbstractLLMProvider, get_provider
from main import app


@pytest.fixture
def mock_provider():
    provider = AsyncMock(spec=AbstractLLMProvider)
    provider.generate.return_value = LLMResponse(
        response="Mocked response", model="test-model"
    )
    return provider


@pytest.fixture
def client(mock_provider):
    app.dependency_overrides[get_provider] = lambda: mock_provider
    yield TestClient(app)
    app.dependency_overrides.clear()
