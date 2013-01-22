<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>${item.summary}</title>
    <g:render template="/layouts/detail"/>
</head>
<body>
<table class="hmp-labeled-values">
    <tr>
        <td>Facility</td>
        <td>${item.facilityName}</td>
    </tr>
    <tr>
        <td>ICD-9</td>
        <td>${item.icdName}</td>
    </tr>
    <tr>
        <td>Onset</td>
        <td><hmp:formatDate date="${item.onset}"/></td>
    </tr>
    <tr>
        <td>Status</td>
        <td>${item.statusName}<g:if test="${item.acuityName}">/${item.acuityName}</g:if></td>
    </tr>
    <tr>
        <td>Provider</td>
        <td>${item.providerName}</td>
    </tr>
    <tr>
        <td>Location</td>
        <td>${item.locationName}</td>
    </tr>
    <tr>
        <td>Entered</td>
        <td><hmp:formatDate date="${item.entered}"/></td>
    </tr>
    <tr>
        <td>Updated</td>
        <td><hmp:formatDate date="${item.updated}"/></td>
    </tr>
</table>
<%--
<g:each in="${item.comments}" status="i" var="prblcomm">
    <hr/>
    <pre>${prblcomm.comment}</pre>
</g:each>
  --%>
</body>
</html>