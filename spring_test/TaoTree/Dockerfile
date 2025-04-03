# build 만을 위한 도커 image
FROM maven:3.9.5-eclipse-temurin-21-alpine AS MAVEN_BUILD

WORKDIR /build

COPY pom.xml .

RUN mvn dependency:resolve

COPY src ./src
# maven install
RUN mvn clean install -DskipTests

# 실제 배포시 사용하는 docker image
FROM eclipse-temurin:21.0.1_12-jre

# path 생성 및 위치이동
WORKDIR /app

# .env 파일 복사 및 환경변수 설정
COPY .env .
RUN set -a && . ./.env && set +a

COPY --from=MAVEN_BUILD /build/target/tao.jar ./

# 환경 변수 정보
#ARG profile
#ARG JASYPT_PASSWORD
#ENV profile=$profile
#ENV JASYPT_PASSWORD=$JASYPT_PASSWORD

CMD export $(cat .env | xargs) && echo "192.168.0.175 myhome.minipc.com" >> /etc/hosts && \
     java -jar tao.jar --spring.profiles.active=${profile}
   #    tail -f /dev/null

#  java -jar tao.jar --spring.profiles.active=${profile}