<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head profile="http://www.w3.org/2005/10/profile">
    <title>CPE</title>
    <g:render template="/layouts/extjs"/>
    <g:javascript>
        Ext.require(['org.osehra.cpe.CPEApp']);
        Ext.onReady(function(){
            Ext.create('org.osehra.cpe.CPEApp');
        });
    </g:javascript>
</head>
<body>
</body>
</html>
