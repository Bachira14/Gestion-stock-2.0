package com.gestionstock.backend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.math.RoundingMode;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "commande_client")
public class CommandeClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Date dateCommande;

    private String statut = "EN_COURS";
    private String etat;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @OneToMany(mappedBy = "commandeClient", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<LigneCommandeClient> ligneCommandes;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Date getDateCommande() { return dateCommande; }

    public void setDateCommande(Date dateCommande) { this.dateCommande = dateCommande; }

    public String getStatut() { return statut; }

    public void setStatut(String statut) { this.statut = statut; }

    public String getEtat() { return etat; }

    public void setEtat(String etat) { this.etat = etat; }

    public Client getClient() { return client; }

    public void setClient(Client client) { this.client = client; }

    public List<LigneCommandeClient> getLigneCommandes() {
        return ligneCommandes;
    }

    public void setLigneCommandes(List<LigneCommandeClient> ligneCommandes) {
        this.ligneCommandes = ligneCommandes;
    }

}