FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN chmod +x ./gradlew
RUN ./gradlew bootJar
EXPOSE 8080

CMD ["java", "-jar", "build/libs/app.jar"]