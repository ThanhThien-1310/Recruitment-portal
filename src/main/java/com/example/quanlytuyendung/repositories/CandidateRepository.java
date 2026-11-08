package com.example.quanlytuyendung.repositories;

import com.example.quanlytuyendung.entities.CandidateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateRepository extends JpaRepository<CandidateEntity, Long> {
    List<CandidateEntity> findByJobId(Long jobId);
    List<CandidateEntity> findByStage(String stage);
    Optional<CandidateEntity> findByEmail(String email);
    Optional<CandidateEntity> findByEmailOrPhone(String email, String phone);
}
