# EumGrowthFlow 백엔드 아키텍처 문서

## 개요

EumGrowthFlow 백엔드는 FastAPI 기반의 LLM API 서버로, **Provider 패턴**을 사용하여 다양한 LLM 제공자(Ollama, OpenAI 등)를 교체 가능하게 설계되었다.

현재는 로컬 Ollama 서버와 연동되며, 기본 모델은 `deepseek-r1:1.5b` 이다.

---

## 디렉토리 구조

```
backend/app/
├── main.py                  # FastAPI 진입점, 라우터, 인증
├── config.py                # 환경변수 기반 설정
├── models.py                # Pydantic 요청/응답 모델
├── providers/
│   ├── __init__.py          # Provider 팩토리 (get_provider)
│   ├── base.py              # 추상 인터페이스 (AbstractLLMProvider)
│   └── ollama.py            # Ollama 구현체 (OllamaProvider)
├── tests/
│   ├── conftest.py          # 테스트 픽스처 (mock provider)
│   ├── test_main.py         # 엔드포인트 인증 테스트
│   └── test_providers.py    # Provider 단위 테스트
├── .env                     # 환경변수 설정 파일
├── .env_sample              # 환경변수 예시 파일
└── pyproject.toml           # 프로젝트 의존성 정의
```

---

## 아키텍처: Provider 패턴

### 핵심 개념

엔드포인트 코드가 구체적인 LLM 서비스 구현에 직접 의존하지 않고, **추상 인터페이스에만 의존**하도록 설계하였다. 이를 통해 새로운 LLM 서비스를 추가할 때 기존 코드 수정 없이 Provider 구현체만 추가하면 된다.

### 계층 구조

```
┌─────────────────────────────────────────────┐
│  클라이언트 (프론트엔드 / curl / etc.)       │
│  POST /llm  {prompt: "...", model?: "..."}  │
└──────────────────┬──────────────────────────┘
                   │ Bearer Token 인증
                   ▼
┌─────────────────────────────────────────────┐
│  main.py - FastAPI 엔드포인트                │
│  - validate_bearer_token()  → 인증 검증     │
│  - call_llm()               → Provider 호출 │
│  - lifespan()               → 리소스 정리   │
└──────────────────┬──────────────────────────┘
                   │ Depends(get_provider)
                   ▼
┌─────────────────────────────────────────────┐
│  providers/__init__.py - 팩토리 함수         │
│  get_provider() → LLM_PROVIDER 값에 따라    │
│                    적절한 구현체 반환        │
└──────────────────┬──────────────────────────┘
                   │
         ┌─────────┴─────────┐
         ▼                   ▼
┌─────────────────┐  ┌─────────────────┐
│  OllamaProvider  │  │  (향후)         │
│  (ollama.py)     │  │  OpenAIProvider │
│                  │  │  ...            │
│  POST /api/      │  │                 │
│  generate        │  │                 │
└────────┬─────────┘  └─────────────────┘
         │
         ▼
┌─────────────────────────────────────────────┐
│  Ollama 서버 (localhost:11434)               │
│  deepseek-r1:1.5b 모델                       │
└─────────────────────────────────────────────┘
```

---

## 데이터 흐름

### 1. 요청 흐름 (Request → Response)

```
[클라이언트]
    │  POST /llm
    │  Header:  Authorization: Bearer <token>
    │  Body:    {"prompt": "안녕하세요", "model": null}
    ▼
[validate_bearer_token]
    │  1. Authorization 헤더 존재 여부 확인
    │  2. "Bearer " 접두사 확인
    │  3. 토큰 값 일치 여부 확인 (LLM_BEARER_TOKEN 과 비교)
    │  → 실패 시 401 또는 403 반환
    ▼
[call_llm 엔드포인트]
    │  FastAPI 가 LLMRequest 로 요청 바디 파싱 및 검증
    │  get_provider() 로 Provider 인스턴스 주입
    ▼
[Provider.generate(prompt, model)]
    │  1. model 이 None 이면 기본 모델 사용 (deepseek-r1:1.5b)
    │  2. Ollama API 호출 페이로드 구성
    │     {"model": "deepseek-r1:1.5b", "prompt": "안녕하세요", "stream": false}
    │  3. POST http://localhost:11434/api/generate
    ▼
[Ollama 서버]
    │  모델 추론 수행
    │  응답: {"model": "...", "response": "...", "done": true}
    ▼
[Provider.generate → LLMResponse]
    │  Ollama 응답을 LLMResponse 로 변환
    │  {"response": "안녕하세요! 무엇을 도와드릴까요?", "model": "deepseek-r1:1.5b"}
    ▼
[클라이언트]
    │  HTTP 200 + LLMResponse JSON
```

### 2. Provider 선택 흐름

```
LLM_PROVIDER 환경변수 값
    │
    ├── "ollama" ──→ OllamaProvider()  반환
    │
    └── 그 외     ──→ ValueError 발생 (Unknown LLM_PROVIDER)
```

---

## 주요 컴포넌트 상세

### config.py — 설정 관리

```python
@dataclass(frozen=True)
class Settings:
    bearer_token: str         # LLM_BEARER_TOKEN  (인증 토큰)
    llm_provider: str         # LLM_PROVIDER      (사용할 Provider 식별자)
    ollama_base_url: str      # OLLAMA_BASE_URL   (Ollama 서버 주소)
    ollama_model: str         # OLLAMA_MODEL       (기본 모델명)
```

