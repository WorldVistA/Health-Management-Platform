CREATE OR REPLACE FUNCTION umls_is_member_of_class(p_aui1 IN VARCHAR, p_aui2 IN VARCHAR) RETURNS boolean
AS $proc$
DECLARE

BEGIN

END;
$proc$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION umls_get_ancestors(p_aui IN VARCHAR(9)) RETURNS SETOF VARCHAR(9)
AS $proc$
DECLARE
  r varchar(9);
  v_ancestor_list varchar(1000);
  i int;
BEGIN

   select ptr into v_ancestor_list from mrhier where rownum=1;

   i := position('.' in v_ancestor_list);
   

   
  

END;
$proc$ LANGUAGE plpgsql;


----------------------------------------

select * from umls_2008aa.umls_concept_index where sab='SNOMEDCT' and code='257728006'

select * from umls_2008aa.umls_concept_index where aui in ('A4893466','A10867638')

select * from umls_2008aa.mrhier where cui='C0446378' and sab='SNOMEDCT'





select foo from regexp_split_to_table('A3684559.A3713095.A3506985.A3659459', E'\\\.') as foo


select string_to_array('A3684559.A3713095.A3506985.A3659459','.')


SELECT foo FROM regexp_split_to_table('the quick brown fox jumped over the lazy dog', E'\\\s+') AS foo;


select regexp_replace('Thomas', '.[mN]a.', 'M')


CREATE OR REPLACE FUNCTION regexp_split_to_table(text, text, text)
  RETURNS SETOF text AS
'regexp_split_to_table'
  LANGUAGE 'internal' IMMUTABLE STRICT
  COST 1
  ROWS 1000;
ALTER FUNCTION regexp_split_to_table(text, text, text) OWNER TO postgres;
COMMENT ON FUNCTION regexp_split_to_table(text, text, text) IS 'split string by pattern';






