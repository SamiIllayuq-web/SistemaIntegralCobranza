package com.startup.cobranza.cartera.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.startup.cobranza.agencia.entity.Agencia;
import com.startup.cobranza.agencia.repository.AgenciaRepository;
import com.startup.cobranza.cartera.dto.ImportacionDTO;
import com.startup.cobranza.cartera.entity.Importacion;
import com.startup.cobranza.cartera.exception.CarteraException;
import com.startup.cobranza.cartera.repository.ImportacionRepository;
import com.startup.cobranza.cliente.entity.Cliente;
import com.startup.cobranza.cliente.repository.ClienteRepository;
import com.startup.cobranza.operacion.entity.BienEmbargado;
import com.startup.cobranza.operacion.repository.BienEmbargadoRepository;
import com.startup.cobranza.operacion.entity.Operacion;
import com.startup.cobranza.operacion.repository.OperacionRepository;
import com.startup.cobranza.empresa.entity.Empresa;
import com.startup.cobranza.empresa.repository.EmpresaRepository;
import jakarta.annotation.PostConstruct;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CarteraService {

    private final ImportacionRepository importacionRepository;
    private final ClienteRepository clienteRepository;
    private final OperacionRepository operacionRepository;
    private final EmpresaRepository empresaRepository;
    private final AgenciaRepository agenciaRepository;
    private final BienEmbargadoRepository bienEmbargadoRepository;

    private JsonNode perfilJson;
    private DateTimeFormatter dateFormatter;
    private String perfilActual;
    private static final String PERFIL_CAJA_AREQUIPA = "perfiles-import/caja-arequipa-cartera.json";
    private static final String PERFIL_EXCEL_AVANCE = "perfiles-import/excel-avance-procesal-arequipa.json";
    private static final String PERFIL_INVENTARIO_JUNIO = "perfiles-import/inventario-junio-2026.json";

    public CarteraService(ImportacionRepository importacionRepository,
                          ClienteRepository clienteRepository,
                          OperacionRepository operacionRepository,
                          EmpresaRepository empresaRepository,
                          AgenciaRepository agenciaRepository,
                          BienEmbargadoRepository bienEmbargadoRepository) {
        this.importacionRepository = importacionRepository;
        this.clienteRepository = clienteRepository;
        this.operacionRepository = operacionRepository;
        this.empresaRepository = empresaRepository;
        this.agenciaRepository = agenciaRepository;
        this.bienEmbargadoRepository = bienEmbargadoRepository;
    }

    @PostConstruct
    public void init() {
        // Por defecto se carga el perfil caja-arequipa; se sobrescribe en importarExcel si corresponde
        cargarPerfil(PERFIL_CAJA_AREQUIPA);
    }

    private void cargarPerfil(String path) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ClassPathResource resource = new ClassPathResource(path);
            try (InputStream is = resource.getInputStream()) {
                perfilJson = mapper.readTree(is);
            }
        } catch (IOException e) {
            throw new IllegalStateException(
                "No se pudo cargar el perfil de import: " + path + " — " + e.getMessage(), e);
        }
        String dateFormat = perfilJson.has("dateFormat") ? perfilJson.get("dateFormat").asText() : "dd/MM/yyyy";
        this.dateFormatter = DateTimeFormatter.ofPattern(dateFormat);
        this.perfilActual = path;
    }

    @Transactional
    public ImportacionDTO importarExcel(MultipartFile archivo, Long empresaId, Long agenciaId, String usuario) {
        if (archivo.isEmpty()) {
            throw new CarteraException("El archivo está vacío");
        }
        String nombreOriginal = archivo.getOriginalFilename();
        if (nombreOriginal == null || !nombreOriginal.toLowerCase().endsWith(".xlsx")) {
            throw new CarteraException("Solo se permiten archivos Excel (.xlsx)");
        }

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new CarteraException("Empresa no encontrada: " + empresaId));

        int total = 0;
        int creados = 0;
        int actualizados = 0;
        int errores = 0;
        List<String> listaErrores = new ArrayList<>();

        try (InputStream is = archivo.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            // Iterar sobre las hojas reales del archivo Excel
            for (int s = 0; s < workbook.getNumberOfSheets(); s++) {
                Sheet sheet = workbook.getSheetAt(s);
                String hojaNombre = sheet.getSheetName();
                if (sheet == null) continue;

                // Determinar perfil y estado según nombre de hoja
                String perfilPath;
                String estadoCarteraDefault;

                if (hojaNombre.equalsIgnoreCase("Inventario")) {
                    perfilPath = PERFIL_INVENTARIO_JUNIO;
                    estadoCarteraDefault = "ACTIVO";
                } else if (hojaNombre.toLowerCase().contains("avance") || hojaNombre.toLowerCase().contains("procesal")) {
                    perfilPath = PERFIL_EXCEL_AVANCE;
                    estadoCarteraDefault = "ACTIVO";
                } else if (hojaNombre.toLowerCase().contains("cancelado") || hojaNombre.toLowerCase().contains("cancelada")) {
                    // Hoja 2: CARTERA C. CREDITO CANCELADO
                    perfilPath = PERFIL_CAJA_AREQUIPA;
                    estadoCarteraDefault = "CANCELADA";
                } else if (hojaNombre.toLowerCase().contains("devuelta")) {
                    // Hoja 3: CARPETAS DEVUELTAS.
                    perfilPath = PERFIL_CAJA_AREQUIPA;
                    estadoCarteraDefault = "DEVUELTA";
                } else {
                    perfilPath = PERFIL_CAJA_AREQUIPA;
                    estadoCarteraDefault = "ACTIVO";
                }

                cargarPerfil(perfilPath);
                JsonNode columns = perfilJson.get("columns");
                int headerRowIdx = perfilJson.has("headerRow") ? perfilJson.get("headerRow").asInt() : 0;
                boolean skipRowsWithoutDni = perfilJson.has("skipRowsWithoutDni") && perfilJson.get("skipRowsWithoutDni").asBoolean();

                Row headerRow = sheet.getRow(headerRowIdx);
                if (headerRow == null) {
                    listaErrores.add("Hoja '" + hojaNombre + "': no se encontró fila de encabezado en posición " + (headerRowIdx + 1));
                    continue;
                }

                // PASOS 1 y 2: Procesar filas — la deteccion de section headers
                // va ANTES de isRowEmpty para que el estado se actualice incluso
                // cuando la fila del header no tiene datos (DNI vacio).
                // Logica: ni bien se encuentra un section header, el estado cambia
                // para esa fila y todas las siguientes hasta el proximo header.
                int lastRowNum = sheet.getLastRowNum();
                for (int i = headerRowIdx + 1; i <= lastRowNum; i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;

                    // Primero: detectar si es un section header en col B (ej. CARTERA VENDIDA)
                    // — esto se hace antes de isRowEmpty para que el estado se actualice
                    // aunque la fila del header no tenga DNI ni datos.
                    Cell cellB = row.getCell(1);
                    if (cellB != null) {
                        String val = cellToString(cellB);
                        if (val != null && !val.isBlank()) {
                            String upper = val.toUpperCase().trim();
                            if (upper.startsWith("CARTERA DESASIGNADA")) {
                                estadoCarteraDefault = "DESASIGNADA";
                            } else if (upper.startsWith("CARTERA VENDIDA")) {
                                estadoCarteraDefault = "VENDIDA";
                            } else if (upper.startsWith("CARTERA CANCELADA")) {
                                estadoCarteraDefault = "CANCELADA";
                            } else if (upper.startsWith("CARTERA CANCELADO")) {
                                estadoCarteraDefault = "CANCELADA";
                            }
                        }
                    }

                    // Segundo: si no hay datos en las columnas clave, saltar.
                    // El estado ya se actualizo arriba si era un section header.
                    if (isRowEmpty(row, columns)) {
                        continue;
                    }

                    total++;
                    try {
                        ParseResult result = parseRow(row, columns, empresa, skipRowsWithoutDni, estadoCarteraDefault, i);
                        if (result.esNuevo) {
                            creados++;
                        } else {
                            actualizados++;
                        }
                    } catch (Exception e) {
                        errores++;
                        listaErrores.add("Hoja '" + hojaNombre + "' fila " + (i + 1) + ": " + e.getMessage());
                    }
                }
            }

        } catch (CarteraException e) {
            throw e;
        } catch (Exception e) {
            throw new CarteraException("Error al leer el archivo: " + e.getMessage());
        }

        Importacion importacion = Importacion.builder()
                .nombreArchivo(nombreOriginal)
                .totalRegistros(total)
                .registrosExitosos(creados + actualizados)
                .registrosFallidos(errores)
                .empresaId(empresaId)
                .agenciaId(agenciaId)
                .usuarioImporta(usuario)
                .estado(errores == 0 ? "COMPLETADO" : "COMPLETADO_CON_ERRORES")
                .errores(listaErrores.isEmpty() ? null : String.join("\n", listaErrores))
                .build();

        Importacion saved = importacionRepository.save(importacion);
        return toDTO(saved, empresa.getNombre(), null);
    }

    private record ParseResult(Cliente cliente, Operacion operacion, boolean esNuevo) {}

    private ParseResult parseRow(Row row, JsonNode columns, Empresa empresa,
                                 boolean skipRowsWithoutDni, String estadoCarteraDefault,
                                 int rowIndex) {
        // 1) Leer campos crudos del Excel según el perfil
        String dni = normalizarDni(getCellString(row, columns, "dni"));
        String nombreCompleto = getCellString(row, columns, "nombreCompleto");
        String cuenta = getCellString(row, columns, "cuenta");
        String numeroOperacion = getCellString(row, columns, "numeroOperacion");
        String tipoCredito = getCellString(row, columns, "tipoCredito");
        String analista = getCellString(row, columns, "analista");
        String analistaSenior = getCellString(row, columns, "analistaSenior");
        String moneda = getCellString(row, columns, "moneda");
        String agenciaNombre = getCellString(row, columns, "agencia");
        String estado = getCellString(row, columns, "estado");
        String etapa = getCellString(row, columns, "etapa");
        String situacion = getCellString(row, columns, "situacion");
        Integer diasMora = getCellInteger(row, columns, "diasMora");
        String observacion = getCellString(row, columns, "observacion");

        // Campos judiciales
        String numeroExpediente = getCellString(row, columns, "numeroExpediente");
        String tipoProceso = getCellString(row, columns, "tipoProceso");
        String tipoJuzgado = getCellString(row, columns, "tipoJuzgado");
        String distritoJudicial = getCellString(row, columns, "distritoJudicial");
        String numeroJuzgado = getCellString(row, columns, "numeroJuzgado");
        Boolean trans = getCellBoolean(row, columns, "trans");
        Boolean busquedaBienes = getCellBoolean(row, columns, "busquedaBienes");
        BigDecimal montoDemandado = getCellBigDecimal(row, columns, "montoDemandado");
        String escribanoLegal = getCellString(row, columns, "escribanoLegal");
        String codigoExpCautelar = getCellString(row, columns, "codigoExpCautelar");
        Boolean incidente = getCellBoolean(row, columns, "incidente");

        // 5 campos nuevos (solo hojas 2 y 3)
        LocalDate fechaDesembolso = getCellLocalDate(row, columns, "fechaDesembolso");
        BigDecimal importeDesembolso = getCellBigDecimal(row, columns, "importeDesembolso");
        String etapaProcesalTexto = getCellString(row, columns, "etapaProcesal");
        String actoPendiente = getCellString(row, columns, "actoPendiente");
        LocalDate fechaUltimoEstadoProceso = getCellLocalDate(row, columns, "fechaUltimoEstadoProceso");

        // 2) Determinar estadoCartera: el main loop ya actualiza estadoCarteraDefault
        // ni bien detecta un section header en col B. Aqui se usa directo.
        // Sin override desde OBSERVACION — el estado lo fija el section header.
        String estadoCartera = estadoCarteraDefault;

        // 3) Validar campos obligatorios
        if (dni == null || dni.isEmpty()) {
            if (skipRowsWithoutDni) {
                throw new IllegalArgumentException("DNI vacío — saltando fila");
            }
            throw new IllegalArgumentException("DNI no puede estar vacío");
        }
        if (cuenta == null || cuenta.isEmpty()) {
            throw new IllegalArgumentException("Cuenta vacía");
        }
        if (numeroOperacion == null || numeroOperacion.isEmpty()) {
            throw new IllegalArgumentException("Número de operación vacío");
        }

        // 4) Find-or-create Cliente por DNI
        Cliente cliente = clienteRepository.findByDni(dni).orElse(null);
        boolean clienteNuevo = false;
        if (cliente == null) {
            cliente = Cliente.builder()
                    .nombreCompleto(nombreCompleto)
                    .dni(dni)
                    .activo(true)
                    .build();
            cliente = clienteRepository.save(cliente);
            clienteNuevo = true;
        } else {
            if (nombreCompleto != null && !nombreCompleto.isBlank()) {
                cliente.setNombreCompleto(nombreCompleto);
            }
            clienteRepository.save(cliente);
        }

        // 5) Find-or-create Agencia por nombre + empresa
        Agencia agencia = null;
        if (agenciaNombre != null && !agenciaNombre.isBlank()) {
            agencia = agenciaRepository.findByEmpresaIdAndActivoTrue(empresa.getId()).stream()
                    .filter(a -> a.getNombre().equalsIgnoreCase(agenciaNombre.trim()))
                    .findFirst()
                    .orElse(null);
            if (agencia == null) {
                agencia = Agencia.builder()
                        .nombre(agenciaNombre.trim())
                        .empresa(empresa)
                        .activo(true)
                        .build();
                agencia = agenciaRepository.save(agencia);
            }
        }

        // 6) Upsert Operacion por (empresa_id, cuenta, numero_operacion)
        Operacion operacion = operacionRepository
                .findByEmpresaIdAndCuentaAndNumeroOperacion(empresa.getId(), cuenta.trim(), numeroOperacion.trim())
                .orElse(null);
        boolean operacionNueva = false;
        if (operacion == null) {
            operacion = Operacion.builder()
                    .cliente(cliente)
                    .empresa(empresa)
                    .agencia(agencia)
                    .cuenta(cuenta.trim())
                    .numeroOperacion(numeroOperacion.trim())
                    .tipoCredito(tipoCredito)
                    .analista(analista)
                    .analistaSenior(analistaSenior)
                    .moneda(moneda != null ? moneda : "PEN")
                    .montoCapital(getCellBigDecimal(row, columns, "montoCapital"))
                    .montoTotal(getCellBigDecimal(row, columns, "montoTotal"))
                    .diasMora(diasMora)
                    .estado(estado)
                    .etapa(etapa)
                    .situacion(situacion)
                    .numeroExpediente(numeroExpediente)
                    .tipoProceso(tipoProceso)
                    .tipoJuzgado(tipoJuzgado)
                    .distritoJudicial(distritoJudicial)
                    .numeroJuzgado(numeroJuzgado)
                    .trans(trans)
                    .busquedaBienes(busquedaBienes)
                    .montoDemandado(montoDemandado)
                    .escribanoLegal(escribanoLegal)
                    .codigoExpCautelar(codigoExpCautelar)
                    .incidente(incidente)
                    .fechaPresentacion(getCellLocalDate(row, columns, "fechaPresentacion"))
                    .fechaInadmisiblePrincipal(getCellLocalDate(row, columns, "fechaInadmisiblePrincipal"))
                    .fechaAdmisionPrincipal(getCellLocalDate(row, columns, "fechaAdmisionPrincipal"))
                    .fechaAudienciaUnica(getCellLocalDate(row, columns, "fechaAudienciaUnica"))
                    .fechaAutoFinal(getCellLocalDate(row, columns, "fechaAutoFinal"))
                    .fechaConsentimiento(getCellLocalDate(row, columns, "fechaConsentimiento"))
                    .fechaEjecutoriada(getCellLocalDate(row, columns, "fechaEjecutoriada"))
                    .fechaIngresoEjecucion(getCellLocalDate(row, columns, "fechaIngresoEjecucion"))
                    .fechaTasacion(getCellLocalDate(row, columns, "fechaTasacion"))
                    .fechaNombramientoMartillero(getCellLocalDate(row, columns, "fechaNombramientoMartillero"))
                    .fechaRemate1(getCellLocalDate(row, columns, "fechaRemate1"))
                    .fechaRemate2(getCellLocalDate(row, columns, "fechaRemate2"))
                    .fechaRemate3(getCellLocalDate(row, columns, "fechaRemate3"))
                    .observacionActos(getCellString(row, columns, "observacionActos"))
                    .comentario(getCellString(row, columns, "comentario"))
                    .estadoCartera(estadoCartera)
                    .fechaDesembolso(fechaDesembolso)
                    .importeDesembolso(importeDesembolso)
                    .etapaProcesalTexto(etapaProcesalTexto)
                    .actoPendiente(actoPendiente)
                    .fechaUltimoEstadoProceso(fechaUltimoEstadoProceso)
                    .zona(getCellString(row, columns, "zona"))
                    .departamento(getCellString(row, columns, "departamento"))
                    .provincia(getCellString(row, columns, "provincia"))
                    .distrito(getCellString(row, columns, "distrito"))
                    .direccion(getCellString(row, columns, "direccion"))
                    .referencia(getCellString(row, columns, "referencia"))
                    .telefono(getCellString(row, columns, "telefono"))
                    .montoAprobado(getCellBigDecimal(row, columns, "montoAprobado"))
                    .fechaAceptacionDemanda(getCellLocalDate(row, columns, "fechaAceptacionDemanda"))
                    .fechaEnvioJudicial(getCellLocalDate(row, columns, "fechaEnvioJudicial"))
                    .fechaAsignacionAbogado(getCellLocalDate(row, columns, "fechaAsignacionAbogado"))
                    .fechaCastigo(getCellLocalDate(row, columns, "fechaCastigo"))
                    .tipoFondo(getCellString(row, columns, "tipoFondo"))
                    .activo(true)
                    .build();
            operacionNueva = true;
        } else {
            // Actualizar montos y datos operativos
            operacion.setAgencia(agencia);
            operacion.setTipoCredito(tipoCredito);
            operacion.setAnalista(analista);
            operacion.setAnalistaSenior(analistaSenior);
            if (moneda != null) operacion.setMoneda(moneda);
            operacion.setMontoCapital(getCellBigDecimal(row, columns, "montoCapital"));
            operacion.setMontoTotal(getCellBigDecimal(row, columns, "montoTotal"));
            operacion.setDiasMora(diasMora);
            operacion.setEstado(estado);
            operacion.setEtapa(etapa);
            operacion.setSituacion(situacion);
            operacion.setNumeroExpediente(numeroExpediente);
            operacion.setTipoProceso(tipoProceso);
            operacion.setTipoJuzgado(tipoJuzgado);
            operacion.setDistritoJudicial(distritoJudicial);
            operacion.setNumeroJuzgado(numeroJuzgado);
            operacion.setTrans(trans);
            operacion.setBusquedaBienes(busquedaBienes);
            operacion.setMontoDemandado(montoDemandado);
            operacion.setEscribanoLegal(escribanoLegal);
            operacion.setCodigoExpCautelar(codigoExpCautelar);
            operacion.setIncidente(incidente);
            operacion.setFechaPresentacion(getCellLocalDate(row, columns, "fechaPresentacion"));
            operacion.setFechaInadmisiblePrincipal(getCellLocalDate(row, columns, "fechaInadmisiblePrincipal"));
            operacion.setFechaAdmisionPrincipal(getCellLocalDate(row, columns, "fechaAdmisionPrincipal"));
            operacion.setFechaAudienciaUnica(getCellLocalDate(row, columns, "fechaAudienciaUnica"));
            operacion.setFechaAutoFinal(getCellLocalDate(row, columns, "fechaAutoFinal"));
            operacion.setFechaConsentimiento(getCellLocalDate(row, columns, "fechaConsentimiento"));
            operacion.setFechaEjecutoriada(getCellLocalDate(row, columns, "fechaEjecutoriada"));
            operacion.setFechaIngresoEjecucion(getCellLocalDate(row, columns, "fechaIngresoEjecucion"));
            operacion.setFechaTasacion(getCellLocalDate(row, columns, "fechaTasacion"));
            operacion.setFechaNombramientoMartillero(getCellLocalDate(row, columns, "fechaNombramientoMartillero"));
            operacion.setFechaRemate1(getCellLocalDate(row, columns, "fechaRemate1"));
            operacion.setFechaRemate2(getCellLocalDate(row, columns, "fechaRemate2"));
            operacion.setFechaRemate3(getCellLocalDate(row, columns, "fechaRemate3"));
            operacion.setObservacionActos(getCellString(row, columns, "observacionActos"));
            operacion.setComentario(getCellString(row, columns, "comentario"));
            operacion.setEstadoCartera(estadoCartera);
            operacion.setFechaDesembolso(fechaDesembolso);
            operacion.setImporteDesembolso(importeDesembolso);
            operacion.setEtapaProcesalTexto(etapaProcesalTexto);
            operacion.setActoPendiente(actoPendiente);
            operacion.setFechaUltimoEstadoProceso(fechaUltimoEstadoProceso);
            operacion.setZona(getCellString(row, columns, "zona"));
            operacion.setDepartamento(getCellString(row, columns, "departamento"));
            operacion.setProvincia(getCellString(row, columns, "provincia"));
            operacion.setDistrito(getCellString(row, columns, "distrito"));
            operacion.setDireccion(getCellString(row, columns, "direccion"));
            operacion.setReferencia(getCellString(row, columns, "referencia"));
            operacion.setTelefono(getCellString(row, columns, "telefono"));
            operacion.setMontoAprobado(getCellBigDecimal(row, columns, "montoAprobado"));
            operacion.setFechaAceptacionDemanda(getCellLocalDate(row, columns, "fechaAceptacionDemanda"));
            operacion.setFechaEnvioJudicial(getCellLocalDate(row, columns, "fechaEnvioJudicial"));
            operacion.setFechaAsignacionAbogado(getCellLocalDate(row, columns, "fechaAsignacionAbogado"));
            operacion.setFechaCastigo(getCellLocalDate(row, columns, "fechaCastigo"));
            operacion.setTipoFondo(getCellString(row, columns, "tipoFondo"));
        }
        operacion = operacionRepository.save(operacion);

        // 7) BienEmbargado — solo si hay partida registral
        String partidaRegistral = getCellString(row, columns, "numeroFichaRegistral");
        if (partidaRegistral != null && !partidaRegistral.isBlank()) {
            BienEmbargado bien = BienEmbargado.builder()
                    .operacion(operacion)
                    .detalleGarantia(getCellString(row, columns, "detalleBien"))
                    .partidaRegistral(getCellString(row, columns, "numeroFichaRegistral"))
                    .tipoBien(getCellString(row, columns, "tipoBien"))
                    .direccion(getCellString(row, columns, "direccionInmueble"))
                    .distrito(getCellString(row, columns, "distritoInmueble"))
                    .provincia(getCellString(row, columns, "provinciaInmueble"))
                    .departamento(getCellString(row, columns, "departamentoInmueble"))
                    .tipoPreferencia(getCellString(row, columns, "tipoPreferencia"))
                    .montoMc(getCellBigDecimal(row, columns, "montoMc"))
                    .monedaMc(getCellString(row, columns, "monedaMc"))
                    .garantiaInscrita(getCellString(row, columns, "garantiaInscrita"))
                    .fechaInscripcion(getCellLocalDate(row, columns, "fechaInscripcionRrpp"))
                    .fechaPresentacionRrpp(getCellLocalDate(row, columns, "fechaPresentacionRrpp"))
                    .asientoInscripcion(getCellString(row, columns, "asientoInscripcion"))
                    .fechaPresentacionMc(getCellLocalDate(row, columns, "fechaPresentacionMc"))
                    .fechaInadmisible(getCellLocalDate(row, columns, "fechaInadmisibleMc"))
                    .fechaAdmision(getCellLocalDate(row, columns, "fechaAdmisionMc"))
                    .comentarioMc(getCellString(row, columns, "comentarioMc"))
                    .detalleAcreedores(getCellString(row, columns, "detalleAcreedores"))
                    .titularPredio(getCellString(row, columns, "titularPredio"))
                    .fechaGeneracionMc(getCellLocalDate(row, columns, "fechaGeneracionMc"))
                    .build();
            bienEmbargadoRepository.save(bien);
        }

        return new ParseResult(cliente, operacion, clienteNuevo || operacionNueva);
    }

    /**
     * Escanea la columna B de la hoja para detectar section headers
     * que marcan cambios de estado de cartera.
     * Ejemplo: "CARTERA DESASIGNADA" en col B = DESASIGNADA para esa fila en adelante.
     *
     * Retorna un Map de rowIndex -> estado para cada fila de datos.
     */
    private Map<Integer, String> escanearSeccionesColB(Sheet sheet, int headerRowIdx, int lastRowNum, String estadoCarteraDefault) {
        Map<Integer, String> filasEstado = new java.util.LinkedHashMap<>();

        // Primer paso: detectar las filas donde cambian los section headers
        Map<Integer, String> sectionHeaders = new java.util.LinkedHashMap<>();
        for (int i = headerRowIdx + 1; i <= lastRowNum; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            Cell cellB = row.getCell(1); // Columna B (0-indexed = 1)
            if (cellB == null) continue;
            String valor = cellToString(cellB);
            if (valor == null || valor.isBlank()) continue;
            String upper = valor.toUpperCase().trim();
            String estado = null;
            if (upper.startsWith("CARTERA DESASIGNADA")) {
                estado = "DESASIGNADA";
            } else if (upper.startsWith("CARTERA VENDIDA")) {
                estado = "VENDIDA";
            } else if (upper.startsWith("CARTERA CANCELADA")) {
                estado = "CANCELADA";
            }
            if (estado != null) {
                sectionHeaders.put(i, estado);
            }
        }

        // Segundo paso: asignar estado a cada fila de datos
        String estadoActual = estadoCarteraDefault; // default de la hoja (CANCELADA o DEVUELTA)
        List<Integer> sectionRowIndices = new java.util.ArrayList<>(sectionHeaders.keySet());
        int sectionIdx = 0;

        for (int i = headerRowIdx + 1; i <= lastRowNum; i++) {
            // Si esta fila es un section header, actualizar el estado activo
            if (sectionHeaders.containsKey(i)) {
                estadoActual = sectionHeaders.get(i);
                sectionIdx++;
            } else {
                // Si no hay section header en esta fila, verificar si cruzamos uno
                while (sectionIdx < sectionRowIndices.size() && i > sectionRowIndices.get(sectionIdx)) {
                    sectionIdx++;
                    if (sectionIdx < sectionRowIndices.size()) {
                        estadoActual = sectionHeaders.get(sectionRowIndices.get(sectionIdx));
                    }
                }
            }
            filasEstado.put(i, estadoActual);
        }

        return filasEstado;
    }

    /**
     * Detecta el estado de cartera desde la columna OBSERVACION.
     * Override del estado default de la hoja (CANCELADA para hoja 2, DEVUELTA para hoja 3).
     */
    private String detectarEstadoCartera(String estadoDefault, String observacion) {
        if (observacion == null || observacion.isBlank()) {
            return estadoDefault;
        }
        String upper = observacion.toUpperCase();
        if (upper.contains("CANCELADO") || upper.contains("CANCELO")) {
            return "CANCELADA";
        }
        if (upper.contains("CARTERA VENDIDA") || upper.contains("VENDIDA")) {
            return "VENDIDA";
        }
        if (upper.contains("DESASIGNADA")) {
            return "DESASIGNADA";
        }
        if (upper.contains("DEVUELTA")) {
            return "DEVUELTA";
        }
        return estadoDefault;
    }
    // ---- helpers de lectura de celdas ----

    private String getCellString(Row row, JsonNode columns, String field) {
        if (!columns.has(field) || columns.get(field).isNull()) return "";
        int colIdx = columns.get(field).asInt() - 1; // JSON es 1-indexed
        Cell cell = row.getCell(colIdx);
        return cellToString(cell);
    }

    private BigDecimal getCellBigDecimal(Row row, JsonNode columns, String field) {
        if (!columns.has(field) || columns.get(field).isNull()) return BigDecimal.ZERO;
        int colIdx = columns.get(field).asInt() - 1;
        Cell cell = row.getCell(colIdx);
        return cellToBigDecimal(cell);
    }

    private Integer getCellInteger(Row row, JsonNode columns, String field) {
        if (!columns.has(field) || columns.get(field).isNull()) return null;
        int colIdx = columns.get(field).asInt() - 1;
        Cell cell = row.getCell(colIdx);
        return cellToInteger(cell);
    }

    private Boolean getCellBoolean(Row row, JsonNode columns, String field) {
        if (!columns.has(field) || columns.get(field).isNull()) return null;
        int colIdx = columns.get(field).asInt() - 1;
        Cell cell = row.getCell(colIdx);
        return cellToBoolean(cell);
    }

    private LocalDate getCellLocalDate(Row row, JsonNode columns, String field) {
        if (!columns.has(field) || columns.get(field).isNull()) return null;
        int colIdx = columns.get(field).asInt() - 1;
        Cell cell = row.getCell(colIdx);
        return cellToLocalDate(cell);
    }

    private String cellToString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    private Integer cellToInteger(Cell cell) {
        if (cell == null) return null;
        try {
            return switch (cell.getCellType()) {
                case NUMERIC -> (int) cell.getNumericCellValue();
                case STRING -> {
                    String val = cell.getStringCellValue().trim();
                    yield val.isEmpty() ? null : Integer.parseInt(val);
                }
                default -> null;
            };
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Boolean cellToBoolean(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case BOOLEAN -> cell.getBooleanCellValue();
            case STRING -> {
                String val = cell.getStringCellValue().trim().toLowerCase();
                if (val.equals("si") || val.equals("sí") || val.equals("true") || val.equals("1") || val.equals("x")) {
                    yield true;
                }
                if (val.equals("no") || val.equals("negativo") || val.equals("false") || val.equals("0")) {
                    yield false;
                }
                yield null;
            }
            default -> null;
        };
    }

    private BigDecimal cellToBigDecimal(Cell cell) {
        if (cell == null) return BigDecimal.ZERO;
        try {
            return switch (cell.getCellType()) {
                case NUMERIC -> BigDecimal.valueOf(cell.getNumericCellValue());
                case STRING -> {
                    String val = cell.getStringCellValue().trim();
                    yield val.isEmpty() ? BigDecimal.ZERO : new BigDecimal(val.replace(",", ""));
                }
                default -> BigDecimal.ZERO;
            };
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private LocalDate cellToLocalDate(Cell cell) {
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate();
                }
                return LocalDate.ofEpochDay((long) cell.getNumericCellValue());
            } else if (cell.getCellType() == CellType.STRING) {
                String val = cell.getStringCellValue().trim();
                if (val.isEmpty()) return null;
                return LocalDate.parse(val, dateFormatter);
            }
        } catch (DateTimeParseException e) {
            return null;
        }
        return null;
    }

    private boolean isRowEmpty(Row row, JsonNode columns) {
        for (var field : List.of("dni", "cuenta", "numeroOperacion")) {
            if (!columns.has(field) || columns.get(field).isNull()) continue;
            int colIdx = columns.get(field).asInt() - 1;
            Cell cell = row.getCell(colIdx);
            String val = cellToString(cell);
            if (!val.isEmpty()) return false;
        }
        return true;
    }

    private String normalizarDni(String dni) {
        if (dni == null) return null;
        String limpio = dni.replaceAll("[^0-9]", "");
        if (limpio.length() > 8) limpio = limpio.substring(0, 8);
        return limpio.isEmpty() ? null : limpio;
    }

    // ---- listado ----

    public List<ImportacionDTO> listarImportaciones() {
        return importacionRepository.findAllByOrderByFechaImportacionDesc().stream()
                .map(i -> {
                    String empNombre = empresaRepository.findById(i.getEmpresaId())
                            .map(Empresa::getNombre).orElse("N/A");
                    String agNombre = i.getAgenciaId() != null
                            ? agenciaRepository.findById(i.getAgenciaId())
                                    .map(Agencia::getNombre).orElse("N/A")
                            : null;
                    return toDTO(i, empNombre, agNombre);
                })
                .toList();
    }

    private ImportacionDTO toDTO(Importacion entity, String empresaNombre, String agenciaNombre) {
        return ImportacionDTO.builder()
                .id(entity.getId())
                .nombreArchivo(entity.getNombreArchivo())
                .totalRegistros(entity.getTotalRegistros())
                .registrosExitosos(entity.getRegistrosExitosos())
                .registrosFallidos(entity.getRegistrosFallidos())
                .empresaId(entity.getEmpresaId())
                .empresaNombre(empresaNombre)
                .agenciaId(entity.getAgenciaId())
                .agenciaNombre(agenciaNombre)
                .estado(entity.getEstado())
                .usuarioImporta(entity.getUsuarioImporta())
                .fechaImportacion(entity.getFechaImportacion() != null
                        ? entity.getFechaImportacion().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : null)
                .errores(entity.getErrores())
                .build();
    }
}
