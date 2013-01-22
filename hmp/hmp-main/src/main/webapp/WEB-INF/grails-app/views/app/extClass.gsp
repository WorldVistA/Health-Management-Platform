<html>
<head>
    <title>${title}</title>
    <g:render template="/layouts/extjs"/>
    <script type="text/javascript">
        Ext.require('${extClass}');
        Ext.onReady(function () {
            Ext.create('${extClass}');
        });
    </script>
</head>
<body>
</body>
</html>