<!--
	eGov suite of products aim to improve the internal efficiency,transparency, 
    accountability and the service delivery of the government  organizations.
 
    Copyright (C) <2016>  eGovernments Foundation
 
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn"%>

<div class="row">
	<div class="col-md-12">
		<form:form method="post" action=""
		modelAttribute="sewerageRatesSearch" id="sewerageRatesViewForm"
		cssClass="form-horizontal form-groups-bordered">
			<div class="panel panel-primary" data-collapsed="0">
				<div class="panel-heading"></div>
					<div class="panel-body custom-form">
						<div class="form-group">
							<label class="col-sm-2 control-label text-right"> <spring:message
									code="lbl.propertytype" /> <span class="mandatory"></span>
							</label>
							<div class="col-sm-3 add-margin">
								<form:select path="propertyType" data-first-option="false"
									id="propertyType" cssClass="form-control" required="required">
									<form:option value="">
										<spring:message code="lbl.select" />
									</form:option>
									<form:options items="${propertyTypes}" />
								</form:select>
								<form:errors path="propertyType" cssClass="add-margin error-msg" />
							</div>
							<form:hidden path="id" id="id" value="${sewerageRatesSearch.id}"></form:hidden>
							<label class="col-sm-2 control-label text-right"><spring:message
									code="lbl.effective.fromdate" /></label>
							<div class="col-sm-3 add-margin">
								<form:select path="fromDate" id="effectiveFromDate"
									cssClass="form-control">
									<form:option value="">
										<spring:message code="lbl.select" />
									</form:option>
								</form:select>
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2 control-label text-right"> <spring:message code="lbl.status" /> </label>
						    <div class="col-sm-3 add-margin">
		                   		<form:select path="status" id="connectionstatus" cssClass="form-control" 
									cssErrorClass="form-control error">
										<form:option value=""><spring:message code="lbl.select" /></form:option>
										<form:options items="${statusValues}" />
								</form:select>
								<form:errors path="status" cssClass="error-msg"/> 
		                    </div> 
						</div>
					</div>
				</div>
				<div class="form-group text-center">
					<button type="button" class="btn btn-primary" id="search"><spring:message code="lbl.search" /></button>
					<a onclick="self.close()" class="btn btn-default"
					href="javascript:void(0)"><spring:message code="lbl.close" /></a>
				</div>
		</form:form>
		<br/>
		<table  class="table table-bordered datatable dt-responsive" id="sewerage_master_rates_table"></table>
	</div>
	<br>
</div>
<link rel="stylesheet" href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/jquery.dataTables.min.css?rnd=${app_release_no}' context='/egi'/>"/>
<link rel="stylesheet" href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/dataTables.bootstrap.min.css?rnd=${app_release_no}' context='/egi'/>">
<script type="text/javascript" src="<cdn:url  value='/resources/global/js/jquery/plugins/datatables/jquery.dataTables.min.js?rnd=${app_release_no}' context='/egi'/>"></script>
<script type="text/javascript" src="<cdn:url  value='/resources/global/js/jquery/plugins/datatables/dataTables.bootstrap.js?rnd=${app_release_no}' context='/egi'/>"></script>
<script type="text/javascript" src="<cdn:url  value='/resources/global/js/jquery/plugins/datatables/dataTables.tableTools.js?rnd=${app_release_no}' context='/egi'/>"></script>
<script type="text/javascript" src="<cdn:url  value='/resources/global/js/jquery/plugins/datatables/TableTools.min.js?rnd=${app_release_no}' context='/egi'/>"></script>
<script type="text/javascript" src="<cdn:url  value='/resources/global/js/jquery/plugins/datatables/responsive/js/datatables.responsive.js?rnd=${app_release_no}' context='/egi'/>"></script>
<script src="<cdn:url  value='/resources/global/js/jquery/plugins/datatables/moment.min.js?rnd=${app_release_no}' context='/egi'/>"></script>
<script src="<cdn:url  value='/resources/global/js/jquery/plugins/datatables/datetime-moment.js?rnd=${app_release_no}' context='/egi'/>"></script>
<script src="<cdn:url  value='/resources/js/masters/sewerageRates.js?rnd=${app_release_no}'/>"></script>
