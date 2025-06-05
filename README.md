#  Resource Management System
A microservices-based system for managing metering points and connection points across multiple countries, with real-time notifications via Apache Kafka.
##  Architecture Overview
The system consists of two main microservices:

* Resource Service (Port 8082): Manages resources, locations, and characteristics
* Notification Service : Handles Kafka event consumption and notifications

##  Tech Stack

* Java 21+
* Spring Boot 3.x
* PostgreSQL - Primary database
* Apache Kafka - Event streaming and notifications
* Docker & Docker Compose - Containerization
* Swagger/OpenAPI - API documentation
* Liquibase - Database migrations

##  Prerequisites
Before running the application, ensure you have:

* Docker and Docker Compose installed
* Java 21+ (for local development)
* Maven 3.8+ (for local development)
* Git

## Quick Start
1. Clone the Repository
    git clone git@github.com:o-shyshkan/ResourceManagementSystem.git
2. Start the Application
 Start all services using Docker Compose
 docker-compose up -d

 Check if all containers are running
 docker-compose ps

3. Verify Services

* Resource Service: http://localhost:8082
* Swagger UI: http://localhost:8082/swagger-ui/index.html
* API Docs: http://localhost:8082/v3/api-docs
* PgAdmin: http://localhost:16543 (test@gmail.com / test123!)    

## Services Overview
## Resource Service (Port 8082)
Manages the core business entities:
Domain Models:

* Resource: METERING_POINT or CONNECTION_POINT with country code
* Location: Street address, city, postal code, country code
* Characteristic: Code (max 5 chars), type (CONSUMPTION_TYPE, CHARGING_POINT, CONNECTION_POINT_STATUS), value

Key Features:

* RESTful CRUD operations for resources
* Automatic Kafka notifications on resource changes
* Bulk data export to stakeholders
* Database initialization with sample data (http://localhost:8082/inject)

Notification Service
Handles event processing and stakeholder notifications:
Key Features:

* Consumes Kafka events from resource changes
* Processes bulk data exports
* Manages stakeholder notifications

## API Documentation
API Docs: http://localhost:8082/v3/api-docs

## Sample Data
The application provides sample data through a dedicated endpoint:
Data Initialization Endpoint
GET /inject

This endpoint creates sample data including:

3 resources of different types:

* CONNECTION_POINT in Tallinn, Estonia (EE)
* METERING_POINT in Tartu, Estonia (EE)
* CONNECTION_POINT in Helsinki, Finland (FI)
* Multiple countries: Estonia (EE) and Finland (FI)
* Various characteristics for each resource:
  * CONSUMPTION_TYPE: "220", "380" (voltage levels)
  * CHARGING_POINT: "2 socket"
  * CONNECTION_POINT_STATUS: "Available"

## Database Schema
The application uses PostgreSQL with the following main tables:

* resources - Main resource entities
* locations - Address information
* characteristics - Resource characteristics
* Migration scripts handle schema versioning

## Kafka Topics

* notificationTopic - Individual resource change events
* notificationTopicAllData - Bulk data export events
