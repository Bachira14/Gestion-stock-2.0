package com.gestionstock.backend.dto;

import com.gestionstock.backend.entity.Client;
import java.math.BigDecimal;
import java.util.Date;

/**
 * DTO (Data Transfer Object) pour afficher une commande dans une liste.
 * Ne contient que les informations nécessaires pour la vue liste,
 * y compris le montant TTC calculé par le service.
 */
public class CommandeClientDto {

    private Long id;
    private Date dateCommande; // On garde java.util.Date pour être cohérent avec l'entité
    private String statut;
    private BigDecimal montantTTC;
    private Client client;

    public CommandeClientDto(Long id, Date dateCommande, String statut, BigDecimal montantTTC, Client client) {
        this.id = id;
        this.dateCommande = dateCommande;
        this.statut = statut;
        this.montantTTC = montantTTC;
        this.client = client;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Date getDateCommande() { return dateCommande; }
    public void setDateCommande(Date dateCommande) { this.dateCommande = dateCommande; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public BigDecimal getMontantTTC() { return montantTTC; }
    public void setMontantTTC(BigDecimal montantTTC) { this.montantTTC = montantTTC; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

}