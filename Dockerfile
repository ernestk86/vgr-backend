FROM java:8 as runner

EXPOSE 8880

ENV DB_URL=$DB_URL
ENV DB_USERNAME=$DB_USERNAME
ENV DB_PASSWORD=$DB_PASSWORD

COPY target/vgr-backend-0.0.1-SNAPSHOT.jar vgr-backend.jar

ENTRYPOINT ["java", "-jar", "vgr-backend.jar"]