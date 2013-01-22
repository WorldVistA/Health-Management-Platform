select sab, code, count(distinct cui) 
from umls2007ac.mrconso
where code='3092008' or code='49436004' or code='11241001'
group by sab, code
having count(distinct cui) > 1

select * from umls2007ac.mrconso
where code='3092008'

select * from umls2007ac.mrrel
where cui1='C0038172'

select * 
from umls2007ac.mrconso a, umls2007ac.mrrank b
where cui in ('C0196252','C1306560','C1306561')
and a.sab=b.sab and a.tty=b.tty and b.sab='SNOMEDCT'
order by code

select * from umls2007ac.mrrank where sab='SNOMEDCT'

order by code

-- step 1: create the table with uniqueue CUI/SAB pairs
drop table if exists umls_2008AA.umls_concept_index;
create table umls_2008AA.umls_concept_index as
select cui, sab, count(*) as description_count
from umls_2008aa.mrconso
group by cui, sab

-- step 2: create the index
CREATE INDEX umls_concept_index_pk ON umls_2008aa.umls_concept_index (cui,sab) TABLESPACE umls2007ac;

-- step 3: for each CUI/SAB pair, calculate the preferred desc (and its AUI)
ALTER TABLE umls_2008aa.umls_concept_index ADD COLUMN aui character varying(9);
ALTER TABLE umls_2008aa.umls_concept_index ADD COLUMN pref_desc character varying(3000);



select c.cui, c.sab, count(*) as description_count

select a.*, c.*
from (
  select c.cui, c.sab, max(r.rank) as max_rank, count(*) as desc_count,
         (select rr.tty from umls_2008aa.mrrank rr where rr.sab=c.sab and rr.rank=max(r.rank)) as max_rank_tty
  from umls_2008aa.mrconso c, umls_2008aa.mrrank r
  where c.cui < 'C0000172' and c.sab=r.sab and c.tty=r.tty 
  group by c.cui, c.sab
  order by c.cui, c.sab
) a, umls_2008aa.mrconso c
where c.cui=a.cui and c.sab=a.sab and c.tty=a.max_rank_tty
group by c.cui, c.sab,

select * from umls_2008aa.mrconso where cui='C0000039'

update umls_2008aa.umls_concept_index
set aui, pref_desc

select * 
from umls_2008aa.umls_concept_index c, umls_2008aa.mrrank r
where c.sab=r.sab


select count(distinct cui) from umls2007ac.mrconso

select * from umls_2008aa.mrconso where code='3092008'


select rel.*, src.str as srcstr, target.str as targetstr from umls_2008aa.mrrel rel, umls_2008aa.mrconso src, umls_2008aa.mrconso target 
where cui1='C0038172' and rela='isa' and rel.sab='SNOMEDCT' and src.cui=cui1 and target.cui=cui2 
and src.ts='P' and src.stt='PF' and src.ispref='Y'
and target.ts='P' and target.stt='PF' and target.ispref='Y'

select * from umls_2008aa.mrsab order by tfr desc

select *
from umls_2008aa.mrconso sc
where sc.cui='C0038172' and ts='P' and stt='PF' and ispref='Y'

select * from umls_2008aa.mrrel rel, umls_2008aa.mrconso sc where sc.cui='C0038172' and rela='isa' and sc.cui=rel.cui1 and ts='P' and stt='PF' and ispref='Y' and sc.aui=rel.aui1
order by code

		SELECT distinct r.cui1
		FROM   umls_2008aa.mrrel r, umls_2008aa.mrconso
		WHERE  r.cui2='C0038172' AND r.rela='isa' and aui1=aui --AND r.sab='SNOMEDCT'

                select * from umls_2008aa.mrconso where cui='C0314732' and sab='SNOMEDCT'

		select * from umls_2008aa.mrhier
		where cui='C0038172' and sab='SNOMEDCT'

select * from umls_2008aa.mrconso where aui in ('A3684559','A6921673','A3034111','A7873175','A6917107','A3008685','A3026730','A3107358','A2886832','A3718278')

select 1
from   umls_2008aa.mrconso tc, umls_2008aa.mrhier h
where  h.cui='C0038172' and h.sab='SNOMEDCT' and 
       tc.cui='C0314732' and tc.ts='P' and tc.stt='PF' and tc.sab=h.sab and position(tc.aui in h.ptr) > 0
limit 1       
       

select *
from   umls_2008aa.mrconso tc
where  tc.cui='C0314732' and ts='P' and stt='PF' and sab='SNOMEDCT'

