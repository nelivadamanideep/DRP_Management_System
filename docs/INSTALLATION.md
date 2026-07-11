# Installation Guide

This guide walks you through running ERPMS on your local machine using
**VS Code** as the primary editor. Everything is scripted, so once the
prerequisites are installed the app is one command away.

## 1. Prerequisites

| Tool | Version | Why |
|------|---------|-----|
| Java | 21+ (Temurin recommended) | Compile & run the Spring Boot backend |
| Maven | 3.9+ | Backend build (Maven wrapper is not shipped) |
| Node | 20+ | Frontend build |
| Yarn | 1.22+ (or `corepack enable`) | Frontend dependency manager |
| Docker + Docker Compose | latest | One-shot orchestration |
| MySQL | 8.x (only if running backend outside Docker) | Persistence |
| VS Code | latest | Recommended editor |

### VS Code extensions
- **Extension Pack for Java** (`vscjava.vscode-java-pack`)
- **Spring Boot Extension Pack** (`vmware.vscode-boot-dev-pack`)
- **ES7+ React** (`dsznajder.es7-react-js-snippets`)
- **Tailwind CSS IntelliSense** (`bradlc.vscode-tailwindcss`)
- **REST Client** or **Thunder Client** (for API poking)

## 2. Clone & bootstrap

```bash
git clone <your-fork-url>
cd erpms

# Copy environment defaults
cp .env.example .env
```

## 3. Run everything with Docker Compose (recommended)

```bash
docker compose up --build
```

- Frontend →  http://localhost
- Backend  →  http://localhost:8080/api
- Swagger  →  http://localhost:8080/api/swagger-ui.html
- MySQL    →  localhost:3306 (user `erpms`, password from `.env`)

Admin login: `admin@example.com` / `Admin12345`.

## 4. Run each service individually (VS Code / native)

### 4.1 MySQL

Start a MySQL 8 container:
```bash
docker run --name erpms-mysql -p 3306:3306 \
  -e MYSQL_DATABASE=erpms \
  -e MYSQL_USER=erpms \
  -e MYSQL_PASSWORD=erpms_dev_password \
  -e MYSQL_ROOT_PASSWORD=root_dev_password \
  -v $PWD/database/schema.sql:/docker-entrypoint-initdb.d/01-schema.sql:ro \
  -d mysql:8.4
```

### 4.2 Backend

```bash
cd backend
mvn spring-boot:run
```

In VS Code:
1. Open `backend/pom.xml` — Java tooling will import the project.
2. Open `ErpmsApplication.java` → Run.
3. Environment variables can be added via a launch config
   (`.vscode/launch.json` → `env` block).

Backend listens on `http://localhost:8080/api`.

### 4.3 Frontend

```bash
cd frontend
yarn install
yarn dev
```

Frontend runs on `http://localhost:5173` and proxies `/api` to
`http://localhost:8080` automatically (see `vite.config.js`).

Set `VITE_BACKEND_URL` in `frontend/.env` to point at a remote backend
instead of using the proxy.

## 5. Verify

- Open http://localhost:5173, sign in as the seeded admin.
- Visit `Departments` — you should see the seeded R&D / ENG / OPS / FIN / QA rows.
- Visit `Projects` → create a project — it should appear on the Dashboard.
- Visit `AI Assistant` — you'll see a canned message until you add
  `ANTHROPIC_API_KEY` in `.env` and restart the backend.

## 6. Common issues

| Symptom | Fix |
|---------|-----|
| `Communications link failure` on backend startup | MySQL not ready yet. `docker compose logs mysql` and wait for the health check. |
| `Access denied for user 'erpms'` | Password mismatch between `.env` and MySQL volume. Delete `mysql_data` volume or reset credentials. |
| `401 Unauthorized` on every call | Access token expired. Log out and back in, or verify `ERPMS_JWT_SECRET` is stable across restarts. |
| Uploads fail with 413 | Increase `spring.servlet.multipart.max-file-size` in `application.yml`. |
| AI endpoints return the disabled notice | Set `ANTHROPIC_API_KEY` and restart backend. |
