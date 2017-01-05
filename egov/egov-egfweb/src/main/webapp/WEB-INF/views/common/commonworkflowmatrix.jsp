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

<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/tags/cdn.tld" prefix="cdn" %>
<form:hidden  path="" id="workflowAction" name="workflowBean.workflowAction"/>	
<div class="panel panel-primary" data-collapsed="0" >	
	<c:if test="${workflowBean.nextAction !='END'}" > 
			
		<div class="panel-heading">
			<div class="panel-title">
				<spring:message code="lbl.approverdetails"/>
			</div>					
		</div>
	</c:if>
		<div class="panel-body">
		 
	<input type="hidden"  id="currentState" name="workflowBean.currentState"  value="${workflowBean.currentState}"/>
	
	<input type="hidden" name="workflowBean.currentDesignationId" value="${workflowBean.currentDesignationId}" id="currentDesignation"/>
	<input type="hidden" name="workflowBean.additionalRule" value="${ workflowBean.additionalRule}" id="additionalRule"/>
	<input type="hidden" name="workflowBean.amountRule" value="${workflowBean.amountRule }" id="amountRule"/>
	<input type="hidden" name="workflowBean.worklfowDepartment" value="${workflowBean.workflowDepartment }" id="workflowDepartment"/>
	<input type="hidden" name="workflowBean.pendingActions" value="${workflowBean.pendingActions}" id="pendingActions"/>
	<input type="hidden" name="workflowBean.approverName" value="${workflowBean.approverName}" id="approverName"/>
	<input type="hidden" name="workflowBean.businessKey" value="${workflowBean.businessKey }" id="businessKey"/>
	<input type="hidden" name="workflowBean.workflowId" value="${workflowBean.workflowId }" id="workflowId"/>
	<input type="hidden" id="approverDesignationName" name="workflowBean.approverDesignationName" value="${workflowBean.approverDesignationName }"   />
	
  	

				<div class="row show-row"  id="approverDetailHeading">
				<c:if test="${workflowBean.nextAction !='END'}" > 
					<div class="show-row form-group" >
						<label class="col-sm-3 control-label text-right"><spring:message code="lbl.approverdepartment"/><span class="mandatory"></span></label>
						<div class="col-sm-3 add-margin">
							<form:select path="" data-first-option="false" name="workflowBean.approverDepartmentId"
								id="approverDepartmentId" cssClass="form-control"
								cssErrorClass="form-control error" required="required">
								<form:option value="">
									<spring:message code="lbl.select" />
								</form:option>
								<form:options items="${workflowBean.departmentList}" itemValue="id"
									itemLabel="name" />     
							</form:select>
						</div>
						<label class="col-sm-2 control-label text-right"><spring:message code="lbl.approverdesignation"/><span class="mandatory"></span></label>
						<div class="col-sm-3 add-margin">
							<form:select path="" data-first-option="false" name="workflowBean.approverDesignationId"
								id="approverDesignationId" cssClass="form-control" onfocus="callAlertForDepartment();"
								cssErrorClass="form-control error" required="required">  
								<form:option value="">
									<spring:message code="lbl.select" />
								</form:option>
								
							</form:select>					
						</div>
					</div>
					<div class="show-row form-group">
						<label class="col-sm-3 control-label text-right"><spring:message code="lbl.approver"/><span class="mandatory"></span></label>
						<div class="col-sm-3 add-margin">
						<form:select path="" data-first-option="false" 
							id="approverPositionId" name="workflowBean.approverPositionId" cssClass="form-control" onfocus="callAlertForDesignation();" 
							cssErrorClass="form-control error" required="required">  
							<form:option value="">
									<spring:message code="lbl.select" />
								</form:option>
						</form:select>		
						</div> 
					</div>
					</c:if>
					<div class="show-row form-group">
						<label class="col-sm-3 control-label text-right"><spring:message code="lbl.comments"/></label>
						<div class="col-sm-8 add-margin">
							<textarea class="form-control" id="workflowComments" name="workflowBean.workflowComments" ></textarea>
						</div>
					</div>
				</div>
			</div>		

</div>

<script src="<cdn:url value='/resources/app/js/common/commonworkflow.js?rnd=${app_release_no}'/>"></script>