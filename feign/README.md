# feign README.md

## 📌 프로젝트 개요
Spring Boot 2.7 기반의 RESTful 서비스 프로젝트입니다. Java 11을 사용하여 개발되었으며, Feign 클라이언트를 통한 마이크로서비스 간 통신을 지원합니다. 주요 기능은 다음과 같습니다:

- HTTP 컨트롤러(REST API)
- Feign 클라이언트 기반 서비스 통신
- 테스트용 유틸리티 및 테스트 라이브러리
- 랜덤 솔트 생성 및 토큰 처리 기능

## 🧰 기술 스택
- **프레임워크**: Spring Boot 2.7.18
- **언어**: Java 11
- **RPC**: Feign Client (Spring Cloud OpenFeign)
- **테스트**: JUnit 5, Mockito, AssertJ
- **DB**: H2 (테스트용 인메모리 DB)
- **빌드**: Maven 3.8.6
- **코드 스타일**: 4-space 탭, snake_case, 80자 제한

## 📁 패키지 구조
```
src/main/java
├── kr.growth.eum.feign
│   ├── App.java                 // 메인 애플리케이션 진입점
│   ├── controller
│   │   ├── MyController.java    // 주요 HTTP 컨트롤러
│   │   └── MyLocalController.java // 로컬 서비스 처리 컨트롤러
│   ├── client
│   │   ├── MyServiceClient.java // 외부 서비스 Feign 클라이언트
│   │   ├── MyLocalServiceClient.java // 로컬 서비스 Feign 클라이언트
│   │   └── MyServiceFallback.java // Feign 클라이언트 Fallback 처리
│   ├── utils
│   │   ├── RandomSaltUtil.java  // 랜덤 솔트 생성 유틸리티
│   │   └── TokenUtils.java      // 토큰 처리 유틸리티
│   └── dto
│       └── DataResponse.java    // 응답 데이터 DTO
```

## 🚀 설정 방법
1. **프로젝트 복제**
```bash
git clone https://github.com/your-username/feign.git
cd feign
```

2. **의존성 설치**
```bash
mvn clean install
```

3. **애플리케이션 실행**
```bash
mvn spring-boot:run
```

## 🧪 테스트 방법
1. **단위 테스트 실행**
```bash
mvn test
```

2. **코드 커버리지 확인**
```bash
mvn jacoco:report
```

3. **REST API 테스트**
```bash
curl http://localhost:8080/api/data
```

## 📌 주요 기능 설명
### 🔐 보안 처리
- `TokenUtils` 클래스: JWT 토큰 생성 및 검증 로직
- `RandomSaltUtil` 클래스: 랜덤 솔트 생성기

### 🌐 서비스 통신
- `MyServiceClient`/`MyLocalServiceClient`: Feign 클라이언트로 외부 서비스 호출
- `MyServiceFallback`: 네트워크 오류 시 대체 로직 처리

### 📡 HTTP API
- `MyController`: 주요 REST 엔드포인트 제공
- `DataResponse`: 응답 데이터 포맷 정의

## 📝 참고 사항
1. 프로젝트는 Maven 기반으로 구성되어 있습니다. `pom.xml` 파일에서 의존성과 빌드 설정을 확인할 수 있습니다.
2. 테스트 시 H2 데이터베이스가 자동으로 초기화됩니다.
3. Feign 클라이언트는 Spring Cloud OpenFeign을 사용하고 있습니다.
4. 코드 스타일은 4-space 탭, snake_case, 80자 제한을 준수하고 있습니다.

이 README는 프로젝트의 기본 구조와 사용 방법을 설명합니다. 자세한 내용은 `pom.xml` 파일과 각 패키지의 소스 코드를 참고하시기 바랍니다.