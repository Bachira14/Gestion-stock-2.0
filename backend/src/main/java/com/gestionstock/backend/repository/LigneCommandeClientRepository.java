package com.gestionstock.backend.repository;

import com.gestionstock.backend.entity.LigneCommandeClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.gestionstock.backend.dto.CategoryOrderStatsDto;

import java.util.List;

public interface LigneCommandeClientRepository extends JpaRepository<LigneCommandeClient, Long> {
    List<LigneCommandeClient> findByCommandeClientId(Long commandeId);

    @Query("SELECT new com.gestionstock.backend.dto.CategoryOrderStatsDto(p.categorie.libelle, SUM(lc.quantite)) FROM LigneCommandeClient lc JOIN lc.produit p GROUP BY p.categorie.libelle")
    List<CategoryOrderStatsDto> findTotalQuantitiesPerCategory();
}