# 02 — Nuevas entidades del modelo

**What to build:** Nuevas entidades JPA para el modelo objetivo: Cliente, Operacion, BienEmbargado, Agencia, Empresa, AuditoriaEvento — compilando y con repositorios básicos con las queries нужные.

**Blocked by:** 01 (fix de compilación)

**Status:** done

**Resolved:** 2026-08-14 (entidades ya existen en código — verificadas: Cliente, Operacion, BienEmbargado, Agencia, Empresa, AuditoriaEvento con repositorios)

- [ ] Entidad `Cliente` en nuevo paquete: id, dni (único), nombre_completo, telefono, telefono2, telefono3, direccion, email, activo, created_at, updated_at, deleted_at
- [ ] Entidad `Operacion`: id, cliente_id, empresa_id, agencia_id, cuenta, numero_operacion (unique index juntos), monto_capital, monto_total, dias_mora, moneda, tipo_credito, situacion, estado, etapa, observacion, rango, analista, analista_senior, numero_expediente, tipo_proceso, tipo_juzgado, distrito_judicial, numero_juzgado, activo, created_at, updated_at
- [ ] Entidad `BienEmbargado`: id, operacion_id, tipo_mc, fecha_inscripcion_rrpp, numero_ficha_registral, direccion_inmueble, distrito, provincia, departamento, valor_tasacion, titular_predio
- [ ] Entidad `Agencia`: id, empresa_id, nombre, region, created_at, updated_at
- [ ] Entidad `Empresa`: id, nombre, tipo, ruc, created_at, updated_at
- [ ] Entidad `AuditoriaEvento`: id, usuario, tipo, objeto_tipo, objeto_id, payload (JSON), created_at
- [ ] Repositorios con las queries necesarias (buscar por DNI, upsert por cuenta+numero_operacion, etc.)
- [ ] `mvn compile` pasa con las nuevas entidades

---

## Notes

- Las entidades nuevas viven en nuevo paquete (ej: `dominio.modelo.`) para separarlas del código viejo.
- El código viejo (Cliente old, Expediente) sigue existiendo — esto es expand, no replace.
- Ver SPEC.md sección 2 para el modelo completo.
