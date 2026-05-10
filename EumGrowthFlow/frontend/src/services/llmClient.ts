//
// llmClient — 백엔드 LLM API 호출을 담당하는 서비스 모듈
//
// Java 비교: 백엔드 API 를 호출하는 HTTP Client Service 클래스.
//
//   public class LlmClient {
//       private static final String API_URL = "http://localhost:8000/llm";
//       private static final String TOKEN_KEY = "llm_bearer_token";
//
//       public static String getBearerToken() {
//           return Preferences.userRoot().get(TOKEN_KEY, null);
//       }
//       ...
//   }
//
// React 에서는 "모듈 스코프 함수" 를 사용한다.
// - Class 없이 독립 함수를 export 하는 방식이 일반적.
// - 상태는 localStorage (브라우저 API) 로 관리한다.
// - 비동기 호출은 async/await + fetch API 를 사용한다.
//

// Vite 환경변수에서 API 기본 URL 을 읽는다. (기본값: /api)
// Java: @Value("${api.base-url:/api}") private String apiBaseUrl;
//       또는 System.getenv("VITE_API_BASE_URL");
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api';

// localStorage 키 상수
// Java: private static final String TOKEN_STORAGE_KEY = "llm_bearer_token";
const TOKEN_STORAGE_KEY = 'llm_bearer_token';

//
// ── 토큰 관리 함수들 ─────────────────────────────────────────────────────
//
// Java 에서는 보통 TokenService 클래스의 인스턴스 메서드로 관리하지만,
// React/TypeScript 에서는 모듈 레벨 함수로 충분하다.
// (의존성 주입보다 간단한 함수가 선호되는 패턴)
//

/** 저장된 Bearer 토큰을 반환한다. 없으면 null */
export function getBearerToken(): string | null {
  return localStorage.getItem(TOKEN_STORAGE_KEY);
}

/** Bearer 토큰을 localStorage 에 저장한다 */
export function setBearerToken(token: string): void {
  localStorage.setItem(TOKEN_STORAGE_KEY, token);
}

/** Bearer 토큰이 설정되어 있는지 확인한다 */
export function hasBearerToken(): boolean {
  const token = getBearerToken();
  return token !== null && token.length > 0;
}

/** Bearer 토큰을 localStorage 에서 제거한다 */
export function clearBearerToken(): void {
  localStorage.removeItem(TOKEN_STORAGE_KEY);
}

//
// ── 데이터 타입 ───────────────────────────────────────────────────────────
//
// TypeScript interface = Java interface + record
// "export" 키워드 → 외부 모듈이 이 타입을 import 할 수 있다.
//

/** 백엔드 /llm 응답 형식 */
export interface LLMResponse {
  response: string;
  model: string;
}

/** 대화 메시지 형식 */
export interface ChatMessage {
  role: 'user' | 'assistant' | 'system';  // Union Type (Java: Enum)
  content: string;
}

//
// callLLM: 백엔드에 LLM 추론을 요청하는 비동기 함수
//
// Java 비교:
//   public CompletableFuture<LLMResponse> callLLM(List<ChatMessage> messages, String model) {
//       return CompletableFuture.supplyAsync(() -> {
//           HttpRequest request = HttpRequest.newBuilder()
//               .uri(URI.create(apiUrl))
//               .header("Authorization", "Bearer " + getBearerToken())
//               .POST(HttpRequest.BodyPublishers.ofString(body))
//               .build();
//           HttpResponse<String> response = HttpClient.newHttpClient()
//               .send(request, HttpResponse.BodyHandlers.ofString());
//           if (response.statusCode() != 200) { throw new IOException(...); }
//           return objectMapper.readValue(response.body(), LLMResponse.class);
//       });
//   }
//
// JavaScript 의 fetch() 는 Java 의 HttpClient + JSON.parse 를 합친 것.
// async/await → CompletableFuture + .thenApply() 의 문법 설탕.
//
export async function callLLM(
  messages: ChatMessage[],
  model?: string,
): Promise<LLMResponse> {
  const token = getBearerToken();
  if (!token) {
    throw new Error(
      'Bearer 토큰이 설정되지 않았습니다. 설정 버튼(🔑)에서 API 토큰을 입력해주세요.',
    );
  }

  //
  // Record<string, unknown>: TypeScript 의 동적 객체 타입
  // Java: Map<String, Object> body = new HashMap<>();
  //
  const body: Record<string, unknown> = { messages };
  if (model) {
    body.model = model;
  }

  const response = await fetch(`${API_BASE_URL}/llm`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(body),
  });

  if (!response.ok) {
    const errorText = await response.text();
    if (response.status === 401 || response.status === 403) {
      throw new Error(
        `인증 실패 (${response.status}). Bearer 토큰을 확인해주세요.`,
      );
    }
    throw new Error(`백엔드 API 오류 (${response.status}): ${errorText}`);
  }

  // response.json() → JSON 문자열을 JS 객체로 파싱
  // Java: objectMapper.readValue(response.getBody(), LLMResponse.class);
  return response.json() as Promise<LLMResponse>;
}
