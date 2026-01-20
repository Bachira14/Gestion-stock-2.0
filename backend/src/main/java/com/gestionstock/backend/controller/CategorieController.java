package com.gestionstock.backend.controller;

import com.gestionstock.backend.entity.Categorie;
import com.gestionstock.backend.service.CategorieService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategorieController {

    private final CategorieService categorieService;

    public CategorieController(CategorieService categorieService) {
        this.categorieService = categorieService;
    }

    // Lister toutes les catégories
    @GetMapping
    public List<Categorie> getCategories() {
        return categorieService.listerCategories();
    }

    // Trouver une catégorie par ID
    @GetMapping("/{id}")
    public Categorie getCategorie(@PathVariable Long id) {
        return categorieService.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));
    }

    // Créer une nouvelle catégorie
    @PostMapping
    public Categorie creerCategorie(@RequestBody Categorie categorie) {
        return categorieService.creerCategorie(categorie);
    }

    // Modifier une catégorie existante
    @PutMapping("/{id}")
    public Categorie modifierCategorie(@PathVariable Long id, @RequestBody Categorie categorie) {
        return categorieService.modifierCategorie(id, categorie);
    }

    // Supprimer une catégorie
    @DeleteMapping("/{id}")
    public void supprimerCategorie(@PathVariable Long id) {
        categorieService.supprimerCategorie(id);
    }
}
