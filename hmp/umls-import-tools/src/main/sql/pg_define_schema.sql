-- defines the UMLS schema after the tables have been loaded in.
-- loads the appropriate functions/procedures

\set UNAME UMLS2007AC

CREATE ROLE :UNAME;
CREATE SCHEMA :UNAME AUTHORIZATION :UNAME;
GRANT USAGE ON SCHEMA :UNAME TO public;

CREATE TABLESPACE :UNAME OWNER :UNAME LOCATION E'\\postgresplus\\data\\:UNAME' ;

SET ROLE :UNAME;
SET default_tablespace=:UNAME;

CREATE TABLE version ( umls_version character varying )
ALTER TABLE version OWNER TO :UNAME;
INSERT INTO version VALUES ('V_' || user);
COMMIT;


-- this is the meta data index for CUI's.
CREATE TABLE hs_umls_cui_cache (
  cui char(8) NOT NULL,
  aui varchar(9) NOT NULL,
  pref_desc varchar(255)
  
);
ALTER TABLE hs_umls_cui_cache ADD CONSTRAINT hs_umls_cui_cache_pk PRIMARY KEY (cui);
ALTER TABLE hs_umls_cui_cache OWNER TO :UNAME;


CREATE TABLE hs_umls_rel_cache (
  cui1 char(8) NOT NULL,
  cui2 char(8) NOT NULL,
  type char(8) NOT NULL,
  result boolean NOT NULL
);
ALTER TABLE hs_umls_rel_cache ADD CONSTRAINT hs_umls_rel_cache_pk PRIMARY KEY (cui1,cui2,type,result);
ALTER TABLE hs_umls_rel_cache OWNER TO :UNAME;


CREATE OR REPLACE FUNCTION calc_perf_aui
RETURN VARCHAR
AS $$
  ret varchar;
BEGIN
  SELECT aui INTO ret FROM umls2007AC.MRCONSO
  WHERE cui = 'C0001175' AND ts='P' and stt='PF' and ispref='Y'
  ORDER BY cui, lat, lower(ts), lui, stt
  LIMIT 1;

  RETURN ret;
END;
$$

CREATE OR REPLACE FUNCTION calc_perf_desc 
RETURN VARCHAR
AS $$
DECLARE
  ret varchar;
BEGIN
  SELECT str INTO ret FROM umls2007AC.MRCONSO
  WHERE cui = 'C0001175' AND ts='P' and stt='PF' and ispref='Y'
  ORDER BY cui, lat, lower(ts), lui, stt
  LIMIT 1;

  RETURN ret;
END;
$$


CREATE OR REPLACE FUNCTION getversion 
RETURN VARCHAR
AS $$
DECLARE
  RET varchar;
BEGIN
  SELECT umls_version INTO ret FROM version;

  RETURN ret;
END;
$$
 

