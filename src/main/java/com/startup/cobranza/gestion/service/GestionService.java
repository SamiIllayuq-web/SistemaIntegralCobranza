package com.startup.cobranza.gestion.service;

import com.startup.cobranza.cliente.entity.Cliente;
import com.startup.cobranza.cliente.repository.ClienteRepository;
import com.startup.cobranza.gestion.dto.GestionDTO;
import com.startup.cobranza.gestion.dto.GestionFormDTO;
import com.startup.cobranza.gestion.entity.Gestion;
import com.startup.cobranza.gestion.exception.GestionException;
import com.startup.cobranza.gestion.repository.GestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GestionService {

    private final GestionRepository gestionRepository;
    private final ClienteRepository clienteRepository;

    @Transactional
    public GestionDTO registrar(Long clienteId, GestionFormDTO form, String usuario) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new GestionException("Cliente no encontrado"));

        if (form.getTipo() == null) {
            throw new GestionException("El tipo de gestión es obligatorio");
        }

        // Validaciones para compromiso de pago
        if (form.getTipo().name().equals("COMPROMISO_PAGO")) {
            if (form.getMontoCompromiso() == null || form.getMontoCompromiso().signum() <= 0) {
                throw new GestionException("El monto del compromiso es obligatorio para compromisos de pago");
            }
            if (form.getFechaCompromiso() == null) {
                throw new GestionException("La fecha del compromiso es obligatoria para compromisos de pago");
            }
        }

        Gestion gestion = Gestion.builder()
                .tipo(form.getTipo())
                .fechaGestion(form.getFechaGestion())
                .observaciones(form.getObservaciones())
                .montoCompromiso(form.getMontoCompromiso())
                .fechaCompromiso(form.getFechaCompromiso())
                .cliente(cliente)
                .usuarioRegistra(usuario)
                .build();

        Gestion saved = gestionRepository.save(gestion);

        return toDTO(saved);
    }

    public List<GestionDTO> listarPorCliente(Long clienteId) {
        return gestionRepository.findByClienteIdOrderByFechaGestionDesc(clienteId).stream()
                .map(this::toDTO)
                .toList();
    }

    public GestionDTO obtenerPorId(Long id) {
        Gestion gestion = gestionRepository.findById(id)
                .orElseThrow(() -> new GestionException("Gestión no encontrada"));
        return toDTO(gestion);
    }

    private GestionDTO toDTO(Gestion entity) {
        return GestionDTO.builder()
                .id(entity.getId())
                .tipo(entity.getTipo())
                .tipoLabel(entity.getTipo().getLabel())
                .fechaGestion(entity.getFechaGestion())
                .observaciones(entity.getObservaciones())
                .montoCompromiso(entity.getMontoCompromiso())
                .fechaCompromiso(entity.getFechaCompromiso())
                .clienteId(entity.getCliente().getId())
                .clienteNombre(entity.getCliente().getNombreCompleto())
                .usuarioRegistra(entity.getUsuarioRegistra())
                .fechaRegistro(entity.getFechaRegistro())
                .build();
    }
}
