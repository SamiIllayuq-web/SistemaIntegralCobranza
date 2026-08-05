# SPEC v1 — Sistema Integral de Cobranza

> Basado en: decisiones del grilling (001-grilling-disenos-decisiones.md), decisiones de diseño del PLAN.md original, e inspección del Excel real "05 - MAYO MC.xlsx".
> Status: BORRADOR — requiere revisión antes de implementar.

---

## 1. Goal

Automatizar el flujo de importación → revisión → exportación de carteras de cobranza para un estudio jurídico peruano. El estudio recibe Exceles de entidades financieras (bancos, financieras, retail) y necesita procesarlos sin trabajo manual.

---

## 2. Modelo de datos objetivo

### Entidades

```
Cliente (persona deudora)
  id
  dni (único)
  nombre_completo
  telefono / telefono2 / telefono3
  direccion
  email
  activo
  created_at / updated_at / deleted_at (soft delete)

Operacion (una deuda, clave única: cuenta + numero_operacion)
  id
  cliente_id → Cliente
  empresa_id → Empresa
  agencia_id → Agencia
  cuenta
  numero_operacion
  monto_capital
  monto_total
  dias_mora
  moneda (MONEDA DE LA CARGA — del Excel)
  tipo_credito (TIPO CREDITO — del Excel)
  situacion (JUDICIAL / EXTRAJUDICIAL / PRESCRITA / PAGADA)
  estado (VIGENTE / VENCIDA)
  etapa (EXTRAJUDICIAL / JUDICIAL)
  observacion
  rango (texto libre del Excel — columna L)
  analista (nombre del analista — columna E, texto libre)
  analista_senior (nombre del senior — columna X, texto libre)
  -- Campos procesales (opcionales en cartera simple)
  numero_expediente
  tipo_proceso
  tipo_juzgado
  distrito_judicial
  numero_juzgado
  abogado_id → Usuario
  activo
  created_at / updated_at

BienEmbargado (asociado a una Operacion)
  id
  operacion_id → Operacion
  tipo_mc (TIPO MC — columna M)
  fecha_inscripcion_rrpp (FECHA INSCRIPCION RRPP — columna N)
  numero_ficha_registral (NRO/FICHA REGISTRAL O PARTIDA — columna O)
  direccion_inmueble (DIRECCION DEL INMUEBLE EMBARGADO — columna R)
  distrito (DISTRITO DEL INMUEBLE EMBARGADO — columna S)
  provincia (PROVINCIA DEL INMUEBLE EMBARGADO — columna T)
  departamento (DEPARTAMENTO DEL INMUEBLE EMBARGADO — columna U)
  valor_tasacion (VALOR DE TASACION EN SOLES — columna Y)
  titular_predio (TITULAR DEL PREDIO — columna Z)

GestionProcesal (fechas procesales — solo desde Avance Procesal)
  id
  operacion_id → Operacion
  tipo_gestion (MC / PRINCIPAL / AUDIENCIA / EJECUCION / REMATE)
  etapa
  fecha
  observacion
  created_at

Empresa
  id
  nombre
  tipo (banco | financiera | retail | otro)
  ruc
  created_at / updated_at

Agencia
  id
  empresa_id → Empresa
  nombre ("Oxapampa", "Chanchamayo", etc. — columna D del Excel)
  region (ZONA del Excel — columna C)
  created_at / updated_at

AuditoriaEvento
  id
  usuario
  tipo (IMPORT_OK | IMPORT_ERROR | EXPORT_OK | CLIENTE_CREATE | CLIENTE_UPDATE | CLIENTE_DELETE | OPERACION_CREATE | OPERACION_UPDATE)
  objeto_tipo
  objeto_id
  payload (JSON)
  created_at
```

### Notas del modelo
- Un solo Cliente por DNI (mismo DNI en empresas distintas = mismo Cliente).
- Operacion se identifica por `(cuenta, numero_operacion)`.
- BienEmbargado es hijo de Operacion — los inmuebles embargados vienen en la cartera simple del Excel.
- GestionProcesal es hijo de Operacion — avance procesal se asocia directamente a la operación.
- Expediente YA NO es una entidad separada — los campos procesales viven en Operacion.
- Agencia pertenece a Empresa.
- "ANALISTA" y "ANALISTA SENIOR" son nombres de texto libre en v1 (no se crean como Usuarios del sistema).

---

## 3. Formato real del Excel de import

El archivo de producción "05 - MAYO MC.xlsx" (Caja Arequipa) tiene 26 columnas en Hoja2. Se aprovecha TODO:

