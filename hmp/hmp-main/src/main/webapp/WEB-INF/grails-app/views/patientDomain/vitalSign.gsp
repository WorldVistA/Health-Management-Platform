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
        <td>Type Code</td>
        <td>${item.typeCode}</td>
    </tr>
    <tr>
        <td>Type Name</td>
        <td>${item.typeName}</td>
    </tr>
    <tr>
        <td>Result</td>
        <td>${item.result}</td>
    </tr>
    <tr>
        <td>Unit</td>
        <td>${item.units}</td>
    </tr>
    <tr>
        <td>Metric Result</td>
        <td>${item.metricResult}</td>
    </tr>
    <tr>
        <td>Metric Unit</td>
        <td>${item.metricUnits}</td>
    </tr>
    <tr>
        <td>Interpretation</td>
        <td>${item.interpretationName}</td>
    </tr>
    <tr>
        <td>Low</td>
        <td>${item.low}</td>
    </tr>
    <tr>
        <td>High</td>
        <td>${item.high}</td>
    </tr>
    <tr>
        <td>Body Site</td>
        <td>${item.bodySite}</td>
    </tr>
    <tr>
        <td>Document</td>
        <td>${item.document}</td>
    </tr>
</table>
</body>
</html>