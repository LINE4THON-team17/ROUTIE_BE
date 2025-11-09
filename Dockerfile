FROM gradle:8.10-jdk21 AS build
WORKDIR /workspace

COPY settings.gradle build.gradle ./
COPY gradle gradle
COPY gradlew gradlew
COPY gradlew.bat gradlew.bat

RUN chmod +x gradlew
RUN ./gradlew --no-daemon dependencies || true

COPY src src
RUN ./gradlew --no-daemon clean bootJar -Pprofile=dev

FROM eclipse-temurin:21-jre
WORKDIR /app
ARG JAR_FILE=/workspace/build/libs/*.jar
COPY --from=build ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]