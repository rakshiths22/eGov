<!--
	eGov suite of products aim to improve the internal efficiency,transparency, 
    accountability and the service delivery of the government  organizations.
 
    Copyright (C) <2015>  eGovernments Foundation
 
	The updated version of eGov suite of products as by eGovernments Foundation 
    is available at http://www.egovernments.org
 
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.
 
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 
    You should have received a copy of the GNU General Public License
    along with this program. If not, see http://www.gnu.org/licenses/ or 
    http://www.gnu.org/licenses/gpl.html .
 
    In addition to the terms of the GPL license to be adhered to in using this
    program, the following additional terms are to be complied with:
 
 	1) All versions of this program, verbatim or modified must carry this 
 	   Legal Notice.
 
 	2) Any misrepresentation of the origin of the material is prohibited. It 
 	   is required that all modified versions of this material be marked in 
 	   reasonable ways as different from the original version.
 
 	3) This license does not grant any rights to any user of the program 
 	   with regards to rights under trademark law for use of the trade names 
 	   or trademarks of eGovernments Foundation.
 
   	In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
-->
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ include file="/includes/taglibs.jsp"%>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn"%>

<script
	src="<cdn:url value='/resources/global/js/bootstrap/typeahead.bundle.js?rnd=${app_release_no}' context='/egi'/>"></script>

<script
	src="<cdn:url value='/resources/js/app/ajaxCommonFunctions.js?rnd=${app_release_no}'/>"></script>

<form:form class="form-horizontal form-groups-bordered" method="get"
	id="searchEmployeeAssignmentForm" modelAttribute="employee" action="">
	<div class="row">
		<div class="col-md-12">
			<div class="panel panel-primary" data-collapsed="0">
				<div class="panel-heading">
					<div class="panel-title" style="text-align: center;">
						<spring:message code="title.searchemployeeposition" />
					</div>
				</div>
				<div class="panel-body custom-form">
					<div class="form-group advanced-forms">


						<div class="form-group">

							<label for="field-1" class="col-sm-2 control-label"><spring:message
									code="lbl.department" /></label>

							<div class="col-sm-2 add-margin">
								<form:select cssClass="form-control" id="department"
									name="department" path="department" onchange="getPosition();">
									<option value="">
										<spring:message code="lbl.select" />
									</option>
									<form:options items="${department}" itemValue="id"
										itemLabel="name" />
								</form:select>
							</div>

							<div class="col-sm-2">
								<label for="field-1" class="control-label"><spring:message
										code="lbl.designation" /></label>
							</div>

							<div class="col-sm-3 add-margin">
								<form:select cssClass="form-control" id="designation"
									name="designation" path="designation" onchange="getPosition();">
									<option value="">
										<spring:message code="lbl.select" />
									</option>
									<form:options items="${desigList}" itemValue="id"
										itemLabel="name" />
								</form:select>
							</div>
						</div>

						<div class="form-group">
							<label for="field-1" class="col-sm-2 control-label"><spring:message
									code="lbl.position" /></label>

							<div class="col-sm-2 add-margin">
								<form:select cssClass="form-control" id="position"
									name="position" path="position" >
									<option value="">
										<spring:message code="lbl.select" />
									</option>
									<form:options items="${position}" itemValue="id"
										itemLabel="name" />
								</form:select>
							</div>
							<div class="col-sm-2">
								<label for="field-1" class="control-label"><spring:message code="lbl.primary.temp" /></label>
							</div>

							<div class="col-sm-2 add-margin">
								<form:select cssClass="form-control" id="isPrimary"
									name="isPrimary" path="isPrimary">
									<form:option value="">
	                                                Select
												</form:option>
									<form:option value="true">
													Primary
												</form:option>
									<form:option value="false">
													Temporary
												</form:option>
								</form:select>
							</div>


						</div>
						<div class="text-center">
							<button type="button" class="btn btn-primary" id="btnsearch"><spring:message code="lbl.submit" /></button>
							<a href="javascript:void(0)" class="btn btn-default"
								onclick="self.close()"><spring:message code="lbl.close" /></a>

						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<div class="row">
		<div>
			<div id="empAssignment-header"
				class="col-md-12 table-header text-left">
								<input type="hidden" value="Employee Position Report" id="pdfTitle"/>
				</div>
			<div class="col-md-12 form-group report-table-container">
				<table class="table table-bordered datatable" id="tblempassign">
				</table>
			</div>
		</div>
	</div>

</form:form>



<script
	src="<cdn:url value='/resources/js/reports/employeepositionreport.js?rnd=${app_release_no}'/>"></script>
<link rel="stylesheet"
	href="<cdn:url value='/resources/global/js/jquery/plugins/datatables/responsive/css/datatables.responsive.css?rnd=${app_release_no}' context='/egi'/>" />
<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/jquery.dataTables.min.js?rnd=${app_release_no}' context='/egi'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/dataTables.bootstrap.js?rnd=${app_release_no}' context='/egi'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/responsive/js/datatables.responsive.js?rnd=${app_release_no}' context='/egi'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/dataTables.tableTools.js?rnd=${app_release_no}' context='/egi'/>"
	type="text/javascript"></script>
<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/TableTools.min.js?rnd=${app_release_no}' context='/egi'/>"
	type="text/javascript"></script>