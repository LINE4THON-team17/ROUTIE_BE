# ---------- Build stage ----------
FROM gradle:8.10-jdk21 AS build
WORKDIR /workspace

# 캐시 효율을 위해 의존성 메타 먼저 복사
COPY settings.gradle build.gradle gradle/ ./
COPY gradlew ./
RUN chmod +x gradlew

# 의존성만 미리 받아 캐시
RUN ./gradlew --no-daemon dependencies || true

# 실제 소스 복사 후 빌드
COPY src ./src
RUN ./gradlew --no-daemon clean bootJar -Pprofile=dev

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# 빌드 결과 JAR만 복사
ARG JAR_FILE=/workspace/build/libs/*.jar
COPY --from=build ${JAR_FILE} app.jar

# 필요시 프로필/옵션은 환경변수로 주입(Compose의 environment로 세팅)
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]