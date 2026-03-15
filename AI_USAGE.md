# AI Usage Documentation

This file documents how AI tools were used while building this submission, as required by the technical test brief.

---

## 1. Which AI tools you used

- **Cursor** (with built-in AI) — used for code generation, refactoring, and answering questions during development. I did not use ChatGPT, Claude, or GitHub Copilot for this submission.

---

## 2. What you delegated to AI vs wrote yourself

**I wrote myself:** The overall design (REST API, in-memory H2 in dev, file-based H2 in prod, single-module app), the core domain model (`Customer`, `CustomerRequest`, `CustomerResponse`), the controller and service logic (create/list, trimming, mapping), and the validation rules (what to validate and why). I also decided to make the app production-style without login: profiles, actuator, configurable CORS, and safe error responses in prod.

**I delegated to AI:** Boilerplate and repetitive parts: `GlobalExceptionHandler`, custom validators (`NotBlankAfterTrim`, date-of-birth range), CORS config (property-based allowed origins), profile-based `application-*.properties` (dev/prod/test), and the README structure. I also used AI to generate unit and integration tests (service tests with Mockito, controller tests with MockMvc and `@ActiveProfiles("test")`) and to add edge-case handling (whitespace-only names, max length, malformed JSON, and the corresponding tests). AI helped draft the `AI_USAGE.md` structure; I filled in the content.

---

## 3. How you validated AI-generated code

- **Tests:** I ran `mvn test` after significant changes and fixed any failures. I added or adjusted tests when AI missed cases.
- **Manual checks:** I ran the app with `dev` and `prod` profiles, used the UI (create customer, view list), and called the API with curl/Postman for success and validation-error cases.
- **Code review:** I read through AI-generated code before committing: checked status codes, validation messages, and that no stack traces or internal detail are exposed in prod (`app.api.expose-error-detail`).
- **Docs:** I verified that README commands, env vars, and the production-readiness section match the actual configuration.

---

## 4. Examples where you corrected AI mistakes

1. **Test profile:** The integration tests needed a dedicated in-memory H2 database so they don’t clash with dev data. I had AI add `@ActiveProfiles("test")` and an `application-test.properties` with a separate DB URL and create-drop DDL so tests stay fast and isolated.

2. **Error response in prod:** The handler for `HttpMessageNotReadableException` was always adding a `detail` field with the exception message. I had it made configurable via `app.api.expose-error-detail` so prod does not expose internal error details, and I checked that the integration test for “invalid request body” still passes in the test profile where detail is enabled.

3. **UI reversion:** At one point the form was narrowed and centered; I preferred the original full-width layout with the Spring Boot badge. I asked to revert the customer page to the earlier version and kept the production-ready backend and config as-is.

---

## 5. Approximate time breakdown

| Activity                              | With AI | Without AI (estimate) |
|---------------------------------------|---------|------------------------|
| Backend (API, service, validation)    | ~1.5 h  | ~2.5 h                 |
| Frontend (UI, forms, list)            | ~0.5 h  | ~1 h                   |
| Tests                                 | ~1 h    | ~2 h                   |
| Profiles, actuator, CORS, prod config | ~1 h    | ~2 h                   |
| Documentation (README, AI_USAGE)      | ~0.5 h  | ~1 h                   |
| **Total**                             | **~4.5 h** | **~8.5 h**          |

I used rough hours; the “Without AI” column is my estimate if I had written and researched everything manually.

---

## 6. How AI impacted your development process

AI sped up the first pass: I could get a working controller, service, validation, and config quickly and then focus on design (profiles, production behaviour, edge cases) and on reviewing and correcting the generated code. I spent more time on tests and validation logic than I would have if I had been writing all the boilerplate myself, and I was able to add production-style features (actuator, configurable CORS, safe error responses) without getting stuck on syntax. I chose not to delegate the overall architecture and the decision of what to validate and how to structure the API; I used AI for implementation and then validated and adjusted the output so the submission reflects my own standards and understanding.
