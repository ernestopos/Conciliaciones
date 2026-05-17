package com.conciliaciones.msmanagementtask.infrastructure.adapter.in.kafka;

import com.conciliaciones.msmanagementtask.application.event.TaskExecutionRequestedEvent;
import com.conciliaciones.msmanagementtask.application.usecase.PipelineTaskExecutorService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskExecutionRequestedConsumer {

    private final PipelineTaskExecutorService pipelineTaskExecutorService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${management-task.kafka.topics.task-execution-requested}")
    public void consume(String message) {
        log.info("LOG INICIO X = consumeTaskExecutionRequested - message={}", message);

        try {
            TaskExecutionRequestedEvent event = objectMapper.readValue(message, TaskExecutionRequestedEvent.class);

            log.info("Evento task-execution-requested recibido correctamente - executionPlanTaskId={}, scheduledTaskId={}, taskType={}",
                    event.executionPlanTaskId(), event.scheduledTaskId(), event.taskType());

            pipelineTaskExecutorService.execute(event.scheduledTaskId());

            log.info("LOG FIN X = consumeTaskExecutionRequested - executionPlanTaskId={}, scheduledTaskId={}, taskType={}",
                    event.executionPlanTaskId(), event.scheduledTaskId(), event.taskType());
        } catch (JsonProcessingException e) {
            log.error("Error deserializando evento Kafka task-execution-requested. message={}", message, e);
        } catch (Exception e) {
            log.error("Error procesando evento Kafka task-execution-requested. message={}", message, e);
        }
    }
}
