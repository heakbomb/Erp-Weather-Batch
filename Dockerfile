# 수정 전: FROM openjdk:17-jdk-slim
# 수정 후: 아래 내용으로 변경
FROM eclipse-temurin:17-jdk-focal

WORKDIR /app
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]