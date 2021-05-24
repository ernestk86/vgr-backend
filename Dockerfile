FROM maven:3.6.3-openjdk-8 as builder

COPY pom.xml pom.xml
COPY src/ src/

RUN mvn clean package

FROM java:8 as runner

EXPOSE 8880

ENV DB_URL=$DB_URL
ENV DB_USERNAME=$DB_USERNAME
ENV DB_PASSWORD=$DB_PASSWORD

COPY --from=builder target/Project-One-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]