package com.noticias.controller;

import com.noticias.dto.UsuarioSession;
import com.noticias.model.Comentario;
import com.noticias.model.Noticia;
import com.noticias.model.Subtema;
import com.noticias.model.Tema;
import com.noticias.service.NoticiaService;
import com.noticias.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class NoticiaController {

    private static final int PAGE_SIZE = 10;

    private final NoticiaService noticiaService;

    public NoticiaController(NoticiaService noticiaService) {
        this.noticiaService = noticiaService;
    }

    @GetMapping("/")
    public String listarNoticias(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "0") int idTema,
            @RequestParam(defaultValue = "0") int idSubtema,
            Model model) {

        int offset = page * PAGE_SIZE;
        List<Noticia> noticias = noticiaService.listarNoticiasPublicadas(idTema, idSubtema, offset, PAGE_SIZE);
        int total = noticiaService.contarNoticiasPublicadas(idTema, idSubtema);
        int totalPaginas = (int) Math.ceil((double) total / PAGE_SIZE);

        List<Tema> temas = noticiaService.listarTemas();
        List<Subtema> subtemas = idTema > 0
                ? noticiaService.listarSubtemasPorTema(idTema)
                : List.of();

        model.addAttribute("noticias", noticias);
        model.addAttribute("paginaActual", page);
        model.addAttribute("totalPaginas", totalPaginas);
        model.addAttribute("temas", temas);
        model.addAttribute("subtemas", subtemas);
        model.addAttribute("idTemaSeleccionado", idTema);
        model.addAttribute("idSubtemaSeleccionado", idSubtema);

        return "index";
    }

    @GetMapping("/noticias/{id}")
    public String verNoticia(
            @PathVariable int id,
            HttpSession session,
            Model model) {

        Noticia noticia = noticiaService.obtenerNoticiaPorId(id);
        List<Comentario> comentarios = noticiaService.listarComentariosPorNoticia(id);
        int totalComentarios = noticiaService.contarComentariosPorNoticia(id);

        model.addAttribute("noticia", noticia);
        model.addAttribute("comentarios", comentarios);
        model.addAttribute("totalComentarios", totalComentarios);

        UsuarioSession usuario = SessionUtil.getUsuario(session);
        if (usuario != null) {
            int calificacion = noticiaService.obtenerCalificacionUsuario(id, usuario.getIdUsuario());
            model.addAttribute("yaCalificado", calificacion > 0);
            model.addAttribute("calificacionUsuario", calificacion);
        }

        return "noticias/detalle";
    }

    @PostMapping("/noticias/{id}/comentar")
    public String comentar(
            @PathVariable int id,
            @RequestParam String contenido,
            HttpSession session,
            HttpServletRequest request,
            Model model,
            RedirectAttributes redirectAttributes) {

        UsuarioSession usuario = SessionUtil.getUsuario(session);
        if (usuario == null) {
            return AuthController.redirigirALogin(request, session);
        }

        if (contenido == null || contenido.isBlank()) {
            model.addAttribute("error", "El comentario no puede estar vacío.");
            model.addAttribute("noticia", noticiaService.obtenerNoticiaPorId(id));
            model.addAttribute("comentarios", noticiaService.listarComentariosPorNoticia(id));
            int cal = noticiaService.obtenerCalificacionUsuario(id, usuario.getIdUsuario());
            model.addAttribute("yaCalificado", cal > 0);
            return "noticias/detalle";
        }

        noticiaService.insertarComentario(id, usuario.getIdUsuario(), contenido.trim());
        return "redirect:/noticias/" + id;
    }

    @PostMapping("/noticias/{id}/calificar")
    public String calificar(
            @PathVariable int id,
            @RequestParam(required = false) Integer valor,
            HttpSession session,
            HttpServletRequest request,
            Model model) {

        UsuarioSession usuario = SessionUtil.getUsuario(session);
        if (usuario == null) {
            return AuthController.redirigirALogin(request, session);
        }

        if (valor == null || valor < 1 || valor > 5) {
            model.addAttribute("error", "La calificación debe ser entre 1 y 5.");
            model.addAttribute("noticia", noticiaService.obtenerNoticiaPorId(id));
            model.addAttribute("comentarios", noticiaService.listarComentariosPorNoticia(id));
            model.addAttribute("yaCalificado", false);
            return "noticias/detalle";
        }

        try {
            noticiaService.insertarCalificacion(id, usuario.getIdUsuario(), valor);
            return "redirect:/noticias/" + id;
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("noticia", noticiaService.obtenerNoticiaPorId(id));
            model.addAttribute("comentarios", noticiaService.listarComentariosPorNoticia(id));
            model.addAttribute("yaCalificado", true);
            return "noticias/detalle";
        }
    }
}
