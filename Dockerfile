FROM gradle:8.9-jdk21-alpine AS build
WORKDIR /app

ENV GRADLE_USER_HOME=/gradle-cache

COPY gradlew gradlew
COPY gradle/wrapper gradle/wrapper
COPY build.gradle settings.gradle ./

COPY . .
RUN gradle build -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

ARG SPRING_PROFILES_ACTIVE=dev
ENV SPRING_PROFILES_ACTIVE=$SPRING_PROFILES_ACTIVE

ENV TZ=Asia/Seoul
COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "app.jar"]
