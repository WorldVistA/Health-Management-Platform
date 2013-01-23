<%@ page contentType="text/html;charset=UTF-8" %>
<table class="hmp-labeled-values" border="1">
	<tr>
		<td><img src="/images/icons/warning_sign.png" style="float: left;"/></td>
		<td>
			<b>Title:</b> ${alert.title}<br/>
			<b>Date:</b> ${alert.referenceDateTime}<br/>
			<g:if test="${alert.severe}"><b>Severity:</b> ???<br/></g:if>
		</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td><i>${alert.description}</i></td>
	</tr>
	<g:if test="${links}">
	<tr><td>Relevant Data</td><td>
		<g:each in="${links}">
			<li>${it.summary}</li>
		</g:each>
	</td></tr>
	</g:if>
	<g:if test="${alert.getActions()}">
	<tr><td>Actions/<br/>Mitigating Factors</td><td>
		<form id="AlertDialogFormID">
		<g:each in="${alert.getActions()}" var="ob">
			<g:if test="${ob.type.equals('ObsDateRequestAction')}">
				<input type="checkbox" name="" value=""/>${ob.getTitle()}
				<input type="date" name="${ob.getValue()}" value=""
				  onBlur="org.osehra.cpe.AlertDialog.obs(this.name, '', this.value);"
				  /><br>
			</g:if>
			<g:else>
				<input type="checkbox" name="${ob.getValue()}" value="1"
					onClick="org.osehra.cpe.AlertDialog.obs(this.name, this.value);"
					/>${ob.getTitle()}<br>
			</g:else>
		</g:each>
		</form>
	</td></tr>
	</g:if>
	<tr>
		<td>Comment/Note:</td>
		<td><textarea style="width: 100%; height: 100%;"></textarea>
	</tr>			
</table>
