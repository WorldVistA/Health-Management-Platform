Ext.define('EXT.DOMAIN.hmp.team.PatientField', {
    extend:'Ext.form.field.ComboBox',
    alias: 'widget.patientfield',
    store:Ext.create('Ext.data.Store', {
        fields:['name', 'id'],
        proxy:{
            type:'ajax',
            url:'/roster/source',
            extraParams:{
                id:'Patient'
            },
            reader:{
                root:'data',
                type:'json'
            }
        }
    }),
    displayField:'name',
    emptyText:'Search Patients',
    forceSelection:true,
    queryParam:'filter',
    minChars:4,
    queryMode:'remote'
});
