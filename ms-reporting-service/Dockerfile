FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

ARG JAR_FILE=target/ms-reporting-service-*.jar

COPY ${JAR_FILE} app.jar

EXPOSE 9095

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
