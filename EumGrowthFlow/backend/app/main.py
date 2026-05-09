"""
EumGrowthFlow 백엔드 애플리케이션 진입점

FastAPI 기반의 LLM API 서버.
POST /llm 엔드포인트를 통해 Bearer 토큰 인증 후 LLM 추론을 수행한다.

실행 방법:
    uvicorn main:app --host 0.0.0.0 --port 8000

주요 설계:
  - Provider 패턴: LLM 제공자를 인터페이스로 추상화하여 교체 가능하게 설계
  - 의존성 주입: FastAPI Depends() 로 인증 및 Provider 주입
  - Lifespan: 앱 시작/종료 시 리소스 관리
"""

from contextlib import asynccontextmanager
from fastapi import FastAPI, Depends, HTTPException, status
from fastapi.security import APIKeyHeader
from config import settings
from models import LLMRequest, LLMResponse
from providers import get_provider, AbstractLLMProvider


@asynccontextmanager
async def lifespan(app: FastAPI):
    """
    애플리케이션 생명주기 관리

    Startup: (현재 별도 초기화 없음 - Provider 는 지연 초기화)
    Shutdown: Provider 의 HTTP 클라이언트 연결 정리
    """
    # Startup: 필요한 경우 여기에 초기화 코드 추가
    yield
    # Shutdown: Provider 가 close() 메서드를 지원하면 호출하여 리소스 정리
    provider = get_provider()
    if hasattr(provider, "close"):
        await provider.close()


# FastAPI 애플리케이션 인스턴스 생성 (생명주기 핸들러 포함)
app = FastAPI(lifespan=lifespan)


def validate_bearer_token(
    authorization: str = Depends(
        APIKeyHeader(name="Authorization", auto_error=False)
    ),
):
    """
    Bearer 토큰 인증 의존성

    HTTP Authorization 헤더에서 Bearer 토큰을 추출하고 검증한다.
    auto_error=False 로 설정하여 헤더 누락 시 FastAPI 가 자동으로 403 을
    반환하지 않고 함수 내에서 직접 401 을 반환하도록 제어한다.

    Args:
        authorization: Authorization 헤더 값 (자동 주입)

    Returns:
        str: 검증된 토큰 문자열

    Raises:
        HTTPException 401: 헤더가 없거나 "Bearer " 접두사가 없는 경우
        HTTPException 403: 토큰이 설정된 값과 일치하지 않는 경우
    """
    # 헤더가 없거나 "Bearer " 로 시작하지 않으면 401
    if not authorization or not authorization.startswith("Bearer "):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid or missing bearer token",
        )

    # "Bearer " 접두사를 제거하고 실제 토큰만 추출
    # split(" ", 1) 로 분할하여 토큰 자체에 공백이 포함된 경우에도 대응
    token = authorization.split(" ", 1)[1]

    # 토큰이 일치하지 않으면 403 (인증 실패)
    if token != settings.bearer_token:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Invalid bearer token",
        )

    return token


@app.post("/llm", response_model=LLMResponse)
async def call_llm(
    request: LLMRequest,
    _token: str = Depends(validate_bearer_token),
    provider: AbstractLLMProvider = Depends(get_provider),
):
    """
    LLM 추론 엔드포인트

    Bearer 토큰 인증 후 설정된 LLM Provider 를 통해 프롬프트를 전송하고
    생성된 응답을 반환한다.

    인증 흐름:
      1. validate_bearer_token 으로 Authorization 헤더 검증
      2. get_provider 로 현재 설정된 LLM Provider 인스턴스 주입

    데이터 흐름:
      1. 클라이언트 → LLMRequest (prompt, model?)
      2. Provider.generate(prompt, model) → Ollama API 호출
      3. Ollama API 응답 → LLMResponse (response, model)
      4. LLMResponse → 클라이언트 JSON 응답

    Args:
        request: 프롬프트와 선택적 모델명을 포함한 요청 바디
        _token: validate_bearer_token 으로 검증된 Bearer 토큰
        provider: get_provider 팩토리가 생성한 LLM Provider 인스턴스

    Returns:
        LLMResponse: 생성된 응답 텍스트와 사용된 모델명

    Raises:
        HTTPException 401: 인증 헤더 누락/형식 오류
        HTTPException 403: 유효하지 않은 Bearer 토큰
        HTTPException 500: LLM 호출 중 발생한 모든 예외
    """
    try:
        # Provider 를 통해 LLM 추론 실행 (비동기)
        result = await provider.generate(request.prompt, request.model)
        return result
    except Exception as e:
        # Provider 레벨의 모든 예외를 500 으로 변환
        # (연결 오류, 타임아웃, Ollama 서버 오류 등)
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=str(e),
        )


# 직접 실행 시 uvicorn 서버 시작
if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host="0.0.0.0", port=8000)
