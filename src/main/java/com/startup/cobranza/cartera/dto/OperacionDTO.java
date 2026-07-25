package com.startup.cobranza.cartera.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperacionDTO {

    // === ID Y CLAVES ===
    private Long id;
    private String cuenta;
    private String numeroOperacion;

    // === RELACIONES ===
    private Long clienteId;
    private String clienteNombre;
    private String clienteDni;
    private Long agenciaId;
    private String agenciaNombre;

    // === ABOGADO ===
    private String abogadoNombre;

    // === BANDERAS Y DATOS SIMPLES ===
    private String transferido;
    private String observaciones;
    private String situacion;
    private String moneda;
    private String busquedaBienes;

    // === DEUDA ===
    private BigDecimal deudaCap;
    private BigDecimal deudaTotal;

    // === DATOS JUDICIALES ===
    private String tipoProceso;
    private String tipoJuzgado;
    private String distritoJudicial;
    private String numeroJuzgado;
    private String numeroExpediente;
    private Boolean tieneIncidente;
    private BigDecimal montoDemandado;
    private String secretarioLegal;
    private String codigoExpedienteCautelar;
    private String detalleBienEmbargado;
    private String numeroPartida;
    private String tipoBienEmbargado;
    private String rango;

    // === ACREEDORES Y PREFERENCIA ===
    private String detalleAcreedores;
    private String tipoPreferente;

    // === MEDIDA CAUTELAR ===
    private BigDecimal montoMedidaCautelar;
    private String monedaMc;
    private String medidaCautelarEjecutada;
    private String fechaInscripcionEmbargo;
    private String fechaPresentacionTituloRrpp;
    private String asientoInscripcion;
    private String fechaPresentacionMc;

    // === ADMISIÓN E INADMISIÓN ===
    private String fechaInadmisible;
    private String fechaAdmision;
    private String comentario;
    private String fechaPresentacion;
    private String fechaInadmisible2;
    private String fechaAdmision2;

    // === AUDIENCIAS ===
    private String audienciaTipo;

    // === RESOLUCIONES Y EJECUTORIA ===
    private String fechaAutoFinal;
    private String fechaEjecutoriada;
    private String fechaNombramientoPeritos;
    private String fechaNombramientoMartillero;

    // === REMATES ===
    private String fechaRemate1;
    private String fechaRemate2;
    private String fechaRemate3;

    // === SEGUIMIENTO ===
    private String fechaProximoActoProcesal;
    private String comentarioProcesal;

    // === AUDITORÍA ===
    private String fechaCreacion;
    private String fechaActualizacion;
}
