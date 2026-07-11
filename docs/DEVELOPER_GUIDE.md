# Developer Guide

Guidance for contributors working on the ERPMS backend and frontend.

## Architecture

### Backend (Clean layered)

```
controller  ──▶  service  ──▶  repository  ──▶  entity/db
    │              │
    │              └──▶ external clients (AnthropicClient, EmailSender)
    │
    ▼
DTO (records)     ◀── responses/requests are always DTOs, never entities
```

Cross-cutting concerns:

- **Exception translation**: `GlobalExceptionHandler` maps every
  domain exception (`ResourceNotFoundException`, `BusinessRuleException`,
  `ForbiddenOperationException`, plus Spring's own) to a stable
  `ApiErrorResponse` JSON envelope.
- **Security**: `SecurityConfig` + `JwtAuthenticationFilter` +
  `JwtService`. Access + refresh tokens; refresh tokens are additionally
  persisted (SHA-256 hashed) so they can be revoked.
- **Audit**: `AuditLoggingAspect` intercepts every method inside
  `com.erpms..controller..*` and persists an `AuditLogEntity` via
  `AuditLogService.record(…)` **asynchronously** (see `AsyncConfig`).
- **Auditing timestamps**: entities extend `BaseAuditEntity` which uses
  Spring Data's `@CreatedDate` / `@LastModifiedDate`.
- **Storage**: `FileStorageService` interface; `LocalFileStorageService`
  is the default. To move to S3/MinIO, provide another `@Component`
  implementing `FileStorageService` and mark the local one `@Primary(false)`.
- **AI**: `AnthropicClient` is a thin `RestClient` wrapper. Adding new
  intelligence lives in `AiService`, not the controller.

### Frontend

- **Redux Toolkit** manages auth + theme; everything else uses
  local component state (deliberate to keep the surface small).
- **Axios instance** in `src/app/apiClient.js` transparently:
  - Injects the access token.
  - Rotates via `/auth/refresh` on 401 (with a single-flight guard).
  - Surfaces server-side error messages via `react-hot-toast`.
- **Routing**: React Router v6 with an `AppLayout` protected route tree
  and stand-alone `/login`, `/register`, `/forgot-password`,
  `/reset-password` pages.
- **Design system**: Tailwind with a bespoke palette (ink / accent / moss),
  the `Fraunces` display font and `Manrope` UI font. Components in
  `components/ui/Primitives.jsx` (`PageHeader`, `Section`, `StatCard`,
  etc.) drive visual consistency.

## Coding conventions

- **Java**
  - Java 21, records for DTOs, constructor injection everywhere.
  - No Lombok on production code (kept as an *optional* dep for
    ergonomic tests only).
  - `@Transactional(readOnly=true)` for query methods, plain
    `@Transactional` for writes.
  - Never return entities from controllers.
- **React**
  - Function components + hooks; no class components.
  - Every interactive element has a `data-testid` (Playwright / Cypress
    friendly).
  - Prefer composition over prop-drilling; primitives live in
    `components/ui/`.

## Running the test suite

```bash
# Backend (unit)
cd backend
mvn test

# Frontend (build check)
cd frontend
yarn build
```

Sample tests are provided for `JwtService` and `HashUtils`. Extend them
by mocking with **Mockito** for service classes and using
`@DataJpaTest` for repositories.

## Adding a new module

1. Create the package `com.erpms.<domain>/{entity,repository,service,controller,dto}`.
2. Model the entity extending `BaseAuditEntity` (created/updated at auto).
3. Add a Spring Data JPA repository — Spring Boot picks it up automatically.
4. Write the service (transactional, orchestrating the repo).
5. Expose the controller under `/api/<domain>`, annotate with
   `@Tag(...)` for Swagger, `@SecurityRequirement(name = "bearerAuth")`
   and `@PreAuthorize(...)` for RBAC.
6. Add a page under `frontend/src/pages/<domain>/`, register the route
   in `frontend/src/app/App.jsx` and the nav item in
   `frontend/src/components/layout/AppLayout.jsx`.
7. Extend `schema.sql` — Hibernate will auto-create the table in dev,
   but check the SQL for the production `validate` path.

## Swagger

Live at `http://localhost:8080/api/swagger-ui.html`. Every controller is
annotated with `@Tag(name, description)` and every non-trivial method
carries a `@Operation(summary = "…")`. Authentication is wired up via the
`bearerAuth` security scheme — press *Authorize* in Swagger UI once,
paste your access token, and every subsequent call is authenticated.

## Common gotchas

- **`ddl-auto=update`** is convenient in dev but never a substitute for
  managed migrations. For production, generate a Flyway migration from
  the current schema (`flyway baseline` + `flyway migrate`) and switch
  to `ddl-auto=validate`.
- **JWT secret must survive restarts** — otherwise every user is signed
  out on redeploy. Bake it into the environment, not the image.
- **Datetime storage** — everything uses UTC. The DB stores
  `DATETIME(6)` with `jdbc.time_zone=UTC` on Hibernate side.
- **Storage volume** — the `LocalFileStorageService` writes under
  `/var/lib/erpms/storage`. In Docker Compose this is mounted as a
  named volume; in Kubernetes bind it to a `PersistentVolumeClaim`.

## Roadmap

- OpenTelemetry auto-instrumentation for traces + metrics.
- Real full-text search (OpenSearch) backing the `/documents?q=` and
  `/ai/semantic-search` endpoints.
- WebSocket push for notifications and Kanban updates.
- Role-editable dashboards.
