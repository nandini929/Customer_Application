# Customer Manager

A full-stack, production-style application to manage customer records: create customers via a simple UI or REST API and list them. Built with Spring Boot 3 and H2 (in-memory for dev, file-based for production).

## Tech stack

- **Backend**: Java 17, Spring Boot 3.2 (Web, Data JPA, Validation, Security, Actuator)
- **Database**: H2 (in-memory in dev, persistent file in prod)
- **Frontend**: HTML, CSS, and vanilla JavaScript served by Spring Boot
- **Security**: Form login + HTTP Basic; credentials from configuration/env

## Prerequisites

- Java 17+
- Maven 3.9+

## Getting started (development)

Default profile is `dev` (in-memory H2, H2 console enabled).

```bash
mvn spring-boot:run
```

Open **http://localhost:8080**. In the **dev** profile you go straight to the app (no login).

- **Home**: form to create a customer (first name, last name, date of birth).
- To test login locally, set `APP_REQUIRE_AUTH=true`; then use `admin` / `admin` (or `APP_USERNAME` / `APP_PASSWORD`).
- **View customers**: table of all customers.

## Production run

Use the `prod` profile. Set credentials and optional DB path via environment variables:

```bash
export SPRING_PROFILES_ACTIVE=prod
export APP_USERNAME=your-admin-user
export APP_PASSWORD=your-secure-password
# Optional: DB file directory (default: ./data)
export DATA_DIR=/var/lib/customer-manager/data
# Optional: DB credentials (default: sa / empty)
export DB_USERNAME=sa
export DB_PASSWORD=your-db-password

mvn spring-boot:run
# Or run the built jar: java -jar target/customer-manager-0.0.1-SNAPSHOT.jar
```

- **H2** stores data in `DATA_DIR/customerdb` (persists across restarts).
- **H2 console** is disabled in prod.
- **Actuator**: `GET /actuator/health` and `/actuator/info` are unauthenticated for monitoring; health details are hidden in prod.
- **CORS**: Configure allowed origins with `APP_CORS_ORIGINS` (comma-separated, e.g. `https://app.example.com`).

## Running tests

```bash
mvn test
```

- **Unit tests**: `CustomerServiceTest` — service logic with mocked repository.
- **Integration tests**: `CustomerControllerIntegrationTest` — API with test profile and `@WithMockUser`; includes auth (401 when unauthenticated).

## REST API

Base URL: `http://localhost:8080/api/customers`

All API endpoints require authentication (session cookie after form login, or HTTP Basic).

| Method | Endpoint           | Description                    |
|--------|--------------------|--------------------------------|
| POST   | `/api/customers`   | Create a customer (body below) |
| GET    | `/api/customers`   | List all customers             |

### Create customer (POST)

**Request body:**

```json
{
  "firstName": "Jane",
  "lastName": "Doe",
  "dateOfBirth": "1995-06-10"
}
```

- **201 Created**: returns the created customer (with `id`).
- **400 Bad Request**: validation errors in body `errors`.
- **401 Unauthorized**: missing or invalid credentials.

### Validation and edge cases

- **Names**: Required, non-blank after trim, max 100 characters each.
- **Date of birth**: Required, past date, from year 1900 onward.
- **Malformed request**: Invalid JSON or wrong date format returns `400`; in prod the response does not expose exception detail.

## Profiles

| Profile | Database      | H2 console | Use case   |
|---------|---------------|------------|------------|
| `dev`   | In-memory H2  | Enabled    | Local dev  |
| `prod`  | File H2       | Disabled   | Production |
| `test`  | In-memory H2  | Disabled   | Tests      |

## Actuator

- **Health**: `GET /actuator/health` — unauthenticated; in prod `show-details` is off.
- **Info**: `GET /actuator/info` — unauthenticated.

## H2 console (development only)

When running with profile `dev`:

- URL: http://localhost:8080/h2-console  
- JDBC URL: `jdbc:h2:mem:customerdb`  
- User: `sa`  
- Password: *(empty)*  
- Access requires being logged in.

## Submission (git bundle)

To create a bundle with full history:

```bash
git bundle create your-name-tech-test.bundle --all
```

Submit the generated `.bundle` file.
