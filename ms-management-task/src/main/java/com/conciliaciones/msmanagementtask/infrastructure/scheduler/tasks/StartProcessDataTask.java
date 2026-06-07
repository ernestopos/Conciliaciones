package com.conciliaciones.msmanagementtask.infrastructure.scheduler.tasks;

import com.conciliaciones.domain.entity.*;
import com.conciliaciones.persistence.jpa.entity.ScheduledTaskEntity;
import com.conciliaciones.persistence.jpa.entity.SourceFileEntity;
import com.conciliaciones.persistence.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component("startProcessDataTask")
@RequiredArgsConstructor
@Transactional
public class StartProcessDataTask extends AbstractManagementTask {

    private static final String SYSTEM_USER = "startProcessDataTask";
    private static final String POLICY_STATUS_GROUP = "POLICY_STATUS";

    private static final String CLIENT_ID_FIELD = "client_id";
    private static final String CLIENT_FIRST_NAME_FIELD = "client_first_name";
    private static final String CLIENT_MIDDLE_NAME_FIELD = "client_middle_name";
    private static final String CLIENT_LAST_NAME_FIELD = "client_last_name";
    private static final String CLIENT_FULL_NAME_FIELD = "client_full_name";
    private static final String CLIENT_BIRTH_DATE_FIELD = "client_birth_date";
    private static final String CLIENT_STATE_FIELD = "client_state";

    private static final String POLICY_NUMBER_FIELD = "policy_number";
    private static final String SUBSCRIBER_ID_FIELD = "subscriber_id";
    private static final String EFFECTIVE_DATE_FIELD = "effective_date";
    private static final String ISSUE_DATE_FIELD = "issue_date";
    private static final String TERMINATION_DATE_FIELD = "termination_date";
    private static final String STATUS_ID_FIELD = "status_id";
    private static final String ISSUE_STATE_FIELD = "issue_state";
    private static final String MEMBERS_COUNT_FIELD = "members_count";
    private static final String PRODUCER_EXTERNAL_ID="producer_external_id";

    private static final String STATEMENT_DATE_FIELD = "statement_date";
    private static final String PAID_DATE_FIELD = "paid_date";
    private static final String NET_AMOUNT_FIELD = "net_amount";
    private static final String RATE_FIELD = "rate";
    private static final String COMMISSION_RATE_PCT_FIELD = "commission_rate_pct";


    private static final String STATUS_ACTIVE = "Activa";
    private static final String STATUS_APPROVED = "Aprobada";

    private final RawImportRecordRepository rawImportRecordRepository;
    private final ClientRepository clientRepository;
    private final PolicyRepository policyRepository;
    private final ParameterRepository parameterRepository;
    private final ProducerRepository producerRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    protected void doExecute(ScheduledTaskEntity task) {
        Long sourceFileId = task.getExecutionPlanTask().getSourceFile().getId();
        SourceFileEntity sourceFile = task.getExecutionPlanTask().getSourceFile();

        log.info("LOG INICIO X = startProcessDataTask.processData - sourceFileId={}, carrierId={}, executionPlanTaskId={}, taskId={}",
                sourceFileId, sourceFile.getCarrierId(), task.getExecutionPlanTask().getId(), task.getId());

        List<RawImportRecordEntity> rawRecords = rawImportRecordRepository.findBySourceFileIdOrderByRowNumberAsc(sourceFileId);

        int clientsCreated = 0;
        int clientsUpdated = 0;
        int policiesCreated = 0;
        int policiesUpdated = 0;
        int skippedRows = 0;

        for (RawImportRecordEntity rawRecord : rawRecords) {
            JsonNode payload = rawRecord.getRawPayload();

            if (payload == null || !payload.isObject()) {
                skippedRows++;
                log.warn("Registro raw sin payload válido. rawImportRecordId={}, rowNumber={}",
                        rawRecord.getId(), rawRecord.getRowNumber());
                continue;
            }

            String externalClientId = getText(payload, CLIENT_ID_FIELD);
            if (externalClientId == null) {
                skippedRows++;
                log.warn("Registro raw sin client_id. rawImportRecordId={}, rowNumber={}",
                        rawRecord.getId(), rawRecord.getRowNumber());
                continue;
            }

            UpsertResult<ClientEntity> clientResult = upsertClient(payload, externalClientId);
            if (clientResult.created()) {
                clientsCreated++;
            } else {
                clientsUpdated++;
            }

            String policyNumber = getText(payload, POLICY_NUMBER_FIELD);
            if (policyNumber == null) {
                skippedRows++;
                log.warn("Registro raw sin policy_number. rawImportRecordId={}, rowNumber={}, clientId={}",
                        rawRecord.getId(), rawRecord.getRowNumber(), externalClientId);
                continue;
            }

            UpsertResult<PolicyEntity> policyResult = upsertPolicy(
                    payload,
                    sourceFile,
                    rawRecord,
                    clientResult.entity(),
                    policyNumber
            );

            if (policyResult.created()) {
                policiesCreated++;
                createCommissionStatement(payload, sourceFile, rawRecord, clientResult.entity(), policyResult.entity());
            } else {
                policiesUpdated++;
            }


        }

        log.info("LOG FIN X = startProcessDataTask.processData - sourceFileId={}, rawRows={}, clientsCreated={}, clientsUpdated={}, policiesCreated={}, policiesUpdated={}, skippedRows={}",
                sourceFileId, rawRecords.size(), clientsCreated, clientsUpdated, policiesCreated, policiesUpdated, skippedRows);
    }

