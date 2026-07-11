-- ============================================================================
-- ERPMS canonical database schema (MySQL 8.x)
-- ============================================================================
-- This file is the authoritative reference for the ERPMS relational model.
--   • In development, Spring Boot's `ddl-auto=update` will maintain the schema
--     automatically from the JPA entities.
--   • In production, set SPRING_JPA_DDL_AUTO=validate and pipe this script
--     into MySQL so the schema is deterministic and auditable.
--
-- Naming conventions:
--   • Tables      : snake_case, plural.
--   • Primary keys: `id` varchar(36) UUID (application-generated).
--   • Timestamps  : `created_at`, `updated_at` (datetime(6), UTC).
--   • Foreign keys: fk_<table>_<column> pattern.
-- ============================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------------------------------------------------------
-- Security & user domain
-- ----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS roles (
  id varchar(36) PRIMARY KEY,
  name varchar(60) NOT NULL UNIQUE,
  description varchar(180) NOT NULL,
  created_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
);

CREATE TABLE IF NOT EXISTS user_accounts (
  id varchar(36) PRIMARY KEY,
  email varchar(180) NOT NULL UNIQUE,
  full_name varchar(120) NOT NULL,
  password_hash varchar(255) NOT NULL,
  role varchar(30) NOT NULL DEFAULT 'GUEST',
  status varchar(30) NOT NULL DEFAULT 'ACTIVE',
  last_login_at datetime(6),
  created_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
);

