# Glosario del dominio

## Cliente

Persona natural deudora. Se identifica por DNI único. Puede tener una o más operaciones. Los datos de contacto (teléfono, email, dirección) van en Cliente, no en Operacion.

## Operacion

Una deuda específica de un cliente. Se identifica por el par `(cuenta, numero_operacion)` — la clave única es el par, no una sola columna. Una operacion pertenece a una empresa y una agencia. Puede tener cero o más bienes embargados y cero o más gestiones procesales.

## BienEmbargado

Inmueble embargado como medida cautelar asociado a una operación. Se crea durante la importación de cartera cuando el Excel tiene partida registral (columna O del Excel de Caja Arequipa). Campos: tipo de MC, fecha de inscripción en RRPP, número de partida registral, dirección del inmueble, distrito, provincia, departamento, valor de tasación, titular del predio.

## Agencia

Sucursal del estudio jurídico o de la empresa financiera. Nombre examples: "Oxapampa", "Chanchamayo", "Pichanaqui". Cada agencia pertenece a una empresa.

## Empresa

Entidad financiera (banco, financiera, retail) que encarga la cobranza al estudio. Examples: "Caja Arequipa".

## Cartera

Container de importación. Agrupa las operaciones importadas en un lote con nombre y fecha de recepción. La cartera no tiene peso en la lógica de negocio — es solo un identificador histórico del import.

## Importación

Proceso de subir un Excel de cartera, parsear las filas, y hacer upsert de operaciones y clientes. Se hace por empresa. El matching es por `(cuenta, numero_operacion)`.

## Exportación

Proceso de generar el Excel que la entidad financiera espera recibir para reportar la gestión. Se filtra por empresa + agencia.

## GestionProcesal

Fecha o evento procesal de una operación (presentación, admisión, audiencia, remate, etc.). Se acumula en reimports — nunca se borra. Solo aplica a operaciones con avance procesal (no cartera simple).

## AuditoriaEvento

Registro de auditoría a nivel aplicación. Se genera en: importación, exportación, y toda edición manual (crear/editar/borrar cliente, operación). Cada evento incluye: tipo, usuario, entidad afectada, payload JSON con datos antes/después.

## Medida Cautelar (MC)

Embargo sobre un bien inmueble del deudor. En el Excel de Caja Arequipa viene en las columnas M a Z. El tipo de MC puede ser "PROPIEDAD", "VEHÍCULO", etc.

## Rango

Clasificación interna de la operación según días de mora o monto. Viene como texto libre en el Excel (columna L).

## Situacion

Estado de la deuda según la entidad financiera. Valores: JUDICIAL, EXTRAJUDICIAL, PRESCRITA, PAGADA.

## Estado

Estado actual de la operación en el sistema del estudio. Valores: VIGENTE, VENCIDA.

## Etapa

Etapa procesal de la cobranza. Valores: EXTRAJUDICIAL, JUDICIAL.

---

## Términos deprecated / reemplazados

- **Expediente** — ya no es una entidad separada. Los campos procesales viven en `Operacion`. Un proceso judicial existe como datos en la operación, no como tabla independiente.
- **ExpedienteCliente** — link histórico entre expediente y cliente. Ya no aplica con el nuevo modelo.
- **Cartera como entidad de negocio** — era un container de imports en el modelo viejo. En el nuevo modelo la cartera es solo un concepto histórico.
