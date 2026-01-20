package com.gestionstock.backend.repository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import com.gestionstock.backend.entity.CommandeClient;

public interface CommandeClientRepository extends JpaRepository<CommandeClient, Long> {
    
    @Override
    @EntityGraph(attributePaths = {"ligneCommandes", "ligneCommandes.produit", "client"})
    Page<CommandeClient> findAll(Pageable pageable);

    // Trouver toutes les commandes d'un client avec pagination
    @EntityGraph(attributePaths = {"ligneCommandes", "ligneCommandes.produit", "client"})
    Page<CommandeClient> findByClientId(Long clientId, Pageable pageable);
    // Trouver toutes les commandes d’un client
    List<CommandeClient> findByClientId(Long clientId);

    // Trouver toutes les commandes selon leur statut (ex: EN_COURS, LIVREE, etc.)
    List<CommandeClient> findByStatut(String statut);

    // Trouver toutes les commandes actives/inactives
    List<CommandeClient> findByEtat(String etat);

    // Trouver les commandes par date
    List<CommandeClient> findByDateCommande(LocalDate date);

    // Exemple : toutes les commandes d’un client données à une date précise
    List<CommandeClient> findByClientIdAndDateCommande(Long clientId, LocalDate date);
}
