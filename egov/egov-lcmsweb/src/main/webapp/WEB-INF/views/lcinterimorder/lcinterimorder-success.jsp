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
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ include file="/includes/taglibs.jsp"%>
<form:form method="post" action=""
	class="form-horizontal form-groups-bordered"
	modelAttribute="lcInterimOrder" id="lcInterimOrderform">
	<div class="main-content">
		<div class="row">
			<div class="col-md-12">
				<c:if test="${not empty message}">
					<div role="alert">${message}</div>
				</c:if>
				<div class="panel panel-primary" data-collapsed="0">
					<div class="panel-heading">
						<div class="panel-title">Interim Order Details</div>
					</div>
					<div class="panel-body custom">
						<div class="row add-border">
							<div class="col-xs-3 add-margin">
								<spring:message code="lbl.interimorder" />
							</div>
							<div class="col-sm-3 add-margin view-content">
								${lcInterimOrder.interimOrder.interimOrderType}</div>

							<div class="col-xs-3 add-margin">
								<spring:message code="lbl.iodate" />
							</div>
							<div class="col-md-3 col-xs-6 add-margin view-content">
								<fmt:formatDate value="${lcInterimOrder.ioDate}" var="ioDate"
									pattern="dd/MM/yyyy" />
								<c:out value="${ioDate}" />
							</div>
						</div>
						<div class="row add-border">
							<div class="col-xs-3 add-margin">
								<spring:message code="lbl.mpnumber" />
							</div>
							<div class="col-sm-3 add-margin view-content">
								${lcInterimOrder.mpNumber}</div>

							<div class="col-xs-3 add-margin">
								<spring:message code="lbl.notes" />
							</div>
							<div class="col-sm-3 add-margin view-content">
								${lcInterimOrder.notes}</div>
						</div>
						<div class="row add-border">
							<div class="col-xs-3 add-margin">
								<spring:message code="lbl.sendtostandingcounsel" />
							</div>
							<div class="col-sm-3 add-margin view-content">
								<fmt:formatDate pattern="MM/dd/yyyyy"
									value="${lcInterimOrder.sendtoStandingCounsel}" />
							</div>
							<div class="col-xs-3 add-margin">
								<spring:message code="lbl.petitionfiledon" />
							</div>
							<div class="col-sm-3 add-margin view-content">
								<fmt:formatDate pattern="MM/dd/yyyyy"
									value="${lcInterimOrder.petitionFiledOn}" />
							</div>
						</div>
						<div class="row add-border">
							<div class="col-xs-3 add-margin">
								<spring:message code="lbl.reportfilingdue" />
							</div>
							<div class="col-sm-3 add-margin view-content">
								<fmt:formatDate pattern="MM/dd/yyyyy"
									value="${lcInterimOrder.reportFilingDue}" />
							</div>
							<div class="col-xs-3 add-margin">
								<spring:message code="lbl.sendtodepartment" />
							</div>
							<div class="col-sm-3 add-margin view-content">
								<fmt:formatDate pattern="MM/dd/yyyyy"
									value="${lcInterimOrder.sendtoDepartment}" />
							</div>
						</div>
						<div class="row add-border">
							<div class="col-xs-3 add-margin">
								<spring:message code="lbl.reportfromhod" />
							</div>
							<div class="col-sm-3 add-margin view-content">
								<fmt:formatDate pattern="MM/dd/yyyyy"
									value="${lcInterimOrder.reportFromHod}" />
							</div>

							<div class="col-xs-3 add-margin">
								<spring:message code="lbl.reportsendtostandingcounsel" />
							</div>
							<div class="col-sm-3 add-margin view-content">
								<fmt:formatDate pattern="MM/dd/yyyyy"
									value="${lcInterimOrder.reportSendtoStandingCounsel}" />
							</div>
						</div>
						<div class="row add-border">
							<div class="col-xs-3 add-margin">
								<spring:message code="lbl.reportfilingdate" />
							</div>
							<div class="col-sm-3 add-margin view-content">
								<fmt:formatDate pattern="MM/dd/yyyyy"
									value="${lcInterimOrder.reportFilingDate}" />
							</div>
						</div>
					</div>

				</div>
			</div>
		</div>
		<div class="row text-center">
			<div class="add-margin">
				<a href="javascript:void(0)" class="btn btn-default"
					onclick="self.close()">Close</a>
			</div>
		</div>
</form:form>