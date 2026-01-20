package com.gestionstock.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {

    // Exception personnalisée pour un message clair dans la console
    public static class EntryPointDebugException extends RuntimeException {
        public EntryPointDebugException(String message) {
            super(message);
        }
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        // Si la requête est pour /api/test, on lance une exception de débogage
        if ("/api/test".equals(request.getServletPath())) {
            throw new EntryPointDebugException(
                "POINT D'ENTRÉE D'AUTHENTIFICATION ATTEINT pour /api/test. " +
                "Cela prouve que Spring Security tente d'authentifier une route qui devrait être publique (`permitAll`). " +
                "La cause est probablement un filtre (comme JwtAuthFilter) qui s'exécute et échoue avant que la règle `permitAll` ne soit appliquée. " +
                "L'exception originale était : " + authException.getClass().getName() + " - " + authException.getMessage()
            );
        }

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Non autorisé");
        body.put("message", authException.getMessage());
        body.put("path", request.getServletPath());

        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }
}