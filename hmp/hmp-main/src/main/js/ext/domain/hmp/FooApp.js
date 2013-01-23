Ext.define('org.osehra.hmp.FooApp', {
    extend:'org.osehra.hmp.Application',
    requires:[
        'org.osehra.hmp.Viewport',
        'org.osehra.hmp.containers.SlidingCardLayout'
    ],
    launch:function () {
        Ext.create('org.osehra.hmp.Viewport', {
            items:[
                {
                    xtype:'panel',
                    itemId: 'foo',
                    region:'center',
                    title:'Foo',
                    layout: 'slidingcard',
                    bodyPadding: 10,
                    dockedItems:[
                        {
                            xtype:'toolbar',
                            dock:'top',
                            items:[
                                {
                                    text:'Prev',
                                    handler:function () {
                                        this.up('viewport').down('#foo').getLayout().prev();
                                    }
                                },
                                '->',
                                {
                                    text:'Next',
                                    handler:function () {
                                        this.up('viewport').down('#foo').getLayout().next();
                                    }
                                }
                            ]
                        }
                    ],
                    items:[
                        {
                            xtype:'panel', title:'1', html:'testing'
                        },
                        {
                            xtype:'gridpanel', title:'2', columns:[
                            {
                                text:'Label', dataIndex:'label'
                            }
                        ], store:{
                            fields:['label'], data:[
                                {
                                    label:'a'
                                },
                                {
                                    label:'b'
                                },
                                {
                                    label:'c'
                                },
                                {
                                    label:'d'
                                },
                                {
                                    label:'e'
                                },
                                {
                                    label:'f'
                                },
                                {
                                    label:'g'
                                },
                                {
                                    label:'h'
                                },
                                {
                                    label:'i'
                                },
                                {
                                    label:'j'
                                },
                                {
                                    label:'k'
                                },
                                {
                                    label:'l'
                                },
                                {
                                    label:'m'
                                },
                                {
                                    label:'n'
                                },
                                {
                                    label:'o'
                                },
                                {
                                    label:'p'
                                },
                                {
                                    label:'q'
                                },
                                {
                                    label:'r'
                                },
                                {
                                    label:'s'
                                },
                                {
                                    label:'t'
                                },
                                {
                                    label:'u'
                                },
                                {
                                    label:'v'
                                },
                                {
                                    label:'w'
                                },
                                {
                                    label:'x'
                                },
                                {
                                    label:'y'
                                },
                                {
                                    label:'z'
                                },
                                {
                                    label:'a'
                                },
                                {
                                    label:'b'
                                },
                                {
                                    label:'c'
                                },
                                {
                                    label:'d'
                                },
                                {
                                    label:'e'
                                },
                                {
                                    label:'f'
                                },
                                {
                                    label:'g'
                                },
                                {
                                    label:'h'
                                },
                                {
                                    label:'i'
                                },
                                {
                                    label:'j'
                                },
                                {
                                    label:'k'
                                },
                                {
                                    label:'l'
                                },
                                {
                                    label:'m'
                                },
                                {
                                    label:'n'
                                },
                                {
                                    label:'o'
                                },
                                {
                                    label:'p'
                                },
                                {
                                    label:'q'
                                },
                                {
                                    label:'r'
                                },
                                {
                                    label:'s'
                                },
                                {
                                    label:'t'
                                },
                                {
                                    label:'u'
                                },
                                {
                                    label:'v'
                                },
                                {
                                    label:'w'
                                },
                                {
                                    label:'x'
                                },
                                {
                                    label:'y'
                                },
                                {
                                    label:'z'
                                }
                            ]
                        }
                        },
                        {
                            xtype:'panel', title:'3', html:'One last panel'
                        }
                    ]
                }
            ]
        });
    }
});

