"""
LLM Provider 추상 인터페이스

모든 LLM 제공자 구현체가 따라야 하는 계약(contract)을 정의한다.
구체적인 구현체(예: OllamaProvider, 향후 OpenAIProvider 등)는
이 추상 클래스를 상속받아 generate() 메서드를 구현해야 한다.

이 패턴을 통해 엔드포인트 코드는 구체적인 구현을 알 필요 없이
인터페이스에만 의존할 수 있다 (의존성 역전 원칙).
"""

from abc import ABC, abstractmethod
from models import LLMResponse


class AbstractLLMProvider(ABC):
    """
    LLM 제공자의 추상 기본 클래스

    모든 구체 Provider 는 이 클래스를 상속받아 비동기 generate() 메서드를 구현해야 한다.
    **kwargs 를 통해 제공자별 추가 파라미터(temperature, max_tokens 등)를 전달할 수 있다.
    """

    @abstractmethod
    async def generate(
        self, messages: list[dict], model: str | None = None, **kwargs
    ) -> LLMResponse:
        """
        LLM 에 대화 메시지를 전송하고 생성된 응답을 반환한다.

        Args:
            messages: [{"role": "user"|"assistant"|"system", "content": "..."}] 형식의 대화 이력
            model: 사용할 모델명. None 이면 Provider 기본 모델 사용
            **kwargs: 제공자별 추가 파라미터 (예: temperature, max_tokens)

        Returns:
            LLMResponse: 생성된 응답 텍스트와 사용된 모델명

        Raises:
            구현체별로 다르며, 일반적으로 HTTP 오류 시 예외 발생
        """
        ...
