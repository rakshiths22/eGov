<%--
  ~ eGov suite of products aim to improve the internal efficiency,transparency,
  ~    accountability and the service delivery of the government  organizations.
  ~
  ~     Copyright (C) <2015>  eGovernments Foundation
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

<%@ page contentType="text/html" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn"%>
<div class="form-group">
	<label class="col-sm-2 control-label text-right"> <spring:message
			code="lbl.fromDate" />:<span class="mandatory"></span>
	</label>
	<div class="col-sm-3 add-margin">
		<input type="text" name="caseFromDate" required="required"
			class="form-control datepicker" data-date-end-date="0d"
			id="caseFromDate" data-inputmask="'mask': 'd/m/y'"
			onblur="onchnageofDate()" />
	</div>
	<label class="col-sm-2 control-label text-right"> <spring:message
			code="lbl.toDate" />:<span class="mandatory"></label>
	<div class="col-sm-3 add-margin">
		<input type="text" name="caseToDate"
			class="form-control datepicker today" data-date-end-date="0d"
			id="caseToDate" data-inputmask="'mask': 'd/m/y'" />
	</div>
</div>
<div class="form-group">
	<label class="col-sm-2 control-label text-right"><spring:message
			code="lbl.officerincharge" />:</label>
	<div class="col-sm-3 add-margin">
		<input type="text" class="form-control" maxlength="50"
			id="officialIncharge" name="officialIncharge" />
	</div>
	<div class="col-sm-3 add-margin"></div>
</div>

<link rel="stylesheet"
	href="<cdn:url value='/resources/global/css/bootstrap/bootstrap-datepicker.css?rnd=${app_release_no}' context='/egi'/>" />
<script
	src="<cdn:url value='/resources/global/js/bootstrap/bootstrap-datepicker.js?rnd=${app_release_no}' context='/egi'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/jquery.inputmask.bundle.min.js?rnd=${app_release_no}' context='/egi'/>"></script>