FROM eclipse-temurin:25-jre-alpine

LABEL authors="Hantonik"
ARG JAR_FILE

WORKDIR /app
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]