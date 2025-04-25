package br.com.lucena.incident;

import br.com.lucena.incident.dto.IncidentRequestDTO;
import br.com.lucena.incident.dto.IncidentResponseDTO;
import br.com.lucena.incident.exception.ResourceNotFoundException;
import br.com.lucena.incident.model.Incident;
import br.com.lucena.incident.repository.IncidentRepository;
import br.com.lucena.incident.service.IncidentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    @Mock
    private IncidentRepository incidentRepository;

    @InjectMocks
    private IncidentService incidentService;

    private Incident incident;
    private IncidentRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        
        incident = new Incident();
        incident.setIdIncident(1L);
        incident.setName("Test Incident");
        incident.setDescription("Test Description");
        incident.setCreatedAt(now);
        incident.setUpdatedAt(now);
        
        requestDTO = new IncidentRequestDTO();
        requestDTO.setName("Test Incident");
        requestDTO.setDescription("Test Description");
    }

    @Test
    void createIncident_ShouldReturnSavedIncident() {
        when(incidentRepository.save(any(Incident.class))).thenReturn(incident);

        IncidentResponseDTO result = incidentService.createIncident(requestDTO);

        assertNotNull(result);
        assertEquals(incident.getIdIncident(), result.getIdIncident());
        assertEquals(incident.getName(), result.getName());
        verify(incidentRepository, times(1)).save(any(Incident.class));
    }

    @Test
    void getIncidentById_WithValidId_ShouldReturnIncident() {
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incident));

        IncidentResponseDTO result = incidentService.getIncidentById(1L);

        assertNotNull(result);
        assertEquals(incident.getIdIncident(), result.getIdIncident());
        verify(incidentRepository, times(1)).findById(1L);
    }

    @Test
    void getIncidentById_WithInvalidId_ShouldThrowException() {
        when(incidentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            incidentService.getIncidentById(999L);
        });
        
        verify(incidentRepository, times(1)).findById(999L);
    }

    @Test
    void getAllIncidents_ShouldReturnAllIncidents() {
        List<Incident> incidents = Arrays.asList(incident);
        when(incidentRepository.findAll()).thenReturn(incidents);

        List<IncidentResponseDTO> results = incidentService.getAllIncidents();

        assertNotNull(results);
        assertEquals(1, results.size());
        verify(incidentRepository, times(1)).findAll();
    }

    @Test
    void getLatest20Incidents_ShouldReturnLatestIncidents() {
        PageRequest pageRequest = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt"));
        PageImpl<Incident> page = new PageImpl<>(Arrays.asList(incident));
        
        when(incidentRepository.findAll(pageRequest)).thenReturn(page);

        List<IncidentResponseDTO> results = incidentService.getLatest20Incidents();

        assertNotNull(results);
        assertEquals(1, results.size());
        verify(incidentRepository, times(1)).findAll(pageRequest);
    }

    @Test
    void updateIncident_WithValidId_ShouldReturnUpdatedIncident() {
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incident));
        when(incidentRepository.save(any(Incident.class))).thenReturn(incident);

        IncidentResponseDTO result = incidentService.updateIncident(1L, requestDTO);

        assertNotNull(result);
        assertEquals(incident.getIdIncident(), result.getIdIncident());
        verify(incidentRepository, times(1)).findById(1L);
        verify(incidentRepository, times(1)).save(any(Incident.class));
    }

    @Test
    void deleteIncident_WithValidId_ShouldDeleteIncident() {
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incident));
        doNothing().when(incidentRepository).delete(incident);

        assertDoesNotThrow(() -> incidentService.deleteIncident(1L));
        
        verify(incidentRepository, times(1)).findById(1L);
        verify(incidentRepository, times(1)).delete(incident);
    }
} 