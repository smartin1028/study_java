/**
 * 노드 설정 패널
 *
 * 선택된 워크플로우 노드의 설정을 편집하는 우측 슬라이드 패널.
 * LLM 노드의 경우 프롬프트, 모델명을 설정하고 백엔드 API 테스트를 수행할 수 있다.
 * API URL, API Key 등은 백엔드에서 관리하므로 더 이상 노드별로 설정하지 않는다.
 */

import { useState, useEffect } from 'react';
import { callLLM } from '../services/llmClient';

interface NodeConfig {
  id: string;
  label: string;
  agentType: string;
  prompt?: string;
  model?: string;
}

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
  const [config, setConfig] = useState<NodeConfig | null>(selectedNode);
  const [testResult, setTestResult] = useState<any>(null);
  const [isTesting, setIsTesting] = useState(false);

  useEffect(() => {
    setConfig(selectedNode);
    setTestResult(null);
  }, [selectedNode]);

  if (!config) return null;

  const handleSave = () => {
    onSave(config);
    onClose();
  };

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

          {/* 테스트 및 채팅 버튼 */}
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

          {/* 테스트 결과 */}
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
