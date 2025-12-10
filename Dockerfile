FROM eclipse-temurin:21

WORKDIR /app

COPY src/versions/crm-1.0.0.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]