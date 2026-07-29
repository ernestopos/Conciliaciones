package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.sourcefile;

import com.conciliaciones.persistence.repository.projection.DonutCharValTolProjection;
import com.conciliaciones.persistence.repository.projection.SourceFileProjection;
import com.conciliaciones.reconciliation.core.application.port.in.sourceFile.ListSourceFilesUseCase;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.sourceFile.SourceFileResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/core/v1/source-files")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Source Files", description = "Consulta de archivos fuente cargados")
public class SourceFileController {

    private final ListSourceFilesUseCase listSourceFilesUseCase;

    @GetMapping(path = "/rawdata")
    public Page<SourceFileResponse> list(Pageable pageable) {
        log.info("LOG INICIO X = listSourceFilesController page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<SourceFileResponse> response = listSourceFilesUseCase.list(pageable);
        log.info("LOG FIN X = listSourceFilesController totalElements={}", response.getTotalElements());
        return response;
    }

    @GetMapping
    public Page<SourceFileProjection> findAllWithProcessingStatus(Pageable pageable) {
        log.info("LOG INICIO X = findAllWithProcessingStatus page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<SourceFileProjection> response = listSourceFilesUseCase.findAllWithProcessingStatus(pageable);
        log.info("LOG FIN X = findAllWithProcessingStatus totalElements={}", response.getTotalElements());
        return response;
    }

    @GetMapping(path = "/charFileUploads")
    public List<DonutCharValTolProjection> charFileUploads() {
        log.info("LOG INICIO X = charFileUploads page={} size={}");
        List<DonutCharValTolProjection> response = listSourceFilesUseCase.charFileUploads();
        log.info("LOG FIN X = charFileUploads totalElements={}",response.size());
        return response;
    }
}