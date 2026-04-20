-- ============================================================
-- ProyectoFinal News Portal — Triggers (5 required)
-- Script: 14_triggers.sql
-- ============================================================

-- ============================================================
-- TRG_ACTUALIZAR_PROMEDIO
-- AFTER INSERT OR UPDATE OR DELETE ON CALIFICACIONES
-- Recalculates and persists NOTICIAS.PROMEDIO_CALIFICACION.
-- AC: FR35, NFR10
-- ============================================================
CREATE OR REPLACE TRIGGER TRG_ACTUALIZAR_PROMEDIO
    FOR INSERT OR UPDATE OR DELETE ON CALIFICACIONES
    COMPOUND TRIGGER

    -- Collect affected NOTICIA IDs in the row section;
    -- query the table only in the statement section (no mutating table).
    TYPE t_id_list IS TABLE OF NUMBER INDEX BY PLS_INTEGER;
    v_ids   t_id_list;
    v_count PLS_INTEGER := 0;

    AFTER EACH ROW IS
    BEGIN
        v_count := v_count + 1;
        IF DELETING THEN
            v_ids(v_count) := :OLD.ID_NOTICIA;
        ELSE
            v_ids(v_count) := :NEW.ID_NOTICIA;
        END IF;
    END AFTER EACH ROW;

    AFTER STATEMENT IS
        v_promedio NUMBER(3,2);
    BEGIN
        FOR i IN 1 .. v_count LOOP
            SELECT NVL(ROUND(AVG(VALOR), 2), 0)
              INTO v_promedio
              FROM CALIFICACIONES
             WHERE ID_NOTICIA = v_ids(i);

            UPDATE NOTICIAS
               SET PROMEDIO_CALIFICACION = v_promedio
             WHERE ID_NOTICIA = v_ids(i);
        END LOOP;
    END AFTER STATEMENT;

END TRG_ACTUALIZAR_PROMEDIO;
/

-- ============================================================
-- TRG_FECHA_PUBLICACION
-- BEFORE UPDATE ON NOTICIAS
-- Sets FECHA_PUBLICACION when estado transitions to PUBLICADO.
-- ============================================================
CREATE OR REPLACE TRIGGER TRG_FECHA_PUBLICACION
    BEFORE UPDATE ON NOTICIAS
    FOR EACH ROW
BEGIN
    IF :NEW.ESTADO = 'PUBLICADO' AND
       (:OLD.ESTADO IS NULL OR :OLD.ESTADO != 'PUBLICADO') THEN
        :NEW.FECHA_PUBLICACION := SYSDATE;
    END IF;
END TRG_FECHA_PUBLICACION;
/

-- ============================================================
-- TRG_VALIDAR_ROL
-- BEFORE INSERT OR UPDATE ON USUARIOS
-- Enforces ROL IN ('LECTOR','AUTOR','ADMIN').
-- Note: Also enforced by CK_USUARIOS_ROL constraint — trigger
-- provides explicit error message.
-- ============================================================
CREATE OR REPLACE TRIGGER TRG_VALIDAR_ROL
    BEFORE INSERT OR UPDATE ON USUARIOS
    FOR EACH ROW
BEGIN
    IF :NEW.ROL NOT IN ('LECTOR', 'AUTOR', 'ADMIN') THEN
        RAISE_APPLICATION_ERROR(-20050,
            'Rol inválido: ' || :NEW.ROL || '. Valores permitidos: LECTOR, AUTOR, ADMIN.');
    END IF;
END TRG_VALIDAR_ROL;
/

-- ============================================================
-- TRG_VALIDAR_CALIFICACION
-- BEFORE INSERT OR UPDATE ON CALIFICACIONES
-- Enforces VALOR BETWEEN 1 AND 5.
-- Note: Also enforced by CK_CALIFICACIONES_VALOR constraint.
-- ============================================================
CREATE OR REPLACE TRIGGER TRG_VALIDAR_CALIFICACION
    BEFORE INSERT OR UPDATE ON CALIFICACIONES
    FOR EACH ROW
BEGIN
    IF :NEW.VALOR < 1 OR :NEW.VALOR > 5 THEN
        RAISE_APPLICATION_ERROR(-20051,
            'Calificación inválida: ' || :NEW.VALOR || '. Debe ser entre 1 y 5.');
    END IF;
END TRG_VALIDAR_CALIFICACION;
/

-- ============================================================
-- TRG_FECHA_CREACION
-- BEFORE INSERT ON NOTICIAS
-- Ensures FECHA_CREACION is always set to SYSDATE on insert.
-- ============================================================
CREATE OR REPLACE TRIGGER TRG_FECHA_CREACION
    BEFORE INSERT ON NOTICIAS
    FOR EACH ROW
BEGIN
    IF :NEW.FECHA_CREACION IS NULL THEN
        :NEW.FECHA_CREACION := SYSDATE;
    END IF;
END TRG_FECHA_CREACION;
/

-- Verify all triggers are ENABLED
-- SELECT TRIGGER_NAME, STATUS FROM USER_TRIGGERS ORDER BY TRIGGER_NAME;
