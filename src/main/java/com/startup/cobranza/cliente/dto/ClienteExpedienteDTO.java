package com.startup.cobranza.cliente.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Resumen de un cliente para la vista /cartera/expedientes.
 * Muestra el cliente y sus conteos de expedientes Judicial / Castigada.
 */
@Data
@Builder
public class ClienteExpedienteDTO {

    private Long clienteId;
    private String nombreCompleto;
    private String dni;
    private Long agenciaId;
    private String agenciaNombre;

    /** Cantidad de operaciones con situacion = 'Judicial' */
    private long judiciales;

    /** Cantidad de operaciones con situacion = 'Castigada' */
    private long castigadas;

    /** Total de operaciones con numeroExpediente (Judicial + Castigada + otras situaciones) */
    private long total;
}
