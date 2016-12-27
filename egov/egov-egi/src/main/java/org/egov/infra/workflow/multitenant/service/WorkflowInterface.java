package org.egov.infra.workflow.multitenant.service;

import java.util.List;

import org.egov.infra.workflow.multitenant.model.ProcessInstance;
import org.egov.infra.workflow.multitenant.model.Task;

public interface WorkflowInterface {
    
     ProcessInstance start(String jurisdiction,ProcessInstance processInstance);
     ProcessInstance getProcess(String jurisdiction,ProcessInstance processInstance);
     List<Task> getTasks(String jurisdiction,ProcessInstance processInstance);
     ProcessInstance update(String jurisdiction,ProcessInstance task);
     
     //complete it first
     
}