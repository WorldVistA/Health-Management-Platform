package EXT.DOMAIN.cpe.feed.atom

import org.codehaus.groovy.grails.validation.Validateable
import EXT.DOMAIN.cpe.datetime.PointInTime
import EXT.DOMAIN.cpe.feed.atom.Link
import EXT.DOMAIN.cpe.feed.atom.Category

@Validateable
class Entry implements Comparable {
    /**
     * Identifies the entry using a universally unique and permanent URI.
     */
    String id

    /**
     * Contains a human readable title for the entry.
     */
    Text title

    /**
     * Indicates the last time the entry was modified in a significant way.
     * This value need not change after a typo is fixed, only after a substantial modification.
     * Generally, different entries in a feed will have different updated timestamps.
     */
    PointInTime updated

    List<Person> authors = []

    Content content

    /**
     * Identifies a related Web page. The type of relation is defined by the rel attribute.
     */
    List<Link> links = []

    Text summary

    List<Category> categories = []

    List<Person> contributors = []

    PointInTime published

    Feed source

    Text rights

    Person getAuthor() {
        if (!authors) return null

        if (authors?.size() == 1) {
            authors.get(0)
        } else {
            throw new UnsupportedOperationException("unable to get entry's author - there is more than one!")
        }
    }

    void setAuthor(Person author) {
        authors = [author]
    }

    Link getLink() {
        if (!links) return null

        if (links?.size() == 1) {
            links.get(0)
        } else {
            throw new UnsupportedOperationException("unable to get entry's link - there is more than one!")
        }
    }

    void setLink(Link link) {
        links = [link]
    }

    void addToAuthors(Map props) {
        addToAuthors new Person(props)
    }

    void addToAuthors(Person author) {
        authors.add(author)
    }

    void addToLinks(Map props) {
        addToLinks new Link(props)
    }

    void addToLinks(Link link) {
        links.add(link)
    }

    void addToCategories(Map props) {
        addToCategories new Category(props)
    }

    void addToCategories(Category c) {
        categories.add(c)
    }

    void addToContributors(Map props) {
        addToContributors(new Person(props))
    }

    void addToContributors(Person contributor) {
        contributors.add(contributor)
    }

    /**
     * Compares to {Entry}s based on their <code>updated</code> property.  More recent entries compare earlier then later ones.
     * @param o
     * @return
     */
    int compareTo(Object o) {
        Entry e = o as Entry
        return -updated.compareTo(e.updated);
    }

    static constraints = {
        id(url: true, nullable: false, blank: false)
        title(nullable: false)
        updated(nullable: false)
    }
}
