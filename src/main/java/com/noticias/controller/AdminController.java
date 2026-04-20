package com.noticias.controller;

import com.noticias.dto.UsuarioSession;
import com.noticias.model.Noticia;
import com.noticias.service.AdminService;
import com.noticias.service.NoticiaService;
import com.noticias.util.SessionUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final NoticiaService noticiaService;

    public AdminController(AdminService adminService, NoticiaService noticiaService) {
        this.adminService = adminService;
        this.noticiaService = noticiaService;
    }

    private String checkAdmin(HttpSession session) {
        UsuarioSession u = SessionUtil.getUsuario(session);
        if (u == null) return "redirect:/login";
        if (!"ADMIN".equals(u.getRol())) return "redirect:/";
        return null;
    }

    @GetMapping("/estadisticas")
    public String estadisticas(HttpSession session, Model model) {
        String redirect = checkAdmin(session);
        if (redirect != null) return redirect;

        model.addAttribute("totalUsuarios",          adminService.contarUsuarios());
        model.addAttribute("totalPublicadas",        adminService.contarNoticiasPublicadas());
        model.addAttribute("promedioPortal",         adminService.promedioGeneralPortal());
        model.addAttribute("comentariosPorNoticia",  adminService.comentariosPorNoticia());
        model.addAttribute("promedioCalificacion",   adminService.promedioCalificacionPorNoticia());
        model.addAttribute("articulosPorAutor",      adminService.articulosPorAutor());
        model.addAttribute("topCalificadas",         adminService.topNoticiasCalificadas());
        model.addAttribute("topComentadas",          adminService.topNoticiasComentadas());
        model.addAttribute("articulosPorEstado",     adminService.articulosPorEstado());
        return "admin/estadisticas";
    }

    @GetMapping("/usuarios")
    public String listarUsuarios(HttpSession session, Model model) {
        String redirect = checkAdmin(session);
        if (redirect != null) return redirect;

        model.addAttribute("usuarios", adminService.listarUsuarios());
        return "admin/usuarios";
    }

    @PostMapping("/usuarios/nuevo")
    public String crearUsuario(
            @RequestParam String nombre,
            @RequestParam String apellidos,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String rol,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        String redirect = checkAdmin(session);
        if (redirect != null) return redirect;

        if (nombre == null || nombre.isBlank()) {
            model.addAttribute("error", "El nombre no puede estar vacío.");
            model.addAttribute("usuarios", adminService.listarUsuarios());
            return "admin/usuarios";
        }
        if (apellidos == null || apellidos.isBlank()) {
            model.addAttribute("error", "Los apellidos no pueden estar vacíos.");
            model.addAttribute("usuarios", adminService.listarUsuarios());
            return "admin/usuarios";
        }
        if (email == null || email.isBlank()) {
            model.addAttribute("error", "El email no puede estar vacío.");
            model.addAttribute("usuarios", adminService.listarUsuarios());
            return "admin/usuarios";
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            model.addAttribute("error", "El email no tiene un formato válido.");
            model.addAttribute("usuarios", adminService.listarUsuarios());
            return "admin/usuarios";
        }
        if (password == null || password.length() > 16) {
            model.addAttribute("error", "La contraseña no puede superar 16 caracteres.");
            model.addAttribute("usuarios", adminService.listarUsuarios());
            return "admin/usuarios";
        }

        try {
            adminService.insertarUsuario(nombre.trim(), apellidos.trim(),
                    email.trim(), password, rol, 1);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario creado correctamente.");
            return "redirect:/admin/usuarios";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("usuarios", adminService.listarUsuarios());
            return "admin/usuarios";
        }
    }

    @PostMapping("/usuarios/{id}/toggleActivo")
    public String toggleActivo(
            @PathVariable int id,
            @RequestParam int activo,
            HttpSession session) {

        String redirect = checkAdmin(session);
        if (redirect != null) return redirect;

        adminService.toggleActivoUsuario(id, activo);
        return "redirect:/admin/usuarios";
    }


    @GetMapping("/temas")
    public String listarTemas(HttpSession session, Model model) {
        String redirect = checkAdmin(session);
        if (redirect != null) return redirect;

        model.addAttribute("temas", adminService.listarTemas());
        return "admin/temas";
    }

    @PostMapping("/temas/nuevo")
    public String crearTema(
            @RequestParam String nombre,
            @RequestParam(defaultValue = "") String descripcion,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        String redirect = checkAdmin(session);
        if (redirect != null) return redirect;

        if (nombre == null || nombre.isBlank()) {
            model.addAttribute("error", "El nombre del tema no puede estar vacío.");
            model.addAttribute("temas", adminService.listarTemas());
            return "admin/temas";
        }

        adminService.insertarTema(nombre.trim(), descripcion.trim());
        redirectAttributes.addFlashAttribute("mensaje", "Tema creado.");
        return "redirect:/admin/temas";
    }

    @PostMapping("/temas/{id}/editar")
    public String editarTema(
            @PathVariable int id,
            @RequestParam String nombre,
            @RequestParam(defaultValue = "") String descripcion,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        String redirect = checkAdmin(session);
        if (redirect != null) return redirect;

        if (nombre == null || nombre.isBlank()) {
            model.addAttribute("error", "El nombre del tema no puede estar vacío.");
            model.addAttribute("temas", adminService.listarTemas());
            return "admin/temas";
        }

        adminService.actualizarTema(id, nombre.trim(), descripcion.trim());
        redirectAttributes.addFlashAttribute("mensaje", "Tema actualizado.");
        return "redirect:/admin/temas";
    }

    @PostMapping("/temas/{id}/eliminar")
    public String eliminarTema(
            @PathVariable int id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String redirect = checkAdmin(session);
        if (redirect != null) return redirect;

        adminService.eliminarTema(id);
        redirectAttributes.addFlashAttribute("mensaje", "Tema eliminado.");
        return "redirect:/admin/temas";
    }

    @GetMapping("/subtemas")
    public String listarSubtemas(HttpSession session, Model model) {
        String redirect = checkAdmin(session);
        if (redirect != null) return redirect;

        model.addAttribute("subtemas", adminService.listarSubtemas());
        model.addAttribute("temas", adminService.listarTemas());
        return "admin/subtemas";
    }

    @PostMapping("/subtemas/nuevo")
    public String crearSubtema(
            @RequestParam String nombre,
            @RequestParam(defaultValue = "0") int idTema,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        String redirect = checkAdmin(session);
        if (redirect != null) return redirect;

        if (nombre == null || nombre.isBlank()) {
            model.addAttribute("error", "El nombre del subtema no puede estar vacío.");
            model.addAttribute("subtemas", adminService.listarSubtemas());
            model.addAttribute("temas", adminService.listarTemas());
            return "admin/subtemas";
        }
        if (idTema == 0) {
            model.addAttribute("error", "Debes seleccionar un tema.");
            model.addAttribute("subtemas", adminService.listarSubtemas());
            model.addAttribute("temas", adminService.listarTemas());
            return "admin/subtemas";
        }

        adminService.insertarSubtema(nombre.trim(), idTema);
        redirectAttributes.addFlashAttribute("mensaje", "Subtema creado.");
        return "redirect:/admin/subtemas";
    }

    @PostMapping("/subtemas/{id}/editar")
    public String editarSubtema(
            @PathVariable int id,
            @RequestParam String nombre,
            @RequestParam(defaultValue = "0") int idTema,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        String redirect = checkAdmin(session);
        if (redirect != null) return redirect;

        if (nombre == null || nombre.isBlank() || idTema == 0) {
            model.addAttribute("error", "Nombre y tema son obligatorios.");
            model.addAttribute("subtemas", adminService.listarSubtemas());
            model.addAttribute("temas", adminService.listarTemas());
            return "admin/subtemas";
        }

        adminService.actualizarSubtema(id, nombre.trim(), idTema);
        redirectAttributes.addFlashAttribute("mensaje", "Subtema actualizado.");
        return "redirect:/admin/subtemas";
    }

    @PostMapping("/subtemas/{id}/eliminar")
    public String eliminarSubtema(
            @PathVariable int id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String redirect = checkAdmin(session);
        if (redirect != null) return redirect;

        adminService.eliminarSubtema(id);
        redirectAttributes.addFlashAttribute("mensaje", "Subtema eliminado.");
        return "redirect:/admin/subtemas";
    }

    @GetMapping("/noticias")
    public String listarTodasNoticias(HttpSession session, Model model) {
        String redirect = checkAdmin(session);
        if (redirect != null) return redirect;

        model.addAttribute("noticias", noticiaService.listarTodasNoticias());
        return "admin/noticias";
    }

    @GetMapping("/noticias/{id}/editar")
    public String mostrarEditarNoticia(
            @PathVariable int id,
            HttpSession session,
            Model model) {

        String redirect = checkAdmin(session);
        if (redirect != null) return redirect;

        model.addAttribute("noticia", noticiaService.obtenerNoticiaPorId(id));
        model.addAttribute("temas", noticiaService.listarTemas());
        model.addAttribute("subtemas", noticiaService.listarSubtemasPorTema(
                noticiaService.obtenerNoticiaPorId(id).getIdTema()));
        return "admin/formulario-noticia";
    }

    @PostMapping("/noticias/{id}/editar")
    public String editarNoticia(
            @PathVariable int id,
            @RequestParam String titulo,
            @RequestParam String cuerpo,
            @RequestParam(defaultValue = "0") int idTema,
            @RequestParam(defaultValue = "0") int idSubtema,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        String redirect = checkAdmin(session);
        if (redirect != null) return redirect;

        if (titulo == null || titulo.isBlank()) {
            model.addAttribute("error", "El título no puede estar vacío.");
            Noticia n = noticiaService.obtenerNoticiaPorId(id);
            model.addAttribute("noticia", n);
            model.addAttribute("temas", noticiaService.listarTemas());
            model.addAttribute("subtemas", noticiaService.listarSubtemasPorTema(n.getIdTema()));
            return "admin/formulario-noticia";
        }
        if (cuerpo == null || cuerpo.isBlank()) {
            model.addAttribute("error", "El cuerpo no puede estar vacío.");
            Noticia n = noticiaService.obtenerNoticiaPorId(id);
            model.addAttribute("noticia", n);
            model.addAttribute("temas", noticiaService.listarTemas());
            model.addAttribute("subtemas", noticiaService.listarSubtemasPorTema(n.getIdTema()));
            return "admin/formulario-noticia";
        }

        noticiaService.actualizarNoticia(id, titulo.trim(), cuerpo.trim(), idTema, idSubtema);
        redirectAttributes.addFlashAttribute("mensaje", "Artículo actualizado.");
        return "redirect:/admin/noticias";
    }

    @PostMapping("/noticias/{id}/eliminar")
    public String eliminarNoticia(
            @PathVariable int id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String redirect = checkAdmin(session);
        if (redirect != null) return redirect;

        noticiaService.eliminarNoticia(id);
        redirectAttributes.addFlashAttribute("mensaje", "Artículo eliminado.");
        return "redirect:/admin/noticias";
    }
}
