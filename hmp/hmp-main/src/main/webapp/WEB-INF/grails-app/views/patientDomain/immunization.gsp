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
        <td>${item.name}</td>
    </tr>
    <tr>
          <td>Facility</td>
          <td>${item.facilityName}</td>
      </tr>
    <tr>
        <td>Administered</td>
        <td><hmp:formatDate date="${item.administeredDateTime}"/></td>
    </tr>
    <tr>
        <td>Comment</td>
        <td><pre>${item.comments}</pre></td>
    </tr>
</table>
</body>
</html>