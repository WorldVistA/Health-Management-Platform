/**
 * Singleton for tracking the currently selected HMP 'app'.
 */
Ext.define('org.osehra.hmp.AppContext', {
    requires:[
        'org.osehra.hmp.UserContext'
    ],
    singleton:true,
    config:{
        /**
         * @cfg {Object} appInfo
         * @cfg {String} appInfo.displayName
         * @cfg {String} appInfo.app
         * @cfg {Object} appInfo.env System environment of server JVM. (See java.lang.System.getenv())
         * @cfg {Object} appInfo.system System properties of server JVM. (See java.lang.System.getProperties())
         * @cfg {Object} appInfo.props HMP specific properties.
         */
        appInfo:{}
    },
    /**
     *
     * @return
     */
    getVersion:function() {
        var appInfo = this.getAppInfo();
        if (appInfo.props && appInfo.props['hmp.version'])
            return appInfo.props['hmp.version'];
        else
            return "SOMETHING IS WRONG!!!"
    },
    /**
     * Loads AppContext and UserContext with currently selected app and logged in user.
     * @param fn
     * @param scope
     */
    load:function(fn, scope) {
        var me = this;
        me.ajaxRequest = Ext.Ajax.request({
            url:'/app/info',
            callback: me.onLoad,
            scope: me,
            fn: fn
        });
    },
    /**
     * @private
     */
    onLoad:function (options, success, response) {
        var me = this;
        if (success) {
            var jsonc = Ext.JSON.decode(response.responseText);
            var appInfo = jsonc.data;

            // coordinate user stuff with UserContext
            org.osehra.hmp.UserContext.setUserInfo(appInfo.userInfo);
            org.osehra.hmp.UserContext.setUserPrefs(appInfo.userPrefs);

            // remove the user-related stuff now that it has been set in UserContext
            delete appInfo.userInfo;
            delete appInfo.userPrefs;

            me.setAppInfo(appInfo);
        } else {
            // TODO: report this error or sommat?
        }

        if (Ext.isDefined(options.fn)) {
            options.fn.call(options.scope || me, success);
        }
    },
//    applyAppInfo:function (appInfo) {
//        this.appInfo = appInfo;
//        if (Ext.isDefined(this.listeners) && this.listeners != null) {
//            for (var i = 0; i < this.listeners.length; i++) {
//                this.listeners[i].fn.call(this.listeners[i].scope || this.appInfo, this.appInfo);
//            }
//            delete this.listeners;
//        }
//    },
    /**
     *
     * @param {Function} fn
     */
//    onAvailable:function (fn, scope) { // TODO: maybe rename this onReady?
//        if (!Ext.isDefined(this.ajaxRequest)) {
//            fn.call(scope || this.appInfo, this.appInfo);
//        } else {
//            // meh
//            this.listeners = new Array();
//            this.listeners.push({fn:fn, scope:scope});
//        }
//    }
});
