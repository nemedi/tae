FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /order2cash
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

FROM eclipse-temurin:21-jre-jammy
WORKDIR /order2cash
COPY --from=build /order2cash/target/order2cash-ws-1.0.0.jar order2cash.jar
ENTRYPOINT ["java", "-jar", "order2cash.jar"]
