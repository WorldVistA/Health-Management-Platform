/**
 * Controls behavior of {@link EXT.DOMAIN.hmp.admin.TermBrowserPanel}
 */
Ext.define('EXT.DOMAIN.hmp.admin.TermBrowserController', {
    extend:'EXT.DOMAIN.hmp.Controller',
    requires:[
        'EXT.DOMAIN.hmp.admin.TermBrowserTree',
        'EXT.DOMAIN.hmp.admin.TermBrowserPanel'
    ],
    refs: [
        {
            ref: 'termSearchField',
            selector: '#termSearchField'
        },
        {
            ref: 'termSearchTabs',
            selector: '#termSearchTabs'
        }
    ],
    init:function () {
//        console.log(Ext.getClassName(this) + ".init()");
        var me = this;

        me.control({
            '#termSearchField': {
                'select': me.executeTermSearch
            }
        });
    },
//    onLaunch:function() {
//        this.getTermSearchField().getStore().load();
//    },
    executeTermSearch: function() {
        var me = this;

        var target = me.getTermSearchTabs();
        var searchText = me.getTermSearchField().getValue();
        var tab = target.add({xtype: 'component', title: 'Search: ' + searchText, closable: true, loader: {url: '/term/display?urn='+searchText, renderer: 'html', autoLoad: true}});
//    	tab = target.add({xtype: 'termbrowsertree', title: 'Search: ' + searchText, searchText: searchText, closable: true});
        target.setActiveTab(tab);
    }
});
