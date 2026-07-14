package com.startup.cobranza.cartera.service;

import com.startup.cobranza.agencia.entity.Agencia;
import com.startup.cobranza.agencia.repository.AgenciaRepository;
import com.startup.cobranza.cartera.dto.ImportacionDTO;
import com.startup.cobranza.cartera.entity.Importacion;
import com.startup.cobranza.cartera.exception.CarteraException;
import com.startup.cobranza.cartera.repository.ImportacionRepository;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarteraService {

    private final ImportacionRepository importacionRepository;
    private final ClienteRepository clienteRepository;
    private final EmpresaRepository empresaRepository;
    private final AgenciaRepository agenciaRepository;

    private static final String[] COLUMNAS_ESPERADAS = {
            "NOMBRE", "DNI", "NUMERO_CUENTA", "NUMERO_OPERACION",
            "DEUDA_CAPITAL", "DEUDA_TOTAL", "TELEFONO", "DIRECCION", "ESTADO"
    };

    @Transactional
    public ImportacionDTO importarExcel(MultipartFile archivo, Long empresaId, Long agenciaId, String usuario) {
        if (archivo.isEmpty()) {
            throw new CarteraException("El archivo está vacío");
        }

        String nombreOriginal = archivo.getOriginalFilename();
        if (nombreOriginal == null || (!nombreOriginal.endsWith(".xlsx") && !nombreOriginal.endsWith(".xls"))) {
            throw new CarteraException("Solo se permiten archivos Excel (.xlsx o .xls)");
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

            validarEncabezados(headerRow);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                total++;
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) continue;

                try {
                    Cliente cliente = parseRow(row, empresa, agencia);
                    clienteRepository.save(cliente);
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

    private void validarEncabezados(Row headerRow) {
        for (int i = 0; i < COLUMNAS_ESPERADAS.length; i++) {
            Cell cell = headerRow.getCell(i);
            String valor = getCellValueAsString(cell);
            if (!COLUMNAS_ESPERADAS[i].equalsIgnoreCase(valor.trim())) {
                throw new CarteraException(
                        "Columna esperada en posición " + (i + 1) + ": " + COLUMNAS_ESPERADAS[i] + ", encontrada: " + valor);
            }
        }
    }

    private Cliente parseRow(Row row, Empresa empresa, Agencia agencia) {
        return Cliente.builder()
                .nombreCompleto(getCellValueAsString(row.getCell(0)))
                .dni(getCellValueAsString(row.getCell(1)))
                .numeroCuenta(getCellValueAsString(row.getCell(2)))
                .numeroOperacion(getCellValueAsString(row.getCell(3)))
                .deudaCapital(parseBigDecimal(row.getCell(4)))
                .deudaTotal(parseBigDecimal(row.getCell(5)))
                .telefono(getCellValueAsString(row.getCell(6)))
                .direccion(getCellValueAsString(row.getCell(7)))
                .estadoGestion(getCellValueAsString(row.getCell(8)))
                .empresa(empresa)
                .agencia(agencia)
                .activo(true)
                .build();
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    private BigDecimal parseBigDecimal(Cell cell) {
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

    private boolean isRowEmpty(Row row) {
        for (int i = 0; i < COLUMNAS_ESPERADAS.length; i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String val = getCellValueAsString(cell);
                if (!val.isEmpty()) return false;
            }
        }
        return true;
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
