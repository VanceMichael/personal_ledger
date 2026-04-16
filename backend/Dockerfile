FROM maven:3.9.6-amazoncorretto-17 as builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=builder /app/target/ledger-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
