# EumGrowthFlow Backend

EumGrowthFlow 의 LLM API 백엔드 서버입니다. Provider 패턴을 기반으로 다양한 LLM 서비스를 교체 가능하게 설계되었습니다.

## 기술 스택

- **Python** 3.13+
- **FastAPI** 0.136+
- **httpx** (비동기 HTTP 클라이언트)
- **Pydantic** v2
- **Ollama** (로컬 LLM)

## 빠른 시작

```bash
# 의존성 설치
uv sync

# 환경변수 설정 (.env 파일 편집)
cp .env_sample .env

# 서버 실행
uvicorn main:app --host 0.0.0.0 --port 8000
```

## API

### POST /llm

Bearer 토큰 인증 후 LLM 추론을 수행합니다.

```bash
curl -X POST http://localhost:8000/llm \
  -H "Authorization: Bearer <your_token>" \
  -H "Content-Type: application/json" \
  -d '{"prompt": "안녕하세요"}'
```

**요청 바디:**

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `prompt` | `string` | O | LLM 에 전송할 프롬프트 |
| `model` | `string` | X | 사용할 모델명 (미지정 시 기본값) |

**응답:**

```json
{
  "response": "안녕하세요! 무엇을 도와드릴까요?",
  "model": "deepseek-r1:1.5b"
}
```

## 환경변수

| 변수명 | 기본값 | 설명 |
|--------|--------|------|
| `LLM_BEARER_TOKEN` | - | API 인증용 Bearer 토큰 |
| `LLM_PROVIDER` | `ollama` | LLM 제공자 선택 |
| `OLLAMA_BASE_URL` | `http://localhost:11434` | Ollama 서버 URL |
| `OLLAMA_MODEL` | `deepseek-r1:1.5b` | 기본 모델명 |

## 테스트

```bash
python -m pytest tests/ -v
```

## 아키텍처

자세한 내용은 [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) 를 참고하세요.

## 라이선스

MIT License — [LICENSE](LICENSE) 참고
