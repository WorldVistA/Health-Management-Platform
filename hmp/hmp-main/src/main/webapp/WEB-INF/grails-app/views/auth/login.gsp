<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>${message(code: 'platform.name')} &raquo; Sign In</title>
    <g:render template="/layouts/extjs"/>
    <script type="text/javascript">
        Ext.require(['org.osehra.hmp.auth.Login']);
        Ext.onReady(function () {
            Ext.create('org.osehra.hmp.auth.Login');
        });
    </script>
</head>

<body>
</body>
</html>
