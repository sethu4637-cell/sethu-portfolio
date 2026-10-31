package com.sethuraman.portfolio.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Tracks unique visitor fingerprints per day to prevent double-counting.
 * Stores only SHA-256 hashes of visitor fingerprints (IP + User-Agent hash).
 * Raw data is NEVER persisted.
 */
@Entity
@Table(name = "visitor_fingerprints",
        uniqueConstraints = @UniqueConstraint(columnNames = {"fingerprint_hash", "visit_date"}))
public class VisitorFingerprint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fingerprint_hash", nullable = false, length = 64)
    private String fingerprintHash;

    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;

    // ---- Constructors ----

    public VisitorFingerprint() {}

    public VisitorFingerprint(String fingerprintHash, LocalDate visitDate) {
        this.fingerprintHash = fingerprintHash;
        this.visitDate = visitDate;
    }

    // ---- Getters & Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFingerprintHash() { return fingerprintHash; }
    public void setFingerprintHash(String fingerprintHash) { this.fingerprintHash = fingerprintHash; }

    public LocalDate getVisitDate() { return visitDate; }
    public void setVisitDate(LocalDate visitDate) { this.visitDate = visitDate; }
}
