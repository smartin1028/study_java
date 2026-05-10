//
// ChatInterface — LLM 과 대화하는 채팅 패널
//
// Java 비교: JPanel 기반의 채팅 클라이언트 UI.
//           ChatClient chatClient = new ChatClient(model);
//           chatClient.onMessageReceived(msg -> appendMessage(msg));
//
// React 특유 개념:
//   - useRef: 렌더링 간에 유지되는 가변 참조 (DOM 접근용)
//     Java: private JScrollPane scrollPane; (인스턴스 필드, 리렌더링 무관)
//   - useEffect + scrollToBottom: 새 메시지 도착 시 자동 스크롤
//     Java: scrollPane.getVerticalScrollBar().setValue(max);
//   - ReactMarkdown: 외부 라이브러리 (CommonMark → HTML 변환)
//

import { useState, useRef, useEffect } from 'react';
import ReactMarkdown from 'react-markdown';
import { callLLM, hasBearerToken } from '../services/llmClient';
import type { ChatMessage } from '../services/llmClient';
import './ChatInterface.css';

//
// 메시지 데이터 구조체
// Java: record Message(String role, String content, Instant timestamp) { }
//
interface Message {
  role: 'user' | 'assistant';
  content: string;
  timestamp: Date;
}

interface ChatInterfaceProps {
  nodeConfig: {
    model?: string;
  };
  maxHistory?: number;
  onClose: () => void;
}

