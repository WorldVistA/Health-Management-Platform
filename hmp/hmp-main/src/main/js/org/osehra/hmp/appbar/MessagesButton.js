Ext.define('org.osehra.hmp.appbar.MessagesButton', {
    extend:'Ext.button.Button',
    alias:'widget.messagesbutton',
    text:'1',
//    iconCls: 'hmp-messages-button-icon',
//    overCls: 'hmp-messages-button-icon-over',
//    pressedCls: 'hmp-messages-button-icon-pressed',
    tooltip:'Messages',
    style: 'background-color:red;border-radius:4;text-color:white',
    menu:{
        componentCls:'hmp-popupbutton-menu',
        plain:true,
        minWidth:400,
        padding:'6 0 6 0',
        style:'background-color: #f5f5f5',
        items:[
            // These are prototypical only
            {
                xtype:'component',
                html:'<div style="border-top:1px solid #bbbbbb;padding:6"><div class="hmp-label">THIRTYTHREE AVIVAUSER pinged you</div><div style="padding:12 6 0 6;font-weight: bold">Please f/u with result of Lymes test.</div></div>'
            },
            {
                xtype:'component',
                html:'<div style="border-top:1px solid #bbbbbb;padding:6"><div class="hmp-label">TEN VEHU added you to RED TEAM</div><div style="padding:12 6 0 6">7 healthcare associates, 256 patients, 12 team to-dos</div></div>'
            },
            {
                xtype:'component',
                html:'<div style="border-top:1px solid #bbbbbb;padding:6"><div class="hmp-label">THIRTYTHREE AVIVAUSER pinged you</div><div style="padding:12 6 0 6">Wanna grab lunch in the cafeteria at 1:30?</div></div>'
            }
        ],
        dockedItems:[
            {
                xtype:'toolbar',
                ui:'plain',
                dock:'top',
                padding:6,
                items:[
                    {
                        xtype:'button',
                        ui:'link',
                        text:'View all messages'
                    }
                ]
            }
        ]
    }
});
