//
// NodeConfigPanel — 노드 설정 편집 슬라이드 패널
//
// Java 비교: JDialog / JOptionPane 기반의 설정 대화상자 (Modal Dialog).
//           또는 Eclipse/IntelliJ 의 Properties View (Dockable Panel).
//
// React 패턴:
//   - "제어 컴포넌트(Controlled Component)": input 의 value 를 React state 로 제어
//     Java: DocumentListener 로 JTextField 변경 감지 → Model 업데이트
//   - 콜백 Props (onSave, onDelete): 부모(App)에게 액션 위임
//     Java: ActionListener 콜백 / Observer 패턴
//   - 조건부 렌더링은 부모에서 처리 ({selectedNode && <NodeConfigPanel/>})
//     → Panel 자체는 항상 전체 DOM 을 그리고, 부모가 표시 여부를 결정
//

import { useState, useEffect } from 'react';
import { callLLM } from '../services/llmClient';

//
// 노드 설정 데이터 구조체
// Java: record NodeConfig(String id, String label, String agentType, ...) { }
//
interface NodeConfig {
  id: string;
  label: string;
  agentType: string;
  prompt?: string;
  model?: string;
}

//
// Props 인터페이스 — 콜백 기반 이벤트 처리
// Java: ActionListener / Consumer<T> 콜백 인터페이스
//   onClose: () => void        = Runnable
//   onSave:  (config) => void  = Consumer<NodeConfig>
//   onDelete: () => void       = Runnable (optional)
//
interface NodeConfigPanelProps {
  selectedNode: NodeConfig | null;
  onClose: () => void;
  onSave: (config: NodeConfig) => void;
  onDelete?: () => void;
  onOpenChat?: () => void;
}

