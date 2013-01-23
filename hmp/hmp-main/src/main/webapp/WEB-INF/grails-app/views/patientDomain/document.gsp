<%@ page import="org.osehra.cpe.vpr.web.converter.dateTime.PointInTimeToStringConverter" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>${item.summary}</title>
    <g:render template="/layouts/detail"/>
</head>
<body>
<g:if test="${item.text && item.text[0]}">
    <table class="hmp-labeled-values">
        <tr><td>Local Title</td><td>${item.localTitle}</td></tr>
        <g:if test="${item.nationalTitle!=null}"><tr><td>Standard Title</td><td>${item.nationalTitle.title}</td></tr></g:if>
        <tr>
            <td>Date of Note</td><td><hmp:formatDate date="${item.text[0].dateTime}" format="MMM dd, yyyy@HH:mm"/></td>
            <g:if test="${item.enteredDateTime!=null}"><td>Entry Date</td><td><hmp:formatDate date="${item.enteredDateTime}" format="MMM dd, yyyy@HH:mm:ss"/></td></g:if>
        </tr>
        <tr>
            <td>Author</td><td>${item.text[0].getAuthor()}</td>
            <g:if test="${item.cosigner!=null}"><td>Exp. Cosigner</td><td>${item.cosigner}</td></g:if>
        </tr>
        <tr>
            <td>Urgency</td><td><g:if test="${item.urgency!=null}">${item.urgency}</g:if><g:else><span class="hmp-unknown">&lt;Unknown&gt;</span></g:else></td>
            <td>Status</td><td>${item.status}</td>
        </tr>
        <g:if test="${item.attending!=null}"><tr><td>Attending</td><td>${item.attending}</td></tr></g:if>
    </table>
    <br/>
    <pre>${item.text[0].content}
	<g:each in="${item.clinicians}" status="c" var="clin">
		<g:if test="${clin.role.equals('S')}">
            <br/>/es/ ${clin.signature}
            <br/>Signed: <hmp:formatDate date="${clin.signedDateTime}" format="MM/dd/yy HH:mm"/><br/>
		</g:if>
	</g:each>
    </pre>
	<g:each in="${item.text}" status="j" var="txt">
	<g:if test="${j>0 && txt.content}">
		<hmp:collapsibleDocument docId="doc-result-addendum-${txt.uid}" title="ADDENDUM-${j}">
            <table class="hmp-labeled-values">
                <tr>
                    <td>Date of Addendum</td><td><hmp:formatDate date="${txt.dateTime}" format="MMM dd, yyyy@HH:mm"/></td>
                    <g:if test="${txt.enteredDateTime!=null}"><td>Entry Date</td><td><hmp:formatDate date="${txt.enteredDateTime}" format="MMM dd, yyyy@HH:mm"/></td></g:if>
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
			<pre>${txt.content}
			<g:each in="${txt.clinicians}" status="c" var="clin">
				<g:if test="${clin.role.equals('S')}">
					<br/>/es/ ${clin.signature}
					<br/>Signed: <hmp:formatDate date="${clin.signedDateTime}" format="MM/dd/yy HH:mm"/><br/>
				</g:if>
			</g:each>
            </pre>
		</hmp:collapsibleDocument>
	</g:if>
</g:each>
</g:if>
</body>
</html>
