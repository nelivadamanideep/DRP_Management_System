# Database Documentation

MySQL 8.x is the system of record for ERPMS. The canonical schema lives in
[`database/schema.sql`](../database/schema.sql). Because we ship
`spring.jpa.hibernate.ddl-auto=update` by default, Hibernate will
create/patch the schema automatically from the entity classes; the SQL
file is the reference for production `validate` mode and human review.

## Table map

| Domain | Tables |
|--------|--------|
| Access & Security | `roles`, `user_accounts`, `user_profiles`, `refresh_tokens`, `otp_verifications`, `password_reset_tokens` |
| Organisation | `departments` |
| Projects | `projects`, `milestones`, `tasks`, `task_comments`, `task_dependencies`, `project_team_members` |
| Documents | `document_folders`, `documents`, `document_versions` |
| Equipment | `equipment`, `equipment_bookings`, `equipment_maintenance_logs` |
| Inventory | `warehouses`, `suppliers`, `inventory_items`, `stock_movements` |
| Procurement | `purchase_requests`, `purchase_orders`, `invoices` |
| Budget | `budget_allocations`, `expenses` |
| Notifications & Audit | `notifications`, `audit_logs` |

## Conventions

- **Primary keys**: `id VARCHAR(36)` — application-generated UUID v4.
- **Timestamps**: `created_at` and `updated_at` (`DATETIME(6)`, UTC).
  Populated by MySQL `DEFAULT CURRENT_TIMESTAMP(6)` and by Hibernate's
  `@CreatedDate` / `@LastModifiedDate` (via `BaseAuditEntity`).
- **Money**: `DECIMAL(18,2)` for currency, `DECIMAL(18,4)` for stock
  quantities.
- **Enums**: represented as `VARCHAR` with domain-specific length. This
  keeps the schema portable (H2 for tests, PostgreSQL as an eventual
  option).
- **Soft-deletes**: intentionally avoided at v1 — `active` booleans are
  used on Department/Warehouse/Supplier for lifecycle. Full history is
  provided by the `audit_logs` table.

## Relationships (highlights)

```
user_accounts 1───n refresh_tokens
             1───n otp_verifications
             1───n password_reset_tokens
             1───1 user_profiles

departments  1───n projects
projects     1───n milestones
             1───n tasks
             1───n project_team_members
             1───n budget_allocations
             1───n expenses

milestones   1───n tasks
tasks        1───n task_comments
tasks        n───n tasks (via task_dependencies)

document_folders 1───n document_folders     (self-referential)
document_folders 1───n documents
documents        1───n document_versions

equipment    1───n equipment_bookings
             1───n equipment_maintenance_logs

warehouses   1───n inventory_items
suppliers    1───n inventory_items
             1───n purchase_orders
inventory_items  1───n stock_movements

purchase_requests 1───1 purchase_orders (optional)
purchase_orders   1───n invoices
```

## Indexes

Every hot query path has a covering index:

- `user_accounts(email)` — login lookup
- `projects(department_id, status)` — dashboard aggregation
- `tasks(project_id)`, `tasks(status)`, `tasks(assigned_to_user_id)`
- `documents(status)`, `documents(project_id)`
- `document_versions(document_id)`
- `equipment(status)` and `equipment_bookings(equipment_id, start_time, end_time)`
  — the overlap query uses this composite index
- `inventory_items(warehouse_id)`
- `stock_movements(item_id)`
- `purchase_requests(status)`, `purchase_orders(status)`
- `budget_allocations(project_id)`, `expenses(project_id)`
- `notifications(recipient_user_id, read_flag)`
- `audit_logs(user_id)`, `audit_logs(occurred_at)`, `audit_logs(target_type, target_id)`

## ER Diagram

An informal ER view is embedded in the schema file header. For a visual
diagram, import `database/schema.sql` into
[dbdiagram.io](https://dbdiagram.io/) or MySQL Workbench → *Reverse
Engineer Database*.

## Backups

```bash
mysqldump -u root -p erpms | gzip > erpms-$(date +%F).sql.gz
```

Restores:

```bash
gunzip < erpms-2026-01-15.sql.gz | mysql -u root -p erpms
```
