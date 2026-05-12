SET search_path TO reconciliation;

CREATE TABLE IF NOT EXISTS security_audit_log (
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
-- LIMPIEZA OPCIONAL PARA REEJECUTAR PRUEBAS
-- =========================================================
DELETE FROM execution_plan_task;
DELETE FROM scheduled_task;
DELETE FROM security_audit_log;
DELETE FROM commission_payment_detail;
DELETE FROM security_audit_log;
DELETE FROM commission_payment;
DELETE FROM reconciliation_case;
DELETE FROM commission_statement_item;
DELETE FROM commission_statement;
DELETE FROM commission_rule;
DELETE FROM commission_assignment;
DELETE FROM policy_status_history;
DELETE FROM policy_plan;
DELETE FROM policy;
DELETE FROM raw_import_record;
DELETE FROM source_file_sheet;
DELETE FROM source_file;
DELETE FROM client;
DELETE FROM producer;
DELETE FROM agency;

-- =========================================================
-- 1. AGENCIAS
-- =========================================================
INSERT INTO agency (
    carrier_id,
    external_agency_id,
    name,
    active,
    created_by
)
VALUES
(
    (SELECT id FROM carrier WHERE code = 'SENTARA'),
    '13192',
    'NOHELIA ISABEL NAVARRO FANDINO',
    TRUE,
    'seed'
),
(
    (SELECT id FROM carrier WHERE code = 'SAFEGUARD'),
    'SG-001',
    'SAFEGUARD AGENCY GROUP',
    TRUE,
    'seed'
),
(
    (SELECT id FROM carrier WHERE code = 'UNITED_H_ONE'),
    'UH1-001',
    'UNITED H ONE SALES GROUP',
    TRUE,
    'seed'
);

-- =========================================================
-- 2. PRODUCTORES / AGENTES
-- =========================================================
INSERT INTO producer (
    agency_id,
    external_producer_id,
    first_name,
    last_name,
    full_name,
    email,
    phone,
    npn,
    tax_id_masked,
    active,
    created_by
)
VALUES
(
    (SELECT id FROM agency WHERE external_agency_id = '13192'),
    '20253546',
    'Nohelia',
    'Navarro-Fandino',
    'NOHELIA I NAVARRO FANDINO',
    'nohelia@example.com',
    '555-1001',
    '20253546',
    'XXX-XX-9657',
    TRUE,
    'seed'
),
(
    (SELECT id FROM agency WHERE external_agency_id = 'UH1-001'),
    '21156867',
    'Leonardo',
    'Lopez',
    'LEONARDO LOPEZ',
    'leonardo.lopez@example.com',
    '555-1002',
    '21156867',
    'XXX-XX-1234',
    TRUE,
    'seed'
),
(
    (SELECT id FROM agency WHERE external_agency_id = '13192'),
    'SUB-0001',
    'Manager',
    'Demo',
    'MANAGER DEMO',
    'manager.demo@example.com',
    '555-1003',
    '30000001',
    'XXX-XX-0001',
    TRUE,
    'seed'
);

-- =========================================================
-- 3. CLIENTES
-- =========================================================
INSERT INTO client (
    external_client_id,
    first_name,
    middle_name,
    last_name,
    full_name,
    birth_date,
    state,
    active,
    created_by
)
VALUES
('EL-0001', 'ALCIDES', NULL, 'BRITO FAUKNER', 'ALCIDES BRITO FAUKNER', NULL, 'PA', TRUE, 'seed'),
('EL-0002', 'ANA MARIA', NULL, 'DUARTE PASCUAL', 'ANA MARIA DUARTE PASCUAL', NULL, 'AL', TRUE, 'seed'),
('EL-0003', 'ANDRES JULIAN', NULL, 'RAMIREZ BENITEZ', 'ANDRES JULIAN RAMIREZ BENITEZ', NULL, 'PA', TRUE, 'seed'),
('I2571344', 'JONATHAN', NULL, 'VENTURA NUNEZ', 'JONATHAN VENTURA NUNEZ', NULL, 'FL', TRUE, 'seed'),
('I2638704', 'SINDY JUDITH', NULL, 'ESCAMILLA REYES', 'SINDY JUDITH ESCAMILLA REYES', NULL, 'FL', TRUE, 'seed'),
('I2659684', 'MIRIAM', NULL, 'BLANCO-MORALES', 'MIRIAM BLANCO-MORALES', NULL, 'FL', TRUE, 'seed'),
('12793741001', 'ADA', NULL, 'COREAS RIVERA', 'ADA COREAS RIVERA', NULL, 'FL', TRUE, 'seed'),
('U98544537', 'ADIANEZ', NULL, 'AQUINO GONZALEZ', 'ADIANEZ AQUINO GONZALEZ', NULL, 'FL', TRUE, 'seed'),
('450788742', 'EVELYN', NULL, 'VERDAYES ROJAS', 'EVELYN VERDAYES ROJAS', NULL, 'FL', TRUE, 'seed'),
('443036458', 'FRANCISCO JAVIE', NULL, 'RICO', 'FRANCISCO JAVIE RICO', NULL, 'FL', TRUE, 'seed'),
('443135199', 'JOSE OMAR', NULL, 'ESCOBAR', 'JOSE OMAR ESCOBAR', NULL, 'MD', TRUE, 'seed');

-- =========================================================
-- 4. ARCHIVOS FUENTE
-- =========================================================
INSERT INTO source_file (
    carrier_id,
    original_file_name,
    stored_file_name,
    file_extension,
    mime_type,
    file_size_bytes,
    s3_bucket,
    s3_key,
    checksum_sha256,
    source_system,
    upload_date,
    uploaded_by,
    processing_status_id,
    error_message,
    total_rows,
    processed_rows,
    failed_rows,
    created_by
)
VALUES
(
    (SELECT id FROM carrier WHERE code = 'ELITE'),
    'elite_commissions_dec_jan.xlsx',
    '2026/03/elite_commissions_dec_jan.xlsx',
    'xlsx',
    'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    12000,
    'local-test-bucket',
    'elite/2026/03/elite_commissions_dec_jan.xlsx',
    'sha256-elite-001',
    'ELITE_PORTAL',
    CURRENT_TIMESTAMP,
    'seed_user',
    (SELECT id FROM parameter WHERE parameter_group = 'SOURCE_FILE_STATUS' AND name = 'PROCESSED'),
    NULL,
    30,
    30,
    0,
    'seed'
),
(
    (SELECT id FROM carrier WHERE code = 'SENTARA'),
    'sentara_statement_20260309.xlsx',
    '2026/03/sentara_statement_20260309.xlsx',
    'xlsx',
    'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    28000,
    'local-test-bucket',
    'sentara/2026/03/sentara_statement_20260309.xlsx',
    'sha256-sentara-001',
    'SENTARA_PORTAL',
    CURRENT_TIMESTAMP,
    'seed_user',
    (SELECT id FROM parameter WHERE parameter_group = 'SOURCE_FILE_STATUS' AND name = 'PROCESSED'),
    NULL,
    140,
    140,
    0,
    'seed'
),
(
    (SELECT id FROM carrier WHERE code = 'SAFEGUARD'),
    'safeguard_paid_20260331.xlsx',
    '2026/03/safeguard_paid_20260331.xlsx',
    'xlsx',
    'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    41000,
    'local-test-bucket',
    'safeguard/2026/03/safeguard_paid_20260331.xlsx',
    'sha256-safeguard-001',
    'SAFEGUARD_PORTAL',
    CURRENT_TIMESTAMP,
    'seed_user',
    (SELECT id FROM parameter WHERE parameter_group = 'SOURCE_FILE_STATUS' AND name = 'PROCESSED'),
    NULL,
    497,
    497,
    0,
    'seed'
),
(
    (SELECT id FROM carrier WHERE code = 'UNITED_H_ONE'),
    'united_h_one_extract_20260317.xlsx',
    '2026/03/united_h_one_extract_20260317.xlsx',
    'xlsx',
    'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    35000,
    'local-test-bucket',
    'united_h_one/2026/03/united_h_one_extract_20260317.xlsx',
    'sha256-united-001',
    'UNITED_PORTAL',
    CURRENT_TIMESTAMP,
    'seed_user',
    (SELECT id FROM parameter WHERE parameter_group = 'SOURCE_FILE_STATUS' AND name = 'PROCESSED'),
    NULL,
    120,
    120,
    0,
    'seed'
);

-- =========================================================
-- 5. HOJAS DE ARCHIVO
-- =========================================================
INSERT INTO source_file_sheet (
    source_file_id,
    sheet_name,
    sheet_order,
    total_rows,
    created_by
)
VALUES
(
    (SELECT id FROM source_file WHERE original_file_name = 'elite_commissions_dec_jan.xlsx'),
    'ELITE',
    1,
    30,
    'seed'
),
(
    (SELECT id FROM source_file WHERE original_file_name = 'sentara_statement_20260309.xlsx'),
    'SENTARA .',
    1,
    140,
    'seed'
),
(
    (SELECT id FROM source_file WHERE original_file_name = 'safeguard_paid_20260331.xlsx'),
    'SAFEGUARD',
    1,
    497,
    'seed'
),
(
    (SELECT id FROM source_file WHERE original_file_name = 'united_h_one_extract_20260317.xlsx'),
    'UNITED H ONE ',
    1,
    120,
    'seed'
);

-- =========================================================
-- 6. RAW IMPORT RECORD
-- =========================================================
INSERT INTO raw_import_record (
    source_file_id,
    source_file_sheet_id,
    row_number,
    source_row_key,
    raw_payload,
    parse_status_id,
    parse_error,
    created_by
)
VALUES
(
    (SELECT id FROM source_file WHERE original_file_name = 'elite_commissions_dec_jan.xlsx'),
    (SELECT id FROM source_file_sheet WHERE sheet_name = 'ELITE'),
    1,
    'ELITE-1',
    '{
      "CARRIER":"Ambetter",
      "CLIENT/TITLE":"ALCIDES BRITO FAUKNER",
      "STATE":"PA",
      "STATUS":"Approved",
      "RATE":30,
      "APPLICANTS":1,
      "SUBTOTAL":30,
      "MONTH PAID":"2025-12-01"
    }'::jsonb,
    (SELECT id FROM parameter WHERE parameter_group = 'PARSE_STATUS' AND name = 'PARSED'),
    NULL,
    'seed'
),
(
    (SELECT id FROM source_file WHERE original_file_name = 'elite_commissions_dec_jan.xlsx'),
    (SELECT id FROM source_file_sheet WHERE sheet_name = 'ELITE'),
    2,
    'ELITE-2',
    '{
      "CARRIER":"Ambetter",
      "CLIENT/TITLE":"ANA MARIA DUARTE PASCUAL",
      "STATE":"AL",
      "STATUS":"Approved",
      "RATE":20,
      "APPLICANTS":3,
      "SUBTOTAL":60,
      "MONTH PAID":"2026-01-01"
    }'::jsonb,
    (SELECT id FROM parameter WHERE parameter_group = 'PARSE_STATUS' AND name = 'PARSED'),
    NULL,
    'seed'
),
(
    (SELECT id FROM source_file WHERE original_file_name = 'elite_commissions_dec_jan.xlsx'),
    (SELECT id FROM source_file_sheet WHERE sheet_name = 'ELITE'),
    3,
    'ELITE-3',
    '{
      "CARRIER":"Ambetter",
      "CLIENT/TITLE":"ANDRES JULIAN RAMIREZ BENITEZ",
      "STATE":"PA",
      "STATUS":"Not Assigned",
      "RATE":30,
      "APPLICANTS":2,
      "SUBTOTAL":0,
      "MONTH PAID":"2026-01-01"
    }'::jsonb,
    (SELECT id FROM parameter WHERE parameter_group = 'PARSE_STATUS' AND name = 'PARSED'),
    NULL,
    'seed'
),

