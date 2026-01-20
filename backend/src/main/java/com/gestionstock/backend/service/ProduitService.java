package com.gestionstock.backend.service;

import com.gestionstock.backend.entity.Categorie;
import com.gestionstock.backend.entity.Produit;
import com.gestionstock.backend.repository.CategorieRepository;
import com.gestionstock.backend.repository.LigneCommandeClientRepository;
import com.gestionstock.backend.dto.CategoryOrderStatsDto;
import com.gestionstock.backend.repository.ProduitRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProduitService {

    private final ProduitRepository produitRepository;
    private final CategorieRepository categorieRepository;
    private final LigneCommandeClientRepository ligneCommandeClientRepository;

    public ProduitService(ProduitRepository produitRepository, CategorieRepository categorieRepository, LigneCommandeClientRepository ligneCommandeClientRepository) {
        this.produitRepository = produitRepository;
        this.categorieRepository = categorieRepository;
        this.ligneCommandeClientRepository = ligneCommandeClientRepository;
    }

    // Créer un produit
    public Produit createProduit(Produit produit) {
        // Assurer que la catégorie est une entité managée par JPA
        if (produit.getCategorie() != null && produit.getCategorie().getId() != null) {
            Categorie categorie = categorieRepository.findById(produit.getCategorie().getId())
                    .orElseThrow(() -> new RuntimeException("Catégorie introuvable avec l'id : " + produit.getCategorie().getId()));
            produit.setCategorie(categorie);
        } else {
            // Cette vérification est déjà dans le contrôleur, mais c'est une bonne pratique de se protéger ici aussi.
            throw new RuntimeException("L'ID de la catégorie est manquant pour la sauvegarde du produit.");
        }

        if (produit.getEtat() == null) {
            produit.setEtat("ACTIF");
        }
        return produitRepository.save(produit);
    }

    // Mettre à jour un produit existant
    public Produit updateProduit(Long id, Produit produitDetails) {
        // 1. Récupérer le produit existant
        Produit produitExistant = produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'id : " + id));

        // 2. Mettre à jour les champs fournis
        produitExistant.setLibelle(produitDetails.getLibelle());
        produitExistant.setDescription(produitDetails.getDescription());
        produitExistant.setPrixUnitaire(produitDetails.getPrixUnitaire());
        produitExistant.setQuantite(produitDetails.getQuantite());

        // Si un nouveau nom d'image est fourni (par le contrôleur), on le met à jour.
        if (produitDetails.getImageProduit() != null && !produitDetails.getImageProduit().isEmpty()) {
            produitExistant.setImageProduit(produitDetails.getImageProduit());
        }

        // 3. Mettre à jour la catégorie si elle est fournie
        if (produitDetails.getCategorie() != null && produitDetails.getCategorie().getId() != null) {
            Categorie categorie = categorieRepository.findById(produitDetails.getCategorie().getId())
                    .orElseThrow(() -> new RuntimeException("Catégorie introuvable avec l'id : " + produitDetails.getCategorie().getId()));
            produitExistant.setCategorie(categorie);
        } else {
            throw new RuntimeException("L'ID de la catégorie est manquant pour la mise à jour du produit.");
        }

        // 4. Sauvegarder l'entité mise à jour
        return produitRepository.save(produitExistant);
    }

    public Page<Produit> listerProduits(String searchTerm, Pageable pageable) {
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            return produitRepository.findByLibelleContainingIgnoreCase(searchTerm, pageable);
        }
        return produitRepository.findAll(pageable);
    }

    // Trouver un produit par ID
    public Optional<Produit> findById(Long id) {
        return produitRepository.findById(id);
    }

    // Supprimer un produit
    public void supprimerProduit(Long id) {
        produitRepository.deleteById(id);
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalProduits", produitRepository.count());
        stats.put("produitPlusCher", produitRepository.findTopByOrderByPrixUnitaireDesc().orElse(null));
        // Le nom est clarifié pour correspondre à la logique (basée sur la quantité en stock)
        stats.put("produitPlusEnStock", produitRepository.findTopByOrderByQuantiteDesc().orElse(null));
        return stats;
    }

    public List<CategoryOrderStatsDto> getCategoryOrderStats() {
        return ligneCommandeClientRepository.findTotalQuantitiesPerCategory();
    }
}
