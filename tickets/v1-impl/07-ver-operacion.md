# 07 — Ver operación

**What to build:** Vista independiente de una operación con todos sus datos y bien embargado. Edición de los campos editables de la operación.

**Blocked by:** 06

**Status:** ready-for-agent

- [ ] Página `/operaciones/{id}` con todos los campos de la operación
- [ ] Ver bien embargado si existe (todos los 11 campos)
- [ ] Edición de: estado, etapa, analista, abogado (texto), notas
- [ ] Cada edición genera AuditoriaEvento (OPERACION_UPDATE)
- [ ] Link de vuelta al cliente

---

## Notes

Ver SPEC.md sección 4.5.
"ABOGADO" es texto libre en v1 — no es un Usuario del sistema (P18 del grilling).
