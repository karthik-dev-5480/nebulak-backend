package com.nebulak.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.nebulak.model.EmailSubscriptions;

@Repository
public interface EmailSubscriptionsRepository extends JpaRepository<EmailSubscriptions, Long>, JpaSpecificationExecutor<EmailSubscriptions> {
    // This interface is complete. No additional code is needed.
}