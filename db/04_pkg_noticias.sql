-- ============================================================
-- ProyectoFinal News Portal — PKG_NOTICIAS Body
-- Script: 04_pkg_noticias.sql
-- ============================================================

CREATE OR REPLACE PACKAGE BODY PKG_NOTICIAS AS

    -- --------------------------------------------------------
    -- SP_INSERTAR_NOTICIA
    -- Creates a new article in BORRADOR state.
    -- --------------------------------------------------------
    PROCEDURE SP_INSERTAR_NOTICIA(
        p_titulo     IN  VARCHAR2,
        p_cuerpo     IN  CLOB,
        p_id_autor   IN  NUMBER,
        p_id_tema    IN  NUMBER,
        p_id_subtema IN  NUMBER
    ) AS
    BEGIN
        INSERT INTO NOTICIAS (
            ID_NOTICIA, TITULO, CUERPO, ESTADO, FECHA_CREACION,
            PROMEDIO_CALIFICACION, ID_AUTOR, ID_TEMA, ID_SUBTEMA
        ) VALUES (
            SEQ_NOTICIAS.NEXTVAL, p_titulo, p_cuerpo, 'BORRADOR', SYSDATE,
            0, p_id_autor, p_id_tema, p_id_subtema
        );
        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END SP_INSERTAR_NOTICIA;

    -- --------------------------------------------------------
    -- SP_LISTAR_NOTICIAS_PUBLICADAS
    -- Returns paginated published articles with optional filters.
    -- p_id_tema=0 means no tema filter; p_id_subtema=0 means no subtema filter.
    -- --------------------------------------------------------
    PROCEDURE SP_LISTAR_NOTICIAS_PUBLICADAS(
        p_id_tema    IN  NUMBER,
        p_id_subtema IN  NUMBER,
        p_offset     IN  NUMBER,
        p_limite     IN  NUMBER,
        o_cursor     OUT SYS_REFCURSOR,
        o_total      OUT NUMBER
    ) AS
        CURSOR CUR_NOTICIAS_PUBLICADAS IS
            SELECT N.ID_NOTICIA, N.TITULO, N.FECHA_PUBLICACION, N.PROMEDIO_CALIFICACION,
                   T.NOMBRE AS NOMBRE_TEMA, S.NOMBRE AS NOMBRE_SUBTEMA,
                   U.NOMBRE || ' ' || U.APELLIDOS AS NOMBRE_AUTOR
              FROM NOTICIAS N
              JOIN TEMAS    T ON N.ID_TEMA    = T.ID_TEMA
              JOIN SUBTEMAS S ON N.ID_SUBTEMA = S.ID_SUBTEMA
              JOIN USUARIOS U ON N.ID_AUTOR   = U.ID_USUARIO
             WHERE N.ESTADO = 'PUBLICADO'
               AND (p_id_tema    = 0 OR N.ID_TEMA    = p_id_tema)
               AND (p_id_subtema = 0 OR N.ID_SUBTEMA = p_id_subtema)
             ORDER BY N.FECHA_PUBLICACION DESC;
    BEGIN
        -- Total count for pagination
        SELECT COUNT(*)
          INTO o_total
          FROM NOTICIAS N
         WHERE N.ESTADO = 'PUBLICADO'
           AND (p_id_tema    = 0 OR N.ID_TEMA    = p_id_tema)
           AND (p_id_subtema = 0 OR N.ID_SUBTEMA = p_id_subtema);

        -- Paginated result
        OPEN o_cursor FOR
            SELECT N.ID_NOTICIA, N.TITULO, N.FECHA_PUBLICACION, N.PROMEDIO_CALIFICACION,
                   T.NOMBRE AS NOMBRE_TEMA, S.NOMBRE AS NOMBRE_SUBTEMA,
                   U.NOMBRE || ' ' || U.APELLIDOS AS NOMBRE_AUTOR
              FROM NOTICIAS N
              JOIN TEMAS    T ON N.ID_TEMA    = T.ID_TEMA
              JOIN SUBTEMAS S ON N.ID_SUBTEMA = S.ID_SUBTEMA
              JOIN USUARIOS U ON N.ID_AUTOR   = U.ID_USUARIO
             WHERE N.ESTADO = 'PUBLICADO'
               AND (p_id_tema    = 0 OR N.ID_TEMA    = p_id_tema)
               AND (p_id_subtema = 0 OR N.ID_SUBTEMA = p_id_subtema)
             ORDER BY N.FECHA_PUBLICACION DESC
            OFFSET p_offset ROWS FETCH NEXT p_limite ROWS ONLY;
    END SP_LISTAR_NOTICIAS_PUBLICADAS;

    -- --------------------------------------------------------
    -- SP_LISTAR_NOTICIAS_POR_AUTOR
    -- Returns all articles belonging to a specific author.
    -- --------------------------------------------------------
    PROCEDURE SP_LISTAR_NOTICIAS_POR_AUTOR(
        p_id_autor IN  NUMBER,
        o_cursor   OUT SYS_REFCURSOR
    ) AS
        CURSOR CUR_NOTICIAS_POR_AUTOR IS
            SELECT N.ID_NOTICIA, N.TITULO, N.ESTADO, N.FECHA_CREACION, N.FECHA_PUBLICACION,
                   N.PROMEDIO_CALIFICACION, T.NOMBRE AS NOMBRE_TEMA, S.NOMBRE AS NOMBRE_SUBTEMA
              FROM NOTICIAS N
              JOIN TEMAS    T ON N.ID_TEMA    = T.ID_TEMA
              JOIN SUBTEMAS S ON N.ID_SUBTEMA = S.ID_SUBTEMA
             WHERE N.ID_AUTOR = p_id_autor
             ORDER BY N.FECHA_CREACION DESC;
    BEGIN
        OPEN o_cursor FOR
            SELECT N.ID_NOTICIA, N.TITULO, N.ESTADO, N.FECHA_CREACION, N.FECHA_PUBLICACION,
                   N.PROMEDIO_CALIFICACION, T.NOMBRE AS NOMBRE_TEMA, S.NOMBRE AS NOMBRE_SUBTEMA
              FROM NOTICIAS N
              JOIN TEMAS    T ON N.ID_TEMA    = T.ID_TEMA
              JOIN SUBTEMAS S ON N.ID_SUBTEMA = S.ID_SUBTEMA
             WHERE N.ID_AUTOR = p_id_autor
             ORDER BY N.FECHA_CREACION DESC;
    END SP_LISTAR_NOTICIAS_POR_AUTOR;

    -- --------------------------------------------------------
    -- SP_OBTENER_NOTICIA_POR_ID
    -- Returns full article data including CLOB cuerpo.
    -- --------------------------------------------------------
    PROCEDURE SP_OBTENER_NOTICIA_POR_ID(
        p_id_noticia IN  NUMBER,
        o_cursor     OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN o_cursor FOR
            SELECT N.ID_NOTICIA, N.TITULO, N.CUERPO, N.ESTADO,
                   N.FECHA_CREACION, N.FECHA_PUBLICACION, N.PROMEDIO_CALIFICACION,
                   N.ID_AUTOR, N.ID_TEMA, N.ID_SUBTEMA,
                   T.NOMBRE AS NOMBRE_TEMA, S.NOMBRE AS NOMBRE_SUBTEMA,
                   U.NOMBRE || ' ' || U.APELLIDOS AS NOMBRE_AUTOR
              FROM NOTICIAS N
              JOIN TEMAS    T ON N.ID_TEMA    = T.ID_TEMA
              JOIN SUBTEMAS S ON N.ID_SUBTEMA = S.ID_SUBTEMA
              JOIN USUARIOS U ON N.ID_AUTOR   = U.ID_USUARIO
             WHERE N.ID_NOTICIA = p_id_noticia;
    END SP_OBTENER_NOTICIA_POR_ID;

    -- --------------------------------------------------------
    -- SP_ACTUALIZAR_NOTICIA
    -- Updates título, cuerpo, tema, subtema of an article.
    -- --------------------------------------------------------
    PROCEDURE SP_ACTUALIZAR_NOTICIA(
        p_id_noticia IN NUMBER,
        p_titulo     IN VARCHAR2,
        p_cuerpo     IN CLOB,
        p_id_tema    IN NUMBER,
        p_id_subtema IN NUMBER
    ) AS
    BEGIN
        UPDATE NOTICIAS
           SET TITULO     = p_titulo,
               CUERPO     = p_cuerpo,
               ID_TEMA    = p_id_tema,
               ID_SUBTEMA = p_id_subtema
         WHERE ID_NOTICIA = p_id_noticia;

        IF SQL%ROWCOUNT = 0 THEN
            RAISE_APPLICATION_ERROR(-20010, 'Noticia no encontrada: ' || p_id_noticia);
        END IF;
        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END SP_ACTUALIZAR_NOTICIA;

    -- --------------------------------------------------------
    -- SP_ELIMINAR_NOTICIA
    -- Deletes an article and its associated comments and ratings.
    -- --------------------------------------------------------
    PROCEDURE SP_ELIMINAR_NOTICIA(
        p_id_noticia IN NUMBER
    ) AS
    BEGIN
        DELETE FROM CALIFICACIONES WHERE ID_NOTICIA = p_id_noticia;
        DELETE FROM COMENTARIOS    WHERE ID_NOTICIA = p_id_noticia;
        DELETE FROM NOTICIAS       WHERE ID_NOTICIA = p_id_noticia;

        IF SQL%ROWCOUNT = 0 THEN
            RAISE_APPLICATION_ERROR(-20011, 'Noticia no encontrada: ' || p_id_noticia);
        END IF;
        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END SP_ELIMINAR_NOTICIA;

    -- --------------------------------------------------------
    -- SP_CAMBIAR_ESTADO_NOTICIA
    -- Changes article estado (BORRADOR→PUBLICADO→ARCHIVADO).
    -- --------------------------------------------------------
    PROCEDURE SP_CAMBIAR_ESTADO_NOTICIA(
        p_id_noticia IN NUMBER,
        p_estado     IN VARCHAR2
    ) AS
    BEGIN
        UPDATE NOTICIAS
           SET ESTADO = p_estado
         WHERE ID_NOTICIA = p_id_noticia;

        IF SQL%ROWCOUNT = 0 THEN
            RAISE_APPLICATION_ERROR(-20012, 'Noticia no encontrada: ' || p_id_noticia);
        END IF;
        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END SP_CAMBIAR_ESTADO_NOTICIA;

    -- --------------------------------------------------------
    -- SP_LISTAR_TODAS_NOTICIAS
    -- Returns all articles (all estados, all authors) for ADMIN.
    -- --------------------------------------------------------
    PROCEDURE SP_LISTAR_TODAS_NOTICIAS(
        o_cursor OUT SYS_REFCURSOR
    ) AS
        CURSOR CUR_TODAS_NOTICIAS IS
            SELECT N.ID_NOTICIA, N.TITULO, N.ESTADO, N.FECHA_CREACION, N.FECHA_PUBLICACION,
                   T.NOMBRE AS NOMBRE_TEMA, S.NOMBRE AS NOMBRE_SUBTEMA,
                   U.NOMBRE || ' ' || U.APELLIDOS AS NOMBRE_AUTOR,
                   N.ID_AUTOR
              FROM NOTICIAS N
              JOIN TEMAS    T ON N.ID_TEMA    = T.ID_TEMA
              JOIN SUBTEMAS S ON N.ID_SUBTEMA = S.ID_SUBTEMA
              JOIN USUARIOS U ON N.ID_AUTOR   = U.ID_USUARIO
             ORDER BY N.FECHA_CREACION DESC;
    BEGIN
        OPEN o_cursor FOR
            SELECT N.ID_NOTICIA, N.TITULO, N.ESTADO, N.FECHA_CREACION, N.FECHA_PUBLICACION,
                   T.NOMBRE AS NOMBRE_TEMA, S.NOMBRE AS NOMBRE_SUBTEMA,
                   U.NOMBRE || ' ' || U.APELLIDOS AS NOMBRE_AUTOR,
                   N.ID_AUTOR
              FROM NOTICIAS N
              JOIN TEMAS    T ON N.ID_TEMA    = T.ID_TEMA
              JOIN SUBTEMAS S ON N.ID_SUBTEMA = S.ID_SUBTEMA
              JOIN USUARIOS U ON N.ID_AUTOR   = U.ID_USUARIO
             ORDER BY N.FECHA_CREACION DESC;
    END SP_LISTAR_TODAS_NOTICIAS;

    -- --------------------------------------------------------
    -- FN_VERIFICAR_PROPIETARIO_NOTICIA
    -- Returns 1 if the article belongs to the author, 0 otherwise.
    -- --------------------------------------------------------
    FUNCTION FN_VERIFICAR_PROPIETARIO_NOTICIA(
        p_id_noticia NUMBER,
        p_id_autor   NUMBER
    ) RETURN NUMBER AS
        v_count NUMBER;
        CURSOR CUR_VERIFICAR_PROPIETARIO IS
            SELECT COUNT(*)
              FROM NOTICIAS
             WHERE ID_NOTICIA = p_id_noticia
               AND ID_AUTOR   = p_id_autor;
    BEGIN
        OPEN CUR_VERIFICAR_PROPIETARIO;
        FETCH CUR_VERIFICAR_PROPIETARIO INTO v_count;
        CLOSE CUR_VERIFICAR_PROPIETARIO;
        RETURN v_count;
    END FN_VERIFICAR_PROPIETARIO_NOTICIA;

    -- --------------------------------------------------------
    -- FN_CONTAR_NOTICIAS_PUBLICADAS
    -- Returns total published article count for pagination.
    -- --------------------------------------------------------
    FUNCTION FN_CONTAR_NOTICIAS_PUBLICADAS(
        p_id_tema    NUMBER,
        p_id_subtema NUMBER
    ) RETURN NUMBER AS
        v_total NUMBER;
        CURSOR CUR_CONTAR_PUBLICADAS IS
            SELECT COUNT(*)
              FROM NOTICIAS
             WHERE ESTADO = 'PUBLICADO'
               AND (p_id_tema    = 0 OR ID_TEMA    = p_id_tema)
               AND (p_id_subtema = 0 OR ID_SUBTEMA = p_id_subtema);
    BEGIN
        OPEN CUR_CONTAR_PUBLICADAS;
        FETCH CUR_CONTAR_PUBLICADAS INTO v_total;
        CLOSE CUR_CONTAR_PUBLICADAS;
        RETURN NVL(v_total, 0);
    END FN_CONTAR_NOTICIAS_PUBLICADAS;

    -- --------------------------------------------------------
    -- FN_CONTAR_NOTICIAS_POR_AUTOR
    -- Returns total article count for a given author.
    -- --------------------------------------------------------
    FUNCTION FN_CONTAR_NOTICIAS_POR_AUTOR(
        p_id_autor NUMBER
    ) RETURN NUMBER AS
        v_total NUMBER;
        CURSOR CUR_CONTAR_POR_AUTOR IS
            SELECT COUNT(*) FROM NOTICIAS WHERE ID_AUTOR = p_id_autor;
    BEGIN
        OPEN CUR_CONTAR_POR_AUTOR;
        FETCH CUR_CONTAR_POR_AUTOR INTO v_total;
        CLOSE CUR_CONTAR_POR_AUTOR;
        RETURN NVL(v_total, 0);
    END FN_CONTAR_NOTICIAS_POR_AUTOR;

END PKG_NOTICIAS;
/
