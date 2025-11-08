# ---------- Build stage ----------
FROM gradle:8.10-jdk21 AS build
WORKDIR /workspace

# 래퍼/설정 파일을 '경로 보존'하여 복사
COPY settings.gradle build.gradle ./
# (KTS 쓰면 위 두 줄 대신 build.gradle.kts / settings.gradle.kts 로 교체)
COPY gradle gradle
COPY gradlew gradlew
COPY gradlew.bat gradlew.bat

RUN chmod +x gradlew

# 의존성 캐시
RUN ./gradlew --no-daemon dependencies || true

# 소스 복사 후 빌드
COPY src src
RUN ./gradlew --no-daemon clean bootJar -Pprofile=dev

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

ARG JAR_FILE=/workspace/build/libs/*.jar
COPY --from=build ${JAR_FILE} app.jar

ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]