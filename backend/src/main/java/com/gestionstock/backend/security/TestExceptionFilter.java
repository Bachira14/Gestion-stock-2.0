package com.gestionstock.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class TestExceptionFilter extends OncePerRequestFilter { // Ce filtre est pour le débogage

    // Exception personnalisée pour un message clair dans la console
    public static class DebugException extends RuntimeException {
        public DebugException(String message) {
            super(message);
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getServletPath();
        if ("/api/test".equals(path)) {
            // On lance une exception pour voir la pile d'appel complète
            throw new DebugException("FILTRE DE DÉBOGAGE: La requête pour /api/test a été interceptée. Si vous voyez cette exception, cela signifie qu'un filtre s'exécute avant même que les règles de sécurité (`permitAll`) ne soient évaluées. Le problème se situe probablement dans la configuration de la chaîne de filtres (SecurityFilterChain).");
        }
        filterChain.doFilter(request, response);
    }
}
