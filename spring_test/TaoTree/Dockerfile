FROM maven:3.9.2-eclipse-temurin-17

WORKDIR /build

COPY pom.xml .

COPY src ./src

# maven build
RUN mvn clean install -DskipTests

# path 생성 및 위치이동
WORKDIR /app

# 내부 복사
RUN cp /build/target/tao.jar ./

# todo 추가 예정
#ARG profile
#RUN echo $profile
#ENV profile=$profile

ENTRYPOINT ["java", "-jar", "tao.jar"]
#ENTRYPOINT ["java", "-jar", "tao.jar", "--spring.profiles.active=${profile}"]
