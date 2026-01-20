package com.gestionstock.backend.controller;

import com.gestionstock.backend.dto.LoginRequest;
import com.gestionstock.backend.entity.Client;
import com.gestionstock.backend.security.ClientAuthService;
import com.gestionstock.backend.security.JwtUtil;
import com.gestionstock.backend.service.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final ClientAuthService clientAuthService;
    private final JwtUtil jwtUtil;
    private final ClientService clientService;

    public AuthController(ClientAuthService clientAuthService, JwtUtil jwtUtil, ClientService clientService) {
        this.clientAuthService = clientAuthService;
        this.jwtUtil = jwtUtil;
        this.clientService = clientService;
    }

    // Login et génération du token JWT
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Log amélioré pour voir exactement ce que le backend reçoit
            logger.info("Requête de login reçue pour l'email: '{}'", loginRequest.getEmail());
            String token = clientAuthService.login(loginRequest);

            // Puisque l'authentification a réussi, on peut récupérer l'utilisateur.
            // C'est sûr car login() aurait levé une exception si l'authentification avait échoué.
            Client client = (Client) clientService.loadUserByUsername(loginRequest.getEmail());

            logger.info("Authentification réussie, token et client retournés pour {}", loginRequest.getEmail());
            return ResponseEntity.ok(Map.of("token", token, "client", client));

        } catch (UsernameNotFoundException | BadCredentialsException e) {
            // Regrouper les exceptions d'authentification pour un message d'erreur unifié et plus de sécurité.
            logger.warn("Échec login pour {}: {}", loginRequest.getEmail(), e.getMessage());
            return ResponseEntity.status(401).body(Map.of("error", "Email ou mot de passe incorrect."));

        } catch (Exception e) {
            logger.error("Erreur inattendue lors du login pour {}", loginRequest.getEmail(), e);
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Erreur serveur inattendue",
                    "exception_type", e.getClass().getName(),
                    "exception_message", e.getMessage()
            ));
        }
    }

    // Endpoint pour récupérer le client connecté à partir du token
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal Client client) {
        if (client == null) {
            // Ce cas ne devrait pas arriver si le filtre JWT fonctionne et que l'endpoint est sécurisé.
            return ResponseEntity.status(401).body(Map.of("error", "Token invalide ou utilisateur non trouvé"));
        }
        // L'objet client est déjà peuplé par Spring Security grâce au token JWT.
        // On peut le retourner directement.
        // Le mot de passe ne sera pas sérialisé grâce à @JsonIgnore dans l'entité.
        return ResponseEntity.ok(client);
    }
}
