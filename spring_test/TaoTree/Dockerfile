# build 만을 위한 도커 image
FROM maven:3.9.2-eclipse-temurin-17 AS MAVEN_BUILD

WORKDIR /build

COPY pom.xml .

RUN mvn dependency:resolve

COPY src ./src
# maven install
RUN mvn clean install -DskipTests

# 실제 배포시 사용하는 docker image
FROM eclipse-temurin:17.0.7_7-jre

# path 생성 및 위치이동
WORKDIR /app

COPY --from=MAVEN_BUILD /build/target/tao.jar ./

# todo 추가 예정
#ARG profile
#RUN echo $profile
#ENV profile=$profile

ENTRYPOINT ["java", "-jar", "tao.jar"]
#ENTRYPOINT ["java", "-jar", "tao.jar", "--spring.profiles.active=${profile}"]
