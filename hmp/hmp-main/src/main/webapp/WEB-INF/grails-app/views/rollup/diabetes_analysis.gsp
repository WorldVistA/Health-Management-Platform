<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="EXT.DOMAIN.cpe.vpr.queryeng.*" %>
<%@ page import="EXT.DOMAIN.cpe.vpr.viewdef.*" %>
<%@ page import="java.util.*" %>
<html>
<body>
<%
	RenderTask task = renderer.render(viewdef, params);
	out.println('count='+task.size());
%>
</body>
</html>
