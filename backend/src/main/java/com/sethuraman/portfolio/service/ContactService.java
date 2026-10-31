package com.sethuraman.portfolio.service;

import com.sethuraman.portfolio.dto.ContactMessageDto;
import com.sethuraman.portfolio.dto.ContactRequest;
import com.sethuraman.portfolio.entity.ContactMessage;
import com.sethuraman.portfolio.repository.ContactMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for handling contact form submission and message management.
 * Stores messages securely in MySQL. Raw IP is hashed before storage.
 */
@Service
@Transactional
public class ContactService {

    private static final Logger log = LoggerFactory.getLogger(ContactService.class);

    private final ContactMessageRepository contactRepo;

    public ContactService(ContactMessageRepository contactRepo) {
        this.contactRepo = contactRepo;
    }

    /**
     * Saves a validated contact message from the portfolio contact form.
     *
     * @param request  The validated contact DTO.
     * @param clientIp The client's IP address (will be hashed, never stored raw).
     */
    public void saveMessage(ContactRequest request, String clientIp) {
        String ipHash = hashString(clientIp + ":portfolio-salt-2026");
        ContactMessage msg = new ContactMessage(
                request.getName(),
                request.getEmail(),
                request.getSubject(),
                request.getMessage(),
                LocalDateTime.now(),
                ipHash
        );
        contactRepo.save(msg);
        log.info("Contact message saved from: {}", request.getEmail());
    }

    /**
     * Returns the N most recent messages for the admin dashboard.
     */
    @Transactional(readOnly = true)
    public List<ContactMessageDto> getRecentMessages(int limit) {
        return contactRepo.findAllByOrderBySubmittedAtDesc(PageRequest.of(0, limit))
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Returns all messages for the admin dashboard.
     */
    @Transactional(readOnly = true)
    public List<ContactMessageDto> getAllMessages() {
        return contactRepo.findAll()
                .stream()
                .sorted((a, b) -> b.getSubmittedAt().compareTo(a.getSubmittedAt()))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Returns the total number of contact messages.
     */
    @Transactional(readOnly = true)
    public long getTotalMessageCount() {
        return contactRepo.count();
    }

    /**
     * Marks a message as read.
     */
    public void markAsRead(Long id) {
        contactRepo.findById(id).ifPresent(msg -> {
            msg.setRead(true);
            contactRepo.save(msg);
        });
    }

    // ---- Private helpers ----

    private ContactMessageDto toDto(ContactMessage msg) {
        return new ContactMessageDto(
                msg.getId(),
                msg.getName(),
                msg.getEmail(),
                msg.getSubject(),
                msg.getMessage(),
                msg.getSubmittedAt(),
                msg.isRead()
        );
    }

    /**
     * SHA-256 hash of a string — used for IP hashing.
     * Never stores raw IPs.
     */
    private String hashString(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 not available", e);
            return "unknown";
        }
    }
}
