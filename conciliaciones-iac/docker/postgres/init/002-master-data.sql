-- =========================================================
-- PROYECTO: RECONCILIACION / LIQUIDACION DE COMISIONES
-- ARCHIVO: 002-seed-parametros-seguridad-rectificado.sql
-- OBJETIVO: parámetros faltantes del motor de tareas/Kafka, reportes, menús y rol ADMIN.
-- NOTA: script idempotente; puede ejecutarse más de una vez.
-- =========================================================

-- =========================================================
-- EXECUTION_PLAN_TASK_STATUS
-- =========================================================

INSERT INTO conciliaciones.parameter (
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
(80, 'CANCELLED', 'Plan cancelado',                'Cancelado', 'EXECUTION_PLAN_TASK_STATUS', TRUE, 5, 'seed')
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
-- SCHEDULED_TASK_STATUS
-- =========================================================

INSERT INTO conciliaciones.parameter (
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
(85, 'CANCELLED', 'Tarea cancelada',                'Cancelado', 'SCHEDULED_TASK_STATUS', TRUE, 5, 'seed')
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
-- SCHEDULED_TASK_TYPE
-- =========================================================

INSERT INTO conciliaciones.parameter (
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
(90, 'START_PAYMENT_PROCESS','Inicia generación de pagos y detalle',               'start-payment-process-task','SCHEDULED_TASK_TYPE', TRUE, 5, 'seed')
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
-- 1. CREACIÓN DE LOS PARAMETROS PARA LA VALIDACIÓN DE TAREAS
-- =========================================================

INSERT INTO conciliaciones.parameter (id, name, description, value, parameter_group, active, sort_order, created_by)
VALUES
(107, 'PENDING',  'Plan de validación pendiente por iniciar',    'Pendiente', 'VALIDATION_SOURCE_PLAN_STATUS', TRUE, 1, 'system'),
(108, 'PROCESS',  'Plan de validación en procesamiento',         'Proceso',   'VALIDATION_SOURCE_PLAN_STATUS', TRUE, 2, 'system'),
(109, 'EXECUTED', 'Plan de validación ejecutado correctamente',  'Ejecutado', 'VALIDATION_SOURCE_PLAN_STATUS', TRUE, 3, 'system'),
(110, 'FAILED',   'Plan de validación finalizado con error',     'Fallido',   'VALIDATION_SOURCE_PLAN_STATUS', TRUE, 4, 'system')
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
-- REPORTING SERVICE
-- =========================================================
INSERT INTO conciliaciones.parameter (
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
(126, 'COMISIONES', 'REPORTE DE COMISIONES', 'PaymentForEachProducer', 'REPORTING_SERVICES', TRUE, 1, 'seed')
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
-- MENUS / SUBMENUS - SEGURIDAD + APP
-- =========================================================

-- =========================================================
-- MENUS
-- =========================================================
INSERT INTO conciliaciones.parameter (id, name, description, value, parameter_group, active, sort_order, created_by)
VALUES
(127, 'SEGURIDAD', 'Menú de Seguridad', 'Seguridad', 'MENU_HEAD', TRUE, 1, 'seed'),
(128, 'CATALOGO_MAESTRO', 'Menú de Catálogos & Maestros', 'Catálogos / Maestros', 'MENU_HEAD', TRUE, 2, 'seed'),
(129, 'CONCILIACION', 'Menú de Conciliación', 'Conciliación', 'MENU_HEAD', TRUE, 3, 'seed'),
(130, 'UPLOAD_FILES', 'Menú de Upload Files', 'Upload Files', 'MENU_HEAD', TRUE, 4, 'seed'),
(131, 'PAGOS_LIQUIDACION', 'Menú de Pagos & Liquidaciones', 'Pagos / Liquidaciones', 'MENU_HEAD', TRUE, 5, 'seed'),
(132, 'REPORTES', 'Menú de Reportes', 'Reportes', 'MENU_HEAD', TRUE, 6, 'seed')
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
-- SUB MENUS
-- =========================================================
INSERT INTO conciliaciones.parameter (id, name, description, value, parameter_group, active, sort_order, created_by)
VALUES
(133, 'ROL', 'Sub menú de Roles', 'Roles', 'SEGURIDAD', TRUE, 1, 'seed'),
(134, 'CONFIG_USUARIOS', 'Sub menú de Configuración de Usuarios', 'Configuración de usuarios', 'SEGURIDAD', TRUE, 2, 'seed'),
(135, 'CONFIG_FUNCIONES_ROLES', 'Sub menú de Configuración Funciones a Roles', 'Configuración Funciones a Roles', 'SEGURIDAD', TRUE, 3, 'seed'),
(136, 'ASOCIAR_ROL_USUARIO', 'Sub menú de Asociar Rol a Usuario', 'Asociar Rol a Usuario', 'SEGURIDAD', TRUE, 4, 'seed'),

(137, 'CLIENTES', 'Sub menú de Clientes', 'Clientes', 'CATALOGO_MAESTRO', TRUE, 1, 'seed'),
(138, 'PRODUCTORES', 'Sub menú de Productores', 'Productores', 'CATALOGO_MAESTRO', TRUE, 2, 'seed'),
(139, 'AGENCIAS', 'Sub menú de Agencias', 'Agencias', 'CATALOGO_MAESTRO', TRUE, 3, 'seed'),
(140, 'CARRIERS', 'Sub menú de Carriers', 'Carriers', 'CATALOGO_MAESTRO', TRUE, 4, 'seed'),
(141, 'POLIZAS', 'Sub menú de Pólizas', 'Pólizas', 'CATALOGO_MAESTRO', TRUE, 5, 'seed'),

(142, 'ARCHIVO_FUENTE', 'Sub menú de Archivos Fuentes', 'Archivos fuente', 'CONCILIACION', TRUE, 1, 'seed'),
(143, 'EJECUCIONES', 'Sub menú de Ejecuciones', 'Ejecuciones', 'CONCILIACION', TRUE, 2, 'seed'),
(144, 'CASOS_CONCILIACION', 'Sub menú de Casos Conciliación', 'Casos de conciliación', 'CONCILIACION', TRUE, 3, 'seed'),
(145, 'CONCILIACION_MANUAL', 'Sub menú de Conciliación Manual', 'Conciliación manual', 'CONCILIACION', TRUE, 4, 'seed'),

(146, 'UPLOAD_SOURCE_FILE', 'Sub menú de Upload Source Files', 'Upload sources file', 'UPLOAD_FILES', TRUE, 1, 'seed'),

(147, 'LIQUIDACIONES', 'Sub menú de Liquidaciones', 'Liquidaciones', 'PAGOS_LIQUIDACION', TRUE, 1, 'seed'),

(148, 'REPORTES_GENERALES', 'Sub menú de Reportes', 'Reportes', 'REPORTES', TRUE, 1, 'seed')
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    value = EXCLUDED.value,
    parameter_group = EXCLUDED.parameter_group,
    active = EXCLUDED.active,
    sort_order = EXCLUDED.sort_order,
    updated_at = CURRENT_TIMESTAMP,
    updated_by = EXCLUDED.created_by;

INSERT INTO conciliaciones.parameter (id,name,description,value,parameter_group,active,sort_order,created_by)
VALUES (149,'PAGOS_MENSUALES_ASEGURADORA','REPORTE DE PAGOS MENSUALES POR ASEGURADORA','PaymentMonthlyForCarrier','REPORTING_SERVICES',TRUE,2,'seed')
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
-- SECURITY MENUS
-- =========================================================
INSERT INTO conciliaciones.security_menu (parameter_id, code, label, icon, sort_order, active, created_by)
VALUES
(127, 'SEGURIDAD', 'Seguridad', 'security', 1, TRUE, 'seed'),
(128, 'CATALOGO_MAESTRO', 'Catálogos / Maestros', 'folder_open', 2, TRUE, 'seed'),
(129, 'CONCILIACION', 'Conciliación', 'account_balance', 3, TRUE, 'seed'),
(130, 'UPLOAD_FILES', 'Upload Files', 'upload_file', 4, TRUE, 'seed'),
(131, 'PAGOS_LIQUIDACION', 'Pagos / Liquidaciones', 'payments', 5, TRUE, 'seed'),
(132, 'REPORTES', 'Reportes', 'assessment', 6, TRUE, 'seed')
ON CONFLICT (code) DO UPDATE SET
    parameter_id = EXCLUDED.parameter_id,
    label = EXCLUDED.label,
    icon = EXCLUDED.icon,
    sort_order = EXCLUDED.sort_order,
    active = EXCLUDED.active,
    updated_at = CURRENT_TIMESTAMP,
    updated_by = EXCLUDED.created_by;

-- =========================================================
-- SECURITY SUB MENUS - SEGURIDAD
-- =========================================================
INSERT INTO conciliaciones.security_sub_menu (menu_id, parameter_id, code, label, route, icon, sort_order, active, created_by)
SELECT m.id, 133, 'ROL', 'Roles', '/seguridad/roles', 'admin_panel_settings', 1, TRUE, 'seed'
FROM conciliaciones.security_menu m WHERE m.code = 'SEGURIDAD'
ON CONFLICT (code) DO UPDATE SET
    menu_id = EXCLUDED.menu_id,
    parameter_id = EXCLUDED.parameter_id,
    label = EXCLUDED.label,
    route = EXCLUDED.route,
    icon = EXCLUDED.icon,
    sort_order = EXCLUDED.sort_order,
    active = EXCLUDED.active,
    updated_at = CURRENT_TIMESTAMP,
    updated_by = EXCLUDED.created_by;

INSERT INTO conciliaciones.security_sub_menu (menu_id, parameter_id, code, label, route, icon, sort_order, active, created_by)
SELECT m.id, 134, 'CONFIG_USUARIOS', 'Configuración de usuarios', '/seguridad/usuarios', 'manage_accounts', 2, TRUE, 'seed'
FROM conciliaciones.security_menu m WHERE m.code = 'SEGURIDAD'
ON CONFLICT (code) DO UPDATE SET
    menu_id = EXCLUDED.menu_id,
    parameter_id = EXCLUDED.parameter_id,
    label = EXCLUDED.label,
    route = EXCLUDED.route,
    icon = EXCLUDED.icon,
    sort_order = EXCLUDED.sort_order,
    active = EXCLUDED.active,
    updated_at = CURRENT_TIMESTAMP,
    updated_by = EXCLUDED.created_by;

INSERT INTO conciliaciones.security_sub_menu (menu_id, parameter_id, code, label, route, icon, sort_order, active, created_by)
SELECT m.id, 135, 'CONFIG_FUNCIONES_ROLES', 'Configuración Funciones a Roles', '/seguridad/funciones-roles', 'rule_settings', 3, TRUE, 'seed'
FROM conciliaciones.security_menu m WHERE m.code = 'SEGURIDAD'
ON CONFLICT (code) DO UPDATE SET
    menu_id = EXCLUDED.menu_id,
    parameter_id = EXCLUDED.parameter_id,
    label = EXCLUDED.label,
    route = EXCLUDED.route,
    icon = EXCLUDED.icon,
    sort_order = EXCLUDED.sort_order,
    active = EXCLUDED.active,
    updated_at = CURRENT_TIMESTAMP,
    updated_by = EXCLUDED.created_by;

INSERT INTO conciliaciones.security_sub_menu (menu_id, parameter_id, code, label, route, icon, sort_order, active, created_by)
SELECT m.id, 136, 'ASOCIAR_ROL_USUARIO', 'Asociar Rol a Usuario', '/seguridad/usuarios-roles', 'person_add', 4, TRUE, 'seed'
FROM conciliaciones.security_menu m WHERE m.code = 'SEGURIDAD'
ON CONFLICT (code) DO UPDATE SET
    menu_id = EXCLUDED.menu_id,
    parameter_id = EXCLUDED.parameter_id,
    label = EXCLUDED.label,
    route = EXCLUDED.route,
    icon = EXCLUDED.icon,
    sort_order = EXCLUDED.sort_order,
    active = EXCLUDED.active,
    updated_at = CURRENT_TIMESTAMP,
    updated_by = EXCLUDED.created_by;

-- =========================================================
-- SECURITY SUB MENUS - CATÁLOGOS / MAESTROS
-- =========================================================
INSERT INTO conciliaciones.security_sub_menu (menu_id, parameter_id, code, label, route, icon, sort_order, active, created_by)
SELECT m.id, 137, 'CLIENTES', 'Clientes', '/maestros/clientes', 'business', 1, TRUE, 'seed'
FROM conciliaciones.security_menu m WHERE m.code = 'CATALOGO_MAESTRO'
ON CONFLICT (code) DO UPDATE SET
    menu_id = EXCLUDED.menu_id,
    parameter_id = EXCLUDED.parameter_id,
    label = EXCLUDED.label,
    route = EXCLUDED.route,
    icon = EXCLUDED.icon,
    sort_order = EXCLUDED.sort_order,
    active = EXCLUDED.active,
    updated_at = CURRENT_TIMESTAMP,
    updated_by = EXCLUDED.created_by;

INSERT INTO conciliaciones.security_sub_menu (menu_id, parameter_id, code, label, route, icon, sort_order, active, created_by)
SELECT m.id, 138, 'PRODUCTORES', 'Productores', '/maestros/productores', 'groups', 2, TRUE, 'seed'
FROM conciliaciones.security_menu m WHERE m.code = 'CATALOGO_MAESTRO'
ON CONFLICT (code) DO UPDATE SET
    menu_id = EXCLUDED.menu_id,
    parameter_id = EXCLUDED.parameter_id,
    label = EXCLUDED.label,
    route = EXCLUDED.route,
    icon = EXCLUDED.icon,
    sort_order = EXCLUDED.sort_order,
    active = EXCLUDED.active,
    updated_at = CURRENT_TIMESTAMP,
    updated_by = EXCLUDED.created_by;

INSERT INTO conciliaciones.security_sub_menu (menu_id, parameter_id, code, label, route, icon, sort_order, active, created_by)
SELECT m.id, 139, 'AGENCIAS', 'Agencias', '/maestros/agencias', 'apartment', 3, TRUE, 'seed'
FROM conciliaciones.security_menu m WHERE m.code = 'CATALOGO_MAESTRO'
ON CONFLICT (code) DO UPDATE SET
    menu_id = EXCLUDED.menu_id,
    parameter_id = EXCLUDED.parameter_id,
    label = EXCLUDED.label,
    route = EXCLUDED.route,
    icon = EXCLUDED.icon,
    sort_order = EXCLUDED.sort_order,
    active = EXCLUDED.active,
    updated_at = CURRENT_TIMESTAMP,
    updated_by = EXCLUDED.created_by;

INSERT INTO conciliaciones.security_sub_menu (menu_id, parameter_id, code, label, route, icon, sort_order, active, created_by)
SELECT m.id, 140, 'CARRIERS', 'Carriers', '/maestros/carriers', 'domain', 4, TRUE, 'seed'
FROM conciliaciones.security_menu m WHERE m.code = 'CATALOGO_MAESTRO'
ON CONFLICT (code) DO UPDATE SET
    menu_id = EXCLUDED.menu_id,
    parameter_id = EXCLUDED.parameter_id,
    label = EXCLUDED.label,
    route = EXCLUDED.route,
    icon = EXCLUDED.icon,
    sort_order = EXCLUDED.sort_order,
    active = EXCLUDED.active,
    updated_at = CURRENT_TIMESTAMP,
    updated_by = EXCLUDED.created_by;

INSERT INTO conciliaciones.security_sub_menu (menu_id, parameter_id, code, label, route, icon, sort_order, active, created_by)
SELECT m.id, 141, 'POLIZAS', 'Pólizas', '/maestros/polizas', 'description', 5, TRUE, 'seed'
FROM conciliaciones.security_menu m WHERE m.code = 'CATALOGO_MAESTRO'
ON CONFLICT (code) DO UPDATE SET
    menu_id = EXCLUDED.menu_id,
    parameter_id = EXCLUDED.parameter_id,
    label = EXCLUDED.label,
    route = EXCLUDED.route,
    icon = EXCLUDED.icon,
    sort_order = EXCLUDED.sort_order,
    active = EXCLUDED.active,
    updated_at = CURRENT_TIMESTAMP,
    updated_by = EXCLUDED.created_by;

-- =========================================================
-- SECURITY SUB MENUS - CONCILIACIÓN
-- =========================================================
INSERT INTO conciliaciones.security_sub_menu (menu_id, parameter_id, code, label, route, icon, sort_order, active, created_by)
SELECT m.id, 142, 'ARCHIVO_FUENTE', 'Archivos fuente', '/conciliacion/source-files', 'upload_file', 1, TRUE, 'seed'
FROM conciliaciones.security_menu m WHERE m.code = 'CONCILIACION'
ON CONFLICT (code) DO UPDATE SET
    menu_id = EXCLUDED.menu_id,
    parameter_id = EXCLUDED.parameter_id,
    label = EXCLUDED.label,
    route = EXCLUDED.route,
    icon = EXCLUDED.icon,
    sort_order = EXCLUDED.sort_order,
    active = EXCLUDED.active,
    updated_at = CURRENT_TIMESTAMP,
    updated_by = EXCLUDED.created_by;

INSERT INTO conciliaciones.security_sub_menu (menu_id, parameter_id, code, label, route, icon, sort_order, active, created_by)
SELECT m.id, 143, 'EJECUCIONES', 'Ejecuciones', '/conciliacion/processing-executions', 'sync', 2, TRUE, 'seed'
FROM conciliaciones.security_menu m WHERE m.code = 'CONCILIACION'
ON CONFLICT (code) DO UPDATE SET
    menu_id = EXCLUDED.menu_id,
    parameter_id = EXCLUDED.parameter_id,
    label = EXCLUDED.label,
    route = EXCLUDED.route,
    icon = EXCLUDED.icon,
    sort_order = EXCLUDED.sort_order,
    active = EXCLUDED.active,
    updated_at = CURRENT_TIMESTAMP,
    updated_by = EXCLUDED.created_by;

INSERT INTO conciliaciones.security_sub_menu (menu_id, parameter_id, code, label, route, icon, sort_order, active, created_by)
SELECT m.id, 144, 'CASOS_CONCILIACION', 'Casos de conciliación', '/conciliacion/casos', 'fact_check', 3, TRUE, 'seed'
FROM conciliaciones.security_menu m WHERE m.code = 'CONCILIACION'
ON CONFLICT (code) DO UPDATE SET
    menu_id = EXCLUDED.menu_id,
    parameter_id = EXCLUDED.parameter_id,
    label = EXCLUDED.label,
    route = EXCLUDED.route,
    icon = EXCLUDED.icon,
    sort_order = EXCLUDED.sort_order,
    active = EXCLUDED.active,
    updated_at = CURRENT_TIMESTAMP,
    updated_by = EXCLUDED.created_by;

INSERT INTO conciliaciones.security_sub_menu (menu_id, parameter_id, code, label, route, icon, sort_order, active, created_by)
SELECT m.id, 145, 'CONCILIACION_MANUAL', 'Conciliación manual', '/conciliacion/manual', 'edit_note', 4, TRUE, 'seed'
FROM conciliaciones.security_menu m WHERE m.code = 'CONCILIACION'
ON CONFLICT (code) DO UPDATE SET
    menu_id = EXCLUDED.menu_id,
    parameter_id = EXCLUDED.parameter_id,
    label = EXCLUDED.label,
    route = EXCLUDED.route,
    icon = EXCLUDED.icon,
    sort_order = EXCLUDED.sort_order,
    active = EXCLUDED.active,
    updated_at = CURRENT_TIMESTAMP,
    updated_by = EXCLUDED.created_by;

-- =========================================================
-- SECURITY SUB MENUS - UPLOAD FILES
-- =========================================================
INSERT INTO conciliaciones.security_sub_menu (menu_id, parameter_id, code, label, route, icon, sort_order, active, created_by)
SELECT m.id, 146, 'UPLOAD_SOURCE_FILE', 'Upload sources file', '/upload-files/upload-sources-file', 'description', 1, TRUE, 'seed'
FROM conciliaciones.security_menu m WHERE m.code = 'UPLOAD_FILES'
ON CONFLICT (code) DO UPDATE SET
    menu_id = EXCLUDED.menu_id,
    parameter_id = EXCLUDED.parameter_id,
    label = EXCLUDED.label,
    route = EXCLUDED.route,
    icon = EXCLUDED.icon,
    sort_order = EXCLUDED.sort_order,
    active = EXCLUDED.active,
    updated_at = CURRENT_TIMESTAMP,
    updated_by = EXCLUDED.created_by;

-- =========================================================
-- SECURITY SUB MENUS - PAGOS / LIQUIDACIONES
-- =========================================================
INSERT INTO conciliaciones.security_sub_menu (menu_id, parameter_id, code, label, route, icon, sort_order, active, created_by)
SELECT m.id, 147, 'LIQUIDACIONES', 'Liquidaciones', '/pagos/liquidaciones', 'payments', 1, TRUE, 'seed'
FROM conciliaciones.security_menu m WHERE m.code = 'PAGOS_LIQUIDACION'
ON CONFLICT (code) DO UPDATE SET
    menu_id = EXCLUDED.menu_id,
    parameter_id = EXCLUDED.parameter_id,
    label = EXCLUDED.label,
    route = EXCLUDED.route,
    icon = EXCLUDED.icon,
    sort_order = EXCLUDED.sort_order,
    active = EXCLUDED.active,
    updated_at = CURRENT_TIMESTAMP,
    updated_by = EXCLUDED.created_by;

-- =========================================================
-- SECURITY SUB MENUS - REPORTES
-- =========================================================
INSERT INTO conciliaciones.security_sub_menu (menu_id, parameter_id, code, label, route, icon, sort_order, active, created_by)
SELECT m.id, 148, 'REPORTES_GENERALES', 'Reportes', '/reportes', 'assessment', 1, TRUE, 'seed'
FROM conciliaciones.security_menu m WHERE m.code = 'REPORTES'
ON CONFLICT (code) DO UPDATE SET
    menu_id = EXCLUDED.menu_id,
    parameter_id = EXCLUDED.parameter_id,
    label = EXCLUDED.label,
    route = EXCLUDED.route,
    icon = EXCLUDED.icon,
    sort_order = EXCLUDED.sort_order,
    active = EXCLUDED.active,
    updated_at = CURRENT_TIMESTAMP,
    updated_by = EXCLUDED.created_by;


-- =========================================================
-- ROL ADMIN
-- =========================================================
INSERT INTO conciliaciones.security_role (code,name,description,active,created_by)
VALUES ('ADMIN','Administrador','Rol administrador con acceso total al sistema',TRUE,'seed')
ON CONFLICT (code) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    active = EXCLUDED.active,
    updated_at = CURRENT_TIMESTAMP,
    updated_by = EXCLUDED.created_by;

-- =========================================================
-- PERMISOS ADMIN - TODOS LOS MENÚS Y SUBMENÚS
-- =========================================================
INSERT INTO conciliaciones.security_role_menu_permission (role_id,menu_id,sub_menu_id,active,created_by) 
SELECT r.id,sm.id AS menu_id, ssm.id AS sub_menu_id,TRUE,'seed'
FROM conciliaciones.security_role r 
INNER JOIN conciliaciones.security_sub_menu ssm ON ssm.active = TRUE
INNER JOIN conciliaciones.security_menu sm ON sm.id = ssm.menu_id AND sm.active = TRUE
WHERE r.code = 'ADMIN'
ON CONFLICT (role_id, menu_id, sub_menu_id) DO UPDATE SET
    active = EXCLUDED.active,
    updated_at = CURRENT_TIMESTAMP,
    updated_by = EXCLUDED.created_by;

-- =========================================================
-- AJUSTE DEFENSIVO DE SECUENCIAS BIGSERIAL
-- =========================================================
SELECT setval(pg_get_serial_sequence('conciliaciones.security_menu', 'id'), COALESCE((SELECT MAX(id) FROM conciliaciones.security_menu), 1), true);
SELECT setval(pg_get_serial_sequence('conciliaciones.security_sub_menu', 'id'), COALESCE((SELECT MAX(id) FROM conciliaciones.security_sub_menu), 1), true);
SELECT setval(pg_get_serial_sequence('conciliaciones.security_role', 'id'), COALESCE((SELECT MAX(id) FROM conciliaciones.security_role), 1), true);
SELECT setval(pg_get_serial_sequence('conciliaciones.security_role_menu_permission', 'id'), COALESCE((SELECT MAX(id) FROM conciliaciones.security_role_menu_permission), 1), true);