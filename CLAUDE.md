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

## Tarea actual: HU-5 — Autenticación JWT

### Contexto del sistema
- `ms-usuario` corre en `localhost:8080`
- `ms-plazoleta` corre en `localhost:8081`
- Los roles existentes son: `ADMINISTRADOR`, `PROPIETARIO`, `EMPLEADO`, `CLIENTE`
- El usuario **ADMINISTRADOR** se inserta directamente en BD con contraseña BCrypt (no hay endpoint para crearlo)

### Decisiones de arquitectura tomadas
- El login (`POST /auth/login`) vive en `ms-usuario` — él tiene correo, clave y rol
- Al hacer login se genera un **JWT token** que incluye: `id`, `correo`, `rol`
- `ms-plazoleta` valida el JWT en cada request **sin llamar a ms-usuario** — el token ya trae el rol
- Contraseñas almacenadas con BCrypt (ya implementado en `UsuarioUseCase`)
- No se implementa recuperación de contraseña en esta versión

### Endpoints a proteger (criterios de aceptación HU-5)
**En ms-usuario:**
- `POST /usuarios/propietario` → solo `ADMINISTRADOR`
- `POST /usuarios/empleado` → solo `PROPIETARIO` *(aún no existe este endpoint)*

**En ms-plazoleta:**
- `POST /restaurantes` → solo `ADMINISTRADOR`
- `POST /restaurantes/{id}/platos` → solo `PROPIETARIO` (y dueño del restaurante)
- `PATCH /platos/{id}` → solo `PROPIETARIO` (y dueño del restaurante)

### Dependencia JWT a agregar en build.gradle
```groovy
implementation 'io.jsonwebtoken:jjwt-api:0.11.5'
runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.11.5'
runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.11.5'
```

### Archivos a crear/modificar en ms-usuario (en orden):

1. **`build.gradle`** — agregar dependencias JWT (ver arriba)

2. **`application.properties`** — agregar:
   ```properties
   jwt.secret=clave-secreta-muy-larga-para-firmar-el-token
   jwt.expiration=86400000
   ```

3. **`JwtTokenProvider.java`** — en `infrastructure/config/security/`:
   - Genera el token JWT con `id`, `correo`, `rol` como claims
   - Valida un token JWT
   - Extrae el correo del token

4. **`LoginRequestDto.java`** — en `infrastructure/input/rest/dto/`:
   - Campos: `correo` (@Email @NotBlank), `clave` (@NotBlank)

5. **`LoginResponseDto.java`** — en `infrastructure/input/rest/dto/`:
   - Campo: `String token`

6. **`IUsuarioServicePort.java`** — agregar método:
   ```java
   String login(String correo, String clave);
   ```

7. **`IUsuarioPersistencePort.java`** — ya tiene `buscarPorCorreo` — verificar que devuelve `Optional<Usuario>`

8. **`UsuarioUseCase.java`** — implementar `login(String correo, String clave)`:
   - Buscar usuario por correo → si no existe, excepción
   - Validar clave con `passwordEncoder.matches(clave, usuario.getClave())`
   - Si clave incorrecta → excepción `CredencialesInvalidasException`
   - Devolver el token generado por `JwtTokenProvider`

9. **`CredencialesInvalidasException.java`** — en `domain/exception/`

10. **`AuthRestController.java`** — en `infrastructure/input/rest/`:
    - `POST /auth/login` con `@RequestBody LoginRequestDto` → devuelve `LoginResponseDto` con el token

11. **`JwtAuthenticationFilter.java`** — en `infrastructure/config/security/`:
    - Filtro que intercepta cada request
    - Extrae el token del header `Authorization: Bearer <token>`
    - Valida el token y setea el contexto de seguridad de Spring

12. **`SecurityConfig.java`** — reemplazar la configuración temporal:
    - Permitir sin autenticación: `POST /auth/login`, `GET /usuarios/{id}/rol`
    - Proteger `POST /usuarios/propietario` → solo `ADMINISTRADOR`
    - Agregar el filtro JWT a la cadena de seguridad

## Reglas del proyecto

- El **dominio** no tiene dependencias externas: sin Lombok, sin Spring, sin JPA.
- Los **casos de uso** no tienen `@Service` — se registran como beans en `BeanConfiguration`.
- Los **mappers de request** son `@Component` concretos (no interfaces).
- Los **mappers de entidad** son interfaz + implementación `@Component`.
- Validaciones de **formato** van en el DTO; validaciones de **negocio** van en el caso de uso.
- Las **excepciones de dominio** van en `domain/exception/` y no extienden clases de Spring.

## Comportamiento esperado de Claude

El usuario está **practicando y aprendiendo**. Crear archivos de **uno en uno**, explicando qué hace cada uno y por qué existe, luego esperar confirmación antes de continuar con el siguiente.