"""
DeepSeek API Provider 구현체

DeepSeek API 는 OpenAI Chat Completions API 와 호환되는 인터페이스를 제공한다.
https://api.deepseek.com/v1/chat/completions 엔드포인트를 통해
대화형 LLM 추론을 수행한다.

지원 모델:
  - deepseek-v4-flash: 빠른 응답, 비용 효율적 (기본값)
  - deepseek-v4-pro: 고품질 추론, 복잡한 작업용
  - deepseek-chat: 구버전 (2026/07/24 폐기 예정)
  - deepseek-reasoner: 구버전 (2026/07/24 폐기 예정)

API 인증:
  Authorization: Bearer <DEEPSEEK_API_KEY>
"""

import httpx
from providers.base import AbstractLLMProvider
from config import settings
from models import LLMResponse


class DeepSeekProvider(AbstractLLMProvider):
    """
    DeepSeek API 와 통신하는 Provider 구현체

    OpenAI 호환 Chat Completions API 를 사용하며,
    프롬프트를 messages[].content 형식으로 변환하여 전송한다.
    """

    # DeepSeek API 기본 URL
    BASE_URL = "https://api.deepseek.com"

    # 지원 모델 목록
    AVAILABLE_MODELS = {
        "deepseek-v4-flash",     # 빠른 응답 (기본값)
        "deepseek-v4-pro",       # 고품질 추론
        "deepseek-chat",         # 구버전 (2026/07/24 폐기 예정)
        "deepseek-reasoner",     # 구버전 (2026/07/24 폐기 예정)
    }

    def __init__(
        self,
        api_key: str = settings.deepseek_api_key,
        default_model: str = settings.deepseek_model,
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

        # OpenAI 호환 Chat Completions 요청 페이로드
        payload = {
            "model": model_name,
            "messages": messages,
        }
        # temperature, max_tokens 등 추가 파라미터 병합
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

        # Chat Completions 응답에서 텍스트 추출
        # choices[0].message.content 구조 (OpenAI 호환)
        content = data["choices"][0]["message"]["content"]
        used_model = data.get("model", model_name)

        return LLMResponse(response=content, model=used_model)

    async def close(self):
        if self._client is not None:
            await self._client.aclose()
            self._client = None
