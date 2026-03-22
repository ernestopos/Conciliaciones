FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/ms-reconciliation-core-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
