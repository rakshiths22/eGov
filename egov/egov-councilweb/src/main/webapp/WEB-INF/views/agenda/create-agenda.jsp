<%--
  ~ eGov suite of products aim to improve the internal efficiency,transparency,
  ~    accountability and the service delivery of the government  organizations.
  ~
  ~     Copyright (C) <2016>  eGovernments Foundation
  ~
  ~     The updated version of eGov suite of products as by eGovernments Foundation
  ~     is available at http://www.egovernments.org
  ~
  ~     This program is free software: you can redistribute it and/or modify
  ~     it under the terms of the GNU General Public License as published by
  ~     the Free Software Foundation, either version 3 of the License, or
  ~     any later version.
  ~
  ~     This program is distributed in the hope that it will be useful,
  ~     but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~     GNU General Public License for more details.
  ~
  ~     You should have received a copy of the GNU General Public License
  ~     along with this program. If not, see http://www.gnu.org/licenses/ or
  ~     http://www.gnu.org/licenses/gpl.html .
  ~
  ~     In addition to the terms of the GPL license to be adhered to in using this
  ~     program, the following additional terms are to be complied with:
  ~
  ~         1) All versions of this program, verbatim or modified must carry this
  ~            Legal Notice.
  ~
  ~         2) Any misrepresentation of the origin of the material is prohibited. It
  ~            is required that all modified versions of this material be marked in
  ~            reasonable ways as different from the original version.
  ~
  ~         3) This license does not grant any rights to any user of the program
  ~            with regards to rights under trademark law for use of the trade names
  ~            or trademarks of eGovernments Foundation.
  ~
  ~   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
  --%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn" %>
<form:form role="form" action="new" modelAttribute="councilPreamble"
	id="councilPreambleform"
	cssClass="form-horizontal form-groups-bordered"
	enctype="multipart/form-data">
	
		<jsp:include page="../councilpreamble/councilpreamble-search-form.jsp"/>
		
</form:form>
<div class="row display-hide report-section">
	<div class="col-md-12 table-header text-left">Preamble
		Search Result</div>
	<div class="col-md-12 form-group report-table-container">
		<table class="table table-bordered table-hover multiheadertbl"
			id="resultTable">
		</table>
	</div>
</div>
<form:form role="form" action="create" modelAttribute="councilAgenda"
	id="councilAgendaform"
	cssClass="form-horizontal form-groups-bordered"
	enctype="multipart/form-data">
	
<div class="row display-hide agenda-section">
	<div class="col-md-6 table-header text-left">Create Agenda</div>
	<!-- <div class="col-md-6 text-right pull-right"><button type="button" class="btn btn-primary" id="add-agenda">Add Row</button></div> -->
		<div>

			<label class="col-sm-3 control-label text-right"><spring:message
					code="lbl.committeetype" /> <span class="mandatory"></span> </label>
			<div class="col-sm-3 add-margin">
				<form:select path="committeeType" id="committeeType" required="required"
					cssClass="form-control" cssErrorClass="form-control error">
					<form:option value="">
						<spring:message code="lbl.select" />
					</form:option>
					<form:options items="${committeeType}" itemValue="id"
						itemLabel="name" />
				</form:select>
				<form:errors path="committeeType" cssClass="error-msg" />
			</div>
		</div>

		<div class="col-md-12 report-table-container">
		<table class="table table-bordered table-hover multiheadertbl"
			id=agendaTable>
			<thead>
				<tr>
					<th><spring:message code="lbl.preamble.number" /></th>
					<th><spring:message code="lbl.department" /></th>
					<th><spring:message code="lbl.gist.preamble" /></th>
					<th><spring:message code="lbl.amount"/></th>
					<th><spring:message code="lbl.action"/></th>				
				</tr>
			</thead>
			<tbody>
			
			
		</table>
		<div class="row">
					<div class="text-center">
						<input type="submit" class='btn btn-primary validate' value='Create' id="btnsave">
					</div>
				</div>
	</div>
</div>
</form:form>
<script>

	$('#btnsearch').click(function(e) {
	
		if ($('form').valid()) {
			return true;
		} else {
			e.preventDefault();
		}
	});
	
	
	
</script>
<link rel="stylesheet"
	href="<cdn:url value='/resources/global/css/bootstrap/bootstrap-datepicker.css?rnd=${app_release_no}' context='/egi'/>" />
	<link rel="stylesheet" href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/jquery.dataTables.min.css?rnd=${app_release_no}' context='/egi'/>"/>
<link rel="stylesheet" href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/dataTables.bootstrap.min.css?rnd=${app_release_no}' context='/egi'/>">
<script type="text/javascript"
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/jquery.dataTables.min.js?rnd=${app_release_no}' context='/egi'/>"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/dataTables.bootstrap.js?rnd=${app_release_no}' context='/egi'/>"></script>
<%-- <script type="text/javascript"
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/dataTables.tableTools.js' context='/egi'/>"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/TableTools.min.js' context='/egi'/>"></script> --%>
<script type="text/javascript"
	src="<cdn:url value='/resources/global/js/bootstrap/typeahead.bundle.js?rnd=${app_release_no}' context='/egi'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/jquery.inputmask.bundle.min.js?rnd=${app_release_no}' context='/egi'/>"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/global/js/jquery/plugins/jquery.validate.min.js?rnd=${app_release_no}' context='/egi'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/bootstrap/bootstrap-datepicker.js?rnd=${app_release_no}' context='/egi'/>"
	type="text/javascript"></script>
<script src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/datetime-moment.js?rnd=${app_release_no}' context='/egi'/>"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/app/js/councilAgenda.js?rnd=${app_release_no}'/>"></script>
