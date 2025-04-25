package br.com.lucena.incident.repository;

import br.com.lucena.incident.model.Incident;
import br.com.lucena.incident.model.IncidentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {
    
    @Query("SELECT i FROM Incident i ORDER BY i.createdAt DESC")
    List<Incident> findTop20ByOrderByCreatedAtDesc();
    
    List<Incident> findByStatus(IncidentStatus status);
} 