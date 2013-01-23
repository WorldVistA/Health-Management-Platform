package org.osehra.cpe.feed.atom

class Content extends Text {

    String src

    Content() {
        super(null)
        this.type = null
    }

    Content(String text) {
        super(text)
    }
}
