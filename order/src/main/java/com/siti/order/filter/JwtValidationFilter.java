package com.siti.order.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtValidationFilter extends OncePerRequestFilter {

    // RestTemplate ber-@LoadBalanced ini akan resolve "auth-service" lewat Eureka,
    // jadi tidak perlu hardcode IP/port (cocok untuk Docker maupun jalan manual,
    // selama Order Service & Auth Service terdaftar di Eureka yang sama).
    @Autowired
    @Qualifier("restTemplate")
    private RestTemplate restTemplate;

    private static final String AUTH_SERVICE_URL = "http://auth-service/api/auth/validate";

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-ui");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path.equals("/orders") && request.getMethod().equals("POST")) {

            String authHeader = request.getHeader("Authorization");

            System.out.println("=== JWT FILTER ===");
            System.out.println("Auth Header: " + authHeader);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Token tidak ditemukan. Silakan login terlebih dahulu.\"}");
                return;
            }

            try {
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", authHeader);
                HttpEntity<String> entity = new HttpEntity<>(headers);

                System.out.println("Validating token ke: " + AUTH_SERVICE_URL);

                ResponseEntity<Boolean> validationResponse = restTemplate.exchange(
                    AUTH_SERVICE_URL,
                    HttpMethod.POST,
                    entity,
                    Boolean.class
                );

                System.out.println("Validation result: " + validationResponse.getBody());

                if (validationResponse.getBody() == null || !validationResponse.getBody()) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Token tidak valid atau sudah expired.\"}");
                    return;
                }

                // Token valid -> isi SecurityContext supaya Spring Security
                // (yang mewajibkan .authenticated() di SecurityConfig) meloloskan request.
                // Tanpa baris ini, request akan tetap kena 403 walau token-nya benar.
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                "authenticated-user",
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER"))
                        );
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                System.err.println("Error validasi token: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Gagal validasi token: " + e.getMessage() + "\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
