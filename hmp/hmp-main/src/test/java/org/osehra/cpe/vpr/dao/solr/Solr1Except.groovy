package org.osehra.cpe.vpr.dao.solr

class Solr1Except {
    static solr = {
        aString name: 'astringanothername_s' //, asText:true
        aFloat name: 'afloatanohername_f'
        except = ['aFloat', 'aDate']
//        bstring asText:true
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
