FROM openjdk:17-jdk-slim
WORKDIR /app

# Maven은 결과물이 target 폴더에 생기므로 경로를 수정합니다.
COPY target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]