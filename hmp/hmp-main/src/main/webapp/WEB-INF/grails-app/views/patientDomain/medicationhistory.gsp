<%@ page contentType="text/html;charset=UTF-8" %>
<html>

%{--<head>--}%
%{--<title>${item.qualifiedName}</title>--}%
%{--<g:render template="/layouts/detail"/>--}%
%{--</head>--}%

<body>
<g:each in="${items}" status="m" var="item">
    <g:if test="${m==0}">
    <table class="hmp-labeled-values">

        <tr>
            <td>Medication</td>
            <td>${item.qualifiedName}</td>
        </tr>
        <tr>
            <td>Type</td>
            <td>${item.kind}</td>
        </tr>
        <tr>
            <td>Facility</td>
            <td>${item.facilityName}</td>
        </tr>
        <tr>
            <td>VA Status</td>
            <td>${item.vaStatus}</td>
        </tr>
        <tr>
            <td>Sig</td>
            <td><pre>${item.sig}</pre></td>
        </tr>
        <g:each in="${item.dosages}" status="i" var="dosage">
            <tr>
                <td>Dose/Route/Schedule</td>
                <td>${dosage.dose} ${dosage.routeName} ${dosage.scheduleName} ${dosage.duration}</td>
            </tr>
        </g:each>
        <g:if test="${item.vaType == 'O'}">
            <tr>
                <td>Day Supply</td>
                <td>${item.orders*.daysSupply[0]}</td>
            </tr>
            <tr>
                <td>Quantity</td>
                <td>${item.orders*.quantityOrdered[0]}</td>
            </tr>
            <tr>
                <td>Refills Remaining</td>
                <td>${item.orders*.fillsRemaining[0]}</td>
            </tr>
        </g:if>
        <tr>
            <td>Start</td>
            <td><hmp:formatDate date="${item.overallStart}"/></td>
        </tr>
        <tr>
            <td>Stop</td>
            <td><hmp:formatDate date="${item.overallStop}"/></td>
        </tr>
        <tr>
            <td>Location</td>
            <td>${item.orders*.locationName[0]}</td>
        </tr>
        <tr>
            <td>Facility</td>
            <td>${item.facilityName}</td>
        </tr>
        <tr>
            <td>Ordering Provider</td>
            <td>${item.orders*.providerName[0]}</td>
        </tr>
        <tr>
            <td>Finishing Pharmacist</td>
            <td>${item.orders*.pharmacistName[0]}</td>
        </tr>
        </table>
    </g:if>
    <g:else>
        <hmp:collapsibleMed medId="med-${item.uid}" title="${item.fPropMap.anchorLink}">
            <table class="hmp-labeled-values">
            <tr>
                <td>Medication</td>
                <td>${item.qualifiedName}</td>
            </tr>
            <tr>
                <td>Type</td>
                <td>${item.kind}</td>
            </tr>
            <tr>
                <td>Facility</td>
                <td>${item.facilityName}</td>
            </tr>
            <tr>
                <td>VA Status</td>
                <td>${item.vaStatus}</td>
            </tr>
            <tr>
                <td>Sig</td>
                <td><pre>${item.sig}</pre></td>
            </tr>
            <g:each in="${item.dosages}" status="i" var="dosage">
                <tr>
                    <td>Dose/Route/Schedule</td>
                    <td>${dosage.dose} ${dosage.routeName} ${dosage.scheduleName} ${dosage.duration}</td>
                </tr>
            </g:each>
            <g:if test="${item.vaType == 'O'}">
                <tr>
                    <td>Day Supply</td>
                    <td>${item.orders*.daysSupply[0]}</td>
                </tr>
                <tr>
                    <td>Quantity</td>
                    <td>${item.orders*.quantityOrdered[0]}</td>
                </tr>
                <tr>
                    <td>Refills Remaining</td>
                    <td>${item.orders*.fillsRemaining[0]}</td>
                </tr>
            </g:if>
            <tr>
                <td>Start</td>
                <td><hmp:formatDate date="${item.overallStart}"/></td>
            </tr>
            <tr>
                <td>Stop</td>
                <td><hmp:formatDate date="${item.overallStop}"/></td>
            </tr>
            <tr>
                <td>Location</td>
                <td>${item.orders*.locationName[0]}</td>
            </tr>
            <tr>
                <td>Facility</td>
                <td>${item.facilityName}</td>
            </tr>
            <tr>
                <td>Ordering Provider</td>
                <td>${item.orders*.providerName[0]}</td>
            </tr>
            <tr>
                <td>Finishing Pharmacist</td>
                <td>${item.orders*.pharmacistName[0]}</td>
            </tr>
            </table>
        </hmp:collapsibleMed>
    </g:else>
</g:each>
%{--</table>--}%
</body>
</html>