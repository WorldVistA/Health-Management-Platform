<%@ page import="org.osehra.cpe.datetime.format.PointInTimeFormat" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>${item.summary}</title>
    <g:render template="/layouts/detail"/>
</head>

<body>
<table class="hmp-labeled-values">
    <tr>
        <td>Date</td>
        <td><hmp:formatDate date="${item.dateTime}"/></td>
    </tr>
    <tr>
        <td>Kind</td>
        <td>${item.kind}</td>
    </tr>
    <g:each in="${item.providers}" status="i" var="prov">
        <tr>
            <td>Provider</td>
            <td>${prov.clinician.name}</td>
            <g:if test="${prov.role == 'P'}">
                <td>Primary</td>
            </g:if>
            <g:else>
                <td>Secondary</td>
            </g:else>
        </tr>
    </g:each>
</table>
</body>
</html>
