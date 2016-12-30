/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2015>  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.infra.workflow.multitenant.service;

import java.util.List;

import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.admin.master.service.DepartmentService;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infra.workflow.entity.WorkflowTypes;
import org.egov.infra.workflow.multitenant.model.WorkflowBean;
import org.egov.infra.workflow.multitenant.model.WorkflowConstants;
import org.egov.infra.workflow.multitenant.model.WorkflowContainer;
import org.egov.infra.workflow.multitenant.model.WorkflowEntity;
import org.egov.infra.workflow.service.WorkflowTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
public abstract class BaseWorkFlowAction extends BaseFormAction {

    private static final long serialVersionUID = 5111571178164448793L;

    protected WorkflowBean workflowBean=new WorkflowBean();
    @Autowired
    protected DepartmentService departmentService;

    @Autowired
    private BaseWorkFlow baseWorkFlow;
    
    @Autowired
    private WorkflowTypeService workflowTypeService;

    public List<Department> addAllDepartments() {
        return departmentService.getAllDepartments();
    }

    /**
     * @param prepareModel
     * @param model
     * @param container This method we are calling In GET Method..
     */
    @Deprecated
    protected void prepareWorkflow(final Model prepareModel, final WorkflowEntity model, final WorkflowContainer container) {
    }

    protected WorkflowBean prepareWorkflow(final Model prepareModel, final WorkflowEntity model, WorkflowBean workflowBean) {
        workflowBean= baseWorkFlow.prepareWorkflow(prepareModel, model, workflowBean);
        if(workflowBean.getDepartmentList()==null)
        {
            workflowBean.setDepartmentList(addAllDepartments());
        }
        return workflowBean;

    }

    public WorkflowEntity transitionWorkFlow( WorkflowEntity workflowEntity, final WorkflowBean workflowBean) {
        workflowEntity= baseWorkFlow.transitionWorkFlow(workflowEntity, workflowBean);
        generateActionMessage(workflowEntity,workflowBean);
       
        
        return workflowEntity;
        
        
        
    }

    protected String generateActionMessage(WorkflowEntity workflowEntity, WorkflowBean workflowBean2) {
        WorkflowTypes type = workflowTypeService.getWorkflowTypeByType(workflowBean2.getBusinessKey());
        String message="";
        switch(workflowBean2.getWorkflowAction().toLowerCase())   
       {
       case  WorkflowConstants.ACTION_APPROVE :
           message="Workflow Item "+type.getDisplayName()+"of "+workflowEntity.getStateDetails()+" approved Successfully";
           break;
       case  WorkflowConstants.ACTION_REJECT :
           message="Workflow Item "+type.getDisplayName()+"of "+workflowEntity.getStateDetails()+" Rejected Successfully"
                   +" and sent back to creator";
           break;   
       case  WorkflowConstants.ACTION_FORWARD :
           message="Workflow Item "+type.getDisplayName()+"of "+workflowEntity.getStateDetails()+" Forwarded Successfully"
                   + " to "+workflowBean2.getApproverName();
           break;    
       case  WorkflowConstants.ACTION_CANCEL :
           message="Workflow Item "+type.getDisplayName()+"of "+workflowEntity.getStateDetails()+" Cancelled Successfully";
           break; 
       case  WorkflowConstants.ACTION_SAVE :
           message="Workflow Item "+type.getDisplayName()+"of "+workflowEntity.getStateDetails()+" Saved Successfully";
           break;   
           
       }
        return message;
   
    }

   
    public DepartmentService getDepartmentService() {
        return departmentService;
    }

    public void setDepartmentService(final DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    public WorkflowBean getWorkflowBean() {
        return workflowBean;
    }

    public void setWorkflowBean(WorkflowBean workflowBean) {
        this.workflowBean = workflowBean;
    }

}
