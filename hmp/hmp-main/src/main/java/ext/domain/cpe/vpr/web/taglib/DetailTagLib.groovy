package EXT.DOMAIN.cpe.vpr.web.taglib

class DetailTagLib
{
	static namespace = 'hmp'
	
	/**
	 * docId - Unique identifier of this across the *entire* HTML document.
	 * title - Display title for expander link
	 * content - Self-explanatory (will render in a shaded paragraph.)
	 */
	def collapsibleDocument = { attrs, body ->
		out << """
<div id="result-${attrs.docId}" class="hmp-document hmp-document-collapsed">
    <div class="hmp-document-collapse-trigger" onclick="jQuery('#result-${attrs.docId.replaceAll(/:/,"\\\\:").replaceAll(/\./,"\\\\.").encodeAsJavaScript()}').toggleClass('hmp-document-collapsed')">
        <a href="javascript:void(0)">
        ${attrs.title ?: '<No Title>'}
        </a>
    </div>
    <hr class="hmp-document-title-hr"/>
    <div class="hmp-document-body">${body()}</div>
</div>"""
	}
}
