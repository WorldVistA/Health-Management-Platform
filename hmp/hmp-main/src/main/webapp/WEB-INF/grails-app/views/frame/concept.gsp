<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Concept</title>
    <g:render template="/layouts/detail"/>
</head>

<body>
<table class="hmp-labeled-values" border="1">
	<tr>
		<td colspan="2">${c.urn}: ${c.description} (${c.code})</td>
	</tr>
	<tr>
		<td colspan="2">Computed Ancestors</td>
	</tr>
	<tr>
		<td colspan="2">
		<g:each in="${c.getAncestorMap() }" var="ancestor">
			<li><a title="${ancestor.key}" href="/term/display?urn=${ancestor.key}">${ancestor.value}</a></li>
		</g:each>
		</td>
	</tr>
	<tr>
		<td colspan="2">Computed Relationships</td>
	</tr>
	<tr>
		<td colspan="2">
		<ul>
		<g:each in="${c.getRelationshipTree() }" var="rel">
			<li>${rel.key}:</li>
			<ul>
				<g:each in="${rel.value}" var="item">
					<li><a title="${item.key}" href="/term/display?urn=${item.key}">${item.value}</a></li>
				</g:each>
			</ul>
		</g:each>
		</ul>
		</td>
	</tr>	
	<%-- 
	<g:if test="${c.attributes.size()}">
		<tr>
			<td colspan="2">Attributes (${c.attributes.size()})</td>
		</tr>
		<g:each in="${c.attributes}" var="attr">
			<tr>
				<td>${attr.key}</td>
				<td>${attr.value}</td>
			</tr>
		</g:each>
	</g:if>
	--%>
</table>
<iframe src="/term/${c.urn}" style="height: 100%; width: 100%;"></iframe>
</body>
</html>