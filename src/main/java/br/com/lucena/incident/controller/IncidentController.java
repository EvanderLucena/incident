package br.com.lucena.incident.controller;

import br.com.lucena.incident.dto.IncidentRequestDTO;
import br.com.lucena.incident.dto.IncidentResponseDTO;
import br.com.lucena.incident.dto.StatusUpdateDTO;
import br.com.lucena.incident.model.IncidentStatus;
import br.com.lucena.incident.service.IncidentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@Tag(name = "Incident Management", description = "Endpoints for managing incidents")
public class IncidentController {

    private IncidentService incidentService;
    
    @Autowired
    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @PostMapping
    @Operation(summary = "Create a new incident")
    public ResponseEntity<IncidentResponseDTO> createIncident(@Valid @RequestBody IncidentRequestDTO requestDTO) {
        return new ResponseEntity<>(incidentService.createIncident(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get incident by ID")
    public ResponseEntity<IncidentResponseDTO> getIncidentById(@PathVariable Long id) {
        return ResponseEntity.ok(incidentService.getIncidentById(id));
    }

    @GetMapping
    @Operation(summary = "Get all incidents")
    public ResponseEntity<List<IncidentResponseDTO>> getAllIncidents() {
        return ResponseEntity.ok(incidentService.getAllIncidents());
    }

    @GetMapping("/latest")
    @Operation(summary = "Get 20 latest incidents")
    public ResponseEntity<List<IncidentResponseDTO>> getLatest20Incidents() {
        return ResponseEntity.ok(incidentService.getLatest20Incidents());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an incident")
    public ResponseEntity<IncidentResponseDTO> updateIncident(@PathVariable Long id, 
                                                             @Valid @RequestBody IncidentRequestDTO requestDTO) {
        return ResponseEntity.ok(incidentService.updateIncident(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an incident")
    public ResponseEntity<Void> deleteIncident(@PathVariable Long id) {
        incidentService.deleteIncident(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update incident status")
    public ResponseEntity<IncidentResponseDTO> updateStatus(
            @PathVariable Long id, 
            @Valid @RequestBody StatusUpdateDTO statusUpdate) {
        return ResponseEntity.ok(incidentService.updateStatus(id, statusUpdate.getStatus()));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get incidents by status")
    public ResponseEntity<List<IncidentResponseDTO>> getByStatus(@PathVariable IncidentStatus status) {
        return ResponseEntity.ok(incidentService.findByStatus(status));
    }
} 