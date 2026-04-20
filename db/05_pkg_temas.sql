-- ============================================================
-- ProyectoFinal News Portal — PKG_TEMAS Body
-- Script: 05_pkg_temas.sql
-- ============================================================

CREATE OR REPLACE PACKAGE BODY PKG_TEMAS AS

    -- --------------------------------------------------------
    -- SP_INSERTAR_TEMA
    -- --------------------------------------------------------
    PROCEDURE SP_INSERTAR_TEMA(
        p_nombre      IN VARCHAR2,
        p_descripcion IN VARCHAR2
    ) AS
    BEGIN
        INSERT INTO TEMAS (ID_TEMA, NOMBRE, DESCRIPCION)
        VALUES (SEQ_TEMAS.NEXTVAL, p_nombre, p_descripcion);
        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END SP_INSERTAR_TEMA;

    -- --------------------------------------------------------
    -- SP_ACTUALIZAR_TEMA
    -- --------------------------------------------------------
    PROCEDURE SP_ACTUALIZAR_TEMA(
        p_id_tema     IN NUMBER,
        p_nombre      IN VARCHAR2,
        p_descripcion IN VARCHAR2
    ) AS
    BEGIN
        UPDATE TEMAS
           SET NOMBRE      = p_nombre,
               DESCRIPCION = p_descripcion
         WHERE ID_TEMA = p_id_tema;

        IF SQL%ROWCOUNT = 0 THEN
            RAISE_APPLICATION_ERROR(-20020, 'Tema no encontrado: ' || p_id_tema);
        END IF;
        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END SP_ACTUALIZAR_TEMA;

    -- --------------------------------------------------------
    -- SP_ELIMINAR_TEMA
    -- --------------------------------------------------------
    PROCEDURE SP_ELIMINAR_TEMA(
        p_id_tema IN NUMBER
    ) AS
    BEGIN
        DELETE FROM TEMAS WHERE ID_TEMA = p_id_tema;

        IF SQL%ROWCOUNT = 0 THEN
            RAISE_APPLICATION_ERROR(-20021, 'Tema no encontrado: ' || p_id_tema);
        END IF;
        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END SP_ELIMINAR_TEMA;

    -- --------------------------------------------------------
    -- SP_LISTAR_TEMAS
    -- --------------------------------------------------------
    PROCEDURE SP_LISTAR_TEMAS(
        o_cursor OUT SYS_REFCURSOR
    ) AS
        CURSOR CUR_TODOS_TEMAS IS
            SELECT ID_TEMA, NOMBRE, DESCRIPCION
              FROM TEMAS
             ORDER BY NOMBRE;
    BEGIN
        OPEN o_cursor FOR
            SELECT ID_TEMA, NOMBRE, DESCRIPCION
              FROM TEMAS
             ORDER BY NOMBRE;
    END SP_LISTAR_TEMAS;

    -- --------------------------------------------------------
    -- FN_LISTAR_TEMAS
    -- Returns a SYS_REFCURSOR — used by dropdowns (no OUT param).
    -- --------------------------------------------------------
    FUNCTION FN_LISTAR_TEMAS RETURN SYS_REFCURSOR AS
        o_cursor SYS_REFCURSOR;
        CURSOR CUR_TEMAS_DROPDOWN IS
            SELECT ID_TEMA, NOMBRE FROM TEMAS ORDER BY NOMBRE;
    BEGIN
        OPEN o_cursor FOR
            SELECT ID_TEMA, NOMBRE FROM TEMAS ORDER BY NOMBRE;
        RETURN o_cursor;
    END FN_LISTAR_TEMAS;

END PKG_TEMAS;
/
