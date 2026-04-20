-- ============================================================
-- ProyectoFinal News Portal — PKG_COMENTARIOS Body
-- Script: 07_pkg_comentarios.sql
-- ============================================================

CREATE OR REPLACE PACKAGE BODY PKG_COMENTARIOS AS

    -- --------------------------------------------------------
    -- SP_INSERTAR_COMENTARIO
    -- Inserts a comment. No unique constraint — unlimited per user.
    -- --------------------------------------------------------
    PROCEDURE SP_INSERTAR_COMENTARIO(
        p_id_noticia IN NUMBER,
        p_id_usuario IN NUMBER,
        p_contenido  IN VARCHAR2
    ) AS
    BEGIN
        INSERT INTO COMENTARIOS (
            ID_COMENTARIO, CONTENIDO, FECHA_COMENTARIO, ID_NOTICIA, ID_USUARIO
        ) VALUES (
            SEQ_COMENTARIOS.NEXTVAL, p_contenido, SYSDATE, p_id_noticia, p_id_usuario
        );
        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END SP_INSERTAR_COMENTARIO;

    -- --------------------------------------------------------
    -- SP_LISTAR_COMENTARIOS_POR_NOTICIA
    -- Returns all comments for an article with author name.
    -- --------------------------------------------------------
    PROCEDURE SP_LISTAR_COMENTARIOS_POR_NOTICIA(
        p_id_noticia IN  NUMBER,
        o_cursor     OUT SYS_REFCURSOR
    ) AS
        CURSOR CUR_COMENTARIOS_POR_NOTICIA IS
            SELECT C.ID_COMENTARIO, C.CONTENIDO, C.FECHA_COMENTARIO,
                   U.NOMBRE || ' ' || U.APELLIDOS AS NOMBRE_AUTOR
              FROM COMENTARIOS C
              JOIN USUARIOS    U ON C.ID_USUARIO = U.ID_USUARIO
             WHERE C.ID_NOTICIA = p_id_noticia
             ORDER BY C.FECHA_COMENTARIO ASC;
    BEGIN
        OPEN o_cursor FOR
            SELECT C.ID_COMENTARIO, C.CONTENIDO, C.FECHA_COMENTARIO,
                   U.NOMBRE || ' ' || U.APELLIDOS AS NOMBRE_AUTOR
              FROM COMENTARIOS C
              JOIN USUARIOS    U ON C.ID_USUARIO = U.ID_USUARIO
             WHERE C.ID_NOTICIA = p_id_noticia
             ORDER BY C.FECHA_COMENTARIO ASC;
    END SP_LISTAR_COMENTARIOS_POR_NOTICIA;

    -- --------------------------------------------------------
    -- FN_CONTAR_COMENTARIOS_POR_NOTICIA
    -- Returns comment count for a given article.
    -- --------------------------------------------------------
    FUNCTION FN_CONTAR_COMENTARIOS_POR_NOTICIA(
        p_id_noticia NUMBER
    ) RETURN NUMBER AS
        v_total NUMBER;
        CURSOR CUR_CONTAR_COMENTARIOS IS
            SELECT COUNT(*) FROM COMENTARIOS WHERE ID_NOTICIA = p_id_noticia;
    BEGIN
        OPEN CUR_CONTAR_COMENTARIOS;
        FETCH CUR_CONTAR_COMENTARIOS INTO v_total;
        CLOSE CUR_CONTAR_COMENTARIOS;
        RETURN NVL(v_total, 0);
    END FN_CONTAR_COMENTARIOS_POR_NOTICIA;

END PKG_COMENTARIOS;
/