(
    (SELECT id FROM source_file WHERE original_file_name = 'sentara_statement_20260309.xlsx'),
    (SELECT id FROM source_file_sheet WHERE sheet_name = 'SENTARA .'),
    1,
    '2026-03-09 00:00:00-NOHELIA I NAVARRO FANDINO-I2571344-7846156',
    '{
      "Row ID":"2026-03-09 00:00:00-NOHELIA I NAVARRO FANDINO-I2571344-7846156",
      "CHECK DATE":"2026-03-09",
      "LAST PAY DATE":"2026-02-01",
      "AGENCY #":"13192",
      "AGENCY (group)":"NOHELIA ISABEL NAVARRO FANDINO",
      "CLIENT #":"I2571344",
      "CLIENT":"JONATHAN VENTURA NUNEZ",
      "INVOICE #":"7846156",
      "AGENT":"NOHELIA I NAVARRO FANDINO",
      "COMM RATE":20,
      "PREMIUM":0,
      "NET":40,
      "CONTRACT COUNT":1,
      "RETRO COUNT":1
    }'::jsonb,
    (SELECT id FROM parameter WHERE parameter_group = 'PARSE_STATUS' AND name = 'PARSED'),
    NULL,
    'seed'
),
(
    (SELECT id FROM source_file WHERE original_file_name = 'sentara_statement_20260309.xlsx'),
    (SELECT id FROM source_file_sheet WHERE sheet_name = 'SENTARA .'),
    2,
    '2026-03-09 00:00:00-NOHELIA I NAVARRO FANDINO-I2638704-7846157',
    '{
      "Row ID":"2026-03-09 00:00:00-NOHELIA I NAVARRO FANDINO-I2638704-7846157",
      "CHECK DATE":"2026-03-09",
      "LAST PAY DATE":"2026-02-01",
      "AGENCY #":"13192",
      "AGENCY (group)":"NOHELIA ISABEL NAVARRO FANDINO",
      "CLIENT #":"I2638704",
      "CLIENT":"SINDY JUDITH ESCAMILLA REYES",
      "INVOICE #":"7846157",
      "AGENT":"NOHELIA I NAVARRO FANDINO",
      "COMM RATE":20,
      "PREMIUM":0,
      "NET":60,
      "CONTRACT COUNT":3,
      "RETRO COUNT":0
    }'::jsonb,
    (SELECT id FROM parameter WHERE parameter_group = 'PARSE_STATUS' AND name = 'PARSED'),
    NULL,
    'seed'
),

