# Build stage
FROM maven:3.9.11-eclipse-temurin-25 AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src

# Generate protobuf & package
RUN mvn clean package -DskipTests

# Run stage
FROM amazoncorretto:25-alpine AS runner

WORKDIR /app

COPY --from=builder /app/target/product-service-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 4001

ENTRYPOINT ["java", "-jar", "app.jar"]
