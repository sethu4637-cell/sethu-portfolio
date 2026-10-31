package com.sethuraman.portfolio.controller;

import com.sethuraman.portfolio.config.RateLimiter;
import com.sethuraman.portfolio.dto.ContactRequest;
import com.sethuraman.portfolio.service.ContactService;
import com.sethuraman.portfolio.service.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for the portfolio contact form.
 * Public endpoint: POST /api/contact
 * Rate-limited to prevent spam (5 requests per minute per IP).
 * Sends an email notification to Sethuraman's Gmail and stores the message in MySQL.
 */
@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private static final Logger log = LoggerFactory.getLogger(ContactController.class);

    private final ContactService contactService;
    private final EmailService emailService;
    private final RateLimiter rateLimiter;

    public ContactController(ContactService contactService, EmailService emailService, RateLimiter rateLimiter) {
        this.contactService = contactService;
        this.emailService = emailService;
        this.rateLimiter = rateLimiter;
    }

    /**
     * Accepts a contact form submission.
     * Validates all fields server-side before sending email and saving to database.
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> submitContact(
            @Valid @RequestBody ContactRequest request,
            HttpServletRequest httpRequest) {

        String clientIp = getClientIp(httpRequest);

        // Rate limiting
        if (!rateLimiter.allowContactRequest(clientIp)) {
            log.warn("Rate limit exceeded for contact form: {}", clientIp);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of(
                            "success", false,
                            "message", "Too many requests. Please wait a moment before trying again."
                    ));
        }

        try {
            // 1. Send the email via Gmail SMTP (Confirmed before claiming success)
            emailService.sendContactEmail(
                    request.getName(),
                    request.getEmail(),
                    request.getSubject(),
                    request.getMessage()
            );

            // 2. Persist message to MySQL database
            contactService.saveMessage(request, clientIp);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Your message has been sent successfully to Sethuraman's Gmail! I'll get back to you soon."
            ));
        } catch (IllegalStateException e) {
            log.error("Email configuration error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
        } catch (Exception e) {
            log.error("Failed to deliver email via Gmail SMTP", e);
            String errorMsg = e.getMessage() != null ? e.getMessage() : "Unknown SMTP error";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Failed to deliver email via Gmail SMTP: " + errorMsg + ". Please verify SMTP credentials."
                    ));
        }
    }

    /**
     * Handles @Valid annotation validation failures and returns field-level errors.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String msg   = error.getDefaultMessage();
            fieldErrors.put(field, msg);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "success", false,
                        "message", "Validation failed. Please check your input.",
                        "errors", fieldErrors
                ));
    }

    // ---- Private helpers ----

    /**
     * Extracts real client IP, handling X-Forwarded-For from proxies/load balancers.
     */
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
