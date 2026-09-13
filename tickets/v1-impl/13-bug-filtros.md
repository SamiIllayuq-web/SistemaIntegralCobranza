---
id: 13
titulo: Bug en filtros — investigar y corregir
status: pending
prioridad: alta
tags: [bug, filtros]
created: 2026-09-13
---

## Problema

Giordan reporta que hay un bug en los filtros de alguna vista (por confirmar cuál). Escribir "limpieza" reinicia esta sesión y amanhã se investigará.

## Plan de ataque

1. **Identificar** en qué vista exacta falla y cómo se manifiesta
2. **Reproducir** el bug paso a paso
3. **Diagnosticar** causa raíz (parámetro faltante en query, Thymeleaf mal linkado, etc.)
4. **Corregir**
5. **Verificar** con compile + prueba manual

## Hipótesis a verificar

- ¿Filtros de `/clientes` no propagan correctamente al servidor?
- ¿Filtros de `/expedientes` responden bien después de los cambios de今天?
- ¿Algún `colspan` desincronizado tras quitar columnas?

## Archivos relevantes (según último sprint)

- `ClienteController.java` — endpoint `/clientes`
- `CarteraController.java` — endpoint `/cartera/expedientes`
- Templates `cliente/lista.html`, `cartera/expedientes.html`
