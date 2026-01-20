package com.gestionstock.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Cette exception est levée lorsqu'une ressource demandée n'est pas trouvée.
 * L'annotation @ResponseStatus(HttpStatus.NOT_FOUND) indique à Spring de
 * renvoyer un code de statut HTTP 404 par défaut si cette exception n'est pas
 * gérée par un @ExceptionHandler.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
