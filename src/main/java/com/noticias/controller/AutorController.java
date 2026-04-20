package com.noticias.controller;

import com.noticias.dto.UsuarioSession;
import com.noticias.model.Noticia;
import com.noticias.model.Subtema;
import com.noticias.model.Tema;
import com.noticias.service.NoticiaService;
import com.noticias.util.SessionUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/autor")
public class AutorController {

    private final NoticiaService noticiaService;

    public AutorController(NoticiaService noticiaService) {
        this.noticiaService = noticiaService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        UsuarioSession usuario = SessionUtil.getUsuario(session);
        if (usuario == null) return "redirect:/login";
        if (!"AUTOR".equals(usuario.getRol())) return "redirect:/";

        List<Noticia> noticias = noticiaService.listarNoticiasPorAutor(usuario.getIdUsuario());
        int totalNoticias = noticiaService.contarNoticiasPorAutor(usuario.getIdUsuario());
        model.addAttribute("noticias", noticias);
        model.addAttribute("totalNoticias", totalNoticias);
        return "autor/dashboard";
    }

    @GetMapping("/subtemas")
    @ResponseBody
    public List<Subtema> getSubtemasPorTema(
            @RequestParam int idTema,
            HttpSession session) {
        UsuarioSession usuario = SessionUtil.getUsuario(session);
        if (usuario == null || !"AUTOR".equals(usuario.getRol())) return List.of();
        return noticiaService.listarSubtemasPorTema(idTema);
    }

    @GetMapping("/noticias/nueva")
    public String mostrarFormularioNueva(HttpSession session, Model model) {
        UsuarioSession usuario = SessionUtil.getUsuario(session);
        if (usuario == null) return "redirect:/login";
        if (!"AUTOR".equals(usuario.getRol())) return "redirect:/";

        model.addAttribute("temas", noticiaService.listarTemas());
        model.addAttribute("subtemas", List.of());
        return "autor/formulario-noticia";
    }

    @PostMapping("/noticias/nueva")
    public String crearNoticia(
            @RequestParam String titulo,
            @RequestParam String cuerpo,
            @RequestParam(defaultValue = "0") int idTema,
            @RequestParam(defaultValue = "0") int idSubtema,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        UsuarioSession usuario = SessionUtil.getUsuario(session);
        if (usuario == null) return "redirect:/login";
        if (!"AUTOR".equals(usuario.getRol())) return "redirect:/";

        if (titulo == null || titulo.isBlank()) {
            model.addAttribute("error", "El título no puede estar vacío.");
            model.addAttribute("temas", noticiaService.listarTemas());
            model.addAttribute("subtemas", idTema > 0 ? noticiaService.listarSubtemasPorTema(idTema) : List.of());
            return "autor/formulario-noticia";
        }
        if (titulo.length() > 300) {
            model.addAttribute("error", "El título no puede superar 300 caracteres.");
            model.addAttribute("temas", noticiaService.listarTemas());
            model.addAttribute("subtemas", idTema > 0 ? noticiaService.listarSubtemasPorTema(idTema) : List.of());
            return "autor/formulario-noticia";
        }
        if (cuerpo == null || cuerpo.isBlank()) {
            model.addAttribute("error", "El cuerpo no puede estar vacío.");
            model.addAttribute("temas", noticiaService.listarTemas());
            model.addAttribute("subtemas", idTema > 0 ? noticiaService.listarSubtemasPorTema(idTema) : List.of());
            return "autor/formulario-noticia";
        }
        if (idTema <= 0) {
            model.addAttribute("error", "Debes seleccionar un tema.");
            model.addAttribute("temas", noticiaService.listarTemas());
            model.addAttribute("subtemas", List.of());
            return "autor/formulario-noticia";
        }
        if (idSubtema <= 0) {
            model.addAttribute("error", "Debes seleccionar un subtema.");
            model.addAttribute("temas", noticiaService.listarTemas());
            model.addAttribute("subtemas", noticiaService.listarSubtemasPorTema(idTema));
            return "autor/formulario-noticia";
        }

        noticiaService.insertarNoticia(titulo.trim(), cuerpo.trim(),
                usuario.getIdUsuario(), idTema, idSubtema);
        redirectAttributes.addFlashAttribute("mensaje", "Artículo creado exitosamente.");
        return "redirect:/autor/dashboard";
    }

