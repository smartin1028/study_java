"""
LLM Provider 팩토리 모듈

LLM_PROVIDER 설정값에 따라 적절한 Provider 구현체를 생성하여 반환한다.
FastAPI 의 Depends() 메커니즘을 통해 엔드포인트에 의존성 주입된다.

새로운 LLM 제공자(예: OpenAI)를 추가하려면:
  1. providers/ 디렉토리에 새 구현체 파일 생성 (예: openai.py)
  2. AbstractLLMProvider 를 상속받아 generate() 메서드 구현
  3. 이 파일의 get_provider() 함수에 match case 분기 추가
"""

from providers.base import AbstractLLMProvider
from providers.ollama import OllamaProvider
from config import settings


def get_provider() -> AbstractLLMProvider:
    """
    설정된 LLM_PROVIDER 값에 따라 적절한 Provider 인스턴스를 반환한다.

    Returns:
        AbstractLLMProvider: LLM 제공자 구현체 인스턴스

    Raises:
        ValueError: 지원하지 않는 LLM_PROVIDER 값이 설정된 경우

    사용 예 (FastAPI 의존성 주입):
        @app.post("/llm")
        async def call_llm(provider: AbstractLLMProvider = Depends(get_provider)):
            ...
    """
    match settings.llm_provider:
        case "ollama":
            return OllamaProvider()
        case _:
            raise ValueError(
                f"Unknown LLM_PROVIDER: {settings.llm_provider}"
            )
