<%@ page import="EXT.DOMAIN.cpe.vpr.web.converter.dateTime.PointInTimeToStringConverter" contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title>${item.summary}</title>
    <g:render template="/layouts/detail"/>
</head>

<body>
<table class="hmp-labeled-values">
    <tr>
          <td>Facility</td>
          <td>${item.facilityName}</td>
      </tr>
    <tr>
        <td>Type</td>
        <td>${item.kind}</td>
    </tr>
    <tr>
        <td>Status</td>
        <td>${item.status}</td>
    </tr>
    <tr>
        <td>Date Time</td>
        <td><hmp:formatDate date="${item.dateTime}" /></td>
    </tr>
        <tr>
            <td>Provider</td>
        <td>
            <g:if test="${item.providers}">
                <ul>
                    <g:each in="${item.providers}" status="i" var="prov">
                        <li>${prov.provider?.name}</li>
                    </g:each>
                </ul>
            </g:if>
            <g:else>
                <span class="hmp-unknown">&lt;Unknown&gt;</span>
            </g:else>
        </td>
        </tr>
</table>
<br/>
<g:each in="${item.results}" status="i" var="results">
    <g:if test="${results.document && results.document.text && results.document.text[0]}">
		<hmp:collapsibleDocument docId="proc-result-${results.document.uid}" title="${results.document.localTitle}">
            <table class="hmp-labeled-values">
                <tr><td>Local Title</td><td>${results.document.localTitle}</td></tr>
                <g:if test="${results.document.nationalTitle!=null}"><tr><td>Standard Title</td><td>${results.document.nationalTitle.title}</td></tr></g:if>
                <tr>
                    <td>Date of Note</td><td><hmp:formatDate date="${results.document.text[0].dateTime}" format="MMM dd, yyyy@HH:mm"/></td>
                    <g:if test="${results.document.enteredDateTime!=null}"><td>Entry Date</td><td><hmp:formatDate date="${results.document.enteredDateTime}" format="MMM dd, yyyy@HH:mm:ss"/></td></g:if>
                </tr>
                <tr>
                    <td>Author</td><td>${results.document.text[0].getAuthor()}</td>
                    <g:if test="${results.document.cosigner!=null}"><td>Exp. Cosigner</td><td>${results.document.cosigner}</td></g:if>
                </tr>
                <tr>
                    <td>Urgency</td><td><g:if test="${results.document.urgency!=null}">${results.document.urgency}</g:if><g:else><span class="hmp-unknown">&lt;Unknown&gt;</span></g:else></td>
                    <td>Status</td><td>${results.document.status}</td>
                </tr>
                <g:if test="${results.document.attending!=null}"><tr><td>Attending</td><td>${results.document.attending}</td></tr></g:if>
            </table>
            <br/>
			<pre>${results.document.text[0].content}
			<g:each in="${results.document.clinicians}" status="c" var="clin">
				<g:if test="${clin.role.equals('S')}">
					<br>/es/ ${clin.signature}
					<br>Signed: <hmp:formatDate date="${clin.signedDateTime}" format="MM/dd/yy HH:mm"/><br>
				</g:if>
			</g:each>
            </pre>
			<g:each in="${results.document.text}" status="j" var="txt">
				<g:if test="${j>0 && txt.content}">
					<hmp:collapsibleDocument docId="proc-result-addendum-${txt.uid}" title="ADDENDUM-${j}">
                        <table class="hmp-labeled-values">
                            <tr>
                                <td>Date of Addendum</td><td><hmp:formatDate date="${txt.dateTime}" format="MMM dd, yyyy@HH:mm"/></td>
                                <g:if test="${txt.enteredDateTime!=null}"><td>Entry Date</td><td><hmp:formatDate date="${txt.enteredDateTime}" format="MMM dd, yyyy@HH:mm:ss"/></td></g:if>
                            </tr>
                            <tr>
                                <td>Author</td><td>${txt.getAuthor()}</td>
                                <g:if test="${txt.cosigner!=null}"><td>Exp. Cosigner</td><td>${txt.cosigner}</td></g:if>
                            </tr>
                            <tr>
                                <td>Urgency</td><td><g:if test="${txt.urgency!=null}">${txt.urgency}</g:if><g:else><span class="hmp-unknown">&lt;Unknown&gt;</span></g:else></td>
                                <td>Status</td><td>${txt.status}</td>
                            </tr>
                            <g:if test="${txt.attending!=null}"><tr><td>Attending</td><td>${txt.attending}</td></tr></g:if>
                        </table>
						Date of Addendum: <hmp:formatDate date="${txt.dateTime}"/><br>
						Author: ${txt.getAuthor()}<br>
						Status: ${txt.status}<br>
						<g:if test="${txt.urgency!=null}">Urgency: ${txt.urgency}<br></g:if>
						<g:if test="${txt.cosigner!=null}">Exp. Cosigner: ${txt.cosigner}</g:if>
						<g:if test="${txt.enteredDateTime!=null}">Entry Date: <hmp:formatDate date="${txt.enteredDateTime}"/></g:if>
						<g:if test="${txt.attending!=null}">Attending: ${txt.attending}</g:if>
						<pre>${txt.content}
						<g:each in="${txt.clinicians}" status="c" var="clin">
							<g:if test="${clin.role.equals('S')}">
								<br>/es/ ${clin.signature}
								<br>Signed: <hmp:formatDate date="${txt.enteredDateTime}" format="MM/dd/yy HH:mm"/><br>
							</g:if>
						</g:each>
                        </pre>
					</hmp:collapsibleDocument>
				</g:if>
			</g:each>
            <br/>
		</hmp:collapsibleDocument>
    </g:if>
</g:each>
</body>
</html>
