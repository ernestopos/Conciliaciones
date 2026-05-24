package com.conciliaciones.msfilemanagement.infrastructure.adapter.in.rest.storage;

import com.conciliaciones.msfilemanagement.application.port.in.storage.ConfirmUploadCallbackCommand;
import com.conciliaciones.msfilemanagement.application.port.in.storage.ConfirmUploadCallbackResult;
import com.conciliaciones.msfilemanagement.application.port.in.storage.ConfirmUploadCallbackUseCase;
import com.conciliaciones.msfilemanagement.application.port.in.storage.CreateBucketUseCase;
import com.conciliaciones.msfilemanagement.application.port.in.storage.GeneratePresignedUploadUrlCommand;
import com.conciliaciones.msfilemanagement.application.port.in.storage.GeneratePresignedUploadUrlResult;
import com.conciliaciones.msfilemanagement.application.port.in.storage.GeneratePresignedUploadUrlUseCase;
import com.conciliaciones.msfilemanagement.application.port.in.storage.ListBucketsUseCase;
import com.conciliaciones.msfilemanagement.infrastructure.adapter.in.rest.storage.dto.ConfirmUploadCallbackRequest;
import com.conciliaciones.msfilemanagement.infrastructure.adapter.in.rest.storage.dto.ConfirmUploadCallbackResponse;
import com.conciliaciones.msfilemanagement.infrastructure.adapter.in.rest.storage.dto.CreateBucketRequest;
import com.conciliaciones.msfilemanagement.infrastructure.adapter.in.rest.storage.dto.CreateBucketResponse;
import com.conciliaciones.msfilemanagement.infrastructure.adapter.in.rest.storage.dto.GeneratePresignedUploadUrlRequest;
import com.conciliaciones.msfilemanagement.infrastructure.adapter.in.rest.storage.dto.GeneratePresignedUploadUrlResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/storage")
@RequiredArgsConstructor
@Tag(name = "Storage", description = "Operaciones S3 del microservicio ms-file-management")
public class StorageController {

    private final CreateBucketUseCase createBucketUseCase;
    private final ListBucketsUseCase listBucketsUseCase;
    private final GeneratePresignedUploadUrlUseCase generatePresignedUploadUrlUseCase;
    private final ConfirmUploadCallbackUseCase confirmUploadCallbackUseCase;

    @PostMapping("/buckets")
    @Operation(summary = "Crear bucket en S3")
    public ResponseEntity<CreateBucketResponse> createBucket(@RequestBody(required = false) CreateBucketRequest request) {
        String bucketName = createBucketUseCase.create(request == null ? null : request.bucketName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateBucketResponse(bucketName, "Bucket creado exitosamente"));
    }

    @GetMapping("/buckets")
    @Operation(summary = "Listar buckets en S3")
    public ResponseEntity<List<String>> listBuckets() {
        return ResponseEntity.ok(listBucketsUseCase.list());
    }

    @PostMapping("/presigned-url")
    @Operation(summary = "Generar URL prefirmada para carga de archivo")
    public ResponseEntity<GeneratePresignedUploadUrlResponse> generatePresignedUploadUrl(
            @Valid @RequestBody GeneratePresignedUploadUrlRequest request) {

        GeneratePresignedUploadUrlResult result = generatePresignedUploadUrlUseCase.generate(
                new GeneratePresignedUploadUrlCommand(
                        request.bucketName(),
                        request.fileName(),
                        request.contentType(),
                        request.carrierId(),
                        request.folder(),
                        request.createBucketPerUpload()
                )
        );

        return ResponseEntity.ok(new GeneratePresignedUploadUrlResponse(
                result.sourceFileId(),
                result.bucketName(),
                result.objectKey(),
                result.presignedUrl(),
                result.expiresInMinutes()
        ));
    }

    @PostMapping("/upload-callback")
    @Operation(summary = "Confirmar resultado del cargue del archivo a S3")
    public ResponseEntity<ConfirmUploadCallbackResponse> confirmUploadCallback(
            @Valid @RequestBody ConfirmUploadCallbackRequest request) {

        ConfirmUploadCallbackResult result = confirmUploadCallbackUseCase.confirm(
                new ConfirmUploadCallbackCommand(
                        request.sourceFileId(),
                        request.bucketName(),
                        request.objectKey(),
                        request.fileName(),
                        request.contentType(),
                        request.fileSizeBytes(),
                        request.success(),
                        request.errorMessage()
                )
        );

        return ResponseEntity.ok(new ConfirmUploadCallbackResponse(
                result.sourceFileId(),
                result.processingStatusId(),
                result.processingStatus(),
                "Estado del archivo actualizado correctamente"
        ));
    }
}
