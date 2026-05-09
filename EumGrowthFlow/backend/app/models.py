"""
Pydantic 요청/응답 모델

API 엔드포인트의 입출력 스키마를 정의한다.
FastAPI 는 이 모델을 기반으로 자동 요청 검증, 직렬화/역직렬화, OpenAPI 문서를 생성한다.
"""

from typing import Literal

from pydantic import BaseModel, Field


class Message(BaseModel):
    """대화 메시지 스키마"""

    role: Literal["user", "assistant", "system"] = Field(
        ...,
        description="메시지 발신자 (user, assistant, system)",
    )
    content: str = Field(
        ...,
        min_length=1,
        description="메시지 내용",
    )


class LLMRequest(BaseModel):
    """
    POST /llm 엔드포인트의 요청 바디 스키마

    클라이언트는 대화 메시지 배열과 선택적으로 모델명을 전달한다.
    """

    messages: list[Message] = Field(
        ...,
        min_length=1,
        description="LLM 에 전송할 대화 메시지 배열",
    )
    model: str | None = Field(
        None,
        description="사용할 모델 이름. 지정하지 않으면 Provider 의 기본 모델 사용",
    )


class LLMResponse(BaseModel):
    """
    POST /llm 엔드포인트의 응답 스키마

    생성된 텍스트와 실제 사용된 모델명을 포함한다.
    FastAPI 의 response_model 파라미터에 지정되어 응답 직렬화에 사용된다.
    """

    response: str = Field(
        ...,
        description="LLM 이 생성한 응답 텍스트",
    )
    model: str = Field(
        ...,
        description="응답을 생성하는 데 사용된 실제 모델명",
    )
