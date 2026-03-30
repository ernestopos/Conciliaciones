# conciliaciones-persistence-jpa-parent

Proyecto Maven multi-módulo en Java 21 para centralizar el modelo de dominio y la persistencia JPA de la plataforma Conciliaciones.

## Módulos

### 1. conciliaciones-domain-model
Contiene:
- entidades de dominio
- objetos de valor
- enums
- contratos de repositorio

### 2. conciliaciones-persistence-jpa
Contiene:
- entidades JPA
- repositorios Spring Data JPA
- mappers MapStruct
- adaptadores que implementan los puertos del dominio
- auto-configuración Spring Boot
- configuración base con `application.properties`

## Estructura de paquetes

### conciliaciones-domain-model
- `com.conciliaciones.domain.common`
- `com.conciliaciones.domain.audit`
- `com.conciliaciones.domain.file`
- `com.conciliaciones.domain.processing`

### conciliaciones-persistence-jpa
- `com.conciliaciones.persistence.config`
- `com.conciliaciones.persistence.adapter`
- `com.conciliaciones.persistence.mapper`
- `com.conciliaciones.persistence.jpa.entity`
- `com.conciliaciones.persistence.jpa.file`
- `com.conciliaciones.persistence.jpa.processing`

## Notas
- Java 21
- Spring Boot 3.3.x
- PostgreSQL
- Configuración en `.properties`
- Auto-configuración habilitada vía `AutoConfiguration.imports`

## Importante
En este entorno no fue posible ejecutar `mvn package` porque Maven no está instalado en el contenedor. La estructura y los POM quedaron preparados para compilar en un entorno con Maven 3.9+ y JDK 21.
