# REST API Reference

Every endpoint is fully documented through Swagger UI —
open **`http://<host>:8080/api/swagger-ui.html`** after boot.  This file
summarises the surface for a quick scan.

Base URL: `/api`. Everything (except the auth flows and Swagger) requires
a `Authorization: Bearer <accessToken>` header.

Error envelope (every 4xx / 5xx):
```json
{
  "timestamp": "2026-01-15T10:12:33.918Z",
  "status": 404,
  "error": "Not Found",
  "message": "Project with identifier 'xyz' was not found",
  "path": "/api/projects/xyz",
  "violations": []
}
```

## Authentication (`/auth`)

| Method | Path | Purpose |
|--------|------|---------|
| POST | `/auth/register` | Create a new account (default role GUEST) |
| POST | `/auth/login` | Exchange credentials for access + refresh tokens |
| POST | `/auth/refresh` | Rotate refresh token for a fresh pair |
| POST | `/auth/logout` | Revoke a single refresh token |
| POST | `/auth/logout-all` | Revoke every refresh token for the current user |
| POST | `/auth/forgot-password` | Email a 6-digit OTP |
| POST | `/auth/verify-otp` | Verify OTP, return a one-shot reset token |
| POST | `/auth/reset-password` | Consume reset token and set a new password |

## Users (`/users`) & profiles

| Method | Path | Notes |
|--------|------|-------|
| GET | `/users/me` | Current authenticated user |
| GET | `/users` | List (admin/head/director/auditor) |
| GET | `/users/{id}` | Fetch by id |
| PUT | `/users/{id}/role` | Change role (admin) |
| PUT | `/users/{id}/status` | Activate / suspend (admin) |
| GET | `/users/{id}/profile` | Extended profile |
| PUT | `/users/{id}/profile` | Create/update profile |

## Departments (`/departments`)

`POST | GET | GET/{id} | PUT/{id} | DELETE/{id}`

## Projects (`/projects`)

`POST | GET | GET/{id} | PUT/{id} | DELETE/{id}`

## Milestones (`/milestones`)

Same CRUD + `GET /milestones/project/{projectId}`.

## Tasks (`/tasks`)

Same CRUD + filters: `/tasks/project/{projectId}`, `/tasks/milestone/{milestoneId}`, `/tasks/assigned/{userId}`.

## Teams (`/project-teams`)

Same CRUD + `/project-teams/project/{projectId}` and `/project-teams/user/{userId}`.

## Documents (`/documents`)

| Method | Path | Purpose |
|--------|------|---------|
| POST | `/documents/folders` | Create folder |
| GET  | `/documents/folders` | List root folders |
| GET  | `/documents/folders/{parentId}/children` | List children |
| POST | `/documents` | Create document metadata |
| GET  | `/documents?q=...` | List (optional title filter) |
| GET  | `/documents/{id}` | Detail |
| PUT  | `/documents/{id}/status` | Advance approval workflow |
| DELETE | `/documents/{id}` | Remove document + versions + blobs |
| POST | `/documents/{id}/versions` | Upload a new binary version (`multipart/form-data`) |
| GET  | `/documents/{id}/versions` | List versions (newest first) |
| GET  | `/documents/versions/{versionId}/download` | Stream the binary |

## Equipment (`/equipment`)

CRUD + bookings + maintenance:

- `POST /equipment/bookings` · overlap-checked
- `POST /equipment/bookings/{id}/cancel`
- `GET /equipment/{id}/bookings`
- `POST /equipment/maintenance` · rolls calibration date forward
- `GET /equipment/{id}/maintenance`

## Inventory (`/inventory`)

- Warehouses: `POST /warehouses`, `GET /warehouses`
- Items: `POST/PUT /items`, `GET /items`, `GET /items/low-stock`
- Movements: `POST /movements` (IN/OUT/ADJUST), `GET /items/{id}/movements`
- Suppliers: `POST/PUT /suppliers`, `GET /suppliers`

## Procurement (`/procurement`)

- Requests: `POST /requests`, `POST /requests/{id}/approve|reject`, `GET /requests?status=`
- Orders:   `POST /orders`, `GET /orders?status=`
- Invoices: `POST /invoices`, `POST /invoices/{id}/pay`, `GET /invoices`

## Budget (`/budgets`)

- `POST /allocations`
- `GET /projects/{projectId}/allocations`
- `GET /projects/{projectId}/summary?fiscalYear=`
- `POST /expenses`  (refused if allocation would be exceeded)
- `GET /projects/{projectId}/expenses`

## Notifications (`/notifications`)

- `GET /notifications` — paginated (`?page&size`)
- `GET /notifications/unread-count`
- `POST /notifications/{id}/read`
- `POST /notifications/read-all`

## Audit logs (`/audit-logs`) · ADMIN / AUDITOR

- `GET /audit-logs?userId=&action=&page=&size=`

## Reports (`/reports`)

- `GET /reports/projects.pdf`
- `GET /reports/projects.xlsx`

## Dashboard (`/dashboard`)

- `GET /dashboard/summary` — cross-module counters + status breakdowns

## AI (`/ai`)

- `GET /ai/projects/{id}/risk`
- `GET /ai/projects/{id}/delay`
- `GET /ai/projects/{id}/budget-forecast`
- `POST /ai/chat` — multi-turn conversation
- `POST /ai/meeting-summary` — `{ "transcript": "…" }`
- `GET /ai/semantic-search?q=…` — ranks the document catalog

All AI endpoints degrade to a friendly canned message when
`ANTHROPIC_API_KEY` is unset — no crashes, no 500s.

---

### Rate limits & security

- CORS: driven by `erpms.security.allowed-origins`.
- All state-changing endpoints require a valid access token.
- Method-level RBAC via `@PreAuthorize` — see the individual controllers.
- Rate limiting should be terminated at your ingress (Nginx / API gateway).
