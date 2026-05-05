import CryptoJS from 'crypto-js';

/**
 * 데이터를 AES 암호화하여 문자열로 반환
 */
export const encryptData = (data: any, password: string): string => {
  try {
    const jsonString = JSON.stringify(data);
    const encrypted = CryptoJS.AES.encrypt(jsonString, password).toString();
    return encrypted;
  } catch (error) {
    console.error('암호화 실패:', error);
    throw new Error('데이터 암호화에 실패했습니다.');
  }
};

/**
 * 암호화된 문자열을 복호화하여 원본 데이터 반환
 */
export const decryptData = (encryptedData: string, password: string): any => {
  try {
    const decrypted = CryptoJS.AES.decrypt(encryptedData, password);
    const jsonString = decrypted.toString(CryptoJS.enc.Utf8);

    if (!jsonString) {
      throw new Error('잘못된 비밀번호입니다.');
    }

    return JSON.parse(jsonString);
  } catch (error) {
    console.error('복호화 실패:', error);
    throw new Error('데이터 복호화에 실패했습니다. 비밀번호를 확인해주세요.');
  }
};

/**
 * 워크플로우 데이터를 암호화하여 파일로 다운로드
 */
export const downloadEncryptedWorkflow = (
  nodes: any[],
  edges: any[],
  password: string,
  filename: string = 'workflow.eum'
): void => {
  try {
    const workflowData = {
      version: '1.0',
      timestamp: new Date().toISOString(),
      nodes,
      edges,
    };

    const encrypted = encryptData(workflowData, password);

    // Blob 생성 및 다운로드
    const blob = new Blob([encrypted], { type: 'text/plain' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    link.click();
    URL.revokeObjectURL(url);
  } catch (error) {
    console.error('파일 다운로드 실패:', error);
    throw error;
  }
};

/**
 * 암호화된 파일을 읽어서 복호화
 */
export const loadEncryptedWorkflow = (
  file: File,
  password: string
): Promise<{ nodes: any[]; edges: any[] }> => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();

    reader.onload = (e) => {
      try {
        const encryptedData = e.target?.result as string;
        const decrypted = decryptData(encryptedData, password);

        // 버전 체크
        if (!decrypted.version || !decrypted.nodes || !decrypted.edges) {
          throw new Error('올바른 워크플로우 파일이 아닙니다.');
        }

        resolve({
          nodes: decrypted.nodes,
          edges: decrypted.edges,
        });
      } catch (error) {
        reject(error);
      }
    };

    reader.onerror = () => {
      reject(new Error('파일 읽기에 실패했습니다.'));
    };

    reader.readAsText(file);
  });
};