(
    (SELECT id FROM source_file WHERE original_file_name = 'safeguard_paid_20260331.xlsx'),
    (SELECT id FROM source_file_sheet WHERE sheet_name = 'SAFEGUARD'),
    1,
    'SAFEGUARD-1',
    '{
      "Agent Name":"Nohelia I Navarro Fandino",
      "Client Name":"Ada Coreas Rivera",
      "Effective Date":"2026-01-01",
      "Paid Date":"2026-03-31",
      "Cancel":0,
      "Suscriber ID":"12793741001",
      "Num Members":1,
      "Month Amount":18
    }'::jsonb,
    (SELECT id FROM parameter WHERE parameter_group = 'PARSE_STATUS' AND name = 'PARSED'),
    NULL,
    'seed'
),
(
    (SELECT id FROM source_file WHERE original_file_name = 'safeguard_paid_20260331.xlsx'),
    (SELECT id FROM source_file_sheet WHERE sheet_name = 'SAFEGUARD'),
    2,
    'SAFEGUARD-2',
    '{
      "Agent Name":"Nohelia I Navarro Fandino",
      "Client Name":"Adianez Aquino Gonzalez",
      "Effective Date":"2024-03-01",
      "Paid Date":"2026-12-31",
      "Cancel":0,
      "Suscriber ID":"U98544537",
      "Num Members":4,
      "Month Amount":120
    }'::jsonb,
    (SELECT id FROM parameter WHERE parameter_group = 'PARSE_STATUS' AND name = 'PARSED'),
    NULL,
    'seed'
),

(
    (SELECT id FROM source_file WHERE original_file_name = 'united_h_one_extract_20260317.xlsx'),
    (SELECT id FROM source_file_sheet WHERE sheet_name = 'UNITED H ONE '),
    1,
    'UNITED-450788742-MANAGER',
    '{
      "Reporting Company/Carrier":"GRIC",
      "Extract Date":"2026-03-17",
      "Role for Payment Level":"MANAGER",
      "Payee Name":"Nohelia I Navarro-Fandino",
      "Producer First Name":"Leonardo",
      "Producer Last Name":"Lopez",
      "Producer Id (NPN or VUE Id)":"21156867",
      "Policy Number":"450788742",
      "Policy Effective Date":"2026-01-13",
      "Policyholder First Name":"EVELYN",
      "Policyholder Last Name":"VERDAYES ROJAS",
      "Policy Resident State":"FL",
      "Policy Issue State":"FL",
      "Base Medical Plan Name":"DentalWise Basic",
      "Coverage Plan Name":"DentalWise Base",
      "Policy Mode":"Monthly",
      "Payment Type":"F",
      "Policy - # of Lives":1,
      "Policy - Gross Premium":34.50,
      "Policy - Commissionable Premium":34.50,
      "Commission Amount Calculated":0.00,
      "Commission Rate Percentage":1.50,
      "Reason Code":"APP ADV"
    }'::jsonb,
    (SELECT id FROM parameter WHERE parameter_group = 'PARSE_STATUS' AND name = 'PARSED'),
    NULL,
    'seed'
),
(
    (SELECT id FROM source_file WHERE original_file_name = 'united_h_one_extract_20260317.xlsx'),
    (SELECT id FROM source_file_sheet WHERE sheet_name = 'UNITED H ONE '),
    2,
    'UNITED-443036458-SUB',
    '{
      "Reporting Company/Carrier":"GRIC",
      "Extract Date":"2026-03-17",
      "Role for Payment Level":"SUB",
      "Payee Name":"Nohelia I Navarro-Fandino",
      "Producer First Name":"Nohelia",
      "Producer Last Name":"Navarro-Fandino",
      "Producer Id (NPN or VUE Id)":"20253546",
      "Policy Number":"443036458",
      "Policy Effective Date":"2024-11-20",
      "Policyholder First Name":"FRANCISCO JAVIE",
      "Policyholder Last Name":"RICO",
      "Policy Resident State":"FL",
      "Policy Issue State":"FL",
      "Base Medical Plan Name":"VisionWise Premier",
      "Coverage Plan Name":"VisionWise Base",
      "Policy Mode":"Monthly",
      "Payment Type":"R",
      "Policy - # of Lives":1,
      "Policy - Gross Premium":13.28,
      "Policy - Commissionable Premium":13.28,
      "Commission Amount Calculated":0.96,
      "Commission Rate Percentage":7.25,
      "Reason Code":"COMM"
    }'::jsonb,
    (SELECT id FROM parameter WHERE parameter_group = 'PARSE_STATUS' AND name = 'PARSED'),
    NULL,
    'seed'
),
(
    (SELECT id FROM source_file WHERE original_file_name = 'united_h_one_extract_20260317.xlsx'),
    (SELECT id FROM source_file_sheet WHERE sheet_name = 'UNITED H ONE '),
    3,
    'UNITED-443135199-MANAGER',
    '{
      "Reporting Company/Carrier":"GRIC",
      "Extract Date":"2026-03-17",
      "Role for Payment Level":"MANAGER",
      "Payee Name":"Nohelia I Navarro-Fandino",
      "Producer First Name":"Nohelia",
      "Producer Last Name":"Navarro-Fandino",
      "Producer Id (NPN or VUE Id)":"20253546",
      "Policy Number":"443135199",
      "Policy Effective Date":"2024-12-19",
      "Policyholder First Name":"JOSE OMAR",
      "Policyholder Last Name":"ESCOBAR",
      "Policy Resident State":"MD",
      "Policy Issue State":"MD",
      "Base Medical Plan Name":"VisionWise Premier",
      "Coverage Plan Name":"VisionWise Base",
      "Policy Mode":"Monthly",
      "Payment Type":"R",
      "Policy - # of Lives":1,
      "Policy - Gross Premium":13.28,
      "Policy - Commissionable Premium":13.28,
      "Commission Amount Calculated":0.00,
      "Commission Rate Percentage":0.00,
      "Reason Code":"OVR"
    }'::jsonb,
    (SELECT id FROM parameter WHERE parameter_group = 'PARSE_STATUS' AND name = 'PARSED'),
    NULL,
    'seed'
);

