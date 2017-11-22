FROM openjdk:8-jdk-alpine

ADD build/libs/TT_backend.jar /app.jar

EXPOSE 8008

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]