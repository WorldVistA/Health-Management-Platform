import org.osehra.cpe.vista.rpc.RpcTemplate
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.JsonNode

RpcTemplate rpcTemplate = new RpcTemplate(); ;

String host = project.properties['kids.host']
String port = project.properties['kids.port']
String access = project.properties['kids.accessCode']
String verify = project.properties['kids.verifyCode']

String version = project.version
String pVersion = version.substring(version.indexOf('P'))

List filenames = []
filenames << project.properties['kids.file1.prefix'] + pVersion + project.properties['kids.file.suffix']
filenames << project.properties['kids.file2.prefix'] + pVersion + project.properties['kids.file.suffix']
filenames = filenames.collect { filename ->
    filename.replace(' ' as char, '_' as char).replace('-' as char, '_' as char)
}

project.properties['kids.finalNames'] = filenames.join(',') // all filenames in comma-separated list so wagon-maven-plugin can copy them via SSH

// individual files and paths so maven-build-helper can attach the individual files to the build and upload them to nexus
project.properties['kids.file1.finalName'] = filenames[0]
project.properties['kids.file1.finalPath'] = project.properties['kids.path'] + filenames[0]
project.properties['kids.file2.finalName'] = filenames[1]
project.properties['kids.file2.finalPath'] = project.properties['kids.path'] + filenames[1]

List buildNames = project.properties['kids.buildNames'].split(',')
List buildList = [];
	buildNames.eachWithIndex{ name, i ->
		Map map =[:]
		map.put('buildName', name)
		map.put('defaultFileName', filenames.get(i))
		buildList.add(map)
	}

Map params = ['build': buildList,
    'defaultPath': project.properties['kids.path'],
    hmpVersion: version
];
log.info("Parameters :" + params)
log.info("URL: vrpcb://${access};${verify}@${host}:${port}/VPR MAKE BUILD OPTION/VPR MAKE BUILD")
try {
    String jsonString = rpcTemplate.executeForString("vrpcb://${access};${verify}@${host}:${port}/VPR MAKE BUILD OPTION/VPR MAKE BUILD", params);
    JsonNode json = new ObjectMapper().readTree(jsonString)

    String message = json.get("text").asText()
    if (json.get("success").asBoolean()) {
        log.info("KIDS build created: ${message}")
    } else {
        fail("Unable to create KIDS build: ${message}")
    }
} catch (Throwable t) {
    fail("Unable to create KIDS build", t)
} finally {
    try {
        rpcTemplate.destroy();
    } catch (Exception e) {
        fail("Exception destroying RpcTemplate", e)
    }
}
