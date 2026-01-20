package com.gestionstock.backend.service;

import com.gestionstock.backend.dto.CommandeClientDto;
import com.gestionstock.backend.entity.CommandeClient;
import com.gestionstock.backend.entity.Client;
import com.gestionstock.backend.entity.LigneCommandeClient;
import com.gestionstock.backend.entity.Produit;
import com.gestionstock.backend.repository.CommandeClientRepository;
import com.gestionstock.backend.repository.ProduitRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

@Service
public class CommandeClientService {

    private final CommandeClientRepository commandeClientRepository;
    private final ProduitRepository produitRepository;

    public CommandeClientService(CommandeClientRepository commandeClientRepository,
                                 ProduitRepository produitRepository) {
        this.commandeClientRepository = commandeClientRepository;
        this.produitRepository = produitRepository;
    }

    // ---------------------------
    // Création d'une commande
    // ---------------------------
    @Transactional
    public CommandeClient creerCommande(CommandeClient commande, Client client) {
        commande.setEtat("ACTIF");
        commande.setStatut("LIVREE");
        commande.setDateCommande(new Date());
        commande.setClient(client);

        if (commande.getLigneCommandes() == null || commande.getLigneCommandes().isEmpty()) {
            throw new RuntimeException("Une commande doit contenir au moins une ligne de commande.");
        }

        for (LigneCommandeClient ligne : commande.getLigneCommandes()) {
            Produit produit = produitRepository.findById(ligne.getProduit().getId())
                    .orElseThrow(() -> new RuntimeException("Produit introuvable : " + ligne.getProduit().getId()));

            if (produit.getQuantite() < ligne.getQuantite()) {
                throw new RuntimeException(
                    "Stock insuffisant pour le produit : " + produit.getLibelle() +
                    ". Stock disponible : " + produit.getQuantite()
                );
            }

            // Déduire du stock
            produit.setQuantite(produit.getQuantite() - ligne.getQuantite());
            produitRepository.save(produit);

            // Associer infos à la ligne
            ligne.setProduit(produit);
            ligne.setPrixUnitaire(produit.getPrixUnitaire());
            ligne.setCommandeClient(commande);
        }

        return commandeClientRepository.save(commande);
    }

    // 
    // Calcul du montant TTC
    // ---------------------------

    
     
    public BigDecimal calculerTotalTtc(CommandeClient commande) {
        BigDecimal totalTTC = BigDecimal.ZERO;
        
        if (commande.getLigneCommandes() != null) {
            for (LigneCommandeClient ligne : commande.getLigneCommandes()) {
                BigDecimal sousTotalHT = ligne.getPrixUnitaire()
                .multiply(BigDecimal.valueOf(ligne.getQuantite()));
                
                // TVA 19,25 % Cameroun
                BigDecimal tva = sousTotalHT.multiply(BigDecimal.valueOf(0.1925));
                
                totalTTC = totalTTC.add(sousTotalHT.add(tva));
            }
            
        }
        
        return totalTTC.setScale(2, RoundingMode.HALF_UP);
    }
    

    // ---------------------------
    // Lister toutes les commandes
    // ---------------------------
    public Page<CommandeClientDto> listerCommandesPourClientOuAdmin(Client client, Pageable pageable) {
        if ("phenixnguifo@gmail.com".equals(client.getEmail())) {
            return commandeClientRepository.findAll(pageable).map(this::convertToDto);
        } else {
            return commandeClientRepository.findByClientId(client.getId(), pageable).map(this::convertToDto);
        }
    }

    private CommandeClientDto convertToDto(CommandeClient commande) {
        BigDecimal montantTTC = calculerTotalTtc(commande);
        return new CommandeClientDto(commande.getId(), commande.getDateCommande(), commande.getStatut(), montantTTC, commande.getClient());
    }

    // ---------------------------
    // Trouver une commande par ID
    // ---------------------------
    public CommandeClient findById(Long id) {
        return commandeClientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande introuvable"));
    }

    // ---------------------------
    // Supprimer une commande
    // ---------------------------
    @Transactional
    public void supprimerCommande(Long id) {
        CommandeClient commande = commandeClientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande introuvable avec l'id: " + id));
        commandeClientRepository.delete(commande);
    }
       // Récupérer la quantité totale d'une commande
    public int getQuantiteTotale(Long commandeId) {
        CommandeClient commande = commandeClientRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));

        return commande.getLigneCommandes()
                       .stream()
                       .mapToInt(LigneCommandeClient::getQuantite)
                       .sum();
    }
}

