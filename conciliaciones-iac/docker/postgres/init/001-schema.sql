-- =========================================================
-- PROYECTO: RECONCILIACION / LIQUIDACION DE COMISIONES
-- MOTOR: PostgreSQL
-- VERSION AJUSTADA CON TABLA PARAMETER
-- CONVENCION:
--   name  = codigo tecnico
--   value = texto visible en UI
-- =========================================================

CREATE SCHEMA IF NOT EXISTS reconciliation;
SET search_path TO reconciliation;

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- =========================================================
-- 1. TABLA GENERAL DE PARAMETROS
-- =========================================================
CREATE TABLE IF NOT EXISTS parameter (
    id                  BIGSERIAL PRIMARY KEY,
    name                VARCHAR(150) NOT NULL,
    description         VARCHAR(500),
    value               VARCHAR(150) NOT NULL,
    parameter_group     VARCHAR(100) NOT NULL,
    active              BOOLEAN NOT NULL DEFAULT TRUE,
    sort_order          INTEGER NOT NULL DEFAULT 0,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by          VARCHAR(100),
    updated_at          TIMESTAMP,
    updated_by          VARCHAR(100),
    CONSTRAINT uq_parameter_group_name UNIQUE (parameter_group, name),
    CONSTRAINT uq_parameter_group_value UNIQUE (parameter_group, value)
);

CREATE INDEX IF NOT EXISTS idx_parameter_group
    ON parameter(parameter_group);

CREATE INDEX IF NOT EXISTS idx_parameter_group_active
    ON parameter(parameter_group, active);

CREATE INDEX IF NOT EXISTS idx_parameter_group_name
    ON parameter(parameter_group, name);

-- =========================================================
-- 2. TABLAS MAESTRAS
-- =========================================================

