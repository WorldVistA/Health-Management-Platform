Ext.define('org.osehra.hmp.team.TeamAssignment', {
    extend:'Ext.data.Model',
    fields:[
        {
            name:'positionUid',
            type:'string'
        },
        {
            name:'positionName',
            type:'string'
        },
        {
            name:'personUid',
            type:'string'
        },
        {
            name:'personName',
            type:'string'
        },
        {
            name:'personPhotoHref',
            type:'string'
        }
    ],
    belongsTo: 'org.osehra.hmp.team.Team'
});
