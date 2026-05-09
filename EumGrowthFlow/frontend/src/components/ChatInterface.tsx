/**
 * LLM 채팅 인터페이스
 *
 * 선택된 LLM 노드와 대화할 수 있는 우측 채팅 패널.
 * 백엔드 /llm API 를 통해 대화를 처리하며, 이전 대화 이력을 프롬프트에 포함한다.
 */

import { useState, useRef, useEffect } from 'react';
import ReactMarkdown from 'react-markdown';
import { callLLM, hasBearerToken } from '../services/llmClient';
import type { ChatMessage } from '../services/llmClient';
import './ChatInterface.css';

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
  const [messages, setMessages] = useState<Message[]>([]);
  const [input, setInput] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [historyLimit, setHistoryLimit] = useState(maxHistory);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

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

    setMessages((prev) => [...prev, userMessage]);
    setInput('');
    setIsLoading(true);

    try {
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

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      sendMessage();
    }
  };

  const clearChat = () => {
    setMessages([]);
  };

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

      {/* 히스토리 설정 */}
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

      {/* 메시지 영역 */}
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
