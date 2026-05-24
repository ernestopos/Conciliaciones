package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.policy;

import com.conciliaciones.reconciliation.core.application.port.in.policy.CreatePolicyUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.policy.DeletePolicyUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.policy.GetPolicyByIdUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.policy.ListPoliciesUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.policy.UpdatePolicyUseCase;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.policy.CreatePolicyRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.policy.PolicyResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.policy.UpdatePolicyRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.support.AuthenticatedUserResolver;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/policies")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Policy", description = "Operaciones CRUD para pólizas")
public class PolicyController {

    private final CreatePolicyUseCase createPolicyUseCase;
    private final GetPolicyByIdUseCase getPolicyByIdUseCase;
    private final ListPoliciesUseCase listPoliciesUseCase;
    private final UpdatePolicyUseCase updatePolicyUseCase;
    private final DeletePolicyUseCase deletePolicyUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PolicyResponse create(@Valid @RequestBody CreatePolicyRequest request, @AuthenticationPrincipal Jwt jwt) {
        log.info("LOG INICIO X = createPolicyController");
        PolicyResponse response = createPolicyUseCase.create(request, AuthenticatedUserResolver.resolveUsername(jwt));
        log.info("LOG FIN X = createPolicyController id={}", response.id());
        return response;
    }

    @GetMapping("/{id}")
    public PolicyResponse getById(@PathVariable Long id) {
        log.info("LOG INICIO X = getPolicyByIdController id={}", id);
        PolicyResponse response = getPolicyByIdUseCase.getById(id);
        log.info("LOG FIN X = getPolicyByIdController id={}", response.id());
        return response;
    }

    @GetMapping
    public Page<PolicyResponse> list(Pageable pageable) {
        log.info("LOG INICIO X = listPoliciesController page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<PolicyResponse> response = listPoliciesUseCase.list(pageable);
        log.info("LOG FIN X = listPoliciesController totalElements={}", response.getTotalElements());
        return response;
    }

    @PutMapping("/{id}")
    public PolicyResponse update(@PathVariable Long id, @Valid @RequestBody UpdatePolicyRequest request, @AuthenticationPrincipal Jwt jwt) {
        log.info("LOG INICIO X = updatePolicyController id={}", id);
        PolicyResponse response = updatePolicyUseCase.update(id, request, AuthenticatedUserResolver.resolveUsername(jwt));
        log.info("LOG FIN X = updatePolicyController id={}", response.id());
        return response;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        log.info("LOG INICIO X = deletePolicyController id={}", id);
        deletePolicyUseCase.delete(id);
        log.info("LOG FIN X = deletePolicyController id={}", id);
    }
}
