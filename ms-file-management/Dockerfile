FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY conciliaciones-iac/docker/maven-libs/ /tmp/maven-libs/

RUN mvn install:install-file \
    -Dfile=/tmp/maven-libs/conciliaciones-persistence-jpa-parent-1.0.0-SNAPSHOT.pom \
    -DgroupId=com.conciliaciones \
    -DartifactId=conciliaciones-persistence-jpa-parent \
    -Dversion=1.0.0-SNAPSHOT \
    -Dpackaging=pom

RUN mvn install:install-file \
    -Dfile=/tmp/maven-libs/conciliaciones-domain-model-1.0.0-SNAPSHOT.jar \
    -DgroupId=com.conciliaciones \
    -DartifactId=conciliaciones-domain-model \
    -Dversion=1.0.0-SNAPSHOT \
    -Dpackaging=jar

RUN mvn install:install-file \
    -Dfile=/tmp/maven-libs/conciliaciones-persistence-jpa-1.0.0-SNAPSHOT.jar \
    -DgroupId=com.conciliaciones \
    -DartifactId=conciliaciones-persistence-jpa \
    -Dversion=1.0.0-SNAPSHOT \
    -Dpackaging=jar

COPY conciliaciones-iac/docker/maven-libs/ /tmp/maven-libs/
COPY ms-file-management/pom.xml .
COPY ms-file-management/src ./src

RUN mvn clean package -DskipTests -Dspring-boot.main-class=com.conciliaciones.msfilemanagement.MsFileManagementApplication                                                

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 9092

ENTRYPOINT ["java", "-jar", "app.jar"]
