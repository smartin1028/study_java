//
// Vite 빌드 도구 설정 (Maven/Gradle 의 pom.xml / build.gradle 에 해당)
//
// Java 비교:
//   - Vite          → Maven (빌드 도구)
//   - @vitejs/plugin-react → maven-compiler-plugin (JSX/TS 변환 플러그인)
//   - dev server proxy    → Spring Cloud Gateway / Nginx 리버스 프록시
//   - defineConfig()      → pom.xml 의 <build> 섹션
//

import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

//
// defineConfig: Vite 타입 지원을 받는 설정 헬퍼
// Java: Maven 의 XML → <configuration><source>...</source></configuration>
//
export default defineConfig({
  //
  // plugins: Vite 빌드 파이프라인에 추가할 플러그인
  // Java: <plugins><plugin><groupId>...</groupId>...</plugin></plugins>
  //
  plugins: [react()],       // @vitejs/plugin-react → JSX/TSX 트랜스파일

  //
  // dev server 설정
  // Java: Spring Boot 의 application-dev.yml
  //       또는 webpack-dev-server 의 proxy 설정
  //
  server: {
    //
    // proxy: 개발 서버에서 /api 요청을 백엔드로 전달 (CORS 우회)
    // Java: @Bean public RouteLocator gatewayRoutes(RouteLocatorBuilder b) {
    //           return b.routes()
    //               .route(r -> r.path("/api/**")
    //                   .filters(f -> f.rewritePath("/api/(?<path>.*)", "/${path}"))
    //                   .uri("http://localhost:8000"))
    //               .build();
    //       }
    //
    proxy: {
      '/api': {
        target: 'http://localhost:8000',    // 백엔드 서버 주소
        changeOrigin: true,                 // Host 헤더를 target 으로 변경
        rewrite: (path) => path.replace(/^\/api/, ''),  // /api/users → /users
      },
    },
  },
})
