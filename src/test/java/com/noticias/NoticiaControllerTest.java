package com.noticias;

import com.noticias.controller.NoticiaController;
import com.noticias.dto.UsuarioSession;
import com.noticias.model.Comentario;
import com.noticias.model.Noticia;
import com.noticias.model.Tema;
import com.noticias.service.NoticiaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class NoticiaControllerTest {

    private NoticiaService noticiaService;
    private NoticiaController controller;

    @BeforeEach
    void setUp() {
        noticiaService = mock(NoticiaService.class);
        controller = new NoticiaController(noticiaService);
    }

    // ---- Story 3.1: Public listing ----

    @Test
    void listarNoticias_returnsIndexView() {
        when(noticiaService.listarNoticiasPublicadas(0, 0, 0, 10)).thenReturn(List.of());
        when(noticiaService.contarNoticiasPublicadas(0, 0)).thenReturn(0);
        when(noticiaService.listarTemas()).thenReturn(List.of());

        String view = controller.listarNoticias(0, 0, 0, new ConcurrentModel());
        assertEquals("index", view);
    }

    @Test
    void listarNoticias_calculatesOffsetCorrectly() {
        when(noticiaService.listarNoticiasPublicadas(0, 0, 20, 10)).thenReturn(List.of());
        when(noticiaService.contarNoticiasPublicadas(0, 0)).thenReturn(35);
        when(noticiaService.listarTemas()).thenReturn(List.of());

        Model model = new ConcurrentModel();
        controller.listarNoticias(2, 0, 0, model);

        // page=2 → offset=20
        verify(noticiaService).listarNoticiasPublicadas(0, 0, 20, 10);
        assertEquals(4, model.getAttribute("totalPaginas")); // ceil(35/10)=4
    }

    @Test
    void listarNoticias_withTemaFilter_loadsSubtemas() {
        when(noticiaService.listarNoticiasPublicadas(1, 0, 0, 10)).thenReturn(List.of());
        when(noticiaService.contarNoticiasPublicadas(1, 0)).thenReturn(0);
        when(noticiaService.listarTemas()).thenReturn(List.of());
        when(noticiaService.listarSubtemasPorTema(1)).thenReturn(List.of());

        controller.listarNoticias(0, 1, 0, new ConcurrentModel());

        verify(noticiaService).listarSubtemasPorTema(1);
    }

    // ---- Story 3.3: Article detail ----

    @Test
    void verNoticia_returnsDetalleView() {
        Noticia n = new Noticia();
        n.setIdNoticia(5);
        when(noticiaService.obtenerNoticiaPorId(5)).thenReturn(n);
        when(noticiaService.listarComentariosPorNoticia(5)).thenReturn(List.of());

        String view = controller.verNoticia(5, new MockHttpSession(), new ConcurrentModel());
        assertEquals("noticias/detalle", view);
    }

    @Test
    void verNoticia_loggedInUser_checksRating() {
        Noticia n = new Noticia();
        n.setIdNoticia(5);
        when(noticiaService.obtenerNoticiaPorId(5)).thenReturn(n);
        when(noticiaService.listarComentariosPorNoticia(5)).thenReturn(List.of());
        when(noticiaService.obtenerCalificacionUsuario(5, 42)).thenReturn(3);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuario", buildSession(42, "LECTOR"));
        Model model = new ConcurrentModel();

        controller.verNoticia(5, session, model);

        assertTrue((Boolean) model.getAttribute("yaCalificado"));
        assertEquals(3, model.getAttribute("calificacionUsuario"));
    }

    // ---- Story 4.1: Comment ----

    @Test
    void comentar_unauthenticated_redirectsToLogin() {
        MockHttpSession session = new MockHttpSession();
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/noticias/5/comentar");

        String result = controller.comentar(5, "texto", session, request,
                new ConcurrentModel(), new RedirectAttributesModelMap());

        assertEquals("redirect:/login", result);
        verifyNoInteractions(noticiaService);
    }

    @Test
    void comentar_blankContent_returnsDetailView() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuario", buildSession(1, "LECTOR"));
        Noticia n = new Noticia();
        n.setIdNoticia(5);
        when(noticiaService.obtenerNoticiaPorId(5)).thenReturn(n);
        when(noticiaService.listarComentariosPorNoticia(5)).thenReturn(List.of());
        when(noticiaService.obtenerCalificacionUsuario(5, 1)).thenReturn(0);

        Model model = new ConcurrentModel();
        String result = controller.comentar(5, "   ", session, new MockHttpServletRequest(),
                model, new RedirectAttributesModelMap());

        assertEquals("noticias/detalle", result);
        assertEquals("El comentario no puede estar vacío.", model.getAttribute("error"));
        verify(noticiaService, never()).insertarComentario(anyInt(), anyInt(), anyString());
    }

    @Test
    void comentar_valid_redirectsToDetail() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuario", buildSession(1, "LECTOR"));
        doNothing().when(noticiaService).insertarComentario(5, 1, "Gran artículo");

        String result = controller.comentar(5, "Gran artículo", session,
                new MockHttpServletRequest(), new ConcurrentModel(), new RedirectAttributesModelMap());

        assertEquals("redirect:/noticias/5", result);
        verify(noticiaService).insertarComentario(5, 1, "Gran artículo");
    }

    // ---- Story 4.2: Rating ----

    @Test
    void calificar_unauthenticated_redirectsToLogin() {
        MockHttpSession session = new MockHttpSession();
        String result = controller.calificar(5, 3, session,
                new MockHttpServletRequest(), new ConcurrentModel());
        assertEquals("redirect:/login", result);
    }

    @Test
    void calificar_invalidValue_returnsDetailView() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuario", buildSession(1, "LECTOR"));
        Noticia n = new Noticia(); n.setIdNoticia(5);
        when(noticiaService.obtenerNoticiaPorId(5)).thenReturn(n);
        when(noticiaService.listarComentariosPorNoticia(5)).thenReturn(List.of());

        Model model = new ConcurrentModel();
        String result = controller.calificar(5, 6, session, new MockHttpServletRequest(), model);

        assertEquals("noticias/detalle", result);
        assertEquals("La calificación debe ser entre 1 y 5.", model.getAttribute("error"));
    }

    @Test
    void calificar_valid_redirectsToDetail() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuario", buildSession(1, "LECTOR"));
        doNothing().when(noticiaService).insertarCalificacion(5, 1, 4);

        String result = controller.calificar(5, 4, session, new MockHttpServletRequest(), new ConcurrentModel());

        assertEquals("redirect:/noticias/5", result);
    }

    @Test
    void calificar_duplicate_returnsDetailWithError() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuario", buildSession(1, "LECTOR"));
        doThrow(new RuntimeException("Ya has calificado este artículo."))
                .when(noticiaService).insertarCalificacion(5, 1, 4);
        Noticia n = new Noticia(); n.setIdNoticia(5);
        when(noticiaService.obtenerNoticiaPorId(5)).thenReturn(n);
        when(noticiaService.listarComentariosPorNoticia(5)).thenReturn(List.of());

        Model model = new ConcurrentModel();
        String result = controller.calificar(5, 4, session, new MockHttpServletRequest(), model);

        assertEquals("noticias/detalle", result);
        assertEquals("Ya has calificado este artículo.", model.getAttribute("error"));
    }

    private UsuarioSession buildSession(int id, String rol) {
        UsuarioSession u = new UsuarioSession();
        u.setIdUsuario(id);
        u.setNombre("Test");
        u.setApellidos("User");
        u.setRol(rol);
        return u;
    }
}
