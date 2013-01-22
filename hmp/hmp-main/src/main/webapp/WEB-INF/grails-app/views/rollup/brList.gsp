<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="EXT.DOMAIN.cpe.vpr.queryeng.*" %>
<%@ page import="EXT.DOMAIN.cpe.vpr.viewdef.*" %>
<%@ page import="EXT.DOMAIN.cpe.datetime.*" %>
<%@ page import="EXT.DOMAIN.cpe.datetime.format.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>
<%@ page import="EXT.DOMAIN.cpe.vpr.web.converter.dateTime.*" %>
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
