package com.conciliaciones.msmanagementtask.infrastructure.adapter.out.kafka;

import com.conciliaciones.msmanagementtask.application.event.TaskExecutionRequestedEvent;
import com.conciliaciones.msmanagementtask.application.port.out.TaskExecutionEventPublisher;
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
public class KafkaTaskExecutionEventPublisher implements TaskExecutionEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${management-task.kafka.topics.task-execution-requested:conciliaciones.task-execution-requested}")
    private String topic;

    @Override
    public void publish(TaskExecutionRequestedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            String key = String.valueOf(event.executionPlanTaskId());
            log.info("Publicando evento para ejecutar tarea. topic={}, executionPlanTaskId={}, scheduledTaskId={}, taskType={}",
                    topic, event.executionPlanTaskId(), event.scheduledTaskId(), event.taskType());
            kafkaTemplate.send(topic, key, payload);
        } catch (JsonProcessingException e) {
            log.error("Error serializando evento de ejecución de tarea. executionPlanTaskId={}, scheduledTaskId={}",
                    event.executionPlanTaskId(), event.scheduledTaskId(), e);
        }
    }
}
