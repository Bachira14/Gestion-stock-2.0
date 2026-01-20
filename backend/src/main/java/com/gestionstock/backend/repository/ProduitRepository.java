package com.gestionstock.backend.repository;

import com.gestionstock.backend.entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProduitRepository extends JpaRepository<Produit, Long> {

    /**
     * Compte le nombre de produits associés à une catégorie.
     * Utilisé dans CategorieService pour empêcher la suppression d'une catégorie utilisée.
     * @param categorieId L'ID de la catégorie.
     * @return Le nombre de produits.
     */
    long countByCategorieId(Long categorieId);

    /**
     * Trouve le premier produit en le triant par prix unitaire décroissant.
     * Utilisé pour les statistiques du tableau de bord.
     * @return Un Optional contenant le produit le plus cher, ou vide s'il n'y a aucun produit.
     */
    Optional<Produit> findTopByOrderByPrixUnitaireDesc();

    /**
     * Trouve le premier produit en le triant par quantité en stock décroissante.
     * Utilisé pour les statistiques du tableau de bord.
     * @return Un Optional contenant le produit avec le plus de stock, ou vide s'il n'y a aucun produit.
     */
    Optional<Produit> findTopByOrderByQuantiteDesc();

    Page<Produit> findByLibelleContainingIgnoreCase(String libelle, Pageable pageable);
}