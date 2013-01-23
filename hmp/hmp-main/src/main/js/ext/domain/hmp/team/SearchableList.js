/**
 * Like an editable ComboBox with its popUp stuck open.
 */
Ext.define('org.osehra.hmp.team.SearchableList', {
    extend:'Ext.container.Container',
    alias:'widget.searchablelist',
    /**
     * @cfg {Ext.data.Store/String/Array} store (required)
     * The data source to which this list is bound. Acceptable values for this property are:
     *
     *   - **any {@link Ext.data.Store Store} subclass**
     *   - **an {@link Ext.data.Store#storeId ID of a store}**
     *   - **an Array** : Arrays will be converted to a {@link Ext.data.Store} internally, automatically generating
     *     {@link Ext.data.Field#name field names} to work with all data components.
     *
     *     - **1-dimensional array** : (e.g., `['Foo','Bar']`)
     *
     *       A 1-dimensional array will automatically be expanded (each array item will be used for both the combo
     *       {@link #valueField} and {@link #displayField})
     *
     *     - **2-dimensional array** : (e.g., `[['f','Foo'],['b','Bar']]`)
     *
     *       For a multi-dimensional array, the value in index 0 of each item will be assumed to be the combo
     *       {@link #valueField}, while the value at index 1 is assumed to be the combo {@link #displayField}.
     *
     * See also {@link #queryMode}.
     */
    /**
     * @cfg {String} displayField
     * The underlying {@link Ext.data.Field#name data field name} to bind to this list.
     *
     * See also `{@link #valueField}`.
     */
    displayField:'text',

    /**
     * @cfg {Object} listConfig
     * An optional set of configuration properties that will be passed to the {@link Ext.view.BoundList}'s constructor.
     * Any configuration that is valid for BoundList can be included. Some of the more useful ones are:
     *
     *   - {@link Ext.view.BoundList#cls cls} - defaults to empty
     *   - {@link Ext.view.BoundList#emptyText emptyText} - defaults to empty string
     *   - {@link Ext.view.BoundList#itemSelector itemSelector} - defaults to the value defined in BoundList
     *   - {@link Ext.view.BoundList#loadingText loadingText} - defaults to `'Loading...'`
     *   - {@link Ext.view.BoundList#minWidth minWidth} - defaults to `70`
     *   - {@link Ext.view.BoundList#maxWidth maxWidth} - defaults to `undefined`
     *   - {@link Ext.view.BoundList#maxHeight maxHeight} - defaults to `300`
     *   - {@link Ext.view.BoundList#resizable resizable} - defaults to `false`
     *   - {@link Ext.view.BoundList#shadow shadow} - defaults to `'sides'`
     *   - {@link Ext.view.BoundList#width width} - defaults to `undefined` (automatically set to the width of the ComboBox
     *     field if {@link #matchFieldWidth} is true)
     */
    layout:{
        type:'vbox',
        align:'stretch'
    },
    items:[
        {
            xtype:'triggerfield',
            enableKeyEvents:true,
            triggerCls:'x-form-clear-trigger'
        },
        {
            xtype:'boundlist',
            emptyText: 'No matching items',
            flex:1,
            minWidth:70,
            minHeight:300,
            maxHeight:300
        }
    ],
    constructor:function (config) {
        this.initConfig(config);
        return this.callParent(arguments);
    },
    initComponent:function () {
        var me = this;
        // apply configs to triggerfield
        Ext.apply(me.items[0], {
            emptyText:me.emptyText,
            onTriggerClick:function () {
                me.onClickClearQuery.call(me)
            }
        });

        // apply configs to boundlist
        Ext.apply(me.items[1], {
            store:me.store,
            displayField:me.displayField
        });
        Ext.apply(me.items[1], me.listConfig);

        isLocalMode = true;
        if (!Ext.isDefined(me.queryDelay)) {
            me.queryDelay = isLocalMode ? 10 : 500;
        }
        if (!Ext.isDefined(me.minChars)) {
            me.minChars = isLocalMode ? 0 : 4;
        }

        me.callParent(arguments);

        me.addEvents(
            /**
             * @event beforequery
             * Fires before all queries are processed. Return false to cancel the query or set the queryEvent's cancel
             * property to true.
             *
             * @param {Object} queryEvent An object that has these properties:
             *
             *   - `picker` : org.osehra.hmp.team.PersonPicker
             *
             *     This combo box
             *
             *   - `query` : String
             *
             *     The query string
             *
             *   - `forceAll` : Boolean
             *
             *     True to force "all" query
             *
             *   - `cancel` : Boolean
             *
             *     Set to true to cancel the query
             */
            'beforequery');

        me.relayEvents(me.down('boundlist'), ['beforedeselect', 'beforeselect', 'deselect', 'highlightitem', 'select', 'selectionchange', 'unhighlightitem']);

        me.doQueryTask = new Ext.util.DelayedTask(me.doRawQuery, me);
    },
    initEvents:function () {
        this.callParent(arguments);

        this.mon(this.down('triggerfield'), 'keyup', this.onKeyUp, this);
    },
    /**
     * Returns the store associated with this list.
     * @return {Ext.data.Store} The store
     */
    getStore:function () {
        return this.down('boundlist').getStore();
    },
    /**
     * @private
     * store the last key and doQuery if relevant
     */
    onKeyUp:function (textfield, e) {
        var me = this,
            key = e.getKey();

        if (!me.disabled) {
            me.lastKey = key;
            // we put this in a task so that we can cancel it if a user is
            // in and out before the queryDelay elapses

            // perform query w/ any normal key or backspace or delete
            if (!e.isSpecialKey() || key == e.BACKSPACE || key == e.DELETE) {
                me.doQueryTask.delay(me.queryDelay);
            }
        }
    },
    doRawQuery:function () {
        this.doQuery(this.down('triggerfield').getRawValue(), false, true);
    },
    /**
     * Executes a query to filter the dropdown list. Fires the {@link #beforequery} event prior to performing the query
     * allowing the query action to be canceled if needed.
     *
     * @param {String} queryString The SQL query to execute
     * @param {Boolean} [forceAll=false] `true` to force the query to execute even if there are currently fewer characters in
     * the field than the minimum specified by the `{@link #minChars}` config option. It also clears any filter
     * previously saved in the current store.
     * @param {Boolean} [rawQuery=false] Pass as true if the raw typed value is being used as the query string. This causes the
     * resulting store load to leave the raw value undisturbed.
     * @return {Boolean} true if the query was permitted to run, false if it was cancelled by a {@link #beforequery}
     * handler.
     */
    doQuery:function (queryString, forceAll, rawQuery) {
        queryString = queryString || '';

        // store in object and pass by reference in 'beforequery'
        // so that client code can modify values.
        var me = this,
            qe = {
                query:queryString,
                forceAll:forceAll,
                picker:me,
                cancel:false
            },
            store = me.getStore(),
            isLocalMode = true,
            needsRefresh;

        if (me.fireEvent('beforequery', qe) === false || qe.cancel) {
            return false;
        }

        // get back out possibly modified values
        queryString = qe.query;
        forceAll = qe.forceAll;

        // query permitted to run
        if (forceAll || (queryString.length >= me.minChars)) {
            // make sure they aren't querying the same thing
            if (!me.queryCaching || me.lastQuery !== queryString) {
                me.lastQuery = queryString;

                if (isLocalMode) {
                    // forceAll means no filtering - show whole dataset.
                    store.suspendEvents();
                    needsRefresh = me.clearFilter();
                    if (queryString || !forceAll) {
                        me.activeFilter = new Ext.util.Filter({
                            root:'data',
                            property:'name',
                            value:me.enableRegEx ? new RegExp(queryString) : queryString
                        });
                        store.filter(me.activeFilter);
                        needsRefresh = true;
                    } else {
                        delete me.activeFilter;
                    }
                    store.resumeEvents();
                    if (me.rendered && needsRefresh) {
                        me.down('boundlist').refresh();
                    }
                } else {
                    // Set flag for onLoad handling to know how the Store was loaded
                    me.rawQuery = rawQuery;

                    // In queryMode: 'remote', we assume Store filters are added by the developer as remote filters,
                    // and these are automatically passed as params with every load call, so we do *not* call clearFilter.
                    if (me.pageSize) {
                        // if we're paging, we've changed the query so start at page 1.
                        me.loadPage(1);
                    } else {
                        store.load({
                            params:me.getParams(queryString)
                        });
                    }
                }
            }

            // Clear current selection if it does not match the current value in the field
//            if (me.getRawValue() !== me.getDisplayValue()) {
//                me.ignoreSelection++;
//                me.picker.getSelectionModel().deselectAll();
//                me.ignoreSelection--;
//            }

            if (isLocalMode) {
                me.doAutoSelect();
            }
        }
        return true;
    },
    /**
     * Clears any previous filters applied by the picker to the store
     * @private
     * @return {Boolean} True if a filter was removed
     */
    clearFilter:function () {
        var store = this.getStore(),
            filter = this.activeFilter,
            filters = store.filters,
            remaining;

        if (filter) {
            if (filters.getCount() > 1) {
                // More than 1 existing filter
                filters.remove(filter);
                remaining = filters.getRange();
            }
            store.clearFilter(true);
            if (remaining) {
                store.filter(remaining);
            }
        }
        return !!filter;
    },
    /**
     * @private
     * Highlights the first matching item.
     */
    doAutoSelect:function () {
        var me = this,
            picker = me.down('boundlist'),
            lastSelected, itemNode;
        if (me.getStore().getCount() > 0) {
            // Highlight the last selected item and scroll it into view
            lastSelected = picker.getSelectionModel().lastSelected;
            itemNode = picker.getNode(lastSelected || 0);
            if (itemNode) {
                picker.highlightItem(itemNode);
            }
        }
    },
    // private
    onClickClearQuery:function () {
        this.down('triggerfield').setValue("");
        this.doRawQuery();
    },
    setEmptyText:function(emptyText) {
        var field = this.down('triggerfield');
        field.emptyText = emptyText;
        this.emptyText = emptyText;
        field.reset();
    }
});
