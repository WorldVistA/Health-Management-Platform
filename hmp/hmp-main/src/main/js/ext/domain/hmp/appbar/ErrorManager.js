/**
 *
 * TODO: The warning/error window should have a close [x] icon if there is no display timeout.
 * TODO: The warning/error window should have a stack of messages/timeouts.  So that one message/error does not clobber others.
 */
Ext.define('EXT.DOMAIN.hmp.appbar.ErrorManager', {
    singleton:true,
    requires: [
        'EXT.DOMAIN.hmp.appbar.ErrorWindow'
    ],
    /**
     * The list of errors
     * @private
     */
    errors:[],
    constructor:function (cfg) {
        var me = this;

        me.initConfig(cfg);

        // the error/warning message window
        this.msgwin = Ext.create('Ext.Component', {
            floating:true,
            frame:true,
            itemId:'msgwinWarnId',
            shadow:false,
            padding:'3 10 3 10',
            style:{
                'fontWeight':'bold',
                'border':'1px solid #F0C36D'
            },
            html:'[Message]'
        });

        this.msgwin.on('render', function(win) {
            // attach a click handler to the element on render
            win.getEl().on('click', function () {
                if (win.details) {
                    EXT.DOMAIN.hmp.appbar.ErrorWindow.show();
                }
            });
        });

        // task for hiding warning window if needed
        this.warnTask = new Ext.util.DelayedTask(function () {
            me.msgwin.hide();
        });
    },
    warn:function (str, delay, details) {
        if (delay === undefined) {
            delay = 0;
        }
        this.msgwin.title = str;
        this.msgwin.details = details;
        this.msgwin.update(str);
        if (!this.msgwin.rendered || this.msgwin.isHidden()) {
            var appbar = Ext.ComponentQuery.query('#AppBar')[0];
            if (Ext.isDefined(appbar)) {
                this.msgwin.show(appbar.getEl());
                this.msgwin.alignTo(appbar, 't-b', [0, -5]);
            } else {
                this.msgwin.show();
            }
        }
        this.msgwin.removeCls('hmp-error-window');
        this.msgwin.addCls('hmp-warning-window');
        if (delay == 0) {
            this.warnTask.cancel();
        } else {
            this.warnTask.delay(delay);
        }
    },
    error:function (str, delay, details) {
        this.warn(str, delay, details);
        this.msgwin.addCls('hmp-warning-window');
        this.msgwin.addCls('hmp-error-window');
    }
});
