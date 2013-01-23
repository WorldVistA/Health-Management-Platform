<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.osehra.cpe.vpr.frameeng.*" %>
<%@ page import="org.osehra.cpe.vpr.frameeng.Frame.*" %>
<%@ page import="org.osehra.cpe.vpr.frameeng.FrameJob" %>
<%@ page import="org.osehra.cpe.vpr.frameeng.FrameJob.FrameTask" %>
<%@ page import="org.osehra.cpe.vpr.frameeng.IFrameEvent.InvokeEvent" %>
<%@ page import="org.osehra.cpe.vpr.frameeng.FrameAction.ObsRequestAction" %>
<%@ page import="org.osehra.cpe.vpr.Medication" %>
<%@ page import="org.osehra.cpe.vpr.pom.PatientEvent" %>
<html>
<head>
    <title>${item.qualifiedName}</title>
    <g:render template="/layouts/detail"/>
</head>

<body>
<table class="hmp-labeled-values" style="float: left;">
    <tr>
        <td>Medication</td>
        <td>${item.qualifiedName}</td>
    </tr>
    <tr>
        <td>Type</td>
        <td>${item.kind}</td>
    </tr>
    <tr>
        <td>Facility</td>
        <td>${item.facilityName}</td>
    </tr>
    <tr>
        <td>VA Status</td>
        <td>${item.vaStatus}</td>
    </tr>
    <tr>
        <td>Sig</td>
        <td><pre>${item.sig}</pre></td>
    </tr>
    <g:each in="${item.dosages}" status="i" var="dosage">
        <tr>
            <td>Dose/Route/Schedule</td>
            <td>${dosage.dose} ${dosage.routeName} ${dosage.scheduleName} ${dosage.duration}</td>
        </tr>
    </g:each>
    <g:if test="${item.vaType == 'O'}">
        <tr>
            <td>Day Supply</td>
            <td>${item.orders*.daysSupply[0]}</td>
        </tr>
        <tr>
            <td>Quantity</td>
            <td>${item.orders*.quantityOrdered[0]}</td>
        </tr>
        <tr>
            <td>Refills Remaining</td>
            <td>${item.orders*.fillsRemaining[0]}</td>
        </tr>
    </g:if>
    <tr>
        <td>Start</td>
        <td><hmp:formatDate date="${item.overallStart}"/></td>
    </tr>
    <tr>
        <td>Stop</td>
        <td><hmp:formatDate date="${item.overallStop}"/></td>
    </tr>
    <tr>
        <td>Location</td>
        <td>${item.orders*.locationName[0]}</td>
    </tr>
    <tr>
        <td>Facility</td>
        <td>${item.facilityName}</td>
    </tr>
    <tr>
        <td>Ordering Provider</td>
        <td>${item.orders*.providerName[0]}</td>
    </tr>
    <tr>
        <td>Finishing Pharmacist</td>
        <td>${item.orders*.pharmacistName[0]}</td>
    </tr>
</table>
</body>
</html>
