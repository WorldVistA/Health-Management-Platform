<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="EXT.DOMAIN.cpe.vpr.*"%>
<%@ page import="org.springframework.data.domain.Page"%>

<html>
<head>
    <title>Vaccinations</title>
    <g:render template="/layouts/detail"/>
</head>

<%

	Page<Immunization> immuninzations = dao.findAllByPID(Immunization.class, pid, null)

%>

<body>

<table class="hmp-labeled-values" style="float: left;">
    <tr>
        <td>Vaccination History (${immuninzations.getTotalElements()})</td>
    </tr>
    <tr>
    	<td>Name</td>
    	<td>Facility</td>
    	<td>Administered</td>
    	<td>Comments</td>
    </tr>
    <g:each in="${immuninzations}" var="imm">
    <tr>
    	<td>${imm.name}</td>
        <td>${imm.facilityName}</td>
    	<td><hmp:formatDate date="${imm.administeredDateTime}"/></td>
    	<td><hmp:formatDate date="${imm.comments}"/></td>
    </tr>
    </g:each>
</table>

<table border="1"  class="hmp-labeled-values">
	<tr>
		<td colspan="2">Pneum Vacc Compliance/Goal Status: <span style="color: red; font-weight: bold;">DUE NOW</span></td>
	</tr>
	<tr>
		<td> Mitigating Reason: </td>
		<td>
			<input type="radio" name="mit"> Scheduled for future visit<br>
			<input type="radio" name="mit"> Patient Refuses<br>
			<input type="radio" name="mit"> Medically inappropriate<br>
			<input type="radio" name="mit"> Done outside the VA<br>
			<input type="radio" name="mit"> Other...<br>
		</td>
	</tr>
	<tr>
		<td>New Comment:</td>
		<td><textarea></textarea>
	</tr>
	<tr>
    	<td colspan="2" align="center"><input type="submit" value="Submit"/></td>
    </tr>
</table>


</body>
</html>
