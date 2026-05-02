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
    public void consume(FileUploadConfirmedEvent event) throws JsonProcessingException {
        log.info("LOG INICIO X = consume - sourceFileId={}", event.sourceFileId());
        String payload = objectMapper.writeValueAsString(event);
        createScheduledTaskUseCase.create(new CreateScheduledTaskCommand(
                event.sourceFileId(),
                TaskType.FILE_DATA_REVIEW,
                payload,
                "KAFKA"
        ));
    }
}
