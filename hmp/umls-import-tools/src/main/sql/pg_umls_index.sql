---------------------------------------------------------------------------------------
-- issue: different descriptions of a single code can get assigned to different CUI's
select *
from umls2012aa.mrconso
where sab='SNOMEDCT' and code='102579002'

----------------------------------------------------------------------------------------------
-- issue: 1 CUI can be mapped to many different codes across vocabulars and even to multiple 
-- codes within a single vocabulary. 
select * from umls2012aa.mrconso where cui='C0000167'
order by cui, sab, code

select * from umls2012aa.mrrel where cui1='C0000167'

-----------------------------------------------------------------------------------------------
-- issue: how do you know which AUI of a CUI contains all the relationships/mappings? 
-- your preferred description AUI does not necessarily hold all the mapping/relationships.
select c.*, r.rank,
       (select count(*) from umls2012aa.mrrel r where r.cui1=c.cui and r.aui1=c.aui) as aui_rela_count
from umls2012aa.mrconso c, umls2012aa.mrrank r
where c.sab=r.sab and c.tty=r.tty
      --and c.cui = 'C0000167'
      and c.cui in ('C0000962','C0000983')
order by cui, c.sab, code

-- example of the relationships that exist from the above query.
select * from umls2012aa.mrrel r, umls2012aa.mrdoc d
where  r.aui1 in ('A13012649','A3504546') and r.cui1='C0000167' and d.dockey='REL' and r.rel=d.value and d.type='expanded_form'
order by r.sab, r.aui1

-----------------------------------------------------------------------------------------
-- issue: it appears that multiple AUI's from a single SAB/CODE can have mappings.
-- does this imply that you can't tease the "concept" row out from the "description" rows?
select cui, sab, code, count(*)
from (
  select c.cui, c.sab, c.code, c.aui, count(*)
  from umls2012aa.mrconso c, umls2012aa.mrrel r
  where c.cui=r.cui1 and c.aui=r.aui1 and c.cui <= 'C0010167' --and c.sab='SNOMEDCT' 
        and r.rel in ('PAR','CHD')
  group by c.cui, c.sab, c.code, c.aui
  order by c.cui, c.sab, c.code
)
group by cui, sab, code
having count(*) > 1
order by cui, sab, code

-- although, it appears that (in this one case at least) that the primary parent/child relationships are under 1 AUI.
select c1.cui, c1.aui, c1.sab, c1.code, c1.str,
       r.*,
       c2.cui, c2.aui, c2.sab, c2.code, c2.str
--select *
from umls2012aa.mrconso c1, umls2012aa.mrrel r, umls2012aa.mrconso c2
where  c1.cui=r.cui1 and c1.aui=r.aui1 and c2.cui=r.cui2 and c2.aui=r.aui2 and 
       --c1.sab='CSP' and c1.code='2511-0411' 
       c1.sab='HL7V2.5' and c1.code='ACET' 

-- 12/30/2011: Issue: VANDF records are getting left out of umls_concept_index because they do not have parent/child_counts
select * from umls2012aa.mrconso where sab='VANDF' and code='4009245' -- two rows
select * from umls2012aa.umls_concept_index where sab='VANDF' and code='4009245' -- 0 rows
-- NDF meds likely don't have explicit relationships.  Look into only building RxNorm? And looking the VUID's up as attributes?

-- 1/4/2012: Issue: Found a circular reference somewhere in LOINC with sameas relationships
select * from umls2012aa.mrrel where aui1=aui2

----------------------------------------------------------------------------------
-- to generate the index, we are proceeding with generating a table for each uniqueue CUI/SAB/CODE combination
-- then the highest ranking description (and its AUI) are calculated for each row.

-- new Step 1a: create the concept index table
drop table if exists umls2012aa.umls_concept_index;
--delete from umls2012aa.umls_concept_index where sab='LNC'

CREATE TABLE umls2012aa.umls_concept_index
(
  cui character(8),
  sab character varying(20),
  code character varying(50),
  tty character varying(20),
  aui character varying(9) NOT NULL,
  pref_desc character varying(3000) NOT NULL,
  term_count integer,
  child_count integer,
  parent_count integer,
  sameas_count integer,
  related_count integer,
  decendant_count integer,
  ancestor_count integer,
  sibling_count integer,
  desc_count integer,
  inverse_level integer,
  ancestor_set character varying(9)[],
  parent_set character varying(9)[],
  sameas_set character varying(9)[],
  CONSTRAINT umls_concept_index_pk PRIMARY KEY (aui )
)
WITH (
  OIDS=FALSE
);
ALTER TABLE umls2012aa.umls_concept_index
  OWNER TO umls2012aa;

