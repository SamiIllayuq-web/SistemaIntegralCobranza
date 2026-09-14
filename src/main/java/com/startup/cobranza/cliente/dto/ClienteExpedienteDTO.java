package com.startup.cobranza.cliente.dto;

import lombok.Builder;
import lombok.Data;

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

    /**
     * Constructor usado por JPQL constructor expression.
     * Orden: clienteId, nombreCompleto, dni, agenciaId, agenciaNombre, judiciales, castigadas, total
     */
    public ClienteExpedienteDTO(Long clienteId, String nombreCompleto, String dni,
                                Long agenciaId, String agenciaNombre,
                                Long judiciales, Long castigadas, Long total) {
        this.clienteId = clienteId;
        this.nombreCompleto = nombreCompleto;
        this.dni = dni;
        this.agenciaId = agenciaId;
        this.agenciaNombre = agenciaNombre;
        this.judiciales = judiciales != null ? judiciales : 0L;
        this.castigadas = castigadas != null ? castigadas : 0L;
        this.total = total != null ? total : 0L;
    }

    // Default constructor para Jackson/Lombok
    public ClienteExpedienteDTO() {}
}