| Columna | Header | Entidad | Campo |
|---------|--------|---------|-------|
| A | NRO | — | Ignorado (no confiable) |
| B | CUENTA Y OPERACIÓN | — | Ignorada (P4 — se usan H+I) |
| C | ZONA | Agencia | region |
| D | AGENCIA | Agencia | nombre |
| E | ANALISTA | Operacion | analista |
| F | CLIENTE | Cliente | nombre_completo |
| G | DNI | Cliente | dni |
| H | CUENTA | Operacion | cuenta |
| I | OPERACIÓN | Operacion | numero_operacion |
| J | TIPO CREDITO | Operacion | tipo_credito |
| K | ABOGADO | Operacion | abogado (texto) |
| L | RANGO | Operacion | rango |
| M | TIPO MC | BienEmbargado | tipo_mc |
| N | FECHA INSCRIPCION RRPP | BienEmbargado | fecha_inscripcion_rrpp |
| O | NRO/FICHA REGISTRAL O PARTIDA | BienEmbargado | numero_ficha_registral |
| P | MONEDA DE LA CARGA | Operacion | moneda |
| Q | MONTO DE LA CARGA | — | Ignorado (redundante con V/W) |
| R | DIRECCION DEL INMUEBLE EMBARGADO | BienEmbargado | direccion_inmueble |
| S | DISTRITO DEL INMUEBLE EMBARGADO | BienEmbargado | distrito |
| T | PROVINCIA DEL INMUEBLE EMBARGADO | BienEmbargado | provincia |
| U | DEPARTAMENTO DEL INMUEBLE EMBARGADO | BienEmbargado | departamento |
| V | CAPITAL S/ | Operacion | monto_capital |
| W | MONTO TOTAL DEUDA S/ | Operacion | monto_total |
| X | ANALISTA SENIOR | Operacion | analista_senior |
| Y | VALOR DE TASACION EN SOLES | BienEmbargado | valor_tasacion |
| Z | TITULAR DEL PREDIO | BienEmbargado | titular_predio |

**Notas:**
- BienEmbargado se crea SOLO si hay partida registral (columna O con valor).
- No todos los registros tienen inmueble embargado.
- La columna Q (MONTO DE LA CARGA) parece ser el monto de la medida cautelar, diferente al capital o total de la deuda.

---

## 4. Funcionalidades v1

### 4.1 Dashboard
- Al entrar muestra: totales de cartera, operaciones por estado/etapa, alertas (operaciones sin inmueble embargado, etc.).
- Accesos directos: importar / bandeja de clientes / exportar.

### 4.2 Importación
- Flujo único: el usuario elige empresa + tipo de import (dropdown: "Cartera Simple").
- El perfil de import se lee del JSON en `resources/perfiles-import/<empresa>.json`.
- Por cada fila del Excel:
  - Validar campos obligatorios (DNI, cuenta, operacion, monto).
  - Upsert por `(cuenta, numero_operacion)` — si existe, actualizar; si no, crear.
  - Si DNI existe → asociar a ese Cliente. Si DNI no existe → crear Cliente.
  - Upsert de Agencia (buscar por nombre, crear si no existe).
  - Si hay partida registral → crear/upsert BienEmbargado asociado a la operación.
- Resultado: X importados / Y actualizados / Z errores.
- Descargar reporte de errores (CSV).

### 4.3 Bandeja de clientes
- Lista paginada: DNI, nombre, empresa(s), estado, etapa, monto total, agencias.
- Búsqueda: por DNI (exacto) o nombre (parcial).
- Filtros: empresa, agencia, estado, etapa, rango de mora, rango de monto.

### 4.4 Ver / editar cliente
- Ver: datos del cliente + sus operaciones + inmuebles embargados.
- Editar: contacto (teléfono, email, dirección), estado, etapa, notas.
- Cada edición genera AuditoriaEvento.

### 4.5 Ver operación
- Ver: todos los datos de la operación + bien embargado si existe.
- Editar: estado, etapa, analista, abogado, notas.

### 4.6 Exportación
- Filtro: empresa + agencia (exporta todas las operaciones de esa agencia).
- Genera Excel según el perfil de export de la empresa (`resources/perfiles-export/<empresa>.json`).

### 4.7 Auditoría
- Todas las operaciones (import, export, create/edit/delete de Cliente, Operacion) generan evento.
- Vista de historial: tabla paginada con filtros.

---

## 5. Perfiles de import/export

Ubicación: `resources/perfiles-import/` y `resources/perfiles-export/`.

