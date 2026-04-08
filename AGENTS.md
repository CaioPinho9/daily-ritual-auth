# AGENTS.md — daily-ritual-auth

## Scope
This repository is the **backend auth service** for Daily Ritual.
Keep changes limited to backend/API, database migrations, and backend infrastructure already tracked here.

## Stack
- Java 17
- Spring Boot 4.0.3
- Spring Web MVC
- Spring Security
- Spring Data JPA
- Liquibase
- PostgreSQL
- Tests: Spring Boot test starters + H2 for tests

## Local commands
- `mvn test`
- `mvn spring-boot:run`
- `docker compose up -d`

## Auth contract (keep stable)
- Access: JWT in `Authorization: Bearer <token>`
- Refresh: HttpOnly cookie `refresh_token`
- Refresh tokens persisted server-side (hash + expiry + revoked timestamp)
- Local cookie defaults: `SameSite=Lax`, `secure=false`

## API conventions
- Validation at controller boundary (`@Valid`)
- Error responses are consistent JSON:
  - `{ "message": "...", "details": { "field": "error" } }` (details optional)
- Status codes:
  - 400 validation
  - 401 invalid credentials/token
  - 403 disabled/forbidden
  - 409 conflicts (e.g., duplicate email)

## Database & migrations
- Liquibase is the source of truth.
- Use split changelog files under `src/main/resources/db/`.
- Never edit applied changeSets; add new changeSets for changes.
- Keep schema/table/column naming consistent with the repo’s conventions.

## Testing expectations
- Prefer small, meaningful tests:
  - auth flows (signup/login/me/refresh/logout)
  - repository/persistence tests where relevant
- Avoid brittle tests (random sleeps, time-dependent without clocks).

## Work hygiene
- Keep diffs minimal and scoped.
- Do not introduce additional frameworks (e.g., Spring Cloud AWS) unless explicitly requested.
- No frontend/UI changes in this repo.

## Commits
- Use prefixes: `feat:`, `fix:`, `refactor:`, `misc:`
- Reference GitHub issue numbers when applicable.
