<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
<title>${message(code: 'platform.name')} &raquo; Setup</title>
<g:render template="/layouts/viewport"/>
<g:render template="/setup/style"/>
</head>
<body>
<div id="center">
    <p class="success">Your configuration was saved successfully. Please restart The ${message(code: 'platform.name')} to continue.</p>
</div>
</body>
</html>