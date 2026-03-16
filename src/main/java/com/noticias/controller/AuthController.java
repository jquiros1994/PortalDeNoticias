package com.noticias.controller;

import com.noticias.dto.UsuarioSession;
import com.noticias.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/registro")
    public String mostrarRegistro() {
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(
            @RequestParam String nombre,
            @RequestParam String apellidos,
            @RequestParam String email,
            @RequestParam String password,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (nombre == null || nombre.isBlank()) {
            model.addAttribute("error", "El nombre no puede estar vacío.");
            return "auth/registro";
        }
        if (apellidos == null || apellidos.isBlank()) {
            model.addAttribute("error", "Los apellidos no pueden estar vacíos.");
            return "auth/registro";
        }
        if (email == null || email.isBlank()) {
            model.addAttribute("error", "El email no puede estar vacío.");
            return "auth/registro";
        }
        if (password == null || password.isBlank()) {
            model.addAttribute("error", "La contraseña no puede estar vacía.");
            return "auth/registro";
        }
        if (password.length() > 16) {
            model.addAttribute("error", "La contraseña no puede superar 16 caracteres.");
            return "auth/registro";
        }

        try {
            usuarioService.insertarUsuario(nombre.trim(), apellidos.trim(),
                    email.trim(), password, "LECTOR", 1);
            redirectAttributes.addFlashAttribute("mensaje",
                    "Cuenta creada exitosamente. Ya puedes iniciar sesión.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", "No se pudo registrar: " + e.getMessage());
            return "auth/registro";
        }
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String procesarLogin(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            model.addAttribute("error", "Credenciales incorrectas.");
            return "auth/login";
        }

        try {
            UsuarioSession usuario = usuarioService.autenticarUsuario(email.trim(), password);

            if (usuario == null) {
                model.addAttribute("error", "Credenciales incorrectas.");
                return "auth/login";
            }

            session.setAttribute("usuario", usuario);

            String returnUrl = (String) session.getAttribute("returnUrl");
            session.removeAttribute("returnUrl");
            if (returnUrl != null && !returnUrl.isBlank()) {
                return "redirect:" + returnUrl;
            }

            return switch (usuario.getRol()) {
                case "AUTOR"  -> "redirect:/autor/dashboard";
                case "ADMIN"  -> "redirect:/admin/estadisticas";
                default       -> "redirect:/";
            };

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    public static String redirigirALogin(HttpServletRequest request, HttpSession session) {
        session.setAttribute("returnUrl", request.getRequestURI());
        return "redirect:/login";
    }
}
