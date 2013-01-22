<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>${item.summary}</title>
    <g:render template="/layouts/detail"/>
</head>
<body>
<table class="hmp-labeled-values">
    <tr>
        <td>Name</td>
        <td>${item.taskName}</td>
    </tr>
    <tr>
          <td>Facility</td>
          <td>${item.facilityName}</td>
    </tr>
    <tr>
          <td>Owner</td>
          <td>${item.ownerName}</td>
    </tr>
    <tr>
          <td>Assign</td>
          <td>${item.assignToName}</td>
    </tr>
    <tr>
        <td>Due</td>
        <td><hmp:formatDate date="${item.dueDate}"/></td>
    </tr>
    <tr>
        <td></td>
        <td><pre>${item.description}</pre></td>
    </tr>
</table>
</body>
</html>