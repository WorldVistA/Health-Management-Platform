#!/usr/bin/env groovy
@Grab(group='net.sourceforge.expectj', module='expectj', version='2.0.7')
import expectj.ExpectJ
import expectj.Spawn

final String namespace = 'JSONVPR'

Spawn csession = new ExpectJ(5).spawn("csession VISTA -U ${namespace}")

csession.expect("${namespace}>")
csession.send('D ALL^VPRJT\n')

csession.expect("${namespace}>")
csession.send('H\n')

csession.stop()
