package org.egov.infra.workflow.multitenant.service;

import org.egov.infra.workflow.multitenant.model.ProcessInstance;
import org.egov.infra.workflow.multitenant.model.Task;

public interface Workflow {
    
     ProcessInstance start(String jurisdiction,ProcessInstance processInstance);
     ProcessInstance update(String jurisdiction,ProcessInstance task);
}