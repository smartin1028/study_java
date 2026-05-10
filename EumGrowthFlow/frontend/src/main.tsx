//
// React 애플리케이션 진입점 (Entry Point)
//
// Java 비교: public static void main(String[] args) 에 해당.
//           JVM 이 main() 을 호출하듯, 브라우저가 index.html 의 <div id="root"> 를 찾아
//           ReactDOM.createRoot() 로 React 컴포넌트 트리를 마운트한다.
//
//           - index.html      = web.xml (배포 설정)
//           - main.tsx        = Main.java (진입 클래스)
//           - <App />         = new MainFrame() (최상위 UI 객체 생성)
//           - <StrictMode>    = 개발모드 전용 검증 도구 (@Deprecated 체크처럼 추가 경고 제공)
//

import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'

// ReactDOM.createRoot() → React의 가상 DOM 트리를 실제 브라우저 DOM 에 연결
// Java: JFrame frame = new JFrame(); frame.add(new App()); frame.setVisible(true);
createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
