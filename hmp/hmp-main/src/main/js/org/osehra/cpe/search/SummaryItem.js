/**
 * An actual shared model!
 */
Ext.define("org.osehra.cpe.search.SummaryItem", {
    extend: 'Ext.data.Model',
    fields: [
        'uid',
        'summary',
        'type',
        'kind',
        'datetime',
        'datetimeFormatted',
        'where',
        'highlight',
        'count'
    ]
});