const NodeConfigPanel = ({
  selectedNode,
  onClose,
  onSave,
  onDelete,
  onOpenChat,
}: NodeConfigPanelProps) => {
  //
  // useState: React 의 지역 상태 관리
  // Java: private NodeConfig config;
  //       private TestResult testResult;
  //       private boolean isTesting;
  //
  // setConfig → config 상태 업데이트 + 리렌더링 예약
  // setConfig({ ...config, label: "새 이름" }) → "불변 업데이트" (스프레드로 복사)
  //
  const [config, setConfig] = useState<NodeConfig | null>(selectedNode);
  const [testResult, setTestResult] = useState<any>(null);
  const [isTesting, setIsTesting] = useState(false);

  //
  // useEffect: 부모가 선택한 노드가 바뀌면 config 를 새로 설정한다.
  // Java 비교: @PostConstruct + PropertyChangeListener 를 합친 개념.
  //
  // useEffect(() => { ... }, [selectedNode]);
  //   → selectedNode 가 변경될 때만 이펙트 실행 (의존성 배열)
  //   → 의존성 배열이 [] 이면 "마운트 시 1회만 실행" (Java: @PostConstruct)
  //   → 의존성 배열 생략 시 "매 렌더링마다 실행" (비추천)
  //
  // 클린업 함수(return)는 여기서 필요 없지만, 보통 이벤트 리스너 해제에 사용.
  // Java: @PreDestroy / removePropertyChangeListener()
  //
  useEffect(() => {
    setConfig(selectedNode);
    setTestResult(null);
  }, [selectedNode]);

  //
  // config 가 null 이면 아무것도 렌더링하지 않는다 (부모에서 조건부로 보여주지만 방어적 처리)
  // Java: if (config == null) return; (early return)
  //
  if (!config) return null;

  const handleSave = () => {
    onSave(config);
    onClose();
  };

  //
  // LLM API 테스트 (비동기)
  // Java: CompletableFuture.supplyAsync(() -> llmClient.call(messages, model))
  //          .thenAccept(data -> updateUI(data))
  //          .exceptionally(e -> showError(e));
  //
  const handleTest = async () => {
    if (!config.prompt) {
      setTestResult({
        success: false,
        error: '프롬프트를 입력하세요.',
      });
      return;
    }

    setIsTesting(true);
    setTestResult(null);

    try {
      const data = await callLLM([{ role: 'user', content: config.prompt }], config.model || undefined);
      setTestResult({ success: true, data });
    } catch (error: any) {
      setTestResult({ success: false, error: error.message });
    } finally {
      setIsTesting(false);
    }
  };

  const isLLMNode = config.agentType === 'llm';

  return (
    <div style={panelStyle}>
      <div style={headerStyle}>
        <h2 style={{ fontSize: '18px', fontWeight: 'bold', color: '#1a192b' }}>
          노드 설정
        </h2>
        <button onClick={onClose} style={closeButtonStyle}>✕</button>
      </div>

      {/*
        input 의 value={config.label} + onChange={...setConfig(...)}
        → "제어 컴포넌트(Controlled Component)" 패턴
        Java: JTextField tf = new JTextField(config.getLabel());
              tf.getDocument().addDocumentListener(doc -> {
                  config.setLabel(tf.getText());
              });

        React 의 제어 컴포넌트는 단방향 데이터 흐름을 강제한다:
          state → value={state} → 사용자 입력 → onChange → setState → 리렌더링
      */}
      <div style={{ marginBottom: '16px' }}>
        <label style={labelStyle}>노드 이름</label>
        <input
          type="text"
          value={config.label}
          onChange={(e) => setConfig({ ...config, label: e.target.value })}
          style={inputStyle}
        />
      </div>

      <div style={{ marginBottom: '16px' }}>
        <label style={labelStyle}>노드 타입</label>
        <input
          type="text"
          value={config.agentType}
          disabled
          style={{
            ...inputStyle,
            backgroundColor: '#f3f4f6',
            cursor: 'not-allowed',
          }}
        />
      </div>

      {/* LLM 노드일 때만 추가 설정 필드 표시 */}
      {isLLMNode && (
        <>
          <div style={backendInfoBoxStyle}>
            <div style={{ fontSize: '13px', fontWeight: '600', color: '#1e40af', marginBottom: '4px' }}>
              🔗 백엔드 프록시 모드
            </div>
            <div style={{ fontSize: '12px', color: '#6b7280' }}>
              API 호출은 백엔드 서버를 통해 처리됩니다.
              Provider 선택(Ollama/DeepSeek/OpenAI)은 서버 설정에서 관리됩니다.
            </div>
          </div>

          <div style={{ marginBottom: '16px' }}>
            <label style={labelStyle}>프롬프트</label>
            <textarea
              value={config.prompt || ''}
              onChange={(e) => setConfig({ ...config, prompt: e.target.value })}
              placeholder="LLM 에 전달할 프롬프트를 입력하세요..."
              rows={6}
              style={textareaStyle}
            />
          </div>

          <div style={{ marginBottom: '16px' }}>
            <label style={labelStyle}>모델 이름</label>
            <input
              type="text"
              value={config.model || ''}
              onChange={(e) => setConfig({ ...config, model: e.target.value })}
              placeholder="미지정 시 서버 기본 모델 사용"
              style={inputStyle}
            />
            <p style={{ fontSize: '12px', color: '#6b7280', marginTop: '4px' }}>
              💡 Ollama: deepseek-r1:1.5b / DeepSeek: deepseek-v4-flash / OpenAI: gpt-4.1-mini
            </p>
          </div>

          <div style={buttonRowStyle}>
            <button
              onClick={handleTest}
              disabled={isTesting}
              style={{ ...testButtonStyle, flex: 1 }}
            >
              {isTesting ? '🔄 테스트 중...' : '🧪 API 테스트'}
            </button>
            {onOpenChat && (
              <button
                onClick={onOpenChat}
                style={{ ...chatButtonStyle, flex: 1 }}
              >
                💬 채팅 모드
              </button>
            )}
          </div>

          {/* 테스트 결과 조건부 렌더링 */}
          {testResult && (
            <div style={testResultContainerStyle(testResult.success)}>
              <div style={testResultHeaderStyle(testResult.success)}>
                {testResult.success ? '✅ 테스트 성공' : '❌ 테스트 실패'}
              </div>
              {testResult.success ? (
                <>
                  {testResult.data.response && (
                    <div style={responseBoxStyle}>
                      <div style={responseLabelStyle}>
                        💬 응답 (Response) — 모델: {testResult.data.model}
                      </div>
                      <div style={responseContentStyle}>
                        {testResult.data.response}
                      </div>
                    </div>
                  )}
                  <details style={{ marginTop: '8px' }}>
                    <summary style={detailSummaryStyle}>
                      📋 전체 응답 보기
                    </summary>
                    <pre style={detailPreStyle}>
                      {JSON.stringify(testResult.data, null, 2)}
                    </pre>
                  </details>
                </>
              ) : (
                <div style={{ fontSize: '12px', color: '#dc2626' }}>
                  {testResult.error}
                </div>
              )}
            </div>
          )}
        </>
      )}

      <div style={{ display: 'flex', gap: '10px', marginTop: '24px' }}>
        <button onClick={handleSave} style={saveButtonStyle}>저장</button>
        <button onClick={onClose} style={cancelButtonStyle}>취소</button>
      </div>

      {/*
        onDelete 콜백이 있을 때만 삭제 버튼 영역 표시
        Java: if (onDelete != null) { add(deleteButton); }
      */}
      {onDelete && (
        <div style={{ marginTop: '16px', paddingTop: '16px', borderTop: '1px solid #e5e7eb' }}>
          <button onClick={onDelete} style={deleteButtonStyle}>
            🗑️ 노드 삭제
          </button>
        </div>
      )}
    </div>
  );
};

//
// ── 스타일 ──
// React.CSSProperties: CSS 속성을 TypeScript 타입으로 정의한 것.
// Java: StyleConstants 클래스에 상수로 정의하는 것과 유사.
//

