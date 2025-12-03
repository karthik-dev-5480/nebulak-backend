package com.nebulak.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.nebulak.model.Tickets;

@Repository
public interface TicketCreationRepository extends JpaRepository<Tickets,Long>, JpaSpecificationExecutor<Tickets> {
    // This interface is complete. No additional code is needed.
}