Ext.define('EXT.DOMAIN.hmp.team.PersonPicker', {
    extend:'Ext.container.Container',
    requires:[
        'EXT.DOMAIN.hmp.team.PersonStore'
    ],
    alias:'widget.personpicker',
    /**
     * @cfg {Number} queryDelay
     * The length of time in milliseconds to delay between the start of typing and sending the query to filter the list.
     *
     * Defaults to `500` if `{@link #queryMode} = 'remote'` or `10` if `{@link #queryMode} = 'local'`
     */
    /**
     * @cfg {Number} minChars
     * The minimum number of characters the user must type before autocomplete and {@link #typeAhead} activate.
     *
     * Defaults to `4` if `{@link #queryMode} = 'remote'` or `0` if `{@link #queryMode} = 'local'`,
     * does not apply if `{@link Ext.form.field.Trigger#editable editable} = false`.
     */
    /**
     * @cfg {Boolean} enableRegEx
     * *When {@link #queryMode} is `'local'` only*
     *
     * Set to `true` to have the Picker use the typed value as a RegExp source to filter the store to get possible matches.
     */

    layout:{
        type:'vbox',
        align:'stretch'
    },
    items:[
        {
            xtype:'triggerfield',
            enableKeyEvents:true,
            emptyText:'Search Staff',
            triggerCls:'x-form-clear-trigger'
        },
        {
            xtype:'dataview',
            itemId:'personList',
            store:'persons',
            tpl:new Ext.XTemplate(
                '<ul class="hmp-person-list-ct">' +
                    '<tpl for=".">' +
                    '<li class="hmp-person-list-item">' +
                    '<img src="{photoHref}"/>' +
                    '<span>{name}</span>' +
                    '</li>' +
                    '</tpl>' +
                    '</ul>'
            ),
            itemSelector:'li.hmp-person-list-item',
            emptyText:'No matching staff',
            flex:1,
            overflowY:'auto',
            overflowX:'hidden',
            trackOver:true,
            overItemCls:'x-boundlist-item-over', // borrow some styling from combobox boundlist
            selectedItemCls: 'x-boundlist-selected'
        }
    ],
    initComponent:function () {
        var me = this,
            isLocalMode = true;

        me.items[0].onTriggerClick = function () {
            me.onClickClearQuery.call(me)
        };
        me.items[1].store = Ext.data.StoreManager.containsKey('persons') ? Ext.getStore('persons') : Ext.create('EXT.DOMAIN.hmp.team.PersonStore');

        me.callParent(arguments);

        me.addEvents(
            /**
             * @event beforequery
             * Fires before all queries are processed. Return false to cancel the query or set the queryEvent's cancel
             * property to true.
             *
             * @param {Object} queryEvent An object that has these properties:
             *
             *   - `picker` : EXT.DOMAIN.hmp.team.PersonPicker
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
            'beforequery',

            /**
             * @event select
             * Fires when at least one list item is selected.
             * @param {EXT.DOMAIN.hmp.team.PersonPicker} combo This picker
             * @param {Array} records The selected records
             */
            'select',

            /**
             * @event beforeselect
             * Fires before the selected item is added to the collection
             * @param {EXT.DOMAIN.hmp.team.PersonPicker} picker This picker
             * @param {EXT.DOMAIN.hmp.team.Person} record The selected record
             * @param {Number} index The index of the selected record
             */
            'beforeselect',

            /**
             * @event beforedeselect
             * Fires before the deselected item is removed from the collection
             * @param {EXT.DOMAIN.hmp.team.PersonPicker} picker This picker
             * @param {EXT.DOMAIN.hmp.team.Person} record The deselected record
             * @param {Number} index The index of the deselected record
             */
            'beforedeselect'
        );

        isLocalMode = true;
        if (!Ext.isDefined(me.queryDelay)) {
            me.queryDelay = isLocalMode ? 10 : 500;
        }
        if (!Ext.isDefined(me.minChars)) {
            me.minChars = isLocalMode ? 0 : 4;
        }

        me.doQueryTask = new Ext.util.DelayedTask(me.doRawQuery, me);
    },
    initEvents:function () {
        this.callParent(arguments);

        this.getTextField().on('keyup', this.onKeyUp, this);
    },
    /**
     * Returns the store associated with this PersonPicker.
     * @return {Ext.data.Store} The store
     */
    getStore:function () {
        if (!this.store) {
            this.store = this.getList().getStore();
        }
        return this.store;
    },
    getTextField:function () {
        return this.down('triggerfield');
    },
    getList:function () {
        return this.down('#personList');
    },
    afterRender:function () {
        this.callParent(arguments);
        this.initializeDragZone();
    },
    onBoxReady:function () {
        this.callParent(arguments);
        this.getStore().load();
    },
    /*
     * Here is where we "activate" the DataView.
     * We have decided that each node with the class "patient-source" encapsulates a single draggable
     * object.
     *
     * So we inject code into the DragZone which, when passed a mousedown event, interrogates
     * the event to see if it was within an element with the class "patient-source". If so, we
     * return non-null drag data.
     *
     * Returning non-null drag data indicates that the mousedown event has begun a dragging process.
     * The data must contain a property called "ddel" which is a DOM element which provides an image
     * of the data being dragged. The actual node clicked on is not dragged, a proxy element is dragged.
     * We can insert any other data into the data object, and this will be used by a cooperating DropZone
     * to perform the drop operation.
     */
    initializeDragZone:function () {
        var v = this.getList();
        v.dragZone = Ext.create('Ext.dd.DragZone', v.getEl(), {

//      On receipt of a mousedown event, see if it is within a draggable element.
//      Return a drag data object if so. The data object can contain arbitrary application
//      data, but it should also contain a DOM element in the ddel property to provide
//      a proxy to drag.
            getDragData:function (e) {
                var sourceEl = e.getTarget(v.itemSelector, 10), d;
                if (sourceEl) {
                    d = sourceEl.cloneNode(true);
                    d.id = Ext.id();
                    return v.dragData = {
                        sourceEl:sourceEl,
                        repairXY:Ext.fly(sourceEl).getXY(),
                        ddel:d,
                        person:v.getRecord(sourceEl)
                    };
                }
            },

//      Provide coordinates for the proxy to slide back to on failed drag.
//      This is the original XY coordinates of the draggable element.
            getRepairXY:function () {
                return this.dragData.repairXY;
            }
        });
    },
    // store the last key and doQuery if relevant
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
        this.doQuery(this.getTextField().getRawValue(), false, true);
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
                        me.getList().refresh();
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
            picker = me.getList(),
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
    onClickClearQuery:function () {
        this.getTextField().setValue("");
        this.doRawQuery();
    }
});
