FROM gradle:8.10-jdk21 AS build
WORKDIR /workspace

COPY settings.gradle build.gradle ./
COPY gradle/ gradle/
COPY gradlew gradlew
COPY gradlew.bat gradlew.bat

RUN chmod +x gradlew

COPY src/ src/

RUN ./gradlew --no-daemon bootJar -x test -Pprofile=dev

FROM eclipse-temurin:21-jre
WORKDIR /app

ENV LANG=C.UTF-8 \
    LC_ALL=C.UTF-8 \
    TZ=Asia/Seoul \
    JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8 -Duser.timezone=Asia/Seoul"

COPY --from=build /workspace/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]