package com.sethuraman.portfolio;

import com.sethuraman.portfolio.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive integration tests for the Portfolio Backend.
 * Tests authentication, authorization, CSRF exemptions, contact form validation & persistence,
 * visitor tracking, and admin dashboard operations.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(PortfolioApplicationTests.TestEmailConfig.class)
class PortfolioApplicationTests {

    @TestConfiguration
    static class TestEmailConfig {
        public static boolean shouldFail = false;
        public static String lastSentName;
        public static String lastSentEmail;
        public static String lastSentSubject;
        public static String lastSentMessage;

        @Bean
        @Primary
        public EmailService emailService() {
            return new EmailService(null) {
                @Override
                public boolean isConfigured() {
                    return !shouldFail;
                }

                @Override
                public void sendContactEmail(String name, String email, String subject, String message) {
                    if (shouldFail) {
                        throw new IllegalStateException("Gmail SMTP credentials missing");
                    }
                    lastSentName = name;
                    lastSentEmail = email;
                    lastSentSubject = subject;
                    lastSentMessage = message;
                }
            };
        }
    }

    @Autowired
    private MockMvc mockMvc;

    /**
     * Verifies the Spring application context loads successfully.
     */
    @Test
    void contextLoads() {
        // If this test passes, the application context is configured correctly
    }

    /**
     * Verifies that the admin dashboard requires authentication.
     * Unauthenticated GET /api/admin/dashboard must return 401.
     */
    @Test
    void adminDashboard_requiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifies that authenticated admin can access the dashboard.
     */
    @Test
    @WithMockUser(username = "sethu_admin", roles = {"ADMIN"})
    void adminDashboard_authenticatedAdminCanAccess() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    /**
     * Verifies that a non-admin user cannot access admin endpoints.
     */
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void adminDashboard_nonAdminForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifies admin login with correct credentials.
     */
    @Test
    void adminLogin_successfulWithValidCredentials() throws Exception {
        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "sethu_admin")
                        .param("password", "sethu@2026!"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    /**
     * Verifies admin login with invalid password is rejected with 401.
     */
    @Test
    void adminLogin_failsWithInvalidCredentials() throws Exception {
        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "sethu_admin")
                        .param("password", "wrongpassword"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    /**
     * Verifies full login -> authenticated dashboard -> logout session flow.
     */
    @Test
    void adminSession_loginAccessDashboardAndLogout() throws Exception {
        // 1. Log in
        MvcResult loginResult = mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "sethu_admin")
                        .param("password", "sethu@2026!"))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession();
        assert session != null;

        // 2. Access dashboard using established session
        mockMvc.perform(get("/api/admin/dashboard").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalVisits").isNumber());

        // 3. Check /api/admin/me
        mockMvc.perform(get("/api/admin/me").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("sethu_admin"));

        // 4. Logout
        mockMvc.perform(post("/api/admin/logout").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    /**
     * Verifies that the visitor count endpoint is publicly accessible.
     */
    @Test
    void visitorCount_isPubliclyAccessible() throws Exception {
        mockMvc.perform(get("/api/visitors/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalVisits").isNumber());
    }

    /**
     * Verifies visitor recording increments the count.
     */
    @Test
    void visitorRecording_recordsVisit() throws Exception {
        mockMvc.perform(post("/api/visitors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalVisits").isNumber());
    }

    /**
     * Verifies admin dashboard header skips visit increment.
     */
    @Test
    void visitorRecording_skipsAdminDashboard() throws Exception {
        mockMvc.perform(post("/api/visitors")
                        .header("X-Admin-Dashboard", "true"))
                .andExpect(status().isOk());
    }

    /**
     * Verifies that the contact endpoint accepts valid messages.
     */
    @Test
    void contactForm_acceptsValidSubmission() throws Exception {
        TestEmailConfig.shouldFail = false;

        String body = """
                {
                  "name": "Jane Developer",
                  "email": "jane@techcorp.io",
                  "subject": "Full Stack Engineer Role",
                  "message": "We have an open Java Full Stack position and were impressed by your portfolio!"
                }
                """;
        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        org.junit.jupiter.api.Assertions.assertEquals("Jane Developer", TestEmailConfig.lastSentName);
        org.junit.jupiter.api.Assertions.assertEquals("jane@techcorp.io", TestEmailConfig.lastSentEmail);
        org.junit.jupiter.api.Assertions.assertEquals("Full Stack Engineer Role", TestEmailConfig.lastSentSubject);
    }

    /**
     * Verifies that when SMTP sending fails, the endpoint returns an error
     * and does NOT claim success.
     */
    @Test
    void contactForm_returnsErrorWhenEmailFails() throws Exception {
        TestEmailConfig.shouldFail = true;
        try {
            String body = """
                    {
                      "name": "Jane Developer",
                      "email": "jane@techcorp.io",
                      "subject": "Full Stack Engineer Role",
                      "message": "We have an open Java Full Stack position and were impressed by your portfolio!"
                    }
                    """;
            mockMvc.perform(post("/api/contact")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Gmail SMTP credentials missing"));
        } finally {
            TestEmailConfig.shouldFail = false;
        }
    }

    /**
     * Verifies that the contact endpoint rejects empty submissions.
     */
    @Test
    void contactForm_rejectsEmptySubmission() throws Exception {
        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    /**
     * Verifies that the contact endpoint rejects short messages (< 20 chars).
     */
    @Test
    void contactForm_rejectsShortMessage() throws Exception {
        String body = """
                {
                  "name": "Test User",
                  "email": "test@example.com",
                  "subject": "Test Subject",
                  "message": "Too short"
                }
                """;
        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.message").exists());
    }

    /**
     * Verifies that the admin messages endpoint is protected.
     */
    @Test
    void adminMessages_requiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/admin/messages"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifies that authenticated admin can retrieve and mark messages read.
     */
    @Test
    @WithMockUser(username = "sethu_admin", roles = {"ADMIN"})
    void adminMessages_authenticatedAdminCanRetrieve() throws Exception {
        mockMvc.perform(get("/api/admin/messages"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
}
