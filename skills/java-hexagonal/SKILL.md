---
name: java-hexagonal
description: >
  Hexagonal Architecture (Ports & Adapters) enforcement rules for this Spring Boot microservice.
  Trigger: When writing or reviewing any Java class in domain/ or application/ layers, or when injecting framework-specific dependencies.
license: Apache-2.0
metadata:
  author: gentleman-programming
  version: "1.0"
---

## When to Use

- Writing or reviewing classes inside `domain/` or `application/`
- Injecting any dependency into a use case
- Any time a Spring, JPA, Jakarta, or third-party import appears outside `infrastructure/`

## Critical Patterns

### Layer import rules

| Layer                   | Allowed imports                                      | Forbidden                          |
|-------------------------|------------------------------------------------------|------------------------------------|
| `domain/model`          | Java standard library only                           | Spring, JPA, Lombok, Jakarta       |
| `domain/ports`          | Java standard library + own domain models            | Spring, JPA, Lombok, Jakarta       |
| `application/usecase`   | Java stdlib + own domain ports/models                | Spring, JPA, Jakarta, **Spring Security** |
| `infrastructure/`       | Anything — Spring, JPA, Lombok, Security, etc.       | —                                  |

### PasswordEncoder / encryption — the correct pattern

`org.springframework.security.crypto.password.PasswordEncoder` is a Spring Security type.
It MUST NOT appear as an import in any `application/` class.

**Wrong** — couples the use case to Spring:
```java
// application/usecase/UsuarioUseCase.java
import org.springframework.security.crypto.password.PasswordEncoder; // ← VIOLATION
```

**Right** — define your own port in `domain/ports/out/`:
```java
// domain/ports/out/IClaveCodificadorPort.java
public interface IClaveCodificadorPort {
    String codificar(String claveTextoPlano);
}
```

Implement it in infrastructure:
```java
// infrastructure/output/security/BCryptClaveCodificador.java
@Component
public class BCryptClaveCodificador implements IClaveCodificadorPort {
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String codificar(String claveTextoPlano) {
        return encoder.encode(claveTextoPlano);
    }
}
```

Wire it in `BeanConfiguration`:
```java
@Bean
public IUsuarioServicePort usuarioServicePort(
        IUsuarioPersistencePort persistencePort,
        IClaveCodificadorPort claveCodificadorPort) {
    return new UsuarioUseCase(persistencePort, claveCodificadorPort);
}
```

The use case only knows `IClaveCodificadorPort` — zero Spring imports.

### The general rule for any framework dependency in a use case

If a use case needs something provided by a framework (encoding, hashing, HTTP clients, file storage):
1. Define a port interface in `domain/ports/out/` with a meaningful name in Spanish/business language
2. Implement it as an adapter in `infrastructure/`
3. Wire it in `BeanConfiguration`

NEVER import framework classes directly into `application/` or `domain/`.

### Quick violation check

Before committing any file in `application/` or `domain/`, scan imports for:
- `org.springframework.*`
- `jakarta.persistence.*`
- `lombok.*`
- Any third-party library

If found → extract a port.

## Resources

- `BeanConfiguration.java` — where all wiring happens
- `IUsuarioPersistencePort.java` — reference example of a correct output port
