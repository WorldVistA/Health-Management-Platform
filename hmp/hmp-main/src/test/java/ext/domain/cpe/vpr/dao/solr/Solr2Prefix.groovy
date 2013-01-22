package EXT.DOMAIN.cpe.vpr.dao.solr

class Solr2Prefix {

    static solr = {
        solrs component: true, prefix: "one_"
    }

    Long id
    Long version

    Set<Solr1> solrs

    Solr1 addToSolrs(Solr1 solr1) {
        if (!solrs) {
            solrs = new HashSet<Solr1>()
        }
        solrs.add(solr1);

        return solr1
    }

    static hasMany = [solrs: Solr1]

    static constraints = {
    }
}