CREATE TABLE IF NOT EXISTS user_profiles (
  id varchar(36) PRIMARY KEY,
  user_id varchar(36) NOT NULL UNIQUE,
  designation varchar(120),
  department_id varchar(36),
  experience_years int NOT NULL DEFAULT 0,
  skills text,
  certifications text,
  phone varchar(30),
  CONSTRAINT fk_user_profiles_user FOREIGN KEY (user_id) REFERENCES user_accounts(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS refresh_tokens (
  id varchar(36) PRIMARY KEY,
  user_id varchar(36) NOT NULL,
  token_hash varchar(128) NOT NULL UNIQUE,
  expires_at datetime(6) NOT NULL,
  revoked tinyint(1) NOT NULL DEFAULT 0,
  created_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES user_accounts(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS otp_verifications (
  id varchar(36) PRIMARY KEY,
  user_id varchar(36) NOT NULL,
  purpose varchar(30) NOT NULL,
  code_hash varchar(128) NOT NULL,
  expires_at datetime(6) NOT NULL,
  consumed tinyint(1) NOT NULL DEFAULT 0,
  attempts int NOT NULL DEFAULT 0,
  created_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_otp_user FOREIGN KEY (user_id) REFERENCES user_accounts(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS password_reset_tokens (
  id varchar(36) PRIMARY KEY,
  user_id varchar(36) NOT NULL,
  token_hash varchar(128) NOT NULL UNIQUE,
  expires_at datetime(6) NOT NULL,
  consumed tinyint(1) NOT NULL DEFAULT 0,
  created_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_pwd_reset_user FOREIGN KEY (user_id) REFERENCES user_accounts(id) ON DELETE CASCADE
);

-- ----------------------------------------------------------------------------
-- Organisation
-- ----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS departments (
  id varchar(36) PRIMARY KEY,
  code varchar(30) NOT NULL UNIQUE,
  name varchar(160) NOT NULL,
  description text,
  head_user_id varchar(36),
  active tinyint(1) NOT NULL DEFAULT 1,
  created_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_departments_head FOREIGN KEY (head_user_id) REFERENCES user_accounts(id)
);

-- ----------------------------------------------------------------------------
-- Projects, milestones, tasks, teams
-- ----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS projects (
  id varchar(36) PRIMARY KEY,
  project_code varchar(40) NOT NULL UNIQUE,
  title varchar(220) NOT NULL,
  summary text,
  department_id varchar(36),
  director_user_id varchar(36),
  priority varchar(30) NOT NULL,
  risk_level varchar(30) NOT NULL,
  status varchar(40) NOT NULL,
  planned_start_date date,
  planned_end_date date,
  approved_budget decimal(18,2) NOT NULL DEFAULT 0,
  created_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_projects_department FOREIGN KEY (department_id) REFERENCES departments(id),
  CONSTRAINT fk_projects_director FOREIGN KEY (director_user_id) REFERENCES user_accounts(id)
);

CREATE TABLE IF NOT EXISTS milestones (
  id varchar(36) PRIMARY KEY,
  project_id varchar(36) NOT NULL,
  name varchar(180) NOT NULL,
  description text,
  due_date date,
  progress_percent int NOT NULL DEFAULT 0,
  status varchar(40) NOT NULL,
  CONSTRAINT fk_milestones_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tasks (
  id varchar(36) PRIMARY KEY,
  project_id varchar(36) NOT NULL,
  milestone_id varchar(36),
  title varchar(220) NOT NULL,
  description text,
  assigned_to_user_id varchar(36),
  priority varchar(30) NOT NULL,
  status varchar(40) NOT NULL,
  due_date date,
  progress_percent int NOT NULL DEFAULT 0,
  CONSTRAINT fk_tasks_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
  CONSTRAINT fk_tasks_milestone FOREIGN KEY (milestone_id) REFERENCES milestones(id),
  CONSTRAINT fk_tasks_user FOREIGN KEY (assigned_to_user_id) REFERENCES user_accounts(id)
);

CREATE TABLE IF NOT EXISTS task_comments (
  id varchar(36) PRIMARY KEY,
  task_id varchar(36) NOT NULL,
  author_user_id varchar(36) NOT NULL,
  body text NOT NULL,
  created_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_task_comments_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS task_dependencies (
  id varchar(36) PRIMARY KEY,
  task_id varchar(36) NOT NULL,
  depends_on_task_id varchar(36) NOT NULL,
  CONSTRAINT uq_task_dep UNIQUE (task_id, depends_on_task_id),
  CONSTRAINT fk_task_dep_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
  CONSTRAINT fk_task_dep_dep FOREIGN KEY (depends_on_task_id) REFERENCES tasks(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS project_team_members (
  id varchar(36) PRIMARY KEY,
  project_id varchar(36) NOT NULL,
  user_id varchar(36) NOT NULL,
  role_in_project varchar(80) NOT NULL,
  allocation_percent int NOT NULL DEFAULT 100,
  active tinyint(1) NOT NULL DEFAULT 1,
  CONSTRAINT uq_team_project_user UNIQUE (project_id, user_id),
  CONSTRAINT fk_team_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
  CONSTRAINT fk_team_user FOREIGN KEY (user_id) REFERENCES user_accounts(id)
);

-- ----------------------------------------------------------------------------
-- Document management
-- ----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS document_folders (
  id varchar(36) PRIMARY KEY,
  name varchar(180) NOT NULL,
  parent_id varchar(36),
  project_id varchar(36),
  path varchar(1000) NOT NULL,
  created_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_folder_parent FOREIGN KEY (parent_id) REFERENCES document_folders(id)
);

CREATE TABLE IF NOT EXISTS documents (
  id varchar(36) PRIMARY KEY,
  title varchar(240) NOT NULL,
  description text,
  document_type varchar(40) NOT NULL,
  status varchar(30) NOT NULL,
  project_id varchar(36),
  folder_id varchar(36),
  owner_user_id varchar(36) NOT NULL,
  current_version_id varchar(36),
  confidential tinyint(1) NOT NULL DEFAULT 0,
  created_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_documents_folder FOREIGN KEY (folder_id) REFERENCES document_folders(id),
  CONSTRAINT fk_documents_owner FOREIGN KEY (owner_user_id) REFERENCES user_accounts(id)
);

CREATE TABLE IF NOT EXISTS document_versions (
  id varchar(36) PRIMARY KEY,
  document_id varchar(36) NOT NULL,
  version_number int NOT NULL,
  storage_key varchar(500) NOT NULL,
  file_name varchar(255) NOT NULL,
  content_type varchar(200),
  size_bytes bigint NOT NULL,
  content_sha256 varchar(64),
  uploaded_by_user_id varchar(36) NOT NULL,
  changelog text,
  created_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_versions_document FOREIGN KEY (document_id) REFERENCES documents(id) ON DELETE CASCADE
);

-- ----------------------------------------------------------------------------
-- Equipment
-- ----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS equipment (
  id varchar(36) PRIMARY KEY,
  asset_tag varchar(60) NOT NULL UNIQUE,
  name varchar(200) NOT NULL,
  description text,
  manufacturer varchar(120),
  model_number varchar(120),
  serial_number varchar(120),
  department_id varchar(36),
  laboratory_location varchar(200),
  purchase_date date,
  next_calibration_date date,
  qr_code_payload varchar(400),
  status varchar(30) NOT NULL DEFAULT 'AVAILABLE',
  created_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_equipment_department FOREIGN KEY (department_id) REFERENCES departments(id)
);

CREATE TABLE IF NOT EXISTS equipment_bookings (
  id varchar(36) PRIMARY KEY,
  equipment_id varchar(36) NOT NULL,
  booked_by_user_id varchar(36) NOT NULL,
  project_id varchar(36),
  start_time datetime(6) NOT NULL,
  end_time datetime(6) NOT NULL,
  status varchar(30) NOT NULL DEFAULT 'SCHEDULED',
  purpose text,
  created_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_bookings_equipment FOREIGN KEY (equipment_id) REFERENCES equipment(id) ON DELETE CASCADE,
  CONSTRAINT fk_bookings_user FOREIGN KEY (booked_by_user_id) REFERENCES user_accounts(id)
);

CREATE TABLE IF NOT EXISTS equipment_maintenance_logs (
  id varchar(36) PRIMARY KEY,
  equipment_id varchar(36) NOT NULL,
  performed_by_user_id varchar(36),
  performed_on date NOT NULL,
  activity varchar(40) NOT NULL,
  notes text,
  next_due_on date,
  created_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_maint_equipment FOREIGN KEY (equipment_id) REFERENCES equipment(id) ON DELETE CASCADE
);

-- ----------------------------------------------------------------------------
-- Inventory
-- ----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS warehouses (
  id varchar(36) PRIMARY KEY,
  code varchar(30) NOT NULL UNIQUE,
  name varchar(160) NOT NULL,
  location varchar(300),
  manager_user_id varchar(36),
  active tinyint(1) NOT NULL DEFAULT 1,
  created_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
);

CREATE TABLE IF NOT EXISTS suppliers (
  id varchar(36) PRIMARY KEY,
  name varchar(200) NOT NULL,
  contact_email varchar(200),
  contact_phone varchar(50),
  address varchar(400),
  gst_number varchar(60),
  active tinyint(1) NOT NULL DEFAULT 1,
  created_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
);

CREATE TABLE IF NOT EXISTS inventory_items (
  id varchar(36) PRIMARY KEY,
  sku varchar(60) NOT NULL UNIQUE,
  name varchar(200) NOT NULL,
  description text,
  category varchar(40),
  unit varchar(30),
  warehouse_id varchar(36),
  stock_quantity decimal(18,4) NOT NULL DEFAULT 0,
  reorder_level decimal(18,4) NOT NULL DEFAULT 0,
  unit_cost decimal(18,2) NOT NULL DEFAULT 0,
  supplier_id varchar(36),
  created_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_items_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),
  CONSTRAINT fk_items_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
);

CREATE TABLE IF NOT EXISTS stock_movements (
  id varchar(36) PRIMARY KEY,
  item_id varchar(36) NOT NULL,
  direction varchar(20) NOT NULL,
  quantity decimal(18,4) NOT NULL,
  reason varchar(200),
  reference_id varchar(36),
  performed_by_user_id varchar(36),
  created_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_movements_item FOREIGN KEY (item_id) REFERENCES inventory_items(id) ON DELETE CASCADE
);

-- ----------------------------------------------------------------------------
-- Procurement
-- ----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS purchase_requests (
  id varchar(36) PRIMARY KEY,
  request_number varchar(60) NOT NULL UNIQUE,
  title varchar(240) NOT NULL,
  justification text,
  project_id varchar(36),
  requested_by_user_id varchar(36) NOT NULL,
  supplier_id varchar(36),
  estimated_cost decimal(18,2) NOT NULL DEFAULT 0,
  status varchar(30) NOT NULL DEFAULT 'DRAFT',
  approver_user_id varchar(36),
  approver_comments text,
  created_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_pr_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
);

CREATE TABLE IF NOT EXISTS purchase_orders (
  id varchar(36) PRIMARY KEY,
  po_number varchar(60) NOT NULL UNIQUE,
  request_id varchar(36),
  supplier_id varchar(36) NOT NULL,
  issued_by_user_id varchar(36) NOT NULL,
  issued_on date NOT NULL,
  expected_delivery date,
  total_amount decimal(18,2) NOT NULL DEFAULT 0,
  status varchar(30) NOT NULL DEFAULT 'ISSUED',
  notes text,
  created_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_po_request FOREIGN KEY (request_id) REFERENCES purchase_requests(id),
  CONSTRAINT fk_po_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
);

CREATE TABLE IF NOT EXISTS invoices (
  id varchar(36) PRIMARY KEY,
  invoice_number varchar(60) NOT NULL UNIQUE,
  purchase_order_id varchar(36) NOT NULL,
  supplier_id varchar(36) NOT NULL,
  invoice_date date NOT NULL,
  due_date date,
  amount decimal(18,2) NOT NULL DEFAULT 0,
  status varchar(30) NOT NULL DEFAULT 'OPEN',
  created_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_invoice_po FOREIGN KEY (purchase_order_id) REFERENCES purchase_orders(id),
  CONSTRAINT fk_invoice_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
);

-- ----------------------------------------------------------------------------
-- Budget
-- ----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS budget_allocations (
  id varchar(36) PRIMARY KEY,
  project_id varchar(36) NOT NULL,
  fiscal_year int NOT NULL,
  category varchar(80) NOT NULL,
  allocated_amount decimal(18,2) NOT NULL DEFAULT 0,
  spent_amount decimal(18,2) NOT NULL DEFAULT 0,
  notes text,
  created_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_alloc_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS expenses (
  id varchar(36) PRIMARY KEY,
  project_id varchar(36) NOT NULL,
  allocation_id varchar(36),
  category varchar(80) NOT NULL,
  amount decimal(18,2) NOT NULL,
  expense_date date NOT NULL,
  description varchar(400),
  recorded_by_user_id varchar(36),
  created_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_expense_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
  CONSTRAINT fk_expense_alloc FOREIGN KEY (allocation_id) REFERENCES budget_allocations(id)
);

-- ----------------------------------------------------------------------------
-- Notifications & audit
-- ----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS notifications (
  id varchar(36) PRIMARY KEY,
  recipient_user_id varchar(36) NOT NULL,
  category varchar(40) NOT NULL,
  title varchar(200) NOT NULL,
  body text,
  link_url varchar(400),
  read_flag tinyint(1) NOT NULL DEFAULT 0,
  created_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_notify_user FOREIGN KEY (recipient_user_id) REFERENCES user_accounts(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS audit_logs (
  id varchar(36) PRIMARY KEY,
  occurred_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  user_id varchar(36),
  user_email varchar(200),
  action varchar(80) NOT NULL,
  target_type varchar(80),
  target_id varchar(120),
  http_method varchar(10),
  request_uri varchar(400),
  ip_address varchar(60),
  status_code int,
  metadata text
);

-- ----------------------------------------------------------------------------
-- Indexes
-- ----------------------------------------------------------------------------

CREATE INDEX idx_users_email ON user_accounts(email);
CREATE INDEX idx_projects_department_status ON projects(department_id, status);
CREATE INDEX idx_projects_status ON projects(status);
CREATE INDEX idx_tasks_project ON tasks(project_id);
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_user ON tasks(assigned_to_user_id);
CREATE INDEX idx_documents_status ON documents(status);
CREATE INDEX idx_documents_project ON documents(project_id);
CREATE INDEX idx_versions_document ON document_versions(document_id);
CREATE INDEX idx_equipment_status ON equipment(status);
CREATE INDEX idx_bookings_range ON equipment_bookings(equipment_id, start_time, end_time);
CREATE INDEX idx_items_warehouse ON inventory_items(warehouse_id);
CREATE INDEX idx_movements_item ON stock_movements(item_id);
CREATE INDEX idx_pr_status ON purchase_requests(status);
CREATE INDEX idx_po_status ON purchase_orders(status);
CREATE INDEX idx_alloc_project ON budget_allocations(project_id);
CREATE INDEX idx_expenses_project ON expenses(project_id);
CREATE INDEX idx_notifications_recipient ON notifications(recipient_user_id, read_flag);
CREATE INDEX idx_audit_user ON audit_logs(user_id);
CREATE INDEX idx_audit_ts ON audit_logs(occurred_at);
CREATE INDEX idx_audit_target ON audit_logs(target_type, target_id);

-- ----------------------------------------------------------------------------
-- Seed data (idempotent — safe to re-run)
-- ----------------------------------------------------------------------------

INSERT IGNORE INTO roles (id, name, description) VALUES
(UUID(), 'ADMINISTRATOR', 'Full platform administration'),
(UUID(), 'PROJECT_DIRECTOR', 'Project approval and delivery governance'),
(UUID(), 'DEPARTMENT_HEAD', 'Department governance'),
(UUID(), 'SCIENTIST', 'Research execution'),
(UUID(), 'RESEARCH_ENGINEER', 'Engineering research support'),
(UUID(), 'LABORATORY_MANAGER', 'Laboratory operations'),
(UUID(), 'FINANCE_OFFICER', 'Budget and expense management'),
(UUID(), 'PROCUREMENT_OFFICER', 'Procurement workflows'),
(UUID(), 'DOCUMENT_CONTROLLER', 'Document lifecycle control'),
(UUID(), 'AUDITOR', 'Audit and compliance review'),
(UUID(), 'GUEST', 'Limited platform access');

SET FOREIGN_KEY_CHECKS = 1;
