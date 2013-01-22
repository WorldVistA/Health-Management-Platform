package EXT.DOMAIN.cpe.vpr.web.taglib

class MedDetailTagLib
{
	static namespace = 'hmp'
	
	/**
	 * docId - Unique identifier of this across the *entire* HTML document.
	 * title - Display title for expander link
	 * content - Self-explanatory (will render in a shaded paragraph.)
	 */
	def collapsibleMed = { attrs, body ->
		out << 
			'<p>'+
				'<a href="javascript:;"onmousedown="if(document.getElementById(\'result-'+attrs.medId+'\').style.display == \'none\'){document.getElementById(\'result-'+attrs.medId+'\').style.display = \'block\'}else{document.getElementById(\'result-'+attrs.medId+'\').style.display = \'none\'}">'+
					'<b>'+(attrs.title && attrs.title.toString().length()>0?attrs.title:'<No Title>')+'</b>'+
				'</a>' +
			'</p>'+
			'<div id="result-'+attrs.medId+'" style="margin-left:10px;display:none;background-color:#DDDDDD">'+
				'<p><pre>'+body()+'</pre></p>'+
			'</div>'
	}
}
