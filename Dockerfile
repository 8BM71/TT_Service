FROM openjdk:8-jdk-alpine

ADD build/libs/tputt-backend.jar /app.jar

EXPOSE 8008

RUN [[ -z "${CUSTOM_SSL_CRED}" ]] \ 
    && echo "[importkeystore] CUSTOM_SSL_CRED env variable does not found" \ 
    || echo yes | keytool -importkeystore -deststorepass ${CUSTOM_SSL_CRED} -destkeypass ${CUSTOM_SSL_CRED} -destkeystore /keys/letsencrypt.jks -srckeystore /keys/keystore.p12 -srcstoretype PKCS12 -srcstorepass ${CUSTOM_SSL_CRED} -alias tomcat
    
RUN [[ -z "${CUSTOM_SSL_CRED}" ]] \ 
    && echo "[import]         CUSTOM_SSL_CRED env variable does not found" \ 
    || echo ${CUSTOM_SSL_CRED} | keytool -import -trustcacerts -alias root -file /keys/chain.pem -keystore /keys/letsencrypt.jks

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
