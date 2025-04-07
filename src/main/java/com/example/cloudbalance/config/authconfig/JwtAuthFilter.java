package com.example.cloudbalance.config.authconfig;

import com.example.cloudbalance.entity.auth.SessionEntity;
import com.example.cloudbalance.service.authservice.JwtService;
import com.example.cloudbalance.service.authservice.CustomUserDetailsService;
import com.example.cloudbalance.entity.auth.UsersEntity;
import com.example.cloudbalance.repository.SessionRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.ExpiredJwtException;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final SessionRepository sessionRepository;

    public JwtAuthFilter(JwtService jwtService, CustomUserDetailsService userDetailsService, SessionRepository sessionRepository) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.sessionRepository = sessionRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);  // Remove "Bearer " prefix
        String userEmail;

        try {
            userEmail = jwtService.extractUsername(token);
        } catch (ExpiredJwtException e) {
            // Token has expired, handle the exception
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("JWT token has expired");
            return;
        }

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            if (jwtService.isTokenValid(token, userEmail)) {
                UsersEntity user = userDetailsService.loadUserEntityByUsername(userEmail);

                sessionRepository.findByTokenId(token).orElseGet(() -> {
                    SessionEntity newSession = SessionEntity.builder()
                            .tokenId(token)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .isValid(true)
                            .user(user)
                            .build();
                    sessionRepository.save(newSession);
                    return newSession;
                });

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}