const ChatInterface = ({
  nodeConfig,
  maxHistory = 5,
  onClose,
}: ChatInterfaceProps) => {
  //
  // 지역 상태들
  // Java: private List<Message> messages = new ArrayList<>();
  //       private String input = "";
  //       private boolean isLoading = false;
  //
  const [messages, setMessages] = useState<Message[]>([]);
  const [input, setInput] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [historyLimit, setHistoryLimit] = useState(maxHistory);

  //
  // useRef: DOM 요소에 대한 참조를 저장한다.
  // Java: private JTextArea chatArea;  — 필드에 컴포넌트 참조 보관.
  //
  // ref.current 는 렌더링 결과물이 실제 DOM 에 마운트된 후 설정된다.
  // useState 와 달리 ref 값 변경은 리렌더링을 유발하지 않는다.
  //
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  //
  // useEffect: messages 가 변경될 때마다 스크롤을 맨 아래로
  // Java: messages.addPropertyChangeListener(evt -> scrollToBottom());
  //
  // deps 가 [messages] → messages 배열이 바뀔 때만 실행
  //
  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const sendMessage = async () => {
    if (!input.trim()) return;

    const userMessage: Message = {
      role: 'user',
      content: input.trim(),
      timestamp: new Date(),
    };

    //
    // setMessages(prev => [...prev, newMsg])
    // "함수형 업데이트": 이전 상태를 인자로 받아 새 상태를 반환한다.
    // Java: List<Message> updated = new ArrayList<>(messages);
    //       updated.add(userMessage); setMessages(updated);
    //
    setMessages((prev) => [...prev, userMessage]);
    setInput('');
    setIsLoading(true);

    try {
      // 최근 메시지만 잘라서 컨텍스트로 전송 (토큰 절약)
      // Java: List<Message> recent = messages.subList(
      //           Math.max(0, messages.size() - historyLimit), messages.size());
      const recentMessages = messages.slice(-historyLimit);
      const chatMessages: ChatMessage[] = [
        ...recentMessages.map((msg) => ({
          role: msg.role as 'user' | 'assistant',
          content: msg.content,
        })),
        { role: 'user' as const, content: userMessage.content },
      ];

      const data = await callLLM(chatMessages, nodeConfig.model);

      const assistantMessage: Message = {
        role: 'assistant',
        content: data.response,
        timestamp: new Date(),
      };

      setMessages((prev) => [...prev, assistantMessage]);
    } catch (error: any) {
      const errorMessage: Message = {
        role: 'assistant',
        content: `❌ 오류: ${error.message}`,
        timestamp: new Date(),
      };
      setMessages((prev) => [...prev, errorMessage]);
    } finally {
      setIsLoading(false);
    }
  };

  //
  // 키보드 이벤트: Enter → 전송 (Shift+Enter 는 줄바꿈)
  // Java: textArea.addKeyListener(new KeyAdapter() {
  //           public void keyPressed(KeyEvent e) {
  //               if (e.getKeyCode() == KeyEvent.VK_ENTER && !e.isShiftDown()) {
  //                   sendMessage();
  //               }
  //           }
  //       });
  //
  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();   // 기본 Enter 동작(줄바꿈) 방지
      sendMessage();
    }
  };

  const clearChat = () => {
    setMessages([]);
  };

  //
  // 동적 버튼 활성화 여부 계산 (렌더링마다 다시 계산됨)
  // Java: boolean canSend = !isLoading && !input.trim().isEmpty() && hasBearerToken();
  //
  const canSend = !isLoading && input.trim().length > 0 && hasBearerToken();

  return (
    <div style={containerStyle}>
      {/* 헤더 */}
      <div style={headerStyle}>
        <div>
          <h3 style={{
            fontSize: '16px', fontWeight: 'bold', marginBottom: '4px',
          }}>
            💬 채팅 인터페이스
          </h3>
          <div style={{ fontSize: '12px', color: '#6b7280' }}>
            모델: {nodeConfig.model || '서버 기본값'} | 히스토리: {historyLimit}개
          </div>
        </div>
        <div style={{ display: 'flex', gap: '8px' }}>
          <button onClick={clearChat} style={clearButtonStyle}>
            🗑️ 초기화
          </button>
          <button onClick={onClose} style={closeButtonStyle}>✕</button>
        </div>
      </div>

      {/* 히스토리 제한 설정 */}
      <div style={historyControlStyle}>
        <label style={{ fontSize: '12px', color: '#6b7280' }}>
          대화 히스토리 개수:
        </label>
        <input
          type="number"
          min="1"
          max="50"
          value={historyLimit}
          onChange={(e) => setHistoryLimit(parseInt(e.target.value) || 5)}
          style={historyInputStyle}
        />
      </div>

      {/* 메시지 목록 — 스크롤 가능 영역 */}
      <div style={messagesContainerStyle}>
        {messages.length === 0 ? (
          <div style={emptyStateStyle}>
            <div style={{ fontSize: '48px', marginBottom: '16px' }}>💬</div>
            <div style={{ fontSize: '14px', color: '#6b7280' }}>
              {hasBearerToken()
                ? '메시지를 입력하여 대화를 시작하세요'
                : '⚠️ API 토큰이 설정되지 않았습니다. 툴바의 🔑 버튼에서 토큰을 설정하세요.'}
            </div>
          </div>
        ) : (
          messages.map((msg, index) => (
            <div
              key={index}
              className={msg.role === 'user' ? 'user-message' : ''}
              style={{
                ...messageStyle,
                // 메시지 역할에 따라 좌/우 정렬
                alignSelf: msg.role === 'user' ? 'flex-end' : 'flex-start',
                backgroundColor: msg.role === 'user' ? '#3b82f6' : '#fff',
                color: msg.role === 'user' ? '#fff' : '#1f2937',
                border:
                  msg.role === 'assistant' ? '1px solid #e5e7eb' : 'none',
              }}
            >
              <div style={{
                fontSize: '11px', marginBottom: '4px', opacity: 0.7,
              }}>
                {msg.role === 'user' ? '👤 You' : '🤖 AI'}
              </div>
              <div className="markdown-content" style={{ wordWrap: 'break-word' }}>
                {/*
                  AI 응답은 Markdown 으로 렌더링, 사용자 입력은 일반 텍스트
                  Java: if (msg.role == "assistant") {
                            markdownRenderer.render(msg.content);
                        } else {
                            new JLabel(msg.content);
                        }
                */}
                {msg.role === 'assistant' ? (
                  <ReactMarkdown>{msg.content}</ReactMarkdown>
                ) : (
                  <div style={{ whiteSpace: 'pre-wrap' }}>{msg.content}</div>
                )}
              </div>
              <div style={{
                fontSize: '10px', marginTop: '4px', opacity: 0.6,
              }}>
                {msg.timestamp.toLocaleTimeString()}
              </div>
            </div>
          ))
        )}
        {/*
          스크롤 앵커: 이 div 로 자동 스크롤된다.
          Java: scrollPane.scrollRectToVisible(anchor.getBounds());
        */}
        <div ref={messagesEndRef} />
      </div>

      {/* 입력 영역 */}
      <div style={inputContainerStyle}>
        <textarea
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyPress={handleKeyPress}
          placeholder={
            hasBearerToken()
              ? '메시지를 입력하세요... (Enter: 전송, Shift+Enter: 줄바꿈)'
              : 'API 토큰을 먼저 설정해주세요.'
          }
          disabled={!hasBearerToken()}
          style={inputStyle}
          rows={3}
        />
        <button
          onClick={sendMessage}
          disabled={!canSend}
          style={sendButtonStyle}
        >
          {isLoading ? '⏳ 전송 중...' : '📤 전송'}
        </button>
      </div>
    </div>
  );
};

