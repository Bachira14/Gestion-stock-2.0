package com.gestionstock.backend.service;

import com.gestionstock.backend.entity.Categorie;
import com.gestionstock.backend.repository.CategorieRepository;
import com.gestionstock.backend.repository.ProduitRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategorieService {

    private final CategorieRepository categorieRepository;
    private final ProduitRepository produitRepository;

    public CategorieService(CategorieRepository categorieRepository, ProduitRepository produitRepository) {
        this.categorieRepository = categorieRepository;
        this.produitRepository = produitRepository;
    }

    public List<Categorie> listerCategories() {
        return categorieRepository.findAll();
    }

    public Optional<Categorie> findById(Long id) {
        return categorieRepository.findById(id);
    }

    public Categorie creerCategorie(Categorie categorie) {
        if (categorie.getEtat() == null) {
            categorie.setEtat("ACTIF");
        }
        return categorieRepository.save(categorie);
    }

    public Categorie modifierCategorie(Long id, Categorie categorieDetails) {
        Categorie categorie = findById(id).orElseThrow(() -> new RuntimeException("Catégorie introuvable"));
        categorie.setLibelle(categorieDetails.getLibelle());
        return categorieRepository.save(categorie);
    }

    public void supprimerCategorie(Long id) {
        if (produitRepository.countByCategorieId(id) > 0) {
            throw new RuntimeException("Impossible de supprimer la catégorie car elle est utilisée par des produits.");
        }
        categorieRepository.deleteById(id);
    }
}