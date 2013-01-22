<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>${item.summary}</title>
    <meta name="layout" content="detail"/>
</head>

<body>
<table class="hmp-labeled-values">
    <tr>
        <td>Description</td>
        <td>${item.name}</td>
    </tr>
    <tr>
        <td>Onset Date</td>
        <td><hmp:formatDate date="${item.recorded}"/></td>
    </tr>
    <tr>
        <td>Facility</td>
        <td>${item.facilityName}</td>
    </tr>
    <tr>
        <td>Comment</td>
        <td><pre>${item.comment}</pre></td>
    </tr>
</table>
</body>
</html>