package com.fooddelivery.gateway.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    // Hardcoded public paths — no @Value injection needed
    private static final List<String> PUBLIC_PATHS = List.of(
            "/auth/login",
            "/auth/register/customer",
            "/auth/register/delivery-partner",
            "/auth/register/restaurant-owner",
            "/actuator",
            "/deliveries/partners/available"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.isTokenValid(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or expired token");
            return;
        }

        Long userId = jwtUtil.extractUserId(token);
        String role = jwtUtil.extractRole(token);
        String email = jwtUtil.extractEmail(token);

        MutableHttpServletRequest mutableRequest =
                new MutableHttpServletRequest(request);
        mutableRequest.putHeader("X-User-ID", String.valueOf(userId));
        mutableRequest.putHeader("X-User-Role", role);
        mutableRequest.putHeader("X-User-Email", email);

        filterChain.doFilter(mutableRequest, response);
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(publicPath -> {
            if (publicPath.endsWith("/**")) {
                String prefix = publicPath.substring(0, publicPath.length() - 3);
                return path.startsWith(prefix);
            }
            return path.startsWith(publicPath);
        });
    }
}