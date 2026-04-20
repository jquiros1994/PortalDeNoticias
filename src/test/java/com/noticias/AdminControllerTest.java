package com.noticias;

import com.noticias.controller.AdminController;
import com.noticias.dto.UsuarioSession;
import com.noticias.model.Noticia;
import com.noticias.service.AdminService;
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

class AdminControllerTest {

    private AdminService adminService;
    private NoticiaService noticiaService;
    private AdminController controller;

    @BeforeEach
    void setUp() {
        adminService = mock(AdminService.class);
        noticiaService = mock(NoticiaService.class);
        controller = new AdminController(adminService, noticiaService);
    }


    @Test
    void anyEndpoint_unauthenticated_redirectsToLogin() {
        String result = controller.listarUsuarios(new MockHttpSession(), new ConcurrentModel());
        assertEquals("redirect:/login", result);
        verifyNoInteractions(adminService);
    }

    @Test
    void anyEndpoint_nonAdmin_redirectsHome() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuario", buildSession(1, "AUTOR"));

        String result = controller.listarUsuarios(session, new ConcurrentModel());
        assertEquals("redirect:/", result);
        verifyNoInteractions(adminService);
    }


    @Test
    void listarUsuarios_admin_returnsView() {
        MockHttpSession session = adminSession();
        when(adminService.listarUsuarios()).thenReturn(List.of());

        Model model = new ConcurrentModel();
        String result = controller.listarUsuarios(session, model);

        assertEquals("admin/usuarios", result);
        assertNotNull(model.getAttribute("usuarios"));
    }

    @Test
    void crearUsuario_blankNombre_returnsViewWithError() {
        MockHttpSession session = adminSession();
        when(adminService.listarUsuarios()).thenReturn(List.of());

        Model model = new ConcurrentModel();
        String result = controller.crearUsuario("  ", "Apellido", "e@e.com", "pass123",
                "LECTOR", session, model, new RedirectAttributesModelMap());

        assertEquals("admin/usuarios", result);
        assertEquals("El nombre no puede estar vacío.", model.getAttribute("error"));
        verify(adminService, never()).insertarUsuario(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyInt());
    }

    @Test
    void crearUsuario_passwordTooLong_returnsViewWithError() {
        MockHttpSession session = adminSession();
        when(adminService.listarUsuarios()).thenReturn(List.of());

        Model model = new ConcurrentModel();
        String result = controller.crearUsuario("Juan", "Perez", "e@e.com", "passwordtoolong123",
                "LECTOR", session, model, new RedirectAttributesModelMap());

        assertEquals("admin/usuarios", result);
        assertEquals("La contraseña no puede superar 16 caracteres.", model.getAttribute("error"));
    }

    @Test
    void crearUsuario_valid_redirectsToUsuarios() {
        MockHttpSession session = adminSession();
        doNothing().when(adminService).insertarUsuario(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyInt());

        RedirectAttributesModelMap ra = new RedirectAttributesModelMap();
        String result = controller.crearUsuario("Juan", "Perez", "juan@test.com", "pass123",
                "LECTOR", session, new ConcurrentModel(), ra);

        assertEquals("redirect:/admin/usuarios", result);
        verify(adminService).insertarUsuario("Juan", "Perez", "juan@test.com", "pass123", "LECTOR", 1);
    }

    @Test
    void crearUsuario_duplicateEmail_returnsViewWithError() {
        MockHttpSession session = adminSession();
        when(adminService.listarUsuarios()).thenReturn(List.of());
        doThrow(new RuntimeException("El email ya está en uso."))
                .when(adminService).insertarUsuario(anyString(), anyString(), anyString(),
                        anyString(), anyString(), anyInt());

        Model model = new ConcurrentModel();
        String result = controller.crearUsuario("Juan", "Perez", "dup@test.com", "pass123",
                "LECTOR", session, model, new RedirectAttributesModelMap());

        assertEquals("admin/usuarios", result);
        assertEquals("El email ya está en uso.", model.getAttribute("error"));
    }

    @Test
    void toggleActivo_admin_redirectsToUsuarios() {
        MockHttpSession session = adminSession();
        doNothing().when(adminService).toggleActivoUsuario(5, 0);

        String result = controller.toggleActivo(5, 0, session);

        assertEquals("redirect:/admin/usuarios", result);
        verify(adminService).toggleActivoUsuario(5, 0);
    }


    @Test
    void listarTemas_admin_returnsView() {
        MockHttpSession session = adminSession();
        when(adminService.listarTemas()).thenReturn(List.of());

        Model model = new ConcurrentModel();
        String result = controller.listarTemas(session, model);

        assertEquals("admin/temas", result);
    }

    @Test
    void crearTema_blankNombre_returnsViewWithError() {
        MockHttpSession session = adminSession();
        when(adminService.listarTemas()).thenReturn(List.of());

        Model model = new ConcurrentModel();
        String result = controller.crearTema("  ", "", session, model, new RedirectAttributesModelMap());

        assertEquals("admin/temas", result);
        assertEquals("El nombre del tema no puede estar vacío.", model.getAttribute("error"));
        verify(adminService, never()).insertarTema(anyString(), anyString());
    }

    @Test
    void crearTema_valid_redirectsToTemas() {
        MockHttpSession session = adminSession();
        doNothing().when(adminService).insertarTema(anyString(), anyString());

        String result = controller.crearTema("Tecnología", "Tema de tecnología",
                session, new ConcurrentModel(), new RedirectAttributesModelMap());

        assertEquals("redirect:/admin/temas", result);
        verify(adminService).insertarTema("Tecnología", "Tema de tecnología");
    }

    @Test
    void eliminarTema_admin_redirectsToTemas() {
        MockHttpSession session = adminSession();
        doNothing().when(adminService).eliminarTema(3);

        String result = controller.eliminarTema(3, session, new RedirectAttributesModelMap());

        assertEquals("redirect:/admin/temas", result);
        verify(adminService).eliminarTema(3);
    }


    @Test
    void crearSubtema_noTemaSelected_returnsViewWithError() {
        MockHttpSession session = adminSession();
        when(adminService.listarSubtemas()).thenReturn(List.of());
        when(adminService.listarTemas()).thenReturn(List.of());

        Model model = new ConcurrentModel();
        String result = controller.crearSubtema("Subtema", 0, session, model,
                new RedirectAttributesModelMap());

        assertEquals("admin/subtemas", result);
        assertEquals("Debes seleccionar un tema.", model.getAttribute("error"));
        verify(adminService, never()).insertarSubtema(anyString(), anyInt());
    }

    @Test
    void crearSubtema_valid_redirectsToSubtemas() {
        MockHttpSession session = adminSession();
        doNothing().when(adminService).insertarSubtema(anyString(), anyInt());

        String result = controller.crearSubtema("Subtema válido", 2, session,
                new ConcurrentModel(), new RedirectAttributesModelMap());

        assertEquals("redirect:/admin/subtemas", result);
        verify(adminService).insertarSubtema("Subtema válido", 2);
    }

    @Test
    void eliminarSubtema_admin_redirectsToSubtemas() {
        MockHttpSession session = adminSession();
        doNothing().when(adminService).eliminarSubtema(7);

        String result = controller.eliminarSubtema(7, session, new RedirectAttributesModelMap());

        assertEquals("redirect:/admin/subtemas", result);
        verify(adminService).eliminarSubtema(7);
    }


    @Test
    void listarTodasNoticias_admin_returnsView() {
        MockHttpSession session = adminSession();
        when(noticiaService.listarTodasNoticias()).thenReturn(List.of());

        Model model = new ConcurrentModel();
        String result = controller.listarTodasNoticias(session, model);

        assertEquals("admin/noticias", result);
        assertNotNull(model.getAttribute("noticias"));
    }

    @Test
    void mostrarEditarNoticia_admin_returnsForm() {
        MockHttpSession session = adminSession();
        Noticia n = new Noticia();
        n.setIdNoticia(3);
        n.setIdTema(1);
        when(noticiaService.obtenerNoticiaPorId(3)).thenReturn(n);
        when(noticiaService.listarTemas()).thenReturn(List.of());
        when(noticiaService.listarSubtemasPorTema(1)).thenReturn(List.of());

        String result = controller.mostrarEditarNoticia(3, session, new ConcurrentModel());

        assertEquals("admin/formulario-noticia", result);
    }

    @Test
    void editarNoticiaPost_blankTitulo_returnsFormWithError() {
        MockHttpSession session = adminSession();
        Noticia n = new Noticia(); n.setIdNoticia(3); n.setIdTema(1);
        when(noticiaService.obtenerNoticiaPorId(3)).thenReturn(n);
        when(noticiaService.listarTemas()).thenReturn(List.of());
        when(noticiaService.listarSubtemasPorTema(1)).thenReturn(List.of());

        Model model = new ConcurrentModel();
        String result = controller.editarNoticia(3, "  ", "Cuerpo", 1, 2,
                session, model, new RedirectAttributesModelMap());

        assertEquals("admin/formulario-noticia", result);
        assertEquals("El título no puede estar vacío.", model.getAttribute("error"));
        verify(noticiaService, never()).actualizarNoticia(anyInt(), anyString(), anyString(), anyInt(), anyInt());
    }

    @Test
    void editarNoticiaPost_valid_redirectsToNoticias() {
        MockHttpSession session = adminSession();
        doNothing().when(noticiaService).actualizarNoticia(anyInt(), anyString(), anyString(), anyInt(), anyInt());

        String result = controller.editarNoticia(3, "Título válido", "Cuerpo válido", 1, 2,
                session, new ConcurrentModel(), new RedirectAttributesModelMap());

        assertEquals("redirect:/admin/noticias", result);
        verify(noticiaService).actualizarNoticia(3, "Título válido", "Cuerpo válido", 1, 2);
    }

    @Test
    void eliminarNoticiaAdmin_redirectsToNoticias() {
        MockHttpSession session = adminSession();
        doNothing().when(noticiaService).eliminarNoticia(8);

        String result = controller.eliminarNoticia(8, session, new RedirectAttributesModelMap());

        assertEquals("redirect:/admin/noticias", result);
        verify(noticiaService).eliminarNoticia(8);
    }


    @Test
    void estadisticas_admin_returnsViewWithAllPanels() {
        MockHttpSession session = adminSession();
        when(adminService.comentariosPorNoticia()).thenReturn(List.of());
        when(adminService.promedioCalificacionPorNoticia()).thenReturn(List.of());
        when(adminService.articulosPorAutor()).thenReturn(List.of());
        when(adminService.topNoticiasCalificadas()).thenReturn(List.of());
        when(adminService.topNoticiasComentadas()).thenReturn(List.of());
        when(adminService.articulosPorEstado()).thenReturn(List.of());

        Model model = new ConcurrentModel();
        String result = controller.estadisticas(session, model);

        assertEquals("admin/estadisticas", result);
        assertNotNull(model.getAttribute("comentariosPorNoticia"));
        assertNotNull(model.getAttribute("promedioCalificacion"));
        assertNotNull(model.getAttribute("articulosPorAutor"));
        assertNotNull(model.getAttribute("topCalificadas"));
        assertNotNull(model.getAttribute("topComentadas"));
        assertNotNull(model.getAttribute("articulosPorEstado"));
    }


    private MockHttpSession adminSession() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuario", buildSession(1, "ADMIN"));
        return session;
    }

    private UsuarioSession buildSession(int id, String rol) {
        UsuarioSession u = new UsuarioSession();
        u.setIdUsuario(id);
        u.setNombre("Admin");
        u.setApellidos("User");
        u.setRol(rol);
        return u;
    }
}
