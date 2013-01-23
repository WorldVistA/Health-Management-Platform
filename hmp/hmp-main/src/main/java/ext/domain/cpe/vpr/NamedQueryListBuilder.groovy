package org.osehra.cpe.vpr

/**
 * Groovy "builder" that evaluates a domain class <code>namedQueries</code> closure for a list of named queries that have no arguments.
 */
class NamedQueryListBuilder {

    List<String> namedQueries = []
    boolean initialized = false

    void evaluate(Closure namedQueriesClosure) {
        Closure closure = namedQueriesClosure.clone()
        closure.resolveStrategy = Closure.DELEGATE_ONLY
        closure.delegate = this
        closure.call()
        initialized = true
    }

    // this is the callback that is called by evaluating the closure with this object as the delegate
    def methodMissing(String name, args) {
        if (args && args[0] instanceof Closure && !initialized) {
            return handleNamedQuery(name, args[0] as Closure)
        }
        throw new MissingMethodException(name, NamedQueryListBuilder, args)
    }

    private handleNamedQuery = {String namedQuery, Closure queryImpl ->
        namedQueries << namedQuery
    }

}
