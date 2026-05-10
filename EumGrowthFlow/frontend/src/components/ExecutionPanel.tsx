//
// ExecutionPanel — 워크플로우 실행 결과를 표시하는 하단 패널
//
// Java 비교: JPanel 기반의 Log Viewer / Console Output 패널.
//           SwingWorker 로 백그라운드 작업 실행 후 결과를 JTable / JTextArea 에 표시.
//

interface ExecutionResult {
  nodeId: string;
  success: boolean;
  output?: any;
  error?: string;
}

//
// Props: 부모가 전달하는 "읽기 전용 데이터" + 콜백
// Java: public ExecutionPanel(List<ExecutionResult> results, boolean isRunning, Runnable onClose) { ... }
//
interface ExecutionPanelProps {
  results: ExecutionResult[];
  isRunning: boolean;
  onClose: () => void;
}

const ExecutionPanel = ({ results, isRunning, onClose }: ExecutionPanelProps) => {
  return (
    <div
      style={{
        position: 'fixed',
        bottom: 0,
        left: 250,
        right: 0,
        height: '300px',
        backgroundColor: '#1a192b',
        color: '#fff',
        padding: '20px',
        overflowY: 'auto',
        zIndex: 10,
        borderTop: '2px solid #3b82f6',
      }}
    >
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
        <h3 style={{ fontSize: '16px', fontWeight: 'bold' }}>
          {isRunning ? '🔄 실행 중...' : '✅ 실행 결과'}
        </h3>
        <button onClick={onClose} style={closeButtonStyle}>✕</button>
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
        {/*
          results.map(result => <div key={index}>...</div>)
          Java: for (int i = 0; i < results.size(); i++) {
                    ExecutionResult result = results.get(i);
                    add(renderResultRow(result, i));
                }
          key={index} → index 를 key 로 사용 (순서가 바뀌지 않는 리스트에서는 허용)
        */}
        {results.map((result, index) => (
          <div
            key={index}
            style={{
              padding: '12px',
              backgroundColor: result.success ? '#065f46' : '#7f1d1d',
              borderRadius: '6px',
              border: `1px solid ${result.success ? '#10b981' : '#ef4444'}`,
            }}
          >
            <div style={{ fontSize: '14px', fontWeight: '600', marginBottom: '8px' }}>
              노드 ID: {result.nodeId}
            </div>
            {result.success ? (
              <div>
                <div style={{ fontSize: '12px', color: '#d1fae5', marginBottom: '8px' }}>
                  ✓ 성공
                </div>

                {/* Response 필드만 강조 표시 */}
                {result.output?.response && (
                  <div style={{
                    marginBottom: '12px',
                    padding: '12px',
                    backgroundColor: '#f0fdf4',
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
                      {result.output.response}
                    </div>
                  </div>
                )}

                {/* <details>: HTML5 접기/펼치기 요소 — JS 없이 토글 가능 */}
                <details style={{ marginTop: '8px' }}>
                  <summary style={{
                    fontSize: '12px',
                    color: '#d1fae5',
                    cursor: 'pointer',
                    userSelect: 'none',
                  }}>
                    📋 전체 응답 보기
                  </summary>
                  <pre
                    style={{
                      fontSize: '11px',
                      backgroundColor: '#0f172a',
                      padding: '8px',
                      borderRadius: '4px',
                      overflow: 'auto',
                      maxHeight: '150px',
                      marginTop: '8px',
                      whiteSpace: 'pre-wrap',
                      wordWrap: 'break-word',
                    }}
                  >
                    {JSON.stringify(result.output, null, 2)}
                  </pre>
                </details>
              </div>
            ) : (
              <div>
                <div style={{ fontSize: '12px', color: '#fecaca', marginBottom: '4px' }}>
                  ✗ 실패
                </div>
                <div style={{ fontSize: '12px', color: '#fecaca' }}>{result.error}</div>
              </div>
            )}
          </div>
        ))}

        {/* 조건부 렌더링: 결과가 없고 실행 중도 아닐 때 빈 상태 표시 */}
        {results.length === 0 && !isRunning && (
          <div style={{ textAlign: 'center', color: '#9ca3af', padding: '40px' }}>
            실행 결과가 없습니다.
          </div>
        )}
      </div>
    </div>
  );
};

const closeButtonStyle = {
  padding: '4px 8px',
  backgroundColor: 'transparent',
  border: 'none',
  fontSize: '20px',
  cursor: 'pointer',
  color: '#9ca3af',
};

export default ExecutionPanel;
