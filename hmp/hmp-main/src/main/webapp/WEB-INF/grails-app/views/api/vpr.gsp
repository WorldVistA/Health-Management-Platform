<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>PLLLLLBBBBBBBBBBBB</title>
    <meta name="layout" content="api"/>
</head>
<body>
<g:each in="${resourcesByType}" var="entry">
    <h2>${entry.key}</h2>
    <dl>
        <g:each in="${entry.value}" var="r">
            <dt>${r.path}</dt>
            <dd>${r.title}</dd>
        </g:each>
    </dl>
</g:each>
</body>
</html>