    @GetMapping("/noticias/{id}/editar")
    public String mostrarFormularioEditar(
            @PathVariable int id,
            HttpSession session,
            Model model) {

        UsuarioSession usuario = SessionUtil.getUsuario(session);
        if (usuario == null) return "redirect:/login";
        if (!"AUTOR".equals(usuario.getRol())) return "redirect:/";

        if (noticiaService.verificarPropietarioNoticia(id, usuario.getIdUsuario()) == 0) {
            return "redirect:/autor/dashboard?error=No+tienes+permiso+para+editar+este+artículo.";
        }

        Noticia noticia = noticiaService.obtenerNoticiaPorId(id);
        List<Tema> temas = noticiaService.listarTemas();
        List<Subtema> subtemas = noticiaService.listarSubtemasPorTema(noticia.getIdTema());

        model.addAttribute("noticia", noticia);
        model.addAttribute("temas", temas);
        model.addAttribute("subtemas", subtemas);
        return "autor/formulario-noticia";
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

        UsuarioSession usuario = SessionUtil.getUsuario(session);
        if (usuario == null) return "redirect:/login";
        if (!"AUTOR".equals(usuario.getRol())) return "redirect:/";

        if (noticiaService.verificarPropietarioNoticia(id, usuario.getIdUsuario()) == 0) {
            return "redirect:/autor/dashboard?error=No+tienes+permiso+para+editar+este+artículo.";
        }

        if (titulo == null || titulo.isBlank()) {
            model.addAttribute("error", "El título no puede estar vacío.");
            Noticia n = noticiaService.obtenerNoticiaPorId(id);
            model.addAttribute("noticia", n);
            model.addAttribute("temas", noticiaService.listarTemas());
            model.addAttribute("subtemas", noticiaService.listarSubtemasPorTema(n.getIdTema()));
            return "autor/formulario-noticia";
        }
        if (cuerpo == null || cuerpo.isBlank()) {
            model.addAttribute("error", "El cuerpo no puede estar vacío.");
            Noticia n = noticiaService.obtenerNoticiaPorId(id);
            model.addAttribute("noticia", n);
            model.addAttribute("temas", noticiaService.listarTemas());
            model.addAttribute("subtemas", noticiaService.listarSubtemasPorTema(n.getIdTema()));
            return "autor/formulario-noticia";
        }

        noticiaService.actualizarNoticia(id, titulo.trim(), cuerpo.trim(), idTema, idSubtema);
        redirectAttributes.addFlashAttribute("mensaje", "Artículo actualizado.");
        return "redirect:/autor/dashboard";
    }

    @PostMapping("/noticias/{id}/estado")
    public String cambiarEstado(
            @PathVariable int id,
            @RequestParam String estado,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        UsuarioSession usuario = SessionUtil.getUsuario(session);
        if (usuario == null) return "redirect:/login";
        if (!"AUTOR".equals(usuario.getRol())) return "redirect:/";

        if (noticiaService.verificarPropietarioNoticia(id, usuario.getIdUsuario()) == 0) {
            redirectAttributes.addFlashAttribute("error",
                    "No tienes permiso para cambiar el estado de este artículo.");
            return "redirect:/autor/dashboard";
        }

        noticiaService.cambiarEstadoNoticia(id, estado);
        return "redirect:/autor/dashboard";
    }

    @PostMapping("/noticias/{id}/eliminar")
    public String eliminarNoticia(
            @PathVariable int id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        UsuarioSession usuario = SessionUtil.getUsuario(session);
        if (usuario == null) return "redirect:/login";
        if (!"AUTOR".equals(usuario.getRol())) return "redirect:/";

        if (noticiaService.verificarPropietarioNoticia(id, usuario.getIdUsuario()) == 0) {
            redirectAttributes.addFlashAttribute("error",
                    "No tienes permiso para eliminar este artículo.");
            return "redirect:/autor/dashboard";
        }

        noticiaService.eliminarNoticia(id);
        redirectAttributes.addFlashAttribute("mensaje", "Artículo eliminado.");
        return "redirect:/autor/dashboard";
    }
}
