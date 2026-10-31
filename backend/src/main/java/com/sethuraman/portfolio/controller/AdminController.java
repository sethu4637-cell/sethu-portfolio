package com.sethuraman.portfolio.controller;

import com.sethuraman.portfolio.dto.ContactMessageDto;
import com.sethuraman.portfolio.dto.DashboardDto;
import com.sethuraman.portfolio.dto.VisitorCountDto;
import com.sethuraman.portfolio.service.ContactService;
import com.sethuraman.portfolio.service.VisitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for the protected admin dashboard.
 * All endpoints require ROLE_ADMIN — enforced by Spring Security filter chain.
 *
 * Base path: /api/admin
 *
 * Endpoints:
 *   GET  /api/admin/dashboard         — Full dashboard data
 *   GET  /api/admin/messages          — All contact messages (newest first)
 *   POST /api/admin/messages/{id}/read — Mark a message as read
 *   GET  /api/admin/visitors          — Visitor statistics
 *   GET  /api/admin/me                — Returns current admin username (session check)
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final ContactService  contactService;
    private final VisitorService  visitorService;

    public AdminController(ContactService contactService, VisitorService visitorService) {
        this.contactService = contactService;
        this.visitorService = visitorService;
    }

    /**
     * Returns all data needed to render the admin dashboard.
     * Visitor stats are retrieved read-only — no visit is recorded.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDto> getDashboard() {
        VisitorCountDto visitorStats = visitorService.getStats();
        List<ContactMessageDto> recentMessages = contactService.getRecentMessages(10);
        long totalMessages = contactService.getTotalMessageCount();

        DashboardDto dashboard = new DashboardDto(
                visitorStats.getTotalVisits(),
                visitorStats.getTodayVisits(),
                visitorStats.getTotalUniqueVisitors(),
                visitorStats.getTodayUniqueVisitors(),
                totalMessages,
                recentMessages
        );

        return ResponseEntity.ok(dashboard);
    }

    /**
     * Returns all contact messages, ordered newest-first.
     */
    @GetMapping("/messages")
    public ResponseEntity<List<ContactMessageDto>> getAllMessages() {
        return ResponseEntity.ok(contactService.getAllMessages());
    }

    /**
     * Marks a specific contact message as read.
     */
    @PostMapping("/messages/{id}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable Long id) {
        contactService.markAsRead(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Message marked as read."));
    }

    /**
     * Returns visitor statistics for the admin (read-only).
     */
    @GetMapping("/visitors")
    public ResponseEntity<VisitorCountDto> getVisitorStats() {
        return ResponseEntity.ok(visitorService.getStats());
    }

    /**
     * Returns the authenticated admin's username.
     * Used by the dashboard frontend to confirm the session is valid.
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMe(Authentication auth) {
        return ResponseEntity.ok(Map.of(
                "username", auth.getName(),
                "role", "ADMIN",
                "authenticated", true
        ));
    }
}
