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
        <td>Name</td>
        <td>${item.typeName}</td>
    </tr>
    <tr>
        <td>Status</td>
        <td>${item.resultStatusCode}</td>
    </tr>
    <tr>
        <td>Request Date</td>
        <td><hmp:formatDate date="${item.observed}" /></td>
    </tr>
</table>
<hr />
<pre>${item.document}</pre>
</body>
</html>