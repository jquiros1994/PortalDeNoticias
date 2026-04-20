-- ============================================================
-- ProyectoFinal News Portal — PKG_SUBTEMAS Body
-- Script: 06_pkg_subtemas.sql
-- ============================================================

CREATE OR REPLACE PACKAGE BODY PKG_SUBTEMAS AS

    -- --------------------------------------------------------
    -- SP_INSERTAR_SUBTEMA
    -- --------------------------------------------------------
    PROCEDURE SP_INSERTAR_SUBTEMA(
        p_nombre  IN VARCHAR2,
        p_id_tema IN NUMBER
    ) AS
    BEGIN
        INSERT INTO SUBTEMAS (ID_SUBTEMA, NOMBRE, ID_TEMA)
        VALUES (SEQ_SUBTEMAS.NEXTVAL, p_nombre, p_id_tema);
        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END SP_INSERTAR_SUBTEMA;

    -- --------------------------------------------------------
    -- SP_ACTUALIZAR_SUBTEMA
    -- --------------------------------------------------------
    PROCEDURE SP_ACTUALIZAR_SUBTEMA(
        p_id_subtema IN NUMBER,
        p_nombre     IN VARCHAR2,
        p_id_tema    IN NUMBER
    ) AS
    BEGIN
        UPDATE SUBTEMAS
           SET NOMBRE  = p_nombre,
               ID_TEMA = p_id_tema
         WHERE ID_SUBTEMA = p_id_subtema;

        IF SQL%ROWCOUNT = 0 THEN
            RAISE_APPLICATION_ERROR(-20030, 'Subtema no encontrado: ' || p_id_subtema);
        END IF;
        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END SP_ACTUALIZAR_SUBTEMA;

    -- --------------------------------------------------------
    -- SP_ELIMINAR_SUBTEMA
    -- --------------------------------------------------------
    PROCEDURE SP_ELIMINAR_SUBTEMA(
        p_id_subtema IN NUMBER
    ) AS
    BEGIN
        DELETE FROM SUBTEMAS WHERE ID_SUBTEMA = p_id_subtema;

        IF SQL%ROWCOUNT = 0 THEN
            RAISE_APPLICATION_ERROR(-20031, 'Subtema no encontrado: ' || p_id_subtema);
        END IF;
        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END SP_ELIMINAR_SUBTEMA;

    -- --------------------------------------------------------
    -- SP_LISTAR_SUBTEMAS
    -- Returns all subtemas with their parent tema name.
    -- --------------------------------------------------------
    PROCEDURE SP_LISTAR_SUBTEMAS(
        o_cursor OUT SYS_REFCURSOR
    ) AS
        CURSOR CUR_TODOS_SUBTEMAS IS
            SELECT S.ID_SUBTEMA, S.NOMBRE, S.ID_TEMA, T.NOMBRE AS NOMBRE_TEMA
              FROM SUBTEMAS S
              JOIN TEMAS    T ON S.ID_TEMA = T.ID_TEMA
             ORDER BY T.NOMBRE, S.NOMBRE;
    BEGIN
        OPEN o_cursor FOR
            SELECT S.ID_SUBTEMA, S.NOMBRE, S.ID_TEMA, T.NOMBRE AS NOMBRE_TEMA
              FROM SUBTEMAS S
              JOIN TEMAS    T ON S.ID_TEMA = T.ID_TEMA
             ORDER BY T.NOMBRE, S.NOMBRE;
    END SP_LISTAR_SUBTEMAS;

    -- --------------------------------------------------------
    -- FN_LISTAR_SUBTEMAS_POR_TEMA
    -- Returns subtemas for a given tema — used by dropdowns.
    -- --------------------------------------------------------
    FUNCTION FN_LISTAR_SUBTEMAS_POR_TEMA(
        p_id_tema NUMBER
    ) RETURN SYS_REFCURSOR AS
        o_cursor SYS_REFCURSOR;
        CURSOR CUR_SUBTEMAS_POR_TEMA IS
            SELECT ID_SUBTEMA, NOMBRE
              FROM SUBTEMAS
             WHERE ID_TEMA = p_id_tema
             ORDER BY NOMBRE;
    BEGIN
        OPEN o_cursor FOR
            SELECT ID_SUBTEMA, NOMBRE
              FROM SUBTEMAS
             WHERE ID_TEMA = p_id_tema
             ORDER BY NOMBRE;
        RETURN o_cursor;
    END FN_LISTAR_SUBTEMAS_POR_TEMA;

END PKG_SUBTEMAS;
/
