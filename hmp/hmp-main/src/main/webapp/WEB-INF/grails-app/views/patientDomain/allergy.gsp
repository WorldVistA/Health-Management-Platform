<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>${item.summary}</title>
    <g:render template="/layouts/detail"/>
</head>
<body>
<b>Type:${item.adverseEventTypeName}</b>
<br><br>
<b>Products:</b><br>
<g:each in="${item.products}" status="i" var="product">
    <g:if test="${product.name}">
		<dd>${product.name}</dd>
    </g:if>
</g:each>
<br>
<b>Reactions:</b><br>
<g:each in="${item.reactions}" status="i" var="reaction">
    <g:if test="${reaction.name}">
		<dd>${reaction.name}</dd>
    </g:if>
</g:each>
</body>
</html>
