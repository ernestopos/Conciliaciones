package com.conciliaciones.msfilemanagement.infrastructure.adapter.in.rest;

import com.conciliaciones.domain.file.SourceFile;
import com.conciliaciones.msfilemanagement.application.port.in.CreateSourceFileCommand;
import com.conciliaciones.msfilemanagement.application.port.in.CreateSourceFileUseCase;
import com.conciliaciones.msfilemanagement.application.port.in.GetSourceFileUseCase;
import com.conciliaciones.msfilemanagement.domain.model.FileManagementHealth;
import com.conciliaciones.msfilemanagement.infrastructure.adapter.in.rest.dto.CreateSourceFileRequest;
import com.conciliaciones.msfilemanagement.infrastructure.adapter.in.rest.dto.SourceFileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Tag(name = "File Management", description = "Operaciones base del microservicio de archivos")
public class FileManagementController {

    private final CreateSourceFileUseCase createSourceFileUseCase;
    private final GetSourceFileUseCase getSourceFileUseCase;

    @PostMapping
    @Operation(summary = "Crear registro base de archivo")
    public ResponseEntity<SourceFileResponse> create(@Valid @RequestBody CreateSourceFileRequest request) {
        CreateSourceFileCommand command = new CreateSourceFileCommand(
                request.clientId(),
                request.originalFileName(),
                request.storagePath(),
                request.mimeType(),
                request.fileSize(),
                request.fileType(),
                request.checksum(),
                request.createdBy()
        );

        SourceFile sourceFile = createSourceFileUseCase.create(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(SourceFileResponse.from(sourceFile));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar archivo por id")
    public ResponseEntity<SourceFileResponse> findById(@PathVariable Long id) {
        return getSourceFileUseCase.findById(id)
                .map(SourceFileResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/health")
    @Operation(summary = "Health funcional del micro")
    public ResponseEntity<FileManagementHealth> health() {
        return ResponseEntity.ok(new FileManagementHealth("ms-file-management", "UP"));
    }
}
