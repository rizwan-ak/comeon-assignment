## Player Service Assignment

This repository implements a Spring Boot-based REST API for managing players, including registration, login/logout sessions, and daily time limits on active sessions.

### Core Features

- **Player Registration:** Register with email, password, name, surname, date of birth, and address.
- **Login:** Authenticate with email and password, creating a session record.
- **Logout:** Invalidate an active session by session identifier.
- **Set Daily Time Limit:** Assign a per-player daily limit on total active session duration.
- **Enforcement:** Block logins when the limit is reached and auto-logout sessions exceeding the limit.

---

## Suggestions for Improvement

Below are recommendations to enhance robustness, maintainability, and production readiness:

1. **Validate Incoming DTOs**

   - Use Jakarta Bean Validation annotations (e.g., `@Email`, `@NotBlank`) on request DTOs.
   - Use `@Valid` to automatically enforce input constraints and return 400 responses on invalid payloads.

2. **Global Exception Handling**

   - Introduce `@ExceptionHandler` methods to convert exceptions into consistent JSON error responses (e.g., 400, 404, ...).
   - Define a standard error DTO containing `code`, `message`, and optional `details`.

3. **Auto‑Logout Scheduling (Cron Job)**

   - The task should scan active sessions, compare durations against each player’s limit, and log out sessions exceeding the limit.

4. **API Documentation (OpenAPI/Swagger)**

   - Add generate OpenAPI definitions and Swagger UI.

5. **Database Clean**

   - Ensure test isolation by cleaning the database (e.g., `@BeforeEach` to `deleteAll()` or `@Transactional` rollback).

6. **Service Split-Up (New Auth Service)**

   - Extract authentication and session management logic into a dedicated `AuthService`, separating concerns from `PlayerService`.
   - `AuthService` would handle login, logout, and time-limit enforcement, while `PlayerService` focuses on player data.

---
