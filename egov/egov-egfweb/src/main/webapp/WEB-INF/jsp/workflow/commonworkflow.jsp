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


<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/includes/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%try{ %>
<script>
	function getUsersByDesignationAndDept() {
		populateapproverPositionId({
			approverDepartmentId : document
					.getElementById('approverDepartmentId').value,
			designationId : document.getElementById('approverDesignationId').value
		});
	}

	function callAlertForDepartment() {
		var value = document.getElementById("approverDepartmentId").value;
		if (value == "-1") {
			bootbox.alert("Please select the Approver Department");
			document.getElementById("approverDepartmentId").focus();
			return false;
		}
	}

	function callAlertForDesignation() {
		var value = document.getElementById("approverDesignationId").value;
		if (value == "-1") {
			bootbox.alert("Please select the approver designation");
			document.getElementById("approverDesignationId").focus();
			return false;
		}
	}

	function loadDesignationByDeptAndType(typeValue, departmentValue,
			currentStateValue, amountRuleValue, additionalRuleValue,
			pendingActionsValue) {
		var designationObj = document.getElementById('approverDesignationId');
		designationObj.options.length = 0;
		designationObj.options[0] = new Option("----Choose----", "-1");
		var approverObj = document.getElementById('approverPositionId');
		approverObj.options.length = 0;
		approverObj.options[0] = new Option("----Choose----", "-1");
		populateapproverDesignationId({
			departmentRule : departmentValue,
			type : typeValue,
			amountRule : amountRuleValue,
			additionalRule : additionalRuleValue,
			currentState : currentStateValue,
			pendingAction : pendingActionsValue
		});
	}

	function loadDesignationFromMatrix() {
		var e = dom.get('approverDepartmentId');
		var dept = e.options[e.selectedIndex].text;
		var currentState = dom.get('currentState').value;
		var amountRule = dom.get('amountRule').value;
		var additionalRule = dom.get('additionalRule').value;
		var pendingAction = document.getElementById('pendingActions').value;
		var stateType=document.getElementById('businessKey').value;
		loadDesignationByDeptAndType(stateType, dept, currentState, amountRule,
				additionalRule, pendingAction);
	}

	function populateApprover() {
		getUsersByDesignationAndDept();
	}

	function setDesignation() {
		document.getElementById("workflowBean.approverDesignationId").value = '<s:property value="%{workflowBean.approverDesignationId}"/>';
		populateApprover();
	}

	function setApprover() {
		document.getElementById("workflowBean.approverPositionId").value = '<s:property value="%{workflowBean.approverPositionId}"/>';
	}
</script>

	<s:if test="%{!'Closed'.equalsIgnoreCase(workflowBean.currentState)}">
		<s:hidden id="currentState" name="workflowBean.currentState" />
	</s:if>
	<s:else>
		<s:hidden id="currentState" name="currentState"   />
	</s:else>
	<s:hidden id="currentDesignation" name="workflowBean.currentDesignation" />
	<s:hidden id="additionalRule" name="workflowBean.additionalRule" />
	<s:hidden id="amountRule" name="workflowBean.amountRule"  />
	<s:hidden id="workFlowDepartment" name="workflowBean.workFlowDepartment" />
	<s:hidden id="pendingActions" name="workflowBean.pendingActions"/>
	<s:hidden id="approverName" name="workflowBean.approverName" />
	<s:hidden id="businessKey" name="workflowBean.businessKey" />
	<s:hidden id="workflowId" name="workflowBean.workflowId"  />

	<s:if test="%{#request.approverOddTextCss==null}">
		<c:set var="approverOddTextCss" value="greybox" scope="request" />
		<c:set var="approverOddCSS" value="greybox" scope="request" />
	</s:if>

	<s:if test="%{#request.approverEvenTextCSS==null}">
		<c:set var="approverEvenTextCSS" value="bluebox" scope="request" />
		<c:set var="approverEvenCSS" value="bluebox" scope="request" />
	</s:if>

	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	</br>
		<tr>
			<div class="headingsmallbg">
				<span class="bold"><s:text name="title.approval.information" /></span>
			</div>

		</tr>
		</br>
		<s:if test="%{workflowBean.nextAction!='END'}">
		<tr>
			<td class="${approverOddCSS}" width="5%">&nbsp;</td>
			<td class="${approverOddCSS}" id="deptLabel" width="14%"><s:text
					name="wf.approver.department" />:</td>
			<td class="${approverOddTextCss}" width="14%"><s:select
					name="workflowBean.approverDepartmentId" id="approverDepartmentId"
					list="workflowBean.departmentList" listKey="id"
					listValue="name" headerKey="-1" headerValue="----Choose----"
					 
					onchange="loadDesignationFromMatrix();" cssClass="dropDownCss" />
				<egov:ajaxdropdown id="approverDesignationId" fields="['Text','Value']"
					url="workflow/ajaxWorkFlow-getDesignationsByObjectType.action"
					 dropdownId="approverDesignationId"
					contextToBeUsed="/eis" afterSuccess="setDesignation();" /></td>
			<td class="${approverOddCSS}" width="14%"><s:text
					name="wf.approver.designation" />:</td>
			<td class="${approverOddTextCss}" width="14%"><s:select
					id="approverDesignationId" name="workflowBean.approverDesignationId"
					list="workflowBean.designationList" listKey="designationId"
					headerKey="-1" listValue="designationName"
					headerValue="----Choose----" onchange="populateApprover();"
					onfocus="callAlertForDepartment();" cssClass="dropDownCss" /> 
					<egov:ajaxdropdown 	id="approverPositionId"
					fields="['Text','Value']"
					url="workflow/ajaxWorkFlow-getPositionByPassingDesigId.action"
					dropdownId="approverPositionId"
					contextToBeUsed="/eis"
					afterSuccess="setApprover();" /></td>
			<td class="${approverOddCSS}" width="14%"><s:text
					name="wf.approver" />:</td>
			<td class="${approverOddTextCss}" width="14%"><s:select
					id="approverPositionId" name="workflowBean.approverPositionId"
					list="workflowBean.userList" headerKey="-1"
					headerValue="----Choose----" listKey="id" listValue="firstName"
					onfocus="callAlertForDesignation();"  
					cssClass="dropDownCss" /></td>
			<td class="${approverOddCSS}" width="5%">&nbsp;</td>
		</tr>
		</s:if>
	</table>

<br />

<div id="workflowCommentsDiv" align="center">
	<table width="100%">
		<tr>
			<td width="10%" class="${approverEvenCSS}">&nbsp;</td>
			<td width="20%" class="${approverEvenCSS}">&nbsp;</td>
			<td class="${approverEvenCSS}" width="13%"><s:text
					name="wf.approver.remarks" />:</td>
			<td class="${approverEvenTextCSS}"><textarea
					id="workflowComments" name="workflowBean.workflowComments" rows="2" cols="35"></textarea>
			</td>
			<td class="${approverEvenCSS}">&nbsp;</td>
			<td width="10%" class="${approverEvenCSS}">&nbsp;</td>
			<td class="${approverEvenCSS}">&nbsp;</td>
		</tr>
	</table>
</div>
<%}catch(Exception e)
{
    e.printStackTrace();
 
}%>
