package com.noticias.service.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.stereotype.Service;

import com.noticias.dto.UsuarioSession;
import com.noticias.service.UsuarioService;

import oracle.jdbc.OracleTypes;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final DataSource dataSource;

    public UsuarioServiceImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // ----------------------------------------------------------------
    // SP: PKG_USUARIOS.SP_INSERTAR_USUARIO
    // ----------------------------------------------------------------
    @Override
    public void insertarUsuario(String nombre, String apellidos, String email,
                                String password, String rol, int activo) {
        // SP: PKG_USUARIOS.SP_INSERTAR_USUARIO
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(
                     "{call PKG_USUARIOS.SP_INSERTAR_USUARIO(?,?,?,?,?,?)}")) {
            cs.setString(1, nombre);
            cs.setString(2, apellidos);
            cs.setString(3, email);
            cs.setString(4, password);
            cs.setString(5, rol);
            cs.setInt(6, activo);
            cs.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error al registrar usuario: " + e.getMessage(), e);
        }
    }

    // ----------------------------------------------------------------
    // FN: PKG_USUARIOS.FN_EXISTE_EMAIL
    // ----------------------------------------------------------------
    @Override
    public boolean existeEmail(String email) {
        // SP: PKG_USUARIOS.FN_EXISTE_EMAIL
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(
                     "{? = call PKG_USUARIOS.FN_EXISTE_EMAIL(?)}")) {
            cs.registerOutParameter(1, java.sql.Types.NUMERIC);
            cs.setString(2, email);
            cs.execute();
            return cs.getInt(1) == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    // ----------------------------------------------------------------
    // SP: PKG_SEGURIDAD.SP_AUTENTICAR_USUARIO
    // ----------------------------------------------------------------
    @Override
    public UsuarioSession autenticarUsuario(String email, String password) {
        // SP: PKG_SEGURIDAD.SP_AUTENTICAR_USUARIO
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(
                     "{call PKG_SEGURIDAD.SP_AUTENTICAR_USUARIO(?,?,?)}")) {
            cs.setString(1, email);
            cs.setString(2, password);
            cs.registerOutParameter(3, OracleTypes.CURSOR);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(3);
            if (rs == null || !rs.next()) {
                return null; // credentials not found
            }

            int activo = rs.getInt("ACTIVO");
            if (activo == 0) {
                rs.close();
                throw new RuntimeException("Cuenta desactivada.");
            }

            UsuarioSession usuario = new UsuarioSession();
            usuario.setIdUsuario(rs.getInt("ID_USUARIO"));
            usuario.setNombre(rs.getString("NOMBRE"));
            usuario.setApellidos(rs.getString("APELLIDOS"));
            usuario.setEmail(rs.getString("EMAIL"));
            usuario.setRol(rs.getString("ROL"));
            rs.close();
            return usuario;

        } catch (RuntimeException e) {
            throw e;
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }
}
