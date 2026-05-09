"""
Ollama LLM Provider 구현체

로컬에서 실행 중인 Ollama 서버의 /api/generate 엔드포인트를 호출하여
LLM 추론을 수행한다. httpx.AsyncClient 를 사용하여 비동기 HTTP 요청을 보내며,
FastAPI 의 이벤트 루프를 차단하지 않는다.

Ollama API 명세:
  - POST /api/generate
  - 요청 바디: {"model": "...", "prompt": "...", "stream": false}
  - 응답 바디: {"model": "...", "response": "...", "done": true}

stream: false 로 설정하여 단일 JSON 응답을 받으며,
스트리밍 응답은 현재 지원하지 않는다 (추후 확장 가능).
"""

import httpx
from providers.base import AbstractLLMProvider
from config import settings
from models import LLMResponse


class OllamaProvider(AbstractLLMProvider):
    """
    Ollama 로컬 LLM 서버와 통신하는 Provider 구현체

    httpx.AsyncClient 를 지연 초기화(lazy initialization)하여
    실제 API 호출이 발생하기 전까지 HTTP 연결을 생성하지 않는다.
    기본 타임아웃은 120초 (대규모 모델의 긴 추론 시간 고려).
    """

    def __init__(
        self,
        base_url: str = settings.ollama_base_url,
        default_model: str = settings.ollama_model,
        timeout: float = 120.0,
    ):
        """
        OllamaProvider 초기화

        Args:
            base_url: Ollama 서버 URL (기본값: http://localhost:11434)
            default_model: 모델 미지정 시 사용할 기본 모델 (기본값: deepseek-r1:1.5b)
            timeout: HTTP 요청 타임아웃(초), 120초는 대규모 모델의 긴 추론 시간을 고려
        """
        self.base_url = base_url.rstrip("/")
        self.default_model = default_model
        self.timeout = timeout
        # HTTP 클라이언트는 첫 API 호출 시점에 지연 초기화 (리소스 절약)
        self._client: httpx.AsyncClient | None = None

    @property
    def client(self) -> httpx.AsyncClient:
        """
        httpx.AsyncClient 인스턴스를 지연 초기화하여 반환

        최초 접근 시점에 클라이언트를 생성하며, 이후 동일 인스턴스를 재사용한다.
        이를 통해 import 시점이 아닌 실제 사용 시점에 연결이 생성된다.
        """
        if self._client is None:
            self._client = httpx.AsyncClient(timeout=self.timeout)
        return self._client

    async def generate(
        self, prompt: str, model: str | None = None, **kwargs
    ) -> LLMResponse:
        """
        Ollama 서버에 프롬프트를 전송하고 생성된 텍스트를 반환한다.

        Ollama /api/generate API 를 호출하며, stream: false 로 설정하여
        단일 JSON 응답을 받는다. model 파라미터가 None 이면 기본 모델을 사용한다.

        Args:
            prompt: LLM 에 전송할 입력 텍스트
            model: 사용할 Ollama 모델명 (None 이면 기본값 deepseek-r1:1.5b)
            **kwargs: Ollama API 로 전달할 추가 파라미터 (예: temperature, top_p)

        Returns:
            LLMResponse: 생성된 응답 텍스트(response)와 사용된 모델명(model)

        Raises:
            httpx.HTTPStatusError: Ollama 서버가 오류 응답을 반환한 경우
            httpx.ConnectError: Ollama 서버에 연결할 수 없는 경우
        """
        # 모델명이 지정되지 않으면 기본 모델 사용
        model_name = model or self.default_model

        # Ollama API 요청 페이로드 구성
        payload = {
            "model": model_name,
            "prompt": prompt,
            "stream": False,  # 스트리밍 비활성화: 단일 JSON 응답
        }
        # temperature 등 추가 파라미터가 있으면 병합
        payload.update(kwargs)

        # Ollama /api/generate 엔드포인트로 비동기 POST 요청
        response = await self.client.post(
            f"{self.base_url}/api/generate", json=payload
        )

        # HTTP 오류(4xx, 5xx) 발생 시 예외 발생
        response.raise_for_status()

        # JSON 응답 파싱
        data = response.json()

        # LLMResponse 모델로 래핑하여 반환
        return LLMResponse(
            response=data["response"],
            model=data.get("model", model_name),
        )

    async def close(self):
        """
        HTTP 클라이언트 연결을 정리(cleanup)한다.

        FastAPI lifespan 의 shutdown 단계에서 호출되어
        애플리케이션 종료 시 열린 HTTP 연결을 안전하게 닫는다.
        """
        if self._client is not None:
            await self._client.aclose()
            self._client = None
