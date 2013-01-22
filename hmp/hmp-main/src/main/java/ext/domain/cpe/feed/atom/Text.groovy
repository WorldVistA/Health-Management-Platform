package EXT.DOMAIN.cpe.feed.atom


class Text {
    String type = 'text'
    String text

    Text() {
        // NOOP
    }

    Text(String text) {
        this.text = text
    }

    static constraints = {
        text(nullable: false, blank: false)
    }
}
