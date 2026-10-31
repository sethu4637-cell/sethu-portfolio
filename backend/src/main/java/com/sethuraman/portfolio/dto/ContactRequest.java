package com.sethuraman.portfolio.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for incoming contact form submissions.
 * All validation is enforced server-side.
 */
public class ContactRequest {

    @NotBlank(message = "Name is required.")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters.")
    private String name;

    @NotBlank(message = "Email is required.")
    @Email(message = "Please provide a valid email address.")
    @Size(max = 150, message = "Email must not exceed 150 characters.")
    private String email;

    @NotBlank(message = "Subject is required.")
    @Size(min = 3, max = 200, message = "Subject must be between 3 and 200 characters.")
    private String subject;

    @NotBlank(message = "Message is required.")
    @Size(min = 20, max = 5000, message = "Message must be between 20 and 5000 characters.")
    private String message;

    // ---- Getters & Setters ----

    public String getName() { return name; }
    public void setName(String name) { this.name = name != null ? name.trim() : null; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email != null ? email.trim().toLowerCase() : null; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject != null ? subject.trim() : null; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message != null ? message.trim() : null; }
}
