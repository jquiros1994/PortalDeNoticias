package com.noticias;

import com.noticias.controller.AutorController;
import com.noticias.dto.UsuarioSession;
import com.noticias.model.Noticia;
import com.noticias.service.NoticiaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AutorControllerTest {

    private NoticiaService noticiaService;
    private AutorController controller;

    @BeforeEach
    void setUp() {
        noticiaService = mock(NoticiaService.class);
        controller = new AutorController(noticiaService);
    }


    @Test
    void dashboard_unauthenticated_redirectsToLogin() {
        String result = controller.dashboard(new MockHttpSession(), new ConcurrentModel());
        assertEquals("redirect:/login", result);
        verifyNoInteractions(noticiaService);
    }

    @Test
    void dashboard_nonAutor_redirectsHome() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuario", buildSession(1, "LECTOR"));

        String result = controller.dashboard(session, new ConcurrentModel());
        assertEquals("redirect:/", result);
        verifyNoInteractions(noticiaService);
    }

    @Test
    void dashboard_autor_returnsView() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuario", buildSession(10, "AUTOR"));
        when(noticiaService.listarNoticiasPorAutor(10)).thenReturn(List.of());

        Model model = new ConcurrentModel();
        String result = controller.dashboard(session, model);

        assertEquals("autor/dashboard", result);
        assertNotNull(model.getAttribute("noticias"));
    }


    @Test
    void crearNoticia_blankTitulo_returnsFormWithError() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuario", buildSession(10, "AUTOR"));
        when(noticiaService.listarTemas()).thenReturn(List.of());

        Model model = new ConcurrentModel();
        String result = controller.crearNoticia("  ", "cuerpo", 0, 0,
                session, model, new RedirectAttributesModelMap());

        assertEquals("autor/formulario-noticia", result);
        assertEquals("El título no puede estar vacío.", model.getAttribute("error"));
        verify(noticiaService, never()).insertarNoticia(anyString(), anyString(), anyInt(), anyInt(), anyInt());
    }

    @Test
    void crearNoticia_titleTooLong_returnsFormWithError() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuario", buildSession(10, "AUTOR"));
        when(noticiaService.listarTemas()).thenReturn(List.of());

        String longTitle = "A".repeat(301);
        Model model = new ConcurrentModel();
        String result = controller.crearNoticia(longTitle, "cuerpo", 0, 0,
                session, model, new RedirectAttributesModelMap());

        assertEquals("autor/formulario-noticia", result);
        assertEquals("El título no puede superar 300 caracteres.", model.getAttribute("error"));
    }

    @Test
    void crearNoticia_valid_redirectsToDashboard() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuario", buildSession(10, "AUTOR"));
        doNothing().when(noticiaService).insertarNoticia(anyString(), anyString(), anyInt(), anyInt(), anyInt());

        RedirectAttributesModelMap ra = new RedirectAttributesModelMap();
        String result = controller.crearNoticia("Título válido", "Cuerpo del artículo", 1, 2,
                session, new ConcurrentModel(), ra);

        assertEquals("redirect:/autor/dashboard", result);
        verify(noticiaService).insertarNoticia("Título válido", "Cuerpo del artículo", 10, 1, 2);
    }


    @Test
    void editarNoticia_notOwner_redirectsToDashboard() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuario", buildSession(10, "AUTOR"));
        // verificarPropietarioNoticia returns 0 → not owner
        when(noticiaService.verificarPropietarioNoticia(5, 10)).thenReturn(0);

        String result = controller.mostrarFormularioEditar(5, session, new ConcurrentModel());

        assertTrue(result.startsWith("redirect:/autor/dashboard"));
        verify(noticiaService, never()).obtenerNoticiaPorId(anyInt());
    }

    @Test
    void editarNoticia_owner_returnsForm() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuario", buildSession(10, "AUTOR"));
        when(noticiaService.verificarPropietarioNoticia(5, 10)).thenReturn(1);

        Noticia n = new Noticia();
        n.setIdNoticia(5);
        n.setIdTema(2);
        when(noticiaService.obtenerNoticiaPorId(5)).thenReturn(n);
        when(noticiaService.listarTemas()).thenReturn(List.of());
        when(noticiaService.listarSubtemasPorTema(2)).thenReturn(List.of());

        String result = controller.mostrarFormularioEditar(5, session, new ConcurrentModel());

        assertEquals("autor/formulario-noticia", result);
    }

    @Test
    void editarNoticiaPost_notOwner_redirectsToDashboard() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuario", buildSession(10, "AUTOR"));
        when(noticiaService.verificarPropietarioNoticia(5, 10)).thenReturn(0);

        String result = controller.editarNoticia(5, "Título", "Cuerpo", 1, 2,
                session, new ConcurrentModel(), new RedirectAttributesModelMap());

        assertTrue(result.startsWith("redirect:/autor/dashboard"));
        verify(noticiaService, never()).actualizarNoticia(anyInt(), anyString(), anyString(), anyInt(), anyInt());
    }


    @Test
    void cambiarEstado_notOwner_redirectsWithError() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuario", buildSession(10, "AUTOR"));
        when(noticiaService.verificarPropietarioNoticia(5, 10)).thenReturn(0);

        RedirectAttributesModelMap ra = new RedirectAttributesModelMap();
        String result = controller.cambiarEstado(5, "PUBLICADO", session, ra);

        assertEquals("redirect:/autor/dashboard", result);
        verify(noticiaService, never()).cambiarEstadoNoticia(anyInt(), anyString());
    }

    @Test
    void cambiarEstado_owner_callsServiceAndRedirects() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuario", buildSession(10, "AUTOR"));
        when(noticiaService.verificarPropietarioNoticia(5, 10)).thenReturn(1);
        doNothing().when(noticiaService).cambiarEstadoNoticia(5, "PUBLICADO");

        String result = controller.cambiarEstado(5, "PUBLICADO", session,
                new RedirectAttributesModelMap());

        assertEquals("redirect:/autor/dashboard", result);
        verify(noticiaService).cambiarEstadoNoticia(5, "PUBLICADO");
    }


    @Test
    void eliminarNoticia_notOwner_redirectsWithError() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuario", buildSession(10, "AUTOR"));
        when(noticiaService.verificarPropietarioNoticia(5, 10)).thenReturn(0);

        RedirectAttributesModelMap ra = new RedirectAttributesModelMap();
        String result = controller.eliminarNoticia(5, session, ra);

        assertEquals("redirect:/autor/dashboard", result);
        verify(noticiaService, never()).eliminarNoticia(anyInt());
    }

    @Test
    void eliminarNoticia_owner_deletesAndRedirects() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuario", buildSession(10, "AUTOR"));
        when(noticiaService.verificarPropietarioNoticia(5, 10)).thenReturn(1);
        doNothing().when(noticiaService).eliminarNoticia(5);

        RedirectAttributesModelMap ra = new RedirectAttributesModelMap();
        String result = controller.eliminarNoticia(5, session, ra);

        assertEquals("redirect:/autor/dashboard", result);
        verify(noticiaService).eliminarNoticia(5);
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