    private UpsertResult<ClientEntity> upsertClient(JsonNode payload, String externalClientId) {
        LocalDateTime now = LocalDateTime.now();
        Optional<ClientEntity> existingClient = findClientByExternalClientId(externalClientId);

        ClientEntity client = existingClient.orElseGet(ClientEntity::new);
        boolean created = existingClient.isEmpty();

        client.setExternalClientId(externalClientId);
        client.setFirstName(getText(payload, CLIENT_FIRST_NAME_FIELD));
        client.setMiddleName(getText(payload, CLIENT_MIDDLE_NAME_FIELD));
        client.setLastName(getText(payload, CLIENT_LAST_NAME_FIELD));
        client.setFullName(resolveClientFullName(payload));
        client.setBirthDate(getLocalDate(payload, CLIENT_BIRTH_DATE_FIELD));
        client.setState(getText(payload, CLIENT_STATE_FIELD));
        client.setActive(Boolean.TRUE);

        if (created) {
            client.setCreatedAt(now);
            client.setCreatedBy(SYSTEM_USER);
        } else {
            client.setUpdatedAt(now);
            client.setUpdatedBy(SYSTEM_USER);
        }

        return new UpsertResult<>(clientRepository.save(client), created);
    }

    private UpsertResult<PolicyEntity> upsertPolicy(JsonNode payload,
                                                    SourceFileEntity sourceFile,
                                                    RawImportRecordEntity rawRecord,
                                                    ClientEntity client,
                                                    String policyNumber) {
        LocalDateTime now = LocalDateTime.now();
        String sourceKey = buildSourceKey(sourceFile.getId(), rawRecord.getRowNumber(), policyNumber);
        Optional<PolicyEntity> existingPolicy = findPolicy(sourceFile.getCarrierId(), policyNumber);

        boolean created = existingPolicy.isEmpty();
        ParameterEntity incomingStatus = resolvePolicyStatus(payload);
        PolicyEntity policy;

        if (created) {
            policy = new PolicyEntity();
            policy.setCarrierId(sourceFile.getCarrierId());
            policy.setClientId(client.getId());
            policy.setPolicyNumber(policyNumber);
            policy.setSubscriberId(getText(payload, SUBSCRIBER_ID_FIELD));
            policy.setEffectiveDate(getLocalDate(payload, EFFECTIVE_DATE_FIELD));
            policy.setIssueDate(getLocalDate(payload, ISSUE_DATE_FIELD));
            policy.setTerminationDate(getLocalDate(payload, TERMINATION_DATE_FIELD));
            policy.setStatusId(incomingStatus);
            policy.setResidentState(resolveResidentState());
            policy.setIssueState(getText(payload, ISSUE_STATE_FIELD));
            policy.setMembersCount(getInteger(payload, MEMBERS_COUNT_FIELD));
            policy.setSourceKey(sourceKey);
            policy.setActive(Boolean.TRUE);
            policy.setCreatedAt(now);
            policy.setCreatedBy(SYSTEM_USER);

            policy = policyRepository.save(policy);
        } else {
            policy = existingPolicy.get();
            if(policy.getStatusId().getName().equalsIgnoreCase(STATUS_ACTIVE) && incomingStatus.getName().equalsIgnoreCase(STATUS_APPROVED)){
                policy.setStatusId(incomingStatus);
                policy = policyRepository.save(policy);
            }
        }
        createPolicyStatusHistory(policy, sourceFile, incomingStatus, payload, now);
        return new UpsertResult<>(policy, created);
    }

