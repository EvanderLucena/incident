package br.com.lucena.incident;

import br.com.lucena.incident.dto.IncidentRequestDTO;
import br.com.lucena.incident.dto.IncidentResponseDTO;
import br.com.lucena.incident.exception.ResourceNotFoundException;
import br.com.lucena.incident.model.Incident;
import br.com.lucena.incident.model.IncidentStatus;
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
import java.util.Collections;
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
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        
        incident = new Incident();
        incident.setIdIncident(1L);
        incident.setName("Test Incident");
        incident.setDescription("Test Description");
        incident.setStatus(IncidentStatus.OPEN);
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
        assertEquals(incident.getDescription(), result.getDescription());
        assertEquals(incident.getStatus(), result.getStatus());
        assertEquals(incident.getCreatedAt(), result.getCreatedAt());
        verify(incidentRepository, times(1)).save(any(Incident.class));
    }

    @Test
    void getIncidentById_WithValidId_ShouldReturnIncident() {
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incident));

        IncidentResponseDTO result = incidentService.getIncidentById(1L);

        assertNotNull(result);
        assertEquals(incident.getIdIncident(), result.getIdIncident());
        assertEquals(incident.getName(), result.getName());
        assertEquals(incident.getDescription(), result.getDescription());
        verify(incidentRepository, times(1)).findById(1L);
    }

    @Test
    void getIncidentById_WithInvalidId_ShouldThrowException() {
        when(incidentRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            incidentService.getIncidentById(999L);
        });
        
        assertEquals("Incident not found with id: 999", exception.getMessage());
        verify(incidentRepository, times(1)).findById(999L);
    }

    @Test
    void getAllIncidents_ShouldReturnAllIncidents() {
        List<Incident> incidents = Arrays.asList(incident);
        when(incidentRepository.findAll()).thenReturn(incidents);

        List<IncidentResponseDTO> results = incidentService.getAllIncidents();

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(incident.getIdIncident(), results.get(0).getIdIncident());
        assertEquals(incident.getName(), results.get(0).getName());
        verify(incidentRepository, times(1)).findAll();
    }

    @Test
    void getAllIncidents_WithNoIncidents_ShouldReturnEmptyList() {
        when(incidentRepository.findAll()).thenReturn(Collections.emptyList());

        List<IncidentResponseDTO> results = incidentService.getAllIncidents();

        assertNotNull(results);
        assertTrue(results.isEmpty());
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
        assertEquals(incident.getIdIncident(), results.get(0).getIdIncident());
        verify(incidentRepository, times(1)).findAll(pageRequest);
    }

    @Test
    void updateIncident_WithValidId_ShouldReturnUpdatedIncident() {
        Incident updatedIncident = new Incident();
        updatedIncident.setIdIncident(1L);
        updatedIncident.setName("Updated Name");
        updatedIncident.setDescription("Updated Description");
        updatedIncident.setStatus(IncidentStatus.IN_PROGRESS);
        updatedIncident.setCreatedAt(now);
        updatedIncident.setUpdatedAt(now.plusHours(1));
        
        IncidentRequestDTO updateRequest = new IncidentRequestDTO();
        updateRequest.setName("Updated Name");
        updateRequest.setDescription("Updated Description");
        
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incident));
        when(incidentRepository.save(any(Incident.class))).thenReturn(updatedIncident);

        IncidentResponseDTO result = incidentService.updateIncident(1L, updateRequest);

        assertNotNull(result);
        assertEquals(updatedIncident.getIdIncident(), result.getIdIncident());
        assertEquals(updatedIncident.getName(), result.getName());
        assertEquals(updatedIncident.getDescription(), result.getDescription());
        verify(incidentRepository, times(1)).findById(1L);
        verify(incidentRepository, times(1)).save(any(Incident.class));
    }

    @Test
    void updateIncident_WithInvalidId_ShouldThrowException() {
        when(incidentRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            incidentService.updateIncident(999L, requestDTO);
        });
        
        assertEquals("Incident not found with id: 999", exception.getMessage());
        verify(incidentRepository, times(1)).findById(999L);
        verify(incidentRepository, never()).save(any(Incident.class));
    }

    @Test
    void deleteIncident_WithValidId_ShouldDeleteIncident() {
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incident));
        doNothing().when(incidentRepository).delete(incident);

        assertDoesNotThrow(() -> incidentService.deleteIncident(1L));
        
        verify(incidentRepository, times(1)).findById(1L);
        verify(incidentRepository, times(1)).delete(incident);
    }
    
    @Test
    void deleteIncident_WithInvalidId_ShouldThrowException() {
        when(incidentRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            incidentService.deleteIncident(999L);
        });
        
        assertEquals("Incident not found with id: 999", exception.getMessage());
        verify(incidentRepository, times(1)).findById(999L);
        verify(incidentRepository, never()).delete(any(Incident.class));
    }
    
    @Test
    void updateStatus_WithValidId_ShouldUpdateStatus() {
        Incident statusUpdatedIncident = new Incident();
        statusUpdatedIncident.setIdIncident(1L);
        statusUpdatedIncident.setName("Test Incident");
        statusUpdatedIncident.setDescription("Test Description");
        statusUpdatedIncident.setStatus(IncidentStatus.RESOLVED);
        statusUpdatedIncident.setCreatedAt(now);
        statusUpdatedIncident.setUpdatedAt(now.plusHours(1));
        statusUpdatedIncident.setClosedAt(now.plusHours(1));
        
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incident));
        when(incidentRepository.save(any(Incident.class))).thenReturn(statusUpdatedIncident);

        IncidentResponseDTO result = incidentService.updateStatus(1L, IncidentStatus.RESOLVED);

        assertNotNull(result);
        assertEquals(statusUpdatedIncident.getIdIncident(), result.getIdIncident());
        assertEquals(statusUpdatedIncident.getStatus(), result.getStatus());
        assertNotNull(result.getClosedAt());
        verify(incidentRepository, times(1)).findById(1L);
        verify(incidentRepository, times(1)).save(any(Incident.class));
    }
    
    @Test
    void updateStatus_WithInvalidId_ShouldThrowException() {
        when(incidentRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            incidentService.updateStatus(999L, IncidentStatus.RESOLVED);
        });
        
        assertEquals("Incident not found with id: 999", exception.getMessage());
        verify(incidentRepository, times(1)).findById(999L);
        verify(incidentRepository, never()).save(any(Incident.class));
    }
    
    @Test
    void findByStatus_ShouldReturnIncidentsWithMatchingStatus() {
        List<Incident> incidents = Arrays.asList(incident);
        when(incidentRepository.findByStatus(IncidentStatus.OPEN)).thenReturn(incidents);

        List<IncidentResponseDTO> results = incidentService.findByStatus(IncidentStatus.OPEN);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(incident.getIdIncident(), results.get(0).getIdIncident());
        assertEquals(IncidentStatus.OPEN, results.get(0).getStatus());
        verify(incidentRepository, times(1)).findByStatus(IncidentStatus.OPEN);
    }
    
    @Test
    void findByStatus_WithNoMatches_ShouldReturnEmptyList() {
        when(incidentRepository.findByStatus(IncidentStatus.RESOLVED)).thenReturn(Collections.emptyList());

        List<IncidentResponseDTO> results = incidentService.findByStatus(IncidentStatus.RESOLVED);

        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(incidentRepository, times(1)).findByStatus(IncidentStatus.RESOLVED);
    }
} 