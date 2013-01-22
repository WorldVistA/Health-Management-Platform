<meta http-equiv="X-UA-Compatible" content="IE=9"/>
<link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">
<link rel="apple-touch-icon" href="${resource(dir: 'images', file: 'apple-touch-icon.png')}">
<link rel="apple-touch-icon" sizes="114x114" href="${resource(dir: 'images', file: 'apple-touch-icon-retina.png')}">

<link rel="stylesheet" href="${hmp.userPrefResource(key: 'ext.theme', defaultVal:'/css/hi2-default.css')}"/>
<link rel="stylesheet" href="/css/hmp.css"/>

<script type="text/javascript" src="${resource(dir: 'js', file: 'WebFontLoader.js')}"></script>
<script type="text/javascript" src="${hmp.userPrefResource(key: 'ext.libver', defaultVal:'/lib/extjs-4.1.3/ext-all-dev.js')}"></script>


<script type="text/javascript" src="/lib/jquery/jquery-1.6.1.min.js"></script>
<script type="text/javascript" src="/lib/highcharts/highcharts.js"></script>
<script type="text/javascript" src="/lib/highcharts/highcharts-more.js"></script>
<link rel="stylesheet" href="/lib/jcrop/css/jquery.Jcrop.min.css">
<script type="text/javascript" src="/lib/jcrop/js/jquery.Jcrop.min.js"></script>

<script type="text/javascript" charset="utf-8">
    var ie9Comp = (navigator.userAgent.indexOf('Trident/5.0') > -1);
    if (Ext.ieVersion >= 9 || ie9Comp || Ext.firefoxVersion >= 4 || Ext.chromeVersion >= 9 || Ext.safariVersion >= 5) {
        Ext.BLANK_IMAGE_URL = "${resource(dir:'images', file:'s.gif')}";
        Ext.Loader.setConfig({
            enabled: true,
            disableCaching: true,
            paths: {
                'gov': '/js/gov',
                'Ext.ux': '/lib/extjs-4.1.3/examples/ux'
            }
        });
    } else {
        window.location = "${createLink(uri:'/supportedBrowsers')}";
    }
</script>
<script type="text/javascript" src="${resource(dir: 'js', file: 'ExtOverrides.js')}"></script>
<script type="text/javascript" src="${resource(dir: 'js/EXT/DOMAIN/hmp/appbar', file: 'ErrorHandler.js')}"></script>
