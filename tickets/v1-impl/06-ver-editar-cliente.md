# 06 — Ver y editar cliente y operación

**What to build:** Vista de detalle de un cliente con sus operaciones y bienes embargados. Edición funcional de contacto y datos básicos.

**Blocked by:** 05

**Status:** ready-for-agent

- [ ] Página `/clientes/{id}` muestra datos del cliente
- [ ] Lista de operaciones del cliente con bien embargado si existe
- [ ] Edición de contacto: teléfono, email, dirección
- [ ] Edición de estado, etapa, notas
- [ ] Cada edición genera AuditoriaEvento (CLIENTE_UPDATE)
- [ ] Link a la vista de operación individual

---

## Notes

Ver SPEC.md sección 4.4.
El edit es de Cliente — la edición de Operacion está en ticket 07.
