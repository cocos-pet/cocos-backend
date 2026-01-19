# syntax=docker/dockerfile:1.19
FROM gradle:8.9-jdk21-alpine AS build
WORKDIR /app

ENV GRADLE_USER_HOME=/gradle-cache

COPY gradlew gradlew
COPY gradle/wrapper gradle/wrapper
COPY build.gradle settings.gradle ./

RUN chmod +x gradlew

RUN --mount=type=cache,target=/gradle-cache \
    ./gradlew dependencies --no-daemon

COPY src src

RUN --mount=type=cache,target=/gradle-cache \
    ./gradlew build -x test --no-daemon


FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

ARG SPRING_PROFILES_ACTIVE=dev
ENV SPRING_PROFILES_ACTIVE=$SPRING_PROFILES_ACTIVE
ENV TZ=Asia/Seoul

COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "app.jar"]
