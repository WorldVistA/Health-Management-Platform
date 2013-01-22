<%@ page contentType="text/html;charset=UTF-8" %>
<html>
  <head>
    <title>Detail Not Found</title>
    <g:render template="/layouts/detail"/>
  </head>
  <body>
    <p style="font-size:24px;font-style:italic;font-weight:bold;color:#AAAAAA;">Detail cannot be shown.</p><br>
    <p style="font-size:18px;font-style:italic;font-weight:bold;color:#AAAAAA;">(Caused by: ${error.message})</p>
  </body>
</html>