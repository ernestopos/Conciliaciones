package com.conciliaciones.messaging.infrastructure.adapter.out.sqs;

import com.conciliaciones.messaging.application.port.out.QueuePublisherPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Component
@RequiredArgsConstructor
public class SqsPublisherAdapter implements QueuePublisherPort {

    private final SqsClient sqsClient;

    @Override
    public void sendMessage(String queueUrl, String message) {
        sqsClient.sendMessage(
            SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(message)
                .build()
        );
    }
}