-- Step 1: create the table, seed it, and add all the columns to be calculated
  delete from umls_2008aa.umls_concept_index;
  drop table if exists umls_2008aa.umls_concept_index;
  create table umls_2008aa.umls_concept_index AS
    select c.cui, c.sab, max(r.rank) as max_rank, count(*) as desc_count,
           (select rr.tty from umls_2008aa.mrrank rr where rr.sab=c.sab and rr.rank=max(r.rank)) as max_rank_tty,
           -1 as child_count, -1 as parent_count, -1 as decendant_count, -1 as ancestor_count
    from umls_2008aa.mrconso c, umls_2008aa.mrrank r
    where c.sab=r.sab and c.tty=r.tty and c.sab='SNOMEDCT' --and c.cui = 'C0038172'
    group by c.cui, c.sab
    order by c.cui, c.sab;
   -- took 140s to run on 308k SNOMEDCT rows.
   
   alter table umls_2008aa.umls_concept_index ADD CONSTRAINT hs_concept_index_pk PRIMARY KEY (cui, sab);
   alter table umls_2008aa.umls_concept_index ADD COLUMN aui CHARACTER VARYING (9);
   alter table umls_2008aa.umls_concept_index ADD COLUMN source_code CHARACTER VARYING (50);
   alter table umls_2008aa.umls_concept_index ADD COLUMN pref_desc CHARACTER VARYING (3000);
   alter table umls_2008aa.umls_concept_index ADD COLUMN inverse_level int;


-- step 2: first calculate the preferred description and the parent/child counts.
-- this ran in 322s over about 308K SNOMEDCT rows
declare
  v_aui varchar2(9);
  v_str varchar2(3000);
  v_source_code varchar2(50);
  v_parent_count int;
  v_child_count int;
begin

  for c1 in (select * from umls_2008aa.umls_concept_index) loop

    -- calculate perf desc/AUI for namespace
    select aui, str, code
    into   v_aui, v_str, v_source_code
    from   (select aui, str, code
            from   umls_2008aa.mrconso c
            where  c.cui=c1.cui and c.sab=c1.sab and c.tty=c1.max_rank_tty
            order by upper(c.ts), c.stt, c.ispref DESC
           )
    where  rownum=1;

    -- calculate parents
    select count(*) into v_parent_count 
    from umls_2008aa.mrrel r
    where  cui1=c1.cui and sab=c1.sab and rela='inverse_isa';

    -- calculate children
    select count(*) into v_child_count 
    from umls_2008aa.mrrel r
    where  cui1=c1.cui and sab=c1.sab and rela='isa';  

    -- update the row (if child cound=0, then decendant count is also 0, otherwise decendant count is still unknown)
    update umls_2008aa.umls_concept_index set aui=v_aui, pref_desc=v_str, source_code=v_source_code,
           child_count=v_child_count, parent_count=v_parent_count, decendant_count= cast(decode(v_child_count,0,0,decendant_count) as int)
    where  cui=c1.cui and sab=c1.sab;

  end loop;

end;		

-- this will reset the decendant count fields (optional)
update umls_2008aa.umls_concept_index set decendant_count= cast(decode(child_count,0,0,-1) as int);

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
 for c1 in (select * from umls_2008aa.umls_concept_index where decendant_count = -1) loop
   v_round_search_count := v_round_search_count +1;
  
   
   -- determine how many children of the current node have not had their decendants calculated yet.
   select count(*) into v_tmp
   from   umls_2008aa.mrrel r, umls_2008aa.umls_concept_index i
   where  r.cui1=c1.cui and r.sab=c1.sab and r.rela='isa' and i.cui=r.cui2 and i.sab=c1.sab and decendant_count = -1;

   -- if no uncalculated children are found, then we can calculate this nodes decendants
   if (v_tmp = 0) then
     select count(*), sum(decendant_count) into v_tmp, v_desc_sum
     from   umls_2008aa.mrrel r, umls_2008aa.umls_concept_index i
     where  r.cui1=c1.cui and r.sab=c1.sab and r.rela='isa' and i.cui=r.cui2 and i.sab=c1.sab;

     -- update the index row
     update umls_2008aa.umls_concept_index set decendant_count=(v_tmp+v_desc_sum), inverse_level=v_round
     where  cui=c1.cui and sab=c1.sab;

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


select * from umls_2008aa.umls_concept_index where decendant_count = -1 order by decendant_count desc


"C0001973" == alchohol intoxication

select * from umls_2008aa.umls_concept_index where cui='C0001973'

select * from umls_2008aa.mrrel r, umls_2008aa.mrconso c, umls_2008aa.umls_concept_index i
where r.cui1='C0001973' and r.sab='SNOMEDCT' and r.rela='isa' and r.aui2=c.aui and i.cui=r.cui2
order by r.cui2


select * from umls_2008aa.mrconso where cui='C0001973' and sab='SNOMEDCT' order by code


select sab, code, count(*) 
from umls_2008aa.mrconso
group by sab, code


select * 
from umls_2008aa.mrhier --where aui='A2886833'
--where sab='SNOMEDCT' and rela='isa' and cui='C0038172'
start with paui='A2886833'
connect by prior paui=aui

start with cui1='C0038172'
connect by prior cui2=cui1

    select * from umls_2008aa.mrrel where cui1='C0038172'
