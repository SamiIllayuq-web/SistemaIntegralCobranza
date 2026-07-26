package com.startup.cobranza.cliente.service;

import com.startup.cobranza.agencia.entity.Agencia;
import com.startup.cobranza.agencia.repository.AgenciaRepository;
import com.startup.cobranza.cartera.repository.OperacionRepository;
import com.startup.cobranza.cliente.dto.ClienteBusquedaDTO;
import com.startup.cobranza.cliente.dto.ClienteDTO;
import com.startup.cobranza.cliente.dto.ClienteFormDTO;
import com.startup.cobranza.cliente.entity.Cliente;
import com.startup.cobranza.cliente.exception.ClienteException;
import com.startup.cobranza.cliente.mapper.ClienteMapper;
import com.startup.cobranza.cliente.repository.ClienteRepository;
import com.startup.cobranza.empresa.entity.Empresa;
import com.startup.cobranza.empresa.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final OperacionRepository operacionRepository;
    private final EmpresaRepository empresaRepository;
    private final AgenciaRepository agenciaRepository;
    private final ClienteMapper clienteMapper;

    public List<ClienteDTO> listarTodos() {
        return clienteRepository.findAll().stream()
                .map(clienteMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ClienteDTO> listarActivos() {
        return clienteRepository.findByActivoTrue().stream()
                .map(clienteMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ClienteDTO obtenerPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteException("Cliente no encontrado con id: " + id));
        return clienteMapper.toDTO(cliente);
    }

    public List<ClienteDTO> buscar(ClienteBusquedaDTO busqueda) {
        // Si hay filtro por cuenta u operación, buscar operaciones primero
        Set<Long> clienteIdsFiltrados = null;
        if (busqueda.getNumeroCuenta() != null && !busqueda.getNumeroCuenta().isBlank()) {
            Set<Long> ids = operacionRepository
                    .findByNumeroCuentaContaining(busqueda.getNumeroCuenta())
                    .stream().map(o -> o.getCliente().getId()).collect(Collectors.toSet());
            if (ids.isEmpty()) return List.of();
            clienteIdsFiltrados = ids;
        }
        if (busqueda.getNumeroOperacion() != null && !busqueda.getNumeroOperacion().isBlank()) {
            Set<Long> idsPorOperacion = operacionRepository
                    .findByNumeroOperacionContaining(busqueda.getNumeroOperacion())
                    .stream().map(o -> o.getCliente().getId()).collect(Collectors.toSet());
            if (clienteIdsFiltrados == null) {
                clienteIdsFiltrados = idsPorOperacion;
            } else {
                clienteIdsFiltrados.retainAll(idsPorOperacion);
                if (clienteIdsFiltrados.isEmpty()) return List.of();
            }
        }

        final Set<Long> finalIds = clienteIdsFiltrados;
        return clienteRepository.findAll().stream()
                .map(clienteMapper::toDTO)
                .filter(c -> {
                    if (finalIds != null && !finalIds.contains(c.getId())) return false;
                    return matchesBusqueda(c, busqueda);
                })
                .collect(Collectors.toList());
    }

    private boolean matchesBusqueda(ClienteDTO cliente, ClienteBusquedaDTO busqueda) {
        if (busqueda.getNombre() != null && !busqueda.getNombre().isBlank()
                && !cliente.getNombreCompleto().toLowerCase().contains(busqueda.getNombre().toLowerCase())) {
            return false;
        }
        if (busqueda.getDni() != null && !busqueda.getDni().isBlank()
                && !cliente.getDni().contains(busqueda.getDni())) {
            return false;
        }
        if (busqueda.getEmpresaId() != null
                && !busqueda.getEmpresaId().equals(cliente.getEmpresaId())) {
            return false;
        }
        return true;
    }

    @Transactional
    public ClienteDTO crear(ClienteFormDTO form) {
        Empresa empresa = empresaRepository.findById(form.getEmpresaId())
                .orElseThrow(() -> new ClienteException("Empresa no encontrada con id: " + form.getEmpresaId()));

        Cliente cliente = clienteMapper.toEntityFromForm(form, empresa, null);
        return clienteMapper.toDTO(clienteRepository.save(cliente));
    }

    @Transactional
    public ClienteDTO actualizar(Long id, ClienteFormDTO form) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteException("Cliente no encontrado con id: " + id));

        Empresa empresa = empresaRepository.findById(form.getEmpresaId())
                .orElseThrow(() -> new ClienteException("Empresa no encontrada con id: " + form.getEmpresaId()));

        cliente.setNombreCompleto(form.getNombreCompleto());
        cliente.setDni(form.getDni());
        cliente.setTelefono(form.getTelefono());
        cliente.setTelefono2(form.getTelefono2());
        cliente.setTelefono3(form.getTelefono3());
        cliente.setDireccion(form.getDireccion());
        cliente.setEmpresa(empresa);

        return clienteMapper.toDTO(clienteRepository.save(cliente));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ClienteException("Cliente no encontrado con id: " + id);
        }
        clienteRepository.deleteById(id);
    }
}