-- =========================================================
-- 7. POLIZAS
-- =========================================================
INSERT INTO policy (
    carrier_id,
    client_id,
    policy_number,
    subscriber_id,
    effective_date,
    issue_date,
    termination_date,
    status_id,
    resident_state,
    issue_state,
    members_count,
    source_key,
    active,
    created_by
)
VALUES
(
    (SELECT id FROM carrier WHERE code = 'ELITE'),
    (SELECT id FROM client WHERE external_client_id = 'EL-0001'),
    'ELITE-PA-0001',
    'ELSUB-0001',
    DATE '2025-12-01',
    DATE '2025-12-01',
    NULL,
    (SELECT id FROM parameter WHERE parameter_group = 'POLICY_STATUS' AND name = 'APPROVED'),
    'PA',
    'PA',
    1,
    'ELITE-1',
    TRUE,
    'seed'
),
(
    (SELECT id FROM carrier WHERE code = 'ELITE'),
    (SELECT id FROM client WHERE external_client_id = 'EL-0002'),
    'ELITE-AL-0002',
    'ELSUB-0002',
    DATE '2026-01-01',
    DATE '2026-01-01',
    NULL,
    (SELECT id FROM parameter WHERE parameter_group = 'POLICY_STATUS' AND name = 'APPROVED'),
    'AL',
    'AL',
    3,
    'ELITE-2',
    TRUE,
    'seed'
),
(
    (SELECT id FROM carrier WHERE code = 'ELITE'),
    (SELECT id FROM client WHERE external_client_id = 'EL-0003'),
    'ELITE-PA-0003',
    'ELSUB-0003',
    DATE '2026-01-01',
    DATE '2026-01-01',
    NULL,
    (SELECT id FROM parameter WHERE parameter_group = 'POLICY_STATUS' AND name = 'NOT_ASSIGNED'),
    'PA',
    'PA',
    2,
    'ELITE-3',
    TRUE,
    'seed'
),
(
    (SELECT id FROM carrier WHERE code = 'SENTARA'),
    (SELECT id FROM client WHERE external_client_id = 'I2571344'),
    'SENTARA-I2571344',
    'I2571344',
    DATE '2026-02-01',
    DATE '2026-02-01',
    NULL,
    (SELECT id FROM parameter WHERE parameter_group = 'POLICY_STATUS' AND name = 'ACTIVE'),
    'FL',
    'FL',
    1,
    'SENTARA-1',
    TRUE,
    'seed'
),
(
    (SELECT id FROM carrier WHERE code = 'SENTARA'),
    (SELECT id FROM client WHERE external_client_id = 'I2638704'),
    'SENTARA-I2638704',
    'I2638704',
    DATE '2026-02-01',
    DATE '2026-02-01',
    NULL,
    (SELECT id FROM parameter WHERE parameter_group = 'POLICY_STATUS' AND name = 'ACTIVE'),
    'FL',
    'FL',
    3,
    'SENTARA-2',
    TRUE,
    'seed'
),
(
    (SELECT id FROM carrier WHERE code = 'SAFEGUARD'),
    (SELECT id FROM client WHERE external_client_id = '12793741001'),
    'SAFEGUARD-12793741001',
    '12793741001',
    DATE '2026-01-01',
    DATE '2026-01-01',
    NULL,
    (SELECT id FROM parameter WHERE parameter_group = 'POLICY_STATUS' AND name = 'ACTIVE'),
    'FL',
    'FL',
    1,
    'SAFEGUARD-1',
    TRUE,
    'seed'
),
(
    (SELECT id FROM carrier WHERE code = 'SAFEGUARD'),
    (SELECT id FROM client WHERE external_client_id = 'U98544537'),
    'SAFEGUARD-U98544537',
    'U98544537',
    DATE '2024-03-01',
    DATE '2024-03-01',
    DATE '2026-11-30',
    (SELECT id FROM parameter WHERE parameter_group = 'POLICY_STATUS' AND name = 'CANCELLED'),
    'FL',
    'FL',
    4,
    'SAFEGUARD-2',
    TRUE,
    'seed'
),
(
    (SELECT id FROM carrier WHERE code = 'UNITED_H_ONE'),
    (SELECT id FROM client WHERE external_client_id = '450788742'),
    '450788742',
    '450788742',
    DATE '2026-01-13',
    DATE '2026-01-12',
    NULL,
    (SELECT id FROM parameter WHERE parameter_group = 'POLICY_STATUS' AND name = 'ACTIVE'),
    'FL',
    'FL',
    1,
    'UNITED-1',
    TRUE,
    'seed'
),
(
    (SELECT id FROM carrier WHERE code = 'UNITED_H_ONE'),
    (SELECT id FROM client WHERE external_client_id = '443036458'),
    '443036458',
    '443036458',
    DATE '2024-11-20',
    DATE '2024-11-19',
    NULL,
    (SELECT id FROM parameter WHERE parameter_group = 'POLICY_STATUS' AND name = 'ACTIVE'),
    'FL',
    'FL',
    1,
    'UNITED-2',
    TRUE,
    'seed'
),
(
    (SELECT id FROM carrier WHERE code = 'UNITED_H_ONE'),
    (SELECT id FROM client WHERE external_client_id = '443135199'),
    '443135199',
    '443135199',
    DATE '2024-12-19',
    DATE '2024-12-18',
    NULL,
    (SELECT id FROM parameter WHERE parameter_group = 'POLICY_STATUS' AND name = 'ACTIVE'),
    'MD',
    'MD',
    1,
    'UNITED-3',
    TRUE,
    'seed'
);

-- =========================================================
-- 8. PLANES DE POLIZA
-- =========================================================
INSERT INTO policy_plan (
    policy_id,
    plan_name,
    base_medical_plan,
    coverage_plan_name,
    payment_type,
    policy_mode,
    created_by
)
VALUES
(
    (SELECT id FROM policy WHERE policy_number = '450788742'),
    'DentalWise Basic',
    'DentalWise Basic',
    'DentalWise Base',
    'F',
    'Monthly',
    'seed'
),
(
    (SELECT id FROM policy WHERE policy_number = '443036458'),
    'VisionWise Premier',
    'VisionWise Premier',
    'VisionWise Base',
    'R',
    'Monthly',
    'seed'
),
(
    (SELECT id FROM policy WHERE policy_number = '443135199'),
    'VisionWise Premier',
    'VisionWise Premier',
    'VisionWise Base',
    'R',
    'Monthly',
    'seed'
);

-- =========================================================
-- 9. HISTORIAL DE ESTADOS
-- =========================================================
INSERT INTO policy_status_history (
    policy_id,
    source_file_id,
    status_id,
    effective_from,
    effective_to,
    notes,
    created_by
)
VALUES
(
    (SELECT id FROM policy WHERE policy_number = 'ELITE-PA-0001'),
    (SELECT id FROM source_file WHERE original_file_name = 'elite_commissions_dec_jan.xlsx'),
    (SELECT id FROM parameter WHERE parameter_group = 'POLICY_STATUS' AND name = 'APPROVED'),
    DATE '2025-12-01',
    NULL,
    'Estado observado en hoja ELITE',
    'seed'
),
(
    (SELECT id FROM policy WHERE policy_number = 'ELITE-PA-0003'),
    (SELECT id FROM source_file WHERE original_file_name = 'elite_commissions_dec_jan.xlsx'),
    (SELECT id FROM parameter WHERE parameter_group = 'POLICY_STATUS' AND name = 'NOT_ASSIGNED'),
    DATE '2026-01-01',
    NULL,
    'Póliza no asignada según hoja ELITE',
    'seed'
),
(
    (SELECT id FROM policy WHERE policy_number = 'SAFEGUARD-U98544537'),
    (SELECT id FROM source_file WHERE original_file_name = 'safeguard_paid_20260331.xlsx'),
    (SELECT id FROM parameter WHERE parameter_group = 'POLICY_STATUS' AND name = 'ACTIVE'),
    DATE '2024-03-01',
    DATE '2026-11-30',
    'Estado inicial de póliza',
    'seed'
),
(
    (SELECT id FROM policy WHERE policy_number = 'SAFEGUARD-U98544537'),
    (SELECT id FROM source_file WHERE original_file_name = 'safeguard_paid_20260331.xlsx'),
    (SELECT id FROM parameter WHERE parameter_group = 'POLICY_STATUS' AND name = 'CANCELLED'),
    DATE '2026-12-01',
    NULL,
    'Póliza cancelada para escenario de reconciliación',
    'seed'
);

