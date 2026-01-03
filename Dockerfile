FROM gradle:8.9-jdk21-alpine AS build
WORKDIR /app

COPY gradle gradle
COPY gradlew build.gradle settings.gradle ./
RUN gradle dependencies --no-daemon

COPY . .
RUN gradle build -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

ARG SPRING_PROFILES_ACTIVE=dev
ENV SPRING_PROFILES_ACTIVE=$SPRING_PROFILES_ACTIVE

ENV TZ=Asia/Seoul
COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "app.jar"]
