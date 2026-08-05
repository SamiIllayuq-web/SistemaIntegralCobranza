---
id: 3
title: "Arreglar errores de compilación — Lombok annotation processor"
status: done
type: afk
priority: critical
dependencies: []
created: 2026-07-24
resolved: 2026-07-25
---

# Arreglar errores de compilación — Lombok annotation processor

## Problema

El proyecto no compila. Errores en múltiples archivos que usan `@Data`, `@Builder` de Lombok:

```
UsuarioMapper.java — cannot find symbol: builder(), getUsername(), getNombreCompleto(), getRol()
ClienteMapper.java — cannot find symbol: builder(), getNombre(), getDni(), etc.
ClienteService.java — cannot find symbol: getNombre(), getDni(), etc.
UsuarioController.java — cannot find symbol: getUsername(), getNombreCompleto()
UsuarioService.java — cannot find symbol: getUsername(), getPassword(), getRol()
```

Los DTOs tienen `@Data` de Lombok, pero el annotation processor no los está generando.

## Diagnóstico

Revisar `pom.xml`:
- Lombok está como `<optional>true</optional>` en dependencies — correcto.
- Falta el annotation processor config en el maven-compiler-plugin.

## Solución probable

En el `<build><plugins>` del `pom.xml`, agregar:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.11.0</version>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>${lombok.version}</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

##验收标准

1. `mvn compile` pasa sin errores.
2. Los getters/setters de Lombok están disponibles en los archivos affected.
