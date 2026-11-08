package com.example.quanlytuyendung.repositories;

import com.example.quanlytuyendung.entities.JobPostingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPostingEntity, Long> {
    List<JobPostingEntity> findByStatus(String status);
    List<JobPostingEntity> findByDepartmentContainingIgnoreCase(String department);
}
