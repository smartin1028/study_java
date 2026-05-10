//
// BackendTokenModal — 백엔드 API Bearer 토큰 설정 모달
//
// Java 비교: 설정 대화상자 (JDialog + Properties 설정).
//           SettingsDialog dialog = new SettingsDialog("API 토큰 설정");
//           dialog.addField("Bearer 토큰", new JPasswordField());
//           dialog.onSave(token -> Preferences.put("token", token));
//
// localStorage: 브라우저의 key-value 저장소 (세션 간 유지)
// Java 비교: java.util.prefs.Preferences (운영체제별 저장소)
//           또는 Properties 파일 (.env 파일과 유사)
//

import { useState } from 'react';
import { getBearerToken } from '../services/llmClient';

interface BackendTokenModalProps {
  isOpen: boolean;
  onConfirm: (token: string) => void;
  onCancel: () => void;
  onClear: () => void;
  hasExistingToken: boolean;
}

const BackendTokenModal = ({
  isOpen,
  onConfirm,
  onCancel,
  onClear,
  hasExistingToken,
}: BackendTokenModalProps) => {
  const [token, setToken] = useState('');
  const [error, setError] = useState('');

  //
  // getBearerToken(): localStorage 에서 현재 저장된 토큰을 읽는다.
  // Java: String existingToken = Preferences.userRoot().get("bearer_token", null);
  //
  const existingToken = getBearerToken();

  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (!token.trim()) {
      setError('Bearer 토큰을 입력해주세요.');
      return;
    }

    onConfirm(token.trim());
    setToken('');
    setError('');
  };

  const handleCancel = () => {
    setToken('');
    setError('');
    onCancel();
  };

  const handleClear = () => {
    setToken('');
    setError('');
    onClear();
  };

  return (
    <div style={overlayStyle} onClick={handleCancel}>
      {/* e.stopPropagation() → 내부 클릭 시 모달이 닫히지 않도록 */}
      <div style={modalStyle} onClick={(e) => e.stopPropagation()}>
        <div style={headerStyle}>
          <h2 style={{ fontSize: '18px', fontWeight: 'bold' }}>
            🔑 백엔드 API 토큰 설정
          </h2>
          <button onClick={handleCancel} style={headerCloseButtonStyle}>✕</button>
        </div>

        <form onSubmit={handleSubmit} style={formStyle}>
          <div style={infoBoxStyle}>
            <div style={{ fontSize: '14px', marginBottom: '8px' }}>
              백엔드 서버의 <code style={codeStyle}>LLM_BEARER_TOKEN</code> 값을 입력하세요.
            </div>
            <div style={{ fontSize: '12px', color: '#6b7280' }}>
              💡 이 토큰은 브라우저 로컬 스토리지에 저장되며, 백엔드 API 호출 시 자동으로 포함됩니다.
            </div>
          </div>

          {/* 기존 토큰이 있으면 미리보기 표시 */}
          {hasExistingToken && (
            <div style={existingTokenBoxStyle}>
              <div style={{ fontSize: '13px', fontWeight: '600', color: '#059669', marginBottom: '4px' }}>
                ✅ 토큰이 설정되어 있습니다
              </div>
              <div style={{ fontSize: '12px', color: '#6b7280', wordBreak: 'break-all' }}>
                {existingToken && existingToken.slice(0, 8)}... (설정된 토큰)
              </div>
            </div>
          )}

          <div style={fieldStyle}>
            <label style={labelStyle}>Bearer 토큰</label>
            <input
              type="password"       // 입력 내용 마스킹 (Java: JPasswordField)
              value={token}
              onChange={(e) => setToken(e.target.value)}
              placeholder="백엔드 LLM_BEARER_TOKEN 값을 입력하세요"
              style={inputStyle}
              autoFocus
            />
          </div>

          {error && (
            <div style={errorStyle}>
              ⚠️ {error}
            </div>
          )}

          <div style={buttonGroupStyle}>
            {/* 초기화 버튼: 기존 토큰이 있을 때만 표시 */}
            {hasExistingToken && (
              <button type="button" onClick={handleClear} style={clearButtonStyle}>
                🗑️ 초기화
              </button>
            )}
            <button type="button" onClick={handleCancel} style={cancelButtonStyle}>
              취소
            </button>
            <button type="submit" style={confirmButtonStyle}>
              💾 저장
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

const overlayStyle: React.CSSProperties = {
  position: 'fixed',
  top: 0, left: 0, right: 0, bottom: 0,
  backgroundColor: 'rgba(0, 0, 0, 0.5)',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  zIndex: 1000,
};

const modalStyle: React.CSSProperties = {
  backgroundColor: 'white',
  borderRadius: '12px',
  width: '90%',
  maxWidth: '450px',
  boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04)',
};

const headerStyle: React.CSSProperties = {
  padding: '20px',
  borderBottom: '1px solid #e5e7eb',
  display: 'flex',
  justifyContent: 'space-between',
  alignItems: 'center',
};

const headerCloseButtonStyle: React.CSSProperties = {
  background: 'transparent',
  border: 'none',
  fontSize: '24px',
  cursor: 'pointer',
  color: '#6b7280',
  padding: 0,
};

const formStyle: React.CSSProperties = {
  padding: '20px',
};

const infoBoxStyle: React.CSSProperties = {
  backgroundColor: '#eff6ff',
  padding: '12px',
  borderRadius: '6px',
  marginBottom: '16px',
  border: '1px solid #dbeafe',
};

const codeStyle: React.CSSProperties = {
  backgroundColor: '#e5e7eb',
  padding: '2px 6px',
  borderRadius: '4px',
  fontSize: '13px',
  fontFamily: 'monospace',
};

const existingTokenBoxStyle: React.CSSProperties = {
  backgroundColor: '#f0fdf4',
  padding: '12px',
  borderRadius: '6px',
  marginBottom: '16px',
  border: '1px solid #86efac',
};

const fieldStyle: React.CSSProperties = {
  marginBottom: '16px',
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
  padding: '10px 12px',
  fontSize: '14px',
  border: '1px solid #d1d5db',
  borderRadius: '6px',
  outline: 'none',
};

const errorStyle: React.CSSProperties = {
  backgroundColor: '#fef2f2',
  color: '#dc2626',
  padding: '10px',
  borderRadius: '6px',
  fontSize: '14px',
  marginBottom: '16px',
  border: '1px solid #fecaca',
};

const buttonGroupStyle: React.CSSProperties = {
  display: 'flex',
  gap: '10px',
  marginTop: '20px',
};

const clearButtonStyle: React.CSSProperties = {
  flex: 1,
  padding: '10px 16px',
  backgroundColor: '#ef4444',
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

const confirmButtonStyle: React.CSSProperties = {
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

export default BackendTokenModal;
