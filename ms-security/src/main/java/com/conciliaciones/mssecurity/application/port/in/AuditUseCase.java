package com.conciliaciones.mssecurity.application.port.in;

import com.conciliaciones.mssecurity.domain.model.AuditActionResult;

public interface AuditUseCase {

    void register(String usuario,
                  String accion,
                  AuditActionResult resultado,
                  String detalle,
                  Object valorAntes,
                  Object valorDespues,
                  String estado);
}
