<%--
  Created by IntelliJ IDEA.
  User: vhaislbrayb
  Date: 6/6/11
  Time: 6:07 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
  <head><title>Simple GSP page</title></head>
  <body>

    <g:each in="${termEng.getMapSetList()}" var="i">

        <li><g:link action="showMapSet" id="${i.getID()}">${i.getName()} </g:link>(${i.getProperties().getProperty("table.size")})</li>

    </g:each>

  
  </body>
</html>