-- ============================================================
-- ProyectoFinal News Portal — PKG_CALIFICACIONES Body
-- Script: 08_pkg_calificaciones.sql
-- ============================================================

CREATE OR REPLACE PACKAGE BODY PKG_CALIFICACIONES AS

    -- --------------------------------------------------------
    -- SP_INSERTAR_CALIFICACION
    -- Inserts a rating. UQ_CALIF_USUARIO_NOTICIA enforces one per user.
    -- TRG_ACTUALIZAR_PROMEDIO fires automatically after INSERT.
    -- --------------------------------------------------------
    PROCEDURE SP_INSERTAR_CALIFICACION(
        p_id_noticia IN NUMBER,
        p_id_usuario IN NUMBER,
        p_valor      IN NUMBER
    ) AS
    BEGIN
        INSERT INTO CALIFICACIONES (
            ID_CALIFICACION, VALOR, FECHA_CALIFICACION, ID_NOTICIA, ID_USUARIO
        ) VALUES (
            SEQ_CALIFICACIONES.NEXTVAL, p_valor, SYSDATE, p_id_noticia, p_id_usuario
        );
        COMMIT;
    EXCEPTION
        WHEN DUP_VAL_ON_INDEX THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20040, 'El usuario ya ha calificado este artículo.');
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END SP_INSERTAR_CALIFICACION;

    -- --------------------------------------------------------
    -- FN_OBTENER_CALIFICACION_USUARIO
    -- Returns the valor (1-5) a user gave to an article, or 0 if none.
    -- Used to disable rating widget for already-rated articles.
    -- --------------------------------------------------------
    FUNCTION FN_OBTENER_CALIFICACION_USUARIO(
        p_id_noticia NUMBER,
        p_id_usuario NUMBER
    ) RETURN NUMBER AS
        v_valor NUMBER := 0;
        CURSOR CUR_CALIFICACION_USUARIO IS
            SELECT VALOR
              FROM CALIFICACIONES
             WHERE ID_NOTICIA = p_id_noticia
               AND ID_USUARIO = p_id_usuario;
    BEGIN
        OPEN CUR_CALIFICACION_USUARIO;
        FETCH CUR_CALIFICACION_USUARIO INTO v_valor;
        CLOSE CUR_CALIFICACION_USUARIO;
        RETURN NVL(v_valor, 0);
    END FN_OBTENER_CALIFICACION_USUARIO;

    -- --------------------------------------------------------
    -- FN_TIENE_CALIFICACION
    -- Returns 1 if user has already rated the article, 0 otherwise.
    -- Used by Thymeleaf to disable/hide rating widget.
    -- --------------------------------------------------------
    FUNCTION FN_TIENE_CALIFICACION(
        p_id_noticia NUMBER,
        p_id_usuario NUMBER
    ) RETURN NUMBER AS
        v_count NUMBER;
        CURSOR CUR_TIENE_CALIFICACION IS
            SELECT COUNT(*)
              FROM CALIFICACIONES
             WHERE ID_NOTICIA = p_id_noticia
               AND ID_USUARIO = p_id_usuario;
    BEGIN
        OPEN CUR_TIENE_CALIFICACION;
        FETCH CUR_TIENE_CALIFICACION INTO v_count;
        CLOSE CUR_TIENE_CALIFICACION;
        RETURN CASE WHEN v_count > 0 THEN 1 ELSE 0 END;
    END FN_TIENE_CALIFICACION;

END PKG_CALIFICACIONES;
/