CREATE TABLE IF NOT EXISTS carrier (
    id                  BIGSERIAL PRIMARY KEY,
    code                VARCHAR(50) NOT NULL,
    name                VARCHAR(150) NOT NULL,
    description         VARCHAR(500),
    active              BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by          VARCHAR(100),
    updated_at          TIMESTAMP,
    updated_by          VARCHAR(100),
    CONSTRAINT uq_carrier_code UNIQUE (code),
    CONSTRAINT uq_carrier_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS agency (
    id                  BIGSERIAL PRIMARY KEY,
    carrier_id          BIGINT,
    external_agency_id  VARCHAR(100),
    name                VARCHAR(200) NOT NULL,
    active              BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by          VARCHAR(100),
    updated_at          TIMESTAMP,
    updated_by          VARCHAR(100),
    CONSTRAINT fk_agency_carrier
        FOREIGN KEY (carrier_id) REFERENCES carrier(id),
    CONSTRAINT uq_agency_carrier_name UNIQUE (carrier_id, name)
);

CREATE TABLE IF NOT EXISTS producer (
    id                   BIGSERIAL PRIMARY KEY,
    agency_id            BIGINT,
    external_producer_id VARCHAR(100),
    first_name           VARCHAR(100),
    last_name            VARCHAR(100),
    full_name            VARCHAR(250) NOT NULL,
    email                VARCHAR(150),
    phone                VARCHAR(50),
    npn                  VARCHAR(100),
    tax_id_masked        VARCHAR(50),
    active               BOOLEAN NOT NULL DEFAULT TRUE,
    created_at           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by           VARCHAR(100),
    updated_at           TIMESTAMP,
    updated_by           VARCHAR(100),
    CONSTRAINT fk_producer_agency
        FOREIGN KEY (agency_id) REFERENCES agency(id),
    CONSTRAINT uq_producer_external_id UNIQUE (external_producer_id)
);

CREATE TABLE IF NOT EXISTS client (
    id                  BIGSERIAL PRIMARY KEY,
    external_client_id  VARCHAR(100),
    first_name          VARCHAR(100),
    middle_name         VARCHAR(100),
    last_name           VARCHAR(100),
    full_name           VARCHAR(250) NOT NULL,
    birth_date          DATE,
    state               VARCHAR(100),
    active              BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by          VARCHAR(100),
    updated_at          TIMESTAMP,
    updated_by          VARCHAR(100)
);

-- =========================================================
-- 3. ARCHIVOS E INGESTA
-- =========================================================

CREATE TABLE IF NOT EXISTS source_file (
    id                   BIGSERIAL PRIMARY KEY,
    carrier_id           BIGINT NOT NULL,
    original_file_name   VARCHAR(255) NOT NULL,
    stored_file_name     VARCHAR(255),
    file_extension       VARCHAR(20),
    mime_type            VARCHAR(100),
    file_size_bytes      BIGINT,
    s3_bucket            VARCHAR(255),
    s3_key               VARCHAR(500),
    checksum_sha256      VARCHAR(128),
    source_system        VARCHAR(100),
    upload_date          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    uploaded_by          VARCHAR(100),
    processing_status_id BIGINT NOT NULL,
    error_message        TEXT,
    total_rows           INTEGER,
    processed_rows       INTEGER,
    failed_rows          INTEGER,
    created_at           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by           VARCHAR(100),
    updated_at           TIMESTAMP,
    updated_by           VARCHAR(100),
    CONSTRAINT fk_source_file_carrier
        FOREIGN KEY (carrier_id) REFERENCES carrier(id),
    CONSTRAINT fk_source_file_processing_status
        FOREIGN KEY (processing_status_id) REFERENCES parameter(id)
);

CREATE TABLE IF NOT EXISTS source_file_sheet (
    id                  BIGSERIAL PRIMARY KEY,
    source_file_id      BIGINT NOT NULL,
    sheet_name          VARCHAR(255) NOT NULL,
    sheet_order         INTEGER NOT NULL,
    total_rows          INTEGER,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by          VARCHAR(100),
    CONSTRAINT fk_source_file_sheet_file
        FOREIGN KEY (source_file_id) REFERENCES source_file(id) ON DELETE CASCADE,
    CONSTRAINT uq_source_file_sheet UNIQUE (source_file_id, sheet_name)
);

-- =========================================================
-- 4. RAW DATA
-- =========================================================

CREATE TABLE IF NOT EXISTS raw_import_record (
    id                   BIGSERIAL PRIMARY KEY,
    source_file_id       BIGINT NOT NULL,
    source_file_sheet_id BIGINT,
    row_number           INTEGER NOT NULL,
    source_row_key       VARCHAR(255),
    raw_payload          JSONB NOT NULL,
    parse_status_id      BIGINT,
    parse_error          TEXT,
    created_at           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by           VARCHAR(100),
    CONSTRAINT fk_raw_import_record_file
        FOREIGN KEY (source_file_id) REFERENCES source_file(id) ON DELETE CASCADE,
    CONSTRAINT fk_raw_import_record_sheet
        FOREIGN KEY (source_file_sheet_id) REFERENCES source_file_sheet(id) ON DELETE CASCADE,
    CONSTRAINT fk_raw_import_record_parse_status
        FOREIGN KEY (parse_status_id) REFERENCES parameter(id),
    CONSTRAINT uq_raw_import_record UNIQUE (source_file_id, row_number)
);

-- =========================================================
-- 5. POLIZAS Y MODELO CANONICO
-- =========================================================

CREATE TABLE IF NOT EXISTS policy (
    id                  BIGSERIAL PRIMARY KEY,
    carrier_id          BIGINT NOT NULL,
    client_id           BIGINT,
    policy_number       VARCHAR(150),
    subscriber_id       VARCHAR(150),
    effective_date      DATE,
    issue_date          DATE,
    termination_date    DATE,
    status_id           BIGINT NOT NULL,
    resident_state      VARCHAR(100),
    issue_state         VARCHAR(100),
    members_count       INTEGER,
    source_key          VARCHAR(255),
    active              BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by          VARCHAR(100),
    updated_at          TIMESTAMP,
    updated_by          VARCHAR(100),
    CONSTRAINT fk_policy_carrier
        FOREIGN KEY (carrier_id) REFERENCES carrier(id),
    CONSTRAINT fk_policy_client
        FOREIGN KEY (client_id) REFERENCES client(id),
    CONSTRAINT fk_policy_status
        FOREIGN KEY (status_id) REFERENCES parameter(id),
    CONSTRAINT uq_policy_carrier_number UNIQUE (carrier_id, policy_number)
);

CREATE TABLE IF NOT EXISTS policy_plan (
    id                  BIGSERIAL PRIMARY KEY,
    policy_id           BIGINT NOT NULL,
    plan_name           VARCHAR(200),
    base_medical_plan   VARCHAR(200),
    coverage_plan_name  VARCHAR(200),
    payment_type        VARCHAR(100),
    policy_mode         VARCHAR(100),
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by          VARCHAR(100),
    updated_at          TIMESTAMP,
    updated_by          VARCHAR(100),
    CONSTRAINT fk_policy_plan_policy
        FOREIGN KEY (policy_id) REFERENCES policy(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS policy_status_history (
    id                  BIGSERIAL PRIMARY KEY,
    policy_id           BIGINT NOT NULL,
    source_file_id      BIGINT,
    status_id           BIGINT NOT NULL,
    effective_from      DATE NOT NULL,
    effective_to        DATE,
    notes               VARCHAR(1000),
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by          VARCHAR(100),
    CONSTRAINT fk_policy_status_history_policy
        FOREIGN KEY (policy_id) REFERENCES policy(id) ON DELETE CASCADE,
    CONSTRAINT fk_policy_status_history_file
        FOREIGN KEY (source_file_id) REFERENCES source_file(id),
    CONSTRAINT fk_policy_status_history_status
        FOREIGN KEY (status_id) REFERENCES parameter(id)
);

-- =========================================================
-- 6. ASIGNACION DE COMISIONES
-- =========================================================

CREATE TABLE IF NOT EXISTS commission_assignment (
    id                  BIGSERIAL PRIMARY KEY,
    policy_id           BIGINT NOT NULL,
    producer_id         BIGINT NOT NULL,
    role_id             BIGINT NOT NULL,
    split_percentage    NUMERIC(7,4),
    valid_from          DATE NOT NULL,
    valid_to            DATE,
    active              BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by          VARCHAR(100),
    updated_at          TIMESTAMP,
    updated_by          VARCHAR(100),
    CONSTRAINT fk_commission_assignment_policy
        FOREIGN KEY (policy_id) REFERENCES policy(id) ON DELETE CASCADE,
    CONSTRAINT fk_commission_assignment_producer
        FOREIGN KEY (producer_id) REFERENCES producer(id),
    CONSTRAINT fk_commission_assignment_role
        FOREIGN KEY (role_id) REFERENCES parameter(id),
    CONSTRAINT ck_commission_assignment_split
        CHECK (split_percentage IS NULL OR (split_percentage >= 0 AND split_percentage <= 100))
);

-- =========================================================
-- 7. REGISTROS DE COMISION / NORMALIZADOS
-- =========================================================

CREATE TABLE IF NOT EXISTS commission_statement (
    id                   BIGSERIAL PRIMARY KEY,
    source_file_id       BIGINT NOT NULL,
    raw_import_record_id BIGINT,
    carrier_id           BIGINT NOT NULL,
    agency_id            BIGINT,
    producer_id          BIGINT,
    client_id            BIGINT,
    policy_id            BIGINT,
    statement_date       DATE,
    check_date           DATE,
    paid_date            DATE,
    last_pay_date        DATE,
    transaction_date     DATE,
    invoice_number       VARCHAR(100),
    row_identifier       VARCHAR(255),
    raw_status_id        BIGINT,
    reason_code_id       BIGINT,
    source_sheet_name    VARCHAR(255),
    source_row_number    INTEGER,
    created_at           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by           VARCHAR(100),
    updated_at           TIMESTAMP,
    updated_by           VARCHAR(100),
    CONSTRAINT fk_commission_statement_source_file
        FOREIGN KEY (source_file_id) REFERENCES source_file(id) ON DELETE CASCADE,
    CONSTRAINT fk_commission_statement_raw_import
        FOREIGN KEY (raw_import_record_id) REFERENCES raw_import_record(id) ON DELETE SET NULL,
    CONSTRAINT fk_commission_statement_carrier
        FOREIGN KEY (carrier_id) REFERENCES carrier(id),
    CONSTRAINT fk_commission_statement_agency
        FOREIGN KEY (agency_id) REFERENCES agency(id),
    CONSTRAINT fk_commission_statement_producer
        FOREIGN KEY (producer_id) REFERENCES producer(id),
    CONSTRAINT fk_commission_statement_client
        FOREIGN KEY (client_id) REFERENCES client(id),
    CONSTRAINT fk_commission_statement_policy
        FOREIGN KEY (policy_id) REFERENCES policy(id),
    CONSTRAINT fk_commission_statement_raw_status
        FOREIGN KEY (raw_status_id) REFERENCES parameter(id),
    CONSTRAINT fk_commission_statement_reason_code
        FOREIGN KEY (reason_code_id) REFERENCES parameter(id)
);

CREATE TABLE IF NOT EXISTS commission_statement_item (
    id                      BIGSERIAL PRIMARY KEY,
    commission_statement_id BIGINT NOT NULL,
    gross_premium           NUMERIC(18,2),
    commissionable_premium  NUMERIC(18,2),
    premium                 NUMERIC(18,2),
    net_amount              NUMERIC(18,2),
    rate                    NUMERIC(10,4),
    commission_rate_pct     NUMERIC(7,4),
    commission_amount       NUMERIC(18,2),
    subtotal                NUMERIC(18,2),
    month_amount            NUMERIC(18,2),
    applicants              INTEGER,
    contract_count          INTEGER,
    retro_count             INTEGER,
    vip_count               INTEGER,
    is_initial_premium      BOOLEAN,
    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(100),
    updated_at              TIMESTAMP,
    updated_by              VARCHAR(100),
    CONSTRAINT fk_commission_statement_item_statement
        FOREIGN KEY (commission_statement_id) REFERENCES commission_statement(id) ON DELETE CASCADE
);

-- =========================================================
-- 8. REGLAS
-- =========================================================

CREATE TABLE IF NOT EXISTS commission_rule (
    id                  BIGSERIAL PRIMARY KEY,
    carrier_id          BIGINT,
    role_id             BIGINT,
    product_type        VARCHAR(100),
    state               VARCHAR(100),
    rule_name           VARCHAR(150) NOT NULL,
    rule_type_id        BIGINT NOT NULL,
    rate                NUMERIC(10,4),
    fixed_amount        NUMERIC(18,2),
    priority_order      INTEGER NOT NULL DEFAULT 1,
    applies_from        DATE NOT NULL,
    applies_to          DATE,
    active              BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by          VARCHAR(100),
    updated_at          TIMESTAMP,
    updated_by          VARCHAR(100),
    CONSTRAINT fk_commission_rule_carrier
        FOREIGN KEY (carrier_id) REFERENCES carrier(id),
    CONSTRAINT fk_commission_rule_role
        FOREIGN KEY (role_id) REFERENCES parameter(id),
    CONSTRAINT fk_commission_rule_type
        FOREIGN KEY (rule_type_id) REFERENCES parameter(id)
);

-- =========================================================
-- 9. CASOS DE RECONCILIACION
-- =========================================================

CREATE TABLE IF NOT EXISTS reconciliation_case (
    id                           BIGSERIAL PRIMARY KEY,
    source_file_id               BIGINT NOT NULL,
    commission_statement_id      BIGINT,
    commission_statement_item_id BIGINT,
    carrier_id                   BIGINT NOT NULL,
    policy_id                    BIGINT,
    producer_id                  BIGINT,
    case_type_id                 BIGINT NOT NULL,
    severity_id                  BIGINT NOT NULL,
    status_id                    BIGINT NOT NULL,
    detected_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    description                  TEXT NOT NULL,
    suggested_action             VARCHAR(1000),
    resolution_notes             TEXT,
    resolved_at                  TIMESTAMP,
    resolved_by                  VARCHAR(100),
    created_at                   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by                   VARCHAR(100),
    updated_at                   TIMESTAMP,
    updated_by                   VARCHAR(100),
    CONSTRAINT fk_reconciliation_case_source_file
        FOREIGN KEY (source_file_id) REFERENCES source_file(id),
    CONSTRAINT fk_reconciliation_case_statement
        FOREIGN KEY (commission_statement_id) REFERENCES commission_statement(id) ON DELETE SET NULL,
    CONSTRAINT fk_reconciliation_case_statement_item
        FOREIGN KEY (commission_statement_item_id) REFERENCES commission_statement_item(id) ON DELETE SET NULL,
    CONSTRAINT fk_reconciliation_case_carrier
        FOREIGN KEY (carrier_id) REFERENCES carrier(id),
    CONSTRAINT fk_reconciliation_case_policy
        FOREIGN KEY (policy_id) REFERENCES policy(id),
    CONSTRAINT fk_reconciliation_case_producer
        FOREIGN KEY (producer_id) REFERENCES producer(id),
    CONSTRAINT fk_reconciliation_case_type
        FOREIGN KEY (case_type_id) REFERENCES parameter(id),
    CONSTRAINT fk_reconciliation_case_severity
        FOREIGN KEY (severity_id) REFERENCES parameter(id),
    CONSTRAINT fk_reconciliation_case_status
        FOREIGN KEY (status_id) REFERENCES parameter(id)
);

-- =========================================================
-- 10. LIQUIDACION / PAGOS
-- =========================================================

CREATE TABLE IF NOT EXISTS commission_payment (
    id                  BIGSERIAL PRIMARY KEY,
    producer_id         BIGINT NOT NULL,
    period_year         INTEGER NOT NULL,
    period_month        INTEGER NOT NULL,
    total_gross         NUMERIC(18,2) NOT NULL DEFAULT 0,
    total_adjustments   NUMERIC(18,2) NOT NULL DEFAULT 0,
    total_payable       NUMERIC(18,2) NOT NULL DEFAULT 0,
    status_id           BIGINT NOT NULL,
    generated_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    approved_at         TIMESTAMP,
    approved_by         VARCHAR(100),
    paid_at             TIMESTAMP,
    payment_reference   VARCHAR(100),
    notes               TEXT,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by          VARCHAR(100),
    updated_at          TIMESTAMP,
    updated_by          VARCHAR(100),
    CONSTRAINT fk_commission_payment_producer
        FOREIGN KEY (producer_id) REFERENCES producer(id),
    CONSTRAINT fk_commission_payment_status
        FOREIGN KEY (status_id) REFERENCES parameter(id),
    CONSTRAINT uq_commission_payment_period UNIQUE (producer_id, period_year, period_month),
    CONSTRAINT ck_commission_payment_month CHECK (period_month BETWEEN 1 AND 12)
);

CREATE TABLE IF NOT EXISTS commission_payment_detail (
    id                           BIGSERIAL PRIMARY KEY,
    commission_payment_id        BIGINT NOT NULL,
    policy_id                    BIGINT,
    commission_statement_item_id BIGINT,
    reconciliation_case_id       BIGINT,
    concept                      VARCHAR(255) NOT NULL,
    amount                       NUMERIC(18,2) NOT NULL,
    included_for_payment         BOOLEAN NOT NULL DEFAULT TRUE,
    exclusion_reason             VARCHAR(1000),
    created_at                   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by                   VARCHAR(100),
    updated_at                   TIMESTAMP,
    updated_by                   VARCHAR(100),
    CONSTRAINT fk_commission_payment_detail_payment
        FOREIGN KEY (commission_payment_id) REFERENCES commission_payment(id) ON DELETE CASCADE,
    CONSTRAINT fk_commission_payment_detail_policy
        FOREIGN KEY (policy_id) REFERENCES policy(id),
    CONSTRAINT fk_commission_payment_detail_statement_item
        FOREIGN KEY (commission_statement_item_id) REFERENCES commission_statement_item(id),
    CONSTRAINT fk_commission_payment_detail_case
        FOREIGN KEY (reconciliation_case_id) REFERENCES reconciliation_case(id)
);

-- =========================================================
-- 11. AUDITORIA
-- =========================================================

CREATE TABLE IF NOT EXISTS audit_log (
    id                  BIGSERIAL PRIMARY KEY,
    entity_name         VARCHAR(150) NOT NULL,
    entity_id           VARCHAR(100) NOT NULL,
    action_id           BIGINT NOT NULL,
    username            VARCHAR(150),
    event_timestamp     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    old_values          JSONB,
    new_values          JSONB,
    details             TEXT,
    CONSTRAINT fk_audit_log_action
        FOREIGN KEY (action_id) REFERENCES parameter(id)
);

-- =========================================================
-- 12. INDICES
-- =========================================================

CREATE INDEX IF NOT EXISTS idx_agency_carrier_id
    ON agency(carrier_id);

CREATE INDEX IF NOT EXISTS idx_producer_agency_id
    ON producer(agency_id);

CREATE INDEX IF NOT EXISTS idx_client_full_name
    ON client(full_name);

CREATE INDEX IF NOT EXISTS idx_source_file_carrier_id
    ON source_file(carrier_id);

CREATE INDEX IF NOT EXISTS idx_source_file_processing_status_id
    ON source_file(processing_status_id);

CREATE INDEX IF NOT EXISTS idx_raw_import_record_file_id
    ON raw_import_record(source_file_id);

CREATE INDEX IF NOT EXISTS idx_raw_import_record_sheet_id
    ON raw_import_record(source_file_sheet_id);

CREATE INDEX IF NOT EXISTS idx_raw_import_record_parse_status_id
    ON raw_import_record(parse_status_id);

CREATE INDEX IF NOT EXISTS idx_raw_import_record_payload_gin
    ON raw_import_record USING GIN(raw_payload);

CREATE INDEX IF NOT EXISTS idx_policy_carrier_id
    ON policy(carrier_id);

CREATE INDEX IF NOT EXISTS idx_policy_client_id
    ON policy(client_id);

CREATE INDEX IF NOT EXISTS idx_policy_subscriber_id
    ON policy(subscriber_id);

CREATE INDEX IF NOT EXISTS idx_policy_status_id
    ON policy(status_id);

CREATE INDEX IF NOT EXISTS idx_policy_effective_date
    ON policy(effective_date);

CREATE INDEX IF NOT EXISTS idx_policy_status_history_policy_id
    ON policy_status_history(policy_id);

CREATE INDEX IF NOT EXISTS idx_policy_status_history_status_id
    ON policy_status_history(status_id);

CREATE INDEX IF NOT EXISTS idx_commission_assignment_policy_id
    ON commission_assignment(policy_id);

CREATE INDEX IF NOT EXISTS idx_commission_assignment_producer_id
    ON commission_assignment(producer_id);

CREATE INDEX IF NOT EXISTS idx_commission_assignment_role_id
    ON commission_assignment(role_id);

CREATE INDEX IF NOT EXISTS idx_commission_statement_source_file_id
    ON commission_statement(source_file_id);

CREATE INDEX IF NOT EXISTS idx_commission_statement_policy_id
    ON commission_statement(policy_id);

CREATE INDEX IF NOT EXISTS idx_commission_statement_producer_id
    ON commission_statement(producer_id);

CREATE INDEX IF NOT EXISTS idx_commission_statement_paid_date
    ON commission_statement(paid_date);

CREATE INDEX IF NOT EXISTS idx_commission_statement_invoice
    ON commission_statement(invoice_number);

CREATE INDEX IF NOT EXISTS idx_commission_statement_raw_status_id
    ON commission_statement(raw_status_id);

CREATE INDEX IF NOT EXISTS idx_commission_statement_reason_code_id
    ON commission_statement(reason_code_id);

CREATE INDEX IF NOT EXISTS idx_commission_statement_item_statement_id
    ON commission_statement_item(commission_statement_id);

CREATE INDEX IF NOT EXISTS idx_commission_rule_carrier_id
    ON commission_rule(carrier_id);

CREATE INDEX IF NOT EXISTS idx_commission_rule_role_id
    ON commission_rule(role_id);

CREATE INDEX IF NOT EXISTS idx_commission_rule_type_id
    ON commission_rule(rule_type_id);

CREATE INDEX IF NOT EXISTS idx_reconciliation_case_source_file_id
    ON reconciliation_case(source_file_id);

CREATE INDEX IF NOT EXISTS idx_reconciliation_case_policy_id
    ON reconciliation_case(policy_id);

CREATE INDEX IF NOT EXISTS idx_reconciliation_case_producer_id
    ON reconciliation_case(producer_id);

CREATE INDEX IF NOT EXISTS idx_reconciliation_case_case_type_id
    ON reconciliation_case(case_type_id);

CREATE INDEX IF NOT EXISTS idx_reconciliation_case_severity_id
    ON reconciliation_case(severity_id);

CREATE INDEX IF NOT EXISTS idx_reconciliation_case_status_id
    ON reconciliation_case(status_id);

CREATE INDEX IF NOT EXISTS idx_commission_payment_producer_id
    ON commission_payment(producer_id);

CREATE INDEX IF NOT EXISTS idx_commission_payment_status_id
    ON commission_payment(status_id);

CREATE INDEX IF NOT EXISTS idx_commission_payment_detail_payment_id
    ON commission_payment_detail(commission_payment_id);

CREATE INDEX IF NOT EXISTS idx_audit_log_entity_name
    ON audit_log(entity_name);

CREATE INDEX IF NOT EXISTS idx_audit_log_event_timestamp
    ON audit_log(event_timestamp);

CREATE INDEX IF NOT EXISTS idx_audit_log_action_id
    ON audit_log(action_id);

-- =========================================================
-- 13. DATOS SEMILLA - CARRIER
-- =========================================================

INSERT INTO carrier (code, name, description, created_by)
VALUES
    ('ELITE', 'ELITE', 'Fuente de datos ELITE', 'system'),
    ('SENTARA', 'SENTARA', 'Fuente de datos SENTARA', 'system'),
    ('SAFEGUARD', 'SAFEGUARD', 'Fuente de datos SAFEGUARD', 'system'),
    ('UNITED_H_ONE', 'UNITED H ONE', 'Fuente de datos UNITED H ONE', 'system')
ON CONFLICT (code) DO NOTHING;

-- =========================================================
-- 14. DATOS SEMILLA - PARAMETER
-- name  = codigo tecnico
-- value = texto visible
-- =========================================================

-- GROUP: SOURCE_FILE_STATUS
INSERT INTO parameter (name, description, value, parameter_group, active, sort_order, created_by)
VALUES
    ('UPLOADED', 'Archivo cargado y pendiente de procesamiento', 'Subido', 'SOURCE_FILE_STATUS', TRUE, 1, 'system'),
    ('PROCESSING', 'Archivo en procesamiento', 'Procesando', 'SOURCE_FILE_STATUS', TRUE, 2, 'system'),
    ('PROCESSED', 'Archivo procesado correctamente', 'Procesado', 'SOURCE_FILE_STATUS', TRUE, 3, 'system'),
    ('FAILED', 'Archivo con error en procesamiento', 'Fallido', 'SOURCE_FILE_STATUS', TRUE, 4, 'system'),
    ('PARTIALLY_PROCESSED', 'Archivo procesado parcialmente', 'Procesado Parcialmente', 'SOURCE_FILE_STATUS', TRUE, 5, 'system')
ON CONFLICT (parameter_group, name) DO NOTHING;

-- GROUP: PARSE_STATUS
INSERT INTO parameter (name, description, value, parameter_group, active, sort_order, created_by)
VALUES
    ('PENDING', 'Fila pendiente de parseo', 'Pendiente', 'PARSE_STATUS', TRUE, 1, 'system'),
    ('PARSED', 'Fila parseada correctamente', 'Parseado', 'PARSE_STATUS', TRUE, 2, 'system'),
    ('FAILED', 'Error en el parseo de la fila', 'Fallido', 'PARSE_STATUS', TRUE, 3, 'system')
ON CONFLICT (parameter_group, name) DO NOTHING;

-- GROUP: POLICY_STATUS
INSERT INTO parameter (name, description, value, parameter_group, active, sort_order, created_by)
VALUES
    ('PENDING', 'Póliza pendiente', 'Pendiente', 'POLICY_STATUS', TRUE, 1, 'system'),
    ('ACTIVE', 'Póliza activa', 'Activa', 'POLICY_STATUS', TRUE, 2, 'system'),
    ('APPROVED', 'Póliza aprobada', 'Aprobada', 'POLICY_STATUS', TRUE, 3, 'system'),
    ('CANCELLED', 'Póliza cancelada', 'Cancelada', 'POLICY_STATUS', TRUE, 4, 'system'),
    ('TERMINATED', 'Póliza terminada', 'Terminada', 'POLICY_STATUS', TRUE, 5, 'system'),
    ('NOT_ASSIGNED', 'Póliza sin asignación', 'No Asignada', 'POLICY_STATUS', TRUE, 6, 'system'),
    ('UNKNOWN', 'Estado desconocido', 'Desconocido', 'POLICY_STATUS', TRUE, 7, 'system')
ON CONFLICT (parameter_group, name) DO NOTHING;

-- GROUP: PRODUCER_ROLE_TYPE
INSERT INTO parameter (name, description, value, parameter_group, active, sort_order, created_by)
VALUES
    ('PRIMARY_AGENT', 'Agente principal de la póliza', 'Agente Principal', 'PRODUCER_ROLE_TYPE', TRUE, 1, 'system'),
    ('SUB_AGENT', 'Subagente asociado', 'Subagente', 'PRODUCER_ROLE_TYPE', TRUE, 2, 'system'),
    ('MANAGER', 'Manager o supervisor', 'Manager', 'PRODUCER_ROLE_TYPE', TRUE, 3, 'system'),
    ('AGENCY', 'Pago o rol asociado a agencia', 'Agencia', 'PRODUCER_ROLE_TYPE', TRUE, 4, 'system'),
    ('PAYEE', 'Payee del carrier', 'Beneficiario de Pago', 'PRODUCER_ROLE_TYPE', TRUE, 5, 'system'),
    ('UNKNOWN', 'Rol no determinado', 'Desconocido', 'PRODUCER_ROLE_TYPE', TRUE, 6, 'system')
ON CONFLICT (parameter_group, name) DO NOTHING;

-- GROUP: COMMISSION_RULE_TYPE
INSERT INTO parameter (name, description, value, parameter_group, active, sort_order, created_by)
VALUES
    ('FIXED_AMOUNT', 'Regla por monto fijo', 'Monto Fijo', 'COMMISSION_RULE_TYPE', TRUE, 1, 'system'),
    ('PERCENTAGE', 'Regla por porcentaje', 'Porcentaje', 'COMMISSION_RULE_TYPE', TRUE, 2, 'system'),
    ('SPLIT_PERCENTAGE', 'Distribución porcentual', 'Split Porcentaje', 'COMMISSION_RULE_TYPE', TRUE, 3, 'system'),
    ('VALIDATION_RULE', 'Regla de validación', 'Regla de Validación', 'COMMISSION_RULE_TYPE', TRUE, 4, 'system'),
    ('EXCLUSION_RULE', 'Regla de exclusión', 'Regla de Exclusión', 'COMMISSION_RULE_TYPE', TRUE, 5, 'system')
ON CONFLICT (parameter_group, name) DO NOTHING;

-- GROUP: RECONCILIATION_CASE_TYPE
INSERT INTO parameter (name, description, value, parameter_group, active, sort_order, created_by)
VALUES
    ('PAYMENT_ON_CANCELLED_POLICY', 'Se detectó pago a póliza cancelada', 'Pago sobre Póliza Cancelada', 'RECONCILIATION_CASE_TYPE', TRUE, 1, 'system'),
    ('PAYMENT_WITHOUT_ASSIGNMENT', 'No existe asignación válida para pagar', 'Pago sin Asignación', 'RECONCILIATION_CASE_TYPE', TRUE, 2, 'system'),
    ('DUPLICATE_COMMISSION', 'Registro de comisión duplicado', 'Comisión Duplicada', 'RECONCILIATION_CASE_TYPE', TRUE, 3, 'system'),
    ('MISSING_POLICY', 'No se encontró la póliza', 'Póliza Faltante', 'RECONCILIATION_CASE_TYPE', TRUE, 4, 'system'),
    ('AMOUNT_MISMATCH', 'Monto inconsistente', 'Diferencia de Monto', 'RECONCILIATION_CASE_TYPE', TRUE, 5, 'system'),
    ('MISSING_PRODUCER', 'No se encontró productor', 'Productor Faltante', 'RECONCILIATION_CASE_TYPE', TRUE, 6, 'system'),
    ('SPLIT_INCONSISTENCY', 'Split de pago inconsistente', 'Inconsistencia de Split', 'RECONCILIATION_CASE_TYPE', TRUE, 7, 'system'),
    ('UNKNOWN_STATUS', 'Estado no homologado o desconocido', 'Estado Desconocido', 'RECONCILIATION_CASE_TYPE', TRUE, 8, 'system'),
    ('DUPLICATE_SOURCE_ROW', 'La fila de origen ya fue importada', 'Fila de Origen Duplicada', 'RECONCILIATION_CASE_TYPE', TRUE, 9, 'system'),
    ('INVALID_DATE_RANGE', 'Rango de fechas inconsistente', 'Rango de Fechas Inválido', 'RECONCILIATION_CASE_TYPE', TRUE, 10, 'system')
ON CONFLICT (parameter_group, name) DO NOTHING;

-- GROUP: SEVERITY_LEVEL
INSERT INTO parameter (name, description, value, parameter_group, active, sort_order, created_by)
VALUES
    ('LOW', 'Severidad baja', 'Baja', 'SEVERITY_LEVEL', TRUE, 1, 'system'),
    ('MEDIUM', 'Severidad media', 'Media', 'SEVERITY_LEVEL', TRUE, 2, 'system'),
    ('HIGH', 'Severidad alta', 'Alta', 'SEVERITY_LEVEL', TRUE, 3, 'system'),
    ('CRITICAL', 'Severidad crítica', 'Crítica', 'SEVERITY_LEVEL', TRUE, 4, 'system')
ON CONFLICT (parameter_group, name) DO NOTHING;

-- GROUP: CASE_STATUS
INSERT INTO parameter (name, description, value, parameter_group, active, sort_order, created_by)
VALUES
    ('OPEN', 'Caso abierto', 'Abierto', 'CASE_STATUS', TRUE, 1, 'system'),
    ('IN_REVIEW', 'Caso en revisión', 'En Revisión', 'CASE_STATUS', TRUE, 2, 'system'),
    ('RESOLVED', 'Caso resuelto', 'Resuelto', 'CASE_STATUS', TRUE, 3, 'system'),
    ('DISMISSED', 'Caso descartado', 'Descartado', 'CASE_STATUS', TRUE, 4, 'system')
ON CONFLICT (parameter_group, name) DO NOTHING;

-- GROUP: PAYMENT_STATUS
INSERT INTO parameter (name, description, value, parameter_group, active, sort_order, created_by)
VALUES
    ('DRAFT', 'Liquidación en borrador', 'Borrador', 'PAYMENT_STATUS', TRUE, 1, 'system'),
    ('GENERATED', 'Pago generado', 'Generado', 'PAYMENT_STATUS', TRUE, 2, 'system'),
    ('APPROVED', 'Pago aprobado', 'Aprobado', 'PAYMENT_STATUS', TRUE, 3, 'system'),
    ('REJECTED', 'Pago rechazado', 'Rechazado', 'PAYMENT_STATUS', TRUE, 4, 'system'),
    ('PAID', 'Pago realizado', 'Pagado', 'PAYMENT_STATUS', TRUE, 5, 'system'),
    ('CANCELLED', 'Pago cancelado', 'Cancelado', 'PAYMENT_STATUS', TRUE, 6, 'system')
ON CONFLICT (parameter_group, name) DO NOTHING;

-- GROUP: AUDIT_ACTION_TYPE
INSERT INTO parameter (name, description, value, parameter_group, active, sort_order, created_by)
VALUES
    ('INSERT', 'Creación de registro', 'Inserción', 'AUDIT_ACTION_TYPE', TRUE, 1, 'system'),
    ('UPDATE', 'Actualización de registro', 'Actualización', 'AUDIT_ACTION_TYPE', TRUE, 2, 'system'),
    ('DELETE', 'Eliminación de registro', 'Eliminación', 'AUDIT_ACTION_TYPE', TRUE, 3, 'system'),
    ('PROCESS', 'Ejecución de proceso', 'Procesamiento', 'AUDIT_ACTION_TYPE', TRUE, 4, 'system'),
    ('APPROVE', 'Aprobación de operación', 'Aprobación', 'AUDIT_ACTION_TYPE', TRUE, 5, 'system'),
    ('REJECT', 'Rechazo de operación', 'Rechazo', 'AUDIT_ACTION_TYPE', TRUE, 6, 'system'),
    ('LOGIN', 'Autenticación en sistema', 'Inicio de Sesión', 'AUDIT_ACTION_TYPE', TRUE, 7, 'system'),
    ('UPLOAD', 'Carga de archivo en sistema', 'Carga de Archivo', 'AUDIT_ACTION_TYPE', TRUE, 8, 'system')
ON CONFLICT (parameter_group, name) DO NOTHING;

-- GROUP: COMMISSION_RAW_STATUS
INSERT INTO parameter (name, description, value, parameter_group, active, sort_order, created_by)
VALUES
    ('ACTIVE', 'Estado crudo activo', 'Activo', 'COMMISSION_RAW_STATUS', TRUE, 1, 'system'),
    ('INACTIVE', 'Estado crudo inactivo', 'Inactivo', 'COMMISSION_RAW_STATUS', TRUE, 2, 'system'),
    ('CANCELLED', 'Estado crudo cancelado', 'Cancelado', 'COMMISSION_RAW_STATUS', TRUE, 3, 'system'),
    ('PENDING', 'Estado crudo pendiente', 'Pendiente', 'COMMISSION_RAW_STATUS', TRUE, 4, 'system'),
    ('UNKNOWN', 'Estado crudo sin homologar', 'Desconocido', 'COMMISSION_RAW_STATUS', TRUE, 5, 'system')
ON CONFLICT (parameter_group, name) DO NOTHING;

-- GROUP: REASON_CODE
INSERT INTO parameter (name, description, value, parameter_group, active, sort_order, created_by)
VALUES
    ('NEW_BUSINESS', 'Alta o nueva venta', 'Nuevo Negocio', 'REASON_CODE', TRUE, 1, 'system'),
    ('RENEWAL', 'Renovación de póliza', 'Renovación', 'REASON_CODE', TRUE, 2, 'system'),
    ('CANCELLATION', 'Cancelación de póliza', 'Cancelación', 'REASON_CODE', TRUE, 3, 'system'),
    ('ADJUSTMENT', 'Ajuste monetario o técnico', 'Ajuste', 'REASON_CODE', TRUE, 4, 'system'),
    ('RETRO', 'Movimiento retroactivo', 'Retroactivo', 'REASON_CODE', TRUE, 5, 'system'),
    ('UNKNOWN', 'Código de razón no homologado', 'Desconocido', 'REASON_CODE', TRUE, 6, 'system')
ON CONFLICT (parameter_group, name) DO NOTHING;

-- =========================================================
-- 15. COMENTARIOS
-- =========================================================
-- Convencion oficial:
--   parameter.name  = codigo tecnico
--   parameter.value = texto visible en UI
--
-- Ejemplo de consulta para backend:
--   SELECT id
--   FROM parameter
--   WHERE parameter_group = 'SOURCE_FILE_STATUS'
--     AND name = 'PROCESSED';
--
-- Ejemplo de consulta para combos:
--   SELECT id, name, value
--   FROM parameter
--   WHERE parameter_group = 'PAYMENT_STATUS'
--     AND active = TRUE
--   ORDER BY sort_order;