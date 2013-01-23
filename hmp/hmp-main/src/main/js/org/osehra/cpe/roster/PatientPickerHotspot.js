Ext.define('org.osehra.cpe.roster.PatientPickerHotspot', {
    requires: [
        'org.osehra.cpe.roster.RosterContext'
    ],
	extend: 'Ext.panel.Panel',
	alias: 'widget.pphotspot',
    hidden: true,
    cls: 'hmp-pphotspot',
    layout: 'fit',
    degrot: 0, // North and South are fine with this.

    refreshHeaderText:function () {
        var me = this;
        if (this.region == 'west') {
            this.degrot = 270;
        }
        else if (this.region == 'east') {
            this.degrot = 90;
        }
        this.removeAll();
        this.add(
            {
                xtype:'draw',
                viewBox:'false',
                autoSize:true,
                padding:0,
                width:me.el.dom.clientWidth,
                cls:'hmp-pphotspot-title',
                height:me.el.dom.clientHeight,
                items:[
                    {
                        itemId:'pphotspot-roster-label',
                        type:'text',
                        text:org.osehra.cpe.roster.RosterContext.getRosterInfo().name || '<No Roster Selected>',
                        rotate:{
                            degrees:me.degrot
                        }
                    }
                ]
            }
        );
    }
});
