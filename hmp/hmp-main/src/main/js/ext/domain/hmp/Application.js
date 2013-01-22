/**
 * Represents an ExtJS 4 based HMP application, which is typically a single page app using a {@link EXT.DOMAIN.hmp.Viewport Viewport}.
 * Provides an entry point for initializing the application via {@link #launch}.
 *
 * Serves similar role as {@link Ext.app.Application} without the same strict namespacing conventions for Models/Stores/Controllers/Views.
 */
Ext.define('EXT.DOMAIN.hmp.Application', {
    requires:[
        'EXT.DOMAIN.hmp.AppContext',
        'EXT.DOMAIN.hmp.Controller'
    ],
    mixins:{
        observable:'Ext.util.Observable'
    },
    /**
     * @cfg {String[]} controllers
     * Names of controllers that the app uses.
     */

    /**
     * @cfg {Object} scope
     * The scope to execute the {@link #launch} function in. Defaults to the Application instance.
     */
    scope:undefined,

    /**
     * @cfg {Boolean} enableQuickTips
     * True to automatically set up Ext.tip.QuickTip support.
     */
    enableQuickTips:true,
    /**
     * @cfg {Boolean} autoCreateViewport
     * True to automatically load and instantiate EXT.DOMAIN.hmp.Viewport before firing the launch function.
     */
    autoCreateViewport:false,

    onClassExtended:function (cls, data, hooks) {
        var j, subLn, controllerName,
            controllers = data.controllers || [],
            requires = [];

        // require all controllers
        for (j = 0, subLn = controllers.length; j < subLn; j++) {
            controllerName = controllers[j];
            requires.push(controllerName);
        }

        if (data.autoCreateViewport) {
            requires.push('EXT.DOMAIN.hmp.Viewport');
        }

        // Any "requires" also have to be processed before we fire up the App instance.
        if (requires.length) {
            onBeforeClassCreated = hooks.onBeforeCreated;

            hooks.onBeforeCreated = function (cls, data) {
                var args = Ext.Array.clone(arguments);

                Ext.require(requires, function () {
                    return onBeforeClassCreated.apply(this, args);
                });
            };
        }
    },

    /**
     * Creates new Application.
     * @param {Object} [config] Config object.
     */
    constructor:function (config) {
        var me = this,
            controllers;

        config = config || {};
        Ext.apply(this, config);

        me.mixins.observable.constructor.call(this);

        me.callParent(arguments);

        controllers = Ext.Array.from(me.controllers);
        me.controllers = new Ext.util.MixedCollection();

//            console.log("init()");
        Ext.state.Manager.setProvider(Ext.create('Ext.state.CookieProvider'));

        me.doInit(me);

        var ln, i, controller;
        ln = controllers && controllers.length;
        for (i = 0; i < ln; i++) {
            controller = me.getController(controllers[i]);
            controller.doInit(me);
        }

//            console.log("AppContext.load()");
        // trigger launch sequence after AppContext has loaded
        EXT.DOMAIN.hmp.AppContext.load(function () {
//                console.log("AppContext loaded()");
            me.onBeforeLaunch.call(me);
        }, me);
    },
    doInit:function (app) {
        if (!this._initialized) {
            this.init(app);
            this._initialized = true;
        }
    },
    /**
     * A template method that is called when your application boots. It is called before the
     * {@link Ext.app.Application Application}'s launch function is executed so gives a hook point to run any code before
     * your Viewport is created.
     *
     * @param {EXT.DOMAIN.hmp.Application} application
     * @template
     */
    init:Ext.emptyFn,

    /**
     * @method
     * @template
     * Called automatically when the page has completely loaded. This is an empty function that should be
     * overridden by each application that needs to take action on page load.
     * @param {String} profile The detected application profile
     * @return {Boolean} By default, the Application will dispatch to the configured startup controller and
     * action immediately after running the launch function. Return false to prevent this behavior.
     */
    launch:Ext.emptyFn,

    /**
     * @private
     */
    onBeforeLaunch:function () {
//        console.log("onBeforeLaunch()");
        var me = this,
            controllers, c, cLen, controller;

        if (me.enableQuickTips) {
            Ext.tip.QuickTipManager.init();
        }

        if (me.autoCreateViewport) {
            Ext.create('EXT.DOMAIN.hmp.Viewport');
        }

        me.launch.call(this.scope || this);
        me.launched = true;

        me.fireEvent('launch', this);

        // call onLaunch() on all controllers
        controllers = me.controllers.items;
        cLen = controllers.length;

        for (c = 0; c < cLen; c++) {
            controller = controllers[c];
            controller.onLaunch(this);
        }
    },
    getController:function (name) {
        var me = this,
            controllers = me.controllers,
            controller = controllers.get(name);

        if (!controller) {
            controller = Ext.create(name, {
                application:me,
                id:name
            });

            controllers.add(controller);
            if (me._initialized) {
                controller.doInit(me);
            }
        }
        return controller;
    }
});
