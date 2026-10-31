package com.sethuraman.portfolio.controller;

import com.sethuraman.portfolio.dto.VisitorCountDto;
import com.sethuraman.portfolio.service.VisitorService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for visitor counting endpoints.
 *
 * Public endpoints:
 *   POST /api/visitors          — Records a portfolio visit, returns updated stats
 *   GET  /api/visitors/count    — Returns current visitor statistics (read-only)
 *
 * Admin dashboard must NOT call POST /api/visitors to avoid inflating counts.
 * A request header "X-Admin-Dashboard: true" is checked to skip recording.
 */
@RestController
@RequestMapping("/api/visitors")
public class VisitorController {

    private static final Logger log = LoggerFactory.getLogger(VisitorController.class);

    private final VisitorService visitorService;

    public VisitorController(VisitorService visitorService) {
        this.visitorService = visitorService;
    }

    /**
     * Records a portfolio visit.
     * The frontend calls this once on portfolio page load.
     * Skips recording if X-Admin-Dashboard header is present (prevents admin count inflation).
     */
    @PostMapping
    public ResponseEntity<VisitorCountDto> recordVisit(HttpServletRequest request) {
        // Explicitly skip recording for admin dashboard requests
        String adminHeader = request.getHeader("X-Admin-Dashboard");
        if ("true".equalsIgnoreCase(adminHeader)) {
            log.debug("Skipping visitor recording for admin dashboard request");
            return ResponseEntity.ok(visitorService.getStats());
        }

        String clientIp  = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        try {
            VisitorCountDto stats = visitorService.recordVisit(clientIp, userAgent);
            log.debug("Visit recorded. Total: {}, Today: {}", stats.getTotalVisits(), stats.getTodayVisits());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error recording visit", e);
            // Return empty stats on error — never break the frontend
            return ResponseEntity.ok(new VisitorCountDto(0, 0, 0, 0));
        }
    }

    /**
     * Returns current visitor statistics without recording a new visit.
     * Used by the portfolio page to display the visitor count badge.
     */
    @GetMapping("/count")
    public ResponseEntity<VisitorCountDto> getCount() {
        try {
            return ResponseEntity.ok(visitorService.getStats());
        } catch (Exception e) {
            log.error("Error fetching visitor stats", e);
            return ResponseEntity.ok(new VisitorCountDto(0, 0, 0, 0));
        }
    }

    // ---- Private helpers ----

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isBlank()) {
            return xRealIp.trim();
        }
        return request.getRemoteAddr();
    }
}
