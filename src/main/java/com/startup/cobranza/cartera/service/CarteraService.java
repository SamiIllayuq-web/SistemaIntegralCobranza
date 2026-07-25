package com.startup.cobranza.cartera.service;

import com.startup.cobranza.agencia.entity.Agencia;
import com.startup.cobranza.agencia.repository.AgenciaRepository;
import com.startup.cobranza.cartera.dto.ImportacionDTO;
import com.startup.cobranza.cartera.entity.Importacion;
import com.startup.cobranza.cartera.entity.Operacion;
import com.startup.cobranza.cartera.exception.CarteraException;
import com.startup.cobranza.cartera.mapper.OperacionMapper;
import com.startup.cobranza.cartera.repository.ImportacionRepository;
import com.startup.cobranza.cartera.repository.OperacionRepository;
import com.startup.cobranza.cliente.entity.Cliente;
import com.startup.cobranza.cliente.repository.ClienteRepository;
import com.startup.cobranza.empresa.entity.Empresa;
import com.startup.cobranza.empresa.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarteraService {

    private final ImportacionRepository importacionRepository;
    private final ClienteRepository clienteRepository;
    private final OperacionRepository operacionRepository;
    private final EmpresaRepository empresaRepository;
    private final AgenciaRepository agenciaRepository;
    private final OperacionMapper operacionMapper;

    // Formato de fechas del Excel: dd.MM.yyyy
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

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
                .orElseThrow(() -> new CarteraException("Empresa no encontrada"));

        Agencia agencia = null;
        if (agenciaId != null) {
            agencia = agenciaRepository.findById(agenciaId).orElse(null);
        }

        int total = 0;
        int exitosos = 0;
        List<String> errores = new ArrayList<>();

        try (InputStream is = archivo.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            if (headerRow == null) {
                throw new CarteraException("El archivo no tiene encabezado");
            }

            // Validar encabezado mínimo: vérifier que las columnas clave existan
            validarEncabezados(headerRow);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                // Ignorar filas vacías o filas que son continuación (sin NRO ni cuenta)
                if (row == null || isRowVacia(row)) continue;

                total++;
                try {
                    procesarRow(row, empresa, agencia);
                    exitosos++;
                } catch (Exception e) {
                    errores.add("Fila " + (i + 1) + ": " + e.getMessage());
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
                .registrosExitosos(exitosos)
                .registrosFallidos(total - exitosos)
                .empresaId(empresaId)
                .agenciaId(agenciaId)
                .usuarioImporta(usuario)
                .estado("COMPLETADO")
                .errores(errores.isEmpty() ? null : String.join("\n", errores))
                .build();

        Importacion saved = importacionRepository.save(importacion);
        return toDTO(saved, empresa.getNombre(), agencia != null ? agencia.getNombre() : null);
    }

    /**
     * Procesa una fila del Excel:
     * 1. Busca o crea el Cliente por DNI
     * 2. Busca o crea la Operacion por (cuenta, numeroOperacion)
     * 3. Actualiza todos los campos de la Operacion
     */
    private void procesarRow(Row row, Empresa empresa, Agencia agencia) {
        // Columnas del Excel (índice 0-based):
        // A=NRO (ignorar), B=ABOGADO, C=C&O (ignorar), D=CUENTA, E=OPERACIÓN,
        // F=MONEDA, G=TRANS., H=SITUACION, I=DEUDA CAP, J=DEUDA TOTAL,
        // K=BUSQUEDA DE BIENES, L=TELÉFONO, M=DIRECCIÓN,
        // N=TIPO PROCESO, O=TIPO JUZGADO, P=DISTRITO JUDICIAL, Q=N° JUZGADO,
        // R=N° EXPEDIENTE, S=¿TIENE INCIDENTE?, T=MONTO DEMANDADO,
        // U=SECRETARIO LEGAL, V=CÓD. EXPEDIENTE CAUTELAR, W=DETALLE BIEN EMBARGADO,
        // X=N° PARTIDA, Y=TIPO BIEN EMBARGADO, Z=RANGO,
        // AA=DETALLE ACREEDORES, AB=TIPO PREFERENTE,
        // AC=MONTO MEDIDA CAUTELAR, AD=MONEDA MC, AE=M. CAUTELAR EJECUTADA,
        // AF=F. INSCRIPCIÓN EMBARGO, AG=F. PRESENTACIÓN TÍTULO RRPP,
        // AH=ASIENTO INSCRIPCIÓN, AI=F. PRESENTACIÓN MC,
        // AJ=F. INADMISIBLE, AK=F. ADMISION, AL=COMENTARIO,
        // AM=F. PRESENTACIÓN, AN=F. INADMISIBLE 2, AO=F. ADMISION 2,
        // AP=TIPO AUDIENCIA, AQ=F. AUTO FINAL, AR=EJECUTORIADA,
        // AS=F. NOMBRAMIENTO PERITOS, AT=F. NOMBRAMIENTO MARTILLERO,
        // AU=F. REMATE 1, AV=F. REMATE 2, AW=F. REMATE 3,
        // AX=F. PRÓXIMO ACTO PROCESAL, AY=COMENTARIO PROCESAL

        String nombre = getCellString(row.getCell(1));  // Columna B: NOMBRE
        String dni = getCellString(row.getCell(2)).replaceAll("[^0-9]", "");  // Columna C: DNI (solo números)

        if (dni.isEmpty()) {
            throw new IllegalArgumentException("DNI vacío, no se puede procesar la fila");
        }

        // 1. Buscar o crear Cliente
        Cliente clienteExistente = clienteRepository.findByDni(dni)
                .map(c -> {
                    String tel = getCellString(row.getCell(11));  // Columna L: TELÉFONO
                    String dir = getCellString(row.getCell(12)); // Columna M: DIRECCIÓN
                    if (!tel.isEmpty()) c.setTelefono(tel);
                    if (!dir.isEmpty()) c.setDireccion(dir);
                    if (!nombre.isEmpty()) c.setNombreCompleto(nombre);
                    return c;
                })
                .orElseGet(() -> Cliente.builder()
                        .nombreCompleto(nombre.isEmpty() ? "SIN NOMBRE" : nombre)
                        .dni(dni)
                        .telefono(getCellString(row.getCell(11)))
                        .direccion(getCellString(row.getCell(12)))
                        .empresa(empresa)
                        .activo(true)
                        .build());

        Cliente cliente = clienteRepository.save(clienteExistente);

        // 2. Preparar datos de la operación desde el row
        String cuenta = getCellString(row.getCell(3));  // Columna D: CUENTA
        String numeroOperacion = getCellString(row.getCell(4));  // Columna E: OPERACIÓN

        if (cuenta.isEmpty() || numeroOperacion.isEmpty()) {
            throw new IllegalArgumentException("Cuenta u operación vacía");
        }

        // 3. Buscar o crear Operacion
        Operacion operacion = operacionRepository
                .findByCuentaAndNumeroOperacion(cuenta, numeroOperacion)
                .orElseGet(() -> Operacion.builder()
                        .cuenta(cuenta)
                        .numeroOperacion(numeroOperacion)
                        .cliente(cliente)
                        .build());

        // 4. Actualizar todos los campos de la operación
        actualizarOperacion(operacion, row, agencia);

        operacionRepository.save(operacion);
    }

    private void actualizarOperacion(Operacion op, Row row, Agencia agencia) {
        op.setAgencia(agencia);
        op.setAbogadoNombre(getCellString(row.getCell(1)));  // B: ABOGADO
        op.setMoneda(getCellString(row.getCell(5)));         // F: MONEDA
        op.setTransferido(getCellString(row.getCell(6)));    // G: TRANS.
        op.setSituacion(getCellString(row.getCell(7)));      // H: SITUACION
        op.setDeudaCap(getCellDecimal(row.getCell(8)));      // I: DEUDA CAP
        op.setDeudaTotal(getCellDecimal(row.getCell(9)));    // J: DEUDA TOTAL
        op.setBusquedaBienes(getCellString(row.getCell(10))); // K: BUSQUEDA DE BIENES
        op.setTipoProceso(getCellString(row.getCell(13)));   // N: TIPO PROCESO
        op.setTipoJuzgado(getCellString(row.getCell(14)));   // O: TIPO JUZGADO
        op.setDistritoJudicial(getCellString(row.getCell(15))); // P: DISTRITO JUDICIAL
        op.setNumeroJuzgado(getCellString(row.getCell(16))); // Q: N° JUZGADO
        op.setNumeroExpediente(getCellString(row.getCell(17))); // R: N° EXPEDIENTE
        op.setTieneIncidente(getCellBoolean(row.getCell(18)));  // S: ¿TIENE INCIDENTE?
        op.setMontoDemandado(getCellDecimal(row.getCell(19)));   // T: MONTO DEMANDADO
        op.setSecretarioLegal(getCellString(row.getCell(20)));   // U: SECRETARIO LEGAL
        op.setCodigoExpedienteCautelar(getCellString(row.getCell(21))); // V: CÓD. EXPEDIENTE CAUTELAR
        op.setDetalleBienEmbargado(getCellString(row.getCell(22))); // W: DETALLE BIEN EMBARGADO
        op.setNumeroPartida(getCellString(row.getCell(23)));    // X: N° PARTIDA
        op.setTipoBienEmbargado(getCellString(row.getCell(24))); // Y: TIPO BIEN EMBARGADO
        op.setRango(getCellString(row.getCell(25)));            // Z: RANGO
        op.setDetalleAcreedores(getCellString(row.getCell(26))); // AA: DETALLE ACREEDORES
        op.setTipoPreferente(getCellString(row.getCell(27)));   // AB: TIPO PREFERENTE
        op.setMontoMedidaCautelar(getCellDecimal(row.getCell(28)));  // AC: MONTO MEDIDA CAUTELAR
        op.setMonedaMc(getCellString(row.getCell(29)));         // AD: MONEDA MC
        op.setMedidaCautelarEjecutada(getCellString(row.getCell(30))); // AE: M. CAUTELAR EJECUTADA
        op.setFechaInscripcionEmbargo(getCellDate(row.getCell(31)));  // AF: F. INSCRIPCIÓN EMBARGO
        op.setFechaPresentacionTituloRrpp(getCellDate(row.getCell(32))); // AG: F. PRESENTACIÓN TÍTULO RRPP
        op.setAsientoInscripcion(getCellString(row.getCell(33))); // AH: ASIENTO INSCRIPCIÓN
        op.setFechaPresentacionMc(getCellDate(row.getCell(34)));  // AI: F. PRESENTACIÓN MC
        op.setFechaInadmisible(getCellDate(row.getCell(35)));     // AJ: F. INADMISIBLE
        op.setFechaAdmision(getCellDate(row.getCell(36)));        // AK: F. ADMISION
        op.setComentario(getCellString(row.getCell(37)));         // AL: COMENTARIO
        op.setFechaPresentacion(getCellDate(row.getCell(38)));    // AM: F. PRESENTACIÓN
        op.setFechaInadmisible2(getCellDate(row.getCell(39)));    // AN: F. INADMISIBLE 2
        op.setFechaAdmision2(getCellDate(row.getCell(40)));       // AO: F. ADMISION 2
        op.setAudienciaTipo(getCellString(row.getCell(41)));      // AP: TIPO AUDIENCIA
        op.setFechaAutoFinal(getCellDate(row.getCell(42)));       // AQ: F. AUTO FINAL
        op.setFechaEjecutoriada(getCellDate(row.getCell(43)));    // AR: EJECUTORIADA
        op.setFechaNombramientoPeritos(getCellDate(row.getCell(44))); // AS: F. NOMBRAMIENTO PERITOS
        op.setFechaNombramientoMartillero(getCellDate(row.getCell(45))); // AT: F. NOMBRAMIENTO MARTILLERO
        op.setFechaRemate1(getCellDate(row.getCell(46)));         // AU: F. REMATE 1
        op.setFechaRemate2(getCellDate(row.getCell(47)));         // AV: F. REMATE 2
        op.setFechaRemate3(getCellDate(row.getCell(48)));         // AW: F. REMATE 3
        op.setFechaProximoActoProcesal(getCellDate(row.getCell(49))); // AX: F. PRÓXIMO ACTO PROCESAL
        op.setComentarioProcesal(getCellString(row.getCell(50)));  // AY: COMENTARIO PROCESAL
    }

    private void validarEncabezados(Row headerRow) {
        // Validación flexible: vérifier que las columnas clave existan
        // No validamos orden exacto porque el Excel puede tener columnas adicionales
        String nombre = getCellString(headerRow.getCell(1));
        String dni = getCellString(headerRow.getCell(2));
        String cuenta = getCellString(headerRow.getCell(3));
        String operacion = getCellString(headerRow.getCell(4));

        if (nombre.isEmpty() && dni.isEmpty() && cuenta.isEmpty()) {
            throw new CarteraException("El archivo no tiene el formato esperado del Excel de cartera");
        }
    }

    private boolean isRowVacia(Row row) {
        // Una fila se considera vacía si no tiene NRO (col A), ni cuenta (col D), ni nombre (col B)
        String nro = getCellString(row.getCell(0));
        String cuenta = getCellString(row.getCell(3));
        String nombre = getCellString(row.getCell(1));
        return nro.isEmpty() && cuenta.isEmpty() && nombre.isEmpty();
    }

    private String getCellString(Cell cell) {
        if (cell == null) return "";
        // Evaluar fórmulas primero
        if (cell.getCellType() == CellType.FORMULA) {
            try {
                return String.valueOf((long) cell.getNumericCellValue());
            } catch (IllegalStateException e) {
                return cell.getStringCellValue();
            }
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue().format(DATE_FORMAT);
                }
                double val = cell.getNumericCellValue();
                yield (val == Math.floor(val)) ? String.valueOf((long) val) : String.valueOf(val);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case BLANK -> "";
            default -> "";
        };
    }

    private BigDecimal getCellDecimal(Cell cell) {
        if (cell == null) return null;
        try {
            return switch (cell.getCellType()) {
                case NUMERIC -> BigDecimal.valueOf(cell.getNumericCellValue());
                case STRING -> {
                    String val = cell.getStringCellValue().trim();
                    yield val.isEmpty() ? null : new BigDecimal(val.replace(",", ""));
                }
                default -> null;
            };
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }

    private LocalDate getCellDate(Cell cell) {
        if (cell == null) return null;
        try {
            return switch (cell.getCellType()) {
                case NUMERIC -> {
                    if (DateUtil.isCellDateFormatted(cell)) {
                        yield cell.getLocalDateTimeCellValue().toLocalDate();
                    }
                    yield null;
                }
                case STRING -> {
                    String val = cell.getStringCellValue().trim();
                    yield val.isEmpty() ? null : LocalDate.parse(val, DATE_FORMAT);
                }
                default -> null;
            };
        } catch (DateTimeParseException | NullPointerException e) {
            return null;
        }
    }

    private Boolean getCellBoolean(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case BOOLEAN -> cell.getBooleanCellValue();
            case STRING -> {
                String val = cell.getStringCellValue().trim().toUpperCase();
                yield switch (val) {
                    case "SI", "SÍ", "VERDADERO", "1", "YES", "TRUE" -> true;
                    case "NO", "FALSO", "0", "NOT", "FALSE" -> false;
                    default -> null;
                };
            }
            default -> null;
        };
    }

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
