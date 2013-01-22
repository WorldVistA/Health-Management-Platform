<%--
  Created by IntelliJ IDEA.
  User: vhaislbrayb
  Date: 6/6/11
  Time: 5:36 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
  <head><title>Simple GSP page</title></head>
  <body>

  <g:each in="${termEng.loadCodeSystemList()}" var="i">
      <li><g:link action="showCodeSystem" id="${i.VUID}">${i.Name} (${i.VersionName})</g:link></li>

  </g:each>



  </body>
</html>