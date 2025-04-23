package br.com.lucena.incident.dto;

import br.com.lucena.incident.model.IncidentStatus;
import lombok.Data;

@Data
public class StatusUpdateDTO {
    private IncidentStatus status;
} 