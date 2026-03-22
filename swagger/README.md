# Swagger API README

## 📦 프로젝트 개요
Swagger API는 Spring Boot 2.7와 Java 11을 기반으로 구축된 RESTful 웹 서비스입니다.
- 프로젝트 버전: 1.0.0
- 주요 기능: Swagger API 문서화, 사용자 관리, JWT 기반 인증
- 의존 라이브러리: Spring Web, Spring Data JPA, Swagger2

## 🧱 프로젝트 구조
```
study_java/swagger/
├── pom.xml                     # Maven 의존성 관리
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── kr.growth.eum.swagger/
│   │   │   │   ├── App.java             # 메인 애플리케이션 클래스
│   │   │   │   ├── config/              # Swagger 설정
│   │   │   │   │   └── OpenApiConfig.java
│   │   │   │   ├── controller/          # REST 컨트롤러
│   │   │   │   │   ├── UserController.java
│   │   │   │   ├── utils/               # 유틸리티 클래스
│   │   │   │   │   ├── RandomSaltUtil.java
│   │   │   │   │   └── TokenUtils.java
│   │   │   │   └── service/             # 서비스 로직 (추후 추가 예정)
│   │   │   └── resources/              # 자원 파일
│   │   │       └── application.properties
│   │   └── resources/
│   └── test/                      # 테스트 코드 (추후 추가 예정)
└── docs/                         # API 문서 (Swagger UI로 확인 가능)
```

## 🚀 빌드 & 실행
```bash
# 의존성 설치
mvn clean install

# 애플리케이션 실행
mvn spring-boot:run

# 빌드 패키징
mvn package
```

## 📦 의존성
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>io.springfox</groupId>
        <artifactId>springfox-swagger2</artifactId>
        <version>2.9.2</version>
    </dependency>
    <dependency>
        <groupId>io.springfox</groupId>
        <artifactId>springfox-swagger-ui</artifactId>
        <version>2.9.2</version>
    </dependency>
</dependencies>
```

## 📄 구성 파일
- `application.properties`: 데이터베이스 연결 정보, Swagger 설정 등
- `OpenApiConfig.java`: Swagger API 문서화 설정

## 🧪 테스트
```bash
# 단위 테스트 실행
mvn test
```

## 🤝 기여 가이드
1. 작업 시작 전 `TaskList`로 작업 목록 확인
2. `EnterPlanMode`로 작업 계획 수립
3. `TaskCreate`로 작업 항목 생성
4. `Bash`로 코드 작성 및 테스트
5. `Grep`으로 코드 검증
6. `TaskUpdate`로 진행 상황 업데이트
7. `ExitPlanMode`로 작업 완료

## 📌 주의사항
- `.env` 파일은 민감 정보를 저장하지 마세요. 환경 변수로 관리하세요.
- API 문서는 Swagger UI에서 확인 가능 (http://localhost:8080/swagger-ui.html)