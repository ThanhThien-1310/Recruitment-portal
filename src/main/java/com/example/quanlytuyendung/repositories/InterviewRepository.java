package com.example.quanlytuyendung.repositories;

import com.example.quanlytuyendung.entities.InterviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewRepository extends JpaRepository<InterviewEntity, Long> {
    List<InterviewEntity> findByCandidateId(Long candidateId);
    List<InterviewEntity> findByStatus(String status);
}
