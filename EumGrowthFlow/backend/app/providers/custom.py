"""
Custom OpenAI-compatible Provider 구현체

OpenAI Chat Completions API 와 호환되는 임의의 엔드포인트를 사용한다.
vLLM, OpenRouter, Together AI, Groq 등 /v1/chat/completions 엔드포인트를
제공하는 모든 서비스에 대해 base_url 만 설정하면 사용 가능하다.

지원 모델:
  - 제한 없음 (CUSTOM_MODEL 로 지정)

API 인증 (선택):
  Authorization: Bearer <CUSTOM_API_KEY>
"""

import httpx
from providers.base import AbstractLLMProvider
from config import settings
from models import LLMResponse


class CustomProvider(AbstractLLMProvider):
    """
    OpenAI 호환 API 와 통신하는 Provider 구현체

    CUSTOM_BASE_URL, CUSTOM_API_KEY, CUSTOM_MODEL 환경변수로
    접속 정보를 설정한다.
    """

    def __init__(
        self,
        base_url: str = settings.custom_base_url,
        api_key: str = settings.custom_api_key,
        default_model: str = settings.custom_model,
        timeout: float = 120.0,
    ):
        self.base_url = base_url.rstrip("/")
        self.api_key = api_key
        self.default_model = default_model
        self.timeout = timeout
        self._client: httpx.AsyncClient | None = None

    @property
    def client(self) -> httpx.AsyncClient:
        if self._client is None:
            self._client = httpx.AsyncClient(timeout=self.timeout)
        return self._client

    async def generate(
        self, messages: list[dict], model: str | None = None, **kwargs
    ) -> LLMResponse:
        model_name = model or self.default_model

        payload = {
            "model": model_name,
            "messages": messages,
        }
        payload.update(kwargs)

        headers = {"Content-Type": "application/json"}
        if self.api_key:
            headers["Authorization"] = f"Bearer {self.api_key}"

        response = await self.client.post(
            f"{self.base_url}/v1/chat/completions",
            json=payload,
            headers=headers,
        )
        response.raise_for_status()
        data = response.json()

        content = data["choices"][0]["message"]["content"]
        used_model = data.get("model", model_name)

        return LLMResponse(response=content, model=used_model)

    async def close(self):
        if self._client is not None:
            await self._client.aclose()
            self._client = None
