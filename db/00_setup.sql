-- ============================================================
-- ProyectoFinal News Portal — Instance Setup
-- Script: 00_setup.sql
-- Run as: SYSDBA
-- ============================================================

-- 1. Tablespace
CREATE TABLESPACE TS_PORTAL
DATAFILE 'C:\ORACLEINSTALLATION\ORADATA\ORCL\TS_PORTAL.DBF'
SIZE 50M
AUTOEXTEND ON NEXT 5M MAXSIZE 200M;

-- 2. User
CREATE USER USR_NOTICIAS
IDENTIFIED BY "Noticias192026$"
DEFAULT TABLESPACE TS_PORTAL
TEMPORARY TABLESPACE TEMP
QUOTA UNLIMITED ON TS_PORTAL;

-- 3. Grants (explicit only — no redundant roles)
GRANT CREATE SESSION   TO USR_NOTICIAS;  -- login
GRANT CREATE TABLE     TO USR_NOTICIAS;  -- tables + indexes
GRANT CREATE VIEW      TO USR_NOTICIAS;  -- views
GRANT CREATE PROCEDURE TO USR_NOTICIAS;  -- stored procs + functions + packages
GRANT CREATE TRIGGER   TO USR_NOTICIAS;  -- triggers
GRANT CREATE SEQUENCE  TO USR_NOTICIAS;  -- sequences
GRANT CREATE TYPE      TO USR_NOTICIAS;  -- object types

-- CONNECT and RESOURCE roles removed:
--   CONNECT   → redundant, CREATE SESSION above already covers login
--   RESOURCE  → implicitly grants UNLIMITED TABLESPACE system-wide,
--               which overrides our intentional QUOTA on TS_PORTAL

COMMIT;
