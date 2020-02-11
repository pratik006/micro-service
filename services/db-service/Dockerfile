FROM openjdk:8-jdk-alpine
LABEL maintainer="pratik006@gmail.com"
EXPOSE 8080
ARG JAR_FILE=build/lib/db-service-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} db-service-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/db-service-0.0.1-SNAPSHOT.jar"]