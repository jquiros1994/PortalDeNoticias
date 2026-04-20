package com.noticias;

import com.noticias.controller.AuthController;
import com.noticias.dto.UsuarioSession;
import com.noticias.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private UsuarioService usuarioService;
    private AuthController controller;

    @BeforeEach
    void setUp() {
        usuarioService = mock(UsuarioService.class);
        controller = new AuthController(usuarioService);
    }


    @Test
    void mostrarRegistroReturnsView() {
        assertEquals("auth/registro", controller.mostrarRegistro());
    }

    @Test
    void procesarRegistro_blankNombre_returnsFormWithError() {
        Model model = new ConcurrentModel();
        String result = controller.procesarRegistro(
                "", "Pérez", "test@mail.com", "pass123", model, new RedirectAttributesModelMap());
        assertEquals("auth/registro", result);
        assertEquals("El nombre no puede estar vacío.", model.getAttribute("error"));
        verifyNoInteractions(usuarioService);
    }

    @Test
    void procesarRegistro_passwordTooLong_returnsFormWithError() {
        Model model = new ConcurrentModel();
        String result = controller.procesarRegistro(
                "Juan", "Pérez", "test@mail.com", "contrasenamuylarga123", model,
                new RedirectAttributesModelMap());
        assertEquals("auth/registro", result);
        assertEquals("La contraseña no puede superar 16 caracteres.", model.getAttribute("error"));
        verifyNoInteractions(usuarioService);
    }

    @Test
    void procesarRegistro_validData_redirectsToLogin() {
        doNothing().when(usuarioService).insertarUsuario(any(), any(), any(), any(), any(), eq(1));
        Model model = new ConcurrentModel();
        var redirectAttrs = new RedirectAttributesModelMap();

        String result = controller.procesarRegistro(
                "Juan", "Pérez", "juan@test.com", "pass123", model, redirectAttrs);

        assertEquals("redirect:/login", result);
        verify(usuarioService).insertarUsuario("Juan", "Pérez", "juan@test.com", "pass123", "LECTOR", 1);
    }

    @Test
    void procesarRegistro_duplicateEmail_returnsFormWithError() {
        doThrow(new RuntimeException("El email ya está registrado en el sistema."))
                .when(usuarioService).insertarUsuario(any(), any(), any(), any(), any(), anyInt());
        Model model = new ConcurrentModel();

        String result = controller.procesarRegistro(
                "Juan", "Pérez", "dup@test.com", "pass123", model, new RedirectAttributesModelMap());

        assertEquals("auth/registro", result);
        assertTrue(model.getAttribute("error").toString().contains("No se pudo registrar"));
    }


    @Test
    void mostrarLoginReturnsView() {
        assertEquals("auth/login", controller.mostrarLogin());
    }

    @Test
    void procesarLogin_badCredentials_returnsLoginWithError() {
        when(usuarioService.autenticarUsuario("wrong@test.com", "bad")).thenReturn(null);
        MockHttpSession session = new MockHttpSession();
        Model model = new ConcurrentModel();

        String result = controller.procesarLogin("wrong@test.com", "bad", session, model);

        assertEquals("auth/login", result);
        assertEquals("Credenciales incorrectas.", model.getAttribute("error"));
    }

    @Test
    void procesarLogin_inactiveAccount_returnsLoginWithError() {
        when(usuarioService.autenticarUsuario("inactive@test.com", "pass"))
                .thenThrow(new RuntimeException("Cuenta desactivada."));
        MockHttpSession session = new MockHttpSession();
        Model model = new ConcurrentModel();

        String result = controller.procesarLogin("inactive@test.com", "pass", session, model);

        assertEquals("auth/login", result);
        assertEquals("Cuenta desactivada.", model.getAttribute("error"));
    }

    @Test
    void procesarLogin_lector_redirectsToHome() {
        UsuarioSession u = buildSession("LECTOR");
        when(usuarioService.autenticarUsuario("lector@test.com", "pass")).thenReturn(u);
        MockHttpSession session = new MockHttpSession();

        String result = controller.procesarLogin("lector@test.com", "pass", session, new ConcurrentModel());

        assertEquals("redirect:/", result);
        assertSame(u, session.getAttribute("usuario"));
    }

    @Test
    void procesarLogin_autor_redirectsToDashboard() {
        UsuarioSession u = buildSession("AUTOR");
        when(usuarioService.autenticarUsuario("autor@test.com", "pass")).thenReturn(u);

        String result = controller.procesarLogin("autor@test.com", "pass",
                new MockHttpSession(), new ConcurrentModel());

        assertEquals("redirect:/autor/dashboard", result);
    }

    @Test
    void procesarLogin_admin_redirectsToEstadisticas() {
        UsuarioSession u = buildSession("ADMIN");
        when(usuarioService.autenticarUsuario("admin@test.com", "pass")).thenReturn(u);

        String result = controller.procesarLogin("admin@test.com", "pass",
                new MockHttpSession(), new ConcurrentModel());

        assertEquals("redirect:/admin/estadisticas", result);
    }


    @Test
    void procesarLogin_withReturnUrl_redirectsToReturnUrl() {
        UsuarioSession u = buildSession("LECTOR");
        when(usuarioService.autenticarUsuario(any(), any())).thenReturn(u);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("returnUrl", "/noticias/5/comentar");

        String result = controller.procesarLogin("l@test.com", "pass", session, new ConcurrentModel());

        assertEquals("redirect:/noticias/5/comentar", result);
        assertNull(session.getAttribute("returnUrl"), "returnUrl must be removed after use");
    }


    @Test
    void logout_invalidatesSessionAndRedirects() {
        MockHttpSession session = new MockHttpSession();
        UsuarioSession u = buildSession("LECTOR");
        session.setAttribute("usuario", u);

        String result = controller.logout(session);

        assertEquals("redirect:/", result);
        assertTrue(session.isInvalid(), "Session must be invalidated after logout");
    }


    private UsuarioSession buildSession(String rol) {
        UsuarioSession u = new UsuarioSession();
        u.setIdUsuario(1);
        u.setNombre("Test");
        u.setApellidos("User");
        u.setEmail("test@test.com");
        u.setRol(rol);
        return u;
    }
}
