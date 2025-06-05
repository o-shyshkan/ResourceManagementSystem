# resource-service/Dockerfile
FROM openjdk:21-jdk-slim

WORKDIR /app

# Copy the parent pom and module pom
COPY pom.xml .
COPY notification-service/pom.xml notification-service/
COPY resource-service/pom.xml resource-service/

# Copy Maven wrapper
COPY mvnw .
COPY .mvn .mvn

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code for all modules (needed for inter-module dependencies)
COPY notification-service/src notification-service/src
COPY resource-service/src resource-service/src

# Build the resource service
RUN ./mvnw clean package -pl resource-service -am -DskipTests -Dliquibase.skip=true

# Expose port
EXPOSE 8080

# Run the resource service
CMD ["java", "-jar", "resource-service/target/resource-service-0.0.1-SNAPSHOT.jar"]
