//
// PasswordModal — 암호화 저장/불러오기용 비밀번호 입력 모달
//
// Java 비교: JDialog / JOptionPane.showInputDialog() 로 구현하는 모달 대화상자.
//           ModalDialog dialog = new ModalDialog(parent, "암호화 저장");
//           dialog.addField("비밀번호", new JPasswordField());
//           dialog.addButton("확인", () -> onConfirm(dialog.getPassword()));
//

import { useState } from 'react';

//
// Props: 모달을 제어하는 데이터와 콜백
// Java: public PasswordModal(Frame owner, boolean isOpen, String mode,
//                            Consumer<String> onConfirm, Runnable onCancel) { ... }
//
interface PasswordModalProps {
  isOpen: boolean;
  mode: 'save' | 'load';
  onConfirm: (password: string) => void;
  onCancel: () => void;
}

const PasswordModal = ({ isOpen, mode, onConfirm, onCancel }: PasswordModalProps) => {
  // 로컬 상태: 폼 필드와 에러 메시지
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');

  //
  // 모달이 닫혀있으면 아무것도 렌더링하지 않는다.
  // Java: if (!isOpen) { dialog.setVisible(false); return; }
  //
  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();   // 폼 기본 제출 동작(페이지 새로고침) 방지
    setError('');

    if (!password) {
      setError('비밀번호를 입력해주세요.');
      return;
    }

    if (mode === 'save' && password !== confirmPassword) {
      setError('비밀번호가 일치하지 않습니다.');
      return;
    }

    if (password.length < 4) {
      setError('비밀번호는 최소 4자 이상이어야 합니다.');
      return;
    }

    onConfirm(password);
    // 확인 후 폼 초기화
    setPassword('');
    setConfirmPassword('');
    setError('');
  };

  const handleCancel = () => {
    setPassword('');
    setConfirmPassword('');
    setError('');
    onCancel();
  };

  return (
    // overlayStyle: 반투명 배경 → 클릭 시 취소 (모달 바깥 클릭 = 취소)
    // Java: JDialog.setModal(true); dialog.setBackground(new Color(0,0,0,128));
    <div style={overlayStyle} onClick={handleCancel}>
      {/*
        e.stopPropagation(): 모달 내부 클릭이 오버레이까지 전파되지 않도록 방지
        Java: MouseEvent.consume() — 이벤트 소비
      */}
      <div style={modalStyle} onClick={(e) => e.stopPropagation()}>
        <div style={headerStyle}>
          <h2 style={{ fontSize: '18px', fontWeight: 'bold' }}>
            {mode === 'save' ? '🔒 워크플로우 암호화 저장' : '🔓 암호화된 파일 불러오기'}
          </h2>
          <button onClick={handleCancel} style={closeButtonStyle}>✕</button>
        </div>

        <form onSubmit={handleSubmit} style={formStyle}>
          <div style={infoBoxStyle}>
            <div style={{ fontSize: '14px', marginBottom: '8px' }}>
              {mode === 'save'
                ? '워크플로우를 암호화하여 안전하게 저장합니다.'
                : '암호화된 파일을 불러오려면 비밀번호가 필요합니다.'}
            </div>
            <div style={{ fontSize: '12px', color: '#6b7280' }}>
              💡 비밀번호는 안전하게 보관하세요. 분실 시 복구할 수 없습니다.
            </div>
          </div>

          <div style={fieldStyle}>
            <label style={labelStyle}>비밀번호</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="최소 4자 이상"
              style={inputStyle}
              autoFocus   // 모달 열릴 때 자동 포커스
            />
          </div>

          {/* 저장 모드일 때만 비밀번호 확인 필드 표시 */}
          {mode === 'save' && (
            <div style={fieldStyle}>
              <label style={labelStyle}>비밀번호 확인</label>
              <input
                type="password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                placeholder="비밀번호를 다시 입력하세요"
                style={inputStyle}
              />
            </div>
          )}

          {/* 조건부 렌더링: 에러가 있을 때만 에러 박스 표시 */}
          {error && (
            <div style={errorStyle}>
              ⚠️ {error}
            </div>
          )}

          <div style={buttonGroupStyle}>
            <button type="button" onClick={handleCancel} style={cancelButtonStyle}>
              취소
            </button>
            <button type="submit" style={confirmButtonStyle}>
              {mode === 'save' ? '💾 저장' : '📂 불러오기'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

const overlayStyle: React.CSSProperties = {
  position: 'fixed',
  top: 0,
  left: 0,
  right: 0,
  bottom: 0,
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

const closeButtonStyle: React.CSSProperties = {
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
  marginBottom: '20px',
  border: '1px solid #dbeafe',
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

export default PasswordModal;
