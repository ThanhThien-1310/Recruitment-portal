package com.example.quanlytuyendung.repositories;

import com.example.quanlytuyendung.entities.OfferEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfferRepository extends JpaRepository<OfferEntity, Long> {
    List<OfferEntity> findByStatus(String status);
    List<OfferEntity> findByCandidateId(Long candidateId);
}
