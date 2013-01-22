Ext.define('EXT.DOMAIN.cpe.search.SearchPanel', {
    extend: 'Ext.panel.Panel',
    requires: [
        'EXT.DOMAIN.cpe.search.SuggestBox',
        'EXT.DOMAIN.cpe.search.SearchResultsPanel',
        'EXT.DOMAIN.cpe.search.SearchDetailPanel'
    ],
    alias: 'widget.searchpanel',
    frame: false,
    border: 0,
    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'top',
            ui: 'plain',
            border: 0,
            padding: '20 0 20 0',
            items: [
                '->',
                {
                    xtype: 'suggestbox',
                    itemId: 'searchBox',
					width: 400
                },
                {
                    xtype: 'button',
                    itemId: 'searchButton',
                    text: 'Search'
                },
                '->'
            ]
        }
    ],
    layout: 'border',
    items: [
        {
            xtype: 'searchresults',
            region: 'center',
            width: '40%'
        },
        {
            xtype: 'searchdetail',
            region: 'east',
            width: '60%',
            split: true
        }
    ],
    initComponent: function() {
        var me = this;
        me.callParent(arguments);

        me.down('#searchButton').on('click', me.search, me);
        me.down('#searchBox').on('specialkey', function(field, e) {
            if (e.getKey() == e.ENTER) {
                me.search();
            }
        }, me);
        me.down('#searchBox').on('keypress', function(field, event) {

        }, me);

        var detail = me.getDetailPanel();
        detail.setDecorateFn(Ext.bind(this.highlightDetail, this));

        /*
         * For some reason I can't figure out, this event gets fired twice for each selection on the grid.
         * This results in a double-call to the loader on the panel, and that gives us a nice JS exception from deep within the bowels of EXT.
         * So I've made a condition not to call it twice if the "appliedItem" has already been set.
         */
        me.getSearchResultsPanel().on('selectionchange', function(selModel, selected) {
            if (selected.length == 0) {
            	if(detail.appliedItem!=null) {
                	detail.applyDetailItem(null);
                	detail.appliedItem = null;
            	}
            } else {
            	if(detail.appliedItem!=selected[0]) {
                    detail.applyDetailItem(selected[0]);
                    detail.appliedItem = selected[0];
            	}
            }
        }, me);
    },

    getSearchResultsPanel: function() {
        return this.down('searchresults');
    },

    getDetailPanel: function() {
        return this.down('searchdetail');
    },

    getSearchTerm: function() {
        return this.down('#searchBox').getValue();
    },

    search: function() {
        var searchTerm = this.getSearchTerm();
        this.getSearchResultsPanel().searchFor(searchTerm);
    },
    highlightDetail: function(text) {
        var searchTerm = this.getSearchTerm();
        if (searchTerm) {
            text = this.highlightMatches(text, searchTerm);
        }
        return text;
    },
    highlightMatches: function(text, searchTerm) {
        var regex = new RegExp(searchTerm, "gim");
        var matches = text.match(regex);
        if (!matches) return text;
        var splits = text.split(regex);
        var matchNum = 0;
        var matchTotal = matches.length;
        var result = '';
        for (var i = 0; i <= splits.length - 1; i++) {
            result = result + splits[i];
            if (matchNum < matchTotal) result = result + '<span class="cpe-search-term-match">' + matches[matchNum] + '</span>';
            matchNum++
        }
        if (result == '') {
            result = text
        }
        return result;
    }
});
