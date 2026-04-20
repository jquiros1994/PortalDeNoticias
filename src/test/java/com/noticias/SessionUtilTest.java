package com.noticias;

import com.noticias.dto.UsuarioSession;
import com.noticias.util.SessionUtil;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

import static org.junit.jupiter.api.Assertions.*;

class SessionUtilTest {

    @Test
    void getUsuarioReturnsNullWhenNoSession() {
        MockHttpSession session = new MockHttpSession();
        assertNull(SessionUtil.getUsuario(session));
    }

    @Test
    void getUsuarioReturnsUserWhenSet() {
        MockHttpSession session = new MockHttpSession();
        UsuarioSession u = new UsuarioSession();
        u.setRol("AUTOR");
        session.setAttribute("usuario", u);

        UsuarioSession result = SessionUtil.getUsuario(session);
        assertNotNull(result);
        assertEquals("AUTOR", result.getRol());
    }

    @Test
    void tieneRolReturnsFalseWhenNoSession() {
        MockHttpSession session = new MockHttpSession();
        assertFalse(SessionUtil.tieneRol(session, "ADMIN"));
    }

    @Test
    void tieneRolReturnsTrueForMatchingRole() {
        MockHttpSession session = new MockHttpSession();
        UsuarioSession u = new UsuarioSession();
        u.setRol("ADMIN");
        session.setAttribute("usuario", u);

        assertTrue(SessionUtil.tieneRol(session, "ADMIN"));
    }

    @Test
    void tieneRolReturnsFalseForDifferentRole() {
        MockHttpSession session = new MockHttpSession();
        UsuarioSession u = new UsuarioSession();
        u.setRol("LECTOR");
        session.setAttribute("usuario", u);

        assertFalse(SessionUtil.tieneRol(session, "AUTOR"));
    }
}
