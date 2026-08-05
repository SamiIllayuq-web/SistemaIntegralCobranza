# 10 — Auditoría (historial)

**What to build:** Vista de historial de auditoría con tabla paginada, filtros, y detalle de cada evento (payload JSON).

**Blocked by:** 04

**Status:** ready-for-agent

- [ ] Página `/auditoria` con tabla paginada
- [ ] Filtros: tipo de evento, rango de fechas, usuario
- [ ] Columnas visibles: fecha, usuario, tipo, objeto_tipo, objeto_id
- [ ] Click abre detalle del evento con payload JSON completo (antes/después para updates)
- [ ] Los eventos de IMPORT_OK, IMPORT_ERROR, EXPORT_OK, CLIENTE_CREATE, CLIENTE_UPDATE, CLIENTE_DELETE, OPERACION_CREATE, OPERACION_UPDATE se registran

---

## Notes

Ver SPEC.md sección 4.7.
AuditoriaEvento se creó en ticket 02 — aquí solo se consume la vista.