CREATE INDEX mrrank_idx1 ON umls2012aa.mrrank USING btree
  (sab COLLATE pg_catalog."default" , tty COLLATE pg_catalog."default" )
TABLESPACE umls2012aa;

-- new Step 1b: procedure to create inital table (invoke once per desired SAB)
create or replace function umls_build_index(p_sab varchar(8)) returns void as $$
declare
  v_total int := -1;
  v_counter int := 0;
  v_aui varchar(9);

  v_sab varchar(8);
  v_code varchar(50);
  v_terms int;
begin
  -- count all the records so we can display progress
  select count(distinct code) into v_total from umls2012aa.mrconso where sab=p_sab;
  raise notice 'Found % records for SAB=%', v_total, p_sab;

  -- for each distinct sab/code combo, grab the one with the highest tty rank
  for v_sab, v_code, v_terms in (select sab, code, count(*) from umls2012aa.mrconso where sab=p_sab group by sab, code) loop
    insert into umls2012aa.umls_concept_index
    select c.cui, c.sab, c.code, c.tty, c.aui, c.str as pref_desc, v_terms, 
         (select count(*) from umls2012aa.mrrel r where r.cui1=c.cui and r.aui1=c.aui and (r.rel='CHD' OR r.rela='isa')) as child_count,
         (select count(*) from umls2012aa.mrrel r where r.cui1=c.cui and r.aui1=c.aui and (r.rel='PAR' OR r.rela='inverse_isa')) as parent_count,
         (select count(*) from umls2012aa.mrrel r where r.cui1=c.cui and r.aui1=c.aui and r.rel='SY') as sameas_count,
         (select count(*) from umls2012aa.mrrel r where r.cui1=c.cui and r.aui1=c.aui and r.rel='RO') as related_count,
         --,(select count(*) from umls2012aa.mrrel r where r.cui1=c.cui and r.aui1=c.aui) as rel_count
         --,(select rank from umls2012aa.mrrank rank where rank.sab=c.sab and rank.tty=c.tty) as tty_rank    
        -1 as decendant_count, -1 as ancestor_count, -1 as sibling_count, -1 as desc_count, -1 as inverse_level
    from umls2012aa.mrconso c, umls2012aa.mrrank r
    where c.sab=r.sab and c.tty=r.tty and c.sab=v_sab and c.code=v_code
          and c.tty != 'MTH_LN' -- LOINC issue where PAR/CHD relationships are not under the highest ranking TTY
    order by r.rank desc limit 1;

    v_counter := v_counter + 1;
    if (v_counter % 1000 = 0) then
      raise notice 'Completed %/% rows.', v_counter, v_total;
    end if;
    
  end loop;
  raise notice 'Completed %/% rows.', v_counter, v_total;

end;
$$ language plpgsql;

-- build the indexes source by source
select umls_build_index('VANDF'); -- 31k records
select umls_build_index('NDFRT'); -- 47k records
select umls_build_index('LNC'); -- 180k records
select umls_build_index('SNOMEDCT'); -- 395k records
select umls_build_index('RXNORM'); -- 243k records
select umls_build_index('ICD9CM'); -- 22k records
select umls_build_index('CPT'); -- 9.7k records

select sab, count(*) from umls2012aa.umls_concept_index group by sab

