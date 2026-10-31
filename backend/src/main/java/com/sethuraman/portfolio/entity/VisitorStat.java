package com.sethuraman.portfolio.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entity representing daily visitor statistics.
 * Counts total visits and unique visitors (by hashed fingerprint) per day.
 * Raw IP addresses are NEVER stored — only salted SHA-256 hashes.
 */
@Entity
@Table(name = "visitor_stats",
        uniqueConstraints = @UniqueConstraint(columnNames = {"visit_date"}))
public class VisitorStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "visit_date", nullable = false, unique = true)
    private LocalDate visitDate;

    @Column(name = "total_visits", nullable = false)
    private long totalVisits = 0;

    @Column(name = "unique_visitors", nullable = false)
    private long uniqueVisitors = 0;

    // ---- Constructors ----

    public VisitorStat() {}

    public VisitorStat(LocalDate visitDate) {
        this.visitDate = visitDate;
        this.totalVisits = 0;
        this.uniqueVisitors = 0;
    }

    // ---- Getters & Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getVisitDate() { return visitDate; }
    public void setVisitDate(LocalDate visitDate) { this.visitDate = visitDate; }

    public long getTotalVisits() { return totalVisits; }
    public void setTotalVisits(long totalVisits) { this.totalVisits = totalVisits; }

    public long getUniqueVisitors() { return uniqueVisitors; }
    public void setUniqueVisitors(long uniqueVisitors) { this.uniqueVisitors = uniqueVisitors; }

    // ---- Helpers ----

    public void incrementTotal() {
        this.totalVisits++;
    }

    public void incrementUnique() {
        this.uniqueVisitors++;
    }
}
