<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>${message(code: 'platform.name')} &raquo; Sign In</title>
    <g:render template="/layouts/extjs"/>
    <script type="text/javascript">
        Ext.require(['EXT.DOMAIN.hmp.auth.Login']);
        Ext.onReady(function () {
            Ext.create('EXT.DOMAIN.hmp.auth.Login');
        });
    </script>
</head>

<body>
</body>
</html>
