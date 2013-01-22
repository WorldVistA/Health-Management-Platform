<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="viewport" />
    <title>Clinical Practice Environment API Documentation</title>
</head>
<body>
    <h1>${wadl.doc.@title}</h1>

    <p>${wadl.doc.text()}</p>

    <h2>Resources</h2>

    <p>The API supports the following resources:</p>
    <g:each in="${resources}" var="resourceElement">
        <g:if test="${resourceElement.id}">
            <p class="resource"><g:link action="resource"
                                        id="${resourceElement.id}">${resourceElement.path}</g:link></p>
        </g:if>
    </g:each>
    <h2><a>HTTP Response Codes and Errors</a></h2>

    <p>Applications send information through the HTTP response codes and error messages. (TBD)</p>

    <h2>WADL</h2>

    <p>TBD <g:link action="wadl">WADL</g:link></p>
</body>
</html>