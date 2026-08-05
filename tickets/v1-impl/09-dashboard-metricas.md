# 09 — Dashboard con métricas

**What to build:** Pantalla inicial con totales de cartera, operaciones por estado/etapa, y alertas. Accesos directos a importar / bandeja / exportar.

**Blocked by:** 04 (necesita datos para las métricas)

**Status:** ready-for-agent

- [ ] Totales: clientes únicos, operaciones totales, monto total (suma de monto_total)
- [ ] Desglose por estado (VIGENTE / VENCIDA): count y monto
- [ ] Desglose por etapa (EXTRAJUDICIAL / JUDICIAL): count y monto
- [ ] Alertas: operaciones sin bien embargado, operaciones sin número de expediente
- [ ] Accesos directos: botón "Importar", botón "Bandeja", botón "Exportar"
- [ ] Los datos se cargan al entrar (no requiere acción del usuario)

---

## Notes

Ver SPEC.md sección 4.1.
Las métricas son aggregates directos de la BD — no hay tablas de métricas precalculadas en v1.
