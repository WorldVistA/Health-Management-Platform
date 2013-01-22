<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.fasterxml.jackson.databind.JsonNode"%>
<%@ page import="com.fasterxml.jackson.databind.node.ArrayNode"%>


<html>
<head>
    <title>Reminder</title>
    <g:render template="/layouts/detail"/>
</head>

<body>

<%
	Map<String, Object> p = new HashMap<String, Object>();
    p.put("command", "evaluateReminder");
    p.put("uid", pid);
    p.put("patientId", params.dfn); // TODO: how to get the right "system id" with no user context? 
    JsonNode ret = rpc.executeForJson("/VPR UI CONTEXT/VPRCRPC RPC", p);
    String status = ret.get("status").asText();
    String due = ret.get("dueDate").asText();
    String last = ret.get("lastDone").asText();
    String text = ret.get("clinicalMaintenance").asText();
%>
<table class="hmp-labeled-values" border="1">
	<tr>
		<td><img src="/images/icons/warning_sign.png" style="float: left;"/></td>
		<td>
			Status: ${status}<br>
			Due: ${due}<br>
			Last: ${last}<br>
		</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td><pre>${text}</pre></td>
	</tr>
</table>
</body>
</html>