-- =========================================================
-- 10. ASIGNACIONES DE COMISION
-- =========================================================
INSERT INTO commission_assignment (
    policy_id,
    producer_id,
    role_id,
    split_percentage,
    valid_from,
    valid_to,
    active,
    created_by
)
VALUES
(
    (SELECT id FROM policy WHERE policy_number = 'SENTARA-I2571344'),
    (SELECT id FROM producer WHERE external_producer_id = '20253546'),
    (SELECT id FROM parameter WHERE parameter_group = 'PRODUCER_ROLE_TYPE' AND name = 'PRIMARY_AGENT'),
    100,
    DATE '2026-02-01',
    NULL,
    TRUE,
    'seed'
),
(
    (SELECT id FROM policy WHERE policy_number = 'SENTARA-I2638704'),
    (SELECT id FROM producer WHERE external_producer_id = '20253546'),
    (SELECT id FROM parameter WHERE parameter_group = 'PRODUCER_ROLE_TYPE' AND name = 'PRIMARY_AGENT'),
    100,
    DATE '2026-02-01',
    NULL,
    TRUE,
    'seed'
),
(
    (SELECT id FROM policy WHERE policy_number = '450788742'),
    (SELECT id FROM producer WHERE external_producer_id = '21156867'),
    (SELECT id FROM parameter WHERE parameter_group = 'PRODUCER_ROLE_TYPE' AND name = 'MANAGER'),
    100,
    DATE '2026-01-13',
    NULL,
    TRUE,
    'seed'
),
(
    (SELECT id FROM policy WHERE policy_number = '443036458'),
    (SELECT id FROM producer WHERE external_producer_id = '20253546'),
    (SELECT id FROM parameter WHERE parameter_group = 'PRODUCER_ROLE_TYPE' AND name = 'SUB_AGENT'),
    100,
    DATE '2024-11-20',
    NULL,
    TRUE,
    'seed'
),
(
    (SELECT id FROM policy WHERE policy_number = '443135199'),
    (SELECT id FROM producer WHERE external_producer_id = '20253546'),
    (SELECT id FROM parameter WHERE parameter_group = 'PRODUCER_ROLE_TYPE' AND name = 'MANAGER'),
    100,
    DATE '2024-12-19',
    NULL,
    TRUE,
    'seed'
);

-- =========================================================
-- 11. REGLAS DE COMISION
-- =========================================================
INSERT INTO commission_rule (
    carrier_id,
    role_id,
    product_type,
    state,
    rule_name,
    rule_type_id,
    rate,
    fixed_amount,
    priority_order,
    applies_from,
    applies_to,
    active,
    created_by
)
VALUES
(
    (SELECT id FROM carrier WHERE code = 'ELITE'),
    (SELECT id FROM parameter WHERE parameter_group = 'PRODUCER_ROLE_TYPE' AND name = 'PRIMARY_AGENT'),
    'GENERAL',
    'PA',
    'ELITE_PA_STANDARD_RATE',
    (SELECT id FROM parameter WHERE parameter_group = 'COMMISSION_RULE_TYPE' AND name = 'FIXED_AMOUNT'),
    NULL,
    30.00,
    1,
    DATE '2025-01-01',
    NULL,
    TRUE,
    'seed'
),
(
    (SELECT id FROM carrier WHERE code = 'SENTARA'),
    (SELECT id FROM parameter WHERE parameter_group = 'PRODUCER_ROLE_TYPE' AND name = 'PRIMARY_AGENT'),
    'GENERAL',
    'FL',
    'SENTARA_PRIMARY_AGENT_20_PCT',
    (SELECT id FROM parameter WHERE parameter_group = 'COMMISSION_RULE_TYPE' AND name = 'PERCENTAGE'),
    20.00,
    NULL,
    1,
    DATE '2025-01-01',
    NULL,
    TRUE,
    'seed'
),
(
    (SELECT id FROM carrier WHERE code = 'UNITED_H_ONE'),
    (SELECT id FROM parameter WHERE parameter_group = 'PRODUCER_ROLE_TYPE' AND name = 'SUB_AGENT'),
    'VISION',
    'FL',
    'UNITED_SUB_STANDARD_7_25',
    (SELECT id FROM parameter WHERE parameter_group = 'COMMISSION_RULE_TYPE' AND name = 'PERCENTAGE'),
    7.25,
    NULL,
    1,
    DATE '2025-01-01',
    NULL,
    TRUE,
    'seed'
),
(
    (SELECT id FROM carrier WHERE code = 'UNITED_H_ONE'),
    (SELECT id FROM parameter WHERE parameter_group = 'PRODUCER_ROLE_TYPE' AND name = 'MANAGER'),
    'DENTAL',
    'FL',
    'UNITED_MANAGER_OVR_REVIEW',
    (SELECT id FROM parameter WHERE parameter_group = 'COMMISSION_RULE_TYPE' AND name = 'VALIDATION_RULE'),
    1.50,
    NULL,
    2,
    DATE '2025-01-01',
    NULL,
    TRUE,
    'seed'
);

