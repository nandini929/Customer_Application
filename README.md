# Customer Manager

A full-stack application to manage customer records: create customers via a simple UI or REST API and list them. Built with Spring Boot 3 and an H2 in-memory database.

## Tech stack

- **Backend**: Java 17, Spring Boot 3.2 (Web, Data JPA, Validation)
- **Database**: H2 in-memory (per brief)
- **Frontend**: HTML, CSS, and vanilla JavaScript served by Spring Boot

### Frontend choice

The UI is plain HTML/CSS/JS (no React/TypeScript) so the app stays self-contained: a single `mvn spring-boot:run` runs backend and frontend with no Node or build step. For a small create-and-list flow this keeps the solution simple and easy to run. The same API can be consumed later by a React or other SPA if needed.

## Prerequisites

- Java 17+
- Maven 3.9+

## Getting started

Run the application:

```bash
mvn spring-boot:run
```

Open **http://localhost:8080** in your browser.

- **Home**: form to create a customer (first name, last name, date of birth).
- **View customers**: table of all customers.

## Running tests

```bash
mvn test
```

- **Unit tests**: `CustomerServiceTest` — service logic with mocked repository (trimming, mapping).
- **Integration tests**: `CustomerControllerIntegrationTest` — HTTP layer with real Spring context and H2: POST/GET success and validation (400) for invalid or missing fields.

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
- **400 Bad Request**: validation errors in body `errors` (e.g. `firstName`, `lastName`, `dateOfBirth`) with message `"Validation failed"`.

### Validation and edge cases

- **Names**: Required, non-blank after trim (whitespace-only rejected), max 100 characters each.
- **Date of birth**: Required, must be a past date and from year 1900 onward.
- **Malformed request**: Invalid JSON or wrong date format returns `400` with body `{"message":"Invalid request body", "detail":"..."}`.
- **UI**: Inputs have `maxlength="100"`; form rejects whitespace-only names and non-past dates; non-JSON error responses are handled without throwing.

## Design decisions

- **Single module**: Backend and static frontend in one Spring Boot app for minimal setup and portability.
- **Validation**: Bean Validation on `CustomerRequest`; `GlobalExceptionHandler` returns structured 400 responses so the UI can show field-level errors.
- **Persistence**: In-memory H2 with `spring.jpa.hibernate.ddl-auto=update` so the schema is created/updated automatically and data persists for the lifetime of the JVM.
- **No auth**: Out of scope for this exercise; the API is open. In production you would add authentication/authorization (e.g. Spring Security) and lock down CORS and H2 console.

## H2 console (development)

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
