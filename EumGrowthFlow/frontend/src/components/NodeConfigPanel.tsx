import { useState, useEffect } from 'react';

interface NodeConfig {
  id: string;
  label: string;
  agentType: string;
  prompt?: string;
  apiUrl?: string;
  apiMethod?: string;
  apiKeyName?: string;
  model?: string;
}

interface NodeConfigPanelProps {
  selectedNode: NodeConfig | null;
  onClose: () => void;
  onSave: (config: NodeConfig) => void;
  onOpenChat?: () => void;
}

const NodeConfigPanel = ({ selectedNode, onClose, onSave, onOpenChat }: NodeConfigPanelProps) => {
  const [config, setConfig] = useState<NodeConfig | null>(selectedNode);
  const [apiKey, setApiKey] = useState('');
  const [testResult, setTestResult] = useState<any>(null);
  const [isTesting, setIsTesting] = useState(false);

  useEffect(() => {
    setConfig(selectedNode);
    setTestResult(null);
    if (selectedNode?.apiKeyName) {
      const storedKey = localStorage.getItem(`apikey_${selectedNode.apiKeyName}`);
      setApiKey(storedKey || '');
    }
  }, [selectedNode]);

  if (!config) return null;

  const handleSave = () => {
    if (config.apiKeyName && apiKey) {
      localStorage.setItem(`apikey_${config.apiKeyName}`, apiKey);
    }
    onSave(config);
    onClose();
  };

  const handleTest = async () => {
    if (!config.apiUrl || !config.prompt) {
      setTestResult({ success: false, error: 'API URL과 프롬프트를 입력하세요.' });
      return;
    }

    setIsTesting(true);
    setTestResult(null);

    try {
      const headers: HeadersInit = {
        'Content-Type': 'application/json',
      };

      if (config.apiKeyName && apiKey) {
        headers['Authorization'] = `Bearer ${apiKey}`;
      }

      const method = config.apiMethod || 'POST';

      // Ollama 형식 요청 바디
      const requestBody = {
        // model: config.model || 'llama2',
        model: config.model || 'qwen3:8b',
        prompt: config.prompt,
        stream: false,
      };

      const response = await fetch(config.apiUrl, {
        method,
        headers,
        body: method !== 'GET' ? JSON.stringify(requestBody) : undefined,
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`API 호출 실패 (${response.status}): ${errorText}`);
      }

      const data = await response.json();
      setTestResult({ success: true, data });
    } catch (error: any) {
      setTestResult({ success: false, error: error.message });
    } finally {
      setIsTesting(false);
    }
  };

  const handleSetOllamaDefaults = () => {
    setConfig({
      ...config,
      apiUrl: 'http://localhost:11434/api/generate',
      apiMethod: 'POST',
      prompt: config.prompt || 'Hello, how are you?',
      model: config.model || 'qwen3:8b',
    });
  };

  const isLLMNode = config.agentType === 'llm';

  return (
    <div
      style={{
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
      }}
    >
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <h2 style={{ fontSize: '18px', fontWeight: 'bold', color: '#1a192b' }}>노드 설정</h2>
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
          style={{ ...inputStyle, backgroundColor: '#f3f4f6', cursor: 'not-allowed' }}
        />
      </div>

      {isLLMNode && (
        <>
          <div style={{ marginBottom: '16px', padding: '12px', backgroundColor: '#eff6ff', borderRadius: '6px' }}>
            <div style={{ fontSize: '14px', fontWeight: '600', color: '#1e40af', marginBottom: '8px' }}>
              🦙 Ollama 빠른 설정
            </div>
            <button onClick={handleSetOllamaDefaults} style={quickSetupButtonStyle}>
              Ollama 기본값 설정
            </button>
            <p style={{ fontSize: '12px', color: '#6b7280', marginTop: '8px' }}>
              로컬 Ollama 서버 (localhost:11434) 기본 설정
            </p>
          </div>

          <div style={{ marginBottom: '16px' }}>
            <label style={labelStyle}>프롬프트</label>
            <textarea
              value={config.prompt || ''}
              onChange={(e) => setConfig({ ...config, prompt: e.target.value })}
              placeholder="LLM에 전달할 프롬프트를 입력하세요..."
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
              placeholder="llama2, mistral, codellama..."
              style={inputStyle}
            />
            <p style={{ fontSize: '12px', color: '#6b7280', marginTop: '4px' }}>
              💡 Ollama 모델: llama2, mistral, codellama, phi 등
            </p>
          </div>

          <div style={{ marginBottom: '16px' }}>
            <label style={labelStyle}>API URL</label>
            <input
              type="text"
              value={config.apiUrl || ''}
              onChange={(e) => setConfig({ ...config, apiUrl: e.target.value })}
              placeholder="http://localhost:11434/api/generate"
              style={inputStyle}
            />
          </div>

          <div style={{ marginBottom: '16px' }}>
            <label style={labelStyle}>HTTP Method</label>
            <select
              value={config.apiMethod || 'POST'}
              onChange={(e) => setConfig({ ...config, apiMethod: e.target.value })}
              style={inputStyle}
            >
              <option value="GET">GET</option>
              <option value="POST">POST</option>
              <option value="PUT">PUT</option>
              <option value="PATCH">PATCH</option>
            </select>
          </div>

          <div style={{ marginBottom: '16px' }}>
            <label style={labelStyle}>API Key 이름</label>
            <input
              type="text"
              value={config.apiKeyName || ''}
              onChange={(e) => setConfig({ ...config, apiKeyName: e.target.value })}
              placeholder="예: openai_key"
              style={inputStyle}
            />
          </div>

          {config.apiKeyName && (
            <div style={{ marginBottom: '16px' }}>
              <label style={labelStyle}>API Key (암호화 저장)</label>
              <input
                type="password"
                value={apiKey}
                onChange={(e) => setApiKey(e.target.value)}
                placeholder="API Key를 입력하세요"
                style={inputStyle}
              />
              <p style={{ fontSize: '12px', color: '#6b7280', marginTop: '4px' }}>
                🔒 브라우저 로컬 스토리지에 저장됩니다
              </p>
            </div>
          )}

          {/* 테스트 및 채팅 버튼 */}
          <div style={{ marginTop: '16px', marginBottom: '16px', display: 'flex', gap: '8px' }}>
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
            <div
              style={{
                marginTop: '16px',
                padding: '12px',
                backgroundColor: testResult.success ? '#f0fdf4' : '#fef2f2',
                border: `1px solid ${testResult.success ? '#86efac' : '#fca5a5'}`,
                borderRadius: '6px',
              }}
            >
              <div style={{
                fontSize: '14px',
                fontWeight: '600',
                color: testResult.success ? '#15803d' : '#dc2626',
                marginBottom: '8px'
              }}>
                {testResult.success ? '✅ 테스트 성공' : '❌ 테스트 실패'}
              </div>
              {testResult.success ? (
                <>
                  {/* Response 필드만 강조 표시 */}
                  {testResult.data.response && (
                    <div style={{
                      marginBottom: '12px',
                      padding: '12px',
                      backgroundColor: '#fff',
                      border: '2px solid #10b981',
                      borderRadius: '6px',
                    }}>
                      <div style={{
                        fontSize: '13px',
                        fontWeight: '600',
                        color: '#059669',
                        marginBottom: '8px',
                      }}>
                        💬 응답 (Response)
                      </div>
                      <div style={{
                        fontSize: '13px',
                        color: '#1f2937',
                        lineHeight: '1.6',
                        whiteSpace: 'pre-wrap',
                        wordWrap: 'break-word',
                      }}>
                        {testResult.data.response}
                      </div>
                    </div>
                  )}

                  {/* 전체 응답 (접을 수 있게) */}
                  <details style={{ marginTop: '8px' }}>
                    <summary style={{
                      fontSize: '12px',
                      color: '#6b7280',
                      cursor: 'pointer',
                      userSelect: 'none',
                    }}>
                      📋 전체 응답 보기
                    </summary>
                    <pre style={{
                      fontSize: '11px',
                      backgroundColor: '#f9fafb',
                      padding: '8px',
                      borderRadius: '4px',
                      overflow: 'auto',
                      maxHeight: '200px',
                      marginTop: '8px',
                      whiteSpace: 'pre-wrap',
                      wordWrap: 'break-word',
                    }}>
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
    </div>
  );
};

const labelStyle = {
  display: 'block',
  fontSize: '14px',
  fontWeight: '600',
  color: '#374151',
  marginBottom: '6px',
};

const inputStyle = {
  width: '100%',
  padding: '8px 12px',
  fontSize: '14px',
  border: '1px solid #d1d5db',
  borderRadius: '6px',
  outline: 'none',
  transition: 'border-color 0.2s',
};

const textareaStyle = {
  ...inputStyle,
  resize: 'vertical' as const,
  fontFamily: 'monospace',
};

const saveButtonStyle = {
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

const cancelButtonStyle = {
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

const closeButtonStyle = {
  padding: '4px 8px',
  backgroundColor: 'transparent',
  border: 'none',
  fontSize: '20px',
  cursor: 'pointer',
  color: '#6b7280',
};

const testButtonStyle = {
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

const chatButtonStyle = {
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

const quickSetupButtonStyle = {
  width: '100%',
  padding: '8px 12px',
  backgroundColor: '#3b82f6',
  color: 'white',
  border: 'none',
  borderRadius: '4px',
  cursor: 'pointer',
  fontSize: '13px',
  fontWeight: '600',
};

export default NodeConfigPanel;
