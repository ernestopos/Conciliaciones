package com.conciliaciones.msmanagementtask.infrastructure.adapter.in.kafka;

import com.conciliaciones.domain.task.TaskType;
import com.conciliaciones.msmanagementtask.application.event.FileUploadConfirmedEvent;
import com.conciliaciones.msmanagementtask.application.port.in.CreateScheduledTaskCommand;
import com.conciliaciones.msmanagementtask.application.port.in.CreateScheduledTaskUseCase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileUploadConfirmedConsumer {

    private final CreateScheduledTaskUseCase createScheduledTaskUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${management-task.kafka.topics.file-upload-confirmed}")
    public void consume(String message) {
        log.info("LOG INICIO X = consume - message={}", message);
        try {
            FileUploadConfirmedEvent event = objectMapper.readValue(message,FileUploadConfirmedEvent.class);
            String payload = objectMapper.writeValueAsString(event);
            log.info("Evento file-upload-confirmed recibido correctamente - sourceFileId={}, bucket={}, objectKey={}",event.sourceFileId(),event.bucketName(),event.objectKey());
            createScheduledTaskUseCase.create(new CreateScheduledTaskCommand(event.sourceFileId(),TaskType.FILE_DATA_REVIEW,payload,"KAFKA"));
            log.info("LOG FIN X = consume - sourceFileId={}", event.sourceFileId());
        } catch (JsonProcessingException e) {
            log.error("Error deserializando mensaje Kafka file-upload-confirmed. message={}", message, e);
        } catch (Exception e) {
            log.error("Error procesando evento Kafka file-upload-confirmed. message={}", message, e);
        }
    }
}