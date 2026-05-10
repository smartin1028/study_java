//
// encryption — 워크플로우 데이터의 AES 암호화/복호화 유틸리티
//
// Java 비교: CryptoUtils 와 같은 보안 유틸 클래스.
//
//   public class CryptoUtils {
//       public static String encrypt(Object data, String password) {
//           String json = objectMapper.writeValueAsString(data);
//           return AES.encrypt(json, password);
//       }
//       public static Object decrypt(String encrypted, String password) {
//           String json = AES.decrypt(encrypted, password);
//           return objectMapper.readValue(json, Object.class);
//       }
//   }
//
// React/TypeScript 의 유틸리티:
//   - Class 없이 모듈 레벨 함수 + export (Java 의 static 메서드와 유사)
//   - 외부 라이브러리(crypto-js) 의존
//   - Blob / FileReader 는 브라우저 API
//

import CryptoJS from 'crypto-js';

//
// 데이터를 AES 암호화하여 Base64 문자열로 반환
// JSON.stringify → AES.encrypt → .toString()
// Java: Cipher.getInstance("AES/CBC/PKCS5Padding").doFinal(json.getBytes());
//
export const encryptData = (data: any, password: string): string => {
  try {
    const jsonString = JSON.stringify(data);     // 객체 → JSON 문자열 (Java: ObjectMapper)
    const encrypted = CryptoJS.AES.encrypt(jsonString, password).toString();
    return encrypted;
  } catch (error) {
    console.error('암호화 실패:', error);
    throw new Error('데이터 암호화에 실패했습니다.');
  }
};

//
// 암호화된 문자열을 복호화하여 원본 데이터 반환
// AES.decrypt → .toString(Utf8) → JSON.parse
//
export const decryptData = (encryptedData: string, password: string): any => {
  try {
    const decrypted = CryptoJS.AES.decrypt(encryptedData, password);
    const jsonString = decrypted.toString(CryptoJS.enc.Utf8);

    if (!jsonString) {
      throw new Error('잘못된 비밀번호입니다.');
    }

    return JSON.parse(jsonString);   // JSON 문자열 → 객체 (Java: ObjectMapper.readValue)
  } catch (error) {
    console.error('복호화 실패:', error);
    throw new Error('데이터 복호화에 실패했습니다. 비밀번호를 확인해주세요.');
  }
};

//
// 워크플로우 데이터를 암호화하여 .eum 파일로 다운로드
//
// Blob → URL.createObjectURL → 가상 <a> 태그 클릭 → 다운로드
// Java 비교:
//   FileChooser fileChooser = new FileChooser();
//   fileChooser.setExtensionFilter(new FileFilter("EUM Files", "*.eum"));
//   File file = fileChooser.showSaveDialog(stage);
//   Files.write(file.toPath(), encrypted.getBytes());
//
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

    // Blob: 바이너리 데이터 덩어리 (Java: ByteArray)
    // URL.createObjectURL: Blob 을 가리키는 임시 URL 생성
    const blob = new Blob([encrypted], { type: 'text/plain' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    link.click();                     // 가상 클릭으로 다운로드 트리거
    URL.revokeObjectURL(url);         // 메모리 누수 방지 (Java: try-finally close)
  } catch (error) {
    console.error('파일 다운로드 실패:', error);
    throw error;
  }
};

//
// 암호화된 파일을 읽어서 복호화한 워크플로우 데이터 반환
//
// FileReader + Promise: 비동기 파일 읽기
// Java 비교:
//   byte[] bytes = Files.readAllBytes(file.toPath());
//   String encrypted = new String(bytes);
//   return decrypt(encrypted, password);
//
export const loadEncryptedWorkflow = (
  file: File,
  password: string
): Promise<{ nodes: any[]; edges: any[] }> => {
  //
  // new Promise((resolve, reject) => { ... })
  // Java: CompletableFuture.supplyAsync(() -> { ... });
  //
  // 콜백 기반 API(FileReader)를 Promise 로 래핑하는 패턴
  // Java: FutureTask / CompletableFuture 로 콜백 감싸기
  //
  return new Promise((resolve, reject) => {
    const reader = new FileReader();

    // onload: 파일 읽기 완료 시 호출되는 콜백 (Java: CompletionHandler)
    reader.onload = (e) => {
      try {
        const encryptedData = e.target?.result as string;
        const decrypted = decryptData(encryptedData, password);

        // 버전 검증
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

    // onerror: 파일 읽기 실패 시
    reader.onerror = () => {
      reject(new Error('파일 읽기에 실패했습니다.'));
    };

    reader.readAsText(file);
  });
};
