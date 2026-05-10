"""
애플리케이션 설정 모듈

.env 파일과 환경변수에서 설정값을 읽어와 Settings 객체로 제공한다.
python-dotenv 를 통해 .env 파일을 자동으로 로드하며,
환경변수가 설정되지 않은 경우 기본값을 사용한다.
"""

import os
from dataclasses import dataclass, field
from dotenv import load_dotenv

# .env 파일을 환경변수로 로드 (이미 설정된 환경변수는 덮어쓰지 않음)
load_dotenv()


@dataclass(frozen=True)
class Settings:
    """
    애플리케이션 전체 설정을 담는 불변 데이터 클래스

    각 필드는 default_factory 를 통해 환경변수에서 값을 읽어오며,
    환경변수가 없으면 지정된 기본값을 사용한다.
    frozen=True 로 설정되어 생성 후 변경할 수 없다.
    """

    # LLM API 호출 시 인증에 사용할 Bearer 토큰
    # .env 파일의 LLM_BEARER_TOKEN 또는 환경변수로 설정
    bearer_token: str = field(
        default_factory=lambda: os.getenv("LLM_BEARER_TOKEN", "")
    )

    # 사용할 LLM 제공자 식별자 ("ollama", "deepseek", 추후 "openai" 등)
    # 이 값에 따라 providers/__init__.py 의 팩토리 함수가 적절한 구현체를 반환한다
    llm_provider: str = field(
        default_factory=lambda: os.getenv("LLM_PROVIDER", "ollama")
    )

    # ---- Ollama 설정 ----

    # Ollama 서버의 기본 URL (로컬 실행 기준 http://localhost:11434)
    ollama_base_url: str = field(
        default_factory=lambda: os.getenv("OLLAMA_BASE_URL", "http://localhost:11434")
    )

    # Ollama 에서 사용할 기본 모델명
    # deepseek-r1:1.5b 는 경량 추론 모델로 로컬 실행에 적합
    ollama_model: str = field(
        default_factory=lambda: os.getenv("OLLAMA_MODEL", "deepseek-r1:1.5b")
    )

    # ---- DeepSeek API 설정 ----

    # DeepSeek API 인증 키 (https://platform.deepseek.com 에서 발급)
    deepseek_api_key: str = field(
        default_factory=lambda: os.getenv("DEEPSEEK_API_KEY", "")
    )

    # DeepSeek 기본 모델
    # deepseek-v4-flash: 빠른 응답, 비용 효율적 (추천)
    # deepseek-v4-pro: 고품질 추론, 복잡한 작업용
    deepseek_model: str = field(
        default_factory=lambda: os.getenv("DEEPSEEK_MODEL", "deepseek-v4-flash")
    )

    # ---- OpenAI API 설정 ----

    # OpenAI API 인증 키 (https://platform.openai.com 에서 발급)
    openai_api_key: str = field(
        default_factory=lambda: os.getenv("OPENAI_API_KEY", "")
    )

    # OpenAI 기본 모델
    # gpt-4.1: 최신 고품질 모델 (추천)
    # gpt-4.1-mini: 빠른 응답, 비용 효율적
    openai_model: str = field(
        default_factory=lambda: os.getenv("OPENAI_MODEL", "gpt-4.1-mini")
    )

    # ---- Custom OpenAI-compatible API 설정 ----

    # OpenAI 호환 API 의 기본 URL (vLLM, OpenRouter, Groq 등)
    custom_base_url: str = field(
        default_factory=lambda: os.getenv("CUSTOM_BASE_URL", "http://localhost:8000")
    )

    # Custom API 인증 키 (필요한 경우만 설정)
    custom_api_key: str = field(
        default_factory=lambda: os.getenv("CUSTOM_API_KEY", "")
    )

    # Custom 기본 모델명
    custom_model: str = field(
        default_factory=lambda: os.getenv("CUSTOM_MODEL", "default")
    )

    # ---- 로그 설정 ----

    # 로그 로테이션 방식: "size" (파일 크기 기준) 또는 "time" (시간 기준)
    log_rotation: str = field(
        default_factory=lambda: os.getenv("LOG_ROTATION", "size")
    )

    # size 모드: 로그 파일 최대 크기 (MB)
    log_max_mb: int = field(
        default_factory=lambda: int(os.getenv("LOG_MAX_MB", "10"))
    )

    # 보관할 백업 파일 개수
    log_backup_count: int = field(
        default_factory=lambda: int(os.getenv("LOG_BACKUP_COUNT", "5"))
    )

    # time 모드: 로테이션 주기
    # "S"=초, "M"=분, "H"=시간, "D"=일, "midnight"=자정, "W0"~"W6"=요일
    log_rotation_when: str = field(
        default_factory=lambda: os.getenv("LOG_ROTATION_WHEN", "midnight")
    )

    # time 모드: 로테이션 간격 (when="H", interval=6 → 6시간마다)
    log_rotation_interval: int = field(
        default_factory=lambda: int(os.getenv("LOG_ROTATION_INTERVAL", "1"))
    )

    # ---- Oracle Database 설정 ----

    oracle_host: str = field(
        default_factory=lambda: os.getenv("ORACLE_HOST", "localhost")
    )
    oracle_port: int = field(
        default_factory=lambda: int(os.getenv("ORACLE_PORT", "1521"))
    )
    oracle_service_name: str = field(
        default_factory=lambda: os.getenv("ORACLE_SERVICE_NAME", "XEPDB1")
    )
    oracle_user: str = field(
        default_factory=lambda: os.getenv("ORACLE_USER", "app_user")
    )
    oracle_password: str = field(
        default_factory=lambda: os.getenv("ORACLE_PASSWORD", "")
    )
    oracle_ro_user: str = field(
        default_factory=lambda: os.getenv("ORACLE_RO_USER", "")
    )
    oracle_ro_password: str = field(
        default_factory=lambda: os.getenv("ORACLE_RO_PASSWORD", "")
    )
    oracle_pool_min: int = field(
        default_factory=lambda: int(os.getenv("ORACLE_POOL_MIN", "1"))
    )
    oracle_pool_max: int = field(
        default_factory=lambda: int(os.getenv("ORACLE_POOL_MAX", "5"))
    )
    oracle_pool_increment: int = field(
        default_factory=lambda: int(os.getenv("ORACLE_POOL_INCREMENT", "1"))
    )


# 애플리케이션 전역에서 사용할 단일 설정 인스턴스
# 모듈 임포트 시점에 한 번 생성되며, 이후 변경되지 않음
settings = Settings()
