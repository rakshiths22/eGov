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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.admin.master.service.DepartmentService;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infra.workflow.entity.State;
import org.egov.infra.workflow.matrix.entity.WorkFlowMatrix;
import org.egov.infra.workflow.matrix.service.CustomizedWorkFlowService;
import org.egov.infra.workflow.multitenant.model.WorkflowContainer;
import org.egov.infra.workflow.multitenant.model.WorkflowEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

@Component
public abstract class BaseWorkFlowAction extends BaseFormAction{

    @Autowired
    protected CustomizedWorkFlowService customizedWorkFlowService;

    @Autowired
    protected DepartmentService departmentService;
    
    private State state;

    
    public List<Department> addAllDepartments() {
        return departmentService.getAllDepartments();
    }

    public WorkflowContainer getWorkflowContainer() {
        return new WorkflowContainer();
    }
    protected String workFlowAction;
    protected String approverComments;
    protected String currentState;
    protected String currentDesignation;
    protected String additionalRule;
    protected BigDecimal amountRule;
    protected String workFlowDepartment;
    protected String pendingActions;
    protected String approverName;
    protected String approverDepartment;
    protected String approverDesignation;
    protected Long approverPositionId;
    /**
     * @param prepareModel
     * @param model
     * @param container
     *            This method we are calling In GET Method..
     */
    protected void prepareWorkflow(final Model prepareModel, final WorkflowEntity model, final WorkflowContainer container) {
        prepareModel.addAttribute("approverDepartmentList", addAllDepartments());
        prepareModel.addAttribute("validActionList", getValidActions(model, container));
        prepareModel.addAttribute("nextAction", getNextAction(model, container));

    }
     
    

    

    /**
     * @param model
     * @param container
     * @return NextAction From Matrix With Parameters
     *         Type,CurrentState,CreatedDate
     */
    public String getNextAction(final WorkflowEntity model, final WorkflowContainer container) {

        WorkFlowMatrix wfMatrix = null;
        if (null != model && null != model.getId())
            if (null != model.getProcessInstance())
                wfMatrix = customizedWorkFlowService.getWfMatrix(model.getProcessInstance().getBusinessKey(),
                        container.getWorkFlowDepartment(), container.getAmountRule(), container.getAdditionalRule(),
                        "", container.getPendingActions(), model.getCreatedDate());
            else
                wfMatrix = customizedWorkFlowService.getWfMatrix(model.getProcessInstance().getBusinessKey(),
                        container.getWorkFlowDepartment(), container.getAmountRule(), container.getAdditionalRule(),
                        State.DEFAULT_STATE_VALUE_CREATED, container.getPendingActions(), model.getCreatedDate());
        return wfMatrix == null ? "" : wfMatrix.getNextAction();
    }

    /**
     * @param model
     * @param container
     * @return List of WorkFlow Buttons From Matrix By Passing parametres
     *         Type,CurrentState,CreatedDate
     */
    public List<String> getValidActions(final WorkflowEntity model, final WorkflowContainer container) {
        List<String> validActions = Collections.emptyList();
        if (null == model
                || null == model.getId() || (model.getProcessInstance()==null) 
                || (model != null && model.getProcessInstance() != null ? model.getProcessInstance().getStatus()
                        .equals("Closed")
                        || model.getProcessInstance().getStatus().equals("END") : false))
            validActions = Arrays.asList("Forward");
        else if (null != model.getProcessInstance())
            validActions = customizedWorkFlowService.getNextValidActions(model.getProcessInstance().getBusinessKey(), container
                    .getWorkFlowDepartment(), container.getAmountRule(), container.getAdditionalRule(), model
                    .getProcessInstance().getStatus(), container.getPendingActions(), model.getCreatedDate());
        else
            // FIXME This May not work
            validActions = customizedWorkFlowService.getNextValidActions(model.getProcessInstance().getBusinessKey(),
                    container.getWorkFlowDepartment(), container.getAmountRule(), container.getAdditionalRule(),
                    State.DEFAULT_STATE_VALUE_CREATED, container.getPendingActions(), model.getCreatedDate());
        return validActions;
    }
    
    protected String getPendingActions() {
        return null;
    }

    public CustomizedWorkFlowService getCustomizedWorkFlowService() {
        return customizedWorkFlowService;
    }

    public void setCustomizedWorkFlowService(CustomizedWorkFlowService customizedWorkFlowService) {
        this.customizedWorkFlowService = customizedWorkFlowService;
    }

    public DepartmentService getDepartmentService() {
        return departmentService;
    }

    public void setDepartmentService(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getWorkFlowAction() {
        return workFlowAction;
    }

    public void setWorkFlowAction(String workFlowAction) {
        this.workFlowAction = workFlowAction;
    }

    public String getApproverComments() {
        return approverComments;
    }

    public void setApproverComments(String approverComments) {
        this.approverComments = approverComments;
    }

    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    public String getCurrentDesignation() {
        return currentDesignation;
    }

    public void setCurrentDesignation(String currentDesignation) {
        this.currentDesignation = currentDesignation;
    }

    public String getAdditionalRule() {
        return additionalRule;
    }

    public void setAdditionalRule(String additionalRule) {
        this.additionalRule = additionalRule;
    }

    public BigDecimal getAmountRule() {
        return amountRule;
    }

    public void setAmountRule(BigDecimal amountRule) {
        this.amountRule = amountRule;
    }

    public String getWorkFlowDepartment() {
        return workFlowDepartment;
    }

    public void setWorkFlowDepartment(String workFlowDepartment) {
        this.workFlowDepartment = workFlowDepartment;
    }

    public String getApproverName() {
        return approverName;
    }

    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }

    public String getApproverDepartment() {
        return approverDepartment;
    }

    public void setApproverDepartment(String approverDepartment) {
        this.approverDepartment = approverDepartment;
    }

    public String getApproverDesignation() {
        return approverDesignation;
    }

    public void setApproverDesignation(String approverDesignation) {
        this.approverDesignation = approverDesignation;
    }

    public Long getApproverPositionId() {
        return approverPositionId;
    }

    public void setApproverPositionId(Long approverPositionId) {
        this.approverPositionId = approverPositionId;
    }

    public void setPendingActions(String pendingActions) {
        this.pendingActions = pendingActions;
    }

    @Transactional
    public WorkflowEntity transitionWorkFlow(final WorkflowEntity voucherHeader, WorkflowContainer workflowBean) {
       
        return voucherHeader;
    }

}
