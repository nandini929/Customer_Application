# Customer Manager

A full-stack, production-style application to manage customer records: create customers via a simple UI or REST API and list them. Built with Spring Boot 3 and H2 (in-memory for dev, file-based for production). **No login** — the API and UI are open; add authentication (e.g. Spring Security) if you deploy to a shared or public environment.

## Tech stack

- **Backend**: Java 17, Spring Boot 3.2 (Web, Data JPA, Validation, Actuator)
- **Database**: H2 (in-memory in dev, persistent file in prod)
- **Frontend**: HTML, CSS, and vanilla JavaScript served by Spring Boot

## Prerequisites

- Java 17+
- Maven 3.9+

## Getting started (development)

Default profile is `dev` (in-memory H2, H2 console enabled).

```bash
mvn spring-boot:run
```

Open **http://localhost:8080** in your browser.

- **Home**: form to create a customer (first name, last name, date of birth).
- **View customers**: table of all customers.

## Production run

Use the `prod` profile. Optional environment variables:

```bash
export SPRING_PROFILES_ACTIVE=prod
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
- **Integration tests**: `CustomerControllerIntegrationTest` — API with test profile (in-memory H2).

## REST API

Base URL: `http://localhost:8080/api/customers`

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

### Validation and edge cases

- **Names**: Required, non-blank after trim, max 100 characters each.
- **Date of birth**: Required, past date, from year 1900 onward.
- **Malformed request**: Invalid JSON or wrong date format returns `400`; in prod the response does not expose exception detail.

## Production readiness (no login)

The app is built for production-style deployment **without authentication**:

- **Profiles**: `dev` (in-memory H2, console on), `prod` (file H2, console off), `test` (isolated DB for tests).
- **Persistence**: Prod uses file-based H2 in `DATA_DIR`; data survives restarts.
- **Actuator**: `/actuator/health` and `/actuator/info` for monitoring; health details disabled in prod.
- **CORS**: Configurable via `APP_CORS_ORIGINS` (comma-separated).
- **Errors**: In prod, invalid request body returns a generic message; exception detail is not exposed (`app.api.expose-error-detail=false`).
- **Validation**: Bean validation and edge-case handling (whitespace-only names, max length, date range, malformed JSON).
- **Tests**: Unit and integration tests; run with `mvn test`.

For a shared or public deployment, add authentication (e.g. Spring Security) and lock down CORS and the H2 console.

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

## Submission (git bundle)

To create a bundle with full history:

```bash
git bundle create your-name-tech-test.bundle --all
```

Submit the generated `.bundle` file.
