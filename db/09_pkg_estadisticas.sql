-- ============================================================
-- ProyectoFinal News Portal — PKG_ESTADISTICAS Body
-- Script: 09_pkg_estadisticas.sql
-- ============================================================

CREATE OR REPLACE PACKAGE BODY PKG_ESTADISTICAS AS

    -- --------------------------------------------------------
    -- SP_COMENTARIOS_POR_NOTICIA (FR27)
    -- Total comments per article.
    -- --------------------------------------------------------
    PROCEDURE SP_COMENTARIOS_POR_NOTICIA(
        o_cursor OUT SYS_REFCURSOR
    ) AS
        CURSOR CUR_COMENTARIOS_POR_NOTICIA_STATS IS
            SELECT N.ID_NOTICIA, N.TITULO, COUNT(C.ID_COMENTARIO) AS TOTAL_COMENTARIOS
              FROM NOTICIAS    N
              LEFT JOIN COMENTARIOS C ON N.ID_NOTICIA = C.ID_NOTICIA
             GROUP BY N.ID_NOTICIA, N.TITULO
             ORDER BY TOTAL_COMENTARIOS DESC;
    BEGIN
        OPEN o_cursor FOR
            SELECT N.ID_NOTICIA, N.TITULO, COUNT(C.ID_COMENTARIO) AS TOTAL_COMENTARIOS
              FROM NOTICIAS    N
              LEFT JOIN COMENTARIOS C ON N.ID_NOTICIA = C.ID_NOTICIA
             GROUP BY N.ID_NOTICIA, N.TITULO
             ORDER BY TOTAL_COMENTARIOS DESC;
    END SP_COMENTARIOS_POR_NOTICIA;

    -- --------------------------------------------------------
    -- SP_PROMEDIO_CALIFICACION_POR_NOTICIA (FR28)
    -- Average rating per article.
    -- --------------------------------------------------------
    PROCEDURE SP_PROMEDIO_CALIFICACION_POR_NOTICIA(
        o_cursor OUT SYS_REFCURSOR
    ) AS
        CURSOR CUR_PROMEDIO_POR_NOTICIA IS
            SELECT N.ID_NOTICIA, N.TITULO, N.PROMEDIO_CALIFICACION
              FROM NOTICIAS N
             WHERE N.ESTADO = 'PUBLICADO'
             ORDER BY N.PROMEDIO_CALIFICACION DESC;
    BEGIN
        OPEN o_cursor FOR
            SELECT N.ID_NOTICIA, N.TITULO, N.PROMEDIO_CALIFICACION
              FROM NOTICIAS N
             WHERE N.ESTADO = 'PUBLICADO'
             ORDER BY N.PROMEDIO_CALIFICACION DESC;
    END SP_PROMEDIO_CALIFICACION_POR_NOTICIA;

    -- --------------------------------------------------------
    -- SP_ARTICULOS_POR_AUTOR (FR29)
    -- Article count per author.
    -- --------------------------------------------------------
    PROCEDURE SP_ARTICULOS_POR_AUTOR(
        o_cursor OUT SYS_REFCURSOR
    ) AS
        CURSOR CUR_ARTICULOS_POR_AUTOR IS
            SELECT U.ID_USUARIO,
                   U.NOMBRE || ' ' || U.APELLIDOS AS NOMBRE_AUTOR,
                   COUNT(N.ID_NOTICIA) AS TOTAL_ARTICULOS
              FROM USUARIOS U
              LEFT JOIN NOTICIAS N ON U.ID_USUARIO = N.ID_AUTOR
             WHERE U.ROL = 'AUTOR'
             GROUP BY U.ID_USUARIO, U.NOMBRE, U.APELLIDOS
             ORDER BY TOTAL_ARTICULOS DESC;
    BEGIN
        OPEN o_cursor FOR
            SELECT U.ID_USUARIO,
                   U.NOMBRE || ' ' || U.APELLIDOS AS NOMBRE_AUTOR,
                   COUNT(N.ID_NOTICIA) AS TOTAL_ARTICULOS
              FROM USUARIOS U
              LEFT JOIN NOTICIAS N ON U.ID_USUARIO = N.ID_AUTOR
             WHERE U.ROL = 'AUTOR'
             GROUP BY U.ID_USUARIO, U.NOMBRE, U.APELLIDOS
             ORDER BY TOTAL_ARTICULOS DESC;
    END SP_ARTICULOS_POR_AUTOR;

    -- --------------------------------------------------------
    -- SP_TOP_NOTICIAS_CALIFICADAS (FR30)
    -- Articles ranked by average rating descending.
    -- --------------------------------------------------------
    PROCEDURE SP_TOP_NOTICIAS_CALIFICADAS(
        o_cursor OUT SYS_REFCURSOR
    ) AS
        CURSOR CUR_TOP_CALIFICADAS IS
            SELECT ROWNUM AS RANKING, ID_NOTICIA, TITULO, PROMEDIO_CALIFICACION
              FROM (
                SELECT N.ID_NOTICIA, N.TITULO, N.PROMEDIO_CALIFICACION
                  FROM NOTICIAS N
                 WHERE N.ESTADO = 'PUBLICADO'
                   AND N.PROMEDIO_CALIFICACION > 0
                 ORDER BY N.PROMEDIO_CALIFICACION DESC
              );
    BEGIN
        OPEN o_cursor FOR
            SELECT ROWNUM AS RANKING, ID_NOTICIA, TITULO, PROMEDIO_CALIFICACION
              FROM (
                SELECT N.ID_NOTICIA, N.TITULO, N.PROMEDIO_CALIFICACION
                  FROM NOTICIAS N
                 WHERE N.ESTADO = 'PUBLICADO'
                   AND N.PROMEDIO_CALIFICACION > 0
                 ORDER BY N.PROMEDIO_CALIFICACION DESC
              );
    END SP_TOP_NOTICIAS_CALIFICADAS;

    -- --------------------------------------------------------
    -- SP_TOP_NOTICIAS_COMENTADAS (FR31)
    -- Articles ranked by comment count descending.
    -- --------------------------------------------------------
    PROCEDURE SP_TOP_NOTICIAS_COMENTADAS(
        o_cursor OUT SYS_REFCURSOR
    ) AS
        CURSOR CUR_TOP_COMENTADAS IS
            SELECT ROWNUM AS RANKING, ID_NOTICIA, TITULO, TOTAL_COMENTARIOS
              FROM (
                SELECT N.ID_NOTICIA, N.TITULO, COUNT(C.ID_COMENTARIO) AS TOTAL_COMENTARIOS
                  FROM NOTICIAS    N
                  LEFT JOIN COMENTARIOS C ON N.ID_NOTICIA = C.ID_NOTICIA
                 GROUP BY N.ID_NOTICIA, N.TITULO
                 ORDER BY TOTAL_COMENTARIOS DESC
              );
    BEGIN
        OPEN o_cursor FOR
            SELECT ROWNUM AS RANKING, ID_NOTICIA, TITULO, TOTAL_COMENTARIOS
              FROM (
                SELECT N.ID_NOTICIA, N.TITULO, COUNT(C.ID_COMENTARIO) AS TOTAL_COMENTARIOS
                  FROM NOTICIAS    N
                  LEFT JOIN COMENTARIOS C ON N.ID_NOTICIA = C.ID_NOTICIA
                 GROUP BY N.ID_NOTICIA, N.TITULO
                 ORDER BY TOTAL_COMENTARIOS DESC
              );
    END SP_TOP_NOTICIAS_COMENTADAS;

    -- --------------------------------------------------------
    -- SP_ARTICULOS_POR_ESTADO (FR32)
    -- Article count grouped by ESTADO.
    -- --------------------------------------------------------
    PROCEDURE SP_ARTICULOS_POR_ESTADO(
        o_cursor OUT SYS_REFCURSOR
    ) AS
        CURSOR CUR_ARTICULOS_POR_ESTADO IS
            SELECT ESTADO, COUNT(*) AS TOTAL_ARTICULOS
              FROM NOTICIAS
             GROUP BY ESTADO
             ORDER BY ESTADO;
    BEGIN
        OPEN o_cursor FOR
            SELECT ESTADO, COUNT(*) AS TOTAL_ARTICULOS
              FROM NOTICIAS
             GROUP BY ESTADO
             ORDER BY ESTADO;
    END SP_ARTICULOS_POR_ESTADO;

    -- --------------------------------------------------------
    -- FN_PROMEDIO_GENERAL_PORTAL
    -- Returns the overall average rating across all published articles.
    -- --------------------------------------------------------
    FUNCTION FN_PROMEDIO_GENERAL_PORTAL RETURN NUMBER AS
        v_promedio NUMBER(3,2);
        CURSOR CUR_PROMEDIO_GENERAL IS
            SELECT NVL(AVG(PROMEDIO_CALIFICACION), 0)
              FROM NOTICIAS
             WHERE ESTADO = 'PUBLICADO'
               AND PROMEDIO_CALIFICACION > 0;
    BEGIN
        OPEN CUR_PROMEDIO_GENERAL;
        FETCH CUR_PROMEDIO_GENERAL INTO v_promedio;
        CLOSE CUR_PROMEDIO_GENERAL;
        RETURN ROUND(NVL(v_promedio, 0), 2);
    END FN_PROMEDIO_GENERAL_PORTAL;

END PKG_ESTADISTICAS;
/
