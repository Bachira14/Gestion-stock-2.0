package com.gestionstock.backend.security;

import com.gestionstock.backend.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final ClientService clientService;
    private final JwtUtil jwtUtil;

    public JwtAuthFilter(ClientService clientService, JwtUtil jwtUtil) {
        this.clientService = clientService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String header = request.getHeader("Authorization");
            if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
                logger.debug("Pas de header Authorization ou pas de Bearer token pour l'URL: {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }

            String token = header.substring(7);
            logger.debug("Token JWT extrait: {}", token);

            String email = jwtUtil.extractEmail(token);
            logger.debug("Email extrait du token: {}", email);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                logger.debug("Email non null et pas d'authentification dans le contexte. On charge l'utilisateur.");
                var userDetails = clientService.loadUserByUsername(email);

                logger.debug("Utilisateur chargé: {}. Validation du token...", userDetails.getUsername());
                if (jwtUtil.validateToken(token, userDetails.getUsername())) {
                    logger.info("Token VALIDE pour l'utilisateur {}. Mise en place de l'authentification.", email);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    logger.warn("Token INVALIDE pour l'utilisateur {}.", email);
                }
            } else {
                logger.debug("Email null ou une authentification existe déjà dans le contexte.");
            }
        } catch (Exception e) {
            // Log plus détaillé pour voir la cause exacte de l'échec
            logger.error("Erreur dans le filtre JWT: {} - {}", e.getClass().getName(), e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
