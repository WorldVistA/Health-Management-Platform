#!/usr/bin/env groovy
//@Grab('com.xlson.groovycsv:groovycsv:1.0')
//import static com.xlson.groovycsv.CsvParser.parseCsv

File basedir = new File(getClass().protectionDomain.codeSource.location.path).parentFile.parentFile

def json = new groovy.json.JsonSlurper().parse(new FileReader(new File(basedir, "src/main/resources/EXT/DOMAIN/cpe/team/team-positions.json")))

def transformedItems = []
json.data.items.eachWithIndex { it, i ->
    def transformedItem = [:]
    transformedItem.uid = "urn:va:team-position:${i + 1}"
    transformedItem.name = it.name
    transformedItem.description = it.description

    transformedItems << transformedItem
}

def builder = new groovy.json.JsonBuilder()
builder.data {
   totalItems(json.data.totalItems);
   items(transformedItems)
}
File foo = new File(basedir, "src/main/resources/EXT/DOMAIN/cpe/team/team-positions.json")
foo.write(builder.toPrettyString())

//def csv = parseCsv(
//        readFirstLine:true,
//        columnNames:['name'],
//        new FileReader(new File(basedir, "src/main/resources/EXT/DOMAIN/cpe/team/team-positions.csv")))
//
//def listItems = []
//csv.each { line ->
////    println it
//    def item = [:]
//    line.columns.keySet().each { field ->
//        item[field] = decapitalize(line[field])
////        println item[field]
//    }
//    listItems << item
//}
//def builder = new groovy.json.JsonBuilder()
//builder.data {
//    totalItems(listItems.size())
//    items(listItems)
//}
////builder.persons(persons)
//File foo = new File(basedir, "src/main/resources/EXT/DOMAIN/cpe/team/team-positions.json")
//foo.write(builder.toPrettyString())
//
//String decapitalize(String name) {
//    String[] words = name.split(" ");
//    for (int i = 0; i < words.size(); i++) {
//        if (words[i].startsWith("(")) continue;
//        words[i] = words[i].charAt(0).toUpperCase().toString() + words[i].substring(1).toLowerCase()
//    }
//    return words.join(" ")
//}
