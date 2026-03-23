# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Proyecto

Microservicio `ms-usuario` — parte de un sistema de plazoleta de comidas. Construido con Java 21, Spring Boot 4, PostgreSQL y Lombok. Sigue **arquitectura hexagonal** (puertos y adaptadores).

## Comandos

```bash
# Compilar
./gradlew build

# Ejecutar
./gradlew bootRun

# Tests
./gradlew test

# Un solo test
./gradlew test --tests "ms_usuario.plazoleta.ms_usuario.MsUsuarioApplicationTests"

# Limpiar
./gradlew clean
```

## Arquitectura hexagonal

```
domain/
  model/         → modelos de dominio puros (sin dependencias externas, sin Lombok)
                   → Usuario.java (id, nombre, apellido, documentoDeIdentidad, celular,
                                   fechaNacimiento, correo, clave, rol)
                   → Rol.java (enum: ADMINISTRADOR, PROPIETARIO, EMPLEADO, CLIENTE)
  ports/
    in/          → IUsuarioServicePort (guardarPropietario, obtenerRolUsuario)
    out/         → IUsuarioPersistencePort (guardarUsuario, buscarPorId)

application/
  usecase/       → UsuarioUseCase: implementa los puertos in (sin @Service)

infrastructure/
  config/        → BeanConfiguration: conecta puertos con implementaciones
  input/rest/
    dto/         → UsuarioRequestDto (entrada), UsuarioRolResponseDto (salida con campo "rol")
    mapper/      → IUsuarioRequestMapper (DTO → dominio)
    *RestController.java
  output/persistence/
    entity/      → UsuarioEntity (@Entity, usa Lombok)
    mapper/      → IUsuarioEntityMapper (interfaz) + UsuarioEntityMapper (implementación)
    repository/  → IUsuarioRepository (extiende JpaRepository<UsuarioEntity, Long>)
    adapter/     → UsuarioJpaAdapter: implementa IUsuarioPersistencePort
```

## Estado actual del proyecto (lo que YA existe)

- `Rol.java` — enum con los 4 roles
- `Usuario.java` — modelo de dominio puro (getters manuales, sin Lombok)
- `IUsuarioServicePort.java` — puerto in con método `guardarPropietario(Usuario)`
- `IUsuarioPersistencePort.java` — puerto out con métodos `guardarUsuario(Usuario)` y `existePorCorreo(String)`
- `UsuarioUseCase.java` — valida mayoría de edad, correo único, encripta clave, asigna rol PROPIETARIO
- `BeanConfiguration.java` — registra UsuarioUseCase como bean
- `SecurityConfig.java` — Spring Security desactivado temporalmente (permite todo)
- `UsuarioRequestDto.java` — DTO de entrada con validaciones Jakarta
- `IUsuarioRequestMapper.java` — mapper DTO → dominio
- `UsuarioRestController.java` — `POST /usuarios/propietario` → HTTP 201 sin body
- `UsuarioEntity.java` — entidad JPA con Lombok, tabla "usuarios"
- `IUsuarioEntityMapper.java` + `UsuarioEntityMapper.java` — dominio ↔ entidad
- `IUsuarioRepository.java` — extiende JpaRepository<UsuarioEntity, Long>
- `UsuarioJpaAdapter.java` — implementa IUsuarioPersistencePort

**Base de datos:** PostgreSQL en `localhost:5432/ms_usuario_db` (usuario: `usuario`, password: `password`)

## Tarea pendiente anterior: exponer el rol de un usuario ✅ COMPLETA

`GET /usuarios/{id}/rol` → `{ "rol": "PROPIETARIO" }` — ya implementado y funcionando.

---

## HU-5 — Autenticación JWT ✅ COMPLETA (incluyendo logout)

### Contexto del sistema
- `ms-usuario` corre en `localhost:8080`
- `ms-plazoleta` corre en `localhost:8081`
- Roles existentes: `ADMINISTRADOR`, `PROPIETARIO`, `EMPLEADO`, `CLIENTE`
- El **ADMINISTRADOR** se inserta directamente en BD con contraseña BCrypt (no hay endpoint para crearlo)

### Decisiones de arquitectura
- JWT incluye claims: `id`, `correo`, `rol`
- `ms-plazoleta` valida el JWT **sin llamar a ms-usuario** — el token ya trae el rol
- Contraseñas con BCrypt
- Logout usa blacklist en memoria (`ConcurrentHashMap`) — se pierde al reiniciar (Redis para producción)

### Endpoints implementados
- `POST /auth/login` → público → devuelve `{ "token": "eyJ..." }`
- `POST /auth/logout` → requiere token → invalida en blacklist → 200 OK
- `POST /usuarios/propietario` → solo `ADMINISTRADOR`
- `GET /usuarios/{id}/rol` → público

### Pendiente en ms-usuario
- `POST /usuarios/empleado` → solo `PROPIETARIO` *(aún no existe)*

### Pendiente en ms-plazoleta
- `POST /restaurantes` → solo `ADMINISTRADOR`
- `POST /restaurantes/{id}/platos` → solo `PROPIETARIO`
- `PATCH /platos/{id}` → solo `PROPIETARIO`

## Reglas del proyecto

- El **dominio** no tiene dependencias externas: sin Lombok, sin Spring, sin JPA.
- Los **casos de uso** no tienen `@Service` — se registran como beans en `BeanConfiguration`.
- Los **mappers de request** son `@Component` concretos (no interfaces).
- Los **mappers de entidad** son interfaz + implementación `@Component`.
- Validaciones de **formato** van en el DTO; validaciones de **negocio** van en el caso de uso.
- Las **excepciones de dominio** van en `domain/exception/` y no extienden clases de Spring.

## Comportamiento esperado de Claude

El usuario está **practicando y aprendiendo**. Crear archivos de **uno en uno**, explicando qué hace cada uno y por qué existe, luego esperar confirmación antes de continuar con el siguiente.