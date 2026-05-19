package com.conciliaciones.msmanagementtask.infrastructure.adapter.out.kafka;

import com.conciliaciones.msmanagementtask.application.event.ExecutionPlanStatusEvent;
import com.conciliaciones.msmanagementtask.application.port.out.ExecutionPlanEventPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaExecutionPlanEventPublisher implements ExecutionPlanEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${management-task.kafka.topics.execution-plan-status-changed:conciliaciones.execution-plan-status-changed}")
    private String topic;

    @Override
    public void publish(ExecutionPlanStatusEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            String key = String.valueOf(event.executionPlanTaskId());
            log.info("Publicando evento de estado del pipeline. topic={}, executionPlanTaskId={}, status={}",
                    topic, event.executionPlanTaskId(), event.planStatus());
            kafkaTemplate.send(topic, key, payload);
        } catch (JsonProcessingException e) {
            log.error("Error serializando evento de estado del pipeline. executionPlanTaskId={}",
                    event.executionPlanTaskId(), e);
        }
    }
}
