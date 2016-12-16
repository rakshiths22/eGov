package org.egov.egf.workflow.multitenant.service;

import org.egov.eis.service.AssignmentService;
import org.egov.eis.service.PositionMasterService;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.workflow.entity.State;
import org.egov.infra.workflow.entity.State.StateStatus;
import org.egov.infra.workflow.entity.StateHistory;
import org.egov.infra.workflow.matrix.entity.WorkFlowMatrix;
import org.egov.infra.workflow.matrix.service.CustomizedWorkFlowService;
import org.egov.infra.workflow.multitenant.model.ProcessInstance;
import org.egov.infra.workflow.multitenant.model.WorkflowEntity;
import org.egov.infra.workflow.multitenant.service.Workflow;
import org.egov.infra.workflow.service.StateHistoryService;
import org.egov.infra.workflow.service.StateService;
import org.egov.pims.commons.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FinancialWorkflowService implements Workflow {

    @Autowired
    @Qualifier("assignmentService")
    private AssignmentService assignmentService;
    
    @Autowired
    private PositionMasterService positionMasterService;
    
    @Autowired
    private StateService stateService;
    
    @Autowired
    private StateHistoryService stateHistoryService;
    
    
    @Autowired
    private CustomizedWorkFlowService  workflowService;

    @Transactional
    @Override
    public ProcessInstance start(String jurisdiction, ProcessInstance processInstance) {
          WorkFlowMatrix wfMatrix = workflowService.getWfMatrix(processInstance.getBusinessKey(), null,
                null, null, null, null);
      Position owner=positionMasterService.getPositionById(Long.valueOf(processInstance.getAssignee()));
      State   state = new State();
      state.setType(processInstance.getType());
      state.setStatus(StateStatus.STARTED);
      state.setValue(State.DEFAULT_STATE_VALUE_CREATED);
      state.setComments(State.DEFAULT_STATE_VALUE_CREATED);
      state.setOwnerPosition(owner);
      
      stateService.create(state);
      
     
      return processInstance;
    }

    @Override
    public ProcessInstance update(String jurisdiction, ProcessInstance task) {
        Position owner=null;
        if(task.getAssignee()!=null)
         owner=positionMasterService.getPositionById(Long.valueOf(task.getAssignee()));
        WorkflowEntity stateAware=(WorkflowEntity) task.getEntity();
        if(task.getAction().equals("reject"))
        {
            User createdBy = stateAware.getCreatedBy(); 
            owner=positionMasterService.getPositionByUserId(createdBy.getId());
        }
        State state=   stateService.getStateById(Long.valueOf(task.getId()));
        state.addStateHistory(new StateHistory(state));
        state.setStatus(StateStatus.INPROGRESS);
        stateService.update(state);
        
       return task;
    }

}
