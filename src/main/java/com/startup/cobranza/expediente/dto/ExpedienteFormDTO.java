package com.startup.cobranza.expediente.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ExpedienteFormDTO {
    private String numeroExpediente;
    private String situacion;
    private String tipoProceso;
    private String tipoJuzgado;
    private String distritoJudicial;
    private String numeroJuzgado;
    private String incidente;
    private BigDecimal montoDemandado;
    private String especialistaLegal;
    private String etapaProcesal;
    private String observacion;
    private String comentarioGeneral;
    private Long empresaId;
    private Long agenciaId;
    private Long abogadoId;
}
