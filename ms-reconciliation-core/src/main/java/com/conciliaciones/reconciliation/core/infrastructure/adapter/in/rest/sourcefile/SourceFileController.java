package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.sourcefile;

import com.conciliaciones.reconciliation.core.application.port.in.ListSourceFilesUseCase;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.SourceFileResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/source-files")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Source Files", description = "Consulta de archivos fuente cargados")
public class SourceFileController {

    private final ListSourceFilesUseCase listSourceFilesUseCase;

    @GetMapping
    public Page<SourceFileResponse> list(Pageable pageable) {
        log.info("LOG INICIO X = listSourceFilesController page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<SourceFileResponse> response = listSourceFilesUseCase.list(pageable);
        log.info("LOG FIN X = listSourceFilesController totalElements={}", response.getTotalElements());
        return response;
    }
}
