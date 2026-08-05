# Kanban — SistemaIntegralCobranza

## Checkpoint
- `tickets/CHECKPOINT-2026-07-26.md` — estado completo, mapa de columnas Excel

## Done ✅
- Fix typo OperacionRepository (maxMora→maxMonto)
- Endpoint /operaciones/editar/{id} + /operaciones/guardar (OperacionController reescrito)
- OperacionService.obtenerEntityPorId + actualizar con observacionActos
- OperacionMapper.toFormDTO + toEntityFromForm con comentario
- OperacionFormDTO: observacionActos + comentario
- Operacion.java: campo comentario (TEXT)
- CarteraService: detalleGarantia, observacionActos (col 52=AZ), comentario (col 53=BA)
- Perfil JSON: observacionActos (52), comentario (53), detalleBien (25)
- operacion/formulario.html: campo comentario
- cliente/formulario.html: limpiado (solo campos ClienteFormDTO)
- **Bienes Embargados editables en formulario.html** (sección completa con tabla editable)
  - OperacionFormDTO: List<BienEmbargadoDTO> bienesEmbargados
  - OperacionMapper.toFormDTO: mapea bienes
  - OperacionService.actualizar(): syncea bienes con clear() + orphanRemoval
  - OperacionService: inyectado BienEmbargadoRepository
  - Template: tabla con inputs para 7 campos, + agregar, × eliminar (JS)
- **Quitado filtro/columna Agencia de cartera/registros.html**

## In Progress
- _(vacío)_

## Blocked
- _(vacío)_

## Needs Triage
- _(vacío)_
