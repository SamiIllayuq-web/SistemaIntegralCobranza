package com.startup.cobranza.gestion.entity;

public enum TipoGestion {
    LLAMADA("Llamada"),
    VISITA("Visita"),
    OBSERVACION("Observación"),
    COMPROMISO_PAGO("Compromiso de Pago");

    private final String label;

    TipoGestion(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
