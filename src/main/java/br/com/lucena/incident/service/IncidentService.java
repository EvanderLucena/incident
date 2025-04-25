package br.com.lucena.incident.service;

import br.com.lucena.incident.dto.IncidentRequestDTO;
import br.com.lucena.incident.dto.IncidentResponseDTO;
import br.com.lucena.incident.exception.ResourceNotFoundException;
import br.com.lucena.incident.model.Incident;
import br.com.lucena.incident.model.IncidentStatus;
import br.com.lucena.incident.repository.IncidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IncidentService {

    private IncidentRepository incidentRepository;

    @Autowired
    public IncidentService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    public IncidentResponseDTO createIncident(IncidentRequestDTO requestDTO) {
        Incident incident = new Incident();
        incident.setName(requestDTO.getName());
        incident.setDescription(requestDTO.getDescription());
        incident.setStatus(IncidentStatus.OPEN);

        return mapToDTO(incidentRepository.save(incident));
    }

    public IncidentResponseDTO getIncidentById(Long id) {
        Incident incident = findIncidentById(id);
        return mapToDTO(incident);
    }

    public List<IncidentResponseDTO> getAllIncidents() {
        return incidentRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public List<IncidentResponseDTO> getLatest20Incidents() {
        PageRequest pageRequest = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt"));
        return incidentRepository.findAll(pageRequest).getContent().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public IncidentResponseDTO updateIncident(Long id, IncidentRequestDTO requestDTO) {
        Incident incident = findIncidentById(id);
        
        incident.setName(requestDTO.getName());
        incident.setDescription(requestDTO.getDescription());
        
        return mapToDTO(incidentRepository.save(incident));
    }

    public void deleteIncident(Long id) {
        Incident incident = findIncidentById(id);
        incidentRepository.delete(incident);
    }
    
    public IncidentResponseDTO updateStatus(Long id, IncidentStatus newStatus) {
        Incident incident = findIncidentById(id);
        incident.setStatus(newStatus);
        
        // Se estiver mudando para CLOSED, atualizar a data de fechamento
        if (newStatus == IncidentStatus.CLOSED) {
            incident.setClosedAt(LocalDateTime.now());
        } else if (incident.getClosedAt() != null) {
            // Se estiver reabrindo um incidente fechado, limpar a data
            incident.setClosedAt(null);
        }
        
        return mapToDTO(incidentRepository.save(incident));
    }
    
    public List<IncidentResponseDTO> findByStatus(IncidentStatus status) {
        return incidentRepository.findByStatus(status).stream()
                .map(this::mapToDTO)
                .toList();
    }

    private Incident findIncidentById(Long id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found with id: " + id));
    }

    private IncidentResponseDTO mapToDTO(Incident incident) {
        return new IncidentResponseDTO(
                incident.getIdIncident(),
                incident.getName(),
                incident.getDescription(),
                incident.getStatus(),
                incident.getCreatedAt(),
                incident.getUpdatedAt(),
                incident.getClosedAt()
        );
    }
} 