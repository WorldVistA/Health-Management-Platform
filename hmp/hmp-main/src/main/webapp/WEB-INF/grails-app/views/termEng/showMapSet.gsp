<%--
  Created by IntelliJ IDEA.
  User: vhaislbrayb
  Date: 6/6/11
  Time: 6:16 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
  <head><title>Simple GSP page: ${params?.id?.encodeAsHTML()}</title></head>
  <body>

 <table border="1">
      <tr>
          <td colspan="3" align="center">${mapSetInfo.getName()} (${mapSetInfo.getID()})</td>
      </tr>




      <tr>
          <td>&nbsp;</td>
          <td>${mapSetInfo.getSourceCodeSystemID()}</td>
          <td>${mapSetInfo.getTargetCodeSystemID()}</td>
      </tr>
      <tr>
          <td>VUID</td>
          <td>Source Concept (code)</td>
          <td>Target Concept (code)</td>
      </tr>
  <g:each in="${items}" var="i" >
      <tr>
          <td></td>
          <td>${i.key}</td>
          <td>${i.value}</td>
      </tr>
  </g:each>
  </table>
  </body>
</html>