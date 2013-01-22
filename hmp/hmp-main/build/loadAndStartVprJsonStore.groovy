#!/usr/bin/env groovy

@Grab(group = 'net.sourceforge.expectj', module = 'expectj', version = '2.0.7')
import expectj.ExpectJ
import expectj.Spawn
import expectj.TimeoutException

final String namespace = args.size() >= 1 ? args[0] : 'JSONVPR'
final String jsonStoreListenPort = args.size() >= 2 ? args[1] : '9080'
final String jsonStoreRoutinePath = args.size() == 3 ? args[2] : null

if (!jsonStoreRoutinePath) {
    File scriptDir = new File(getClass().protectionDomain.codeSource.location.path).parentFile
    jsonStoreRoutinePath = new File(scriptDir, '../src/main/mumps/jds.ro').canonicalPath
}

File jsonStoreRoutines = new File(jsonStoreRoutinePath)
if (!jsonStoreRoutinePath.endsWith(".ro") || !jsonStoreRoutines.exists()) {
    System.err.println("Couldn't load JDS routines. '${jsonStoreRoutinePath}' is not an .ro or doesn't exist")
    System.exit(1)
} else {
    Spawn csession = new ExpectJ(5).spawn("csession VISTA -U ${namespace}")

    csession.expect("${namespace}>")
    csession.send('D ^%RI\n')

    csession.expect('Device:')
    csession.send(jsonStoreRoutinePath + '\n')

    csession.expect('=>')
    csession.send('\n')

    csession.expect('Routine Input Option:')
    csession.send('All Routines\n')

    3.times {
        csession.expect('=>')
        csession.send('Yes\n')
    }

    // setup
    csession.expect("${namespace}>")
    csession.send("D GO^VPRJRCL(${jsonStoreListenPort})\n")

    // reindex & rebuild templates for both VPR and non-patient data
    csession.expect("${namespace}>")
    csession.send('D FULLRBLD^VPRJ\n')

    csession.expect("${namespace}>", 300) // wait 5 minutes
    csession.send('H\n')

    csession.stop()
}
