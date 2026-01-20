package com.gestionstock.backend.controller;

import com.gestionstock.backend.dto.CommandeClientDto;
import com.gestionstock.backend.entity.Client;
import com.gestionstock.backend.entity.CommandeClient;
import com.gestionstock.backend.service.CommandeClientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/commandes")
public class CommandeClientController {

    private final CommandeClientService commandeClientService;

    public CommandeClientController(CommandeClientService commandeClientService) {
        this.commandeClientService = commandeClientService;
    }

    @PostMapping
    public ResponseEntity<CommandeClient> createCommande(@RequestBody CommandeClient commande, @AuthenticationPrincipal Client client) {
        if (client == null) {
            throw new UsernameNotFoundException("Client non authentifié pour créer une commande.");
        }
        CommandeClient nouvelleCommande = commandeClientService.creerCommande(commande, client);
        return ResponseEntity.ok(nouvelleCommande);
    }

    @GetMapping
    public Page<CommandeClientDto> getCommandes(@AuthenticationPrincipal Client client, Pageable pageable) {
        if (client == null) {
            throw new UsernameNotFoundException("Client non authentifié.");
        }

        // Logique : si l'utilisateur est l'admin, on retourne toutes les commandes.
        // Sinon, on ne retourne que ses propres commandes.
        return commandeClientService.listerCommandesPourClientOuAdmin(client, pageable);
    }

    @GetMapping("/{id}")
    public CommandeClient getCommandeById(@PathVariable Long id) {
        // Note : Pour la sécurité, on pourrait vérifier si l'utilisateur est admin ou si la commande lui appartient.
        return commandeClientService.findById(id);
    }
}