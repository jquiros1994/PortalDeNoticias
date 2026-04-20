package com.noticias.service;

import com.noticias.model.Subtema;
import com.noticias.model.Tema;
import com.noticias.model.Usuario;

import java.util.List;
import java.util.Map;

public interface AdminService {

    /** SP_LISTAR_USUARIOS */
    List<Usuario> listarUsuarios();

    /** SP_ACTUALIZAR_ACTIVO_USUARIO */
    void toggleActivoUsuario(int idUsuario, int activo);

    /** SP_INSERTAR_USUARIO (ADMIN variant — any role) */
    void insertarUsuario(String nombre, String apellidos, String email,
                         String password, String rol, int activo);

    /** SP_LISTAR_TEMAS */
    List<Tema> listarTemas();

    /** SP_INSERTAR_TEMA */
    void insertarTema(String nombre, String descripcion);

    /** SP_ACTUALIZAR_TEMA */
    void actualizarTema(int idTema, String nombre, String descripcion);

    /** SP_ELIMINAR_TEMA */
    void eliminarTema(int idTema);

    /** SP_LISTAR_SUBTEMAS */
    List<Subtema> listarSubtemas();

    /** SP_INSERTAR_SUBTEMA */
    void insertarSubtema(String nombre, int idTema);

    /** SP_ACTUALIZAR_SUBTEMA */
    void actualizarSubtema(int idSubtema, String nombre, int idTema);

    /** SP_ELIMINAR_SUBTEMA */
    void eliminarSubtema(int idSubtema);

    /** SP_COMENTARIOS_POR_NOTICIA */
    List<Map<String, Object>> comentariosPorNoticia();

    /** SP_PROMEDIO_CALIFICACION_POR_NOTICIA */
    List<Map<String, Object>> promedioCalificacionPorNoticia();

    /** SP_ARTICULOS_POR_AUTOR */
    List<Map<String, Object>> articulosPorAutor();

    /** SP_TOP_NOTICIAS_CALIFICADAS */
    List<Map<String, Object>> topNoticiasCalificadas();

    /** SP_TOP_NOTICIAS_COMENTADAS */
    List<Map<String, Object>> topNoticiasComentadas();

    /** SP_ARTICULOS_POR_ESTADO */
    List<Map<String, Object>> articulosPorEstado();

    /** FN_CONTAR_USUARIOS */
    int contarUsuarios();

    /** FN_CONTAR_NOTICIAS_PUBLICADAS (total, no filter) */
    int contarNoticiasPublicadas();

    /** FN_PROMEDIO_GENERAL_PORTAL */
    double promedioGeneralPortal();
}
