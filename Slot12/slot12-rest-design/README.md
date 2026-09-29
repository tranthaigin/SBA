# SBA301 Slot 12 - REST Fundamentals with Spring Boot (Part A)

## Objective
Establish the foundational mental model for RESTful APIs:
- Resource identification and URI design using plural nouns
- Mapping HTTP methods (`GET`, `POST`, `PUT`, `DELETE`) to semantics
- Status code selection (`200`, `201`, `400`, `401`, `403`, `404`, `500`)
- Architectural constraints: Client-Server, Statelessness, Cacheability, Uniform Interface, Layered System
- Contract-first specification in `docs/api-contract-funews.md` for FUNewsManagementSystem
- Spring Boot 3 skeleton preparation

## Project Structure
```text
slot12-rest-design/
├── docs/
│    └── api-contract-funews.md
├── src/
│    └── main/
│         ├── java/com/fpt/sba301/slot12/Slot12Application.java
│         └── resources/application.properties
└── pom.xml
```

## Running the Application Skeleton
```bash
./mvnw spring-boot:run
# or on Windows:
mvnw.cmd spring-boot:run
```
