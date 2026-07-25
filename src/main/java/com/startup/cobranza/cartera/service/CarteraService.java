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
            // Los headers reales están en la FILA 2 (índice 1)
            // La fila 1 (índice 0) suele estar vacía o ser informativa
            Row headerRow = sheet.getRow(1);

            if (headerRow == null) {
                throw new CarteraException("El archivo no tiene encabezado");
            }

            // Validar que las columnas clave existan en la fila de headers (índice 1)
            validarEncabezados(headerRow);

            // Data empieza en fila 3 (índice 2)
            for (int i = 2; i <= sheet.getLastRowNum(); i++) {
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
        // Mapeo de columnas Excel (índice 0-based):
        //  A=NRO, B=ABOGADO, C=C&O, D=CUENTA, E=OPERACIÓN,
        //  F=NOMBRE DEL CLIENTE, G=DNI, H=TRANS., I=OBSERVACION, J=SITUACION,
        //  K=AGENCIA, L=MONEDA, M=BUSQUEDA DE BIENES, N=DEUDA CAP, O=DEUDA TOTAL,
        //  P=TIPO PROCESO, Q=TIPO JUZGADO, R=DISTRITO JUDICIAL, S=N° JUZGADO,
        //  T=N° EXPEDIENTE, U=INCIDENTE SI/NO, V=MONTO DDO.,
        //  W=SECRETARIO LEGAL, X=CÓDIGO EXP. CAUTELAR, Y=DETALLE BIEN EMBARGADO,
        //  Z=N° PARTIDA, [=TIPO BIEN EMBARGADO, \=RANGO, ]=DETALLE ACREEDORES,
        //  ^=TIPO PREFERENTE, _=MONTO MC, `=MONEDA MC, a=MC EJECUTADA,
        //  b=F. INSCRIP. EMBARGO, c=F. PRESENTACIÓN TÍTULO RRPP, d=ASIENTO INSCRIPCIÓN,
        //  e=F. PRESENTACIÓN MC, f=F. INADMISIBLE, g=F. ADMISION, h=COMENTARIO,
        //  i=F. PRESENTACIÓN, j=F. INADMISIBLE 2, k=F. ADMISION 2,
        //  l=AUDIENCIA TIPO, m=F. AUTO FINAL, n=F. EJECUTORIADA,
        //  o=F. NOMBRAMIENTO PERITOS, p=F. NOMBRAMIENTO MARTILLERO,
        //  q=F. REMATE 1, r=F. REMATE 2, s=F. REMATE 3,
        //  t=OBSERVACION/ACTOS PROCESALES, u=COMENTARIO

        // 5=F NOMBRE, 6=G DNI, 3=D CUENTA, 4=E OPERACIÓN
        String nombre = getCellString(row.getCell(5));   // F: NOMBRE DEL CLIENTE
        String dni = getCellString(row.getCell(6)).replaceAll("[^0-9]", ""); // G: DNI

        if (dni.isEmpty()) {
            throw new IllegalArgumentException("DNI vacío, no se puede procesar la fila");
        }

        // 1. Buscar o crear Cliente
        Cliente clienteExistente = clienteRepository.findByDni(dni)
                .map(c -> {
                    if (!nombre.isEmpty()) c.setNombreCompleto(nombre);
                    // Teléfono y dirección ya no están en este Excel
                    return c;
                })
                .orElseGet(() -> Cliente.builder()
                        .nombreCompleto(nombre.isEmpty() ? "SIN NOMBRE" : nombre)
                        .dni(dni)
                        .empresa(empresa)
                        .activo(true)
                        .build());

        Cliente cliente = clienteRepository.save(clienteExistente);

        // 2. Buscar datos de operación
        // D (índice 3) y E (índice 4) pueden ser numéricos en el Excel
        String cuenta = getCellString(row.getCell(3));
        String numeroOperacion = getCellString(row.getCell(4));

        if (cuenta.isEmpty() || numeroOperacion.isEmpty()) {
            throw new IllegalArgumentException("Cuenta u operación vacía");
        }

        // 3. Determinar la agencia: prioridad al nombre del Excel (col K=10)
        //    si está vacía, usar la agencia del formulario
        Agencia agenciaOperacion = agencia;
        String agenciaNombreExcel = getCellString(row.getCell(10)).trim();
        if (!agenciaNombreExcel.isEmpty() && !agenciaNombreExcel.equals(" ") && agencia == null) {
            // Buscar agencia por nombre en la empresa
            agenciaOperacion = agenciaRepository
                    .findByNombreIgnoreCaseAndEmpresaId(agenciaNombreExcel, empresa.getId())
                    .orElse(null);
        }

        // 4. Buscar o crear Operacion
        Operacion operacion = operacionRepository
                .findByCuentaAndNumeroOperacion(cuenta, numeroOperacion)
                .orElseGet(() -> Operacion.builder()
                        .cuenta(cuenta)
                        .numeroOperacion(numeroOperacion)
                        .cliente(cliente)
                        .build());

        // 5. Actualizar todos los campos de la operación
        actualizarOperacion(operacion, row, agenciaOperacion);

        operacionRepository.save(operacion);
    }

    private void actualizarOperacion(Operacion op, Row row, Agencia agencia) {
        op.setAgencia(agencia);
        op.setAbogadoNombre(getCellString(row.getCell(1)));   // B: ABOGADO
        op.setTransferido(getCellString(row.getCell(7)));    // H: TRANS.
        op.setObservaciones(getCellString(row.getCell(8)));   // I: OBSERVACION
        op.setSituacion(getCellString(row.getCell(9)));       // J: SITUACION
        op.setMoneda(getCellString(row.getCell(11)));         // L: MONEDA
        op.setBusquedaBienes(getCellString(row.getCell(12))); // M: BUSQUEDA DE BIENES
        op.setDeudaCap(getCellDecimal(row.getCell(13)));      // N: DEUDA CAP
        op.setDeudaTotal(getCellDecimal(row.getCell(14)));     // O: DEUDA TOTAL
        op.setTipoProceso(getCellString(row.getCell(15)));     // P: TIPO PROCESO
        op.setTipoJuzgado(getCellString(row.getCell(16)));    // Q: TIPO JUZGADO
        op.setDistritoJudicial(getCellString(row.getCell(17))); // R: DISTRITO JUDICIAL
        op.setNumeroJuzgado(getCellString(row.getCell(18)));   // S: N° JUZGADO
        op.setNumeroExpediente(getCellString(row.getCell(19))); // T: N° EXPEDIENTE
        op.setTieneIncidente(getCellBoolean(row.getCell(20)));  // U: INCIDENTE SI/NO
        op.setMontoDemandado(getCellDecimal(row.getCell(21)));  // V: MONTO DDO.
        op.setSecretarioLegal(getCellString(row.getCell(22)));  // W: SECRETARIO LEGAL
        op.setCodigoExpedienteCautelar(getCellString(row.getCell(23))); // X: CÓDIGO EXP. CAUTELAR
        op.setDetalleBienEmbargado(getCellString(row.getCell(24))); // Y: DETALLE BIEN EMBARGADO
        op.setNumeroPartida(getCellString(row.getCell(25)));    // Z: N° PARTIDA
        op.setTipoBienEmbargado(getCellString(row.getCell(26))); // [: TIPO BIEN EMBARGADO
        op.setRango(getCellString(row.getCell(27)));            // \: RANGO
        op.setDetalleAcreedores(getCellString(row.getCell(28))); // ]: DETALLE ACREEDORES
        op.setTipoPreferente(getCellString(row.getCell(29)));   // ^: TIPO PREFERENTE
        op.setMontoMedidaCautelar(getCellDecimal(row.getCell(30))); // _: MONTO MC
        op.setMonedaMc(getCellString(row.getCell(31)));         // `: MONEDA MC
        op.setMedidaCautelarEjecutada(getCellString(row.getCell(32))); // a: MC EJECUTADA
        op.setFechaInscripcionEmbargo(getCellDate(row.getCell(33)));  // b: F. INSCRIP. EMBARGO
        op.setFechaPresentacionTituloRrpp(getCellDate(row.getCell(34))); // c: F. PRESENTACIÓN TÍTULO RRPP
        op.setAsientoInscripcion(getCellString(row.getCell(35)));  // d: ASIENTO INSCRIPCIÓN
        op.setFechaPresentacionMc(getCellDate(row.getCell(36)));   // e: F. PRESENTACIÓN MC
        op.setFechaInadmisible(getCellDate(row.getCell(37)));       // f: F. INADMISIBLE
        op.setFechaAdmision(getCellDate(row.getCell(38)));          // g: F. ADMISION
        op.setComentario(getCellString(row.getCell(39)));            // h: COMENTARIO
        op.setFechaPresentacion(getCellDate(row.getCell(40)));       // i: F. PRESENTACIÓN
        op.setFechaInadmisible2(getCellDate(row.getCell(41)));       // j: F. INADMISIBLE 2
        op.setFechaAdmision2(getCellDate(row.getCell(42)));         // k: F. ADMISION 2
        op.setAudienciaTipo(getCellString(row.getCell(43)));         // l: AUDIENCIA TIPO
        op.setFechaAutoFinal(getCellDate(row.getCell(44)));          // m: F. AUTO FINAL
        op.setFechaEjecutoriada(getCellDate(row.getCell(45)));       // n: F. EJECUTORIADA
        op.setFechaNombramientoPeritos(getCellDate(row.getCell(46))); // o: F. NOMBRAMIENTO PERITOS
        op.setFechaNombramientoMartillero(getCellDate(row.getCell(47))); // p: F. NOMBRAMIENTO MARTILLERO
        op.setFechaRemate1(getCellDate(row.getCell(48)));            // q: F. REMATE 1
        op.setFechaRemate2(getCellDate(row.getCell(49)));            // r: F. REMATE 2
        op.setFechaRemate3(getCellDate(row.getCell(50)));            // s: F. REMATE 3
        op.setFechaProximoActoProcesal(getCellDate(row.getCell(51))); // t: OBSERVACION/ACTOS PROCESALES
        op.setComentarioProcesal(getCellString(row.getCell(52)));    // u: COMENTARIO
    }

    private void validarEncabezados(Row headerRow) {
        // Headers reales en fila 2 (índice 1): F=NOMBRE, G=DNI, D=CUENTA, E=OPERACIÓN
        String nombre = getCellString(headerRow.getCell(5));
        String dni = getCellString(headerRow.getCell(6));
        String cuenta = getCellString(headerRow.getCell(3));
        String operacion = getCellString(headerRow.getCell(4));

        if (nombre.isEmpty() && dni.isEmpty() && cuenta.isEmpty()) {
            throw new CarteraException("El archivo no tiene el formato esperado del Excel de cartera");
        }
    }

    private boolean isRowVacia(Row row) {
        // Una fila se considera vacía si no tiene NRO (col A), ni cuenta (col D), ni nombre (col F)
        String nro = getCellString(row.getCell(0));
        String cuenta = getCellString(row.getCell(3));
        String nombre = getCellString(row.getCell(5));
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
