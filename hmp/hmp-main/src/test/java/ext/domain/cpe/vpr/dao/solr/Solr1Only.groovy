package org.osehra.cpe.vpr.dao.solr

class Solr1Only {
    static solr = {
        aString name: 'astringanothername' //, asText:true
        only = ['aLong', 'aDate']
    }

    Long id
    Long version

    String aString

    String bString

    int anInt
    long aLong
    Date aDate
    float aFloat
}
