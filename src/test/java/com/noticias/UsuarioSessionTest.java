package com.noticias;

import com.noticias.dto.UsuarioSession;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioSessionTest {

    @Test
    void fieldsGetterSetterWork() {
        UsuarioSession u = new UsuarioSession();
        u.setIdUsuario(42);
        u.setNombre("Juan");
        u.setApellidos("Pérez");
        u.setEmail("juan@test.com");
        u.setRol("LECTOR");

        assertEquals(42, u.getIdUsuario());
        assertEquals("Juan", u.getNombre());
        assertEquals("Pérez", u.getApellidos());
        assertEquals("juan@test.com", u.getEmail());
        assertEquals("LECTOR", u.getRol());
    }

    @Test
    void getNombreCompletoReturnsConcatenation() {
        UsuarioSession u = new UsuarioSession();
        u.setNombre("María");
        u.setApellidos("García");

        assertEquals("María García", u.getNombreCompleto());
    }

    @Test
    void defaultConstructorCreatesObject() {
        UsuarioSession u = new UsuarioSession();
        assertNotNull(u);
        assertEquals(0, u.getIdUsuario());
        assertNull(u.getNombre());
    }
}
