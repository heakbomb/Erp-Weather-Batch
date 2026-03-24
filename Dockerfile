# 1) Build stage
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# 의존성 캐시 최적화
COPY pom.xml .
RUN mvn -B -q -DskipTests dependency:go-offline

# 소스 복사 후 빌드
COPY . .
RUN mvn -B -DskipTests clean package

# 2) Run stage
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
