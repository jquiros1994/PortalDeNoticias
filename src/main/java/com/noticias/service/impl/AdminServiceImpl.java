package com.noticias.service.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.stereotype.Service;

import com.noticias.model.Subtema;
import com.noticias.model.Tema;
import com.noticias.model.Usuario;
import com.noticias.service.AdminService;

import oracle.jdbc.OracleTypes;

@Service
public class AdminServiceImpl implements AdminService {

    private final DataSource dataSource;

    public AdminServiceImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // ----------------------------------------------------------------
    // SP: PKG_USUARIOS.SP_LISTAR_USUARIOS

    @Override
    public List<Usuario> listarUsuarios() {
        // SP: PKG_USUARIOS.SP_LISTAR_USUARIOS
        try (Connection conn = dataSource.getConnection();
                CallableStatement cs = conn.prepareCall(
                        "{call PKG_USUARIOS.SP_LISTAR_USUARIOS(?)}")) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();
            List<Usuario> lista = new ArrayList<>();
            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("ID_USUARIO"));
                u.setNombre(rs.getString("NOMBRE"));
                u.setApellidos(rs.getString("APELLIDOS"));
                u.setEmail(rs.getString("EMAIL"));
                u.setRol(rs.getString("ROL"));
                u.setActivo(rs.getInt("ACTIVO"));
                lista.add(u);
            }
            rs.close();
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

  
    // SP: PKG_USUARIOS.SP_ACTUALIZAR_ACTIVO_USUARIO
    
    @Override
    public void toggleActivoUsuario(int idUsuario, int activo) {
        // SP: PKG_USUARIOS.SP_ACTUALIZAR_ACTIVO_USUARIO
        try (Connection conn = dataSource.getConnection();
                CallableStatement cs = conn.prepareCall(
                        "{call PKG_USUARIOS.SP_ACTUALIZAR_ACTIVO_USUARIO(?,?)}")) {
            cs.setInt(1, idUsuario);
            cs.setInt(2, activo);
            cs.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

   
    // SP: PKG_USUARIOS.SP_INSERTAR_USUARIO (ADMIN creates any role)

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
            throw new RuntimeException("Error al crear usuario: " + e.getMessage(), e);
        }
    }

   
    // SP: PKG_TEMAS.SP_LISTAR_TEMAS

    @Override
    public List<Tema> listarTemas() {
        // SP: PKG_TEMAS.SP_LISTAR_TEMAS
        try (Connection conn = dataSource.getConnection();
                CallableStatement cs = conn.prepareCall(
                        "{call PKG_TEMAS.SP_LISTAR_TEMAS(?)}")) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();
            List<Tema> lista = new ArrayList<>();
            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                Tema t = new Tema();
                t.setIdTema(rs.getInt("ID_TEMA"));
                t.setNombre(rs.getString("NOMBRE"));
                t.setDescripcion(rs.getString("DESCRIPCION"));
                lista.add(t);
            }
            rs.close();
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

   
    // SP: PKG_TEMAS.SP_INSERTAR_TEMA
  
    @Override
    public void insertarTema(String nombre, String descripcion) {
        // SP: PKG_TEMAS.SP_INSERTAR_TEMA
        try (Connection conn = dataSource.getConnection();
                CallableStatement cs = conn.prepareCall(
                        "{call PKG_TEMAS.SP_INSERTAR_TEMA(?,?)}")) {
            cs.setString(1, nombre);
            cs.setString(2, descripcion);
            cs.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

 
    // SP: PKG_TEMAS.SP_ACTUALIZAR_TEMA
    
    @Override
    public void actualizarTema(int idTema, String nombre, String descripcion) {
        // SP: PKG_TEMAS.SP_ACTUALIZAR_TEMA
        try (Connection conn = dataSource.getConnection();
                CallableStatement cs = conn.prepareCall(
                        "{call PKG_TEMAS.SP_ACTUALIZAR_TEMA(?,?,?)}")) {
            cs.setInt(1, idTema);
            cs.setString(2, nombre);
            cs.setString(3, descripcion);
            cs.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    
    // SP: PKG_TEMAS.SP_ELIMINAR_TEMA

    @Override
    public void eliminarTema(int idTema) {
        // SP: PKG_TEMAS.SP_ELIMINAR_TEMA
        try (Connection conn = dataSource.getConnection();
                CallableStatement cs = conn.prepareCall(
                        "{call PKG_TEMAS.SP_ELIMINAR_TEMA(?)}")) {
            cs.setInt(1, idTema);
            cs.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }


    // SP: PKG_SUBTEMAS.SP_LISTAR_SUBTEMAS

    @Override
    public List<Subtema> listarSubtemas() {
        // SP: PKG_SUBTEMAS.SP_LISTAR_SUBTEMAS
        try (Connection conn = dataSource.getConnection();
                CallableStatement cs = conn.prepareCall(
                        "{call PKG_SUBTEMAS.SP_LISTAR_SUBTEMAS(?)}")) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();
            List<Subtema> lista = new ArrayList<>();
            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                Subtema s = new Subtema();
                s.setIdSubtema(rs.getInt("ID_SUBTEMA"));
                s.setNombre(rs.getString("NOMBRE"));
                s.setIdTema(rs.getInt("ID_TEMA"));
                s.setNombreTema(rs.getString("NOMBRE_TEMA"));
                lista.add(s);
            }
            rs.close();
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

  
    // SP: PKG_SUBTEMAS.SP_INSERTAR_SUBTEMA

    @Override
    public void insertarSubtema(String nombre, int idTema) {
        // SP: PKG_SUBTEMAS.SP_INSERTAR_SUBTEMA
        try (Connection conn = dataSource.getConnection();
                CallableStatement cs = conn.prepareCall(
                        "{call PKG_SUBTEMAS.SP_INSERTAR_SUBTEMA(?,?)}")) {
            cs.setString(1, nombre);
            cs.setInt(2, idTema);
            cs.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    // ----------------------------------------------------------------
    // SP: PKG_SUBTEMAS.SP_ACTUALIZAR_SUBTEMA
   
    @Override
    public void actualizarSubtema(int idSubtema, String nombre, int idTema) {
        // SP: PKG_SUBTEMAS.SP_ACTUALIZAR_SUBTEMA
        try (Connection conn = dataSource.getConnection();
                CallableStatement cs = conn.prepareCall(
                        "{call PKG_SUBTEMAS.SP_ACTUALIZAR_SUBTEMA(?,?,?)}")) {
            cs.setInt(1, idSubtema);
            cs.setString(2, nombre);
            cs.setInt(3, idTema);
            cs.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }


    // SP: PKG_SUBTEMAS.SP_ELIMINAR_SUBTEMA
    
    @Override
    public void eliminarSubtema(int idSubtema) {
        // SP: PKG_SUBTEMAS.SP_ELIMINAR_SUBTEMA
        try (Connection conn = dataSource.getConnection();
                CallableStatement cs = conn.prepareCall(
                        "{call PKG_SUBTEMAS.SP_ELIMINAR_SUBTEMA(?)}")) {
            cs.setInt(1, idSubtema);
            cs.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Map<String, Object>> comentariosPorNoticia() {
        // SP: PKG_ESTADISTICAS.SP_COMENTARIOS_POR_NOTICIA
        return ejecutarEstadistica("{call PKG_ESTADISTICAS.SP_COMENTARIOS_POR_NOTICIA(?)}",
                "ID_NOTICIA", "TITULO", "TOTAL_COMENTARIOS");
    }

    @Override
    public List<Map<String, Object>> promedioCalificacionPorNoticia() {
        // SP: PKG_ESTADISTICAS.SP_PROMEDIO_CALIFICACION_POR_NOTICIA
        return ejecutarEstadistica("{call PKG_ESTADISTICAS.SP_PROMEDIO_CALIFICACION_POR_NOTICIA(?)}",
                "ID_NOTICIA", "TITULO", "PROMEDIO_CALIFICACION");
    }

    @Override
    public List<Map<String, Object>> articulosPorAutor() {
        // SP: PKG_ESTADISTICAS.SP_ARTICULOS_POR_AUTOR
        return ejecutarEstadistica("{call PKG_ESTADISTICAS.SP_ARTICULOS_POR_AUTOR(?)}",
                "ID_USUARIO", "NOMBRE_AUTOR", "TOTAL_ARTICULOS");
    }

    @Override
    public List<Map<String, Object>> topNoticiasCalificadas() {
        // SP: PKG_ESTADISTICAS.SP_TOP_NOTICIAS_CALIFICADAS
        return ejecutarEstadistica("{call PKG_ESTADISTICAS.SP_TOP_NOTICIAS_CALIFICADAS(?)}",
                "RANKING", "ID_NOTICIA", "TITULO", "PROMEDIO_CALIFICACION");
    }

    @Override
    public List<Map<String, Object>> topNoticiasComentadas() {
        // SP: PKG_ESTADISTICAS.SP_TOP_NOTICIAS_COMENTADAS
        return ejecutarEstadistica("{call PKG_ESTADISTICAS.SP_TOP_NOTICIAS_COMENTADAS(?)}",
                "RANKING", "ID_NOTICIA", "TITULO", "TOTAL_COMENTARIOS");
    }

    @Override
    public List<Map<String, Object>> articulosPorEstado() {
        // SP: PKG_ESTADISTICAS.SP_ARTICULOS_POR_ESTADO
        return ejecutarEstadistica("{call PKG_ESTADISTICAS.SP_ARTICULOS_POR_ESTADO(?)}",
                "ESTADO", "TOTAL_ARTICULOS");
    }


    // FN: PKG_USUARIOS.FN_CONTAR_USUARIOS
   
    @Override
    public int contarUsuarios() {
        // SP: PKG_USUARIOS.FN_CONTAR_USUARIOS
        try (Connection conn = dataSource.getConnection();
                CallableStatement cs = conn.prepareCall(
                        "{? = call PKG_USUARIOS.FN_CONTAR_USUARIOS()}")) {
            cs.registerOutParameter(1, Types.NUMERIC);
            cs.execute();
            return cs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

   
    // FN: PKG_NOTICIAS.FN_CONTAR_NOTICIAS_PUBLICADAS

    @Override
    public int contarNoticiasPublicadas() {
        // SP: PKG_NOTICIAS.FN_CONTAR_NOTICIAS_PUBLICADAS
        try (Connection conn = dataSource.getConnection();
                CallableStatement cs = conn.prepareCall(
                        "{? = call PKG_NOTICIAS.FN_CONTAR_NOTICIAS_PUBLICADAS(?,?)}")) {
            cs.registerOutParameter(1, Types.NUMERIC);
            cs.setInt(2, 0);
            cs.setInt(3, 0);
            cs.execute();
            return cs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

  
    // FN: PKG_ESTADISTICAS.FN_PROMEDIO_GENERAL_PORTAL

    @Override
    public double promedioGeneralPortal() {
        // SP: PKG_ESTADISTICAS.FN_PROMEDIO_GENERAL_PORTAL
        try (Connection conn = dataSource.getConnection();
                CallableStatement cs = conn.prepareCall(
                        "{? = call PKG_ESTADISTICAS.FN_PROMEDIO_GENERAL_PORTAL()}")) {
            cs.registerOutParameter(1, Types.NUMERIC);
            cs.execute();
            return cs.getDouble(1);
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    private List<Map<String, Object>> ejecutarEstadistica(String call, String... columnas) {
        try (Connection conn = dataSource.getConnection();
                CallableStatement cs = conn.prepareCall(call)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();
            List<Map<String, Object>> lista = new ArrayList<>();
            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                Map<String, Object> fila = new LinkedHashMap<>();
                for (String col : columnas) {
                    fila.put(col, rs.getObject(col));
                }
                lista.add(fila);
            }
            rs.close();
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }
}
