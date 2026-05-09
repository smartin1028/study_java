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

import json
import logging
import time
from contextlib import asynccontextmanager
import gzip
import os
import shutil
from logging.handlers import RotatingFileHandler, TimedRotatingFileHandler
from pathlib import Path
from fastapi import FastAPI, Depends, HTTPException, Request, status
from fastapi.security import APIKeyHeader
from config import settings
from models import LLMRequest, LLMResponse
from providers import get_provider, AbstractLLMProvider

LOG_DIR = Path(__file__).parent / "logs"
LOG_DIR.mkdir(exist_ok=True)


class CompressedRotatingFileHandler(RotatingFileHandler):
    """크기 기반 로그 로테이션 + .gz 압축"""

    def doRollover(self):
        if self.stream:
            self.stream.close()
            self.stream = None

        for i in range(self.backupCount - 1, 0, -1):
            sfn = f"{self.baseFilename}.{i}.gz"
            dfn = f"{self.baseFilename}.{i + 1}.gz"
            if os.path.exists(sfn):
                if os.path.exists(dfn):
                    os.remove(dfn)
                os.rename(sfn, dfn)

        oldest = f"{self.baseFilename}.{self.backupCount + 1}.gz"
        if os.path.exists(oldest):
            os.remove(oldest)

        if os.path.exists(self.baseFilename):
            dfn = f"{self.baseFilename}.1.gz"
            with open(self.baseFilename, "rb") as f_in:
                with gzip.open(dfn, "wb") as f_out:
                    shutil.copyfileobj(f_in, f_out)

        if not self.delay:
            self.stream = self._open()


class CompressedTimedRotatingFileHandler(TimedRotatingFileHandler):
    """시간 기반 로그 로테이션 + .gz 압축"""

    def doRollover(self):
        if self.stream:
            self.stream.close()
            self.stream = None

        # 현재 로그 파일의 suffix 생성 (TimedRotatingFileHandler 와 동일)
        current_time = int(time.time())
        dst_now = current_time - (current_time % self.interval)
        time_tuple = time.localtime(dst_now)
        dfn = f"{self.baseFilename}.{time.strftime(self.suffix, time_tuple)}.gz"

        if os.path.exists(self.baseFilename):
            with open(self.baseFilename, "rb") as f_in:
                with gzip.open(dfn, "wb") as f_out:
                    shutil.copyfileobj(f_in, f_out)

        # 오래된 백업 정리
        if self.backupCount > 0:
            for s in self.getFilesToDelete():
                os.remove(s)

        if not self.delay:
            self.stream = self._open()

    def getFilesToDelete(self):
        dir_name, base_name = os.path.split(self.baseFilename)
        file_names = os.listdir(dir_name or ".")
        prefix = base_name + "."
        result = []
        for fn in file_names:
            if fn.startswith(prefix) and fn.endswith(".gz"):
                result.append(os.path.join(dir_name, fn))
        result.sort()
        if len(result) >= self.backupCount:
            result = result[:len(result) - self.backupCount + 1]
        else:
            result = []
        return result


def _build_file_handler() -> logging.Handler:
    """설정에 따라 크기 또는 시간 기반 파일 핸들러를 생성"""
    log_path = str(LOG_DIR / "app.log")
    backup = settings.log_backup_count

    if settings.log_rotation == "time":
        return CompressedTimedRotatingFileHandler(
            log_path,
            when=settings.log_rotation_when,
            interval=settings.log_rotation_interval,
            backupCount=backup,
            encoding="utf-8",
        )
    else:
        return CompressedRotatingFileHandler(
            log_path,
            maxBytes=settings.log_max_mb * 1024 * 1024,
            backupCount=backup,
            encoding="utf-8",
        )


logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(name)s: %(message)s",
    handlers=[
        logging.StreamHandler(),
        _build_file_handler(),
    ],
)
logger = logging.getLogger("eumgrowthflow")


@asynccontextmanager
async def lifespan(app: FastAPI):
    """
    애플리케이션 생명주기 관리

    Startup: 현재 설정된 Provider 와 모델 정보를 로그로 출력
    Shutdown: Provider 의 HTTP 클라이언트 연결 정리
    """
    provider = get_provider()
    model = getattr(provider, "default_model", "unknown")
    logger.info(
        "LLM Provider: %s, 기본 모델: %s",
        settings.llm_provider,
        model,
    )
    yield
    if hasattr(provider, "close"):
        await provider.close()


# FastAPI 애플리케이션 인스턴스 생성 (생명주기 핸들러 포함)
app = FastAPI(lifespan=lifespan)


@app.middleware("http")
async def log_requests(request: Request, call_next):
    start = time.perf_counter()

    body = await request.body()
    if body:
        try:
            data = json.loads(body)
            messages = data.get("messages", [])
            model = data.get("model") or settings.llm_provider
            parts = []
            for msg in messages:
                role = msg.get("role", "?")
                content = msg.get("content", "")
                truncated = content[:80] + "..." if len(content) > 80 else content
                parts.append(f"[{role}: {truncated}]")
            history = " ".join(parts)
            logger.info("→ %s %s model=%s %s", request.method, request.url.path, model, history)
        except (json.JSONDecodeError, UnicodeDecodeError):
            logger.info("→ %s %s", request.method, request.url.path)
    else:
        logger.info("→ %s %s", request.method, request.url.path)

    # 요청 바디를 다시 읽을 수 있도록 복원
    async def receive():
        return {"type": "http.request", "body": body, "more_body": False}
    request._receive = receive

    response = await call_next(request)
    elapsed = time.perf_counter() - start
    logger.info("← %s %d (%.2fs)", request.url.path, response.status_code, elapsed)
    return response


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
      1. 클라이언트 → LLMRequest (messages, model?)
      2. Provider.generate(messages, model) → LLM API 호출
      3. LLM API 응답 → LLMResponse (response, model)
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
        messages_dicts = [msg.model_dump() for msg in request.messages]
        result = await provider.generate(messages_dicts, request.model)
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
