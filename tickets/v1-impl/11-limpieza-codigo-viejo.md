# 11 — Limpieza del código viejo

**What to build:** Remover o marcar como deprecated el código viejo que fue reemplazado por el nuevo modelo. Código Thymeleaf viejo, endpoints REST viejos, entidades viejas (Cliente old, Expediente, ExpedienteCliente).

**Blocked by:** 05, 06, 07, 08, 09, 10 (todo el código nuevo debe estar funcionando antes de limpiar)

**Status:** ready-for-agent

- [ ] Evaluar si las tablas/entities viejas necesitan migración de datos antes de borrar
- [ ] Eliminar o mover a paquete `legacy/` las entities viejas: Cliente (old), Expediente, ExpedienteCliente, GestionProcesal (old), BienEmbargado (old)
- [ ] Eliminar código Thymeleaf viejo que referencia las entities viejas
- [ ] Eliminar endpoints REST/Controller viejos que ya fueron reemplazados
- [ ] Eliminar servicios y repositorios viejos (CarteraService old, etc.)
- [ ] `mvn compile` sigue pasando después de la limpieza
- [ ] `mvn test` pasa (o se actualizan los tests que referencian código viejo)

---

## Notes

Este es el paso de **contracción** del patrón expand-contract.
Si hay datos en las tablas viejas que necesitan preservarse, hacer migración primero.
No borrar el código de auditoría vieja hasta que la nueva esté verificada.
