Ext.define('EXT.DOMAIN.cpe.search.SearchResultsPanel', {
    extend: 'Ext.grid.Panel',
    requires: [
        'EXT.DOMAIN.cpe.search.SummaryItem'
    ],
    mixins: {
        patientaware: 'EXT.DOMAIN.hmp.PatientAware'
    },
    alias: 'widget.searchresults',
    layout: 'fit',
    title: 'Search Results',
    store: Ext.create('Ext.data.Store', {
        fields: ['uid', 'summary', 'type', 'kind', 'datetime', 'datetimeFormatted', 'where', 'highlight', 'count'],
        proxy: {
            type: 'ajax',
            extraParams: {
                format: 'json'
            },
            reader: {
                type: 'json',
                root: 'data.items',
                totalProperty: 'totalItems'
            }
        }
    }),
//    hideHeaders: true,
    emptyText: 'No matches found.',
    columns:[
        { header:'',
            xtype:'templatecolumn',
            flex:1,
            tpl:'<tpl for=".">' +
                    '<div class="cpe-search-result">' +
                    '<div class="cpe-search-result-summary">{summary}<tpl if="count &gt; 0"><span class="cpe-search-result-count">({count} more)</span></tpl></div>' +
                    '<tpl if="highlight.length != \'\'"><div class="cpe-search-result-highlight">...{highlight}...</div></tpl>' +
                    '<div class="cpe-search-result-attributes">{datetimeFormatted} - {kind} - {where}</div>' +
                    '</div>' +
                    '</tpl>'
        }
    ],
    listeners: {
        beforepatientchange: function(cmp, pid) {
            this.getStore().removeAll();
        },
        patientchange: function(pid) {
            var me = this;
            this.pid = pid;
            if (pid != 0) {
                me.setDisabled(false);
                var store = me.getStore();
                store.getProxy().url = '/vpr/v1/' + pid + '/search';
                if (store.getProxy().extraParams.query !== undefined) store.load();
            } else {
                me.setDisabled(true);
            }
        }
    },
    initComponent: function() {
        var me = this;

        me.callParent(arguments);

        var selModel = me.getComponent(0).getSelectionModel();

        me.relayEvents(selModel, [
        /**
         * @event selectionchange
         * @alias Ext.selection.Model#selectionchange
         */
            'selectionchange'
        ]);
    },
    onBoxReady:function() {
        this.initPatientContext();
        this.callParent(arguments);
    },
    searchFor: function(text) {
        var store = this.getStore();
        store.getProxy().extraParams.query = text;
        store.load();
    }
});
