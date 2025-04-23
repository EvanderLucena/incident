package br.com.lucena.incident.model;

public enum IncidentStatus {
    OPEN,           // Recém criado
    IN_PROGRESS,    // Em análise/tratamento
    RESOLVED,       // Resolvido mas ainda não fechado
    CLOSED          // Completamente fechado
} 