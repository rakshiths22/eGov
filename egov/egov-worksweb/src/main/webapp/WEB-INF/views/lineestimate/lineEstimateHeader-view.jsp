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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div class="row">
	<div class="col-md-12">
		<div class="panel panel-primary" data-collapsed="0"
			style="text-align: left">
			<c:choose>
				<c:when
					test="${mode == 'readOnly' && lineEstimate.spillOverFlag == 'true' }">
					<div class="panel-heading">
						<div class="panel-title">
							<spring:message code="header.spilloverlineestimate" />
						</div>
					</div>
				</c:when>
				<c:otherwise>
					<div class="panel-heading">
						<div class="panel-title">
							<spring:message code="header.lineestimate" />
						</div>
					</div>
				</c:otherwise>
			</c:choose>

			<spring:hasBindErrors name="lineEstimate">
				<div class="alert alert-danger col-md-10 col-md-offset-1">
					<form:errors path="*" cssClass="error-msg add-margin" />
					<br />
				</div>
			</spring:hasBindErrors>
			<div class="panel-body">
				<div class="row add-border">
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.lineestimatenumber" />
					</div>
					<div class="col-xs-3 add-margin view-content">
						<c:out default="N/A" value="${lineEstimate.lineEstimateNumber}"></c:out>
					</div>
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.date" />
					</div>
					<div class="col-xs-3 add-margin view-content">
						<fmt:formatDate value="${lineEstimate.lineEstimateDate }"
							pattern="dd/MM/yyyy" />
					</div>
				</div>
				<div class="row add-border">
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.department" />
					</div>
					<div class="col-xs-3 add-margin view-content">
						<c:out default="N/A"
							value="${lineEstimate.executingDepartment.name}"></c:out>
					</div>
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.subject" />
					</div>
					<div class="col-xs-3 add-margin view-content">
						<form:hidden path="id" name="id" value="${id}"
							class="form-control table-input hidden-input" />
						<c:out default="N/A" value="${lineEstimate.subject}"></c:out>
					</div>
				</div>
				<div class="row add-border">
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.letter.reference" />
					</div>
					<div class="col-xs-3 add-margin view-content">
						<c:out default="N/A" value="${lineEstimate.reference}"></c:out>
					</div>
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.description" />
					</div>
					<div class="col-xs-3 add-margin view-content">
						<c:out default="N/A" value="${lineEstimate.description}"></c:out>
					</div>
				</div>
				<div class="row add-border">
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.election.ward" />
					</div>
					<div class="col-xs-3 add-margin view-content">
						<c:choose>
							<c:when test="${lineEstimate.ward.boundaryType.name.toUpperCase() == 'CITY'}">
								<c:out default="N/A" value="${lineEstimate.ward.name}" />
							</c:when>
							<c:otherwise>
								<c:out default="N/A" value="${lineEstimate.ward.boundaryNum}" />
							</c:otherwise>
						</c:choose>
					</div>
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.location" />
					</div>
					<div class="col-xs-3 add-margin view-content">
						<c:out default="N/A" value="${lineEstimate.location.name}" />
					</div>
				</div>
				<div class="row add-border">
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.workcategory" />
					</div>
					<div id="workCategoryView" class="col-xs-3 add-margin view-content">
						<c:out  default="N/A" value="${lineEstimate.workCategory}" />
					</div>
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.beneficiary" />
					</div>
					<div id="beneficiaryView" class="col-xs-3 add-margin view-content">
						<c:out default="N/A" value="${lineEstimate.beneficiary}" />
					</div>
				</div>
				<div class="row add-border">
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.natureofwork" />
					</div>
					<div class="col-xs-3 add-margin view-content">
						<c:out default="N/A" value="${lineEstimate.natureOfWork.name}" />
					</div>
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.typeofwork" />
					</div>
					<div class="col-xs-3 add-margin view-content">
						<c:out default="N/A" value="${lineEstimate.typeOfWork.description}" />
					</div>
				</div>
				<div class="row add-border">
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.subtypeofwork" />
					</div>
					<div class="col-xs-3 add-margin view-content">
						<c:out default="N/A" value="${lineEstimate.subTypeOfWork.description}" />
					</div>
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.modeofallotment" />
					</div>
					<div class="col-xs-3 add-margin view-content">
						<c:out default="N/A" value="${lineEstimate.modeOfAllotment}" />
					</div>
				</div>
				<c:if
					test="${mode == 'readOnly' && lineEstimate.spillOverFlag == 'true' }">
					<div class="row add-border">
						<div class="col-xs-3 add-margin">
							<spring:message code="lbl.createdby" />
						</div>
						<div class="col-xs-3 add-margin view-content">
							<c:out default="N/A"
								value="${createdbybydesignation } - ${lineEstimate.createdBy.name }"></c:out>
						</div>
					</div>
				</c:if>
			</div>
		</div>
		<div class="panel panel-primary" data-collapsed="0"
			style="text-align: left">
			<div class="panel-heading">
				<div class="panel-title">
					<spring:message code="lbl.financialdetails" />
				</div>
			</div>
			<div class="panel-body">
				<div class="row add-border">
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.fund" />
					</div>
					<div class="col-xs-3 add-margin view-content">
						<c:out default="N/A" value="${lineEstimate.fund.name}"></c:out>
					</div>
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.function" />
					</div>
					<div class="col-xs-3 add-margin view-content">
						<c:out default="N/A" value="${lineEstimate.function.name}"></c:out>
					</div>
				</div>
				<div class="row add-border">
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.budgethead" />
					</div>
					<div class="col-xs-3 add-margin view-content">
						<c:out default="N/A" value="${lineEstimate.budgetHead.name}" />
					</div>
				</div>
				<div class="row add-border">
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.scheme" />
					</div>
					<div class="col-xs-3 add-margin view-content">
						<c:out default="N/A" value="${lineEstimate.scheme.name}" />
					</div>
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.subscheme" />
					</div>
					<div class="col-xs-3 add-margin view-content">
						<c:out default="N/A" value="${lineEstimate.subScheme.name}"></c:out>
					</div>
				</div>
			</div>
		</div>