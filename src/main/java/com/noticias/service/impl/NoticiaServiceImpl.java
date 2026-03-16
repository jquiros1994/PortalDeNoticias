package com.noticias.service.impl;

import com.noticias.model.Comentario;
import com.noticias.model.Noticia;
import com.noticias.model.Subtema;
import com.noticias.model.Tema;
import com.noticias.service.NoticiaService;
import oracle.jdbc.OracleTypes;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class NoticiaServiceImpl implements NoticiaService {

    private final DataSource dataSource;

    public NoticiaServiceImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // ----------------------------------------------------------------
    // SP: PKG_NOTICIAS.SP_LISTAR_NOTICIAS_PUBLICADAS
    // ----------------------------------------------------------------
    @Override
    public List<Noticia> listarNoticiasPublicadas(int idTema, int idSubtema, int offset, int limite) {
        // SP: PKG_NOTICIAS.SP_LISTAR_NOTICIAS_PUBLICADAS
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(
                     "{call PKG_NOTICIAS.SP_LISTAR_NOTICIAS_PUBLICADAS(?,?,?,?,?,?)}")) {
            cs.setInt(1, idTema);
            cs.setInt(2, idSubtema);
            cs.setInt(3, offset);
            cs.setInt(4, limite);
            cs.registerOutParameter(5, OracleTypes.CURSOR);
            cs.registerOutParameter(6, OracleTypes.NUMBER);
            cs.execute();

            List<Noticia> lista = new ArrayList<>();
            ResultSet rs = (ResultSet) cs.getObject(5);
            while (rs.next()) {
                lista.add(mapNoticiaListado(rs));
            }
            rs.close();
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    // ----------------------------------------------------------------
    // SP: PKG_NOTICIAS.SP_LISTAR_NOTICIAS_PUBLICADAS (o_total only)
    // ----------------------------------------------------------------
    @Override
    public int contarNoticiasPublicadas(int idTema, int idSubtema) {
        // SP: PKG_NOTICIAS.SP_LISTAR_NOTICIAS_PUBLICADAS
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(
                     "{call PKG_NOTICIAS.SP_LISTAR_NOTICIAS_PUBLICADAS(?,?,?,?,?,?)}")) {
            cs.setInt(1, idTema);
            cs.setInt(2, idSubtema);
            cs.setInt(3, 0);
            cs.setInt(4, Integer.MAX_VALUE);
            cs.registerOutParameter(5, OracleTypes.CURSOR);
            cs.registerOutParameter(6, OracleTypes.NUMBER);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(5);
            if (rs != null) rs.close();
            return cs.getInt(6);
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    // ----------------------------------------------------------------
    // SP: PKG_NOTICIAS.SP_OBTENER_NOTICIA_POR_ID
    // ----------------------------------------------------------------
    @Override
    public Noticia obtenerNoticiaPorId(int idNoticia) {
        // SP: PKG_NOTICIAS.SP_OBTENER_NOTICIA_POR_ID
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(
                     "{call PKG_NOTICIAS.SP_OBTENER_NOTICIA_POR_ID(?,?)}")) {
            cs.setInt(1, idNoticia);
            cs.registerOutParameter(2, OracleTypes.CURSOR);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(2);
            if (rs == null || !rs.next()) {
                throw new RuntimeException("Artículo no encontrado o no publicado.");
            }
            Noticia noticia = mapNoticiaDetalle(rs);
            rs.close();
            return noticia;
        } catch (RuntimeException e) {
            throw e;
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    // ----------------------------------------------------------------
    // SP: PKG_COMENTARIOS.SP_LISTAR_COMENTARIOS_POR_NOTICIA
    // ----------------------------------------------------------------
    @Override
    public List<Comentario> listarComentariosPorNoticia(int idNoticia) {
        // SP: PKG_COMENTARIOS.SP_LISTAR_COMENTARIOS_POR_NOTICIA
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(
                     "{call PKG_COMENTARIOS.SP_LISTAR_COMENTARIOS_POR_NOTICIA(?,?)}")) {
            cs.setInt(1, idNoticia);
            cs.registerOutParameter(2, OracleTypes.CURSOR);
            cs.execute();

            List<Comentario> lista = new ArrayList<>();
            ResultSet rs = (ResultSet) cs.getObject(2);
            while (rs.next()) {
                Comentario c = new Comentario();
                c.setIdComentario(rs.getInt("ID_COMENTARIO"));
                c.setContenido(rs.getString("CONTENIDO"));
                c.setFechaComentario(rs.getDate("FECHA_COMENTARIO"));
                c.setNombreAutor(rs.getString("NOMBRE_AUTOR"));
            lista.add(c);
            }
            rs.close();
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    // ----------------------------------------------------------------
    // SP: PKG_TEMAS.SP_LISTAR_TEMAS
    // ----------------------------------------------------------------
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

    // ----------------------------------------------------------------
    // SP: PKG_SUBTEMAS.SP_LISTAR_SUBTEMAS (filtered by tema)
    // Uses FN_LISTAR_SUBTEMAS_POR_TEMA via a FUNCTION call
    // ----------------------------------------------------------------
    @Override
    public List<Subtema> listarSubtemasPorTema(int idTema) {
        // SP: PKG_SUBTEMAS.FN_LISTAR_SUBTEMAS_POR_TEMA
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(
                     "{? = call PKG_SUBTEMAS.FN_LISTAR_SUBTEMAS_POR_TEMA(?)}")) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setInt(2, idTema);
            cs.execute();

            List<Subtema> lista = new ArrayList<>();
            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                Subtema s = new Subtema();
                s.setIdSubtema(rs.getInt("ID_SUBTEMA"));
                s.setNombre(rs.getString("NOMBRE"));
                s.setIdTema(idTema);
                lista.add(s);
            }
            rs.close();
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    // ----------------------------------------------------------------
    // SP: PKG_COMENTARIOS.SP_INSERTAR_COMENTARIO
    // ----------------------------------------------------------------
    @Override
    public void insertarComentario(int idNoticia, int idUsuario, String contenido) {
        // SP: PKG_COMENTARIOS.SP_INSERTAR_COMENTARIO
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(
                     "{call PKG_COMENTARIOS.SP_INSERTAR_COMENTARIO(?,?,?)}")) {
            cs.setInt(1, idNoticia);
            cs.setInt(2, idUsuario);
            cs.setString(3, contenido);
            cs.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    // ----------------------------------------------------------------
    // SP: PKG_CALIFICACIONES.SP_INSERTAR_CALIFICACION
    // ----------------------------------------------------------------
    @Override
    public void insertarCalificacion(int idNoticia, int idUsuario, int valor) {
        // SP: PKG_CALIFICACIONES.SP_INSERTAR_CALIFICACION
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(
                     "{call PKG_CALIFICACIONES.SP_INSERTAR_CALIFICACION(?,?,?)}")) {
            cs.setInt(1, idNoticia);
            cs.setInt(2, idUsuario);
            cs.setInt(3, valor);
            cs.execute();
        } catch (SQLException e) {
            // ORA-00001 unique constraint = duplicate rating
            if (e.getErrorCode() == 1 || e.getMessage().contains("UQ_CALIF")) {
                throw new RuntimeException("Ya has calificado este artículo.", e);
            }
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    // ----------------------------------------------------------------
    // SP: PKG_CALIFICACIONES.FN_OBTENER_CALIFICACION_USUARIO
    // ----------------------------------------------------------------
    @Override
    public int obtenerCalificacionUsuario(int idNoticia, int idUsuario) {
        // SP: PKG_CALIFICACIONES.FN_OBTENER_CALIFICACION_USUARIO
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(
                     "{? = call PKG_CALIFICACIONES.FN_OBTENER_CALIFICACION_USUARIO(?,?)}")) {
            cs.registerOutParameter(1, Types.NUMERIC);
            cs.setInt(2, idNoticia);
            cs.setInt(3, idUsuario);
            cs.execute();
            return cs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    // ----------------------------------------------------------------
    // SP: PKG_NOTICIAS.SP_LISTAR_NOTICIAS_POR_AUTOR
    // ----------------------------------------------------------------
    @Override
    public List<Noticia> listarNoticiasPorAutor(int idAutor) {
        // SP: PKG_NOTICIAS.SP_LISTAR_NOTICIAS_POR_AUTOR
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(
                     "{call PKG_NOTICIAS.SP_LISTAR_NOTICIAS_POR_AUTOR(?,?)}")) {
            cs.setInt(1, idAutor);
            cs.registerOutParameter(2, OracleTypes.CURSOR);
            cs.execute();

            List<Noticia> lista = new ArrayList<>();
            ResultSet rs = (ResultSet) cs.getObject(2);
            while (rs.next()) {
                Noticia n = new Noticia();
                n.setIdNoticia(rs.getInt("ID_NOTICIA"));
                n.setTitulo(rs.getString("TITULO"));
                n.setEstado(rs.getString("ESTADO"));
                n.setFechaCreacion(rs.getDate("FECHA_CREACION"));
                n.setFechaPublicacion(rs.getDate("FECHA_PUBLICACION"));
                n.setPromedioCalificacion(rs.getDouble("PROMEDIO_CALIFICACION"));
                n.setNombreTema(rs.getString("NOMBRE_TEMA"));
                n.setNombreSubtema(rs.getString("NOMBRE_SUBTEMA"));
                lista.add(n);
            }
            rs.close();
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    // ----------------------------------------------------------------
    // SP: PKG_NOTICIAS.SP_INSERTAR_NOTICIA
    // ----------------------------------------------------------------
    @Override
    public void insertarNoticia(String titulo, String cuerpo, int idAutor, int idTema, int idSubtema) {
        // SP: PKG_NOTICIAS.SP_INSERTAR_NOTICIA
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(
                     "{call PKG_NOTICIAS.SP_INSERTAR_NOTICIA(?,?,?,?,?)}")) {
            cs.setString(1, titulo);
            cs.setString(2, cuerpo);
            cs.setInt(3, idAutor);
            cs.setInt(4, idTema);
            cs.setInt(5, idSubtema);
            cs.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    // ----------------------------------------------------------------
    // SP: PKG_NOTICIAS.SP_ACTUALIZAR_NOTICIA
    // ----------------------------------------------------------------
    @Override
    public void actualizarNoticia(int idNoticia, String titulo, String cuerpo, int idTema, int idSubtema) {
        // SP: PKG_NOTICIAS.SP_ACTUALIZAR_NOTICIA
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(
                     "{call PKG_NOTICIAS.SP_ACTUALIZAR_NOTICIA(?,?,?,?,?)}")) {
            cs.setInt(1, idNoticia);
            cs.setString(2, titulo);
            cs.setString(3, cuerpo);
            cs.setInt(4, idTema);
            cs.setInt(5, idSubtema);
            cs.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    // ----------------------------------------------------------------
    // SP: PKG_NOTICIAS.SP_ELIMINAR_NOTICIA
    // ----------------------------------------------------------------
    @Override
    public void eliminarNoticia(int idNoticia) {
        // SP: PKG_NOTICIAS.SP_ELIMINAR_NOTICIA
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(
                     "{call PKG_NOTICIAS.SP_ELIMINAR_NOTICIA(?)}")) {
            cs.setInt(1, idNoticia);
            cs.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    // ----------------------------------------------------------------
    // SP: PKG_NOTICIAS.SP_CAMBIAR_ESTADO_NOTICIA
    // ----------------------------------------------------------------
    @Override
    public void cambiarEstadoNoticia(int idNoticia, String estado) {
        // SP: PKG_NOTICIAS.SP_CAMBIAR_ESTADO_NOTICIA
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(
                     "{call PKG_NOTICIAS.SP_CAMBIAR_ESTADO_NOTICIA(?,?)}")) {
            cs.setInt(1, idNoticia);
            cs.setString(2, estado);
            cs.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    // ----------------------------------------------------------------
    // SP: PKG_NOTICIAS.FN_VERIFICAR_PROPIETARIO_NOTICIA
    // ----------------------------------------------------------------
    @Override
    public int verificarPropietarioNoticia(int idNoticia, int idAutor) {
        // SP: PKG_NOTICIAS.FN_VERIFICAR_PROPIETARIO_NOTICIA
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(
                     "{? = call PKG_NOTICIAS.FN_VERIFICAR_PROPIETARIO_NOTICIA(?,?)}")) {
            cs.registerOutParameter(1, Types.NUMERIC);
            cs.setInt(2, idNoticia);
            cs.setInt(3, idAutor);
            cs.execute();
            return cs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    // ----------------------------------------------------------------
    // SP: PKG_NOTICIAS.SP_LISTAR_TODAS_NOTICIAS
    // ----------------------------------------------------------------
    @Override
    public List<Noticia> listarTodasNoticias() {
        // SP: PKG_NOTICIAS.SP_LISTAR_TODAS_NOTICIAS
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(
                     "{call PKG_NOTICIAS.SP_LISTAR_TODAS_NOTICIAS(?)}")) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            List<Noticia> lista = new ArrayList<>();
            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                Noticia n = new Noticia();
                n.setIdNoticia(rs.getInt("ID_NOTICIA"));
                n.setTitulo(rs.getString("TITULO"));
                n.setEstado(rs.getString("ESTADO"));
                n.setFechaCreacion(rs.getDate("FECHA_CREACION"));
                n.setFechaPublicacion(rs.getDate("FECHA_PUBLICACION"));
                n.setNombreTema(rs.getString("NOMBRE_TEMA"));
                n.setNombreSubtema(rs.getString("NOMBRE_SUBTEMA"));
                n.setNombreAutor(rs.getString("NOMBRE_AUTOR"));
                n.setIdAutor(rs.getInt("ID_AUTOR"));
                lista.add(n);
            }
            rs.close();
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    // ----------------------------------------------------------------
    // Private mapping helpers
    // ----------------------------------------------------------------

    private Noticia mapNoticiaListado(ResultSet rs) throws SQLException {
        Noticia n = new Noticia();
        n.setIdNoticia(rs.getInt("ID_NOTICIA"));
        n.setTitulo(rs.getString("TITULO"));
        n.setFechaPublicacion(rs.getDate("FECHA_PUBLICACION"));
        n.setPromedioCalificacion(rs.getDouble("PROMEDIO_CALIFICACION"));
        n.setNombreTema(rs.getString("NOMBRE_TEMA"));
        n.setNombreSubtema(rs.getString("NOMBRE_SUBTEMA"));
        n.setNombreAutor(rs.getString("NOMBRE_AUTOR"));
        return n;
    }

    private Noticia mapNoticiaDetalle(ResultSet rs) throws SQLException {
        Noticia n = new Noticia();
        n.setIdNoticia(rs.getInt("ID_NOTICIA"));
        n.setTitulo(rs.getString("TITULO"));
        // CLOB handling per project-context.md
        Clob clob = rs.getClob("CUERPO");
        n.setCuerpo(clob != null ? clob.getSubString(1, (int) clob.length()) : "");
        n.setEstado(rs.getString("ESTADO"));
        n.setFechaCreacion(rs.getDate("FECHA_CREACION"));
        n.setFechaPublicacion(rs.getDate("FECHA_PUBLICACION"));
        n.setPromedioCalificacion(rs.getDouble("PROMEDIO_CALIFICACION"));
        n.setIdAutor(rs.getInt("ID_AUTOR"));
        n.setIdTema(rs.getInt("ID_TEMA"));
        n.setIdSubtema(rs.getInt("ID_SUBTEMA"));
        n.setNombreTema(rs.getString("NOMBRE_TEMA"));
        n.setNombreSubtema(rs.getString("NOMBRE_SUBTEMA"));
        n.setNombreAutor(rs.getString("NOMBRE_AUTOR"));
        return n;
    }
}