-- ndf (parent's are the drug categories)
select * from umls2012aa.umls_concept_index where sab='VANDF' and code='4000634' -- "ATROPINE SO4 0.1MG/ML INJ"
select * from umls2012aa.mrsat where sab='VANDF' and code='4000634' limit 10 -- VANDF attributes for atropine
select * from umls2012aa.umls_concept_index where aui='A12100378' -- PARASYMPATHOLYTICS
select * from umls2012aa.mrrel where cui1='C2718123' and aui1 in ('A8440408')

-- LOINC eval
select * from umls2012aa.umls_concept_index where code='2284-8' -- "Folate:MCnc:Pt:Ser/Plas:Qn"="A18375516"
select * from umls2012aa.mrconso where code='2284-8' -- 4 rows "Folate:MCnc:Pt:Ser/Plas:Qn"
select * from umls2012aa.mrsat where metaui='A18105516'


select * from umls2012aa.umls_concept_index where code='LP14635-4'
select * from umls2012aa.mrconso where aui in ('A18105192','A18259419','A18876328')

select * from umls2012aa.mrrel where aui1='A18375516' and (rel='PAR' OR rela='inverse_isa')
select * from umls2012aa.mrconso where aui in ('A3866192','A18375516')

-- 1/3/2012: trying to find the ndf to rxnorm mappings
-- NDF has an attribute for the RXCUI and RXAUI (not sure what % of terms have it), but it seems more appropriate to 
-- find a synonymous relationship between the VUID and RXNORM(CUI/AUI).  NDFRT might have it, but I haven't tracked it down yet.

select * from umls2012aa.mrrel where cui1='C0354080' and aui1 in ('A18003227','A18003227') limit 10
select * from umls2012aa.mrsat where metaui='A8440408' limit 100 

select * from umls2012aa.mrconso where aui in ('A18003227','A10419692')
select metaui, count(*), (select str from umls2012aa.mrconso c where c.aui=metaui) as pref_desc from umls2012aa.mrsat where code='4009245' group by metaui
select * from umls2012aa.mrconso where sab='NDFRT' and str like 'ACETYLCHOLINE%'

select * from umls2012aa.mrrel where (aui2='A17880737' or aui1='A17880737') and rel='SY'

-- snomed eval
select * from umls2012aa.umls_concept_index where sab='SNOMEDCT' and code='25971005'
select * from umls2012aa.mrrel where cui1='C0270512' and aui1 in ('A2976415')

--------------------------------------------------

-- Step 1: create the table, seed it, and add all the columns to be calculated in step2
drop table if exists umls2012aa.umls_concept_index;

create table umls2012aa.umls_concept_index AS
  select cui, sab, code, tty, aui, pref_desc,
         child_count, parent_count, sameas_count, related_count,
         case when child_count = 0 then 0 else -1 end as decendant_count, -- this column will represent a (non-unique) decendant count
         -- TODO: is there a way to represent a uniqueue decendant count?
         -1 as ancestor_count, -1 as sibling_count, -1 as desc_count, -1 as inverse_level
  from (
         select c.cui, c.sab, c.code, c.tty, c.aui, c.str as pref_desc,
         (select count(*) from umls2012aa.mrrel r where r.cui1=c.cui and r.aui1=c.aui and r.rel='CHD') as child_count,
         (select count(*) from umls2012aa.mrrel r where r.cui1=c.cui and r.aui1=c.aui and r.rel='PAR') as parent_count,
         (select count(*) from umls2012aa.mrrel r where r.cui1=c.cui and r.aui1=c.aui and r.rel='SY') as sameas_count,
         (select count(*) from umls2012aa.mrrel r where r.cui1=c.cui and r.aui1=c.aui and r.rel='RO') as related_count
         --,(select count(*) from umls2012aa.mrrel r where r.cui1=c.cui and r.aui1=c.aui) as rel_count
         --,(select rank from umls2012aa.mrrank rank where rank.sab=c.sab and rank.tty=c.tty) as tty_rank
         from   umls2012aa.mrconso c
         where 
		c.sab='VANDF' AND c.code='4009245'
                --c.sab='SNOMEDCT'  --AND c.cui <= 'C0000167' -- shortcut for testing
                --and c.cui in ('C0000962','C0000983') -- some other interesting CUI's
  ) t1 
  where t1.child_count > 0 or t1.parent_count > 0 -- filters out mostly descriptions that do not have any relationship information.
  order by cui, sab, code, aui;
  -- took 320s to run on all of SNOMEDCT producing 378k rows total
  -- TODO: if this gets to large, maybe it could be chunked up to only do a million or so AUI's at a time?
  -- TODO: should lanugage (LAT) be added?

  -- took 10s to create these indexes.
  ALTER TABLE umls2012aa.umls_concept_index ALTER COLUMN pref_desc SET NOT NULL; 
  ALTER TABLE umls2012aa.umls_concept_index ADD CONSTRAINT umls_concept_index_pk PRIMARY KEY (aui);
  CREATE UNIQUE INDEX umls_concept_index_idx1 ON umls2012aa.umls_concept_index (code, sab, tty);
  CREATE INDEX umls_concept_index_idx2 ON umls2012aa.umls_concept_index (cui);

  -- this can be optionally run to reset all decendant_counts so you can rerun step 2 several times.
  -- update umls2012aa.umls_concept_index set decendant_count= cast(decode(child_count,0,0,-1) as int);

-- step 2: recursivley loop to calculate the decendant count.  
-- Start by calculateing leaf nodes parents values, then keep going until all nodes are calculated.
create or replace function umls_index_calc_decendants() returns void as $$
declare
  v_round integer := 0;
  v_round_search_count integer;
  v_round_update_count integer := -1;
  v_tmp integer;
  v_desc_sum integer;

  c1 umls2012aa.umls_concept_index%rowtype;
begin

 while (v_round_update_count != 0)
 loop
   v_round := v_round + 1;
   v_round_update_count := 0;
   v_round_search_count := 0;

 -- loop over nodes that have not been calculated yet.
 for c1 in (select * from umls2012aa.umls_concept_index where decendant_count = -1) loop
   v_round_search_count := v_round_search_count +1;
  
   -- determine how many children of the current node have not had their decendants calculated yet.
   select count(*) into v_tmp
   from   umls2012aa.mrrel r, umls2012aa.umls_concept_index i
   where  r.cui1=c1.cui and r.aui1=c1.aui and r.sab=c1.sab and r.rel='CHD' 
          and i.cui=r.cui2 and i.aui=r.aui2
          and i.decendant_count = -1;

   -- if no uncalculated children are found, then we can calculate this nodes decendants
   if (v_tmp = 0) then
     select count(*), sum(decendant_count) into v_tmp, v_desc_sum
     from   umls2012aa.mrrel r, umls2012aa.umls_concept_index i
     where  r.cui1=c1.cui and r.aui1=c1.aui and r.sab=c1.sab and r.rel='CHD' 
            and i.cui=r.cui2 and i.aui=r.aui2;

     -- update the index row
     update umls2012aa.umls_concept_index set decendant_count=(v_tmp+v_desc_sum), inverse_level=v_round
     where  cui=c1.cui and sab=c1.sab and aui=c1.aui;

     v_round_update_count = v_round_update_count + 1;
   end if;

   -- log progress every 1000 records
   if (v_round_search_count % 1000 = 0) then
     raise notice 'Round %: %/% records updated.', v_round, v_round_update_count, v_round_search_count;
   end if;
 end loop;
 raise notice 'Round %: %/% records updated.', v_round, v_round_update_count, v_round_search_count;

 end loop;

 -- TODO: determine how to calculate siblings and descriptions
end;   
$$ language plpgsql;


select umls_index_calc_decendants();

-- step 2b: calculate ancestor and parent sets
create or replace function umls_index_calc_sets(p_sab varchar(8), p_calc_parent_set int, p_calc_ancestor_set int, p_calc_sameas_set int) returns void as $$
declare
  v_ancestor_set varchar(9)[];
  v_parent_set varchar(9)[];
  v_sameas_set varchar(9)[];
  v_tmp_array varchar(9)[];
  v_rowcount int := 0;
  v_totalcount int;
  i int;

  v_aui varchar(9);
  v_cui char(8);
  v_ptr varchar(1000);
  v_aui2 varchar(9);

  -- TODO: Do I need to append the paui field to the ptr field in MRHIER?
  -- note, running this function took about 1120s over 378k records for calculating ancestor set and 2017s for calculating parent set.
begin
   select count(*) into v_totalcount from umls2012aa.umls_concept_index where sab=p_sab;

   <<umls_concept_index_row>>
   for v_aui, v_cui in (select aui, cui from umls2012aa.umls_concept_index where sab=p_sab)
   loop
     v_ancestor_set := null;
     v_parent_set := null;
     v_sameas_set := null;
     v_rowcount := v_rowcount + 1;

     -- calculate synonymous set
     if (p_calc_sameas_set = 1) then
       <<mrrel_row>>
       for v_aui2 in (select r.aui2 from umls2012aa.mrrel r where r.cui1=v_cui AND r.aui1=v_aui AND r.rel='SY')
       loop
         if (v_sameas_set is null) then
	   v_sameas_set = array[v_aui2];
         else
  	 v_sameas_set := v_sameas_set || v_aui2;
         end if;
       end loop mrrel_row;
       update umls2012aa.umls_concept_index set sameas_set=v_sameas_set where aui=v_aui;
     end if;

     -- calculate the ancestor set from MRHIER (if requested)
     if (p_calc_ancestor_set = 1) then
       <<mrhier_row>>
       for v_ptr in (select ptr from umls2012aa.mrhier where aui=v_aui)
       loop
         v_tmp_array := string_to_array(v_ptr, '.');

         -- shortcut: first row can just be copied straight across
         if (v_ancestor_set is null) then
           v_ancestor_set := v_tmp_array;
           continue;
         end if;

         <<array_loop>>
         for i in 1 .. array_upper(v_tmp_array,1)
         loop
           if (not (v_ancestor_set @> array[v_tmp_array[i]])) then
              v_ancestor_set := v_ancestor_set || v_tmp_array[i];
           end if;
         end loop array_loop;
       end loop mrhier_row;
       update umls2012aa.umls_concept_index set ancestor_set=v_ancestor_set where aui=v_aui;
     end if;


     -- calculate the parent set from MRREL (if specified)
     if (p_calc_parent_set = 1) then
       <<mrrel_row>>
       for v_aui2 in (select r.aui2 from umls2012aa.mrrel r where r.cui1=v_cui AND r.aui1=v_aui AND (r.rel='PAR' OR r.rela='inverse_isa'))
       loop
         if (v_parent_set is null) then
           v_parent_set = array[v_aui2];
         else
           v_parent_set := v_parent_set || v_aui2;
         end if;
       end loop mrrel_row;
       update umls2012aa.umls_concept_index set parent_set=v_parent_set where aui=v_aui;
     end if;
     
     if (v_rowcount % 1000 = 0) then
       raise notice 'Completed % of % records.', v_rowcount, v_totalcount;
     end if;
     
   end loop umls_concept_index_row;
end;
$$ language plpgsql;

-- create the ancestor_set and parent_set columns, populate them, then index them.
ALTER TABLE umls2012aa.umls_concept_index ADD COLUMN ancestor_set character varying(9)[];
ALTER TABLE umls2012aa.umls_concept_index ADD COLUMN parent_set character varying(9)[];
select umls_index_calc_sets('ICD9CM',1,0,1);
select umls_index_calc_sets('VANDF',1,0,1);
select umls_index_calc_sets('NDFRT',1,0,1);
select umls_index_calc_sets('RXNORM',1,0,1);
select umls_index_calc_sets('SNOMEDCT',1,0,1);
select umls_index_calc_sets('LNC',1,0,1);
select umls_index_calc_sets('CPT',1,0,1);


CREATE INDEX umls_concept_index_idx3 ON umls2012aa.umls_concept_index USING gin (ancestor_set) TABLESPACE umls2012aa;
CREATE INDEX umls_concept_index_idx4 ON umls2012aa.umls_concept_index USING gin (parent_set) TABLESPACE umls2012aa;


select * from umls2012aa.umls_concept_index where code='2345-7'

-- the get children query
select * from umls2012aa.umls_concept_index 
where parent_set @> array['A18322025']::varchar[]

-- the get parents query
select * from umls2012aa.umls_concept_index 
where array[aui]::varchar[] = ANY (select parent_set from umls2012aa.umls_concept_index where aui='A3819250')


-- this is essentially a get all decendants query!?!
select * from umls2012aa.umls_concept_index 
where ancestor_set @> array['A3819250']::varchar[]

-- this query will count all uniqueue decendants
-- returns the # of decendants for the root snomedct node
select count(*) from umls2012aa.umls_concept_index 
where ancestor_set @> array['A3684559']::varchar[]

-- this is the is_member_of_class query!!!
select 1 from umls2012aa.umls_concept_index
where  aui='A3819250' and 'A3684559' = any(ancestor_set)

-- this is the get children query
select r.aui2
from   umls2012aa.umls_concept_index i, umls2012aa.mrrel r
where  i.aui='A3684559' AND i.cui=r.cui1 AND i.aui=r.aui1 AND r.rel='CHD';

select * from umls2012aa.umls_concept_index where ancestor_set is null

-- this


select * from umls2012aa.umls_concept_index where aui='A3684559' or aui='A3819250' or aui='A3714377' o

where cui='C0000167'

-- step 3: verify expected results
select count(*) from umls2012aa.umls_concept_index where decendant_count = -1; -- should be zero!!!
select * from umls2012aa.umls_concept_index where inverse_level=18 -- this should be the root snomedCT node!!!

select * from umls2012aa.umls_concept_index where ancestor_set is null -- only the root snomedCT node should be null!!!


select * from umls2012aa.umls_concept_index  where pref_desc like '%Staphylococcus aureus%'

c.cui in ('C0000962','C0000983')

-----------------------------------------------------------------------------------------------------------------------
-- everything below was the first attempt at an index.....

    /* first query was below, which took 140s to run on 308k SNOMEDCT rows
    create table umls2012aa.umls_concept_index AS
    select c.cui, c.sab, c.code, max(r.rank) as max_rank, count(*) as desc_count,
           (select rr.tty from umls2012aa.mrrank rr where rr.sab=c.sab and rr.rank=max(r.rank)) as max_rank_tty,
           -1 as child_count, -1 as parent_count, -1 as decendant_count, -1 as ancestor_count, -1 as sibling_count
    from umls2012aa.mrconso c, umls2012aa.mrrank r
    where c.sab=r.sab and c.tty=r.tty and c.sab='SNOMEDCT'  --AND c.cui = 'C0000167'
          --and c.cui in ('C0000962','C0000983')
    group by c.cui, c.sab, c.code
    order by c.cui, c.sab, c.code;
   
   alter table umls2012aa.umls_concept_index ADD COLUMN aui CHARACTER VARYING (9);
   alter table umls2012aa.umls_concept_index ADD COLUMN pref_desc CHARACTER VARYING (3000);
   alter table umls2012aa.umls_concept_index ADD COLUMN inverse_level int;
   CREATE UNIQUE INDEX umls_concept_index_idx1 ON umls2012aa.umls_concept_index (code, sab, cui); 
   CREATE INDEX umls_concept_index_idx2 ON umls2012aa.umls_concept_index (cui);
    */

   -- need to create a better/faster index on MRREL
   -- took 411 seconds for 13M rows
   DROP INDEX umls2012aa.mrrel_idx1;
   CREATE INDEX mrrel_idx1 ON umls2012aa.mrrel (cui1, aui1);

   -- also need better indexes on MRCONSO
   -- took 240s for 3.7M rows
    DROP INDEX umls2012aa.mrconso_idx1;
    DROP INDEX umls2012aa.mrconso_idx2;
    CREATE INDEX mrconso_idx1 ON umls2012aa.mrconso (cui, aui);
    CREATE INDEX mrconso_idx2 ON umls2012aa.mrconso (code, sab);

-- step 2: first calculate the preferred description and the parent/child counts.
-- this ran in 322s over about 308K SNOMEDCT rows
-- another run took 773s over the same records (401k total rows)
declare
  v_aui varchar2(9);
  v_str varchar2(3000);
  v_parent_count int;
  v_child_count int;
  v_record_count int;
  v_update_count int := 0;

  v_calc_desc int := 0;
  v_calc_parents int := 0;
  v_calc_children int := 1;
  v_do_update int := 1;
begin

  select count(*) into v_record_count from umls2012aa.umls_concept_index;

  for c1 in (select * from umls2012aa.umls_concept_index) loop

    -- calculate perf desc/AUI for namespace
    if (v_calc_desc > 0) then
    /* slower
      select aui, str
      into   v_aui, v_str
      from   (select aui, str
              from   umls2012aa.mrconso c, umls2012aa.mrrank r
              where  c.cui=c1.cui and c.sab=c1.sab and c.code=c1.code and c.sab=r.sab and c.tty=r.tty
              order by r.rank DESC
             )
      where  rownum=1;
    */

      select aui, str
      into   v_aui, v_str
      from   umls2012aa.mrconso c
      where  c.cui=c1.cui and c.sab=c1.sab and c.code=c1.code and c.tty=c1.max_rank_tty and rownum=1;    
    else 
      v_aui := c1.aui;
      v_str := c1.pref_desc;    
    end if;
    
    -- calculate parents
    if (v_calc_parents > 0) then
      select count(*) into v_parent_count 
      from umls2012aa.mrrel r
      where  cui1=c1.cui and aui1=v_aui and sab=c1.sab and rela='inverse_isa';
    else
      v_parent_count := c1.parent_count;
    end if;

    -- calculate children
    if (v_calc_children > 0) then
      -- old, probably wrong
      --select count(*) into v_child_count 
      --from umls2012aa.mrrel r
      --where  cui1=c1.cui and aui1=v_aui and sab=c1.sab and rela='isa';  

      -- pretty slow still...
      -- if there is some situation where multiple AUI's of a single CUI/SAB/CODE key have parent/child mappings, then this should fail
      select count(1) into v_child_count
      from   umls2012aa.mrrel r, umls2012aa.mrconso c
      where  c.cui=r.cui1 and c.aui=r.aui1 and r.rel='CHD'
             and c.cui=c1.cui and c.sab=c1.sab and c.code=c1.code;
    else
      v_child_count := c1.child_count;
    end if;

    -- update the row (if child cound=0, then decendant count is also 0, otherwise decendant count is still unknown)
    if (v_do_update > 0) then
      update umls2012aa.umls_concept_index set aui=v_aui, pref_desc=v_str, 
             child_count=v_child_count, parent_count=v_parent_count, decendant_count= cast(decode(v_child_count,0,0,decendant_count) as int)
      where  cui=c1.cui and sab=c1.sab and code=c1.code;
    end if;

   -- log progress every 1000 records
   v_update_count := v_update_count + 1;
   if (v_update_count % 1000 = 0) then
     dbms_output.put_line(v_update_count || ' of ' || v_record_count || ' records updated.');     
   end if;    

  end loop;

end;	


-- apply the primary key and not null constraints
-- also verifies that all rows were processed and the fields filled in.
alter table umls2012aa.umls_concept_index ADD CONSTRAINT umls_concept_index_pk PRIMARY KEY (aui);
ALTER TABLE umls2012aa.umls_concept_index ALTER COLUMN pref_desc SET NOT NULL;

-- this will reset the decendant count fields (optional) so that you can rerun step 3 several times.
update umls2012aa.umls_concept_index set decendant_count= cast(decode(child_count,0,0,-1) as int);

-- step 3: recursivley loop to calculate the decendant count.  
-- Start by calculateing leaf nodes parents values, then keep going until all nodes are calculated.
declare
  v_round int := 0;
  v_round_search_count int;
  v_round_update_count int := -1;
  v_tmp int;
  v_desc_sum int;
begin

 while (v_round_update_count != 0)
 loop
   v_round := v_round + 1;
   v_round_update_count := 0;
   v_round_search_count := 0;

 -- loop over nodes that have not been calculated yet.
 for c1 in (select * from umls2012aa.umls_concept_index where decendant_count = -1) loop
   v_round_search_count := v_round_search_count +1;
  
   
   -- determine how many children of the current node have not had their decendants calculated yet.
   select count(*) into v_tmp
   from   umls2012aa.mrrel r, umls2012aa.umls_concept_index i
   where  r.cui1=c1.cui and r.aui1=i.aui and r.sab=c1.sab and r.rela='isa' and i.cui=r.cui2 and i.sab=c1.sab and decendant_count = -1;

   -- if no uncalculated children are found, then we can calculate this nodes decendants
   if (v_tmp = 0) then
     select count(*), sum(decendant_count) into v_tmp, v_desc_sum
     from   umls2012aa.mrrel r, umls2012aa.umls_concept_index i
     where  r.cui1=c1.cui and r.aui1=c1.aui and r.sab=c1.sab and r.rela='isa' and i.cui=r.cui2 and i.sab=c1.sab;

     -- update the index row
     update umls2012aa.umls_concept_index set decendant_count=(v_tmp+v_desc_sum), inverse_level=v_round
     where  cui=c1.cui and sab=c1.sab and aui=c1.aui;

     v_round_update_count = v_round_update_count + 1;
   end if;

   -- log progress every 1000 records
   if (v_round_search_count % 1000 = 0) then
     dbms_output.put_line('Round ' || v_round || ': ' || v_round_update_count || '/' || v_round_search_count || ' records updated.');     
   end if;
 end loop;

   dbms_output.put_line('Round ' || v_round || ': ' || v_round_update_count || '/' || v_round_search_count || ' records updated. COMPLETE!');     
 end loop;


end;
