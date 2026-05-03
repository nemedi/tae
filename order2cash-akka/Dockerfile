# ── Build stage ──────────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build
COPY pom.xml .
# Cache dependencies before copying source
RUN mvn dependency:go-offline -q

COPY src ./src
RUN mvn package -DskipTests -q

# ── Runtime stage ─────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app
COPY --from=builder /build/target/order2cash-akka-1.0.0-jar-with-dependencies.jar /app/app.jar

# Default directory watched by BuyerActor; override with PURCHASE_ORDER_DIR
RUN mkdir -p /orders

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
