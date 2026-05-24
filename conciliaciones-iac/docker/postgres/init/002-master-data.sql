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
-- EXECUTION PIPELINE ENGINE - PARAMETERS
-- =========================================================

SET search_path TO reconciliation;

-- =========================================================
-- EXECUTION_PLAN_TASK_STATUS
-- =========================================================

INSERT INTO parameter (
    id,
    name,
    description,
    value,
    parameter_group,
    active,
    sort_order,
    created_by
)
VALUES
(76, 'PENDING',   'Plan pendiente por iniciar',     'Pendiente', 'EXECUTION_PLAN_TASK_STATUS', TRUE, 1, 'seed'),
(77, 'PROCESS',   'Plan en procesamiento',          'Proceso',   'EXECUTION_PLAN_TASK_STATUS', TRUE, 2, 'seed'),
(78, 'EXECUTED',  'Plan ejecutado correctamente',  'Ejecutado', 'EXECUTION_PLAN_TASK_STATUS', TRUE, 3, 'seed'),
(79, 'FAILED',    'Plan finalizado con error',     'Fallido',   'EXECUTION_PLAN_TASK_STATUS', TRUE, 4, 'seed'),
(80, 'CANCELLED', 'Plan cancelado',                'Cancelado', 'EXECUTION_PLAN_TASK_STATUS', TRUE, 5, 'seed');

-- =========================================================
-- SCHEDULED_TASK_STATUS
-- =========================================================

INSERT INTO parameter (
    id,
    name,
    description,
    value,
    parameter_group,
    active,
    sort_order,
    created_by
)
VALUES
(81, 'PENDING',   'Tarea pendiente por iniciar',     'Pendiente', 'SCHEDULED_TASK_STATUS', TRUE, 1, 'seed'),
(82, 'PROCESS',   'Tarea en procesamiento',          'Proceso',   'SCHEDULED_TASK_STATUS', TRUE, 2, 'seed'),
(83, 'EXECUTED',  'Tarea ejecutada correctamente',  'Ejecutado', 'SCHEDULED_TASK_STATUS', TRUE, 3, 'seed'),
(84, 'FAILED',    'Tarea finalizada con error',     'Fallido',   'SCHEDULED_TASK_STATUS', TRUE, 4, 'seed'),
(85, 'CANCELLED', 'Tarea cancelada',                'Cancelado', 'SCHEDULED_TASK_STATUS', TRUE, 5, 'seed');

-- =========================================================
-- SCHEDULED_TASK_TYPE
-- =========================================================
INSERT INTO parameter (
    id,
    name,
    description,
    value,
    parameter_group,
    active,
    sort_order,
    created_by
)
VALUES
(86, 'START_UPLOAD_DATA',   'Inicia registro maestro/detalle del pipeline',        'start-upload-data-task',   'SCHEDULED_TASK_TYPE', TRUE, 1, 'seed'),
(87, 'START_VALIDATE_DATA', 'Inicia validación de estructura y datos',             'start-validate-data-task', 'SCHEDULED_TASK_TYPE', TRUE, 2, 'seed'),
(88, 'START_PROCESS_DATA',  'Inicia procesamiento y normalización de información', 'start-process-data-task',  'SCHEDULED_TASK_TYPE', TRUE, 3, 'seed'),
(89, 'START_POLICY_RULES',  'Inicia evaluación de reglas de negocio',              'start-policy-rules-task',  'SCHEDULED_TASK_TYPE', TRUE, 4, 'seed'),
(90, 'START_PAYMENT_PROCESS','Inicia generación de pagos y detalle',               'start-payment-process-task','SCHEDULED_TASK_TYPE', TRUE, 5, 'seed');