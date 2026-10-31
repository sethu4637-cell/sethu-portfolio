package com.sethuraman.portfolio.repository;

import com.sethuraman.portfolio.entity.VisitorStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Repository for VisitorStat entities.
 */
@Repository
public interface VisitorStatRepository extends JpaRepository<VisitorStat, Long> {

    Optional<VisitorStat> findByVisitDate(LocalDate visitDate);

    @Query("SELECT COALESCE(SUM(v.totalVisits), 0) FROM VisitorStat v")
    long sumTotalVisits();

    @Query("SELECT COALESCE(SUM(v.uniqueVisitors), 0) FROM VisitorStat v")
    long sumUniqueVisitors();
}
