package com.gestionstock.backend.controller;

import com.gestionstock.backend.entity.Produit;
import com.gestionstock.backend.dto.CategoryOrderStatsDto;
import com.gestionstock.backend.service.ProduitService;
import com.gestionstock.backend.service.CategorieService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/produits")
public class ProduitController {

    private final ProduitService produitService;
    private final CategorieService categorieService;

    @Value("${upload.path}")
    private String uploadDir;

    public ProduitController(ProduitService produitService, CategorieService categorieService) {
        this.produitService = produitService;
        this.categorieService = categorieService;
    }

    @PutMapping(value = "/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public Produit modifierProduit(
            @PathVariable Long id,
            @RequestPart("produit") Produit produitDetails,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {

        // Gérer le téléversement de la nouvelle image si elle est fournie
        if (imageFile != null && !imageFile.isEmpty()) {
            String filename = StringUtils.cleanPath(imageFile.getOriginalFilename());
            Path uploadPath = Paths.get(this.uploadDir);

            try (InputStream inputStream = imageFile.getInputStream()) {
                Path filePath = uploadPath.resolve(filename);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                produitDetails.setImageProduit(filename); // Mettre à jour avec le nom de la nouvelle image
            } catch (IOException e) {
                throw new IOException("Impossible de sauvegarder la nouvelle image : " + filename, e);
            }
        }

        // Appel de la nouvelle méthode de service pour la mise à jour
        return produitService.updateProduit(id, produitDetails);
    }

    //  Lister tous les produits
    @GetMapping
    public Page<Produit> getProduits(
            @RequestParam(name = "searchTerm", required = false, defaultValue = "") String searchTerm,
            Pageable pageable) {
        return produitService.listerProduits(searchTerm, pageable);
    }

    //  Créer un produit avec catégorie liée
    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public Produit creerProduit(
            @RequestPart("produit") Produit produit,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {


        if (imageFile != null && !imageFile.isEmpty()) {
            String filename = StringUtils.cleanPath(imageFile.getOriginalFilename());
            Path uploadPath = Paths.get(this.uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = imageFile.getInputStream()) {
                Path filePath = uploadPath.resolve(filename);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                produit.setImageProduit(filename); // On sauvegarde uniquement le nom du fichier
            } catch (IOException e) {
                throw new IOException("Impossible de sauvegarder l'image : " + filename, e);
            }
        }

        if (produit.getCategorie() == null || produit.getCategorie().getId() == null) {
            throw new RuntimeException("Catégorie obligatoire pour créer un produit");
        }
        // Appel de la nouvelle méthode de service pour la création
        return produitService.createProduit(produit);
    }

    // Récupérer un produit par ID
    @GetMapping("/{id}")
    public Produit getProduit(@PathVariable Long id) {
        return produitService.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'id : " + id));
    }

    //  Supprimer un produit
    @DeleteMapping("/{id}")
    public void supprimerProduit(@PathVariable Long id) {
        produitService.supprimerProduit(id);
    }

    @GetMapping("/stats")
    public Map<String, Object> getProduitStats() {
        return produitService.getStats();
    }

    @GetMapping("/stats/category-orders")
    public List<CategoryOrderStatsDto> getCategoryOrderStats() {
        return produitService.getCategoryOrderStats();
    }
}
