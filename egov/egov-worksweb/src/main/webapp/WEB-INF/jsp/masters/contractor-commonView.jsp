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

<div class="row">
	<div class="col-md-12">
		<div class="panel panel-primary" data-collapsed="0">
			<div class="panel-heading">
			</div>
			<div class="panel-body">
				<div class="row add-border">
					<div class="col-xs-3 add-margin">
						<s:text name="contractor.code" />
					</div>
					<div class="col-xs-3 add-margin view-content">
						<s:property value="%{code}" />
					</div>
					<div class="col-xs-3 add-margin">
						<s:text name="contractor.name" />
					</div>
					<div class="col-xs-3 add-margin view-content">
						<s:property value="%{name}" />
					</div>
				</div>
				<div class="row add-border">
					<div class="col-xs-3 add-margin">
						<s:text name="contractor.correspondenceAddress" />
					</div>
					<div class="col-xs-3 add-margin view-content">
						<s:property value="%{correspondenceAddress}"
							default="hello my name " />
					</div>
					<div class="col-xs-3 add-margin">
						<s:text name="contractor.paymentAddress" />
					</div>
					<div class="col-xs-3 add-margin view-content">
						<s:property value="%{paymentAddress}" />
					</div>
				</div>
				<div class="row add-border">
					<div class="col-xs-3 add-margin">
						<s:text name="contractor.contactPerson" />
					</div>
					<div class="col-xs-3 add-margin view-content">
						<s:property value="%{contactPerson}" />
					</div>
					<div class="col-xs-3 add-margin">
						<s:text name="contractor.email" />
					</div>
					<div class="col-xs-3 add-margin view-content">
						<s:property value="%{email}" />
					</div>
				</div>
				<div class="row add-border">
					<div class="col-xs-3 add-margin">
						<s:text name="contractor.narration" />
					</div>
					<div class="col-xs-9 add-margin view-content">
						<s:property value="%{narration}" />
					</div>
				</div>
				<div class="row add-border">
					<div class="col-xs-3 add-margin">
						<s:text name="contractor.panNo" />
					</div>
					<div class="col-xs-3 add-margin view-content">
						<s:property value="%{panNumber}" />
					</div>
					<div class="col-xs-3 add-margin">
						<s:text name="contractor.tinNo" />
					</div>
					<div class="col-xs-3 add-margin view-content">
						<s:property value="%{tinNumber}" />
					</div>
				</div>
				<div class="row add-border">
					<div class="col-xs-3 add-margin">
						<s:text name="contractor.bank" />
					</div>
					<div class="col-xs-3 add-margin view-content">
						<s:property value="%{bank.name}" />
					</div>
					<div class="col-xs-3 add-margin">
						<s:text name="contractor.ifscCode" />
					</div>
					<div class="col-xs-3 add-margin view-content">
						<s:property value="%{ifscCode}" />
					</div>
				</div>
				<div class="row add-border">
					<div class="col-xs-3 add-margin">
						<s:text name="contractor.bankAccount" />
					</div>
					<div class="col-xs-3 add-margin view-content">
						<s:property value="%{bankAccount}" />
					</div>
					<div class="col-xs-3 add-margin">
						<s:text name="contractor.pwdApprovalCode" />
					</div>
					<div class="col-xs-3 add-margin view-content">
						<s:property value="%{pwdApprovalCode}" />
					</div>
				</div>
				<div class="row">
					<div class="col-xs-3 add-margin">
						<s:text name="contractor.exemptionFrom" />
					</div>
					<div class="col-xs-3 add-margin view-content" id="exemptionForm">
						<s:property value="%{exemptionForm}" />
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<div class="panel panel-primary" data-collapsed="0">
	<div class="panel-heading">
		<div class="panel-title">
			<s:text name="contractor.contDetails" />
		</div>
	</div>
	<div class="panel-body">
		<table class="table table-bordered">
			<thead>
				<tr>
					<th><s:text name="column.title.SLNo" /></th>
					<th><s:text name="contractor.department" /></th>
					<th><s:text name="contractor.registrationNo" /></th>
					<th><s:text name="contractor.grade" /></th>
					<th><s:text name="contractor.status" /></th>
					<th><s:text name="contractor.fromDate" /></th>
					<th><s:text name="contractor.toDate" /></th>
				</tr>
			</thead>
			<tbody>
			<s:iterator value="model.contractorDetails" status="row_status">
				<tr>
					<td><s:property value="#row_status.count" /></td>
					<td><s:property value="%{department.name}" /></td>
					<td><s:property value="%{registrationNumber}" /></td>
					<td><s:property value="%{grade.grade}" /></td>
					<td><s:property value="%{status.description}" /></td>
					<td><s:date name="validity.startDate" format="dd/MM/yyyy" /></td>
					<td><s:date name="validity.endDate" format="dd/MM/yyyy" /></td>
				</tr>
			</s:iterator>
			</tbody>
		</table>
	</div>
</div>