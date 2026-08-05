# 05 — Bandeja de clientes (nuevo modelo)

**What to build:** Página web con la lista de clientes del nuevo modelo, búsqueda y filtros. Muestra DNI, nombre, empresa(s), estado, etapa, monto total, agencias.

**Blocked by:** 04

**Status:** ready-for-agent

- [ ] Endpoint `/clientes` con lista paginada (Thymeleaf)
- [ ] Búsqueda por DNI (exacto) y nombre (parcial, case-insensitive)
- [ ] Filtros: empresa, agencia, estado, etapa, rango de mora (dias_mora), rango de monto
- [ ] Tabla muestra: DNI, nombre, empresa(s), estado, etapa, monto_total, agencia(s)
- [ ] Link a la vista de detalle de cada cliente
- [ ] Paginación funcional

---

## Notes

Ver SPEC.md sección 4.3 para los criterios de búsqueda y filtros.
Esta bandeja es del nuevo modelo — no depende de Expediente.
