<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="org.osehra.cpe.vpr.queryeng.*" %>
<%@ page import="org.osehra.cpe.vpr.viewdef.*" %>
<%@ page import="java.util.*" %>
<html>
<body>
<%
	RenderTask task = renderer.render(viewdef, params);
	out.println('count='+task.size());
%>
</body>
</html>
