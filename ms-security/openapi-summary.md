# OpenAPI Summary - ms-security

## Tags

- Auth
- Profile
- Users
- Roles
- Audit

## Public endpoints

- POST `/api/security/auth/login`
- POST `/api/security/auth/refresh`

## Protected endpoints

- POST `/api/security/auth/logout`
- GET `/api/security/me`
- GET `/api/security/me/roles`
- GET `/api/security/me/permissions`
- POST `/api/security/users`
- GET `/api/security/users`
- GET `/api/security/users/{userId}`
- PUT `/api/security/users/{userId}`
- PATCH `/api/security/users/{userId}/status`
- PATCH `/api/security/users/{userId}/reset-password`
- POST `/api/security/users/{userId}/roles`
- GET `/api/security/roles`
- POST `/api/security/roles`
- GET `/api/security/roles/{roleName}`
- PUT `/api/security/roles/{roleName}`
- POST `/api/security/roles/{roleName}/permissions`
- GET `/api/security/audit`
- GET `/api/security/audit/{eventId}`
