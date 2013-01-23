<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Frame Details</title>
    <g:render template="/layouts/detail"/>
</head>

<body>
<table class="hmp-labeled-values" border="1">
	<tr>
		<td colspan="2">FRAME INFO</td>
	</tr>
    <tr>
        <td>Frame Name</td>
        <td>${frame.name}</td>
    </tr>
    <tr>
    	<td>Implementation Class</td>
    	<td>${frame.getClass()}</td>
    </tr>
    <g:if test="${frame.getResource()}">
	    <tr>
	    	<td>Resource</td>
	    	<td>${frame.getResource()}</td>
	    </tr>
    </g:if>
    <g:if test="${meta.size()}">
	    <tr>
	    	<td>Frame Meta</td>
	    	<td>${meta}</td>
	    </tr>
    </g:if>
    <g:if test="${frame.getReferences().size()}">
    <tr>
    	<td>Literature References:</td>
    	<td>
    		<g:each in="${frame.getReferences()}" var="ref">
    			<li><b>${ref.authors}.</b>&nbsp;<i><a target="_BLANK" href="http://www.ncbi.nlm.nih.ext/pubmed/${ref.pmid}">${ref.title}.</a>&nbsp;</i>${ref.source}.</li>
    		</g:each>
    	</td>
    </tr>
    </g:if>
    
    <g:if test="${frame.getTriggers().size()}">
    <tr>
    	<td>Triggers:</td>
    	<td>
    		<g:each in="${frame.getTriggers()}" var="trig">
    			<li>${trig.toString()}</li>
    		</g:each>
    	</td>
    </tr>
    </g:if>
    <g:if test="${meta.relevantDrugClasses}">
    <tr>
    	<td>Drug Filters</td>
    	<td>
    		<g:each in="${meta.relevantDrugClasses}" var="drug">
    			<li>${drug.value}</li>
    		</g:each>
    	</td>
    </tr>
    </g:if>
    <g:if test="${meta.links}">
    <tr>
        <td>Links/References:</td>
        <td>
        	<g:each in="${meta.links}" var="link">
        		<li><a target="_BLANK" href="${link.value}">${link.key}</a></li>
        	</g:each>
        </td>
    </tr>
    </g:if>
    <g:if test="${meta.candidates}">
    <tr>
        <td>Protocol Candidate Conditions:</td>
    	<td>
    		<g:each in="${meta.candidates}" var="candidate">
    			<li>${candidate.desc}</li>
    		</g:each>
    	</td>
    </tr>
    </g:if>
	<tr>
		<td colspan="2">STATS</td>
	</tr>
    <tr>
    	<td>Executions</td>
    	<td>${stats.RUN_COUNT} (last: ${stats.RUN_LAST})</td>
    </tr>
    <tr>
    	<td>Runtime (ms) (min/max/sum/avg)</td>
    	<td>${stats.RUNTIME_MIN_MS}/${stats.RUNTIME_MAX_MS}/${stats.RUNTIME_SUM_MS}/${stats.RUNTIME_AVG_MS}</td>
    </tr>
</table>
</body>
</html>
