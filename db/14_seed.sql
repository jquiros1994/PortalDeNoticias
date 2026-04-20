-- ============================================================
-- ProyectoFinal News Portal — Seed Data
-- Script: 15_seed.sql
-- AC9: admin@portal.com / admin123 / ADMIN / ACTIVO=1
-- ============================================================

-- 15a. ADMIN user
INSERT INTO USUARIOS (ID_USUARIO, NOMBRE, APELLIDOS, EMAIL, PASSWORD, ROL, ACTIVO)
VALUES (SEQ_USUARIOS.NEXTVAL, 'Administrador', 'Portal', 'admin@portal.com', 'admin123', 'ADMIN', 1);

-- 15b. Sample TEMAs
INSERT INTO TEMAS (ID_TEMA, NOMBRE, DESCRIPCION)
VALUES (SEQ_TEMAS.NEXTVAL, 'Tecnología', 'Noticias sobre avances tecnológicos y tendencias digitales.');

INSERT INTO TEMAS (ID_TEMA, NOMBRE, DESCRIPCION)
VALUES (SEQ_TEMAS.NEXTVAL, 'Ciencia', 'Descubrimientos científicos y novedades de investigación.');

INSERT INTO TEMAS (ID_TEMA, NOMBRE, DESCRIPCION)
VALUES (SEQ_TEMAS.NEXTVAL, 'Cultura', 'Arte, literatura, música y eventos culturales.');

-- 15c. Sample SUBTEMAs
INSERT INTO SUBTEMAS (ID_SUBTEMA, NOMBRE, ID_TEMA)
VALUES (SEQ_SUBTEMAS.NEXTVAL, 'Inteligencia Artificial', 1);

INSERT INTO SUBTEMAS (ID_SUBTEMA, NOMBRE, ID_TEMA)
VALUES (SEQ_SUBTEMAS.NEXTVAL, 'Ciberseguridad', 1);

INSERT INTO SUBTEMAS (ID_SUBTEMA, NOMBRE, ID_TEMA)
VALUES (SEQ_SUBTEMAS.NEXTVAL, 'Astronomía', 2);

INSERT INTO SUBTEMAS (ID_SUBTEMA, NOMBRE, ID_TEMA)
VALUES (SEQ_SUBTEMAS.NEXTVAL, 'Biología', 2);

INSERT INTO SUBTEMAS (ID_SUBTEMA, NOMBRE, ID_TEMA)
VALUES (SEQ_SUBTEMAS.NEXTVAL, 'Cine', 3);

INSERT INTO SUBTEMAS (ID_SUBTEMA, NOMBRE, ID_TEMA)
VALUES (SEQ_SUBTEMAS.NEXTVAL, 'Literatura', 3);

COMMIT;

-- ============================================================
-- Verification: confirm seed data inserted
-- SELECT * FROM USUARIOS WHERE ROL='ADMIN';
-- SELECT * FROM TEMAS;
-- SELECT * FROM SUBTEMAS;
-- ============================================================
