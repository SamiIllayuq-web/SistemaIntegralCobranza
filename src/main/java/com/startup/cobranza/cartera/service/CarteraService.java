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
import com.startup.cobranza.expediente.entity.BienEmbargado;
import com.startup.cobranza.expediente.repository.BienEmbargadoRepository;
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

        // Detectar perfil según nombre del archivo
        if (nombreOriginal.toLowerCase().contains("avance") || nombreOriginal.toLowerCase().contains("procesal")) {
            cargarPerfil(PERFIL_EXCEL_AVANCE);
        } else {
            cargarPerfil(PERFIL_CAJA_AREQUIPA);
        }

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new CarteraException("Empresa no encontrada: " + empresaId));

        // sheetName puede estar en el perfil; si no, default a "Hoja2"
        String sheetName = perfilJson.has("sheetName") ? perfilJson.get("sheetName").asText() : "Hoja2";
        int headerRowIdx = perfilJson.has("headerRow") ? perfilJson.get("headerRow").asInt() - 1 : 0;
        boolean skipRowsWithoutDni = perfilJson.has("skipRowsWithoutDni") && perfilJson.get("skipRowsWithoutDni").asBoolean();

        int total = 0;
        int creados = 0;
        int actualizados = 0;
        int errores = 0;
        List<String> listaErrores = new ArrayList<>();

        try (InputStream is = archivo.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                // fallback: buscar por índice 0
                sheet = workbook.getSheetAt(0);
            }

            Row headerRow = sheet.getRow(headerRowIdx);
            if (headerRow == null) {
                throw new CarteraException("No se encontró la fila de encabezado en la posición " + (headerRowIdx + 1));
            }

            JsonNode columns = perfilJson.get("columns");

            int lastRowNum = sheet.getLastRowNum();
            for (int i = headerRowIdx + 1; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row, columns)) continue;

                total++;
                try {
                    ParseResult result = parseRow(row, columns, empresa, skipRowsWithoutDni);
                    if (result.esNuevo) {
                        creados++;
                    } else {
                        actualizados++;
                    }
                } catch (Exception e) {
                    errores++;
                    listaErrores.add("Fila " + (i + 1) + ": " + e.getMessage());
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

    private ParseResult parseRow(Row row, JsonNode columns, Empresa empresa, boolean skipRowsWithoutDni) {
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

        // 2) Validar campos obligatorios
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

        // 3) Find-or-create Cliente por DNI
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
            // Actualizar datos personales si cambiaron
            if (nombreCompleto != null && !nombreCompleto.isBlank()) {
                cliente.setNombreCompleto(nombreCompleto);
            }
            clienteRepository.save(cliente);
        }

        // 4) Find-or-create Agencia por nombre + empresa
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

        // 5) Upsert Operacion por (empresa_id, cuenta, numero_operacion)
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
        }
        operacion = operacionRepository.save(operacion);

        // 6) BienEmbargado — solo si hay partida registral
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
