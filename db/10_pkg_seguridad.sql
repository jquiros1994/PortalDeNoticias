-- ============================================================
-- ProyectoFinal News Portal — PKG_SEGURIDAD Body
-- Script: 10_pkg_seguridad.sql
-- ============================================================

CREATE OR REPLACE PACKAGE BODY PKG_SEGURIDAD AS

    -- --------------------------------------------------------
    -- SP_AUTENTICAR_USUARIO
    -- Returns a SYS_REFCURSOR with user data if credentials are valid
    -- and ACTIVO=1. Returns empty cursor on any failure.
    -- Java: check rs.next() — true=success, false=invalid/inactive.
    -- --------------------------------------------------------
    PROCEDURE SP_AUTENTICAR_USUARIO(
        p_email    IN  VARCHAR2,
        p_password IN  VARCHAR2,
        o_cursor   OUT SYS_REFCURSOR
    ) AS
        CURSOR CUR_AUTENTICAR IS
            SELECT ID_USUARIO, NOMBRE, APELLIDOS, EMAIL, ROL, ACTIVO
              FROM USUARIOS
             WHERE EMAIL    = p_email
               AND PASSWORD = p_password;
        v_activo    NUMBER;
        v_id        NUMBER;
        v_nombre    VARCHAR2(100);
        v_apellidos VARCHAR2(100);
        v_email_out VARCHAR2(150);
        v_rol       VARCHAR2(10);
    BEGIN
        -- First check if user exists with those credentials
        OPEN CUR_AUTENTICAR;
        FETCH CUR_AUTENTICAR INTO v_id, v_nombre, v_apellidos, v_email_out, v_rol, v_activo;
        CLOSE CUR_AUTENTICAR;

        IF v_id IS NULL THEN
            -- No matching user — return empty cursor (wrong credentials)
            OPEN o_cursor FOR
                SELECT ID_USUARIO, NOMBRE, APELLIDOS, EMAIL, ROL, ACTIVO
                  FROM USUARIOS
                 WHERE 1 = 0;
        ELSIF v_activo = 0 THEN
            -- User exists but is deactivated — return cursor with ACTIVO=0 signal
            OPEN o_cursor FOR
                SELECT ID_USUARIO, NOMBRE, APELLIDOS, EMAIL, ROL, ACTIVO
                  FROM USUARIOS
                 WHERE ID_USUARIO = v_id;
        ELSE
            -- Valid, active user
            OPEN o_cursor FOR
                SELECT ID_USUARIO, NOMBRE, APELLIDOS, EMAIL, ROL, ACTIVO
                  FROM USUARIOS
                 WHERE ID_USUARIO = v_id;
        END IF;
    END SP_AUTENTICAR_USUARIO;

END PKG_SEGURIDAD;
/
