# BookBridge

BookBridge is a Spring Boot + Thymeleaf book exchange marketplace with REST APIs, role-based security, Docker support, and GitHub Actions CI.

## Features
- User registration and form-based login/logout
- Role-based access for `ADMIN`, `SELLER`, and `BUYER`
- Book listing, browsing, editing, and seller flows
- Conversations and messaging between buyers and sellers
- Reviews, categories, wishlist, and transactions
- Dockerized app and PostgreSQL database

## Tech Stack
- Java 17
- Spring Boot 3.3.x
- Spring Security
- Spring Data JPA
- Thymeleaf
- PostgreSQL / H2 for tests
- Docker / Docker Compose
- GitHub Actions

## Repository Structure
See [`PROJECT_STRUCTURE.md`](PROJECT_STRUCTURE.md) for the package tree and main templates.

## API Reference
See [`API_ENDPOINTS.md`](API_ENDPOINTS.md) for the current controller routes and endpoints.

## Run Locally
### Prerequisites
- JDK 17+
- Maven Wrapper (`mvnw`)
- PostgreSQL if you are not using Docker

### Start the app
```powershell
./mvnw.cmd spring-boot:run
```

The app runs locally on:
- `http://localhost:8082`

### Run tests
```powershell
./mvnw.cmd test
```

## Run with Docker
```powershell
docker compose up --build
```

Docker exposes the app on:
- `http://localhost:8080`

## Git Workflow
- Create feature branches from `develop`
- Open pull requests into `develop`
- Merge stable changes from `develop` into `main`
- Avoid direct pushes to `main`

## CI
GitHub Actions runs Maven build and tests on pushes and pull requests to `develop` and `main`.

## Notes
- Authentication is currently form-based through Thymeleaf pages, not JWT REST auth.
- If you change database credentials, update the environment variables used by `docker-compose.yml` and `application.yml`.
