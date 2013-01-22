<%@ page contentType="text/html;charset=UTF-8"%>
<html>
<head>
<title>
	${item.displayName}
</title>
<g:if test="${item.comment && item.comment.length()>0}">
	<h2>${item.typeName}</h2>
	<h3>
		${item.comment}
	</h3>
</g:if>
<g:render template="/layouts/detail" />
</head>
<body>
	<b>Summary:</b>${item.summary}<br><br>
	<b>Units:</b>${item.units}<br>
	<table border="1" style="text-align:right;padding:5px">
		<tr>
			<td>Low Value</td>
			<td>High Value</td>
			<td>Result</td>
		</tr>
		<tr>
			<td>
				${item.low}
			</td>
			<td>
				${item.high}
			</td>
			<td>
				${item.result}
			</td>
		</tr>
	</table>
	<g:if test="${item.document && item.document.length()>0}">
		<hmp:collapsibleDocument docId="result-reportText" title="Report Text">
			${item.document}
		</hmp:collapsibleDocument>
	</g:if>
</body>
</html>
