package com.gestionstock.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Le préfixe "file:" est crucial pour indiquer que les ressources proviennent du système de fichiers local.
        // Le chemin doit se terminer par un slash.
        String resourceLocation = "file:" + uploadPath + "/";

        // Cette ligne mappe les requêtes URL commençant par "/images/"
        // au dossier physique défini dans 'resourceLocation'.
        registry.addResourceHandler("/images/**")
                .addResourceLocations(resourceLocation);
    }
}