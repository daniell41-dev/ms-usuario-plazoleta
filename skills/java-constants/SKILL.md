---
name: java-constants
description: >
  Conventions for replacing magic numbers and inline regex patterns with named constants in Java.
  Trigger: When writing or reviewing Java code that contains numeric literals in business logic or regex strings in annotations/validations.
license: Apache-2.0
metadata:
  author: gentleman-programming
  version: "1.0"
---

## When to Use

- Any numeric literal used in a business rule (age, limits, sizes)
- Any regex string used in `@Pattern`, `Pattern.compile()`, or similar
- Any `@Size(max = N)` where N carries business meaning

## Critical Patterns

### Magic numbers in business logic

NEVER leave raw numbers in conditions or calculations.
Define a `private static final` constant in the same class if used only there,
or in a dedicated `*Constants.java` class if shared across multiple classes.

```java
// WRONG
if (edad < 18) { ... }

// RIGHT
private static final int EDAD_MINIMA_PROPIETARIO = 18;
if (edad < EDAD_MINIMA_PROPIETARIO) { ... }
```

### Regex patterns in annotations

Jakarta `@Pattern` and similar annotations require compile-time constants.
Create a `public final class` with `public static final String` fields.
Place it in the same package as the DTO that uses it.

```java
// ValidationPatterns.java
public final class ValidationPatterns {
    private ValidationPatterns() {}
    public static final String SOLO_DIGITOS       = "\\d+";
    public static final String FORMATO_CELULAR    = "^\\+?\\d{7,13}$";
}

// UsuarioRequestDto.java
@Pattern(regexp = ValidationPatterns.SOLO_DIGITOS, message = "...")
private String documentoDeIdentidad;

@Pattern(regexp = ValidationPatterns.FORMATO_CELULAR, message = "...")
private String celular;
```

### Size limits with business meaning

```java
// WRONG
@Size(max = 13, message = "...")

// RIGHT — in ValidationPatterns or the DTO itself
private static final int LONGITUD_MAXIMA_CELULAR = 13;

@Size(max = UsuarioRequestDto.LONGITUD_MAXIMA_CELULAR, message = "...")
```

## Decision Table

| Where is the constant used?        | Where to define it                        |
|------------------------------------|-------------------------------------------|
| Only in one use case / class       | `private static final` in that class      |
| Only in DTO annotations            | `public static final` in `ValidationPatterns` in same package |
| Shared across multiple classes     | Dedicated `*Constants.java` class         |
| Domain business rule               | `private static final` in the use case   |

## Resources

- See the existing `UsuarioUseCase.java` and `UsuarioRequestDto.java` for applied examples.
