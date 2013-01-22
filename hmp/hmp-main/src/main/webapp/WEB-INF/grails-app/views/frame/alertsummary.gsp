<g:if test="${rows.size()}">
<div style="background: url('/images/icons/warning_sign.png'); background-repeat: no-repeat; background-size: 15px 15px;">
${rows.get(0).get("alert_data").size()}
</div>
</g:if>
<g:else>
&nbsp;
</g:else>
