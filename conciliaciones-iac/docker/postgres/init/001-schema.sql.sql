-- =========================================================
-- PROYECTO: RECONCILIACION / LIQUIDACION DE COMISIONES
-- MOTOR: PostgreSQL
-- VERSION RECTIFICADA / IDÉMPOTENTE
-- VERSION AJUSTADA CON TABLA PARAMETER
-- CONVENCION:
--   name  = codigo tecnico
--   value = texto visible en UI
-- =========================================================

CREATE SCHEMA IF NOT EXISTS conciliaciones;
-- No se usa search_path: todos los objetos están calificados explícitamente.
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- =========================================================
-- 1. TABLA GENERAL DE PARAMETROS
-- =========================================================
CREATE TABLE IF NOT EXISTS conciliaciones.parameter (
    id                  BIGINT PRIMARY KEY,
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
    ON conciliaciones.parameter(parameter_group);

CREATE INDEX IF NOT EXISTS idx_parameter_group_active
    ON conciliaciones.parameter(parameter_group, active);

CREATE INDEX IF NOT EXISTS idx_parameter_group_name
    ON conciliaciones.parameter(parameter_group, name);

-- =========================================================
-- 2. TABLAS MAESTRAS
-- =========================================================

CREATE TABLE IF NOT EXISTS conciliaciones.carrier (
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

CREATE TABLE IF NOT EXISTS conciliaciones.agency (
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
        FOREIGN KEY (carrier_id) REFERENCES conciliaciones.carrier(id),
    CONSTRAINT uq_agency_carrier_name UNIQUE (carrier_id, name)
);

CREATE TABLE IF NOT EXISTS conciliaciones.producer (
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
        FOREIGN KEY (agency_id) REFERENCES conciliaciones.agency(id),
    CONSTRAINT uq_producer_external_id UNIQUE (external_producer_id)
);

CREATE TABLE IF NOT EXISTS conciliaciones.client (
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

CREATE TABLE IF NOT EXISTS conciliaciones.source_file (
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
        FOREIGN KEY (carrier_id) REFERENCES conciliaciones.carrier(id),
    CONSTRAINT fk_source_file_processing_status
        FOREIGN KEY (processing_status_id) REFERENCES conciliaciones.parameter(id)
);

CREATE TABLE IF NOT EXISTS conciliaciones.source_file_sheet (
    id                  BIGSERIAL PRIMARY KEY,
    source_file_id      BIGINT NOT NULL,
    sheet_name          VARCHAR(255) NOT NULL,
    sheet_order         INTEGER NOT NULL,
    total_rows          INTEGER,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by          VARCHAR(100),
    CONSTRAINT fk_source_file_sheet_file
        FOREIGN KEY (source_file_id) REFERENCES conciliaciones.source_file(id) ON DELETE CASCADE,
    CONSTRAINT uq_source_file_sheet UNIQUE (source_file_id, sheet_name)
);

-- =========================================================
-- 4. RAW DATA
-- =========================================================

CREATE TABLE IF NOT EXISTS conciliaciones.raw_import_record (
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
        FOREIGN KEY (source_file_id) REFERENCES conciliaciones.source_file(id) ON DELETE CASCADE,
    CONSTRAINT fk_raw_import_record_sheet
        FOREIGN KEY (source_file_sheet_id) REFERENCES conciliaciones.source_file_sheet(id) ON DELETE CASCADE,
    CONSTRAINT fk_raw_import_record_parse_status
        FOREIGN KEY (parse_status_id) REFERENCES conciliaciones.parameter(id),
    CONSTRAINT uq_raw_import_record UNIQUE (source_file_id, row_number)
);


-- =========================================================
-- 4.1 PLAN MAESTRO DE VALIDACION DE ARCHIVO / START_VALIDATE_DATA
-- =========================================================
CREATE TABLE IF NOT EXISTS conciliaciones.validation_source_plan (
    id              BIGSERIAL PRIMARY KEY,
    source_file_id  BIGINT NOT NULL,
    status_id       BIGINT NOT NULL,
    started_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    finished_at     TIMESTAMP,
    successful      BOOLEAN,
    message         TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by      VARCHAR(100),
    updated_at      TIMESTAMP,
    updated_by      VARCHAR(100),
    CONSTRAINT fk_validation_source_plan_source_file
        FOREIGN KEY (source_file_id)
        REFERENCES conciliaciones.source_file(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_validation_source_plan_status
        FOREIGN KEY (status_id)
        REFERENCES conciliaciones.parameter(id)
);

-- =========================================================
-- 4.2 RESULTADO DE VALIDACIONES DE ARCHIVO / START_VALIDATE_DATA
-- =========================================================
CREATE TABLE IF NOT EXISTS conciliaciones.source_file_validation (
    id                        BIGSERIAL PRIMARY KEY,
    validation_source_plan_id BIGINT NOT NULL,
    raw_import_record_id      BIGINT,
    validation_type_id        BIGINT NOT NULL,
    validation_status_id      BIGINT NOT NULL,
    row_number                INTEGER,
    column_name               VARCHAR(150),
    field_value               TEXT,
    message                   VARCHAR(2000) NOT NULL,
    technical_detail          TEXT,
    created_at                TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by                VARCHAR(100),
    CONSTRAINT fk_source_file_validation_plan
        FOREIGN KEY (validation_source_plan_id)
        REFERENCES conciliaciones.validation_source_plan(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_source_file_validation_raw_import_record
        FOREIGN KEY (raw_import_record_id)
        REFERENCES conciliaciones.raw_import_record(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_source_file_validation_type
        FOREIGN KEY (validation_type_id)
        REFERENCES conciliaciones.parameter(id),
    CONSTRAINT fk_source_file_validation_status
        FOREIGN KEY (validation_status_id)
        REFERENCES conciliaciones.parameter(id)
);

-- =========================================================
-- 5.1. PAISES - ESTADOS - CIUDADES
-- =========================================================

CREATE TABLE IF NOT EXISTS conciliaciones.country (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS conciliaciones.state (
    id BIGSERIAL PRIMARY KEY,
    country_id BIGINT NOT NULL,
    code VARCHAR(20) NOT NULL,
    name VARCHAR(150) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_state_country
        FOREIGN KEY (country_id)
        REFERENCES conciliaciones.country (id),
    CONSTRAINT uk_state_country_code
        UNIQUE (country_id, code)
);

CREATE TABLE IF NOT EXISTS conciliaciones.city (
    id BIGSERIAL PRIMARY KEY,
    state_id BIGINT NOT NULL,
    name VARCHAR(150) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_city_state
        FOREIGN KEY (state_id)
        REFERENCES conciliaciones.state (id),
    CONSTRAINT uk_city_state_name
        UNIQUE (state_id, name)
);


-- =========================================================
-- 5. POLIZAS Y MODELO CANONICO
-- =========================================================

CREATE TABLE IF NOT EXISTS conciliaciones.policy (
    id                  BIGSERIAL PRIMARY KEY,
    carrier_id          BIGINT NOT NULL,
    client_id           BIGINT,
    policy_number       VARCHAR(150),
    subscriber_id       VARCHAR(150),
    effective_date      DATE,
    issue_date          DATE,
    termination_date    DATE,
    status_id           BIGINT NOT NULL,
    resident_state      BIGINT NOT NULL,
    issue_state         VARCHAR(100),
    members_count       INTEGER,
    source_key          VARCHAR(255),
    active              BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by          VARCHAR(100),
    updated_at          TIMESTAMP,
    updated_by          VARCHAR(100),
    CONSTRAINT fk_policy_carrier
        FOREIGN KEY (carrier_id) REFERENCES conciliaciones.carrier(id),
    CONSTRAINT fk_policy_client
        FOREIGN KEY (client_id) REFERENCES conciliaciones.client(id),
    CONSTRAINT fk_policy_status
        FOREIGN KEY (status_id) REFERENCES conciliaciones.parameter(id),
	CONSTRAINT fk_policy_resident_state
        FOREIGN KEY (resident_state) REFERENCES conciliaciones.city(id),
    CONSTRAINT uq_policy_carrier_number UNIQUE (carrier_id, policy_number)
);

CREATE TABLE IF NOT EXISTS conciliaciones.policy_plan (
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
        FOREIGN KEY (policy_id) REFERENCES conciliaciones.policy(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS conciliaciones.policy_status_history (
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
        FOREIGN KEY (policy_id) REFERENCES conciliaciones.policy(id) ON DELETE CASCADE,
    CONSTRAINT fk_policy_status_history_file
        FOREIGN KEY (source_file_id) REFERENCES conciliaciones.source_file(id),
    CONSTRAINT fk_policy_status_history_status
        FOREIGN KEY (status_id) REFERENCES conciliaciones.parameter(id)
);

-- =========================================================
-- 6. ASIGNACION DE COMISIONES
-- =========================================================

CREATE TABLE IF NOT EXISTS conciliaciones.commission_assignment (
    id                  BIGSERIAL PRIMARY KEY,
    policy_id           BIGINT NOT NULL,
    producer_id         BIGINT NOT NULL,
    split_percentage    NUMERIC(18,2),
    valid_from          DATE NOT NULL,
    valid_to            DATE,
    active              BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by          VARCHAR(100),
    updated_at          TIMESTAMP,
    updated_by          VARCHAR(100),
    CONSTRAINT fk_commission_assignment_policy
        FOREIGN KEY (policy_id) REFERENCES conciliaciones.policy(id) ON DELETE CASCADE,
    CONSTRAINT fk_commission_assignment_producer
        FOREIGN KEY (producer_id) REFERENCES conciliaciones.producer(id),    
    CONSTRAINT ck_commission_assignment_split
        CHECK (split_percentage IS NULL OR (split_percentage >= 0 AND split_percentage <= 100))
);

-- =========================================================
-- 7. REGISTROS DE COMISION / NORMALIZADOS
-- =========================================================

CREATE TABLE IF NOT EXISTS conciliaciones.commission_statement (
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
        FOREIGN KEY (source_file_id) REFERENCES conciliaciones.source_file(id) ON DELETE CASCADE,
    CONSTRAINT fk_commission_statement_raw_import
        FOREIGN KEY (raw_import_record_id) REFERENCES conciliaciones.raw_import_record(id) ON DELETE SET NULL,
    CONSTRAINT fk_commission_statement_carrier
        FOREIGN KEY (carrier_id) REFERENCES conciliaciones.carrier(id),
    CONSTRAINT fk_commission_statement_agency
        FOREIGN KEY (agency_id) REFERENCES conciliaciones.agency(id),
    CONSTRAINT fk_commission_statement_producer
        FOREIGN KEY (producer_id) REFERENCES conciliaciones.producer(id),
    CONSTRAINT fk_commission_statement_client
        FOREIGN KEY (client_id) REFERENCES conciliaciones.client(id),
    CONSTRAINT fk_commission_statement_policy
        FOREIGN KEY (policy_id) REFERENCES conciliaciones.policy(id),
    CONSTRAINT fk_commission_statement_raw_status
        FOREIGN KEY (raw_status_id) REFERENCES conciliaciones.parameter(id),
    CONSTRAINT fk_commission_statement_reason_code
        FOREIGN KEY (reason_code_id) REFERENCES conciliaciones.parameter(id)
);

CREATE TABLE IF NOT EXISTS conciliaciones.commission_statement_item (
    id                      BIGSERIAL PRIMARY KEY,
    commission_statement_id BIGINT NOT NULL,
    gross_premium           NUMERIC(18,2),
    commissionable_premium  NUMERIC(18,2),
    premium                 NUMERIC(18,2),
    net_amount              NUMERIC(18,2),
    rate                    NUMERIC(10,4),
    commission_rate_pct     NUMERIC(10,4),
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
        FOREIGN KEY (commission_statement_id) REFERENCES conciliaciones.commission_statement(id) ON DELETE CASCADE
);

-- =========================================================
-- 8. REGLAS
-- =========================================================

CREATE TABLE IF NOT EXISTS conciliaciones.commission_rule (
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
        FOREIGN KEY (carrier_id) REFERENCES conciliaciones.carrier(id),
    CONSTRAINT fk_commission_rule_role
        FOREIGN KEY (role_id) REFERENCES conciliaciones.parameter(id),
    CONSTRAINT fk_commission_rule_type
        FOREIGN KEY (rule_type_id) REFERENCES conciliaciones.parameter(id)
);

-- =========================================================
-- 9. CASOS DE RECONCILIACION
-- =========================================================

CREATE TABLE IF NOT EXISTS conciliaciones.reconciliation_case (
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
        FOREIGN KEY (source_file_id) REFERENCES conciliaciones.source_file(id),
    CONSTRAINT fk_reconciliation_case_statement
        FOREIGN KEY (commission_statement_id) REFERENCES conciliaciones.commission_statement(id) ON DELETE SET NULL,
    CONSTRAINT fk_reconciliation_case_statement_item
        FOREIGN KEY (commission_statement_item_id) REFERENCES conciliaciones.commission_statement_item(id) ON DELETE SET NULL,
    CONSTRAINT fk_reconciliation_case_carrier
        FOREIGN KEY (carrier_id) REFERENCES conciliaciones.carrier(id),
    CONSTRAINT fk_reconciliation_case_policy
        FOREIGN KEY (policy_id) REFERENCES conciliaciones.policy(id),
    CONSTRAINT fk_reconciliation_case_producer
        FOREIGN KEY (producer_id) REFERENCES conciliaciones.producer(id),
    CONSTRAINT fk_reconciliation_case_type
        FOREIGN KEY (case_type_id) REFERENCES conciliaciones.parameter(id),
    CONSTRAINT fk_reconciliation_case_severity
        FOREIGN KEY (severity_id) REFERENCES conciliaciones.parameter(id),
    CONSTRAINT fk_reconciliation_case_status
        FOREIGN KEY (status_id) REFERENCES conciliaciones.parameter(id)
);

-- =========================================================
-- 10. LIQUIDACION / PAGOS
-- =========================================================

CREATE TABLE IF NOT EXISTS conciliaciones.commission_payment (
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
        FOREIGN KEY (producer_id) REFERENCES conciliaciones.producer(id),
    CONSTRAINT fk_commission_payment_status
        FOREIGN KEY (status_id) REFERENCES conciliaciones.parameter(id),
    CONSTRAINT uq_commission_payment_period UNIQUE (producer_id, period_year, period_month),
    CONSTRAINT ck_commission_payment_month CHECK (period_month BETWEEN 1 AND 12)
);

CREATE TABLE IF NOT EXISTS conciliaciones.commission_payment_detail (
    id                           BIGSERIAL PRIMARY KEY,
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
    CONSTRAINT fk_commission_payment_detail_policy
        FOREIGN KEY (policy_id) REFERENCES conciliaciones.policy(id),
    CONSTRAINT fk_commission_payment_detail_statement_item
        FOREIGN KEY (commission_statement_item_id) REFERENCES conciliaciones.commission_statement_item(id),
    CONSTRAINT fk_commission_payment_detail_case
        FOREIGN KEY (reconciliation_case_id) REFERENCES conciliaciones.reconciliation_case(id)
);

-- =========================================================
-- 11. AUDITORIA
-- =========================================================

CREATE TABLE IF NOT EXISTS conciliaciones.audit_log (
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
        FOREIGN KEY (action_id) REFERENCES conciliaciones.parameter(id)
);

-- =========================================================
-- 12. SOURCE_FILE_TRACEABILITY
-- =========================================================

CREATE TABLE IF NOT EXISTS conciliaciones.source_file_traceability (
    id BIGSERIAL PRIMARY KEY,
    source_file_id BIGINT NOT NULL,
    processing_status_id BIGINT NOT NULL,
    status_name VARCHAR(80) NOT NULL,
    description VARCHAR(500),
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    CONSTRAINT fk_source_file_traceability_source_file
        FOREIGN KEY (source_file_id)
        REFERENCES conciliaciones.source_file (id)
);

-- =========================================================
-- 13. execution_plan_task
-- =========================================================

CREATE TABLE IF NOT EXISTS conciliaciones.execution_plan_task (
    id BIGSERIAL PRIMARY KEY,
    id_source_file BIGINT NOT NULL,
    id_status BIGINT NOT NULL,
    plan_execute_code VARCHAR(200) NOT NULL,
    started_at TIMESTAMP NOT NULL DEFAULT NOW(),
    finished_at TIMESTAMP,
    successful BOOLEAN,
    message TEXT,
    created_by VARCHAR(80) NOT NULL DEFAULT 'system',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_by VARCHAR(80),
    updated_at TIMESTAMP,
    CONSTRAINT fk_execution_plan_task_source_file FOREIGN KEY (id_source_file) REFERENCES conciliaciones.source_file(id),
    CONSTRAINT fk_execution_plan_task_parameter_status FOREIGN KEY (id_status) REFERENCES conciliaciones.parameter (id),
    CONSTRAINT uq_execution_plan_task_code UNIQUE (plan_execute_code)
);

-- =========================================================
-- 14. SCHEDULED_TASK
-- =========================================================

CREATE TABLE IF NOT EXISTS conciliaciones.scheduled_task (
    id BIGSERIAL PRIMARY KEY,
    id_execution_plan_task BIGINT NOT NULL,
    id_task_type BIGINT NOT NULL,
    id_status BIGINT NOT NULL,
    task_order INTEGER NOT NULL,
    cron_expression VARCHAR(80) NOT NULL,
    task_bean_name VARCHAR(200) NOT NULL,
    method_name VARCHAR(200) NOT NULL,
    payload TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    started_at TIMESTAMP,
    finished_at TIMESTAMP,
    successful BOOLEAN,
    message TEXT,
    created_by VARCHAR(80) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_by VARCHAR(80),
    updated_at TIMESTAMP,
    CONSTRAINT fk_scheduled_task_execution_plan_task FOREIGN KEY (id_execution_plan_task) REFERENCES conciliaciones.execution_plan_task(id),
    CONSTRAINT fk_scheduled_task_parameter_task_type FOREIGN KEY (id_task_type) REFERENCES conciliaciones.parameter (id),
    CONSTRAINT fk_scheduled_task_parameter_status FOREIGN KEY (id_status) REFERENCES conciliaciones.parameter (id),
    CONSTRAINT uq_scheduled_task_plan_order UNIQUE (id_execution_plan_task, task_order),
    CONSTRAINT uq_scheduled_task_plan_type UNIQUE (id_execution_plan_task, id_task_type)
);

-- =========================================================
-- 15. SECURITY_MENU
-- =========================================================
CREATE TABLE IF NOT EXISTS conciliaciones.security_menu (
    id BIGSERIAL PRIMARY KEY,
    parameter_id BIGINT NOT NULL,
    code VARCHAR(100) NOT NULL,
    label VARCHAR(150) NOT NULL,
    icon VARCHAR(100),
    sort_order INTEGER NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    CONSTRAINT fk_security_menu_parameter FOREIGN KEY (parameter_id) REFERENCES conciliaciones.parameter(id),
    CONSTRAINT uk_security_menu_parameter UNIQUE (parameter_id),
    CONSTRAINT uk_security_menu_code UNIQUE (code)
);

-- =========================================================
-- 16. SECURITY_SUB_MENU
-- =========================================================
CREATE TABLE IF NOT EXISTS conciliaciones.security_sub_menu (
    id BIGSERIAL PRIMARY KEY,
    menu_id BIGINT NOT NULL,
    parameter_id BIGINT NOT NULL,
    code VARCHAR(100) NOT NULL,
    label VARCHAR(150) NOT NULL,
    route VARCHAR(200) NOT NULL,
    icon VARCHAR(100),
    sort_order INTEGER NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    CONSTRAINT fk_security_sub_menu_menu FOREIGN KEY (menu_id) REFERENCES conciliaciones.security_menu(id),
    CONSTRAINT fk_security_sub_menu_parameter FOREIGN KEY (parameter_id) REFERENCES conciliaciones.parameter(id),
    CONSTRAINT uk_security_sub_menu_parameter UNIQUE (parameter_id),
    CONSTRAINT uk_security_sub_menu_code UNIQUE (code),
    CONSTRAINT uk_security_sub_menu_route UNIQUE (route)
);

-- =========================================================
-- 17. SECURITY_USER
-- =========================================================
CREATE TABLE IF NOT EXISTS conciliaciones.security_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    full_name VARCHAR(200) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    CONSTRAINT uk_security_user_username UNIQUE (username),
    CONSTRAINT uk_security_user_email UNIQUE (email)
);

-- =========================================================
-- 18. SECURITY_ROLE
-- =========================================================
CREATE TABLE IF NOT EXISTS conciliaciones.security_role (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(100) NOT NULL,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    CONSTRAINT uk_security_role_code UNIQUE (code)
);

-- =========================================================
-- 19. SECURITY_USER
-- =========================================================
CREATE TABLE IF NOT EXISTS conciliaciones.security_role_menu_permission (
    id BIGSERIAL PRIMARY KEY,
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    sub_menu_id BIGINT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    CONSTRAINT fk_security_role_menu_permission_role FOREIGN KEY (role_id) REFERENCES conciliaciones.security_role(id),
    CONSTRAINT fk_security_role_menu_permission_menu FOREIGN KEY (menu_id) REFERENCES conciliaciones.security_menu(id),
    CONSTRAINT fk_security_role_menu_permission_sub_menu FOREIGN KEY (sub_menu_id) REFERENCES conciliaciones.security_sub_menu(id),
	CONSTRAINT uk_security_role_menu_permission UNIQUE (role_id, menu_id, sub_menu_id)
);

-- =========================================================
-- 20. SECURITY_USER_ROLE
-- =========================================================
CREATE TABLE IF NOT EXISTS conciliaciones.security_user_role (
    id bigserial NOT NULL,
    user_id int8 NOT NULL,
    role_id int8 NOT NULL,
    active bool DEFAULT true NOT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by varchar(100) NULL,
    updated_at timestamp NULL,
    updated_by varchar(100) NULL,

    CONSTRAINT security_user_role_pkey PRIMARY KEY (id),

    CONSTRAINT uk_security_user_role_user UNIQUE (user_id),

    CONSTRAINT fk_security_user_role_user
        FOREIGN KEY (user_id)
        REFERENCES conciliaciones.security_user(id),

    CONSTRAINT fk_security_user_role_role
        FOREIGN KEY (role_id)
        REFERENCES conciliaciones.security_role(id)
);

-- =========================================================
-- 21. SECURITY_AUDIT_LOG
-- =========================================================
CREATE TABLE IF NOT EXISTS conciliaciones.security_audit_log (
    id BIGSERIAL PRIMARY KEY,
    usuario VARCHAR(120) NOT NULL,
    accion VARCHAR(120) NOT NULL,
    fecha TIMESTAMP WITH TIME ZONE NOT NULL,
    resultado VARCHAR(30) NOT NULL,
    detalle VARCHAR(500),
    estado VARCHAR(40),
    valor_antes JSONB,
    valor_despues JSONB
);

-- =========================================================
-- 18. INDICES
-- =========================================================

CREATE INDEX IF NOT EXISTS idx_agency_carrier_id
    ON conciliaciones.agency(carrier_id);

CREATE INDEX IF NOT EXISTS idx_producer_agency_id
    ON conciliaciones.producer(agency_id);

CREATE INDEX IF NOT EXISTS idx_client_full_name
    ON conciliaciones.client(full_name);

CREATE INDEX IF NOT EXISTS idx_source_file_carrier_id
    ON conciliaciones.source_file(carrier_id);

CREATE INDEX IF NOT EXISTS idx_source_file_processing_status_id
    ON conciliaciones.source_file(processing_status_id);

CREATE INDEX IF NOT EXISTS idx_execution_plan_task_source_file
    ON conciliaciones.execution_plan_task(id_source_file);

CREATE INDEX IF NOT EXISTS idx_execution_plan_task_status
    ON conciliaciones.execution_plan_task(id_status);

CREATE INDEX IF NOT EXISTS idx_execution_plan_task_code
    ON conciliaciones.execution_plan_task(plan_execute_code);

CREATE INDEX IF NOT EXISTS idx_scheduled_task_execution_plan
    ON conciliaciones.scheduled_task(id_execution_plan_task);

CREATE INDEX IF NOT EXISTS idx_scheduled_task_status
    ON conciliaciones.scheduled_task(id_status);

CREATE INDEX IF NOT EXISTS idx_scheduled_task_type
    ON conciliaciones.scheduled_task(id_task_type);

CREATE INDEX IF NOT EXISTS idx_scheduled_task_plan_order
    ON conciliaciones.scheduled_task(id_execution_plan_task, task_order);

CREATE INDEX IF NOT EXISTS idx_scheduled_task_active_status
    ON conciliaciones.scheduled_task(active, id_status);

CREATE INDEX IF NOT EXISTS idx_raw_import_record_file_id
    ON conciliaciones.raw_import_record(source_file_id);

CREATE INDEX IF NOT EXISTS idx_raw_import_record_sheet_id
    ON conciliaciones.raw_import_record(source_file_sheet_id);

CREATE INDEX IF NOT EXISTS idx_raw_import_record_parse_status_id
    ON conciliaciones.raw_import_record(parse_status_id);

CREATE INDEX IF NOT EXISTS idx_raw_import_record_payload_gin
    ON conciliaciones.raw_import_record USING GIN(raw_payload);

CREATE INDEX IF NOT EXISTS idx_validation_source_plan_source_file_id
    ON conciliaciones.validation_source_plan(source_file_id);

CREATE INDEX IF NOT EXISTS idx_validation_source_plan_status_id
    ON conciliaciones.validation_source_plan(status_id);

CREATE INDEX IF NOT EXISTS idx_validation_source_plan_successful
    ON conciliaciones.validation_source_plan(successful);

CREATE INDEX IF NOT EXISTS idx_validation_source_plan_started_at
    ON conciliaciones.validation_source_plan(started_at);

CREATE INDEX IF NOT EXISTS idx_validation_source_plan_source_file_status
    ON conciliaciones.validation_source_plan(source_file_id, status_id);

CREATE INDEX IF NOT EXISTS idx_source_file_validation_plan_id
    ON conciliaciones.source_file_validation(validation_source_plan_id);

CREATE INDEX IF NOT EXISTS idx_source_file_validation_raw_import_record_id
    ON conciliaciones.source_file_validation(raw_import_record_id);

CREATE INDEX IF NOT EXISTS idx_source_file_validation_type_id
    ON conciliaciones.source_file_validation(validation_type_id);

CREATE INDEX IF NOT EXISTS idx_source_file_validation_status_id
    ON conciliaciones.source_file_validation(validation_status_id);

CREATE INDEX IF NOT EXISTS idx_source_file_validation_row_number
    ON conciliaciones.source_file_validation(row_number);

CREATE INDEX IF NOT EXISTS idx_source_file_validation_column_name
    ON conciliaciones.source_file_validation(column_name);

CREATE INDEX IF NOT EXISTS idx_policy_carrier_id
    ON conciliaciones.policy(carrier_id);

CREATE INDEX IF NOT EXISTS idx_policy_client_id
    ON conciliaciones.policy(client_id);

CREATE INDEX IF NOT EXISTS idx_policy_subscriber_id
    ON conciliaciones.policy(subscriber_id);

CREATE INDEX IF NOT EXISTS idx_policy_status_id
    ON conciliaciones.policy(status_id);

CREATE INDEX IF NOT EXISTS idx_policy_effective_date
    ON conciliaciones.policy(effective_date);

CREATE INDEX IF NOT EXISTS idx_policy_status_history_policy_id
    ON conciliaciones.policy_status_history(policy_id);

CREATE INDEX IF NOT EXISTS idx_policy_status_history_status_id
    ON conciliaciones.policy_status_history(status_id);

CREATE INDEX IF NOT EXISTS idx_commission_assignment_policy_id
    ON conciliaciones.commission_assignment(policy_id);

CREATE INDEX IF NOT EXISTS idx_commission_assignment_producer_id
    ON conciliaciones.commission_assignment(producer_id);

CREATE INDEX IF NOT EXISTS idx_commission_statement_source_file_id
    ON conciliaciones.commission_statement(source_file_id);

CREATE INDEX IF NOT EXISTS idx_commission_statement_policy_id
    ON conciliaciones.commission_statement(policy_id);

CREATE INDEX IF NOT EXISTS idx_commission_statement_producer_id
    ON conciliaciones.commission_statement(producer_id);

CREATE INDEX IF NOT EXISTS idx_commission_statement_paid_date
    ON conciliaciones.commission_statement(paid_date);

CREATE INDEX IF NOT EXISTS idx_commission_statement_invoice
    ON conciliaciones.commission_statement(invoice_number);

CREATE INDEX IF NOT EXISTS idx_commission_statement_raw_status_id
    ON conciliaciones.commission_statement(raw_status_id);

CREATE INDEX IF NOT EXISTS idx_commission_statement_reason_code_id
    ON conciliaciones.commission_statement(reason_code_id);

CREATE INDEX IF NOT EXISTS idx_commission_statement_item_statement_id
    ON conciliaciones.commission_statement_item(commission_statement_id);

CREATE INDEX IF NOT EXISTS idx_commission_rule_carrier_id
    ON conciliaciones.commission_rule(carrier_id);

CREATE INDEX IF NOT EXISTS idx_commission_rule_role_id
    ON conciliaciones.commission_rule(role_id);

CREATE INDEX IF NOT EXISTS idx_commission_rule_type_id
    ON conciliaciones.commission_rule(rule_type_id);

CREATE INDEX IF NOT EXISTS idx_reconciliation_case_source_file_id
    ON conciliaciones.reconciliation_case(source_file_id);

CREATE INDEX IF NOT EXISTS idx_reconciliation_case_policy_id
    ON conciliaciones.reconciliation_case(policy_id);

CREATE INDEX IF NOT EXISTS idx_reconciliation_case_producer_id
    ON conciliaciones.reconciliation_case(producer_id);

CREATE INDEX IF NOT EXISTS idx_reconciliation_case_case_type_id
    ON conciliaciones.reconciliation_case(case_type_id);

CREATE INDEX IF NOT EXISTS idx_reconciliation_case_severity_id
    ON conciliaciones.reconciliation_case(severity_id);

CREATE INDEX IF NOT EXISTS idx_reconciliation_case_status_id
    ON conciliaciones.reconciliation_case(status_id);

CREATE INDEX IF NOT EXISTS idx_commission_payment_producer_id
    ON conciliaciones.commission_payment(producer_id);

CREATE INDEX IF NOT EXISTS idx_commission_payment_status_id
    ON conciliaciones.commission_payment(status_id);

CREATE INDEX IF NOT EXISTS idx_audit_log_entity_name
    ON conciliaciones.audit_log(entity_name);

CREATE INDEX IF NOT EXISTS idx_audit_log_event_timestamp
    ON conciliaciones.audit_log(event_timestamp);

CREATE INDEX IF NOT EXISTS idx_audit_log_action_id
    ON conciliaciones.audit_log(action_id);

CREATE INDEX IF NOT EXISTS idx_source_file_traceability_source_file_id
    ON conciliaciones.source_file_traceability (source_file_id);	

CREATE INDEX IF NOT EXISTS idx_scheduled_task_status_active 
    ON conciliaciones.scheduled_task(id_status, active);

CREATE INDEX IF NOT EXISTS idx_scheduled_task_task_type
    ON conciliaciones.scheduled_task(id_task_type);

CREATE INDEX IF NOT EXISTS idx_execution_plan_task_source_file 
    ON conciliaciones.execution_plan_task(id_source_file);

CREATE INDEX IF NOT EXISTS idx_execution_plan_task_status 
    ON conciliaciones.execution_plan_task(id_status);

CREATE INDEX IF NOT EXISTS idx_state_country_id
    ON conciliaciones.state (country_id);

CREATE INDEX IF NOT EXISTS idx_city_state_id
    ON conciliaciones.city (state_id);

CREATE INDEX IF NOT EXISTS idx_country_name
    ON conciliaciones.country (name);

CREATE INDEX IF NOT EXISTS idx_state_name
    ON conciliaciones.state (name);

CREATE INDEX IF NOT EXISTS idx_city_name
    ON conciliaciones.city (name);

-- =========================================================
-- 17. DATOS SEMILLA - CARRIER
-- =========================================================

INSERT INTO conciliaciones.carrier (code, name, description, created_by)
VALUES
    ('ELITE', 'ELITE', 'Fuente de datos ELITE', 'system'),
    ('SENTARA', 'SENTARA', 'Fuente de datos SENTARA', 'system'),
    ('SAFEGUARD', 'SAFEGUARD', 'Fuente de datos SAFEGUARD', 'system'),
    ('UNITED_H_ONE', 'UNITED H ONE', 'Fuente de datos UNITED H ONE', 'system')
ON CONFLICT (code) DO NOTHING;

-- =========================================================
-- 18. DATOS SEMILLA - PARAMETER (IDS FIJOS)
-- =========================================================

INSERT INTO conciliaciones.parameter (id, name, description, value, parameter_group, active, sort_order, created_by)
VALUES
(1,'PRESIGNED','URL prefirmada generada para carga del archivo','Prefirmado','SOURCE_FILE_STATUS',TRUE,1,'system'),
(2,'S3_UPLOAD','Archivo cargado correctamente en S3','Cargado en S3','SOURCE_FILE_STATUS',TRUE,2,'system'),
(3,'DATA_REVIEW','Archivo en etapa de revisión de datos','Revisión de Datos','SOURCE_FILE_STATUS',TRUE,3,'system'),
(4,'PROCESS_DATA','Archivo en procesamiento de datos','Procesando Datos','SOURCE_FILE_STATUS',TRUE,4,'system'),
(5,'RULER_APLICATION','Aplicación de reglas sobre los datos procesados','Aplicando Reglas','SOURCE_FILE_STATUS',TRUE,5,'system'),
(6,'PROCESS_FINALICE','Proceso finalizado correctamente','Proceso Finalizado','SOURCE_FILE_STATUS',TRUE,6,'system'),
(7,'ERROR_PRESIGNED','Error durante la generación de la URL prefirmada','Error Prefirmado','SOURCE_FILE_STATUS',TRUE,7,'system'),
(8,'ERROR_S3_UPLOAD','Error durante la carga del archivo en S3','Error Carga S3','SOURCE_FILE_STATUS',TRUE,8,'system'),
(9,'ERROR_DATA_REVIEW','Error durante la revisión de datos','Error Revisión Datos','SOURCE_FILE_STATUS',TRUE,9,'system'),
(10,'ERROR_PROCESS_DATA','Error durante el procesamiento de datos','Error Procesamiento','SOURCE_FILE_STATUS',TRUE,10,'system'),
(11,'ERROR_RULER_APLICATION','Error durante la aplicación de reglas','Error Aplicación Reglas','SOURCE_FILE_STATUS',TRUE,11,'system')
ON CONFLICT (id) DO UPDATE SET
name = EXCLUDED.name,
description = EXCLUDED.description,
value = EXCLUDED.value,
parameter_group = EXCLUDED.parameter_group,
active = EXCLUDED.active,
sort_order = EXCLUDED.sort_order,
updated_at = CURRENT_TIMESTAMP,
updated_by = EXCLUDED.created_by;

-- GROUP: PARSE_STATUS (12 - 14)
INSERT INTO conciliaciones.parameter (id, name, description, value, parameter_group, active, sort_order, created_by)
VALUES
(12,'PENDING','Fila pendiente de parseo','Pendiente','PARSE_STATUS',TRUE,1,'system'),
(13,'PARSED','Fila parseada correctamente','Parseado','PARSE_STATUS',TRUE,2,'system'),
(14,'FAILED','Error en el parseo de la fila','Fallido','PARSE_STATUS',TRUE,3,'system')
ON CONFLICT (id) DO UPDATE SET
name = EXCLUDED.name,
description = EXCLUDED.description,
value = EXCLUDED.value,
parameter_group = EXCLUDED.parameter_group,
active = EXCLUDED.active,
sort_order = EXCLUDED.sort_order,
updated_at = CURRENT_TIMESTAMP,
updated_by = EXCLUDED.created_by;

-- GROUP: POLICY_STATUS (15 - 21)
INSERT INTO conciliaciones.parameter (id, name, description, value, parameter_group, active, sort_order, created_by)
VALUES
(15,'PENDING','Póliza pendiente','Pendiente','POLICY_STATUS',TRUE,1,'system'),
(16,'ACTIVE','Póliza activa','Activa','POLICY_STATUS',TRUE,2,'system'),
(17,'APPROVED','Póliza aprobada','Aprobada','POLICY_STATUS',TRUE,3,'system'),
(18,'CANCELLED','Póliza cancelada','Cancelada','POLICY_STATUS',TRUE,4,'system'),
(19,'TERMINATED','Póliza terminada','Terminada','POLICY_STATUS',TRUE,5,'system'),
(20,'NOT_ASSIGNED','Póliza sin asignación','No Asignada','POLICY_STATUS',TRUE,6,'system'),
(21,'UNKNOWN','Estado desconocido','Desconocido','POLICY_STATUS',TRUE,7,'system')
ON CONFLICT (id) DO UPDATE SET
name = EXCLUDED.name,
description = EXCLUDED.description,
value = EXCLUDED.value,
parameter_group = EXCLUDED.parameter_group,
active = EXCLUDED.active,
sort_order = EXCLUDED.sort_order,
updated_at = CURRENT_TIMESTAMP,
updated_by = EXCLUDED.created_by;

-- GROUP: PRODUCER_ROLE_TYPE (22 - 27)
INSERT INTO conciliaciones.parameter (id, name, description, value, parameter_group, active, sort_order, created_by)
VALUES
(22,'PRIMARY_AGENT','Agente principal de la póliza','Agente Principal','PRODUCER_ROLE_TYPE',TRUE,1,'system'),
(23,'SUB_AGENT','Subagente asociado','Subagente','PRODUCER_ROLE_TYPE',TRUE,2,'system'),
(24,'MANAGER','Manager o supervisor','Manager','PRODUCER_ROLE_TYPE',TRUE,3,'system'),
(25,'AGENCY','Pago o rol asociado a agencia','Agencia','PRODUCER_ROLE_TYPE',TRUE,4,'system'),
(26,'PAYEE','Payee del carrier','Beneficiario de Pago','PRODUCER_ROLE_TYPE',TRUE,5,'system'),
(27,'UNKNOWN','Rol no determinado','Desconocido','PRODUCER_ROLE_TYPE',TRUE,6,'system')
ON CONFLICT (id) DO UPDATE SET
name = EXCLUDED.name,
description = EXCLUDED.description,
value = EXCLUDED.value,
parameter_group = EXCLUDED.parameter_group,
active = EXCLUDED.active,
sort_order = EXCLUDED.sort_order,
updated_at = CURRENT_TIMESTAMP,
updated_by = EXCLUDED.created_by;

-- GROUP: COMMISSION_RULE_TYPE (28 - 32)
INSERT INTO conciliaciones.parameter (id, name, description, value, parameter_group, active, sort_order, created_by)
VALUES
(28,'FIXED_AMOUNT','Regla por monto fijo','Monto Fijo','COMMISSION_RULE_TYPE',TRUE,1,'system'),
(29,'PERCENTAGE','Regla por porcentaje','Porcentaje','COMMISSION_RULE_TYPE',TRUE,2,'system'),
(30,'SPLIT_PERCENTAGE','Distribución porcentual','Split Porcentaje','COMMISSION_RULE_TYPE',TRUE,3,'system'),
(31,'VALIDATION_RULE','Regla de validación','Regla de Validación','COMMISSION_RULE_TYPE',TRUE,4,'system'),
(32,'EXCLUSION_RULE','Regla de exclusión','Regla de Exclusión','COMMISSION_RULE_TYPE',TRUE,5,'system')
ON CONFLICT (id) DO UPDATE SET
name = EXCLUDED.name,
description = EXCLUDED.description,
value = EXCLUDED.value,
parameter_group = EXCLUDED.parameter_group,
active = EXCLUDED.active,
sort_order = EXCLUDED.sort_order,
updated_at = CURRENT_TIMESTAMP,
updated_by = EXCLUDED.created_by;

-- GROUP: RECONCILIATION_CASE_TYPE (33 - 42)
INSERT INTO conciliaciones.parameter (id, name, description, value, parameter_group, active, sort_order, created_by)
VALUES
(33,'PAYMENT_ON_CANCELLED_POLICY','Se detectó pago a póliza cancelada','Pago sobre Póliza Cancelada','RECONCILIATION_CASE_TYPE',TRUE,1,'system'),
(34,'PAYMENT_WITHOUT_ASSIGNMENT','No existe asignación válida para pagar','Pago sin Asignación','RECONCILIATION_CASE_TYPE',TRUE,2,'system'),
(35,'DUPLICATE_COMMISSION','Registro de comisión duplicado','Comisión Duplicada','RECONCILIATION_CASE_TYPE',TRUE,3,'system'),
(36,'MISSING_POLICY','No se encontró la póliza','Póliza Faltante','RECONCILIATION_CASE_TYPE',TRUE,4,'system'),
(37,'AMOUNT_MISMATCH','Monto inconsistente','Diferencia de Monto','RECONCILIATION_CASE_TYPE',TRUE,5,'system'),
(38,'MISSING_PRODUCER','No se encontró productor','Productor Faltante','RECONCILIATION_CASE_TYPE',TRUE,6,'system'),
(39,'SPLIT_INCONSISTENCY','Split de pago inconsistente','Inconsistencia de Split','RECONCILIATION_CASE_TYPE',TRUE,7,'system'),
(40,'UNKNOWN_STATUS','Estado no homologado','Estado Desconocido','RECONCILIATION_CASE_TYPE',TRUE,8,'system'),
(41,'DUPLICATE_SOURCE_ROW','Fila duplicada','Fila de Origen Duplicada','RECONCILIATION_CASE_TYPE',TRUE,9,'system'),
(42,'INVALID_DATE_RANGE','Rango inconsistente','Rango de Fechas Inválido','RECONCILIATION_CASE_TYPE',TRUE,10,'system')
ON CONFLICT (id) DO UPDATE SET
name = EXCLUDED.name,
description = EXCLUDED.description,
value = EXCLUDED.value,
parameter_group = EXCLUDED.parameter_group,
active = EXCLUDED.active,
sort_order = EXCLUDED.sort_order,
updated_at = CURRENT_TIMESTAMP,
updated_by = EXCLUDED.created_by;

-- GROUP: SEVERITY_LEVEL (43 - 46)
INSERT INTO conciliaciones.parameter (id, name, description, value, parameter_group, active, sort_order, created_by)
VALUES
(43,'LOW','Severidad baja','Baja','SEVERITY_LEVEL',TRUE,1,'system'),
(44,'MEDIUM','Severidad media','Media','SEVERITY_LEVEL',TRUE,2,'system'),
(45,'HIGH','Severidad alta','Alta','SEVERITY_LEVEL',TRUE,3,'system'),
(46,'CRITICAL','Severidad crítica','Crítica','SEVERITY_LEVEL',TRUE,4,'system')
ON CONFLICT (id) DO UPDATE SET
name = EXCLUDED.name,
description = EXCLUDED.description,
value = EXCLUDED.value,
parameter_group = EXCLUDED.parameter_group,
active = EXCLUDED.active,
sort_order = EXCLUDED.sort_order,
updated_at = CURRENT_TIMESTAMP,
updated_by = EXCLUDED.created_by;

-- GROUP: CASE_STATUS (47 - 50)
INSERT INTO conciliaciones.parameter (id, name, description, value, parameter_group, active, sort_order, created_by)
VALUES
(47,'OPEN','Caso abierto','Abierto','CASE_STATUS',TRUE,1,'system'),
(48,'IN_REVIEW','Caso en revisión','En Revisión','CASE_STATUS',TRUE,2,'system'),
(49,'RESOLVED','Caso resuelto','Resuelto','CASE_STATUS',TRUE,3,'system'),
(50,'DISMISSED','Caso descartado','Descartado','CASE_STATUS',TRUE,4,'system')
ON CONFLICT (id) DO UPDATE SET
name = EXCLUDED.name,
description = EXCLUDED.description,
value = EXCLUDED.value,
parameter_group = EXCLUDED.parameter_group,
active = EXCLUDED.active,
sort_order = EXCLUDED.sort_order,
updated_at = CURRENT_TIMESTAMP,
updated_by = EXCLUDED.created_by;

-- GROUP: PAYMENT_STATUS (51 - 56)
INSERT INTO conciliaciones.parameter (id, name, description, value, parameter_group, active, sort_order, created_by)
VALUES
(51,'DRAFT','Liquidación en borrador','Borrador','PAYMENT_STATUS',TRUE,1,'system'),
(52,'GENERATED','Pago generado','Generado','PAYMENT_STATUS',TRUE,2,'system'),
(53,'APPROVED','Pago aprobado','Aprobado','PAYMENT_STATUS',TRUE,3,'system'),
(54,'REJECTED','Pago rechazado','Rechazado','PAYMENT_STATUS',TRUE,4,'system'),
(55,'PAID','Pago realizado','Pagado','PAYMENT_STATUS',TRUE,5,'system'),
(56,'CANCELLED','Pago cancelado','Cancelado','PAYMENT_STATUS',TRUE,6,'system')
ON CONFLICT (id) DO UPDATE SET
name = EXCLUDED.name,
description = EXCLUDED.description,
value = EXCLUDED.value,
parameter_group = EXCLUDED.parameter_group,
active = EXCLUDED.active,
sort_order = EXCLUDED.sort_order,
updated_at = CURRENT_TIMESTAMP,
updated_by = EXCLUDED.created_by;

-- GROUP: AUDIT_ACTION_TYPE (57 - 64)
INSERT INTO conciliaciones.parameter (id, name, description, value, parameter_group, active, sort_order, created_by)
VALUES
(57,'INSERT','Creación de registro','Inserción','AUDIT_ACTION_TYPE',TRUE,1,'system'),
(58,'UPDATE','Actualización de registro','Actualización','AUDIT_ACTION_TYPE',TRUE,2,'system'),
(59,'DELETE','Eliminación de registro','Eliminación','AUDIT_ACTION_TYPE',TRUE,3,'system'),
(60,'PROCESS','Ejecución de proceso','Procesamiento','AUDIT_ACTION_TYPE',TRUE,4,'system'),
(61,'APPROVE','Aprobación de operación','Aprobación','AUDIT_ACTION_TYPE',TRUE,5,'system'),
(62,'REJECT','Rechazo de operación','Rechazo','AUDIT_ACTION_TYPE',TRUE,6,'system'),
(63,'LOGIN','Autenticación en sistema','Inicio de Sesión','AUDIT_ACTION_TYPE',TRUE,7,'system'),
(64,'UPLOAD','Carga de archivo en sistema','Carga de Archivo','AUDIT_ACTION_TYPE',TRUE,8,'system')
ON CONFLICT (id) DO UPDATE SET
name = EXCLUDED.name,
description = EXCLUDED.description,
value = EXCLUDED.value,
parameter_group = EXCLUDED.parameter_group,
active = EXCLUDED.active,
sort_order = EXCLUDED.sort_order,
updated_at = CURRENT_TIMESTAMP,
updated_by = EXCLUDED.created_by;

-- GROUP: COMMISSION_RAW_STATUS (65 - 69)
INSERT INTO conciliaciones.parameter (id, name, description, value, parameter_group, active, sort_order, created_by)
VALUES
(65,'ACTIVE','Estado crudo activo','Activo','COMMISSION_RAW_STATUS',TRUE,1,'system'),
(66,'INACTIVE','Estado crudo inactivo','Inactivo','COMMISSION_RAW_STATUS',TRUE,2,'system'),
(67,'CANCELLED','Estado crudo cancelado','Cancelado','COMMISSION_RAW_STATUS',TRUE,3,'system'),
(68,'PENDING','Estado crudo pendiente','Pendiente','COMMISSION_RAW_STATUS',TRUE,4,'system'),
(69,'UNKNOWN','Estado crudo sin homologar','Desconocido','COMMISSION_RAW_STATUS',TRUE,5,'system')
ON CONFLICT (id) DO UPDATE SET
name = EXCLUDED.name,
description = EXCLUDED.description,
value = EXCLUDED.value,
parameter_group = EXCLUDED.parameter_group,
active = EXCLUDED.active,
sort_order = EXCLUDED.sort_order,
updated_at = CURRENT_TIMESTAMP,
updated_by = EXCLUDED.created_by;

-- GROUP: REASON_CODE (70 - 75)
INSERT INTO conciliaciones.parameter (id, name, description, value, parameter_group, active, sort_order, created_by)
VALUES
(70,'NEW_BUSINESS','Alta o nueva venta','Nuevo Negocio','REASON_CODE',TRUE,1,'system'),
(71,'RENEWAL','Renovación de póliza','Renovación','REASON_CODE',TRUE,2,'system'),
(72,'CANCELLATION','Cancelación de póliza','Cancelación','REASON_CODE',TRUE,3,'system'),
(73,'ADJUSTMENT','Ajuste monetario o técnico','Ajuste','REASON_CODE',TRUE,4,'system'),
(74,'RETRO','Movimiento retroactivo','Retroactivo','REASON_CODE',TRUE,5,'system'),
(75,'UNKNOWN','Código de razón no homologado','Desconocido','REASON_CODE',TRUE,6,'system')
ON CONFLICT (id) DO UPDATE SET
name = EXCLUDED.name,
description = EXCLUDED.description,
value = EXCLUDED.value,
parameter_group = EXCLUDED.parameter_group,
active = EXCLUDED.active,
sort_order = EXCLUDED.sort_order,
updated_at = CURRENT_TIMESTAMP,
updated_by = EXCLUDED.created_by;


-- GROUP: SOURCE_FILE_VALIDATION_TYPE (91 - 103)
INSERT INTO conciliaciones.parameter (id, name, description, value, parameter_group, active, sort_order, created_by)
VALUES
(91,'FILE_EXISTS','Valida que el archivo exista en el repositorio S3','Archivo Existe','SOURCE_FILE_VALIDATION_TYPE',TRUE,1,'system'),
(92,'FILE_EXTENSION','Valida que la extensión del archivo sea CSV','Extensión Archivo','SOURCE_FILE_VALIDATION_TYPE',TRUE,2,'system'),
(93,'FILE_NOT_EMPTY','Valida que el archivo no esté vacío','Archivo No Vacío','SOURCE_FILE_VALIDATION_TYPE',TRUE,3,'system'),
(94,'FILE_DELIMITER','Valida que el separador del archivo sea pipe','Separador Archivo','SOURCE_FILE_VALIDATION_TYPE',TRUE,4,'system'),
(95,'HEADER_EXISTS','Valida que el archivo tenga encabezado','Encabezado Existe','SOURCE_FILE_VALIDATION_TYPE',TRUE,5,'system'),
(96,'HEADER_STRUCTURE','Valida que el encabezado tenga las columnas esperadas','Estructura Encabezado','SOURCE_FILE_VALIDATION_TYPE',TRUE,6,'system'),
(97,'REQUIRED_FIELD','Valida campos obligatorios por fila','Campo Obligatorio','SOURCE_FILE_VALIDATION_TYPE',TRUE,7,'system'),
(98,'COLUMN_COUNT','Valida que la fila tenga la cantidad esperada de columnas','Cantidad Columnas','SOURCE_FILE_VALIDATION_TYPE',TRUE,8,'system'),
(99,'EMPTY_ROW','Valida filas vacías','Fila Vacía','SOURCE_FILE_VALIDATION_TYPE',TRUE,9,'system'),
(100,'INVALID_NUMBER','Valida campos numéricos','Número Inválido','SOURCE_FILE_VALIDATION_TYPE',TRUE,10,'system'),
(101,'INVALID_DATE','Valida campos de fecha','Fecha Inválida','SOURCE_FILE_VALIDATION_TYPE',TRUE,11,'system'),
(102,'VALIDATE_CARRIER','Valida que exista el carrier','Carrier Existe','SOURCE_FILE_VALIDATION_TYPE',TRUE,12,'system'),
(103,'VALIDATE_PRODUCER','Valida que exista el comisionista','Producer Existe','SOURCE_FILE_VALIDATION_TYPE',TRUE,13,'system')
ON CONFLICT (id) DO UPDATE SET
name = EXCLUDED.name,
description = EXCLUDED.description,
value = EXCLUDED.value,
parameter_group = EXCLUDED.parameter_group,
active = EXCLUDED.active,
sort_order = EXCLUDED.sort_order,
updated_at = CURRENT_TIMESTAMP,
updated_by = EXCLUDED.created_by;

-- GROUP: SOURCE_FILE_VALIDATION_STATUS (104 - 106)
INSERT INTO conciliaciones.parameter (id, name, description, value, parameter_group, active, sort_order, created_by)
VALUES
(104,'SUCCESS','Validación ejecutada correctamente','Exitoso','SOURCE_FILE_VALIDATION_STATUS',TRUE,1,'system'),
(105,'WARNING','Validación ejecutada con advertencia','Advertencia','SOURCE_FILE_VALIDATION_STATUS',TRUE,2,'system'),
(106,'ERROR','Validación ejecutada con error','Error','SOURCE_FILE_VALIDATION_STATUS',TRUE,3,'system')
ON CONFLICT (id) DO UPDATE SET
name = EXCLUDED.name,
description = EXCLUDED.description,
value = EXCLUDED.value,
parameter_group = EXCLUDED.parameter_group,
active = EXCLUDED.active,
sort_order = EXCLUDED.sort_order,
updated_at = CURRENT_TIMESTAMP,
updated_by = EXCLUDED.created_by;

-- GROUP: VALIDATION_SOURCE_PLAN_STATUS (107 - 110)
INSERT INTO conciliaciones.parameter (id, name, description, value, parameter_group, active, sort_order, created_by)
VALUES
(107,'PENDING','Plan de validación pendiente por iniciar','Pendiente','VALIDATION_SOURCE_PLAN_STATUS',TRUE,1,'system'),
(108,'PROCESS','Plan de validación en procesamiento','Proceso','VALIDATION_SOURCE_PLAN_STATUS',TRUE,2,'system'),
(109,'EXECUTED','Plan de validación ejecutado correctamente','Ejecutado','VALIDATION_SOURCE_PLAN_STATUS',TRUE,3,'system'),
(110,'FAILED','Plan de validación finalizado con error','Fallido','VALIDATION_SOURCE_PLAN_STATUS',TRUE,4,'system')
ON CONFLICT (id) DO UPDATE SET
name = EXCLUDED.name,
description = EXCLUDED.description,
value = EXCLUDED.value,
parameter_group = EXCLUDED.parameter_group,
active = EXCLUDED.active,
sort_order = EXCLUDED.sort_order,
updated_at = CURRENT_TIMESTAMP,
updated_by = EXCLUDED.created_by;

-- GROUP: VALIDATION_HEADER_STRUCTURE (111 - 123)
INSERT INTO conciliaciones.parameter (id, name, description, value, parameter_group, active, sort_order, created_by)
VALUES
(111,'carrier_code','Codigo de la Aseguradora','carrier_code_1','VALIDATION_HEADER_STRUCTURE',TRUE,1,'system'),
(112,'producer_external_id','Codigo del Comisionista','producer_external_id_1','VALIDATION_HEADER_STRUCTURE',TRUE,2,'system'),
(113,'client_id','Identificacion del Cliente','client_id_1','VALIDATION_HEADER_STRUCTURE',TRUE,3,'system'),
(114,'client_first_name','Primer nombre del cliente','client_first_name_1','VALIDATION_HEADER_STRUCTURE',TRUE,4,'system'),
(115,'client_middle_name','Nombre Corto del Cliente','client_middle_name_0','VALIDATION_HEADER_STRUCTURE',TRUE,5,'system'),
(116,'client_last_name','Apellido del Cliente','client_last_name_1','VALIDATION_HEADER_STRUCTURE',TRUE,6,'system'),
(117,'policy_number','Identificador del Número de Poliza','policy_number_1','VALIDATION_HEADER_STRUCTURE',TRUE,7,'system'),
(118,'status_id','Estado de Poliza','status_id_1','VALIDATION_HEADER_STRUCTURE',TRUE,8,'system'),
(119,'subscriber_id','Id de la Poliza','subscriber_id_1','VALIDATION_HEADER_STRUCTURE',TRUE,9,'system'),
(120,'members_count','Número de cuentas','members_count_1','VALIDATION_HEADER_STRUCTURE',TRUE,10,'system'),
(121,'statement_date','Fecha Poliza','statement_date_1','VALIDATION_HEADER_STRUCTURE',TRUE,11,'system'),
(122,'paid_date','Fecha de Pago','paid_date_1','VALIDATION_HEADER_STRUCTURE',TRUE,12,'system'),
(123,'net_amount','Monto Neto','net_amount_1','VALIDATION_HEADER_STRUCTURE',TRUE,13,'system'),
(124,'rate','Tasa','rate_1','VALIDATION_HEADER_STRUCTURE',TRUE,14,'system'),
(125,'commission_rate_pct','Porcentaje de Comisión','commission_rate_pct_1','VALIDATION_HEADER_STRUCTURE',TRUE,15,'system')
ON CONFLICT (id) DO UPDATE SET
name = EXCLUDED.name,
description = EXCLUDED.description,
value = EXCLUDED.value,
parameter_group = EXCLUDED.parameter_group,
active = EXCLUDED.active,
sort_order = EXCLUDED.sort_order,
updated_at = CURRENT_TIMESTAMP,
updated_by = EXCLUDED.created_by;

-- =========================================================
-- 19. PAISES
-- =========================================================

INSERT INTO conciliaciones.country (code, name, active)
VALUES ('US', 'United States', TRUE)
ON CONFLICT (code) DO UPDATE SET
    name = EXCLUDED.name,
    active = EXCLUDED.active;

-- States
INSERT INTO conciliaciones.state (country_id, code, name, active)
VALUES
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'AL', 'Alabama', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'AK', 'Alaska', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'AZ', 'Arizona', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'AR', 'Arkansas', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'CA', 'California', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'CO', 'Colorado', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'CT', 'Connecticut', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'DE', 'Delaware', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'FL', 'Florida', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'GA', 'Georgia', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'HI', 'Hawaii', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'ID', 'Idaho', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'IL', 'Illinois', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'IN', 'Indiana', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'IA', 'Iowa', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'KS', 'Kansas', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'KY', 'Kentucky', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'LA', 'Louisiana', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'ME', 'Maine', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'MD', 'Maryland', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'MA', 'Massachusetts', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'MI', 'Michigan', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'MN', 'Minnesota', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'MS', 'Mississippi', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'MO', 'Missouri', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'MT', 'Montana', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'NE', 'Nebraska', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'NV', 'Nevada', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'NH', 'New Hampshire', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'NJ', 'New Jersey', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'NM', 'New Mexico', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'NY', 'New York', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'NC', 'North Carolina', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'ND', 'North Dakota', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'OH', 'Ohio', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'OK', 'Oklahoma', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'OR', 'Oregon', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'PA', 'Pennsylvania', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'RI', 'Rhode Island', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'SC', 'South Carolina', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'SD', 'South Dakota', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'TN', 'Tennessee', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'TX', 'Texas', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'UT', 'Utah', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'VT', 'Vermont', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'VA', 'Virginia', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'WA', 'Washington', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'WV', 'West Virginia', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'WI', 'Wisconsin', TRUE),
((SELECT id FROM conciliaciones.country WHERE code = 'US'), 'WY', 'Wyoming', TRUE)
ON CONFLICT (country_id, code) DO UPDATE SET
    name = EXCLUDED.name,
    active = EXCLUDED.active;

-- Alabama
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'AL' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Birmingham', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'AL' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Montgomery', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'AL' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Mobile', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'AL' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Huntsville', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'AL' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Tuscaloosa', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- Alaska
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'AK' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Anchorage', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'AK' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Fairbanks', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'AK' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Juneau', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'AK' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Sitka', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'AK' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Ketchikan', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- Arizona
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'AZ' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Phoenix', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'AZ' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Tucson', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'AZ' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Mesa', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'AZ' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Chandler', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'AZ' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Scottsdale', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- Arkansas
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'AR' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Little Rock', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'AR' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Fort Smith', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'AR' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Fayetteville', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'AR' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Springdale', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'AR' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Jonesboro', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- California
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'CA' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Los Angeles', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'CA' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'San Diego', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'CA' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'San Jose', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'CA' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'San Francisco', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'CA' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Sacramento', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- Colorado
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'CO' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Denver', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'CO' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Colorado Springs', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'CO' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Aurora', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'CO' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Fort Collins', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'CO' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Lakewood', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- Connecticut
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'CT' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Bridgeport', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'CT' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'New Haven', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'CT' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Stamford', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'CT' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Hartford', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'CT' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Waterbury', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- Delaware
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'DE' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Wilmington', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'DE' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Dover', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'DE' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Newark', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'DE' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Middletown', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'DE' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Smyrna', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- Florida
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'FL' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Jacksonville', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'FL' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Miami', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'FL' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Tampa', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'FL' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Orlando', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'FL' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Fort Lauderdale', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- Georgia
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'GA' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Atlanta', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'GA' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Augusta', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'GA' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Columbus', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'GA' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Savannah', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'GA' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Athens', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- Hawaii
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'HI' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Honolulu', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'HI' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Hilo', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'HI' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Kailua', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'HI' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Kapolei', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'HI' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Kaneohe', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- Idaho
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'ID' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Boise', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'ID' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Meridian', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'ID' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Nampa', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'ID' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Idaho Falls', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'ID' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Pocatello', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- Illinois
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'IL' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Chicago', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'IL' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Aurora', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'IL' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Naperville', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'IL' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Joliet', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'IL' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Rockford', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- Indiana
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'IN' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Indianapolis', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'IN' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Fort Wayne', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'IN' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Evansville', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'IN' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'South Bend', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'IN' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Carmel', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- Iowa
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'IA' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Des Moines', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'IA' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Cedar Rapids', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'IA' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Davenport', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'IA' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Sioux City', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'IA' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Iowa City', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- Kansas
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'KS' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Wichita', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'KS' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Overland Park', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'KS' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Kansas City', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'KS' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Olathe', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'KS' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Topeka', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- Kentucky
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'KY' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Louisville', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'KY' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Lexington', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'KY' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Bowling Green', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'KY' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Owensboro', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'KY' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Covington', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- Louisiana
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'LA' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'New Orleans', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'LA' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Baton Rouge', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'LA' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Shreveport', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'LA' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Lafayette', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'LA' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Lake Charles', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- Maine
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'ME' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Portland', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'ME' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Lewiston', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'ME' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Bangor', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'ME' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'South Portland', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'ME' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Auburn', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- Maryland
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'MD' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Baltimore', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'MD' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Frederick', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'MD' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Rockville', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'MD' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Gaithersburg', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'MD' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Bowie', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- Massachusetts
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'MA' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Boston', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'MA' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Worcester', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'MA' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Springfield', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'MA' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Cambridge', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'MA' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Lowell', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- Michigan
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'MI' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Detroit', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'MI' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Grand Rapids', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'MI' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Warren', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'MI' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Sterling Heights', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'MI' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Ann Arbor', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- Minnesota
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'MN' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Minneapolis', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'MN' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Saint Paul', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'MN' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Rochester', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'MN' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Duluth', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'MN' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Bloomington', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- Mississippi
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'MS' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Jackson', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'MS' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Gulfport', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'MS' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Southaven', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'MS' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Biloxi', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'MS' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Hattiesburg', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- Missouri
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'MO' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Kansas City', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'MO' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Saint Louis', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'MO' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Springfield', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'MO' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Columbia', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'MO' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Independence', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- Montana
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'MT' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Billings', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'MT' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Missoula', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'MT' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Great Falls', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'MT' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Bozeman', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'MT' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Butte', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- Nebraska
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'NE' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Omaha', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'NE' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Lincoln', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'NE' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Bellevue', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'NE' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Grand Island', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'NE' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Kearney', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- Nevada
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'NV' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Las Vegas', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'NV' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Henderson', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'NV' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Reno', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'NV' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'North Las Vegas', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'NV' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Sparks', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- New Hampshire
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'NH' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Manchester', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'NH' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Nashua', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'NH' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Concord', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'NH' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Dover', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'NH' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Rochester', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- New Jersey
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'NJ' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Newark', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'NJ' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Jersey City', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'NJ' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Paterson', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'NJ' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Elizabeth', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'NJ' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Edison', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- New Mexico
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'NM' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Albuquerque', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'NM' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Las Cruces', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'NM' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Rio Rancho', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'NM' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Santa Fe', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'NM' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Roswell', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- New York
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'NY' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'New York', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'NY' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Buffalo', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'NY' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Rochester', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'NY' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Yonkers', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'NY' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Syracuse', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- North Carolina
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'NC' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Charlotte', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'NC' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Raleigh', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'NC' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Greensboro', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'NC' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Durham', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'NC' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Winston-Salem', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- North Dakota
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'ND' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Fargo', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'ND' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Bismarck', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'ND' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Grand Forks', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'ND' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Minot', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'ND' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'West Fargo', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- Ohio
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'OH' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Columbus', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'OH' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Cleveland', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'OH' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Cincinnati', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'OH' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Toledo', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'OH' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Akron', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- Oklahoma
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'OK' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Oklahoma City', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'OK' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Tulsa', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'OK' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Norman', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'OK' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Broken Arrow', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'OK' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Lawton', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- Oregon
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'OR' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Portland', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'OR' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Eugene', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'OR' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Salem', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'OR' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Gresham', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'OR' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Hillsboro', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- Pennsylvania
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'PA' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Philadelphia', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'PA' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Pittsburgh', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'PA' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Allentown', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'PA' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Erie', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'PA' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Reading', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- Rhode Island
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'RI' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Providence', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'RI' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Warwick', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'RI' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Cranston', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'RI' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Pawtucket', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'RI' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'East Providence', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- South Carolina
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'SC' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Charleston', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'SC' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Columbia', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'SC' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'North Charleston', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'SC' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Mount Pleasant', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'SC' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Rock Hill', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- South Dakota
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'SD' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Sioux Falls', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'SD' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Rapid City', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'SD' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Aberdeen', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'SD' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Brookings', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'SD' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Watertown', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- Tennessee
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'TN' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Nashville', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'TN' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Memphis', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'TN' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Knoxville', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'TN' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Chattanooga', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'TN' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Clarksville', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- Texas
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'TX' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Houston', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'TX' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'San Antonio', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'TX' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Dallas', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'TX' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Austin', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'TX' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Fort Worth', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- Utah
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'UT' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Salt Lake City', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'UT' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'West Valley City', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'UT' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Provo', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'UT' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'West Jordan', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'UT' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Orem', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- Vermont
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'VT' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Burlington', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'VT' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'South Burlington', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'VT' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Rutland', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'VT' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Barre', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'VT' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Montpelier', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- Virginia
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'VA' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Virginia Beach', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'VA' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Chesapeake', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'VA' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Norfolk', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'VA' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Richmond', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'VA' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Arlington', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- Washington
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'WA' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Seattle', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'WA' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Spokane', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'WA' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Tacoma', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'WA' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Vancouver', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'WA' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Bellevue', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- West Virginia
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'WV' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Charleston', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'WV' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Huntington', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'WV' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Morgantown', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'WV' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Parkersburg', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'WV' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Wheeling', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- Wisconsin
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'WI' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Milwaukee', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'WI' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Madison', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'WI' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Green Bay', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'WI' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Kenosha', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'WI' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Racine', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- Wyoming
INSERT INTO conciliaciones.city (state_id, name, active)
VALUES
((SELECT id FROM conciliaciones.state WHERE code = 'WY' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Cheyenne', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'WY' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Casper', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'WY' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Laramie', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'WY' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Gillette', TRUE),
((SELECT id FROM conciliaciones.state WHERE code = 'WY' AND country_id = (SELECT id FROM conciliaciones.country WHERE code = 'US')), 'Rock Springs', TRUE)
ON CONFLICT (state_id, name) DO UPDATE SET
    active = EXCLUDED.active;

-- Migrations
ALTER TABLE conciliaciones.commission_statement ALTER COLUMN source_file_id DROP NOT NULL;