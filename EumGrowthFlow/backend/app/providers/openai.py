"""
OpenAI API Provider 구현체

OpenAI Chat Completions API 를 사용하여 대화형 LLM 추론을 수행한다.
https://api.openai.com/v1/chat/completions 엔드포인트 사용.

지원 모델:
  - gpt-4.1: 최신 고품질 모델 (기본값)
  - gpt-4.1-mini: 빠른 응답, 비용 효율적
  - gpt-4.1-nano: 가장 빠르고 저렴한 모델
  - gpt-4o: 멀티모달 지원 범용 모델
  - gpt-4o-mini: 경량 멀티모달 모델
  - o4-mini: 추론 특화 경량 모델

API 인증:
  Authorization: Bearer <OPENAI_API_KEY>
"""

import httpx
from providers.base import AbstractLLMProvider
from config import settings
from models import LLMResponse


class OpenAIProvider(AbstractLLMProvider):
    """
    OpenAI API 와 통신하는 Provider 구현체

    Chat Completions API 를 사용하며,
    프롬프트를 messages[].content 형식으로 변환하여 전송한다.
    """

    BASE_URL = "https://api.openai.com"

    AVAILABLE_MODELS = {
        "gpt-4.1",         # 최신 고품질 모델 (기본값)
        "gpt-4.1-mini",    # 빠른 응답, 비용 효율적
        "gpt-4.1-nano",    # 가장 빠르고 저렴한 모델
        "gpt-4o",          # 멀티모달 지원 범용 모델
        "gpt-4o-mini",     # 경량 멀티모달 모델
        "o4-mini",         # 추론 특화 경량 모델
    }

    def __init__(
        self,
        api_key: str = settings.openai_api_key,
        default_model: str = settings.openai_model,
        timeout: float = 120.0,
    ):
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

        headers = {
            "Authorization": f"Bearer {self.api_key}",
            "Content-Type": "application/json",
        }

        response = await self.client.post(
            f"{self.BASE_URL}/v1/chat/completions",
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
