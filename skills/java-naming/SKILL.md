---
name: java-naming
description: >
  Naming conventions for variables, parameters, and lambda arguments in Java.
  Trigger: When writing or reviewing Java code — especially lambda expressions, streams, or method parameters.
license: Apache-2.0
metadata:
  author: gentleman-programming
  version: "1.0"
---

## When to Use

- Writing lambda expressions (`forEach`, `ifPresent`, `map`, `filter`, etc.)
- Naming method parameters
- Naming local variables inside methods
- Reviewing any Java code for readability

## Critical Patterns

### No single-letter or abbreviated variable names

NEVER use `u`, `e`, `r`, `s`, `x` or any abbreviation that requires context to understand.
Every name must communicate what the variable holds without needing to read surrounding code.

```java
// WRONG
usuarioPersistencePort.buscarPorCorreo(usuario.getCorreo())
    .ifPresent(u -> { throw new UsuarioYaExisteException(); });

// RIGHT
usuarioPersistencePort.buscarPorCorreo(usuario.getCorreo())
    .ifPresent(usuarioEncontrado -> { throw new UsuarioYaExisteException(); });
```

### Lambda argument naming rules

| Context                  | Bad name | Good name              |
|--------------------------|----------|------------------------|
| `Optional.ifPresent`     | `u`      | `usuarioEncontrado`    |
| `List.forEach` on pedidos| `p`      | `pedido`               |
| `stream().filter`        | `x`      | `empleadoActivo`       |
| Exception in catch       | `e`      | `excepcion`, `error`   |

### Method parameters

Parameters must describe their role, not their type.

```java
// WRONG
public void procesar(Usuario u, String s) { ... }

// RIGHT
public void procesar(Usuario usuarioNuevo, String correoBuscado) { ... }
```

### Local variables

Use the most specific name possible. If the variable holds a filtered or transformed value, the name should reflect that.

```java
// WRONG
int resultado = Period.between(fechaNacimiento, LocalDate.now()).getYears();

// RIGHT
int edadCalculada = Period.between(fechaNacimiento, LocalDate.now()).getYears();
```

## Resources

- See `UsuarioUseCase.java` for the corrected `usuarioEncontrado` lambda pattern.
