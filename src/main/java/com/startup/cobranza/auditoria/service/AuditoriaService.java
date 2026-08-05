package com.startup.cobranza.auditoria.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.startup.cobranza.auditoria.entity.AuditoriaEvento;
import com.startup.cobranza.auditoria.repository.AuditoriaEventoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class AuditoriaService {

    public static final String TIPO_CLIENTE_UPDATE = "CLIENTE_UPDATE";
    public static final String TIPO_OPERACION_UPDATE = "OPERACION_UPDATE";
    public static final String TIPO_IMPORT_OK = "IMPORT_OK";
    public static final String TIPO_IMPORT_ERROR = "IMPORT_ERROR";
    public static final String TIPO_EXPORT_OK = "EXPORT_OK";

    private final AuditoriaEventoRepository repository;
    private final ObjectMapper objectMapper;

    public AuditoriaService(AuditoriaEventoRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void registrar(String tipo, String objetoTipo, Long objetoId,
                          String usuario, Map<String, Object> datos) {
        try {
            String payload = objectMapper.writeValueAsString(datos);
            AuditoriaEvento evento = AuditoriaEvento.builder()
                    .tipo(tipo)
                    .objetoTipo(objetoTipo)
                    .objetoId(objetoId)
                    .usuario(usuario)
                    .payload(payload)
                    .build();
            repository.save(evento);
        } catch (Exception e) {
            // Auditoría no debe romper el flujo principal
        }
    }

    @Transactional
    public void registrar(String tipo, String objetoTipo, Long objetoId,
                          String usuario, String descripcion) {
        AuditoriaEvento evento = AuditoriaEvento.builder()
                .tipo(tipo)
                .objetoTipo(objetoTipo)
                .objetoId(objetoId)
                .usuario(usuario)
                .descripcion(descripcion)
                .build();
        repository.save(evento);
    }
}