```json
// resources/perfiles-import/caja-arequipa-cartera.json
{
  "empresa": "Caja Arequipa",
  "tipo": "CARTERA_SIMPLE",
  "headerRow": 1,
  "sheetName": "Hoja2",
  "columns": {
    "nombre": "F",
    "dni": "G",
    "cuenta": "H",
    "numeroOperacion": "I",
    "tipoCredito": "J",
    "analista": "E",
    "abogado": "K",
    "rango": "L",
    "tipoMc": "M",
    "fechaInscripcionRrpp": "N",
    "numeroFichaRegistral": "O",
    "moneda": "P",
    "direccionInmueble": "R",
    "distritoInmueble": "S",
    "provinciaInmueble": "T",
    "departamentoInmueble": "U",
    "montoCapital": "V",
    "montoTotal": "W",
    "analistaSenior": "X",
    "valorTasacion": "Y",
    "titularPredio": "Z",
    "agencia": "D",
    "zona": "C"
  },
  "dateFormat": "dd/MM/yyyy",
  "numberFormat": "#,##0.00"
}
```

---

## 6. Scope v1

**Incluido:**
- Importación cartera simple con todas las columnas del Excel real.
- BienEmbargado (inmuebles) incluidos en la importación.
- Bandeja de clientes con búsqueda y filtros.
- Ver/editar cliente y operación.
- Exportación a Excel.
- Auditoría completa.
- Dashboard con métricas.

**Excluido (v2+):**
- Avance procesal (gestiones procesales, expediente separate).
- Módulo de gestión no procesal (llamadas, visitas, cartas).
- Autenticación multiusuario (1 usuario por ahora).
- Notificaciones.
- Dashboard BI.
- PDFs.
- APIs externas.

---

## 7. Tech stack

- Java 21 + Spring Boot 3
- Spring Data JPA + Hibernate
- Spring Security + JWT (1 usuario hardcoded en v1)
- Thymeleaf + Bootstrap 5 (server-rendered)
- PostgreSQL (Supabase Cloud)
- Apache POI para Excel
- Perfiles de import/export como JSON en resources/

---

## 8. Estructura del plan de implementación

### Fase 0: Fix de compilación
- Arreglar errores de Lombok (el proyecto no compila actualmente).

### Fase 1: Nuevo modelo de datos
- Crear/reescribir entidades: Cliente, Operacion, BienEmbargado, Agencia, Empresa, AuditoriaEvento.
- Eliminar expediente como entidad separada (los campos procesales van en Operacion).
- Crear repositorios con las queries нужные.

### Fase 2: Perfil de import Caja Arequipa
- Escribir `resources/perfiles-import/caja-arequipa-cartera.json` con el mapeo real de columnas.

### Fase 3: Importación cartera simple
- Reescribir CarteraService: upsert por (cuenta, numero_operacion).
- Integrar con perfil de import.
- Parser de BienEmbargado.

### Fase 4: Bandeja + edición
- Endpoints Thymeleaf para cliente y operación.
- Búsqueda y filtros.

### Fase 5: Exportación
- Generar Excel según perfil de export.

### Fase 6: Dashboard
- Métricas aggregate.

### Fase 7: Auditoría
- Integrar con todos los eventos.

---

## 9. Decisiones de diseño (del grilling P1-P20)

| # | Pregunta | Respuesta |
|---|----------|-----------|
| 1 | agencia como entidad | Atributo de empresa |
| 2 | Datos de contacto | En Cliente |
| 3 | Reimportar cartera | Upsert por (cuenta, numero_operacion) |
| 4 | Identificador Operacion | D y E por separado |
| 5 | Flujo de import | Unificado — empresa + tipo |
| 6 | Dashboard | Con métricas |
| 7 | estadoGestion | Lista sugerida + texto libre |
| 8 | Filtro export | Empresa + agencia |
| 9 | Detección import | Dropdown empresa + tipo |
| 10 | Modelo de datos | Cliente → Operacion → BienEmbargado |
| 11 | Cartera simple | Solo Cliente + Operacion (sin Expediente separado) |
| 12 | Clave única Operacion | (cuenta, numero_operacion) suficiente |
| 13 | Auditoría | Import + Export + toda edición manual |
| 14 | Reimport avance procesal | Gestiones se acumulan |
| 15 | Perfiles import | JSON en resources/ |
| 16 | Gestión no procesal | No existe en v1 |
| 17 | Bienes embargados | Vienen en Excel (asociados a Operacion, no Expediente) |
| 18 | Cambio de abogado | Se actualiza directo |
| 19 | Moneda | Solo Soles en v1 |
| 20 | Mismo DNI en empresas | Un solo Cliente |
