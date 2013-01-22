#!/usr/bin/env groovy
@Grab('com.xlson.groovycsv:groovycsv:1.0')
import static com.xlson.groovycsv.CsvParser.parseCsv

File basedir = new File(getClass().protectionDomain.codeSource.location.path).parentFile.parentFile

def csv = parseCsv(
        readFirstLine:true,
        columnNames:['name', 'abbreviation','status','displayName','personClass','subclass','okToDistribute'],
        new FileReader(new File(basedir, "src/main/resources/EXT/DOMAIN/cpe/team/user-classes.csv")))

def itemMap = [:]
csv.eachWithIndex { line, i ->
//    println it
    def item = [:]
    line.columns.keySet().each { field ->
        item[field] = line[field]
    }
    item.uid = "urn:va:userClass:F484:${i + 1}"

    item.personClass = (item.personClass == '' ? null : item.personClass)
    item.okToDistribute = (item.okToDistribute == 'YES' ? true : false)
    String subclass = (item.subclass == '' ? null : item.subclass)

    if (!itemMap.containsKey(item.name)) {
        itemMap.put(item.name, item)
        item.subclasses = []
        item.subclassNames = []
    } else {
        item = itemMap[item.name]
    }
    if (subclass) {
        item.subclassNames << subclass
    } else {
        item.remove('subclass')
    }
}

itemMap.values().each { item ->
   item.subclassNames.each { subclassName ->
       def subclass = itemMap[subclassName]
       item.subclasses << subclass
       subclass.parent = item
   }
    // clean up subclassNames
    item.remove('subclassNames')
    if (!item.subclasses) item.remove("subclasses")
}

// top level of tree is all nodes who have null parent
def itemTree = []
itemMap.values().each { item ->
    if (!item.parent) {
        itemTree << item
    } else {
        item.remove("parent")
    }
}

def builder = new groovy.json.JsonBuilder()
builder.data {
    totalItems(itemTree.size())
    items(itemTree)
}
File foo = new File(basedir, "src/main/resources/EXT/DOMAIN/cpe/team/user-class-hierarchy.json")
foo.write(builder.toPrettyString())
