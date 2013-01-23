Ext.define('org.osehra.cpe.roster.RosterContext', {
    singleton:true,
    config: {
        rosterInfo: {}
    },
    constructor: function(cfg) {
        this.initConfig(cfg);
    }
});