-- =========================================================
-- 12. COMMISSION STATEMENT
-- =========================================================
INSERT INTO commission_statement (
    source_file_id,
    raw_import_record_id,
    carrier_id,
    agency_id,
    producer_id,
    client_id,
    policy_id,
    statement_date,
    check_date,
    paid_date,
    last_pay_date,
    transaction_date,
    invoice_number,
    row_identifier,
    raw_status_id,
    reason_code_id,
    source_sheet_name,
    source_row_number,
    created_by
)
VALUES
(
    (SELECT id FROM source_file WHERE original_file_name = 'elite_commissions_dec_jan.xlsx'),
    (SELECT id FROM raw_import_record WHERE source_row_key = 'ELITE-1'),
    (SELECT id FROM carrier WHERE code = 'ELITE'),
    NULL,
    (SELECT id FROM producer WHERE external_producer_id = '20253546'),
    (SELECT id FROM client WHERE external_client_id = 'EL-0001'),
    (SELECT id FROM policy WHERE policy_number = 'ELITE-PA-0001'),
    DATE '2025-12-01',
    NULL,
    DATE '2025-12-01',
    NULL,
    DATE '2025-12-01',
    NULL,
    'ELITE-ROW-1',
    (SELECT id FROM parameter WHERE parameter_group = 'COMMISSION_RAW_STATUS' AND name = 'ACTIVE'),
    (SELECT id FROM parameter WHERE parameter_group = 'REASON_CODE' AND name = 'NEW_BUSINESS'),
    'ELITE',
    1,
    'seed'
),
(
    (SELECT id FROM source_file WHERE original_file_name = 'elite_commissions_dec_jan.xlsx'),
    (SELECT id FROM raw_import_record WHERE source_row_key = 'ELITE-3'),
    (SELECT id FROM carrier WHERE code = 'ELITE'),
    NULL,
    NULL,
    (SELECT id FROM client WHERE external_client_id = 'EL-0003'),
    (SELECT id FROM policy WHERE policy_number = 'ELITE-PA-0003'),
    DATE '2026-01-01',
    NULL,
    DATE '2026-01-01',
    NULL,
    DATE '2026-01-01',
    NULL,
    'ELITE-ROW-3',
    (SELECT id FROM parameter WHERE parameter_group = 'COMMISSION_RAW_STATUS' AND name = 'PENDING'),
    (SELECT id FROM parameter WHERE parameter_group = 'REASON_CODE' AND name = 'UNKNOWN'),
    'ELITE',
    3,
    'seed'
),
(
    (SELECT id FROM source_file WHERE original_file_name = 'sentara_statement_20260309.xlsx'),
    (SELECT id FROM raw_import_record WHERE source_row_key = '2026-03-09 00:00:00-NOHELIA I NAVARRO FANDINO-I2571344-7846156'),
    (SELECT id FROM carrier WHERE code = 'SENTARA'),
    (SELECT id FROM agency WHERE external_agency_id = '13192'),
    (SELECT id FROM producer WHERE external_producer_id = '20253546'),
    (SELECT id FROM client WHERE external_client_id = 'I2571344'),
    (SELECT id FROM policy WHERE policy_number = 'SENTARA-I2571344'),
    DATE '2026-03-09',
    DATE '2026-03-09',
    NULL,
    DATE '2026-02-01',
    DATE '2026-03-09',
    '7846156',
    'SENTARA-ROW-1',
    (SELECT id FROM parameter WHERE parameter_group = 'COMMISSION_RAW_STATUS' AND name = 'ACTIVE'),
    (SELECT id FROM parameter WHERE parameter_group = 'REASON_CODE' AND name = 'RETRO'),
    'SENTARA .',
    1,
    'seed'
),
(
    (SELECT id FROM source_file WHERE original_file_name = 'sentara_statement_20260309.xlsx'),
    (SELECT id FROM raw_import_record WHERE source_row_key = '2026-03-09 00:00:00-NOHELIA I NAVARRO FANDINO-I2638704-7846157'),
    (SELECT id FROM carrier WHERE code = 'SENTARA'),
    (SELECT id FROM agency WHERE external_agency_id = '13192'),
    (SELECT id FROM producer WHERE external_producer_id = '20253546'),
    (SELECT id FROM client WHERE external_client_id = 'I2638704'),
    (SELECT id FROM policy WHERE policy_number = 'SENTARA-I2638704'),
    DATE '2026-03-09',
    DATE '2026-03-09',
    NULL,
    DATE '2026-02-01',
    DATE '2026-03-09',
    '7846157',
    'SENTARA-ROW-2',
    (SELECT id FROM parameter WHERE parameter_group = 'COMMISSION_RAW_STATUS' AND name = 'ACTIVE'),
    (SELECT id FROM parameter WHERE parameter_group = 'REASON_CODE' AND name = 'NEW_BUSINESS'),
    'SENTARA .',
    2,
    'seed'
),
(
    (SELECT id FROM source_file WHERE original_file_name = 'safeguard_paid_20260331.xlsx'),
    (SELECT id FROM raw_import_record WHERE source_row_key = 'SAFEGUARD-1'),
    (SELECT id FROM carrier WHERE code = 'SAFEGUARD'),
    (SELECT id FROM agency WHERE external_agency_id = 'SG-001'),
    (SELECT id FROM producer WHERE external_producer_id = '20253546'),
    (SELECT id FROM client WHERE external_client_id = '12793741001'),
    (SELECT id FROM policy WHERE policy_number = 'SAFEGUARD-12793741001'),
    DATE '2026-03-31',
    NULL,
    DATE '2026-03-31',
    NULL,
    DATE '2026-03-31',
    NULL,
    'SAFEGUARD-ROW-1',
    (SELECT id FROM parameter WHERE parameter_group = 'COMMISSION_RAW_STATUS' AND name = 'ACTIVE'),
    (SELECT id FROM parameter WHERE parameter_group = 'REASON_CODE' AND name = 'NEW_BUSINESS'),
    'SAFEGUARD',
    1,
    'seed'
),
(
    (SELECT id FROM source_file WHERE original_file_name = 'safeguard_paid_20260331.xlsx'),
    (SELECT id FROM raw_import_record WHERE source_row_key = 'SAFEGUARD-2'),
    (SELECT id FROM carrier WHERE code = 'SAFEGUARD'),
    (SELECT id FROM agency WHERE external_agency_id = 'SG-001'),
    (SELECT id FROM producer WHERE external_producer_id = '20253546'),
    (SELECT id FROM client WHERE external_client_id = 'U98544537'),
    (SELECT id FROM policy WHERE policy_number = 'SAFEGUARD-U98544537'),
    DATE '2026-12-31',
    NULL,
    DATE '2026-12-31',
    NULL,
    DATE '2026-12-31',
    NULL,
    'SAFEGUARD-ROW-2',
    (SELECT id FROM parameter WHERE parameter_group = 'COMMISSION_RAW_STATUS' AND name = 'CANCELLED'),
    (SELECT id FROM parameter WHERE parameter_group = 'REASON_CODE' AND name = 'CANCELLATION'),
    'SAFEGUARD',
    2,
    'seed'
),
(
    (SELECT id FROM source_file WHERE original_file_name = 'united_h_one_extract_20260317.xlsx'),
    (SELECT id FROM raw_import_record WHERE source_row_key = 'UNITED-450788742-MANAGER'),
    (SELECT id FROM carrier WHERE code = 'UNITED_H_ONE'),
    (SELECT id FROM agency WHERE external_agency_id = 'UH1-001'),
    (SELECT id FROM producer WHERE external_producer_id = '21156867'),
    (SELECT id FROM client WHERE external_client_id = '450788742'),
    (SELECT id FROM policy WHERE policy_number = '450788742'),
    DATE '2026-03-17',
    NULL,
    NULL,
    NULL,
    DATE '2026-03-06',
    NULL,
    'UNITED-ROW-1',
    (SELECT id FROM parameter WHERE parameter_group = 'COMMISSION_RAW_STATUS' AND name = 'ACTIVE'),
    (SELECT id FROM parameter WHERE parameter_group = 'REASON_CODE' AND name = 'UNKNOWN'),
    'UNITED H ONE ',
    1,
    'seed'
),
(
    (SELECT id FROM source_file WHERE original_file_name = 'united_h_one_extract_20260317.xlsx'),
    (SELECT id FROM raw_import_record WHERE source_row_key = 'UNITED-443036458-SUB'),
    (SELECT id FROM carrier WHERE code = 'UNITED_H_ONE'),
    (SELECT id FROM agency WHERE external_agency_id = 'UH1-001'),
    (SELECT id FROM producer WHERE external_producer_id = '20253546'),
    (SELECT id FROM client WHERE external_client_id = '443036458'),
    (SELECT id FROM policy WHERE policy_number = '443036458'),
    DATE '2026-03-17',
    NULL,
    NULL,
    NULL,
    DATE '2026-03-11',
    NULL,
    'UNITED-ROW-2',
    (SELECT id FROM parameter WHERE parameter_group = 'COMMISSION_RAW_STATUS' AND name = 'ACTIVE'),
    (SELECT id FROM parameter WHERE parameter_group = 'REASON_CODE' AND name = 'UNKNOWN'),
    'UNITED H ONE ',
    2,
    'seed'
),
(
    (SELECT id FROM source_file WHERE original_file_name = 'united_h_one_extract_20260317.xlsx'),
    (SELECT id FROM raw_import_record WHERE source_row_key = 'UNITED-443135199-MANAGER'),
    (SELECT id FROM carrier WHERE code = 'UNITED_H_ONE'),
    (SELECT id FROM agency WHERE external_agency_id = 'UH1-001'),
    (SELECT id FROM producer WHERE external_producer_id = '20253546'),
    (SELECT id FROM client WHERE external_client_id = '443135199'),
    (SELECT id FROM policy WHERE policy_number = '443135199'),
    DATE '2026-03-17',
    NULL,
    NULL,
    NULL,
    DATE '2026-03-11',
    NULL,
    'UNITED-ROW-3',
    (SELECT id FROM parameter WHERE parameter_group = 'COMMISSION_RAW_STATUS' AND name = 'ACTIVE'),
    (SELECT id FROM parameter WHERE parameter_group = 'REASON_CODE' AND name = 'UNKNOWN'),
    'UNITED H ONE ',
    3,
    'seed'
);

