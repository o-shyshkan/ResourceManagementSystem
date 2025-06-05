#  Resource Management System
A microservices-based system for managing metering points and connection points across multiple countries, with real-time notifications via Apache Kafka.
##  Architecture Overview
The system consists of two main microservices:

Resource Service (Port 8082): Manages resources, locations, and characteristics
Notification Service (Port 8081): Handles Kafka event consumption and notifications

##  Tech Stack

Java 21+
Spring Boot 3.x
PostgreSQL - Primary database
Apache Kafka - Event streaming and notifications
Docker & Docker Compose - Containerization
Swagger/OpenAPI - API documentation
Liquibase - Database migrations

##  Prerequisites
Before running the application, ensure you have:

Docker and Docker Compose installed
Java 21+ (for local development)
Maven 3.8+ (for local development)
Git