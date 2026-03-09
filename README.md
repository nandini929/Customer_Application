## Customer Manager

Simple Spring Boot application to create and list customers, with an HTML/JS frontend and an H2 in‑memory database.

### Tech stack

- **Backend**: Java 17, Spring Boot 3 (Web, Data JPA, Validation)
- **Database**: H2 in‑memory
- **Frontend**: Plain HTML, CSS, and vanilla JavaScript served by Spring Boot

### Getting started

#### Prerequisites

- Java 17+
- Maven 3.9+

#### Run the app

```bash
mvn spring-boot:run
```

Then open `http://localhost:8080` in your browser.

### Features

- Create a customer with:
  - First name
  - Last name
  - Date of birth (must be in the past)
- View all customers in a table.
- Basic validation with clear error messages shown in the UI.

### REST API

Base URL: `http://localhost:8080/api/customers`

- **POST** `/api/customers`
  - Body:
    ```json
    {
      "firstName": "Nandini",
      "lastName": "Matampathi",
      "dateOfBirth": "1998-03-25"
    }
    ```
  - Responses:
    - `201 Created` with created customer
    - `400 Bad Request` with validation errors

- **GET** `/api/customers`
  - Returns `200 OK` with list of customers.

### H2 console

For debugging during development:

- Console: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:customerdb`
- User: `sa`, empty password

### Notes and possible improvements

- Uses an in‑memory H2 database (data is lost on restart).
- No authentication/authorization or production hardening.
- Could be extended with:
  - Persistent database (PostgreSQL/MySQL)
  - Tests (unit + integration)
  - Spring Security and proper profiles for dev/prod.

