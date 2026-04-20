-- ============================================================
-- ProyectoFinal News Portal — PKG_USUARIOS Body
-- Script: 03_pkg_usuarios.sql
-- ============================================================

CREATE OR REPLACE PACKAGE BODY PKG_USUARIOS AS

    -- --------------------------------------------------------
    -- SP_INSERTAR_USUARIO
    -- Inserts a new user with the specified role and active flag.
    -- --------------------------------------------------------
    PROCEDURE SP_INSERTAR_USUARIO(
        p_nombre    IN  VARCHAR2,
        p_apellidos IN  VARCHAR2,
        p_email     IN  VARCHAR2,
        p_password  IN  VARCHAR2,
        p_rol       IN  VARCHAR2,
        p_activo    IN  NUMBER
    ) AS
        CURSOR CUR_VERIFICAR_EMAIL IS
            SELECT COUNT(*) AS TOTAL
              FROM USUARIOS
             WHERE EMAIL = p_email;
        v_count NUMBER;
    BEGIN
        OPEN CUR_VERIFICAR_EMAIL;
        FETCH CUR_VERIFICAR_EMAIL INTO v_count;
        CLOSE CUR_VERIFICAR_EMAIL;

        IF v_count > 0 THEN
            RAISE_APPLICATION_ERROR(-20001, 'El email ya está registrado en el sistema.');
        END IF;

        INSERT INTO USUARIOS (
            ID_USUARIO, NOMBRE, APELLIDOS, EMAIL, PASSWORD, ROL, ACTIVO
        ) VALUES (
            SEQ_USUARIOS.NEXTVAL, p_nombre, p_apellidos, p_email, p_password, p_rol, p_activo
        );
        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END SP_INSERTAR_USUARIO;

    -- --------------------------------------------------------
    -- SP_LISTAR_USUARIOS
    -- Returns all users ordered by apellidos, nombre.
    -- --------------------------------------------------------
    PROCEDURE SP_LISTAR_USUARIOS(
        o_cursor OUT SYS_REFCURSOR
    ) AS
        CURSOR CUR_TODOS_USUARIOS IS
            SELECT ID_USUARIO, NOMBRE, APELLIDOS, EMAIL, ROL, ACTIVO
              FROM USUARIOS
             ORDER BY APELLIDOS, NOMBRE;
    BEGIN
        OPEN o_cursor FOR
            SELECT ID_USUARIO, NOMBRE, APELLIDOS, EMAIL, ROL, ACTIVO
              FROM USUARIOS
             ORDER BY APELLIDOS, NOMBRE;
    END SP_LISTAR_USUARIOS;

    -- --------------------------------------------------------
    -- SP_ACTUALIZAR_ACTIVO_USUARIO
    -- Activates or deactivates a user account.
    -- --------------------------------------------------------
    PROCEDURE SP_ACTUALIZAR_ACTIVO_USUARIO(
        p_id_usuario IN NUMBER,
        p_activo     IN NUMBER
    ) AS
    BEGIN
        UPDATE USUARIOS
           SET ACTIVO = p_activo
         WHERE ID_USUARIO = p_id_usuario;

        IF SQL%ROWCOUNT = 0 THEN
            RAISE_APPLICATION_ERROR(-20002, 'Usuario no encontrado: ' || p_id_usuario);
        END IF;
        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END SP_ACTUALIZAR_ACTIVO_USUARIO;

    -- --------------------------------------------------------
    -- FN_EXISTE_EMAIL
    -- Returns 1 if email already registered, 0 otherwise.
    -- --------------------------------------------------------
    FUNCTION FN_EXISTE_EMAIL(
        p_email VARCHAR2
    ) RETURN NUMBER AS
        v_count NUMBER;
        CURSOR CUR_EXISTE_EMAIL IS
            SELECT COUNT(*) FROM USUARIOS WHERE EMAIL = p_email;
    BEGIN
        OPEN CUR_EXISTE_EMAIL;
        FETCH CUR_EXISTE_EMAIL INTO v_count;
        CLOSE CUR_EXISTE_EMAIL;
        RETURN CASE WHEN v_count > 0 THEN 1 ELSE 0 END;
    END FN_EXISTE_EMAIL;

    -- --------------------------------------------------------
    -- FN_CONTAR_USUARIOS
    -- Returns total number of users in the system.
    -- --------------------------------------------------------
    FUNCTION FN_CONTAR_USUARIOS RETURN NUMBER AS
        v_total NUMBER;
        CURSOR CUR_CONTAR_USUARIOS IS
            SELECT COUNT(*) FROM USUARIOS;
    BEGIN
        OPEN CUR_CONTAR_USUARIOS;
        FETCH CUR_CONTAR_USUARIOS INTO v_total;
        CLOSE CUR_CONTAR_USUARIOS;
        RETURN NVL(v_total, 0);
    END FN_CONTAR_USUARIOS;

END PKG_USUARIOS;
/
