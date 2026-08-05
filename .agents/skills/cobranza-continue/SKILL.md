---
name: cobranza-continue
description: Continuar el proyecto de cobranza con el workflow de Matt Pocock.
---

# Cobranza — Continue

Workflow de Matt Pocock para continuar el proyecto.

## Secuencia

1. **Setup** → `setup-matt-pocock-skills` (skills en `.agents/skills/`)
2. **Spec** → escribir/actualizar `SPEC.md` con modelo de datos + funcionalidades
3. **Grilling** → skill `grilling` en `.agents/skills/grilling/` (P1-P20, sin timer, una pregunta a la vez)
4. **To-tickets** → skill `to-tickets` en `.agents/skills/to-tickets/` → publish en `tickets/v1-impl/`

## Proyecto

- **Path:** `/mnt/d/dev/gato/SistemaIntegralCobranza`
- **Skills del proyecto:** `.agents/skills/` (skills de Matt local al proyecto)
- **Domain docs:** `docs/domain/` (README.md + glossary.md + SPEC.md)
- **Tickets:** `tickets/` (KANBAN.md + issues por feature)
- **Stack:** Java 21 + Spring Boot 3 + Thymeleaf + PostgreSQL (Supabase)

## Estructura de tickets (v1)

```
tickets/v1-impl/
  01-fix-compilacion.md      → Ninguno
  02-nuevas-entidades.md     → 01
  03-perfil-import.md        → 02
  04-importacion.md          → 02+03
  05-bandeja-clientes.md     → 04
  06-ver-editar-cliente.md   → 05
  07-ver-operacion.md        → 06
  08-exportacion.md          → 07
  09-dashboard.md            → 04
  10-auditoria.md            → 04
  11-limpieza-viejo.md       → 05+06+07+08+09+10
```

## Decisiones ya tomadas (grilling P1-P20)

1. Agencia = atributo de Empresa (no entidad independiente)
2. Datos de contacto en Cliente
3. Reimport = upsert por (cuenta, numero_operacion)
4. D y E separados como cuenta y numero_operacion
5. Import unificado: dropdown empresa + tipo
6. Dashboard CON métricas
7. estadoGestion: lista sugerida + texto libre
8. Export filtra por empresa + agencia
9. Dropdown empresa + tipo para detectar import
10. Modelo: Cliente → Operacion → BienEmbargado
11. Cartera simple: sin Expediente (null)
12. Clave Operacion: (cuenta, numero_operacion)
13. Auditoría: Import + Export + toda edición manual
14. Reimport: gestiones se acumulan (no se reemplazan)
15. Perfiles: JSON en resources/
16. Gestión no procesal NO existe en v1
17. Bienes embargados en Excel, asociados a Operacion
18. Abogado se actualiza directo (sin historial)
19. Moneda: solo Soles en v1
20. Mismo DNI = mismo Cliente cross-empresa

## Modelo de datos objetivo

```
Cliente        → id, dni(único), nombreCompleto, telefono/telefono2/telefono3, direccion, email, activo, softDelete
Operacion      → id, cliente_id, empresa_id, agencia_id, cuenta, numero_operacion, monto_capital/total, dias_mora,
                 moneda, tipo_credito, situacion, estado, etapa, observacion, rango, analista, analista_senior,
                 -- campos procesales (opcionales) --
                 numero_expediente, tipo_proceso, tipo_juzgado, distrito_judicial, numero_juzgado, activo
BienEmbargado  → id, operacion_id, tipo_mc, fecha_inscripcion_rrpp, numero_ficha_registral,
                 direccion_inmueble, distrito, provincia, departamento, valor_tasacion, titular_predio
GestionProcesal→ id, operacion_id, tipo_gestion, etapa, fecha, observacion (futuro v2)
Empresa        → id, nombre, tipo, ruc
Agencia        → id, empresa_id, nombre, region
AuditoriaEvento→ id, usuario, tipo, objeto_tipo, objeto_id, payload(JSON), created_at
```

## Excel real inspeccionado

`plantillas/05 - MAYO MC.xlsx` — 26 columnas en Hoja2.
Mapeo completo en `SPEC.md` sección 3.
Perfil de import: `resources/perfiles-import/caja-arequipa-cartera.json` (ticket 03).

## Para retomar una sesión

1. Leer `SPEC.md` para contexto actual
2. Leer `tickets/v1-impl/` para estado de los tickets
3. Leer `KANBAN.md` para el board
4. Si hay que hacer grilling: skill `.agents/skills/grilling/` — sin timer, sin clarify
