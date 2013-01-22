<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head profile="http://www.w3.org/2005/10/profile">
    <title>HMP Config</title>
    <g:render template="/layouts/extjs"/>
    <g:javascript>
        Ext.require('EXT.DOMAIN.cpe.multi.BoardBuilderApp');
        Ext.onReady(function () {
            Ext.create('EXT.DOMAIN.cpe.multi.BoardBuilderApp');
        });
    </g:javascript>
</head>
<body>
</body>
</html>
