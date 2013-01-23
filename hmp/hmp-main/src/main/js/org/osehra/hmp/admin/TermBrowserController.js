/**
 * Controls behavior of {@link org.osehra.hmp.admin.TermBrowserPanel}
 */
Ext.define('org.osehra.hmp.admin.TermBrowserController', {
    extend:'org.osehra.hmp.Controller',
    requires:[
        'org.osehra.hmp.admin.TermBrowserTree',
        'org.osehra.hmp.admin.TermBrowserPanel'
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