    private void createPolicyStatusHistory(PolicyEntity policy,
                                           SourceFileEntity sourceFile,
                                           ParameterEntity status,
                                           JsonNode payload,
                                           LocalDateTime now) {
        PolicyStatusHistoryEntity history = new PolicyStatusHistoryEntity();
        history.setPolicyId(policy.getId());
        history.setSourceFileId(sourceFile.getId());
        history.setStatusId(status.getId());
        history.setEffectiveFrom(resolveStatusEffectiveDate(payload));
        history.setNotes("Estado registrado desde archivo fuente " + sourceFile.getId());
        history.setCreatedAt(now);
        history.setCreatedBy(SYSTEM_USER);
        entityManager.persist(history);
    }

    private void createCommissionStatement(JsonNode payload,SourceFileEntity sourceFile,RawImportRecordEntity rawRecord,ClientEntity client,PolicyEntity policy) {
        LocalDateTime now = LocalDateTime.now();


        String external_producer_Id_Value = getText(payload, PRODUCER_EXTERNAL_ID);
        ProducerEntity producer = producerRepository.findByExternalProducerId(external_producer_Id_Value)
                .orElseThrow(() -> new RuntimeException("Producer no encontrado: " + external_producer_Id_Value));
        CommissionStatementEntity statement = new CommissionStatementEntity();
        statement.setSourceFileId(sourceFile.getId());
        statement.setRawImportRecordId(rawRecord.getId());
        statement.setCarrierId(sourceFile.getCarrierId());
        statement.setClientId(client.getId());
        statement.setProducerId(producer.getId());
        statement.setAgencyId(producer.getAgencyId());
        statement.setPolicyId(policy.getId());
        statement.setStatementDate(getLocalDate(payload, STATEMENT_DATE_FIELD));
        statement.setPaidDate(getLocalDate(payload, PAID_DATE_FIELD));
        statement.setRowIdentifier(rawRecord.getSourceRowKey());
        statement.setSourceRowNumber(rawRecord.getRowNumber());
        statement.setCreatedAt(now);
        statement.setCreatedBy(SYSTEM_USER);
        entityManager.persist(statement);
        entityManager.flush();
        CommissionStatementItemEntity item = new CommissionStatementItemEntity();
        item.setCommissionStatementId(statement.getId());
        item.setNetAmount(getBigDecimal(payload, NET_AMOUNT_FIELD));
        item.setRate(getBigDecimal(payload, RATE_FIELD));
        item.setCommissionRatePct(getBigDecimal(payload, COMMISSION_RATE_PCT_FIELD));
        item.setCreatedAt(now);
        item.setCreatedBy(SYSTEM_USER);
        entityManager.persist(item);
    }

