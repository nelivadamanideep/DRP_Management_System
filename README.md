# ERPMS — Enterprise Research Project Management System

A production-grade platform for research laboratories, engineering
organisations, universities and innovation centres. Manage projects,
milestones, tasks, teams, laboratories, equipment, inventory, procurement,
budgets and audit — with a first-class AI assistant embedded across the
console.

- **Backend**: Java 21 · Spring Boot 3 · Spring Security · Spring Data JPA · MySQL 8
- **Frontend**: React 18 · Vite · Tailwind CSS · Redux Toolkit · Recharts · Framer Motion · MUI icons
- **AI**: Claude Sonnet 4.5 (Anthropic Messages API) called via a small Java REST client
- **Deployment**: Docker · Docker Compose · Nginx · GitHub Actions CI

> **Status:** All 17 spec modules implemented (Auth, Dashboard, Departments, Users,
> Projects, Milestones, Tasks, Teams, Documents, Equipment, Inventory,
> Procurement, Budget, Reporting, Notifications, Audit, AI). ~50-table
> normalised MySQL schema. Swagger/OpenAPI 3 for every endpoint.

---

## Quick start (Docker Compose)

```bash
# 1. Configure environment (optional — sensible defaults ship in .env.example)
cp .env.example .env
$EDITOR .env         # add ANTHROPIC_API_KEY, SMTP creds, etc.

# 2. Bring everything up (MySQL + Backend + Frontend)
docker compose up --build

# 3. Open the app
open http://localhost           # or http://localhost:5173 in dev
open http://localhost:8080/api/swagger-ui.html
```

Default admin credentials (seeded on first boot):
```
email:    admin@example.com
password: Admin12345
```
Change them via `ERPMS_ADMIN_EMAIL` / `ERPMS_ADMIN_PASSWORD` in `.env`.

## Repository layout

```
erpms/
├── backend/                  Spring Boot 3 API (Java 21, Maven)
│   ├── src/main/java/com/erpms/
│   │   ├── auth/             Registration, login, refresh, OTP, reset
│   │   ├── user/             User accounts + profiles
│   │   ├── department/       Departments (heads, activation)
│   │   ├── project/          Projects (CRUD, priority, risk, status)
│   │   ├── milestone/        Milestones + progress
│   │   ├── task/             Tasks (Kanban statuses)
│   │   ├── team/             Project ↔ user allocations
│   │   ├── document/         Folders, documents, versioned uploads
│   │   ├── equipment/        Equipment, bookings, maintenance/calibration
│   │   ├── inventory/        Warehouses, items, movements, suppliers
│   │   ├── procurement/      PR → PO → invoice lifecycle
│   │   ├── budget/           Allocations + expenses per fiscal year
│   │   ├── notification/     In-app notifications + email dispatch
│   │   ├── audit/            AOP-based audit trail
│   │   ├── reporting/        PDF (OpenPDF) + Excel (Apache POI) exports
│   │   ├── ai/               Claude client + risk/delay/budget/chat/summary
│   │   ├── dashboard/        Cross-module aggregations
│   │   ├── common/           Exceptions, config, security, storage, audit base
│   │   └── security/         JWT service, filter, SecurityConfig
│   ├── src/main/resources/application.yml
│   └── Dockerfile
├── frontend/                 React + Vite + Tailwind SPA
│   ├── src/
│   │   ├── app/              Redux store, slices, router entry, axios client
│   │   ├── components/       AppLayout, AuthShell, UI primitives
│   │   └── pages/            Per-module pages (Auth, Dashboard, Projects, …)
│   ├── nginx.conf            Prod reverse-proxy + SPA fallback
│   └── Dockerfile
├── database/schema.sql       Canonical MySQL 8 schema (~50 tables + indexes)
├── docs/                     Detailed installation, deployment, API, DB, dev guides
├── docker-compose.yml        MySQL + backend + frontend orchestration
├── .env.example              Sample runtime configuration
└── .github/workflows/ci.yml  Maven build, Vite build, Docker image build
```

## Documentation

| Guide | Path |
|---|---|
| Installation (VS Code + local run) | [`docs/INSTALLATION.md`](docs/INSTALLATION.md) |
| Deployment (Docker Compose, Nginx, prod checklist) | [`docs/DEPLOYMENT.md`](docs/DEPLOYMENT.md) |
| REST API reference (150+ endpoints) | [`docs/API.md`](docs/API.md) |
| Database schema & ER model | [`docs/DATABASE.md`](docs/DATABASE.md) |
| Developer guide (architecture, conventions, tests) | [`docs/DEVELOPER_GUIDE.md`](docs/DEVELOPER_GUIDE.md) |

## Feature highlights

- **JWT auth** with refresh-token rotation, forgot-password OTP flow, single-use reset tokens, and per-user session revocation.
- **RBAC** with 11 roles, enforced via `@PreAuthorize` at the controller layer.
- **Global exception envelope** — every failure returns a stable `{ timestamp, status, error, message, path, violations }` shape.
- **Audit log** captured by an AOP aspect for every controller method (redacts sensitive params).
- **Document management** with hierarchical folders, versioned uploads, SHA-256 fingerprints and pluggable storage (local disk today, S3/MinIO drop-in tomorrow).
- **Equipment booking** with overlap detection and calibration cascade.
- **Procurement workflow** PR → approval → PO → invoice.
- **Budget guardrail** — expenses that would exceed an allocation are refused.
- **AI assistant** wired to Claude Sonnet 4.5 with dedicated endpoints for risk prediction, schedule delay prediction, budget forecast, semantic document search, meeting summarisation and multi-turn chat.
- **Reporting** — PDF and Excel exports of the project portfolio.
- **Modern SPA** with dark/light theme, responsive shell, Recharts dashboards, Framer Motion micro-animations and toast feedback everywhere.

## License

Proprietary. Adapt freely for internal / educational use.
