package com.example.quanlytuyendung.repositories;

import com.example.quanlytuyendung.entities.OnboardingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OnboardingRepository extends JpaRepository<OnboardingEntity, Long> {
    List<OnboardingEntity> findByCandidateId(Long candidateId);
    List<OnboardingEntity> findByStatus(String status);
}
