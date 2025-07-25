## Kiris Shyǵys

The backend of Kiris Shyǵys is a secure, modular, and scalable financial tracking system built with **Java 17** and **Spring Boot 3.4.0**. It provides RESTful APIs for the client, enabling users to manage income and expenses, view analytics, and handle repeatable transactions.

## Tech Stack

- **Java 17 (LTS)** — core language for server logic
- **Spring Boot 3.4.0** — framework for REST APIs, configuration, and security
- **PostgreSQL** — relational database for storing user financial data
- **Spring Security + JWT + OAuth2** — user authentication and access control
- **Spring Data JPA** — database access layer

## Architecture

The backend follows a layered architecture:
- **Service Layer**: business logic for user registration, transactions, and reporting
- **Persistence Layer**: JPA entities with relational mappings
- **DTOs & Mappers**: secure and clean data transfer between layers
- **Repository Layer**: interfaces extending JpaRepository for CRUD operations
- **REST Controllers**: expose endpoints for client interaction
