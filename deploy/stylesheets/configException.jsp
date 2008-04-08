<%@include file="_commons-include.jsp"%>
<e:page stringsVar="msgs">
	<e:emptyMenu />
	<e:section value="#{msgs['CONFIG_EXCEPTION.TITLE']}" />

	<e:paragraph value="#{msgs['CONFIG_EXCEPTION.TEXT.TOP']}" />

	<e:messages />

	<h:form>
		<e:commandButton value="#{msgs['EXCEPTION.BUTTON.RESTART']}"
			action="#{exceptionController.restart}" />

		<t:htmlTag value="div" id="exceptionDetails" forceId="true">
			<t:htmlTag value="p">
				<h:commandLink onclick="hideExceptionDetails(); return false;"
					value="#{msgs['EXCEPTION.TEXT.HIDE_DETAILS']}" />
			</t:htmlTag>
			<%@include file="_exceptionDetails.jsp"%>
		</t:htmlTag>

		<t:htmlTag value="div" id="exceptionNoDetail" forceId="true">
			<t:htmlTag value="p">
				<h:commandLink onclick="showExceptionDetails(); return false;"
					value="#{msgs['EXCEPTION.TEXT.SHOW_DETAILS']}" />
			</t:htmlTag>
		</t:htmlTag>

		<script>
function hideExceptionDetails() {
	hideElement('exceptionDetails');
	showElement('exceptionNoDetail');
}
function showExceptionDetails() {
	showElement('exceptionDetails');
	hideElement('exceptionNoDetail');
}
hideExceptionDetails();
		</script>

	</h:form>
</e:page>