- `.env` 파일을 `python-dotenv` 로 자동 로드
- `frozen=True` 로 불변성 보장
- `default_factory` 로 환경변수 우선, 없으면 기본값 사용

### models.py — 데이터 스키마

| 모델 | 필드 | 타입 | 설명 |
|------|------|------|------|
| `LLMRequest` | `prompt` | `str` | LLM 에 보낼 프롬프트 (1글자 이상 필수) |
| | `model` | `str \| None` | 사용할 모델명 (미지정 시 기본값) |
| `LLMResponse` | `response` | `str` | LLM 이 생성한 응답 텍스트 |
| | `model` | `str` | 응답 생성에 사용된 모델명 |

### providers/base.py — 추상 인터페이스

```python
class AbstractLLMProvider(ABC):
    @abstractmethod
    async def generate(self, prompt: str, model: str | None = None, **kwargs) -> LLMResponse:
        ...
```

모든 Provider 구현체가 구현해야 하는 계약:
- **비동기** 메서드 (`async def`) — FastAPI 이벤트 루프와 호환
- **`**kwargs`** — Provider 별 추가 파라미터(temperature 등) 확장 가능
- **반환 타입** — 항상 `LLMResponse` 로 일관성 유지

### providers/ollama.py — Ollama 구현체

| 특징 | 설명 |
|------|------|
| HTTP 클라이언트 | `httpx.AsyncClient` (비동기) |
| 지연 초기화 | 첫 API 호출 시점에 클라이언트 생성 |
| 타임아웃 | 120초 (대규모 모델 추론 고려) |
| API 엔드포인트 | `POST {base_url}/api/generate` |
| 스트리밍 | `stream: false` (단일 JSON 응답) |

### providers/__init__.py — 팩토리

```python
def get_provider() -> AbstractLLMProvider:
    match settings.llm_provider:
        case "ollama":
            return OllamaProvider()
        case _:
            raise ValueError(...)
```

FastAPI 의존성 주입을 위해 설계:
```python
@app.post("/llm")
async def call_llm(provider: AbstractLLMProvider = Depends(get_provider)):
    ...
```

---

## 인증 흐름

```
[클라이언트 요청]
    │  Authorization: Bearer <token>
    ▼
[APIKeyHeader(name="Authorization", auto_error=False)]
    │  헤더가 없으면 authorization = None (auto_error=False 이므로 예외 발생 안 함)
    ▼
[validate_bearer_token()]
    │
    ├── authorization 이 None 또는 "Bearer " 미포함 → 401 Unauthorized
    │
    ├── 토큰 값 != settings.bearer_token → 403 Forbidden
    │
    └── 토큰 일치 → 통과
```

---

## 생명주기 (Lifespan)

```
[애플리케이션 시작]
    │  FastAPI 인스턴스 생성
    │  Provider 는 아직 초기화되지 않음 (지연 초기화)
    ▼
[요청 처리 중]
    │  첫 LLM 호출 시 httpx.AsyncClient 생성
    │  이후 동일 클라이언트 재사용
    ▼
[애플리케이션 종료]
    │  lifespan() 의 shutdown 단계 실행
    │  provider.close() → httpx.AsyncClient.aclose()
    │  열린 HTTP 연결 정리
```

---

## 환경변수 목록

| 변수명 | 기본값 | 설명 |
|--------|--------|------|
| `LLM_BEARER_TOKEN` | (없음) | API 인증용 Bearer 토큰 |
| `LLM_PROVIDER` | `ollama` | 사용할 LLM Provider 식별자 |
| `OLLAMA_BASE_URL` | `http://localhost:11434` | Ollama 서버 URL |
| `OLLAMA_MODEL` | `deepseek-r1:1.5b` | 기본 Ollama 모델명 |

---

## 확장 가이드: 새 Provider 추가

1. `providers/` 디렉토리에 새 파일 생성 (예: `openai.py`)
2. `AbstractLLMProvider` 상속 및 `generate()` 구현

```python
# providers/openai.py 예시
class OpenAIProvider(AbstractLLMProvider):
    async def generate(self, prompt, model=None, **kwargs):
        # OpenAI API 호출 구현
        ...
```

3. `providers/__init__.py` 의 `get_provider()` 에 분기 추가

```python
match settings.llm_provider:
    case "ollama":
        return OllamaProvider()
    case "openai":
        return OpenAIProvider()  # ← 추가
```

4. `config.py` 에 필요한 설정 필드 추가 (예: `openai_api_key`)

기존 `main.py`, `models.py`, `base.py` 등은 전혀 수정할 필요가 없다.

---

## 테스트 전략

- **test_main.py**: FastAPI 엔드포인트의 인증 로직 검증 (mock provider 사용)
- **test_providers.py**: `OllamaProvider.generate()` 의 HTTP 호출 및 응답 처리 검증
- **conftest.py**: mock provider fixture 제공, `dependency_overrides` 로 실제 Provider 대체

```bash
# 전체 테스트 실행
python -m pytest tests/ -v

# 단일 파일 테스트
python -m pytest tests/test_providers.py -v
```

---

## 실행 방법

```bash
# 의존성 설치
uv sync

# 서버 실행
uvicorn main:app --host 0.0.0.0 --port 8000

# API 호출
curl -X POST http://localhost:8000/llm \
  -H "Authorization: Bearer <your_token>" \
  -H "Content-Type: application/json" \
  -d '{"prompt": "안녕하세요"}'
```
