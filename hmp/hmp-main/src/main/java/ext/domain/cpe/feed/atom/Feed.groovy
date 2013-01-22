package EXT.DOMAIN.cpe.feed.atom

import org.codehaus.groovy.grails.validation.Validateable
import EXT.DOMAIN.cpe.datetime.PointInTime
import EXT.DOMAIN.cpe.feed.atom.Link
import EXT.DOMAIN.cpe.feed.atom.Category
import EXT.DOMAIN.cpe.feed.atom.Entry

@Validateable
class Feed {
    /**
     * Identifies the feed using a universally unique and permanent URI.
     */
    String id

    /**
     * Contains a human readable title for the feed.
     */
    Text title

    private PointInTime updated

    /**
     * Indicates the last time the feed was modified in a significant way.  If this property has been set explicitly, that
     * value is returned.  Otherwise returns the most recent entrie's updated field
     * @return
     */
    PointInTime getUpdated() {
        if (updated) return updated
        if (!entries) return null
        entries.sort()
        return entries[0].updated
    }

    void setUpdated(PointInTime updated) {
        this.updated = updated
    }

    // recommended
    List<Link> links = []
    List<Person> authors = []

    // optional
    List<Category> categories = []
    Generator generator
    String icon
    String logo
    Text rights
    Text subtitle

    List<Person> contributors = []
    List<Entry> entries = []

    Person getAuthor() {
        if (!authors) return null

        if (authors?.size() == 1) {
            authors.get(0)
        } else {
            throw new UnsupportedOperationException("unable to get feed's author - there is more than one!")
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
            throw new UnsupportedOperationException("unable to get feed's link - there is more than one!")
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

    static constraints = {
        id(nullable: false, blank: false)
        title(nullable: false)
        updated(nullable: false)

        generator(nullable: true)
        rights(nullable: true)
        subtitle(nullable: true)
    }
}
