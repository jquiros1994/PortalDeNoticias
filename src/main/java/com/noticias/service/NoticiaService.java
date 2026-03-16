package com.noticias.service;

import com.noticias.model.Noticia;
import com.noticias.model.Comentario;
import com.noticias.model.Tema;
import com.noticias.model.Subtema;

import java.util.List;

public interface NoticiaService {

    /** SP_LISTAR_NOTICIAS_PUBLICADAS */
    List<Noticia> listarNoticiasPublicadas(int idTema, int idSubtema, int offset, int limite);

    /** Total count for pagination  */
    int contarNoticiasPublicadas(int idTema, int idSubtema);

    /** SP_OBTENER_NOTICIA_POR_ID */
    Noticia obtenerNoticiaPorId(int idNoticia);

    /** SP_LISTAR_COMENTARIOS_POR_NOTICIA */
    List<Comentario> listarComentariosPorNoticia(int idNoticia);

    /** FN_LISTAR_TEMAS — dropdown */
    List<Tema> listarTemas();

    /** FN_LISTAR_SUBTEMAS_POR_TEMA — dropdown */
    List<Subtema> listarSubtemasPorTema(int idTema);

    /** SP_INSERTAR_COMENTARIO */
    void insertarComentario(int idNoticia, int idUsuario, String contenido);

    /** SP_INSERTAR_CALIFICACION */
    void insertarCalificacion(int idNoticia, int idUsuario, int valor);

    /** FN_OBTENER_CALIFICACION_USUARIO */
    int obtenerCalificacionUsuario(int idNoticia, int idUsuario);

    /** SP_LISTAR_NOTICIAS_POR_AUTOR */
    List<Noticia> listarNoticiasPorAutor(int idAutor);

    /** SP_INSERTAR_NOTICIA */
    void insertarNoticia(String titulo, String cuerpo, int idAutor, int idTema, int idSubtema);

    /** SP_ACTUALIZAR_NOTICIA */
    void actualizarNoticia(int idNoticia, String titulo, String cuerpo, int idTema, int idSubtema);

    /** SP_ELIMINAR_NOTICIA */
    void eliminarNoticia(int idNoticia);

    /** SP_CAMBIAR_ESTADO_NOTICIA */
    void cambiarEstadoNoticia(int idNoticia, String estado);

    /** FN_VERIFICAR_PROPIETARIO_NOTICIA — 1=owner, 0=not */
    int verificarPropietarioNoticia(int idNoticia, int idAutor);

    /** SP_LISTAR_TODAS_NOTICIAS — for ADMIN */
    List<Noticia> listarTodasNoticias();
}
