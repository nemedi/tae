FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /order2cash
COPY pom.xml .
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn package -DskipTests -q

FROM eclipse-temurin:21-jre-jammy
WORKDIR /order2cash
COPY --from=builder /order2cash/target/order2cash-akka-1.0.0.jar order2cash.jar
ENTRYPOINT ["java", "-jar", "order2cash.jar"]
