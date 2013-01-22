<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <g:render template="/layouts/viewport"/>
    <script type="text/javascript">
    	Ext.onReady(function() {
            var viewport = Ext.ComponentQuery.query('viewport')[0];
        	viewport.setCenter({
				title: 'CART-CL',
				height: 300,
				width: 400,
				html: 'This is a simple example of where CART-CL or other 3rd party apps, data and functionality could be plugged-in, queried or displayed.'
           	})
    	});
    </script>
</head>
<body>
<div id="center"></div>
</body>
</html>
