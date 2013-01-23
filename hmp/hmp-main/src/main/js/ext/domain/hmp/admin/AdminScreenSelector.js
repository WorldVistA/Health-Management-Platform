Ext.define('org.osehra.hmp.admin.AdminScreenSelector', {
    extend:'Ext.tree.Panel',
    requires:[
        'org.osehra.hmp.AppContext'
    ],
    id:'adminScreenSelector',
    title:'HMP Admin',
    minWidth:250,
    width:250,
    bodyPadding:4,
//    stateful:true,
//    stateEvent:['select'],
    rootVisible:false,
    lines:false,
    useArrows:true,
    store:Ext.create('Ext.data.TreeStore', {
        storeId:'adminScreens',
        fields:['text', 'view'],
        root:{
            expanded:true,
            children:[
                {
                    text:'Virtual Patient Record System',
                    expanded:true,
                    children:[
                        { leaf:true, text:'Sync Patients', view:'sync' },
                        { leaf:true, text:'Browse Patients', view:'vpr-patients' },
                        { leaf:true, text:'Sync Error Trap', view:'sync-errors' }
                    ]
                },
                {
                    text:'VistA RPCs',
                    expanded:true,
                    children:[
                        { leaf:true, text:"Call a VistA RPC", view:'rpc-call' },
                        { leaf:true, text:"VistA RPC Log", view:'rpc-log'}
                    ]
                },
                {
                	text:'Frames',
                	expanded:true,
                	children:[
            	        { leaf: true, text: 'List Frames', view: 'frame-list'},
            	        { leaf: true, text: 'Drools Editor', view: 'drools-edit'},
            	        { leaf: true, text: 'Terminology Browser', view: 'term-browse'}
    	            ]
                },
//                {
//                    text:'Users/Roles/Teams',
//                    expanded:true,
//                    children:[
//                        { leaf: true, text: 'User Class Management', view: 'user-classes'}
//                    ]
//                },
//                {
//                    text:'Patient Data Cache',
//                    expanded:true,
//                    children:[
//                        { leaf:true, text:"JDS Log" }
//                    ]
//                },
                {
                    text:'Environment',
                    expanded:true,
                    children:[
                        {
                            leaf:true,
                            text:"HMP Properties",
                            view:'hmp-properties'
                        },
                        {
                            leaf:true,
                            text:"System Properties",
                            view:'system-properties'
                        },
                        {
                            leaf:true,
                            text:"Environment Variables",
                            view:'environment-variables'
                        }
                    ]
                }
            ]
        }
    })
});
