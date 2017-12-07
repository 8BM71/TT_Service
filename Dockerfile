FROM openjdk:8-jdk-alpine

ADD build/libs/tputt-backend.jar /app.jar

EXPOSE 80 443

RUN [[ -z "${CUSTOM_SSL_CRED}" ]] \ 
    && echo "[importkeystore] CUSTOM_SSL_CRED env variable does not found" \ 
    || echo yes | keytool -importkeystore -deststorepass ${CUSTOM_SSL_CRED} -destkeypass ${CUSTOM_SSL_CRED} -destkeystore /keys/letsencrypt.jks -srckeystore /keys/cert_and_key.p12 -srcstoretype PKCS12 -srcstorepass ${CUSTOM_SSL_CRED} -alias tomcat

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
