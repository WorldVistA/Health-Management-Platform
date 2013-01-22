/**
 * @class EXT.DOMAIN.hmp.Controller
 *
 * HMP Controller base class that provides convenient stuff from Ext.app.Controller without inconvenient namespacing convention
 * and tight coupling to Ext.app.Application.
 */
Ext.define('EXT.DOMAIN.hmp.Controller', {
    mixins:{
        observable:'Ext.util.Observable'
    },
    statics:{
        eventbus:Ext.create('Ext.app.EventBus')
    },

    /**
     * @cfg {Object[]} refs
     * Array of configs to build up references to views on page. For example:
     *
     *     Ext.define("EXT.DOMAIN.foo.Foo", {
     *         extend: "EXT.DOMAIN.hmp.Controller",
     *         refs: [
     *             {
     *                 ref: 'list',
     *                 selector: 'grid'
     *             }
     *         ],
     *     });
     *
     * This will add method `getList` to the controller which will internally use
     * Ext.ComponentQuery to reference the grid component on page.
     *
     * The following fields can be used in ref definition:
     *
     * - `ref` - name of the reference.
     * - `selector` - Ext.ComponentQuery selector to access the component.
     * - `autoCreate` - True to create the component automatically if not found on page.
     * - `forceCreate` - Forces the creation of the component every time reference is accessed
     *   (when `get<REFNAME>` is called).
     */

    /**
     * Creates new Controller.
     * @param {Object} config (optional) Config object.
     */
    constructor: function(config) {
        var me = this;

        me.mixins.observable.constructor.call(me, config);

        Ext.apply(me, config);

        if (me.refs) {
            me.ref(me.refs);
        }

        me.initAutoGetters();

        return this;
    },
    initAutoGetters: function() {
        var proto = this.self.prototype,
            prop, fn;

        for (prop in proto) {
            fn = proto[prop];

            if (Ext.isFunction(fn) && fn.$ext_getter) {
                fn.call(this);
            }
        }
    },
    doInit: function(app) {
        if (!this._initialized) {
            this.init(app);
            this._initialized = true;
        }
    },
    /**
     * A template method to call when your controller is instantiated to establish a link between the controller and the
     * primary view it is controlling.
     *
     * @param {EXT.DOMAIN.hmp.Application} application
     * @template
     */
    init: Ext.emptyFn,
    /**
     * A template method like {@link #init}, but called after the viewport is created.
     * This is called after the {@link EXT.DOMAIN.hmp.Application#launch launch} method of Application is executed.
     *
     * @param {EXT.DOMAIN.hmp.Application} application
     * @template
     */
    onLaunch: Ext.emptyFn,
    /**
     * Adds listeners to components selected via {@link Ext.ComponentQuery}. Accepts an
     * object containing component paths mapped to a hash of listener functions.
     *
     * In the following example the `updateUser` function is mapped to to the `click`
     * event on a button component, which is a child of the `useredit` component.
     *
     *     Ext.define('AM.controller.Users', {
     *         init: function() {
     *             this.control({
     *                 'useredit button[action=save]': {
     *                     click: this.updateUser
     *                 }
     *             });
     *         },
     *
     *         updateUser: function(button) {
     *             console.log('clicked the Save button');
     *         }
     *     });
     *
     * See {@link Ext.ComponentQuery} for more information on component selectors.
     *
     * @param {String/Object} selectors If a String, the second argument is used as the
     * listeners, otherwise an object of selectors -> listeners is assumed
     * @param {Object} listeners
     */
    control:function (selectors, listeners) {
        EXT.DOMAIN.hmp.Controller.eventbus.control(selectors, listeners, this);
    },

    ref: function(refs) {
        refs = Ext.Array.from(refs);

        var me = this,
            i = 0,
            length = refs.length,
            info, ref, fn;

        me.references = me.references || [];

        for (; i < length; i++) {
            info = refs[i];
            ref  = info.ref;
            fn   = 'get' + Ext.String.capitalize(ref);

            if (!me[fn]) {
                me[fn] = Ext.Function.pass(me.getRef, [ref, info], me);
            }
            me.references.push(ref.toLowerCase());
        }
    },

    /**
     * Registers a {@link #refs reference}.
     * @param {Object} ref
     */
    addRef: function(ref) {
        return this.ref([ref]);
    },

    getRef: function(ref, info, config) {
        this.refCache = this.refCache || {};
        info = info || {};
        config = config || {};

        Ext.apply(info, config);

        if (info.forceCreate) {
            return Ext.ComponentManager.create(info, 'component');
        }

        var me = this,
            cached = me.refCache[ref];

        if (!cached) {
            me.refCache[ref] = cached = Ext.ComponentQuery.query(info.selector)[0];
            if (!cached && info.autoCreate) {
                me.refCache[ref] = cached = Ext.ComponentManager.create(info, 'component');
            }
            if (cached) {
                cached.on('beforedestroy', function() {
                    me.refCache[ref] = null;
                });
            }
        }

        return cached;
    },

    /**
     * Returns true if a {@link #refs reference} is registered.
     * @return {Boolean}
     */
    hasRef: function(ref) {
        return this.references && this.references.indexOf(ref.toLowerCase()) !== -1;
    },
    /**
     * Returns the base {@link EXT.DOMAIN.hmp.Application} for this controller.
     * @return {EXT.DOMAIN.hmp.Application} the application
     */
    getApplication: function(){
        return this.application;
    }
});
