<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="org.osehra.cpe.vpr.queryeng.*" %>
<%@ page import="org.osehra.cpe.vpr.viewdef.*" %>
<%@ page import="org.osehra.cpe.datetime.*" %>
<%@ page import="org.osehra.cpe.datetime.format.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>
<%@ page import="org.osehra.cpe.vpr.web.converter.dateTime.*" %>
<html>
<body>
<%
	// TODO: Is all this code appropriate for a "frame" for "Inpatient Meds Due Today" ?
	for(String rslt: results) {
		out.println(rslt);
	}
%>
</body>
</html>
