package com.sethuraman.portfolio.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Service for sending email notifications via Gmail SMTP.
 * Configured with STARTTLS on port 587.
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String senderEmail;

    @Value("${spring.mail.password:}")
    private String mailPassword;

    @Value("${portfolio.contact.recipient-email:sethu4637@gmail.com}")
    private String recipientEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Checks if Gmail SMTP credentials are provided.
     */
    public boolean isConfigured() {
        return senderEmail != null && !senderEmail.isBlank()
                && mailPassword != null && !mailPassword.isBlank();
    }

    /**
     * Sends the contact form submission to the configured Gmail address.
     * Sets the visitor's email as the Reply-To header.
     *
     * @param visitorName  Name of the visitor.
     * @param visitorEmail Email address of the visitor.
     * @param subject      Subject of the message.
     * @param messageText  Body content of the message.
     * @throws MessagingException           If SMTP message construction or delivery fails.
     * @throws UnsupportedEncodingException If sender name encoding fails.
     * @throws IllegalStateException        If SMTP credentials are not configured.
     */
    public void sendContactEmail(String visitorName, String visitorEmail, String subject, String messageText)
            throws MessagingException, UnsupportedEncodingException {

        if (!isConfigured()) {
            log.error("Email dispatch failed: Gmail SMTP credentials are not configured in environment variables or application properties.");
            throw new IllegalStateException(
                    "Gmail SMTP is not configured on the server. Please set MAIL_USERNAME and MAIL_PASSWORD (Gmail App Password) in your environment variables."
            );
        }

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        // The 'from' address must be the authenticated Gmail account for Google SMTP
        helper.setFrom(senderEmail, visitorName + " (via Portfolio)");
        helper.setTo(recipientEmail);
        // Reply-To is set to the visitor's email so clicking Reply responds directly to the visitor
        helper.setReplyTo(visitorEmail, visitorName);
        helper.setSubject("Portfolio Inquiry: " + subject);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));

        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="utf-8">
              <style>
                body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background-color: #f1f5f9; color: #0f172a; padding: 24px 12px; margin: 0; }
                .card { max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 12px; border: 1px solid #e2e8f0; box-shadow: 0 10px 15px -3px rgba(0,0,0,0.07); overflow: hidden; }
                .header { background: linear-gradient(135deg, #1e3a8a, #3b82f6); color: #ffffff; padding: 28px 24px; text-align: left; }
                .header h2 { margin: 0; font-size: 20px; font-weight: 700; letter-spacing: -0.02em; }
                .header p { margin: 6px 0 0; opacity: 0.85; font-size: 13px; }
                .body { padding: 24px; line-height: 1.6; }
                .field { margin-bottom: 20px; }
                .label { font-size: 11px; font-weight: 700; text-transform: uppercase; letter-spacing: 0.05em; color: #64748b; margin-bottom: 4px; }
                .value { font-size: 15px; color: #0f172a; font-weight: 500; word-break: break-word; }
                .reply-link { display: inline-block; margin-top: 4px; font-size: 13px; color: #2563eb; text-decoration: none; font-weight: 600; }
                .message-box { background: #f8fafc; padding: 18px; border-radius: 8px; border-left: 4px solid #3b82f6; font-size: 14px; color: #1e293b; line-height: 1.7; white-space: pre-wrap; word-break: break-word; }
                .footer { background: #f8fafc; padding: 16px 24px; border-top: 1px solid #e2e8f0; font-size: 12px; color: #94a3b8; text-align: center; }
              </style>
            </head>
            <body>
              <div class="card">
                <div class="header">
                  <h2>New Contact Message Received</h2>
                  <p>Submitted via your Portfolio Website on %s</p>
                </div>
                <div class="body">
                  <div class="field">
                    <div class="label">Sender Name</div>
                    <div class="value">%s</div>
                  </div>
                  <div class="field">
                    <div class="label">Sender Email</div>
                    <div class="value">
                      %s
                      <br/>
                      <a href="mailto:%s?subject=Re: %s" class="reply-link">✉ Click here to reply directly</a>
                    </div>
                  </div>
                  <div class="field">
                    <div class="label">Subject</div>
                    <div class="value">%s</div>
                  </div>
                  <div class="field">
                    <div class="label">Message</div>
                    <div class="message-box">%s</div>
                  </div>
                </div>
                <div class="footer">
                  This message was sent from the contact form on your portfolio website.
                </div>
              </div>
            </body>
            </html>
            """.formatted(
                escapeHtml(timestamp),
                escapeHtml(visitorName),
                escapeHtml(visitorEmail),
                escapeHtml(visitorEmail),
                escapeHtml(subject),
                escapeHtml(subject),
                escapeHtml(messageText)
            );

        String textContent = "New Portfolio Message\n\n"
                + "From: " + visitorName + " <" + visitorEmail + ">\n"
                + "Date: " + timestamp + "\n"
                + "Subject: " + subject + "\n\n"
                + "Message:\n" + messageText + "\n\n"
                + "Reply directly to: " + visitorEmail + "\n";

        helper.setText(textContent, htmlContent);

        mailSender.send(mimeMessage);
        log.info("Email successfully sent via Gmail SMTP to {} for visitor {}", recipientEmail, visitorEmail);
    }

    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }
}
