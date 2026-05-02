FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

ARG JAR_FILE=target/ms-reconciliation-core-*.jar

COPY ${JAR_FILE} app.jar

EXPOSE 9091

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
