# Agent Skills — ms-usuario plazoleta

Project-specific skills loaded by AI agents when working on this codebase.

## Skills Registry

| Skill | Description | File |
|-------|-------------|------|
| `java-constants` | Magic numbers and inline regex → named constants | [SKILL.md](skills/java-constants/SKILL.md) |
| `java-naming` | No single-letter variables, descriptive names everywhere | [SKILL.md](skills/java-naming/SKILL.md) |
| `java-hexagonal` | No framework imports in domain/ or application/ layers | [SKILL.md](skills/java-hexagonal/SKILL.md) |
| `java-testing` | JUnit 5 + Mockito + JaCoCo: patrones por tipo de clase, setup y cobertura ≥96% | [SKILL.md](skills/java-testing/SKILL.md) |

## When skills apply

| Working on...                                  | Load these skills                          |
|------------------------------------------------|--------------------------------------------|
| Any class in `domain/` or `application/`       | `java-hexagonal`, `java-naming`, `java-constants` |
| DTO validation (`@Pattern`, `@Size`, etc.)     | `java-constants`, `java-naming`            |
| Use cases (business logic)                     | `java-hexagonal`, `java-constants`, `java-naming` |
| Infrastructure adapters                        | `java-naming`                              |
| Writing or reviewing tests                     | `java-testing`                             |
