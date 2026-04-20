-- ============================================================
-- ProyectoFinal News Portal — PKG_UTILES Body
-- Script: 12_pkg_utiles.sql
-- ============================================================

CREATE OR REPLACE PACKAGE BODY PKG_UTILES AS

    -- --------------------------------------------------------
    -- FN_FORMATEAR_FECHA
    -- Returns date as 'DD/MM/YYYY' string for display.
    -- --------------------------------------------------------
    FUNCTION FN_FORMATEAR_FECHA(
        p_fecha DATE
    ) RETURN VARCHAR2 AS
    BEGIN
        IF p_fecha IS NULL THEN
            RETURN '';
        END IF;
        RETURN TO_CHAR(p_fecha, 'DD/MM/YYYY');
    END FN_FORMATEAR_FECHA;

    -- --------------------------------------------------------
    -- FN_TRUNCAR_TEXTO
    -- Truncates text to max length, appending '...' if cut.
    -- --------------------------------------------------------
    FUNCTION FN_TRUNCAR_TEXTO(
        p_texto   VARCHAR2,
        p_max_len NUMBER
    ) RETURN VARCHAR2 AS
    BEGIN
        IF p_texto IS NULL THEN
            RETURN '';
        END IF;
        IF LENGTH(p_texto) <= p_max_len THEN
            RETURN p_texto;
        END IF;
        RETURN SUBSTR(p_texto, 1, p_max_len - 3) || '...';
    END FN_TRUNCAR_TEXTO;

END PKG_UTILES;
/
