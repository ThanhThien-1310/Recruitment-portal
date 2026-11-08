package com.example.quanlytuyendung.repositories;

import com.example.quanlytuyendung.entities.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long> {
    List<AuditLogEntity> findTop50ByOrderByTimestampDesc();
}
