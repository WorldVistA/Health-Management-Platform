package org.osehra.cpe.vpr

import org.osehra.cpe.vpr.dao.solr.DefaultSolrDao
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class TagService {

    static transactional = true

    LinkService linkService

    DefaultSolrDao solrService

    def createTag(user, url, tagValue) {
        Tag tag = Tag.findByTagName(tagValue) ?: new Tag(tagName: tagValue)
        Tagger tagger = Tagger.findByUserNameAndUrl(user, url) ?: new Tagger(userName: user, url: url)
        tag = tag.save()
        tagger.addToTags(tag)
        tagger.save(flush: true, failOnErrors: true)

        def tagged = linkService.findByUid(url)
        solrService.index(tagged)
    }
}
