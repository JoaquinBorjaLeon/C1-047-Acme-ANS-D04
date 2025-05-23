<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<jstl:if test="${acme:anyOf(_command,'show|update|delete|publish')}">
		<acme:input-moment code = "technician.maintenance-record.form.label.moment" path = "maintenanceMoment"  readonly = "true" />
	</jstl:if>
	<acme:input-select code = "technician.maintenance-record.form.label.status" path = "status" choices="${statuses}"/>
	<jstl:choose>
		<jstl:when test="${draftMode != false}">
			<acme:input-moment code = "technician.maintenance-record.form.label.nextInspectionDate" path = "nextInspectionDate"/>
			<acme:input-money code = "technician.maintenance-record.form.label.estimatedCost" path = "estimatedCost"/>
			<acme:input-textbox code = "technician.maintenance-record.form.label.notes" path = "notes"/>
			<acme:input-select code = "technician.maintenance-record.form.label.aircraft" path = "aircraft" choices="${aircrafts}"/>
		</jstl:when>
		<jstl:when test="${draftMode == false}">
			<acme:input-moment code = "technician.maintenance-record.form.label.nextInspectionDate" path = "nextInspectionDate"  readonly = "true"/>
			<acme:input-money code = "technician.maintenance-record.form.label.estimatedCost" path = "estimatedCost"  readonly = "true"/>
			<acme:input-textbox code = "technician.maintenance-record.form.label.notes" path = "notes"  readonly = "true"/>
			<acme:input-select code = "technician.maintenance-record.form.label.aircraft" path = "aircraft" choices="${aircrafts}"  readonly = "true"/>
		</jstl:when>
	</jstl:choose>
	
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command,'show|update|delete|publish') && draftMode == true}">
			<acme:submit code="technician.maintenance-record.form.button.publish" action="/technician/maintenance-record/publish"/>
			<acme:submit code="technician.maintenance-record.form.button.update" action="/technician/maintenance-record/update"/>	
			<acme:submit code="technician.maintenance-record.form.button.delete" action="/technician/maintenance-record/delete"/>
			<jstl:if test="${_command != 'create'}">
				<acme:button code="technician.maintenance-record.form.add.task" action="/technician/maintenance-record-task/create?id=${id}"/>
				<acme:button code="technician.maintenance-record.form.list.task" action="/technician/maintenance-record-task/list?id=${id}"/>				
			</jstl:if>
		</jstl:when>
		<jstl:when test="${acme:anyOf(_command,'show|update|delete|publish') && draftMode == false}">
			<jstl:if test="${_command != 'create'}">
				<acme:button code="technician.maintenance-record.form.list.task" action="/technician/maintenance-record-task/list?id=${id}"/>
				<acme:submit code="technician.maintenance-record.form.button.update" action="/technician/maintenance-record/update"/>
			</jstl:if>
		</jstl:when>
		<jstl:when  test="${acme:anyOf(_command,'create')}">
			<acme:submit code="technician.maintenance-record.form.button.create" action="/technician/maintenance-record/create"/>
		</jstl:when>
	</jstl:choose>
</acme:form>