BlueTide Backend - Architecture Overview

Purpose
-------
This service is the backend API for BlueTide property management. It provides CRUD operations for properties, inventory, service requests, invoices, notifications, companies, and user authentication.

High-level components
---------------------
- Spring Boot 3 application (starter parent 3.4.2)
- REST controllers in `com.bluetide.services.controller`
- Domain models in `com.bluetide.services.models` (persisted in MongoDB)
- Spring Data MongoDB repositories in `com.bluetide.services.repository`
- Security: JWT-based (custom `JwtAuthenticationFilter`, `JwtUtils`), Spring Security configuration located in `config/SecurityConfig.java`
- Auth service and OAuth service under `com.bluetide.services.service`

Data model overview
-------------------
- `User`: holds basic user info, authentication provider, roles
- `Company`: organization grouping managers
- `Property`: represents properties managed by owners/managers
- `Inventory`: items within a property (appliances, fixtures)
- `Maintenance`: schedule/records tied to inventory
- `ServiceRequest`: tenant/owner requests for service
- `Invoice`: billing entries connected to service requests
- `Notification`: user notifications

Testing
-------
- Unit tests live under `src/test/java`.
- Controller slice tests use `@WebMvcTest` and have been adapted to disable security filters for fast slices where appropriate.

Running locally
---------------
- Use the project Java21 helper: `./scripts/mvnw-java21 spring-boot:run`
- Tests: `./scripts/mvnw-java21 test`

Notes
-----
- The project uses Lombok for models and DTOs. Enable annotation processing in the IDE.
- Some integration tests may try to connect to a local MongoDB at `localhost:27017`. Run MongoDB or mock repositories for isolated test runs.

