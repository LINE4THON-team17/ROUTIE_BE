# ---------- Build stage ----------
FROM gradle:8.10-jdk21 AS build
WORKDIR /workspace

# 경로 보존해서 복사
COPY settings.gradle build.gradle ./
# KTS면 위 두 줄 대신 build.gradle.kts / settings.gradle.kts 로 변경
COPY gradle gradle
COPY gradlew gradlew
COPY gradlew.bat gradlew.bat

RUN chmod +x gradlew
RUN ./gradlew --no-daemon dependencies || true

COPY src src
RUN ./gradlew --no-daemon clean bootJar -Pprofile=dev

# ---------- Runtime ----------
FROM eclipse-temurin:21-jre
WORKDIR /app
ARG JAR_FILE=/workspace/build/libs/*.jar
COPY --from=build ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]