<%@ page import="org.osehra.cpe.vpr.Patient" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <g:render template="/layouts/viewport"/>

  <title>VPR Jump Page: ${patientInstance.familyName}, ${patientInstance.givenNames}</title>
  <link rel="alternate" type="application/atom+xml" title="Full Patient Feed" href="${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/feed')}" />
  <link rel="alternate" type="application/atom+xml" title="Encounters" href="${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/encounter/feed')}" />
  <link rel="alternate" type="application/atom+xml" title="Documents" href="${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/document/feed')}" />
  <link rel="alternate" type="application/atom+xml" title="Lab Results" href="${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/lab/feed')}" />
     <script type="text/javascript">
        Ext.onReady(function() {
           var appbar = Ext.ComponentQuery.query('appbar')[0];
           appbar.addAppMenuItem( {
                text: 'Patients',
                iconCls: 'listIcon',
                href: '${createLink(action:"list")}',
                params:'',
                target:'_self'
            });
            appbar.addAppMenuItem(   {
                text: 'Sync',
                iconCls: 'listIcon',
                href: '${createLink(controller: "sync")}',
                params:'',
                target:'_self'
            });
        });
    </script>
</head>
<body>
<div id="center">
  <h1>VPR Jump Page: ${patientInstance.familyName}, ${patientInstance.givenNames}</h1>
  <g:if test="syncErrors">
    <h3>Sync Errors</h3>
    <p class="resource"><g:link controller="patient" action="syncErrors"></g:link></p>
  </g:if>
  <h3>Patient Demographics</h3>
  <p class="resource"><a href='${createLink(uri: '/vpr/v1/' + patientInstance.icn)}'>${createLink(uri: '/vpr/v1/' + patientInstance.icn)}</a></p>
  <p class="resource"><a href='${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/feed')}'>${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/feed')}</a></p>
  <h3>Allergies</h3>
  <p class="resource">/vpr/v1/${patientInstance.icn}/allergy/show/{uid}</p>
  <p class="resource"><a href='${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/allergy/all')}'>${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/allergy/all')}</a></p>
  <h3>Results</h3>
  <p class="resource">/vpr/v1/${patientInstance.icn}/result/show/{uid}</p>
  <p class="resource"><a href='${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/result/all')}'>${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/result/all')}</a></p>
  <p class="resource"><a href='${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/result/abnormal')}'>${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/result/abnormal')}</a></p>
  <p class="resource"><a href='${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/result/critical')}'>${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/result/critical')}</a></p>
  <p class="resource"><a href='${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/chemistry/all')}'>${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/chemistry/all')}</a></p>
  <p class="resource"><a href='${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/chemistry/abnormal')}'>${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/chemistry/abnormal')}</a></p>
  <p class="resource"><a href='${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/chemistry/critical')}'>${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/chemistry/critical')}</a></p>
  <p class="resource"><a href='${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/microbiology/all')}'>${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/microbiology/all')}</a></p>
  <p class="resource"><a href='${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/microbiology/abnormal')}'>${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/microbiology/abnormal')}</a></p>
  <p class="resource"><a href='${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/microbiology/critical')}'>${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/microbiology/critical')}</a></p>
  <h3>Encounters</h3>
  <p class="resource">/vpr/v1/${patientInstance.icn}/encounter/show/{uid}</p>
  <p class="resource"><a href='${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/encounter/all')}'>${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/encounter/all')}</a></p>
  <p class="resource"><a href='${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/encounter/appointments')}'>${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/encounter/appointments')}</a></p>
  <p class="resource"><a href='${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/encounter/admissions')}'>${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/encounter/admissions')}</a></p>
  <p class="resource"><a href='${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/encounter/visits')}'>${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/encounter/visits')}</a></p>
  <p class="resource"><a href='${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/encounter/last')}'>${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/encounter/last')}</a></p>
  <h3>Documents</h3>
  <p class="resource">/vpr/v1/${patientInstance.icn}/document/show/{id}</p>
  <p class="resource"><a href='${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/document/all')}'>${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/document/all')}</a></p>
  <h3>Immunizations</h3>
  <p class="resource">/vpr/v1/${patientInstance.icn}/immunization/show/{id}</p>
  <p class="resource"><a href='${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/immunization/all')}'>${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/immunization/all')}</a></p>
  <p class="resource"><a href='${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/immunization/contraindicated')}'>${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/immunization/contraindicated')}</a></p>
  <h3>Medications</h3>
  <p class="resource">/vpr/v1/${patientInstance.icn}/medication/{id}</p>
  <p class="resource"><a href='${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/medication/all')}'>${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/medication/all')}</a></p>
  <p class="resource"><a href='${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/medication/allactive')}'>${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/medication/allactive')}</a></p>
  <p class="resource"><a href='${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/medication/alloutactive')}'>${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/medication/alloutactive')}</a></p>
  <p class="resource"><a href='${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/medication/allinactive')}'>${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/medication/allinactive')}</a></p>
  <p class="resource"><a href='${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/medication/findRelated')}'>${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/medication/findRelated')}</a></p>
  <p class="resource"><a href='${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/medication/feed')}'>${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/medication/feed')}</a></p>
  <h3>Vital Sign</h3>
  <p class="resource">/vpr/v1/${patientInstance.icn}/vital/{uid}</p>
  <p class="resource"><a href='${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/vital/all')}'>${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/vital/all')}</a></p>
  <p class="resource"><a href='${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/vital/last')}'>${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/vital/last')}</a></p>
  <h3>Tagger</h3>
  <p class="resource">/vpr/v1/${patientInstance.icn}/tagger/{id}</p>
  <p class="resource"><a href='${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/tagger/all')}'>${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/tagger/all')}</a></p>
  <p class="resource"><a href='${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/tagger/write')}'>${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/tagger/write')}</a></p>
  <h3>Problems</h3>
  <p class="resource">/vpr/v1/${patientInstance.icn}/problem/show/{uid}</p>
  <p class="resource"><a href='${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/problem/all')}'>${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/problem/all')}</a></p>
  <p class="resource"><a href='${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/problem/active')}'>${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/problem/active')}</a></p>
  <p class="resource"><a href='${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/problem/inactive')}'>${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/problem/inactive')}</a></p>
  <h3>Procedures</h3>
  <p class="resource">/vpr/v1/${patientInstance.icn}/procedure/show/{uid}</p>
  <p class="resource"><a href='${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/procedure/all')}'>${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/procedure/all')}</a></p>
  <h3>Consults</h3>
  <p class="resource">/vpr/v1/${patientInstance.icn}/consult/show/{uid}</p>
  <p class="resource"><a href='${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/consult/all')}'>${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/consult/all')}</a></p>
  <h3>Radiology</h3>
  <p class="resource">/vpr/v1/${patientInstance.icn}/radiology/show/{uid}</p>
  <p class="resource"><a href='${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/radiology/all')}'>${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/radiology/all')}</a></p>
  <h3>Orders</h3>
  <p class="resource">/vpr/v1/${patientInstance.icn}/order/show/{uid}</p>
  <p class="resource"><a href='${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/order/all')}'>${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/order/all')}</a></p>
  <h3>Health Factors</h3>
  <p class="resource">/vpr/v1/${patientInstance.icn}/factor/show/{uid}</p>
  <p class="resource"><a href='${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/factor/all')}'>${createLink(uri: '/vpr/v1/' + patientInstance.icn + '/factor/all')}</a></p>
    </div>
</body>
</html>
