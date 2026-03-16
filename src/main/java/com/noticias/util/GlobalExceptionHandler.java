package com.noticias.util;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Catches all unhandled exceptions and renders error.html with the message.
 * Prevents raw stack traces from reaching the user.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {
        String mensaje = ex.getMessage();
        if (mensaje == null || mensaje.isBlank()) {
            mensaje = "Ha ocurrido un error inesperado. Por favor, intente de nuevo.";
        }
        model.addAttribute("mensaje", mensaje);
        return "error";
    }
}
