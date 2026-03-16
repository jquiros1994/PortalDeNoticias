package com.noticias.service;

import com.noticias.dto.UsuarioSession;

public interface UsuarioService {

    /** SP_INSERTAR_USUARIO */
    void insertarUsuario(String nombre, String apellidos, String email,
                         String password, String rol, int activo);

    /** SP_AUTENTICAR_USUARIO */
    UsuarioSession autenticarUsuario(String email, String password);
}
