-- ============================================================
-- ProyectoFinal News Portal — Package Specifications
-- Script: 02_pkg_spec.sql
-- All 10 package headers declared here before bodies
-- ============================================================

-- ============================================================
-- PKG_USUARIOS
-- ============================================================
CREATE OR REPLACE PACKAGE PKG_USUARIOS AS
    PROCEDURE SP_INSERTAR_USUARIO(
        p_nombre    IN  VARCHAR2,
        p_apellidos IN  VARCHAR2,
        p_email     IN  VARCHAR2,
        p_password  IN  VARCHAR2,
        p_rol       IN  VARCHAR2,
        p_activo    IN  NUMBER
    );
    PROCEDURE SP_LISTAR_USUARIOS(
        o_cursor OUT SYS_REFCURSOR
    );
    PROCEDURE SP_ACTUALIZAR_ACTIVO_USUARIO(
        p_id_usuario IN NUMBER,
        p_activo     IN NUMBER
    );
    FUNCTION FN_EXISTE_EMAIL(
        p_email VARCHAR2
    ) RETURN NUMBER;
    FUNCTION FN_CONTAR_USUARIOS RETURN NUMBER;
END PKG_USUARIOS;
/

-- ============================================================
-- PKG_NOTICIAS
-- ============================================================
CREATE OR REPLACE PACKAGE PKG_NOTICIAS AS
    PROCEDURE SP_INSERTAR_NOTICIA(
        p_titulo     IN  VARCHAR2,
        p_cuerpo     IN  CLOB,
        p_id_autor   IN  NUMBER,
        p_id_tema    IN  NUMBER,
        p_id_subtema IN  NUMBER
    );
    PROCEDURE SP_LISTAR_NOTICIAS_PUBLICADAS(
        p_id_tema    IN  NUMBER,
        p_id_subtema IN  NUMBER,
        p_offset     IN  NUMBER,
        p_limite     IN  NUMBER,
        o_cursor     OUT SYS_REFCURSOR,
        o_total      OUT NUMBER
    );
    PROCEDURE SP_LISTAR_NOTICIAS_POR_AUTOR(
        p_id_autor IN  NUMBER,
        o_cursor   OUT SYS_REFCURSOR
    );
    PROCEDURE SP_OBTENER_NOTICIA_POR_ID(
        p_id_noticia IN  NUMBER,
        o_cursor     OUT SYS_REFCURSOR
    );
    PROCEDURE SP_ACTUALIZAR_NOTICIA(
        p_id_noticia IN NUMBER,
        p_titulo     IN VARCHAR2,
        p_cuerpo     IN CLOB,
        p_id_tema    IN NUMBER,
        p_id_subtema IN NUMBER
    );
    PROCEDURE SP_ELIMINAR_NOTICIA(
        p_id_noticia IN NUMBER
    );
    PROCEDURE SP_CAMBIAR_ESTADO_NOTICIA(
        p_id_noticia IN NUMBER,
        p_estado     IN VARCHAR2
    );
    PROCEDURE SP_LISTAR_TODAS_NOTICIAS(
        o_cursor OUT SYS_REFCURSOR
    );
    FUNCTION FN_VERIFICAR_PROPIETARIO_NOTICIA(
        p_id_noticia NUMBER,
        p_id_autor   NUMBER
    ) RETURN NUMBER;
    FUNCTION FN_CONTAR_NOTICIAS_PUBLICADAS(
        p_id_tema    NUMBER,
        p_id_subtema NUMBER
    ) RETURN NUMBER;
    FUNCTION FN_CONTAR_NOTICIAS_POR_AUTOR(
        p_id_autor NUMBER
    ) RETURN NUMBER;
END PKG_NOTICIAS;
/

-- ============================================================
-- PKG_TEMAS
-- ============================================================
CREATE OR REPLACE PACKAGE PKG_TEMAS AS
    PROCEDURE SP_INSERTAR_TEMA(
        p_nombre      IN VARCHAR2,
        p_descripcion IN VARCHAR2
    );
    PROCEDURE SP_ACTUALIZAR_TEMA(
        p_id_tema     IN NUMBER,
        p_nombre      IN VARCHAR2,
        p_descripcion IN VARCHAR2
    );
    PROCEDURE SP_ELIMINAR_TEMA(
        p_id_tema IN NUMBER
    );
    PROCEDURE SP_LISTAR_TEMAS(
        o_cursor OUT SYS_REFCURSOR
    );
    FUNCTION FN_LISTAR_TEMAS RETURN SYS_REFCURSOR;
END PKG_TEMAS;
/

