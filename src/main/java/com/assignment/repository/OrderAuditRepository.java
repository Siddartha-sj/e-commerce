package com.assignment.repository;

import com.assignment.entites.OrderAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderAuditRepository extends JpaRepository<OrderAudit, Long> {
}
