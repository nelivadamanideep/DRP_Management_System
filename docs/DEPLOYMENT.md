# Deployment Guide

ERPMS ships as three Docker images — MySQL 8, Spring Boot backend and
Nginx-served React frontend — orchestrated by a single Docker Compose
file. This guide covers taking that stack to a real environment.

## 1. Production-ready checklist

- [ ] Set `ERPMS_JWT_SECRET` to a random, ≥ 64-char string.
- [ ] Change `ERPMS_ADMIN_EMAIL` / `ERPMS_ADMIN_PASSWORD` before the very first boot.
- [ ] Set `ERPMS_REVEAL_OTP=false` (production must never echo OTPs in HTTP responses).
- [ ] Provide real SMTP creds and `ERPMS_MAIL_ENABLED=true`.
- [ ] Populate `ANTHROPIC_API_KEY` (or leave blank to disable AI cleanly).
- [ ] Point `ERPMS_ALLOWED_ORIGINS` to the exact URL(s) of the frontend.
- [ ] Switch `SPRING_JPA_DDL_AUTO=validate` and pipe `database/schema.sql`
      into MySQL yourself for deterministic schema.
- [ ] Attach persistent volumes for `mysql_data` and `erpms_storage`.
- [ ] Front the frontend container with TLS (see §3).

## 2. Docker Compose deployment

```bash
# on the server
git clone <fork-url> /opt/erpms
cd /opt/erpms
cp .env.example .env
$EDITOR .env                # fill in every value from the checklist above
docker compose pull
docker compose up -d --build
docker compose logs -f
```

Rolling upgrades:
```bash
git pull
docker compose build backend frontend
docker compose up -d
```

Backups:
```bash
docker exec erpms-mysql \
  mysqldump -uroot -p"$MYSQL_ROOT_PASSWORD" erpms > backup-$(date +%F).sql

# storage volume:
docker run --rm -v erpms_erpms_storage:/data -v $PWD:/backup alpine \
  tar czf /backup/storage-$(date +%F).tgz -C /data .
```

## 3. TLS with a public Nginx (recommended)

Place the compose stack behind a lightweight system Nginx that terminates TLS:

```nginx
server {
  listen 443 ssl http2;
  server_name erpms.example.com;

  ssl_certificate     /etc/letsencrypt/live/erpms.example.com/fullchain.pem;
  ssl_certificate_key /etc/letsencrypt/live/erpms.example.com/privkey.pem;

  location / {
    proxy_pass         http://127.0.0.1:80;
    proxy_set_header   Host              $host;
    proxy_set_header   X-Real-IP         $remote_addr;
    proxy_set_header   X-Forwarded-For   $proxy_add_x_forwarded_for;
    proxy_set_header   X-Forwarded-Proto https;
  }
}
```

Certbot issues:
```bash
certbot --nginx -d erpms.example.com --redirect
```

## 4. Kubernetes (optional pattern)

Each Docker image is stateless (the backend delegates state to MySQL +
mounted storage), so a Kubernetes migration is a straightforward mapping:

- `mysql` → StatefulSet with a `PersistentVolumeClaim` (or a managed RDS).
- `backend` → Deployment with 2+ replicas, mount an `erpms-storage` PVC at
  `/var/lib/erpms/storage`, expose via a `ClusterIP` `Service`.
- `frontend` → Deployment behind an `Ingress` (Nginx / Traefik).
- Secrets: `ERPMS_JWT_SECRET`, `ANTHROPIC_API_KEY`, SMTP password, MySQL
  password — all as `Secret` objects mounted as env vars.

## 5. Observability

- **Health**: `GET /api/actuator/health` (public), `/health` inside cluster.
- **Info**:   `GET /api/actuator/info`.
- **Metrics**: `GET /api/actuator/metrics` (secured — expose behind a mesh
  or private ingress and integrate with Prometheus via
  `micrometer-registry-prometheus` — one dependency swap).
- **Logs**: JSON logs to stdout, ready for `docker logs` /
  `kubectl logs` / any log shipper.

## 6. Zero-downtime deploys

Because MySQL migrations happen automatically via
`SPRING_JPA_DDL_AUTO=update`, the safest pattern is:

1. Deploy the new backend to a **canary** container next to the current one.
2. Once its `/actuator/health` reports UP, switch the frontend upstream.
3. Retire the previous backend.

For destructive migrations, freeze writes, run a one-off `mysql` script
from `database/schema.sql`, then roll the app.

## 7. Security hardening

- **Rate limiting** — put Nginx `limit_req_zone` in front of `/api/auth/*`.
- **HSTS + CSP** — add the headers at the public Nginx layer.
- **Secret rotation** — rotate `ERPMS_JWT_SECRET` quarterly, then hit
  `POST /api/auth/logout-all` from an admin session to purge sessions.
- **Backups** — dump MySQL nightly and the storage volume weekly.
