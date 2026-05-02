package com.conciliaciones.msfilemanagement.infrastructure.adapter.out.kafka;

import com.conciliaciones.domain.file.SourceFile;
import com.conciliaciones.msfilemanagement.application.event.FileUploadConfirmedEvent;
import com.conciliaciones.msfilemanagement.application.port.out.FileUploadConfirmedEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaFileUploadConfirmedEventPublisher implements FileUploadConfirmedEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${file-management.kafka.topics.file-upload-confirmed:conciliaciones.file-upload-confirmed}")
    private String topic;

    @Override
    public void publish(SourceFile sourceFile) {
        try {
            FileUploadConfirmedEvent event = new FileUploadConfirmedEvent(
                    sourceFile.getId(),
                    sourceFile.getBucketName(),
                    sourceFile.getObjectKey(),
                    sourceFile.getOriginalFileName(),
                    "S3_UPLOAD_CONFIRMED"
            );
            String payload = objectMapper.writeValueAsString(event);
            log.info("Publicando evento Kafka de cargue confirmado. topic={}, sourceFileId={}", topic, sourceFile.getId());
            kafkaTemplate.send(topic, String.valueOf(sourceFile.getId()), payload);
        } catch (Exception e) {
            log.error("Error serializando evento Kafka", e);
            throw new RuntimeException(e);
        }
    }
}