-- =========================================================
-- 13. COMMISSION STATEMENT ITEM
-- =========================================================
INSERT INTO commission_statement_item (
    commission_statement_id,
    gross_premium,
    commissionable_premium,
    premium,
    net_amount,
    rate,
    commission_rate_pct,
    commission_amount,
    subtotal,
    month_amount,
    applicants,
    contract_count,
    retro_count,
    vip_count,
    is_initial_premium,
    created_by
)
VALUES
(
    (SELECT id FROM commission_statement WHERE row_identifier = 'ELITE-ROW-1'),
    NULL,
    NULL,
    NULL,
    NULL,
    30.00,
    NULL,
    30.00,
    30.00,
    30.00,
    1,
    NULL,
    NULL,
    NULL,
    TRUE,
    'seed'
),
(
    (SELECT id FROM commission_statement WHERE row_identifier = 'ELITE-ROW-3'),
    NULL,
    NULL,
    NULL,
    NULL,
    30.00,
    NULL,
    0.00,
    0.00,
    0.00,
    2,
    NULL,
    NULL,
    NULL,
    TRUE,
    'seed'
),
(
    (SELECT id FROM commission_statement WHERE row_identifier = 'SENTARA-ROW-1'),
    0.00,
    0.00,
    0.00,
    40.00,
    20.00,
    20.00,
    40.00,
    40.00,
    40.00,
    NULL,
    1,
    1,
    NULL,
    FALSE,
    'seed'
),
(
    (SELECT id FROM commission_statement WHERE row_identifier = 'SENTARA-ROW-2'),
    0.00,
    0.00,
    0.00,
    60.00,
    20.00,
    20.00,
    60.00,
    60.00,
    60.00,
    NULL,
    3,
    0,
    NULL,
    FALSE,
    'seed'
),
(
    (SELECT id FROM commission_statement WHERE row_identifier = 'SAFEGUARD-ROW-1'),
    NULL,
    NULL,
    NULL,
    18.00,
    NULL,
    NULL,
    18.00,
    18.00,
    18.00,
    NULL,
    1,
    0,
    NULL,
    FALSE,
    'seed'
),
(
    (SELECT id FROM commission_statement WHERE row_identifier = 'SAFEGUARD-ROW-2'),
    NULL,
    NULL,
    NULL,
    120.00,
    NULL,
    NULL,
    120.00,
    120.00,
    120.00,
    NULL,
    4,
    0,
    NULL,
    FALSE,
    'seed'
),
(
    (SELECT id FROM commission_statement WHERE row_identifier = 'UNITED-ROW-1'),
    34.50,
    34.50,
    34.50,
    0.00,
    NULL,
    1.50,
    0.00,
    0.00,
    0.00,
    NULL,
    NULL,
    0,
    158,
    FALSE,
    'seed'
),
(
    (SELECT id FROM commission_statement WHERE row_identifier = 'UNITED-ROW-2'),
    13.28,
    13.28,
    13.28,
    0.96,
    NULL,
    7.25,
    0.96,
    0.96,
    0.96,
    NULL,
    NULL,
    0,
    158,
    FALSE,
    'seed'
),
(
    (SELECT id FROM commission_statement WHERE row_identifier = 'UNITED-ROW-3'),
    13.28,
    13.28,
    13.28,
    0.00,
    NULL,
    0.00,
    0.00,
    0.00,
    0.00,
    NULL,
    NULL,
    0,
    158,
    FALSE,
    'seed'
);

-- =========================================================
-- 14. CASOS DE RECONCILIACION
-- =========================================================
INSERT INTO reconciliation_case (
    source_file_id,
    commission_statement_id,
    commission_statement_item_id,
    carrier_id,
    policy_id,
    producer_id,
    case_type_id,
    severity_id,
    status_id,
    detected_at,
    description,
    suggested_action,
    resolution_notes,
    resolved_at,
    resolved_by,
    created_by
)
VALUES
(
    (SELECT id FROM source_file WHERE original_file_name = 'elite_commissions_dec_jan.xlsx'),
    (SELECT id FROM commission_statement WHERE row_identifier = 'ELITE-ROW-3'),
    (SELECT csi.id
       FROM commission_statement_item csi
       JOIN commission_statement cs ON cs.id = csi.commission_statement_id
      WHERE cs.row_identifier = 'ELITE-ROW-3'),
    (SELECT id FROM carrier WHERE code = 'ELITE'),
    (SELECT id FROM policy WHERE policy_number = 'ELITE-PA-0003'),
    NULL,
    (SELECT id FROM parameter WHERE parameter_group = 'RECONCILIATION_CASE_TYPE' AND name = 'PAYMENT_WITHOUT_ASSIGNMENT'),
    (SELECT id FROM parameter WHERE parameter_group = 'SEVERITY_LEVEL' AND name = 'HIGH'),
    (SELECT id FROM parameter WHERE parameter_group = 'CASE_STATUS' AND name = 'OPEN'),
    CURRENT_TIMESTAMP,
    'La fila de ELITE indica estado Not Assigned. No debe pagarse comisión.',
    'Excluir de liquidación hasta que exista asignación válida.',
    NULL,
    NULL,
    NULL,
    'seed'
),
(
    (SELECT id FROM source_file WHERE original_file_name = 'safeguard_paid_20260331.xlsx'),
    (SELECT id FROM commission_statement WHERE row_identifier = 'SAFEGUARD-ROW-2'),
    (SELECT csi.id
       FROM commission_statement_item csi
       JOIN commission_statement cs ON cs.id = csi.commission_statement_id
      WHERE cs.row_identifier = 'SAFEGUARD-ROW-2'),
    (SELECT id FROM carrier WHERE code = 'SAFEGUARD'),
    (SELECT id FROM policy WHERE policy_number = 'SAFEGUARD-U98544537'),
    (SELECT id FROM producer WHERE external_producer_id = '20253546'),
    (SELECT id FROM parameter WHERE parameter_group = 'RECONCILIATION_CASE_TYPE' AND name = 'PAYMENT_ON_CANCELLED_POLICY'),
    (SELECT id FROM parameter WHERE parameter_group = 'SEVERITY_LEVEL' AND name = 'CRITICAL'),
    (SELECT id FROM parameter WHERE parameter_group = 'CASE_STATUS' AND name = 'OPEN'),
    CURRENT_TIMESTAMP,
    'La póliza está cancelada y existe un registro monetario de 120.00 en SAFEGUARD.',
    'Bloquear pago y enviar a revisión operativa.',
    NULL,
    NULL,
    NULL,
    'seed'
),
(
    (SELECT id FROM source_file WHERE original_file_name = 'united_h_one_extract_20260317.xlsx'),
    (SELECT id FROM commission_statement WHERE row_identifier = 'UNITED-ROW-3'),
    (SELECT csi.id
       FROM commission_statement_item csi
       JOIN commission_statement cs ON cs.id = csi.commission_statement_id
      WHERE cs.row_identifier = 'UNITED-ROW-3'),
    (SELECT id FROM carrier WHERE code = 'UNITED_H_ONE'),
    (SELECT id FROM policy WHERE policy_number = '443135199'),
    (SELECT id FROM producer WHERE external_producer_id = '20253546'),
    (SELECT id FROM parameter WHERE parameter_group = 'RECONCILIATION_CASE_TYPE' AND name = 'AMOUNT_MISMATCH'),
    (SELECT id FROM parameter WHERE parameter_group = 'SEVERITY_LEVEL' AND name = 'MEDIUM'),
    (SELECT id FROM parameter WHERE parameter_group = 'CASE_STATUS' AND name = 'IN_REVIEW'),
    CURRENT_TIMESTAMP,
    'La transacción tipo OVR tiene 0.00 de comisión calculada; requiere validación frente a la regla.',
    'Revisar si el concepto OVR debe excluirse de liquidación.',
    NULL,
    NULL,
    NULL,
    'seed'
);

