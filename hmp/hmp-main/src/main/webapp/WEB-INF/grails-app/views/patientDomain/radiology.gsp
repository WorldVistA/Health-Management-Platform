<%@ page import="org.osehra.cpe.datetime.format.PointInTimeFormat; org.osehra.cpe.datetime.format.HL7DateTimeFormat" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>${item.patient.familyName}, ${item.patient.givenNames} &raquo; ${item.summary}</title>
    <g:render template="/layouts/detail"/>
</head>

<body>
<p>${item.typeName} ${item.typeCode} <hmp:formatDate date="${item.dateTime}" /></p>
<table class="hmp-labeled-values">
    <tr>
          <td>Facility</td>
          <td>${item.facilityName}</td>
      </tr>
    <tr>
        <td>Category</td>
        <td>${item.category}</td>
    </tr>
    <tr>
        <td>Status</td>
        <td>${item.status}</td>
    </tr>
    <g:each in="${item.providers}" status="i" var="prov">
        <tr>
            <td>Provider</td>
            <td>${prov.provider.name}</td>
        </tr>
    </g:each>
    <g:each in="${item.results}" status="r" var="result">
        <g:if test="${result.report != null}">
            <tr>
                Report
            </tr>
            <tr>
                <pre>${result.report}</pre>
            </tr>
        </g:if>
        <g:if test="${result.localTitle != null}">
            <tr>
                ${result.localTitle}
            </tr>
            <tr>
                <pre>${result.document}</pre>
            </tr>
        </g:if>
    </g:each>

</table>
</body>
</html>
