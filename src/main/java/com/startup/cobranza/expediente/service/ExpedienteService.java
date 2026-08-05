package com.startup.cobranza.expediente.service;

import com.startup.cobranza.agencia.entity.Agencia;
import com.startup.cobranza.agencia.repository.AgenciaRepository;
import com.startup.cobranza.cliente.entity.Cliente;
import com.startup.cobranza.cliente.repository.ClienteRepository;
import com.startup.cobranza.empresa.entity.Empresa;
import com.startup.cobranza.empresa.repository.EmpresaRepository;
import com.startup.cobranza.expediente.entity.*;
import com.startup.cobranza.expediente.repository.*;
import com.startup.cobranza.operacion.entity.Operacion;
import com.startup.cobranza.operacion.repository.OperacionRepository;
import com.startup.cobranza.usuario.entity.Usuario;
import com.startup.cobranza.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExpedienteService {

    private static final Logger log = LoggerFactory.getLogger(ExpedienteService.class);

    private final ExpedienteRepository expedienteRepository;
    private final ExpedienteClienteRepository expedienteClienteRepository;
    private final BienEmbargadoRepository bienEmbargadoRepository;
    private final GestionProcesalRepository gestionProcesalRepository;
    private final ReporteMcRepository reporteMcRepository;
    private final EmpresaRepository empresaRepository;
    private final AgenciaRepository agenciaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final OperacionRepository operacionRepository;

    @Transactional
    public ResultadoImportacion importarExcelAvanceProcesal(
            MultipartFile archivo, Long empresaId, Long agenciaId, String nombreHoja, String usuario) {
        log.info("IMPORT: iniciar importacion archivo={} empresaId={} agenciaId={} hoja={}", archivo.getOriginalFilename(), empresaId, agenciaId, nombreHoja);

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));
        log.info("IMPORT: empresa={}", empresa.getNombre());

        Agencia agencia = null;
        if (agenciaId != null) {
            agencia = agenciaRepository.findById(agenciaId).orElse(null);
        }

        int total = 0;
        int creados = 0;
        int actualizados = 0;
        List<String> errores = new ArrayList<>();

        try (InputStream is = archivo.getInputStream()) {
            Workbook workbook;
            if (archivo.getOriginalFilename() != null && archivo.getOriginalFilename().endsWith(".xls")) {
                workbook = new HSSFWorkbook(is);
            } else {
                workbook = new XSSFWorkbook(is);
            }

            Sheet sheet = workbook.getSheet(nombreHoja);
            if (sheet == null) {
                throw new RuntimeException("Hoja '" + nombreHoja + "' no encontrada");
            }

            List<RowData> filas = parseSheetToRows(sheet);

            for (RowData row : filas) {
                try {
                    Expediente expediente = crearOActualizarExpediente(row, empresa, agencia);
                    if (expediente == null) {
                        continue; // fila sin numeroExpediente
                    }
                    total++;
                    if (expediente.getId() == null) {
                        creados++;
                    } else {
                        actualizados++;
                    }
                } catch (Exception e) {
                    errores.add("Fila " + row.nro + ": " + e.getMessage());
                }
            }

            workbook.close();
        } catch (Exception e) {
            throw new RuntimeException("Error al leer archivo: " + e.getMessage());
        }

        return new ResultadoImportacion(total, creados, actualizados, total - creados - actualizados, errores);
    }

    private List<RowData> parseSheetToRows(Sheet sheet) {
        List<RowData> filas = new ArrayList<>();
        int nrows = sheet.getLastRowNum() + 1;
        log.info("IMPORT: sheet '{}' tiene {} filas totales", sheet.getSheetName(), nrows);

        for (int i = 2; i < nrows; i++) {
            Row row = sheet.getRow(i);
            if (row == null || isRowEmpty(row)) continue;

            RowData rd = new RowData();
            rd.nro = (int) getNumeric(row.getCell(0));
            rd.abogado = getStr(row.getCell(1));
            rd.co = getStr(row.getCell(2));
            rd.cuenta = getStr(row.getCell(3));
            rd.operacion = getStr(row.getCell(4));
            rd.nombreCliente = getStr(row.getCell(5));
            rd.coTitularAval = getStr(row.getCell(6));
            rd.dni = getStr(row.getCell(7));
            rd.trans = getStr(row.getCell(8));
            rd.observacion = getStr(row.getCell(9));
            rd.situacion = getStr(row.getCell(10));
            rd.agencia = getStr(row.getCell(11));
            rd.moneda = getStr(row.getCell(12));
            rd.busquedaBienes = getStr(row.getCell(13));
            rd.deudaCap = getDecimal(row.getCell(14));
            rd.deudaTotal = getDecimal(row.getCell(15));
            rd.tipoProceso = getStr(row.getCell(16));
            rd.tipoJuzgado = getStr(row.getCell(17));
            rd.distritoJudicial = getStr(row.getCell(18));
            rd.numeroJuzgado = getStr(row.getCell(19));
            rd.numeroExpediente = getStr(row.getCell(20));
            rd.incidente = getStr(row.getCell(21)).trim().equalsIgnoreCase("SI") ? "SI" : "NO";
            rd.montoDemandado = getDecimal(row.getCell(22));
            rd.especialistaLegal = getStr(row.getCell(23));
            rd.codigoCautelar = getStr(row.getCell(24));
            rd.detalleBien = getStr(row.getCell(25));
            rd.partidaRegistral = getStr(row.getCell(26));
            rd.tipoBien = getStr(row.getCell(27));
            rd.rango = getStr(row.getCell(28));
            rd.detalleAcreedores = getStr(row.getCell(29));
            rd.tipoPreferencia = getStr(row.getCell(30));
            rd.montoMc = getDecimal(row.getCell(31));
            rd.monedaMc = getStr(row.getCell(32));
            rd.garantiaInscrita = getStr(row.getCell(33));
            rd.fechaInscripcion = getDate(row.getCell(34));
            rd.fechaPresentacionRrpp = getDate(row.getCell(35));
            rd.asientoInscripcion = getStr(row.getCell(36));
            rd.fechaPresentacionMc = getDate(row.getCell(37));
            rd.fechaInadmisible = getDate(row.getCell(38));
            rd.fechaAdmision = getDate(row.getCell(39));
            rd.comentarioMc = getStr(row.getCell(40));
            rd.fechaPresentacion2 = getDate(row.getCell(41));
            rd.fechaInadmisible2 = getDate(row.getCell(42));
            rd.fechaAdmision2 = getDate(row.getCell(43));
            rd.audiencia = getStr(row.getCell(44));
            rd.fechaAutoFinal = getDate(row.getCell(45));
            rd.fechaConsentimiento = getDate(row.getCell(46));
            rd.fechaIngresoEjecucion = getDate(row.getCell(47));
            rd.fechaNombramientoMartillero = getDate(row.getCell(48));
            rd.fechaRemate1 = getDate(row.getCell(49));
            rd.fechaRemate2 = getDate(row.getCell(50));
            rd.fechaRemate3 = getDate(row.getCell(51));
            rd.observacionesActos = getStr(row.getCell(52));
            rd.comentarioGeneral = getStr(row.getCell(53));

            // Log TODAS las columnas para debug del mapeo
            StringBuilder allCols = new StringBuilder();
            for (int c = 0; c < 54; c++) {
                Cell cell = row.getCell(c);
                String v = getStr(cell);
                if (!v.isEmpty()) allCols.append("col").append(c).append("=").append(v).append("|");
            }
            log.info("IMPORT RAW fila {}: {}", i, allCols.toString());

            // Log de debug: nombre, dni, cuenta, exp
            log.info("IMPORT PARSE: fila {} nro={} nombre=[{}] dni=[{}] cuenta=[{}] operacion=[{}] exp=[{}] situacion=[{}]",
                    i, rd.nro, rd.nombreCliente, rd.dni, rd.cuenta, rd.operacion, rd.numeroExpediente, rd.situacion);

            filas.add(rd);
        }
        log.info("IMPORT: parseadas {} filas con datos", filas.size());
        return filas;
    }

    private Expediente crearOActualizarExpediente(RowData rd, Empresa empresa, Agencia agencia) {
        Expediente expediente;
        boolean esNuevo = false;

        if (rd.numeroExpediente == null || rd.numeroExpediente.isEmpty()) {
            log.warn("IMPORT SKIP: fila {} sin numeroExpediente (nombre=[{}] dni=[{}] cuenta=[{}])",
                    rd.nro, rd.nombreCliente, rd.dni, rd.cuenta);
            return null; // saltear fila sin numeroExpediente
        }

        expediente = expedienteRepository.findByNumeroExpediente(rd.numeroExpediente)
                .orElse(null);

        if (expediente == null) {
            expediente = Expediente.builder()
                    .empresa(empresa)
                    .agencia(agencia)
                    .numeroExpediente(rd.numeroExpediente)
                    .situacion(rd.situacion)
                    .tipoProceso(rd.tipoProceso)
                    .tipoJuzgado(rd.tipoJuzgado)
                    .distritoJudicial(rd.distritoJudicial)
                    .numeroJuzgado(rd.numeroJuzgado)
                    .expedienteCautelarCodigo(rd.codigoCautelar)
                    .incidente(rd.incidente != null && rd.incidente.equalsIgnoreCase("SI"))
                    .montoDemandado(rd.montoDemandado)
                    .especialistaLegal(rd.especialistaLegal)
                    .observacion(rd.observacion)
                    .comentarioGeneral(rd.comentarioGeneral)
                    .activo(true)
                    .build();
            expediente = expedienteRepository.save(expediente);
            esNuevo = true;
        } else {
            expediente.setSituacion(rd.situacion);
            expediente.setTipoProceso(rd.tipoProceso);
            expediente.setTipoJuzgado(rd.tipoJuzgado);
            expediente.setDistritoJudicial(rd.distritoJudicial);
            expediente.setNumeroJuzgado(rd.numeroJuzgado);
            expediente.setExpedienteCautelarCodigo(rd.codigoCautelar);
            expediente.setIncidente(rd.incidente != null && rd.incidente.equalsIgnoreCase("SI"));
            expediente.setMontoDemandado(rd.montoDemandado);
            expediente.setEspecialistaLegal(rd.especialistaLegal);
            expediente.setObservacion(rd.observacion);
            expediente.setComentarioGeneral(rd.comentarioGeneral);
        }

        // ExpedienteCliente: buscar por dni+cuenta+operacion para linkear sin duplicar Cliente
        if (rd.nombreCliente != null && !rd.nombreCliente.isEmpty()) {
            ExpedienteCliente clienteExistente = null;
            List<ExpedienteCliente> existentes = expedienteClienteRepository.findByExpedienteId(expediente.getId());
            for (ExpedienteCliente ec : existentes) {
                if (eq(ec.getDni(), rd.dni) && eq(ec.getCuenta(), rd.cuenta) && eq(ec.getOperacion(), rd.operacion)) {
                    clienteExistente = ec;
                    break;
                }
            }

            if (clienteExistente != null) {
                clienteExistente.setNombreCompleto(rd.nombreCliente);
                clienteExistente.setCoTitularAval(rd.coTitularAval);
                clienteExistente.setTrans(rd.trans);
                clienteExistente.setMoneda(rd.moneda);
                clienteExistente.setDeudaCapital(rd.deudaCap);
                clienteExistente.setDeudaTotal(rd.deudaTotal);
                clienteExistente.setBusquedaBienes(rd.busquedaBienes);
                clienteExistente.setObservacion(rd.observacion);
                expedienteClienteRepository.save(clienteExistente);
            } else {
                // buscar o crear Cliente por DNI
                Cliente cliente = null;
                if (rd.dni != null && !rd.dni.isEmpty()) {
                    log.info("IMPORT: buscando cliente dni=[{}]", rd.dni);
                    cliente = clienteRepository.findByDni(rd.dni).orElse(null);
                    if (cliente != null) {
                        log.info("IMPORT: Cliente encontrado id={}", cliente.getId());
                    }
                }
                if (cliente == null) {
                    log.info("IMPORT: creando Cliente nombre={} dni={}", rd.nombreCliente, rd.dni);
                    cliente = Cliente.builder()
                            .nombreCompleto(rd.nombreCliente)
                            .dni(rd.dni)
                            .activo(true)
                            .build();
                    cliente = clienteRepository.save(cliente);
                    log.info("IMPORT: Cliente guardado id={}", cliente.getId());
                }

                // Buscar o crear Operacion para asociar con datos de la fila
                Operacion operacion = null;
                if (rd.dni != null && !rd.dni.isEmpty() && rd.cuenta != null && rd.operacion != null) {
                    log.info("IMPORT: buscando operacion empresaId={} cuenta=[{}] operacion=[{}]",
                            empresa.getId(), rd.cuenta, rd.operacion);
                    operacion = operacionRepository
                            .findByEmpresaIdAndCuentaAndNumeroOperacion(empresa.getId(), rd.cuenta, rd.operacion)
                            .orElse(null);
                    if (operacion != null) {
                        log.info("IMPORT: Operacion encontrada id={}", operacion.getId());
                        operacion.setMontoCapital(rd.deudaCap);
                        operacion.setMontoTotal(rd.deudaTotal);
                        operacion.setObservacion(rd.observacion);
                        if (agencia != null) operacion.setAgencia(agencia);
                        operacion = operacionRepository.save(operacion);
                        log.info("IMPORT: Operacion actualizada id={}", operacion.getId());
                    }
                }
                if (operacion == null) {
                    log.info("IMPORT: creando Operacion cuenta={} operacion={}", rd.cuenta, rd.operacion);
                    operacion = Operacion.builder()
                            .cliente(cliente)
                            .empresa(empresa)
                            .agencia(agencia)
                            .cuenta(rd.cuenta)
                            .numeroOperacion(rd.operacion)
                            .montoCapital(rd.deudaCap)
                            .montoTotal(rd.deudaTotal)
                            .observacion(rd.observacion)
                            .activo(true)
                            .build();
                    operacion = operacionRepository.save(operacion);
                    log.info("IMPORT: Operacion guardada id={}", operacion.getId());
                }

                ExpedienteCliente ec = ExpedienteCliente.builder()
                        .expediente(expediente)
                        .cliente(cliente)
                        .tipo("TITULAR")
                        .dni(rd.dni)
                        .cuenta(rd.cuenta)
                        .operacion(rd.operacion)
                        .coTitularAval(rd.coTitularAval)
                        .trans(rd.trans)
                        .moneda(rd.moneda)
                        .deudaCapital(rd.deudaCap)
                        .deudaTotal(rd.deudaTotal)
                        .busquedaBienes(rd.busquedaBienes)
                        .observacion(rd.observacion)
                        .build();
                expedienteClienteRepository.save(ec);
            }
        }

        // BienEmbargado: solo crear si no existe uno con la misma partida registral bajo este expediente
        if (rd.detalleBien != null && !rd.detalleBien.isEmpty()) {
            boolean bienYaExiste = false;
            if (!esNuevo) {
                List<BienEmbargado> bienesExistentes = bienEmbargadoRepository.findByExpedienteId(expediente.getId());
                for (BienEmbargado be : bienesExistentes) {
                    if (eq(be.getPartidaRegistral(), rd.partidaRegistral)) {
                        bienYaExiste = true;
                        break;
                    }
                }
            }

            if (!bienYaExiste) {
                BienEmbargado bien = BienEmbargado.builder()
                        .expediente(expediente)
                        .tipoBien(rd.tipoBien)
                        .partidaRegistral(rd.partidaRegistral)
                        .detalleGarantia(rd.detalleBien)
                        .direccion(rd.detalleBien)
                        .rango(rd.rango)
                        .detalleAcreedores(rd.detalleAcreedores)
                        .tipoPreferencia(rd.tipoPreferencia)
                        .montoMc(rd.montoMc)
                        .monedaMc(rd.monedaMc)
                        .garantiaInscrita(rd.garantiaInscrita)
                        .fechaInscripcion(rd.fechaInscripcion)
                        .fechaPresentacionRrpp(rd.fechaPresentacionRrpp)
                        .asientoInscripcion(rd.asientoInscripcion)
                        .fechaGeneracionMc(rd.fechaPresentacionMc)
                        .build();
                bienEmbargadoRepository.save(bien);
            }
        }

        // Gestiones: guardar siempre (son fechas que pueden actualizarse)
        expediente = expedienteRepository.save(expediente); // guardar cambios del expediente
        guardarGestionSiHayFecha(expediente, "MC", "PRESENTACION", rd.fechaPresentacionMc, rd.comentarioMc);
        guardarGestionSiHayFecha(expediente, "MC", "INADMISIBLE", rd.fechaInadmisible, null);
        guardarGestionSiHayFecha(expediente, "MC", "ADMISION", rd.fechaAdmision, null);
        guardarGestionSiHayFecha(expediente, "PRINCIPAL", "PRESENTACION", rd.fechaPresentacion2, null);
        guardarGestionSiHayFecha(expediente, "PRINCIPAL", "INADMISIBLE", rd.fechaInadmisible2, null);
        guardarGestionSiHayFecha(expediente, "PRINCIPAL", "ADMISION", rd.fechaAdmision2, null);
        guardarGestionSiHayFecha(expediente, "AUDIENCIA", "UNICA", null, rd.audiencia);
        guardarGestionSiHayFecha(expediente, "EJECUCION", "AUTO_FINAL", rd.fechaAutoFinal, null);
        guardarGestionSiHayFecha(expediente, "EJECUCION", "CONSENTIMIENTO", rd.fechaConsentimiento, null);
        guardarGestionSiHayFecha(expediente, "EJECUCION", "INGRESO", rd.fechaIngresoEjecucion, null);
        guardarGestionSiHayFecha(expediente, "REMATE", "NOMBRAMIENTO_MARTILLERO", rd.fechaNombramientoMartillero, null);
        guardarGestionSiHayFecha(expediente, "REMATE", "REMATE_1", rd.fechaRemate1, null);
        guardarGestionSiHayFecha(expediente, "REMATE", "REMATE_2", rd.fechaRemate2, null);
        guardarGestionSiHayFecha(expediente, "REMATE", "REMATE_3", rd.fechaRemate3, null);

        return expediente;
    }

    private boolean eq(String a, String b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.trim().equals(b.trim());
    }

    private void guardarGestionSiHayFecha(Expediente expediente, String tipo, String etapa, LocalDate fecha, String observacion) {
        if (fecha != null) {
            GestionProcesal g = GestionProcesal.builder()
                    .expediente(expediente)
                    .tipoGestion(tipo)
                    .etapa(etapa)
                    .fecha(fecha)
                    .observacion(observacion)
                    .build();
            gestionProcesalRepository.save(g);
        }
    }

    public byte[] generarReporteMayoMc(Long empresaId, String mes, Integer anio, String usuario) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

        List<Expediente> expedientes = expedienteRepository.findActivosPorEmpresa(empresaId);
        if (expedientes.isEmpty()) {
            throw new RuntimeException("No hay expedientes para la empresa seleccionada");
        }

        // Pre-fetch all clients and bienes to avoid N+1
        List<Long> expIds = expedientes.stream().map(Expediente::getId).toList();
        List<ExpedienteCliente> allClientes = expedienteClienteRepository.findByExpedienteIdIn(expIds);
        List<BienEmbargado> allBienes = bienEmbargadoRepository.findByExpedienteIdIn(expIds);

        // Group by expediente_id for O(1) lookup
        Map<Long, List<ExpedienteCliente>> clientesPorExp = allClientes.stream()
                .collect(java.util.stream.Collectors.groupingBy(ec -> ec.getExpediente().getId()));
        Map<Long, List<BienEmbargado>> bienesPorExp = allBienes.stream()
                .collect(java.util.stream.Collectors.groupingBy(be -> be.getExpediente().getId()));

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("MAYO MC");

            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat(workbook.createDataFormat().getFormat("dd.mm.yyyy"));

            String[] headers = {
                    "NRO", "CUENTA Y OPERACIÓN", "ZONA", "AGENCIA", "ANALISTA", "CLIENTE", "DNI",
                    "CUENTA", "OPERACIÓN", "TIPO CREDITO", "ABOGADO", "RANGO", "TIPO MC",
                    "FECHA INSCRIPCION RRPP", "NRO/ FICHA REGISTRAL O PARTIDA", "MONEDA DE LA CARGA",
                    "MONTO DE LA CARGA", "DIRECCIÓN DEL INMUEBLE EMBARGADO", "DISTRITO DEL INMUEBLE EMBARGADO",
                    "PROVINCIA DEL INMUEBLE EMBARGADO", "DEPARTAMENTO DEL INMUEBLE EMBARGADO",
                    "CAPITAL S/", "MONTO TOTAL DEUDA S/", "ANALISTA SENIOR", "VALOR DE TASACION EN SOLES",
                    "TITULAR DEL PREDIO"
            };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (Expediente exp : expedientes) {
                List<ExpedienteCliente> clientes = clientesPorExp.getOrDefault(exp.getId(), List.of());
                List<BienEmbargado> bienes = bienesPorExp.getOrDefault(exp.getId(), List.of());

                // Si no hay bienes, crear una fila por cliente con bien null
                // Si hay bienes, crear fila por cada par (cliente, bien)
                for (ExpedienteCliente cli : clientes) {
                    if (bienes.isEmpty()) {
                        Row row = crearRowMayoMc(sheet, rowNum++, exp, cli, null, dateStyle);
                    } else {
                        for (BienEmbargado bien : bienes) {
                            Row row = crearRowMayoMc(sheet, rowNum++, exp, cli, bien, dateStyle);
                        }
                    }
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ReporteMc reporte = ReporteMc.builder()
                    .empresa(empresa)
                    .nombreArchivo("MAYO MC " + mes + " " + anio + ".xlsx")
                    .mes(mes)
                    .anio(anio)
                    .build();
            if (usuario != null) {
                usuarioRepository.findByUsername(usuario).ifPresent(reporte::setGeneradoPor);
            }
            reporteMcRepository.save(reporte);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar reporte: " + e.getMessage());
        }
    }

    private Row crearRowMayoMc(Sheet sheet, int rowNum, Expediente exp, ExpedienteCliente cli, BienEmbargado bien, CellStyle dateStyle) {
        Row row = sheet.createRow(rowNum);

        set(row, 0, (long) rowNum);
        // Columna B: Cuenta + Operacion concatenados
        set(row, 1, nvl(cli.getCuenta()) + nvl(cli.getOperacion()));
        // Columna C: ZONA = distrito judicial
        set(row, 2, exp.getDistritoJudicial());
        // Columna D: AGENCIA = nombre de agencia
        set(row, 3, exp.getAgencia() != null ? exp.getAgencia().getNombre() : "");
        // Columna E: ANALISTA = abogado
        set(row, 4, exp.getAbogado() != null ? exp.getAbogado().getNombreCompleto() : "");
        // Columna F: CLIENTE
        set(row, 5, cli.getNombreCompleto());
        // Columna G: DNI
        set(row, 6, cli.getDni());
        // Columna H: CUENTA
        set(row, 7, cli.getCuenta());
        // Columna I: OPERACION
        set(row, 8, cli.getOperacion());
        // Columna J: TIPO CREDITO = situacion del expediente
        set(row, 9, exp.getSituacion());
        // Columna K: ABOGADO = especialista legal (secretario)
        set(row, 10, exp.getEspecialistaLegal());
        // Columna L: RANGO
        set(row, 11, bien != null ? nvl(bien.getRango()) : "");
        // Columna M: TIPO MC
        set(row, 12, "PROPIEDAD");
        // Columna N: FECHA INSCRIPCION RRPP
        if (bien != null && bien.getFechaInscripcion() != null) {
            Cell c = row.createCell(13);
            c.setCellValue(java.time.LocalDateTime.of(bien.getFechaInscripcion(), java.time.LocalTime.NOON));
            c.setCellStyle(dateStyle);
        }
        // Columna O: PARTIDA REGISTRAL
        set(row, 14, bien != null ? nvl(bien.getPartidaRegistral()) : "");
        // Columna P: MONEDA MC
        set(row, 15, bien != null ? nvl(bien.getMonedaMc()) : "");
        // Columna Q: MONTO DE LA CARGA (monto MC)
        if (bien != null && bien.getMontoMc() != null) {
            set(row, 16, bien.getMontoMc());
        }
        // Columna R: DIRECCION DEL INMUEBLE
        set(row, 17, bien != null ? nvl(bien.getDetalleGarantia()) : "");
        // Columna S: DISTRITO
        set(row, 18, bien != null ? nvl(bien.getDistrito()) : "");
        // Columna T: PROVINCIA
        set(row, 19, bien != null ? nvl(bien.getProvincia()) : "");
        // Columna U: DEPARTAMENTO
        set(row, 20, bien != null ? nvl(bien.getDepartamento()) : "");
        // Columna V: CAPITAL
        set(row, 21, cli.getDeudaCapital());
        // Columna W: DEUDA TOTAL
        set(row, 22, cli.getDeudaTotal());
        // Columna X: ANALISTA SENIOR = especialista legal
        set(row, 23, exp.getEspecialistaLegal());
        // Columna Y: VALOR DE TASACION = monto MC
        if (bien != null && bien.getMontoMc() != null) {
            set(row, 24, bien.getMontoMc());
        }
        // Columna Z: TITULAR DEL PREDIO
        set(row, 25, bien != null ? nvl(bien.getTitularPredio()) : "");

        return row;
    }

    private String nvl(String s) {
        return s != null ? s : "";
    }

    private void set(Row row, int col, Object val) {
        if (val == null) return;
        Cell cell = row.createCell(col);
        if (val instanceof String) {
            cell.setCellValue((String) val);
        } else if (val instanceof BigDecimal) {
            cell.setCellValue(((BigDecimal) val).doubleValue());
        } else if (val instanceof Long) {
            cell.setCellValue((Long) val);
        } else if (val instanceof Double) {
            cell.setCellValue((Double) val);
        }
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) return true;
        for (int i = 0; i < 54; i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String v = getStr(cell);
                if (!v.isEmpty()) return false;
            }
        }
        return true;
    }

    private String getStr(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    private double getNumeric(Cell cell) {
        if (cell == null) return 0;
        try {
            return cell.getCellType() == CellType.NUMERIC ? cell.getNumericCellValue() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    private BigDecimal getDecimal(Cell cell) {
        if (cell == null) return BigDecimal.ZERO;
        try {
            return switch (cell.getCellType()) {
                case NUMERIC -> BigDecimal.valueOf(cell.getNumericCellValue());
                case STRING -> {
                    String v = cell.getStringCellValue().trim();
                    yield v.isEmpty() ? BigDecimal.ZERO : new BigDecimal(v.replace(",", ""));
                }
                default -> BigDecimal.ZERO;
            };
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private LocalDate getDate(Cell cell) {
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate();
                }
                return null;
            }
            if (cell.getCellType() == CellType.STRING) {
                String v = cell.getStringCellValue().trim();
                if (v.isEmpty() || v.equalsIgnoreCase("NINGUNO")) return null;
                for (DateTimeFormatter f : List.of(
                        DateTimeFormatter.ofPattern("dd.MM.yyyy"),
                        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd")
                )) {
                    try { return LocalDate.parse(v, f); } catch (Exception ignored) {}
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    // ===================== CRUD EXPEDIENTE =====================

    @Transactional
    public Expediente guardarExpediente(Expediente expediente, Long empresaId, Long agenciaId, Long abogadoId) {
        if (empresaId != null) {
            Empresa empresa = empresaRepository.findById(empresaId)
                    .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));
            expediente.setEmpresa(empresa);
        }
        if (agenciaId != null) {
            agenciaRepository.findById(agenciaId).ifPresent(expediente::setAgencia);
        }
        if (abogadoId != null) {
            usuarioRepository.findById(abogadoId).ifPresent(expediente::setAbogado);
        }
        return expedienteRepository.save(expediente);
    }

    public Expediente obtenerExpediente(Long id) {
        return expedienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expediente no encontrado: " + id));
    }

    @Transactional
    public void eliminarExpediente(Long id) {
        Expediente exp = obtenerExpediente(id);
        exp.setActivo(false);
        expedienteRepository.save(exp);
    }

    // ===================== CRUD EXPEDIENTE CLIENTE =====================

    @Transactional
    public ExpedienteCliente agregarCliente(Long expedienteId, ExpedienteCliente cliente) {
        Expediente exp = obtenerExpediente(expedienteId);
        cliente.setExpediente(exp);
        return expedienteClienteRepository.save(cliente);
    }

    @Transactional
    public ExpedienteCliente actualizarCliente(Long clienteId, ExpedienteCliente datos) {
        ExpedienteCliente cli = expedienteClienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + clienteId));
        if (datos.getTipo() != null) cli.setTipo(datos.getTipo());
        if (datos.getNombreCompleto() != null) cli.setNombreCompleto(datos.getNombreCompleto());
        if (datos.getDni() != null) cli.setDni(datos.getDni());
        if (datos.getCuenta() != null) cli.setCuenta(datos.getCuenta());
        if (datos.getOperacion() != null) cli.setOperacion(datos.getOperacion());
        if (datos.getCoTitularAval() != null) cli.setCoTitularAval(datos.getCoTitularAval());
        if (datos.getTrans() != null) cli.setTrans(datos.getTrans());
        if (datos.getMoneda() != null) cli.setMoneda(datos.getMoneda());
        if (datos.getDeudaCapital() != null) cli.setDeudaCapital(datos.getDeudaCapital());
        if (datos.getDeudaTotal() != null) cli.setDeudaTotal(datos.getDeudaTotal());
        if (datos.getBusquedaBienes() != null) cli.setBusquedaBienes(datos.getBusquedaBienes());
        if (datos.getObservacion() != null) cli.setObservacion(datos.getObservacion());
        return expedienteClienteRepository.save(cli);
    }

    @Transactional
    public void eliminarCliente(Long clienteId) {
        expedienteClienteRepository.deleteById(clienteId);
    }

    // ===================== CRUD BIEN EMBARGADO =====================

    @Transactional
    public BienEmbargado agregarBien(Long expedienteId, BienEmbargado bien) {
        Expediente exp = obtenerExpediente(expedienteId);
        bien.setExpediente(exp);
        return bienEmbargadoRepository.save(bien);
    }

    @Transactional
    public BienEmbargado actualizarBien(Long bienId, BienEmbargado datos) {
        BienEmbargado bien = bienEmbargadoRepository.findById(bienId)
                .orElseThrow(() -> new RuntimeException("Bien no encontrado: " + bienId));
        if (datos.getTipoBien() != null) bien.setTipoBien(datos.getTipoBien());
        if (datos.getPartidaRegistral() != null) bien.setPartidaRegistral(datos.getPartidaRegistral());
        if (datos.getDetalleGarantia() != null) bien.setDetalleGarantia(datos.getDetalleGarantia());
        if (datos.getDireccion() != null) bien.setDireccion(datos.getDireccion());
        if (datos.getDistrito() != null) bien.setDistrito(datos.getDistrito());
        if (datos.getProvincia() != null) bien.setProvincia(datos.getProvincia());
        if (datos.getDepartamento() != null) bien.setDepartamento(datos.getDepartamento());
        if (datos.getGarantiaInscrita() != null) bien.setGarantiaInscrita(datos.getGarantiaInscrita());
        if (datos.getFechaInscripcion() != null) bien.setFechaInscripcion(datos.getFechaInscripcion());
        if (datos.getFechaPresentacionRrpp() != null) bien.setFechaPresentacionRrpp(datos.getFechaPresentacionRrpp());
        if (datos.getAsientoInscripcion() != null) bien.setAsientoInscripcion(datos.getAsientoInscripcion());
        if (datos.getMontoMc() != null) bien.setMontoMc(datos.getMontoMc());
        if (datos.getMonedaMc() != null) bien.setMonedaMc(datos.getMonedaMc());
        if (datos.getRango() != null) bien.setRango(datos.getRango());
        if (datos.getDetalleAcreedores() != null) bien.setDetalleAcreedores(datos.getDetalleAcreedores());
        if (datos.getTipoPreferencia() != null) bien.setTipoPreferencia(datos.getTipoPreferencia());
        if (datos.getTitularPredio() != null) bien.setTitularPredio(datos.getTitularPredio());
        if (datos.getFechaGeneracionMc() != null) bien.setFechaGeneracionMc(datos.getFechaGeneracionMc());
        return bienEmbargadoRepository.save(bien);
    }

    @Transactional
    public void eliminarBien(Long bienId) {
        bienEmbargadoRepository.deleteById(bienId);
    }

    // ===================== CRUD GESTION PROCESAL =====================

    @Transactional
    public GestionProcesal agregarGestion(Long expedienteId, GestionProcesal gestion) {
        Expediente exp = obtenerExpediente(expedienteId);
        gestion.setExpediente(exp);
        return gestionProcesalRepository.save(gestion);
    }

    @Transactional
    public GestionProcesal actualizarGestion(Long gestionId, GestionProcesal datos) {
        GestionProcesal g = gestionProcesalRepository.findById(gestionId)
                .orElseThrow(() -> new RuntimeException("Gestión no encontrada: " + gestionId));
        if (datos.getTipoGestion() != null) g.setTipoGestion(datos.getTipoGestion());
        if (datos.getEtapa() != null) g.setEtapa(datos.getEtapa());
        if (datos.getFecha() != null) g.setFecha(datos.getFecha());
        if (datos.getObservacion() != null) g.setObservacion(datos.getObservacion());
        return gestionProcesalRepository.save(g);
    }

    @Transactional
    public void eliminarGestion(Long gestionId) {
        gestionProcesalRepository.deleteById(gestionId);
    }

    // ===================== REPORTES =====================

    public record ResultadoImportacion(
            int total, int creados, int actualizados, int fallidos, List<String> errores) {}

    private static class RowData {
        int nro;
        String abogado, co, cuenta, operacion, nombreCliente, coTitularAval, dni, trans;
        String observacion, situacion, agencia, moneda, busquedaBienes;
        BigDecimal deudaCap, deudaTotal;
        String tipoProceso, tipoJuzgado, distritoJudicial, numeroJuzgado, numeroExpediente;
        String incidente;
        BigDecimal montoDemandado;
        String especialistaLegal, codigoCautelar, detalleBien, partidaRegistral, tipoBien;
        String rango, detalleAcreedores, tipoPreferencia;
        BigDecimal montoMc;
        String monedaMc, garantiaInscrita;
        LocalDate fechaInscripcion, fechaPresentacionRrpp;
        String asientoInscripcion;
        LocalDate fechaPresentacionMc, fechaInadmisible, fechaAdmision;
        String comentarioMc;
        LocalDate fechaPresentacion2, fechaInadmisible2, fechaAdmision2;
        String audiencia;
        LocalDate fechaAutoFinal, fechaConsentimiento, fechaIngresoEjecucion;
        LocalDate fechaNombramientoMartillero;
        LocalDate fechaRemate1, fechaRemate2, fechaRemate3;
        String observacionesActos, comentarioGeneral;
    }
}