-- =========================================================
-- 15. LIQUIDACION / PAGOS
-- =========================================================
INSERT INTO commission_payment (
    producer_id,
    period_year,
    period_month,
    total_gross,
    total_adjustments,
    total_payable,
    status_id,
    generated_at,
    approved_at,
    approved_by,
    paid_at,
    payment_reference,
    notes,
    created_by
)
VALUES
(
    (SELECT id FROM producer WHERE external_producer_id = '20253546'),
    2026,
    3,
    118.96,
    -120.00,
    -1.04,
    (SELECT id FROM parameter WHERE parameter_group = 'PAYMENT_STATUS' AND name = 'GENERATED'),
    CURRENT_TIMESTAMP,
    NULL,
    NULL,
    NULL,
    'PAY-2026-03-NOHELIA',
    'Liquidación de prueba que demuestra exclusión por póliza cancelada y caso no asignado.',
    'seed'
);

INSERT INTO commission_payment_detail (
    commission_payment_id,
    policy_id,
    commission_statement_item_id,
    reconciliation_case_id,
    concept,
    amount,
    included_for_payment,
    exclusion_reason,
    created_by
)
VALUES
(
    (SELECT id FROM commission_payment WHERE payment_reference = 'PAY-2026-03-NOHELIA'),
    (SELECT id FROM policy WHERE policy_number = 'SENTARA-I2571344'),
    (SELECT csi.id
       FROM commission_statement_item csi
       JOIN commission_statement cs ON cs.id = csi.commission_statement_id
      WHERE cs.row_identifier = 'SENTARA-ROW-1'),
    NULL,
    'Comisión válida SENTARA invoice 7846156',
    40.00,
    TRUE,
    NULL,
    'seed'
),
(
    (SELECT id FROM commission_payment WHERE payment_reference = 'PAY-2026-03-NOHELIA'),
    (SELECT id FROM policy WHERE policy_number = 'SENTARA-I2638704'),
    (SELECT csi.id
       FROM commission_statement_item csi
       JOIN commission_statement cs ON cs.id = csi.commission_statement_id
      WHERE cs.row_identifier = 'SENTARA-ROW-2'),
    NULL,
    'Comisión válida SENTARA invoice 7846157',
    60.00,
    TRUE,
    NULL,
    'seed'
),
(
    (SELECT id FROM commission_payment WHERE payment_reference = 'PAY-2026-03-NOHELIA'),
    (SELECT id FROM policy WHERE policy_number = '443036458'),
    (SELECT csi.id
       FROM commission_statement_item csi
       JOIN commission_statement cs ON cs.id = csi.commission_statement_id
      WHERE cs.row_identifier = 'UNITED-ROW-2'),
    NULL,
    'Comisión válida UNITED SUB',
    0.96,
    TRUE,
    NULL,
    'seed'
),
(
    (SELECT id FROM commission_payment WHERE payment_reference = 'PAY-2026-03-NOHELIA'),
    (SELECT id FROM policy WHERE policy_number = 'SAFEGUARD-U98544537'),
    (SELECT csi.id
       FROM commission_statement_item csi
       JOIN commission_statement cs ON cs.id = csi.commission_statement_id
      WHERE cs.row_identifier = 'SAFEGUARD-ROW-2'),
    (SELECT id
       FROM reconciliation_case
      WHERE description LIKE 'La póliza está cancelada%'
      LIMIT 1),
    'Exclusión por póliza cancelada',
    -120.00,
    FALSE,
    'Pago excluido por cancelación de póliza',
    'seed'
),
(
    (SELECT id FROM commission_payment WHERE payment_reference = 'PAY-2026-03-NOHELIA'),
    (SELECT id FROM policy WHERE policy_number = 'ELITE-PA-0003'),
    (SELECT csi.id
       FROM commission_statement_item csi
       JOIN commission_statement cs ON cs.id = csi.commission_statement_id
      WHERE cs.row_identifier = 'ELITE-ROW-3'),
    (SELECT id
       FROM reconciliation_case
      WHERE description LIKE 'La fila de ELITE indica estado Not Assigned%'
      LIMIT 1),
    'Exclusión por no asignación',
    0.00,
    FALSE,
    'No existe asignación válida para pago',
    'seed'
);

-- =========================================================
-- 16. AUDITORIA
-- =========================================================
INSERT INTO security_audit_log (
    usuario,
    accion,
    fecha,
    resultado,
    detalle,
    estado,
    valor_antes,
    valor_despues
)
VALUES
(
    'seed_user',
    'UPLOAD_FILE',
    CURRENT_TIMESTAMP,
    'SUCCESS',
    'Carga de archivo de prueba para proceso de conciliación',
    'COMPLETED',
    NULL,
    '{"status":"PROCESSED","file":"sentara_statement_20260309.xlsx"}'::jsonb
),
(
    'recon_engine',
    'PROCESS_RECONCILIATION_CASE',
    CURRENT_TIMESTAMP,
    'SUCCESS',
    'Caso detectado automáticamente por motor de conciliación',
    'OPEN',
    NULL,
    '{"caseType":"PAYMENT_ON_CANCELLED_POLICY","severity":"CRITICAL"}'::jsonb
),
(
    'settlement_engine',
    'GENERATE_PAYMENT',
    CURRENT_TIMESTAMP,
    'SUCCESS',
    'Generación de liquidación mensual de prueba',
    'GENERATED',
    NULL,
    '{"paymentReference":"PAY-2026-03-NOHELIA","status":"GENERATED"}'::jsonb
),
(
    'admin_user',
    'UPDATE_POLICY_STATUS',
    CURRENT_TIMESTAMP,
    'SUCCESS',
    'Actualización manual de estado de póliza',
    'UPDATED',
    '{"status":"ACTIVE"}'::jsonb,
    '{"status":"CANCELLED"}'::jsonb
);