const panelStyle: React.CSSProperties = {
  position: 'fixed',
  right: 0,
  top: 0,
  width: '350px',
  height: '100vh',
  backgroundColor: '#fff',
  boxShadow: '-2px 0 8px rgba(0, 0, 0, 0.1)',
  padding: '20px',
  overflowY: 'auto',
  zIndex: 10,
};

const headerStyle: React.CSSProperties = {
  display: 'flex',
  justifyContent: 'space-between',
  alignItems: 'center',
  marginBottom: '20px',
};

const labelStyle: React.CSSProperties = {
  display: 'block',
  fontSize: '14px',
  fontWeight: '600',
  color: '#374151',
  marginBottom: '6px',
};

const inputStyle: React.CSSProperties = {
  width: '100%',
  padding: '8px 12px',
  fontSize: '14px',
  border: '1px solid #d1d5db',
  borderRadius: '6px',
  outline: 'none',
};

const textareaStyle: React.CSSProperties = {
  ...inputStyle,
  resize: 'vertical' as const,
  fontFamily: 'monospace',
};

const backendInfoBoxStyle: React.CSSProperties = {
  marginBottom: '16px',
  padding: '12px',
  backgroundColor: '#eff6ff',
  borderRadius: '6px',
  border: '1px solid #dbeafe',
};

const buttonRowStyle: React.CSSProperties = {
  marginTop: '16px',
  marginBottom: '16px',
  display: 'flex',
  gap: '8px',
};

//
// 동적 스타일 함수 — 파라미터에 따라 다른 스타일 객체를 반환
// Java: Color getResultColor(boolean success) {
//           return success ? new Color(0xf0, 0xfd, 0xf4) : new Color(0xfe, 0xf2, 0xf2);
//       }
//
const testResultContainerStyle = (success: boolean): React.CSSProperties => ({
  marginTop: '16px',
  padding: '12px',
  backgroundColor: success ? '#f0fdf4' : '#fef2f2',
  border: `1px solid ${success ? '#86efac' : '#fca5a5'}`,
  borderRadius: '6px',
});

const testResultHeaderStyle = (success: boolean): React.CSSProperties => ({
  fontSize: '14px',
  fontWeight: '600',
  color: success ? '#15803d' : '#dc2626',
  marginBottom: '8px',
});

const responseBoxStyle: React.CSSProperties = {
  marginBottom: '12px',
  padding: '12px',
  backgroundColor: '#fff',
  border: '2px solid #10b981',
  borderRadius: '6px',
};

const responseLabelStyle: React.CSSProperties = {
  fontSize: '13px',
  fontWeight: '600',
  color: '#059669',
  marginBottom: '8px',
};

const responseContentStyle: React.CSSProperties = {
  fontSize: '13px',
  color: '#1f2937',
  lineHeight: '1.6',
  whiteSpace: 'pre-wrap',
  wordWrap: 'break-word',
};

const detailSummaryStyle: React.CSSProperties = {
  fontSize: '12px',
  color: '#6b7280',
  cursor: 'pointer',
  userSelect: 'none',
};

const detailPreStyle: React.CSSProperties = {
  fontSize: '11px',
  backgroundColor: '#f9fafb',
  padding: '8px',
  borderRadius: '4px',
  overflow: 'auto',
  maxHeight: '200px',
  marginTop: '8px',
  whiteSpace: 'pre-wrap',
  wordWrap: 'break-word',
};

const saveButtonStyle: React.CSSProperties = {
  flex: 1,
  padding: '10px 16px',
  backgroundColor: '#3b82f6',
  color: 'white',
  border: 'none',
  borderRadius: '6px',
  cursor: 'pointer',
  fontSize: '14px',
  fontWeight: '600',
};

const cancelButtonStyle: React.CSSProperties = {
  flex: 1,
  padding: '10px 16px',
  backgroundColor: '#6b7280',
  color: 'white',
  border: 'none',
  borderRadius: '6px',
  cursor: 'pointer',
  fontSize: '14px',
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

const testButtonStyle: React.CSSProperties = {
  width: '100%',
  padding: '10px 16px',
  backgroundColor: '#f59e0b',
  color: 'white',
  border: 'none',
  borderRadius: '6px',
  cursor: 'pointer',
  fontSize: '14px',
  fontWeight: '600',
};

const chatButtonStyle: React.CSSProperties = {
  width: '100%',
  padding: '10px 16px',
  backgroundColor: '#10b981',
  color: 'white',
  border: 'none',
  borderRadius: '6px',
  cursor: 'pointer',
  fontSize: '14px',
  fontWeight: '600',
};

const deleteButtonStyle: React.CSSProperties = {
  width: '100%',
  padding: '10px 16px',
  backgroundColor: '#ef4444',
  color: 'white',
  border: 'none',
  borderRadius: '6px',
  cursor: 'pointer',
  fontSize: '14px',
  fontWeight: '600',
};

export default NodeConfigPanel;
