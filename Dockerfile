FROM openjdk:21-jdk-slim

WORKDIR /app

COPY build/libs/crm-0.0.1.jar app.jar

EXPOSE 9000

CMD ["java", "-jar", "app.jar"]