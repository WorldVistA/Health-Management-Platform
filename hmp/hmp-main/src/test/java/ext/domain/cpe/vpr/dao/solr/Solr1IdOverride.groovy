package org.osehra.cpe.vpr.dao.solr

class Solr1IdOverride {

    static solr = {
        constant name:'alias', value:'fubar'
        id name:"fooId", value: { delegate.astring }
    }

    Long id
    Long version

    String astring
    int aint
    long along
    Date adate
    float afloat

    static constraints = {
    }
}
