Ext.onReady(function() {
    Ext.require('EXT.DOMAIN.hmp.supports');

    // register date format patterns
    Ext.define("Ext.Date_patterns", {
        override: "Ext.Date",
        patterns: {
            ISO8601Long:"Y-m-d H:i:s",
            ISO8601Short:"Y-m-d",
            ShortDate: "n/j/Y",
            LongDate: "l, F d, Y",
            FullDateTime: "l, F d, Y g:i:s A",
            MonthDay: "F d",
            ShortTime: "g:i A",
            LongTime: "g:i:s A",
            SortableDateTime: "Y-m-d\\TH:i:s",
            UniversalSortableDateTime: "Y-m-d H:i:sO",
            YearMonth: "F, Y",
            HL7: "YmdHis.u"
        }
    });

    // override default Ext.LoadMask shadow
    Ext.override(Ext.LoadMask, {
        floating: {
            shadow: false
        },
        /* Hack to fix a bug in 4.0.7 documented here: http://www.sencha.com/forum/showthread.php?152875-Combobox-loadMask-loop&p=667019&viewfull=1#post667019
         * (This has to do with comboboxen ending up with a stuck "loading.." mask when the store underneath is load()'ed.)
         */
        onHide: function() {
            this.callParent();
        }
    });

    // override default labelSeparator (semi-colon's are so 1990s)
    Ext.override(Ext.form.Labelable, {
        labelSeparator: ''
    });
    Ext.override(Ext.form.Panel, {
        layout:'anchor',
//        defaults:{
//            anchor:'100%'
//        },
        fieldDefaults: {
            labelSeparator: '',
            labelAlign: 'right'
        }
    });

    // enables setting filters in TreeStores
    Ext.override(Ext.data.TreeStore, {

        hasFilter: false,

        filter: function(filters, value) {

            if (Ext.isString(filters)) {
                filters = {
                    property: filters,
                    value: value
                };
            }

            var me = this,
                decoded = me.decodeFilters(filters),
                i = 0,
                length = decoded.length;

            for (; i < length; i++) {
                me.filters.replace(decoded[i]);
            }

            Ext.Array.each(me.filters.items, function(filter) {
                Ext.Object.each(me.tree.nodeHash, function(key, node) {
                    if (filter.filterFn) {
                        if (!filter.filterFn(node)) node.remove();
                    } else {
                        if (node.data[filter.property] != filter.value) node.remove();
                    }
                });
            });
            me.hasFilter = true;

        },

        clearFilter: function() {
            var me = this;
            me.filters.clear();
            me.hasFilter = false;
            me.load();
        },

        isFiltered: function() {
            return this.hasFilter;
        }

    });
});
