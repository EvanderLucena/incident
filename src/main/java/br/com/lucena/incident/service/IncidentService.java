package br.com.lucena.incident.service;

import br.com.lucena.incident.exception.ResourceNotFoundException;
import br.com.lucena.incident.model.Incident;
import br.com.lucena.incident.repository.IncidentRepository;
import br.com.lucena.incident.dto.IncidentRequestDTO;
import br.com.lucena.incident.dto.IncidentResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class IncidentService {

    private final IncidentRepository incidentRepository;

    public IncidentResponseDTO createIncident(IncidentRequestDTO requestDTO) {
        Incident incident = new Incident();
        incident.setName(requestDTO.getName());
        incident.setDescription(requestDTO.getDescription());
        incident.setClosedAt(requestDTO.getClosedAt());

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
        incident.setClosedAt(requestDTO.getClosedAt());
        
        return mapToDTO(incidentRepository.save(incident));
    }

    public void deleteIncident(Long id) {
        Incident incident = findIncidentById(id);
        incidentRepository.delete(incident);
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
                incident.getCreatedAt(),
                incident.getUpdatedAt(),
                incident.getClosedAt()
        );
    }
} 