    private Optional<ClientEntity> findClientByExternalClientId(String externalClientId) {
        return entityManager.createQuery("""
                        SELECT client
                        FROM ClientEntity client
                        WHERE client.externalClientId = :externalClientId
                        """, ClientEntity.class)
                .setParameter("externalClientId", externalClientId)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    private Optional<PolicyEntity> findPolicy(Long carrierId, String policyNumber) {
        return entityManager.createQuery("""
                        SELECT policy
                        FROM PolicyEntity policy
                        WHERE policy.carrierId = :carrierId
                          AND policy.policyNumber = :policyNumber
                        """, PolicyEntity.class)
                .setParameter("carrierId", carrierId)
                .setParameter("policyNumber", policyNumber)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    private ParameterEntity resolvePolicyStatus(JsonNode payload) {
        String policyStatusName = firstNonBlank(getText(payload, STATUS_ID_FIELD));

        if (policyStatusName == null) {
            throw new IllegalStateException("No fue informado el estado de la póliza en el payload.");
        }

        return parameterRepository.findByParameterGroupAndValueAndActiveTrue(
                        POLICY_STATUS_GROUP,
                        policyStatusName)
                .orElseThrow(() -> new IllegalStateException(
                        "No existe parámetro activo "
                                + POLICY_STATUS_GROUP
                                + "/"
                                + policyStatusName));
    }

    private CityEntity resolveResidentState() {
        return entityManager.find(CityEntity.class, 1L);
    }

    private LocalDate resolveStatusEffectiveDate(JsonNode payload) {
        LocalDate effectiveDate = getLocalDate(payload, EFFECTIVE_DATE_FIELD);
        if (effectiveDate != null) {
            return effectiveDate;
        }

        LocalDate statementDate = getLocalDate(payload, STATEMENT_DATE_FIELD);
        return statementDate != null ? statementDate : LocalDate.now();
    }

    private String resolveClientFullName(JsonNode payload) {
        String fullName = getText(payload, CLIENT_FULL_NAME_FIELD);
        if (fullName != null) {
            return fullName;
        }

        return firstNonBlank(String.join(" ",
                valueOrEmpty(getText(payload, CLIENT_FIRST_NAME_FIELD)),
                valueOrEmpty(getText(payload, CLIENT_MIDDLE_NAME_FIELD)),
                valueOrEmpty(getText(payload, CLIENT_LAST_NAME_FIELD))
        ).replaceAll("\\s+", " ").trim(), "SIN NOMBRE");
    }

    private String buildSourceKey(Long sourceFileId, Integer rowNumber, String policyNumber) {
        return "sourceFileId=" + sourceFileId + "|rowNumber=" + rowNumber + "|policyNumber=" + policyNumber;
    }

    private String getText(JsonNode payload, String fieldName) {
        JsonNode node = payload.get(fieldName);
        if (node == null || node.isNull()) {
            return null;
        }

        String value = node.asText(null);
        return value == null || value.isBlank() ? null : value.trim();
    }

    private Integer getInteger(JsonNode payload, String fieldName) {
        String value = getText(payload, fieldName);
        if (value == null) {
            return null;
        }

        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException exception) {
            throw new IllegalStateException("El campo " + fieldName + " no tiene un valor numérico válido: " + value, exception);
        }
    }

    private BigDecimal getBigDecimal(JsonNode payload, String fieldName) {
        String value = getText(payload, fieldName);
        if (value == null) {
            return null;
        }

        try {
            return new BigDecimal(value);
        } catch (NumberFormatException exception) {
            throw new IllegalStateException("El campo " + fieldName + " no tiene un valor decimal válido: " + value, exception);
        }
    }

    private LocalDate getLocalDate(JsonNode payload, String fieldName) {
        String value = getText(payload, fieldName);
        if (value == null) {
            return null;
        }

        List<DateTimeFormatter> supportedFormats = List.of(
                DateTimeFormatter.ISO_LOCAL_DATE,
                DateTimeFormatter.ofPattern("M/d/yyyy"),
                DateTimeFormatter.ofPattern("MM/dd/yyyy"),
                DateTimeFormatter.ofPattern("d/M/yyyy"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
        );

        for (DateTimeFormatter formatter : supportedFormats) {
            try {
                return LocalDate.parse(value, formatter);
            } catch (DateTimeParseException ignored) {
                // Se intenta con el siguiente formato soportado.
            }
        }

        throw new IllegalStateException("El campo " + fieldName + " no tiene un formato de fecha válido: " + value);
    }

    private String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }

        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }

        return null;
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

    private record UpsertResult<T>(T entity, boolean created) {
    }
}