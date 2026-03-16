package com.noticias.util;

import com.noticias.dto.UsuarioSession;
import jakarta.servlet.http.HttpSession;


public class SessionUtil {

    private SessionUtil() {
    }

    
    public static UsuarioSession getUsuario(HttpSession session) {
        return (UsuarioSession) session.getAttribute("usuario");
    }

    public static boolean tieneRol(HttpSession session, String rol) {
        UsuarioSession usuario = getUsuario(session);
        if (usuario == null) {
            return false;
        }
        return rol.equals(usuario.getRol());
    }
}
