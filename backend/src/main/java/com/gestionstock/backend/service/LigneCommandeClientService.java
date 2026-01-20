package com.gestionstock.backend.service;

import com.gestionstock.backend.entity.LigneCommandeClient;
import com.gestionstock.backend.entity.CommandeClient;
import com.gestionstock.backend.entity.Produit;
import com.gestionstock.backend.repository.LigneCommandeClientRepository;
import com.gestionstock.backend.repository.ProduitRepository;
import com.gestionstock.backend.repository.CommandeClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class LigneCommandeClientService {

    private final LigneCommandeClientRepository ligneCommandeClientRepository;
    private final CommandeClientRepository commandeClientRepository;
    private final ProduitRepository produitRepository;

    public LigneCommandeClientService(LigneCommandeClientRepository ligneCommandeClientRepository,
                                      CommandeClientRepository commandeClientRepository,
                                      ProduitRepository produitRepository) {
        this.ligneCommandeClientRepository = ligneCommandeClientRepository;
        this.commandeClientRepository = commandeClientRepository;
        this.produitRepository = produitRepository;
    }

    /**
     * Ajouter une ligne de commande pour une commande existante
     */
    @Transactional
    public LigneCommandeClient ajouterLigne(Long commandeId, Long produitId, Integer quantite) {

        // Récupérer la commande
        CommandeClient commande = commandeClientRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande introuvable"));

        // Récupérer le produit
        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new RuntimeException("Produit introuvable avec l'id : " + produitId));

        // Créer la ligne de commande
        LigneCommandeClient ligne = new LigneCommandeClient();
        ligne.setCommandeClient(commande);
        ligne.setProduit(produit);
        ligne.setQuantite(quantite);
        ligne.setPrixUnitaire(produit.getPrixUnitaire());

        // Sauvegarder la ligne de commande
        ligneCommandeClientRepository.save(ligne);

        return ligne;
    }

    /**
     * Calculer et mettre à jour le montant total d’une commande
     */
    private void mettreAJourMontantTotal(CommandeClient commande) {
        List<LigneCommandeClient> lignes = ligneCommandeClientRepository.findByCommandeClientId(commande.getId());

        BigDecimal total = lignes.stream()
                .map(l -> l.getPrixUnitaire().multiply(BigDecimal.valueOf(l.getQuantite())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

    }
    public List<LigneCommandeClient> getToutesLesLignes() {
    return ligneCommandeClientRepository.findAll();
}

}
