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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.math.NumberUtils;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.admin.master.service.DepartmentService;
import org.egov.infra.workflow.multitenant.model.ProcessInstance;
import org.egov.infra.workflow.multitenant.model.Task;
import org.egov.infra.workflow.multitenant.model.WorkflowBean;
import org.egov.infra.workflow.multitenant.model.WorkflowConstants;
import org.egov.infra.workflow.multitenant.model.WorkflowEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
public  class BaseWorkFlow {
    
	 
    
    
    @Autowired
    ApplicationContext applicationContext;
    
    @Autowired
    protected DepartmentService departmentService;
    
    public WorkflowEntity transitionWorkFlow(WorkflowEntity workflowEntity, WorkflowBean workflowBean) {
        
        WorkflowInterface    workflowInterface=getWorkflowImplementation(workflowEntity,workflowBean);
        if(workflowBean.getWorkflowId()==null || workflowBean.getWorkflowId().isEmpty() )
        {
        ProcessInstance pi=workflowBean.mapProcessInstance(workflowEntity);
          pi=  workflowInterface.start("", pi);
          workflowEntity.setWorkflowId(pi.getId()); 
          if(pi.getAssignee()!=null)
          workflowBean.setApproverPositionId(Long.valueOf(pi.getAssignee()));
          if(workflowBean.getWorkflowAction().equalsIgnoreCase(WorkflowConstants.ACTION_REJECT))
          {
              workflowBean.setApproverDesignationName(pi.getAttributes().get("approverDesignationName").getCode()); 
              workflowBean.setApproverName(pi.getAttributes().get("approverName").getCode()); 
              
          }
          workflowBean.setAttributes(pi.getAttributes());
          
          if(NumberUtils.isDigits(pi.getId()))
            workflowEntity.setState_id(Long.valueOf(pi.getId()));
        }
        else
        {
            Task  task=workflowBean.mapTask(workflowEntity);
            task=   workflowInterface.update("", task); 
       
        workflowEntity.setWorkflowId(task.getId()); 
        if(task.getAssignee()!=null)
        workflowBean.setApproverPositionId(Long.valueOf(task.getAssignee()));
        if(workflowBean.getWorkflowAction().equalsIgnoreCase(WorkflowConstants.ACTION_REJECT))
        {
            workflowBean.setApproverDesignationName(task.getAttributes().get("approverDesignationName").getCode()); 
            workflowBean.setApproverName(task.getAttributes().get("approverName").getCode()); 
            
        }
        workflowBean.setAttributes(task.getAttributes());
        if(NumberUtils.isDigits(task.getId()))
            workflowEntity.setState_id(Long.valueOf(task.getId()));
        }
      
        return workflowEntity;
    }

    public WorkflowInterface getWorkflowImplementation(WorkflowEntity workflowEntity, WorkflowBean workflowBean2) {
        return (WorkflowInterface)applicationContext.getBean("internalDefaultWorkflow");
     }
    
    public WorkflowInterface getWorkflowImplementation(ProcessInstance p) {
        //this will decide by p.getBusinessKey and p.getId
        
        return (WorkflowInterface)applicationContext.getBean("internalDefaultWorkflow");
     }
    private ProcessInstance setUpProcessInstance(WorkflowBean workflowBean,WorkflowEntity workflowEntity) {
        ProcessInstance pi=new ProcessInstance();
        if(workflowBean.getWorkflowId()!=null && !workflowBean.getWorkflowId().isEmpty())
        {
            pi.setId(workflowBean.getWorkflowId());
        }
        if(workflowEntity.getWorkflowId()!=null && !workflowEntity.getWorkflowId().isEmpty())
        {
            pi.setId(workflowEntity.getWorkflowId());
        }
        pi.setBusinessKey(workflowBean.getBusinessKey());
        pi.setEntity(workflowEntity);
        pi.setStatus(workflowBean.getCurrentState());
        pi.setAction(workflowBean.getWorkflowAction());
        if(workflowBean.getApproverPositionId()!=null)  
            pi.setAsignee(workflowBean.getApproverPositionId().toString());
        pi.setDescription(workflowBean.getWorkflowComments());
        pi.setAttributes(workflowBean.getAttributes()); 
        return pi;   
          
      }
    private void setUpWorkflowBean(ProcessInstance pi,WorkflowBean workflowBean) {
        workflowBean.setBusinessKey(pi.getBusinessKey());
        workflowBean.setValidActions(pi.getAttributes().get("validActions").getValues());
       // workflowBean.setDepartmentList(pi.getAttributes().get("departmentList").getValues());
        workflowBean.setNextAction((pi.getAttributes().get("nextAction").getCode()));
        workflowBean.setCurrentState(pi.getStatus());
        if( workflowBean.getCurrentState()==null ||workflowBean.getCurrentState().isEmpty())
        {
            workflowBean.setCurrentState("NEW");
        }
        if(pi.getAssignee()!=null)
        workflowBean.setCurrentPositionId(Long.valueOf(pi.getAssignee()));
        if(workflowBean.getWorkflowId()==null)
        workflowBean.setWorkflowId(pi.getId());
        workflowBean.setDesignationList(Collections.EMPTY_LIST);
        workflowBean.setUserList(Collections.EMPTY_LIST);
        if(pi.getAssignee()!=null)
        workflowBean.setApproverPositionId(Long.valueOf(pi.getAssignee())); 
        workflowBean.setAttributes(pi.getAttributes());  
       
      }
    