-- ============================================================
-- PKG_SUBTEMAS
-- ============================================================
CREATE OR REPLACE PACKAGE PKG_SUBTEMAS AS
    PROCEDURE SP_INSERTAR_SUBTEMA(
        p_nombre   IN VARCHAR2,
        p_id_tema  IN NUMBER
    );
    PROCEDURE SP_ACTUALIZAR_SUBTEMA(
        p_id_subtema IN NUMBER,
        p_nombre     IN VARCHAR2,
        p_id_tema    IN NUMBER
    );
    PROCEDURE SP_ELIMINAR_SUBTEMA(
        p_id_subtema IN NUMBER
    );
    PROCEDURE SP_LISTAR_SUBTEMAS(
        o_cursor OUT SYS_REFCURSOR
    );
    FUNCTION FN_LISTAR_SUBTEMAS_POR_TEMA(
        p_id_tema NUMBER
    ) RETURN SYS_REFCURSOR;
END PKG_SUBTEMAS;
/

-- ============================================================
-- PKG_COMENTARIOS
-- ============================================================
CREATE OR REPLACE PACKAGE PKG_COMENTARIOS AS
    PROCEDURE SP_INSERTAR_COMENTARIO(
        p_id_noticia IN NUMBER,
        p_id_usuario IN NUMBER,
        p_contenido  IN VARCHAR2
    );
    PROCEDURE SP_LISTAR_COMENTARIOS_POR_NOTICIA(
        p_id_noticia IN  NUMBER,
        o_cursor     OUT SYS_REFCURSOR
    );
    FUNCTION FN_CONTAR_COMENTARIOS_POR_NOTICIA(
        p_id_noticia NUMBER
    ) RETURN NUMBER;
END PKG_COMENTARIOS;
/

-- ============================================================
-- PKG_CALIFICACIONES
-- ============================================================
CREATE OR REPLACE PACKAGE PKG_CALIFICACIONES AS
    PROCEDURE SP_INSERTAR_CALIFICACION(
        p_id_noticia IN NUMBER,
        p_id_usuario IN NUMBER,
        p_valor      IN NUMBER
    );
    FUNCTION FN_OBTENER_CALIFICACION_USUARIO(
        p_id_noticia NUMBER,
        p_id_usuario NUMBER
    ) RETURN NUMBER;
    FUNCTION FN_TIENE_CALIFICACION(
        p_id_noticia NUMBER,
        p_id_usuario NUMBER
    ) RETURN NUMBER;
END PKG_CALIFICACIONES;
/

-- ============================================================
-- PKG_ESTADISTICAS
-- ============================================================
CREATE OR REPLACE PACKAGE PKG_ESTADISTICAS AS
    PROCEDURE SP_COMENTARIOS_POR_NOTICIA(
        o_cursor OUT SYS_REFCURSOR
    );
    PROCEDURE SP_PROMEDIO_CALIFICACION_POR_NOTICIA(
        o_cursor OUT SYS_REFCURSOR
    );
    PROCEDURE SP_ARTICULOS_POR_AUTOR(
        o_cursor OUT SYS_REFCURSOR
    );
    PROCEDURE SP_TOP_NOTICIAS_CALIFICADAS(
        o_cursor OUT SYS_REFCURSOR
    );
    PROCEDURE SP_TOP_NOTICIAS_COMENTADAS(
        o_cursor OUT SYS_REFCURSOR
    );
    PROCEDURE SP_ARTICULOS_POR_ESTADO(
        o_cursor OUT SYS_REFCURSOR
    );
    FUNCTION FN_PROMEDIO_GENERAL_PORTAL RETURN NUMBER;
END PKG_ESTADISTICAS;
/

-- ============================================================
-- PKG_SEGURIDAD
-- ============================================================
CREATE OR REPLACE PACKAGE PKG_SEGURIDAD AS
    PROCEDURE SP_AUTENTICAR_USUARIO(
        p_email    IN  VARCHAR2,
        p_password IN  VARCHAR2,
        o_cursor   OUT SYS_REFCURSOR
    );
END PKG_SEGURIDAD;
/

-- ============================================================
-- PKG_UTILES
-- ============================================================
CREATE OR REPLACE PACKAGE PKG_UTILES AS
    FUNCTION FN_FORMATEAR_FECHA(
        p_fecha DATE
    ) RETURN VARCHAR2;
    FUNCTION FN_TRUNCAR_TEXTO(
        p_texto   VARCHAR2,
        p_max_len NUMBER
    ) RETURN VARCHAR2;
END PKG_UTILES;
/
