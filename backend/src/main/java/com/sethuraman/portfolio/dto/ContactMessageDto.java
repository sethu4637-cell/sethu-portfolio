package com.sethuraman.portfolio.dto;

import java.time.LocalDateTime;

/**
 * DTO for returning contact message data in admin dashboard responses.
 * Never exposes the IP hash or any sensitive internal fields directly.
 */
public class ContactMessageDto {

    private Long id;
    private String name;
    private String email;
    private String subject;
    private String message;
    private LocalDateTime submittedAt;
    private boolean isRead;

    public ContactMessageDto() {}

    public ContactMessageDto(Long id, String name, String email, String subject,
                              String message, LocalDateTime submittedAt, boolean isRead) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.subject = subject;
        this.message = message;
        this.submittedAt = submittedAt;
        this.isRead = isRead;
    }

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

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}
