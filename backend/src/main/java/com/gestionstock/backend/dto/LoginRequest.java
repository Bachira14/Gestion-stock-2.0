package com.gestionstock.backend.dto;

// Utilisation de getters/setters pour la clarté. Lombok est une excellente alternative.
public class LoginRequest {
    private String email;
    private String mot_de_passe;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMot_de_passe() {
        return mot_de_passe;
    }

    public void setMot_de_passe(String mot_de_passe) {
        this.mot_de_passe = mot_de_passe;
    }
}