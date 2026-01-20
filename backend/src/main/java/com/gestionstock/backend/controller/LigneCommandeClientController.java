package com.gestionstock.backend.controller;

import com.gestionstock.backend.entity.LigneCommandeClient;
import com.gestionstock.backend.service.LigneCommandeClientService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lignes-commande")
public class LigneCommandeClientController {

    private final LigneCommandeClientService ligneCommandeClientService;

    public LigneCommandeClientController(LigneCommandeClientService ligneCommandeClientService) {
        this.ligneCommandeClientService = ligneCommandeClientService;
    }

    /**
     * Ajouter une ligne de commande
     */
    @PostMapping
    public LigneCommandeClient ajouterLigne(
            @RequestParam Long commandeId,
            @RequestParam Long produitId,
            @RequestParam Integer quantite) {

        return ligneCommandeClientService.ajouterLigne(commandeId, produitId, quantite);
    }

    /**
     * Lister toutes les lignes de commande
     */
    @GetMapping
    public List<LigneCommandeClient> getToutesLesLignes() {
        return ligneCommandeClientService.getToutesLesLignes();
    }
}
