package com.sethuraman.portfolio.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a contact message submitted through the portfolio contact form.
 * Messages are stored securely and only accessible to authenticated admins.
 */
@Entity
@Table(name = "contact_messages")
public class ContactMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 200)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @Column(name = "ip_hash", length = 64)
    private String ipHash; // SHA-256 hash of IP, never raw IP

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    // ---- Constructors ----

    public ContactMessage() {}

    public ContactMessage(String name, String email, String subject, String message,
                          LocalDateTime submittedAt, String ipHash) {
        this.name = name;
        this.email = email;
        this.subject = subject;
        this.message = message;
        this.submittedAt = submittedAt;
        this.ipHash = ipHash;
    }

    // ---- Lifecycle ----

    @PrePersist
    public void prePersist() {
        if (this.submittedAt == null) {
            this.submittedAt = LocalDateTime.now();
        }
    }

    // ---- Getters & Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public String getIpHash() { return ipHash; }
    public void setIpHash(String ipHash) { this.ipHash = ipHash; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}
