/**
 * 백엔드 LLM API 클라이언트
 *
 * 모든 LLM 호출은 이 모듈을 통해 백엔드 POST /llm 엔드포인트로 전달된다.
 * Bearer 토큰은 localStorage 에 저장되며, 요청 시 Authorization 헤더에 포함된다.
 */

// Vite 환경변수에서 API 기본 URL 을 가져옴 (기본값: /api)
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api';

// localStorage 키
const TOKEN_STORAGE_KEY = 'llm_bearer_token';

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

/** 백엔드 /llm 응답 형식 */
export interface LLMResponse {
  response: string;
  model: string;
}

/** 대화 메시지 형식 */
export interface ChatMessage {
  role: 'user' | 'assistant' | 'system';
  content: string;
}

/**
 * 백엔드에 LLM 추론을 요청한다.
 *
 * @param messages - LLM 에 전송할 대화 메시지 배열
 * @param model    - 사용할 모델명 (미지정 시 Provider 기본값)
 * @returns 응답 텍스트와 사용된 모델명
 */
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

  return response.json() as Promise<LLMResponse>;
}