/**
 Ext.define('carouselTb', {
 extend:'Ext.toolbar.Toolbar', alias:'widget.carouseltb', directionals:true, initComponent:function () {
 var me = this;

 me.items = [
 {
 xtype:'tbfill'
 },
 {
 xtype:'tbfill'
 }
 ]

 me.callParent(arguments);
 }, handleCarouselEvents:function (carousel) {
 var me = this;
 me.relayEvents(carousel, ['carouselchange']);
 me.on('carouselchange', me.onCarouselChange, me, {buffer:20});
 }, onCarouselChange:function (carousel, item) {
 var me = this;
 var navSprites = me.down('draw').surface.getGroup('carousel');
 navSprites.setAttributes({opacity:.2}, true);
 var i = carousel.items.indexOf(item);
 navSprites.each(function (s) {
 if (s.index == i) {
 s.animate({
 to:{
 opacity:.7
 }
 });
 }
 });
 }, onRender:function () {
 var me = this;

 var prev = {
 text:'<', handler:function () {
 me.ownerCt.down('carousel').previousChild();
 }
 };

 var next = {
 text:'>', handler:function () {
 me.ownerCt.down('carousel').nextChild();
 }
 };

 Ext.suspendLayouts();
 if (me.directionals) {
 me.insert(0, prev);
 me.insert(me.items.items.length, next);
 }

 var index = me.items.indexOf(me.down('tbfill'));
 var circles = [];
 var x = 0;
 var i = 0;
 Ext.each(me.ownerCt.down('carousel').items.items, function (item) {
 var config = {
 type:'circle', x:x, y:0, index:i, radius:1, fill:'black', opacity:i == 0 ? .7 : .2, group:'carousel'
 }
 circles.push(config);
 x += 3;
 i++;
 });
 me.insert(index + 1, {
 xtype:'draw', height:12, items:circles
 });

 Ext.resumeLayouts();

 Ext.defer(function () {
 var c = me.down('draw').surface.getGroup('carousel');
 c.each(function (s) {
 s.on({
 click:function (s) {
 c.setAttributes({opacity:.2}, true);
 var carousel = me.ownerCt.down('carousel');
 carousel.showChild(carousel.items.items[s.index]);
 }
 });
 });
 }, 2);

 var carousel = me.ownerCt.down('carousel');
 if (carousel) {
 me.handleCarouselEvents(carousel);
 }

 me.callParent(arguments);
 }
 });

 Ext.widget('viewport', {
 layout:'fit', items:[
 {
 xtype:'panel', items:[
 {
 xtype:'carousel', items:[
 {
 xtype:'panel', title:'1', html:'testing'
 },
 {
 xtype:'gridpanel', title:'2', columns:[
 {
 text:'Label', dataIndex:'label'
 }
 ], store:{
 fields:['label'], data:[
 {
 label:'a'
 },
 {
 label:'b'
 },
 {
 label:'c'
 },
 {
 label:'d'
 },
 {
 label:'e'
 },
 {
 label:'f'
 },
 {
 label:'g'
 },
 {
 label:'h'
 },
 {
 label:'i'
 },
 {
 label:'j'
 },
 {
 label:'k'
 },
 {
 label:'l'
 },
 {
 label:'m'
 },
 {
 label:'n'
 },
 {
 label:'o'
 },
 {
 label:'p'
 },
 {
 label:'q'
 },
 {
 label:'r'
 },
 {
 label:'s'
 },
 {
 label:'t'
 },
 {
 label:'u'
 },
 {
 label:'v'
 },
 {
 label:'w'
 },
 {
 label:'x'
 },
 {
 label:'y'
 },
 {
 label:'z'
 },
 {
 label:'a'
 },
 {
 label:'b'
 },
 {
 label:'c'
 },
 {
 label:'d'
 },
 {
 label:'e'
 },
 {
 label:'f'
 },
 {
 label:'g'
 },
 {
 label:'h'
 },
 {
 label:'i'
 },
 {
 label:'j'
 },
 {
 label:'k'
 },
 {
 label:'l'
 },
 {
 label:'m'
 },
 {
 label:'n'
 },
 {
 label:'o'
 },
 {
 label:'p'
 },
 {
 label:'q'
 },
 {
 label:'r'
 },
 {
 label:'s'
 },
 {
 label:'t'
 },
 {
 label:'u'
 },
 {
 label:'v'
 },
 {
 label:'w'
 },
 {
 label:'x'
 },
 {
 label:'y'
 },
 {
 label:'z'
 }
 ]
 }
 },
 {
 xtype:'panel', title:'3', html:'One last panel'
 }
 ]
 }
 ], dockedItems:[
 {
 xtype:'carouseltb', dock:'top'
 }
 ]
 }
 ]
 });
 */
