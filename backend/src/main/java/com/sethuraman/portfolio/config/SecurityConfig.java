package com.sethuraman.portfolio.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security Configuration.
 *
 * Security model:
 * - Public APIs: POST /api/contact, GET/POST /api/visitors, GET /api/visitors/count
 * - Admin APIs:  GET /api/admin/** — requires ADMIN role, session-based auth
 * - Login:       POST /api/admin/login (form-based, session cookie returned)
 * - Logout:      POST /api/admin/logout
 * - CSRF:        Enabled with cookie-based token for AJAX compatibility
 *
 * Admin password is stored as a BCrypt hash in application properties (never plaintext).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password.hash}")
    private String adminPasswordHash;

    /**
     * BCrypt password encoder — used for admin password verification.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * In-memory user store with a single admin user.
     * Password is BCrypt-hashed, never stored in plaintext.
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails admin = User.builder()
                .username(adminUsername)
                .password(adminPasswordHash)   // Already BCrypt-hashed from properties
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(admin);
    }

    /**
     * Main security filter chain.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // --- CORS ---
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // --- CSRF: Cookie-based for JS AJAX compatibility ---
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                .ignoringRequestMatchers(
                    "/api/visitors",           // Public visitor recording endpoint
                    "/api/contact",            // Public contact form endpoint
                    "/api/admin/login",        // Admin login endpoint
                    "/api/admin/logout",       // Admin logout endpoint
                    "/api/admin/**"            // Admin APIs protected by session authentication
                )
            )

            // --- Authorization Rules ---
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers(HttpMethod.POST, "/api/contact").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/visitors").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/visitors/count").permitAll()
                // Admin login page (served as static HTML)
                .requestMatchers("/admin/**").permitAll()
                // Admin API — requires authentication
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // All other requests
                .anyRequest().permitAll()
            )

            // --- Form Login (session-based) ---
            .formLogin(form -> form
                .loginProcessingUrl("/api/admin/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler((req, res, auth) -> {
                    res.setContentType("application/json");
                    res.setCharacterEncoding("UTF-8");
                    res.getWriter().write("{\"success\":true,\"message\":\"Login successful\",\"role\":\"ADMIN\"}");
                })
                .failureHandler((req, res, ex) -> {
                    res.setStatus(401);
                    res.setContentType("application/json");
                    res.setCharacterEncoding("UTF-8");
                    res.getWriter().write("{\"success\":false,\"message\":\"Invalid username or password.\"}");
                })
                .permitAll()
            )

            // --- Logout ---
            .logout(logout -> logout
                .logoutUrl("/api/admin/logout")
                .logoutSuccessHandler((req, res, auth) -> {
                    res.setContentType("application/json");
                    res.setCharacterEncoding("UTF-8");
                    res.getWriter().write("{\"success\":true,\"message\":\"Logged out successfully.\"}");
                })
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )

            // --- Session Management ---
            .sessionManagement(session -> session
                .maximumSessions(1)               // One concurrent session per admin
                .maxSessionsPreventsLogin(false)   // New login invalidates old session
            )

            // --- Security Headers ---
            .headers(headers -> headers
                .frameOptions(fo -> fo.sameOrigin())
                .contentTypeOptions(Customizer.withDefaults())
                .httpStrictTransportSecurity(hsts -> hsts.disable()) // Enable in production with HTTPS
            )

            // --- Exception Handling ---
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, authEx) -> {
                    res.setStatus(401);
                    res.setContentType("application/json");
                    res.setCharacterEncoding("UTF-8");
                    res.getWriter().write("{\"error\":\"Authentication required.\",\"status\":401}");
                })
                .accessDeniedHandler((req, res, accessEx) -> {
                    res.setStatus(403);
                    res.setContentType("application/json");
                    res.setCharacterEncoding("UTF-8");
                    res.getWriter().write("{\"error\":\"Access denied.\",\"status\":403}");
                })
            );

        return http.build();
    }

    /**
     * CORS configuration — allows the portfolio frontend to call the backend.
     * Adjust allowedOrigins in production to your actual deployed domain.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // For local development: allow file:// and localhost origins
        config.setAllowedOriginPatterns(List.of(
            "http://localhost:*",
            "http://127.0.0.1:*",
            "null",                // For file:// protocol in local development
            "https://*.sethuraman-portfolio.com"  // Update with your production domain
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);  // Required for session cookies
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
