#!/usr/bin/env groovy
@Grab('com.xlson.groovycsv:groovycsv:1.0')
import static com.xlson.groovycsv.CsvParser.parseCsv

File basedir = new File(getClass().protectionDomain.codeSource.location.path).parentFile.parentFile

def csv = parseCsv(
        readFirstLine:true,
        columnNames:['ien', 'name','sex','dob','title','ssn','userClass','delegateOf','service','providerClass','providerType','surrogate'],
        new FileReader(new File(basedir, "src/main/resources/EXT/DOMAIN/cpe/team/persons.csv")))

def persons = []
csv.each { line ->
//    println it
    def person = [:]
    line.columns.keySet().each { field ->
        person[field] = line[field]
    }
    person.uid = "urn:va:user:F484:${person.ien}" // old school UID here, probably should change
    persons << person
}
def builder = new groovy.json.JsonBuilder()
builder.data {
    totalItems(persons.size())
    items(persons)
}
//builder.persons(persons)
File foo = new File(basedir, "src/main/resources/EXT/DOMAIN/cpe/team/persons.json")
foo.write(builder.toPrettyString())