    protected WorkflowBean prepareWorkflow(final Model prepareModel, final WorkflowEntity model, WorkflowBean workflowBean) {
        
        ProcessInstance pi= setUpProcessInstance(workflowBean,model);
        WorkflowInterface workflowImplementation = getWorkflowImplementation(model, workflowBean);
        pi= workflowImplementation.getProcess("", pi);
        setUpWorkflowBean(pi,workflowBean);
        if(workflowBean.getDepartmentList()==null)
        {
            workflowBean.setDepartmentList(addAllDepartments());
        }
        if(prepareModel!=null)
            
        prepareModel.addAttribute("workflowBean", workflowBean);
        return workflowBean;
    }
    
    public List<Department> addAllDepartments() {
        return departmentService.getAllDepartments();
    }

    public WorkflowBean populateWorkflowBean(HttpServletRequest request) {
        WorkflowBean workflowBean=new WorkflowBean();
        String amountRule = request.getParameter("workflowBean.amountRule");
        if(amountRule!=null && !amountRule.isEmpty())
            workflowBean.setAmountRule(BigDecimal.valueOf(Long.valueOf(amountRule)));
        
        String appDept = request.getParameter("workflowBean.approverDepartmentId");
        if(appDept!=null && !appDept.isEmpty())
            workflowBean.setApproverDepartmentId(Long.valueOf(appDept));
        String desig = request.getParameter("workflowBean.currentDesignationId");
        if(desig!=null && !desig.isEmpty() )
           workflowBean.setCurrentDesignationId(Long.valueOf(desig));
        String position = request.getParameter("workflowBean.approverPositionId");
        if(position!=null && !position.isEmpty())
            workflowBean.setApproverPositionId(Long.valueOf(request.getParameter("workflowBean.approverPositionId")));
        if(request.getParameter("workflowBean.workflowAction")!=null)
            workflowBean.setWorkflowAction(request.getParameter("workflowBean.workflowAction"));
        if(request.getParameter("workflowBean.workflowComments")!=null)
            workflowBean.setWorkflowComments(request.getParameter("workflowBean.workflowComments"));
        if(request.getParameter("workflowBean.currentState")!=null)
            workflowBean.setCurrentState(request.getParameter("workflowBean.currentState"));
        if(request.getParameter("workflowBean.approverName")!=null)
            workflowBean.setApproverName(request.getParameter("workflowBean.approverName"));
        if(request.getParameter("workflowBean.businessKey")!=null)
        workflowBean.setBusinessKey(request.getParameter("workflowBean.businessKey"));
        
        if(request.getParameter("workflowBean.workflowId")!=null)
            workflowBean.setWorkflowId(request.getParameter("workflowBean.workflowId"));
        
        return workflowBean;
        
    }

    public List<Task> getWorkflowHistory(WorkflowEntity entity,WorkflowBean workflowBean) {
        WorkflowInterface workflowImplementation = getWorkflowImplementation(entity,workflowBean);
        return workflowImplementation.getHistoryDetail(entity.getWorkflowId());
    }
    
    public ProcessInstance getProcess(ProcessInstance p)
    {
      return  getWorkflowImplementation(p).getProcess("jurisdiction", p);
    }




}
