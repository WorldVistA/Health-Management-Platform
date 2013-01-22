#!/usr/bin/env groovy
@Grab('com.xlson.groovycsv:groovycsv:1.0')
import static com.xlson.groovycsv.CsvParser.parseCsv

File basedir = new File(getClass().protectionDomain.codeSource.location.path).parentFile.parentFile

def csv = parseCsv(
        readFirstLine:true,
        columnNames:['ien', 'providerType','providerTypeCode','classification','classificationCode','areaOfSpecialization','areaOfSpecializationCode','status','dateInactivated', "vaCode", "X12Code", "specialtyCode"],
        new FileReader(new File(basedir, "src/main/resources/EXT/DOMAIN/cpe/team/person-classes.csv")))

def itemList = []
csv.each { line ->
//    println it
    def item = [:]
    line.columns.keySet().each { field ->
        item[field] = line[field]
        if (item[field] == "") item[field] = null
    }
    item.uid = "urn:va:person-class:F484:${item.ien}" // old school UID here, probably should change
    itemList << item
}
def builder = new groovy.json.JsonBuilder()
builder.data {
    totalItems(itemList.size())
    items(itemList)
}
//builder.persons(persons)
File foo = new File(basedir, "src/main/js/EXT/DOMAIN/hmp/team/person-classes.json")
foo.write(builder.toPrettyString())
