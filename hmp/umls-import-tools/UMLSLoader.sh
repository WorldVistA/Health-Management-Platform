#!/bin/bash 
java -cp lib/postgresql-9.1-901.jdbc4.jar:target/classes:bin UMLSLoader jdbc:postgresql://localhost:5434/postgres umls2012ab umls2012ab umls2012ab /path/to/MRRANK.RRF /path/to/MRCONSO.RRF /path/to/MRREL.RRF /path/to/MRSAT.RRF 
exit 0