const containerStyle: React.CSSProperties = {
  position: 'fixed',
  right: 0,
  top: 0,
  width: '450px',
  height: '100vh',
  backgroundColor: '#f9fafb',
  boxShadow: '-2px 0 8px rgba(0, 0, 0, 0.1)',
  display: 'flex',
  flexDirection: 'column',
  zIndex: 11,
};

const headerStyle: React.CSSProperties = {
  padding: '16px',
  backgroundColor: '#fff',
  borderBottom: '1px solid #e5e7eb',
  display: 'flex',
  justifyContent: 'space-between',
  alignItems: 'center',
};

const historyControlStyle: React.CSSProperties = {
  padding: '12px 16px',
  backgroundColor: '#eff6ff',
  borderBottom: '1px solid #dbeafe',
  display: 'flex',
  alignItems: 'center',
  gap: '8px',
};

const historyInputStyle: React.CSSProperties = {
  width: '60px',
  padding: '4px 8px',
  fontSize: '12px',
  border: '1px solid #d1d5db',
  borderRadius: '4px',
};

const messagesContainerStyle: React.CSSProperties = {
  flex: 1,
  overflowY: 'auto',
  padding: '16px',
  display: 'flex',
  flexDirection: 'column',
  gap: '12px',
};

const emptyStateStyle: React.CSSProperties = {
  display: 'flex',
  flexDirection: 'column',
  alignItems: 'center',
  justifyContent: 'center',
  height: '100%',
  color: '#9ca3af',
};

const messageStyle: React.CSSProperties = {
  maxWidth: '85%',
  padding: '12px',
  borderRadius: '8px',
  fontSize: '14px',
  lineHeight: '1.5',
};

const inputContainerStyle: React.CSSProperties = {
  padding: '16px',
  backgroundColor: '#fff',
  borderTop: '1px solid #e5e7eb',
  display: 'flex',
  gap: '8px',
  alignItems: 'flex-end',
};

const inputStyle: React.CSSProperties = {
  flex: 1,
  padding: '8px 12px',
  fontSize: '14px',
  border: '1px solid #d1d5db',
  borderRadius: '6px',
  outline: 'none',
  resize: 'none',
  fontFamily: 'inherit',
};

const sendButtonStyle: React.CSSProperties = {
  padding: '8px 16px',
  backgroundColor: '#3b82f6',
  color: 'white',
  border: 'none',
  borderRadius: '6px',
  cursor: 'pointer',
  fontSize: '14px',
  fontWeight: '600',
  whiteSpace: 'nowrap',
};

const clearButtonStyle: React.CSSProperties = {
  padding: '6px 12px',
  backgroundColor: '#ef4444',
  color: 'white',
  border: 'none',
  borderRadius: '4px',
  cursor: 'pointer',
  fontSize: '12px',
  fontWeight: '600',
};

const closeButtonStyle: React.CSSProperties = {
  padding: '4px 8px',
  backgroundColor: 'transparent',
  border: 'none',
  fontSize: '20px',
  cursor: 'pointer',
  color: '#6b7280',
};

export default ChatInterface;
