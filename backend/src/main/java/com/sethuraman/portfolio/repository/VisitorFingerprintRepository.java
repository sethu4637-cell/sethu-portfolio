package com.sethuraman.portfolio.repository;

import com.sethuraman.portfolio.entity.VisitorFingerprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

/**
 * Repository for VisitorFingerprint entities used for unique visitor tracking.
 */
@Repository
public interface VisitorFingerprintRepository extends JpaRepository<VisitorFingerprint, Long> {

    boolean existsByFingerprintHashAndVisitDate(String fingerprintHash, LocalDate visitDate);
}
