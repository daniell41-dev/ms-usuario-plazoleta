# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
./gradlew build

# Run
./gradlew bootRun

# Test (all)
./gradlew test

# Run a single test class
./gradlew test --tests "ms_usuario.plazoleta.ms_usuario.MsUsuarioApplicationTests"

# Clean
./gradlew clean
```

## Architecture

This is **ms-usuario**, a Spring Boot 4.0 microservice (Java 21) that handles user management in a "plazoleta" system. It is in early development — the scaffold exists but controllers, services, repositories, and domain models still need to be built.

**Stack:**
- Spring Web MVC — REST endpoints
- Spring Security — authentication/authorization
- Lombok — boilerplate reduction (`@Getter`, `@Builder`, etc.)
- Docker Compose integration (`compose.yaml`) — define dev services (DB, etc.) there

**Base package:** `ms_usuario.plazoleta.ms_usuario`

**Main class:** `src/main/java/ms_usuario/plazoleta/ms_usuario/MsUsuarioApplication.java`

**Configuration:** `src/main/resources/application.properties` (currently minimal — app name only)

**Tests:** `src/test/java/ms_usuario/plazoleta/ms_usuario/` — uses `@SpringBootTest` + Spring Security test support

## Notes

- `compose.yaml` is currently empty — `spring-boot-docker-compose` is on the classpath, so `bootRun` will fail until at least one service is defined there (or the dependency is removed from `build.gradle`)
- The base package uses underscores (`ms_usuario.plazoleta.ms_usuario`) because the original name (`ms-usuario plazoleta.ms-usuario`) contained invalid Java identifier characters
- This microservice is part of a broader "plazoleta" platform (other microservices likely exist separately)
- Test layer uses `spring-boot-starter-webmvc-test` (MockMvc) and `spring-boot-starter-security-test` (`@WithMockUser`, etc.)