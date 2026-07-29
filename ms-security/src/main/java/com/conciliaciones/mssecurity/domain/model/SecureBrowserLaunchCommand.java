package com.conciliaciones.mssecurity.domain.model;

public record SecureBrowserLaunchCommand(
        String userId,
        String username,
        String email,
        String sourceIp,
        String userAgent
) {
}
