# Taco Cloud

Taco Cloud is a Spring Boot learning project upgraded into a portfolio-ready web application. It lets users register, sign in, design tacos, place orders, review their order history, and exposes REST endpoints for taco and ingredient data.

## Tech Stack

- Java 17
- Spring Boot 3.5
- Spring MVC + Thymeleaf
- Spring Security
- Spring Data JPA
- Spring Data REST
- MySQL or H2
- Apache Kafka
- Flyway database migrations
- Maven Wrapper

## Features

- Form login with BCrypt password hashing
- User registration with `ROLE_USER`
- Seeded admin user: `admin / admin123`
- Taco designer grouped by ingredient type
- Order checkout flow with session-backed taco drafts
- User order history with pagination settings
- Admin-only order cleanup endpoint
- REST API for ingredients, orders, and recent tacos
- Optional Kafka publishing for submitted orders
- Local H2 profile for quick demo startup
- Docker Compose setup for the app, MySQL, and Kafka
- Dockerfile for containerized application builds
- GitHub Actions CI for Maven tests

## Quick Start

Run the app with the default H2 database:

```powershell
.\mvnw.cmd spring-boot:run
```

Open:

- App: http://localhost:8080
- H2 console: http://localhost:8080/h2-console
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

Default admin account:

```text
username: admin
password: admin123
```

## Run Full Stack With Docker Compose

Build and start the application, MySQL, and Kafka:

```powershell
docker compose up --build
```

Open:

```text
http://localhost:8080
```

Stop containers:

```powershell
docker compose down
```

Stop containers and remove persisted MySQL/Kafka data:

```powershell
docker compose down -v
```

The Docker Compose stack uses these database defaults:

```text
database: tacocloud
username: tacocloud
password: tacocloud
```

MySQL is also exposed to the host on port `3308`.

## Docker Image

Build the application image:

```powershell
docker build -t taco-cloud .
```

Run the image by itself with the default H2 profile:

```powershell
docker run --rm -p 8080:8080 taco-cloud
```

## Useful Endpoints

- `GET /` - home page
- `GET /design` - taco designer
- `GET /orders` - current user's order history
- `GET /admin` - admin panel
- `GET /api` - recent tacos
- `GET /api/ingredients` - ingredient list
- `POST /api/orders` - create order from JSON
- `GET /data-api` - Spring Data REST root
- `GET /swagger-ui.html` - Swagger UI for REST API documentation
- `GET /v3/api-docs` - OpenAPI specification

Write operations under `/api/ingredients` are protected with JWT scopes:

- `SCOPE_writeIngredients`
- `SCOPE_deleteIngredients`

Database schema is managed by Flyway migrations in `src/main/resources/db/migration`.

## Build and Test

```powershell
.\mvnw.cmd test
```

The test suite covers application startup, MVC security behavior, public REST reads, protected REST writes, and API order creation.

GitHub Actions runs the same Maven test suite on every push or pull request to `main` and `master`.

Build a runnable jar:

```powershell
.\mvnw.cmd package
java -jar target\taco_cloud2-0.0.1-SNAPSHOT.jar
```

## Notes

The default profile intentionally uses H2 and disables Kafka publishing so the application can be reviewed without external services. Use the `mysql` profile when you want to demonstrate MySQL persistence and Kafka order events.

## Screenshots

### Home

![Home page](docs/screenshots/home.png)

### Taco Designer

![Taco designer](docs/screenshots/design.png)

### Checkout

![Checkout form](docs/screenshots/checkout.png)

### Order History

![Order history](docs/screenshots/orders.png)

### API Documentation

![Swagger UI](docs/screenshots/swagger.png)
