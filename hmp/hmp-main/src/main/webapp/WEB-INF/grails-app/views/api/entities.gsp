<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Core Entities</title>
    <meta name="layout" content="viewport"/>
</head>

<body>
<h1>Core Entities</h1>
<g:each in="${entityClasses}" var="entity">
    <h2>${entity.name}</h2>
    <table>
        <thead>
            <th>
                <td>Property</td>
                <td>Type</td>
                <td>Description</td>
            </th>
        </thead>
        <tbody>
             <g:each in="${entity.properties}" var="prop">
                 <tr>
                     <td>${prop.name}</td>
                     <td>${prop.referencedPropertyType.name}</td>
                 </tr>
             </g:each>
        </tbody>
    </table>
</g:each>
</body>
</html>