<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>${item.summary}</title>
    <g:render template="/layouts/detail"/>
</head>

<body>
<table class="hmp-labeled-values">
    <tr>
        <td>Name</td>
        <td>${item.name}<td>
    </tr>
    <tr>
        <td>Facility</td>
        <td>${item.facilityName}<td>
    </tr>
    <tr>
        <td>Start</td>
        <td><hmp:formatDate date="${item.start}"/><td>
    </tr>
    <tr>
        <td>Stop</td>
        <td><hmp:formatDate date="${item.stop}"/><td>
    </tr>
    <tr>
        <td>Status</td>
        <td>${item.statusName}</td>
    </tr>
    <tr>
        <td>Location</td>
        <td>${item.locationName}</td>
    </tr>
    <tr>
        <td>Ordering Provider</td>
        <td>${item.providerName}</td>
    </tr>
</table>
<br />
<pre>${item.content}</pre>
	<g:each in="${item.children}" status="j" var="ord">
		<hmp:collapsibleDocument docId="sub-order-${ord.uid}" title="${ord.summary}">
			<table class="hmp-labeled-values">
			    <tr>
			        <td>Name</td>
			        <td>${ord.name}<td>
			    </tr>
			    <tr>
			        <td>Facility</td>
			        <td>${ord.facilityName}<td>
			    </tr>
			    <tr>
			        <td>Start</td>
			        <td><hmp:formatDate date="${ord.start}"/><td>
			    </tr>
			    <tr>
			        <td>Stop</td>
			        <td><hmp:formatDate date="${ord.stop}"/><td>
			    </tr>
			    <tr>
			        <td>Status</td>
			        <td>${ord.statusName}</td>
			    </tr>
			    <tr>
			        <td>Location</td>
			        <td>${ord.locationName}</td>
			    </tr>
			    <tr>
			        <td>Ordering Provider</td>
			        <td>${ord.providerName}</td>
			    </tr>
			</table>
			<hr />
			<pre>${ord.content}</pre>
		</hmp:collapsibleDocument>
	</g:each>

</body>
</html>