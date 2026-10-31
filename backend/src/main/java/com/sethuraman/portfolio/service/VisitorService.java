package com.sethuraman.portfolio.service;

import com.sethuraman.portfolio.dto.VisitorCountDto;
import com.sethuraman.portfolio.entity.VisitorFingerprint;
import com.sethuraman.portfolio.entity.VisitorStat;
import com.sethuraman.portfolio.repository.VisitorFingerprintRepository;
import com.sethuraman.portfolio.repository.VisitorStatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.HexFormat;

/**
 * Service for recording and retrieving visitor statistics.
 * <p>
 * Unique visitor counting methodology:
 * - A visitor fingerprint is derived from: IP address + User-Agent string
 * - The fingerprint is hashed with SHA-256 (salted), never stored raw
 * - If the same fingerprint appears again on the same calendar day, uniqueVisitors is NOT incremented
 * - totalVisits is always incremented for each valid visit
 * - Admin dashboard requests are explicitly excluded via a request header
 * </p>
 */
@Service
public class VisitorService {

    private static final Logger log = LoggerFactory.getLogger(VisitorService.class);
    private static final String SALT = "portfolio-visitor-2026-salt";

    private final VisitorStatRepository statRepo;
    private final VisitorFingerprintRepository fingerprintRepo;

    public VisitorService(VisitorStatRepository statRepo,
                          VisitorFingerprintRepository fingerprintRepo) {
        this.statRepo = statRepo;
        this.fingerprintRepo = fingerprintRepo;
    }

    /**
     * Records a portfolio visit.
     *
     * @param clientIp  The visitor's IP address.
     * @param userAgent The visitor's User-Agent header.
     */
    @Transactional
    public VisitorCountDto recordVisit(String clientIp, String userAgent) {
        LocalDate today = LocalDate.now();
        String fingerprint = hashFingerprint(clientIp, userAgent);

        // Get or create today's stat row
        VisitorStat stat = statRepo.findByVisitDate(today)
                .orElseGet(() -> {
                    VisitorStat s = new VisitorStat(today);
                    return statRepo.save(s);
                });

        // Always increment total visits
        stat.incrementTotal();

        // Check and increment unique visitors only if fingerprint is new today
        boolean isUnique = !fingerprintRepo.existsByFingerprintHashAndVisitDate(fingerprint, today);
        if (isUnique) {
            stat.incrementUnique();
            fingerprintRepo.save(new VisitorFingerprint(fingerprint, today));
        }

        statRepo.save(stat);

        return buildCountDto(today, stat);
    }

    /**
     * Returns current visitor statistics without recording a new visit.
     */
    @Transactional(readOnly = true)
    public VisitorCountDto getStats() {
        LocalDate today = LocalDate.now();
        VisitorStat todayStat = statRepo.findByVisitDate(today)
                .orElse(new VisitorStat(today));
        return buildCountDto(today, todayStat);
    }

    // ---- Private helpers ----

    private VisitorCountDto buildCountDto(LocalDate today, VisitorStat todayStat) {
        long totalVisits = statRepo.sumTotalVisits();
        long totalUnique = statRepo.sumUniqueVisitors();
        return new VisitorCountDto(
                totalVisits,
                todayStat.getTotalVisits(),
                totalUnique,
                todayStat.getUniqueVisitors()
        );
    }

    /**
     * Creates a salted SHA-256 hash of (IP + UserAgent) for fingerprinting.
     * Raw IP and User-Agent are NEVER stored.
     */
    private String hashFingerprint(String ip, String userAgent) {
        String raw = ip + "|" + (userAgent != null ? userAgent : "unknown") + "|" + SALT;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 not available", e);
            return "fallback-" + ip.hashCode();
        }
    }
}
