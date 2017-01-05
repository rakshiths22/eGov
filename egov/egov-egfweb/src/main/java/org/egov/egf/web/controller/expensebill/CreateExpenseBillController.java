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
package org.egov.egf.web.controller.expensebill;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.egov.egf.budget.service.BudgetControlTypeService;
import org.egov.egf.expensebill.service.ExpenseBillService;
import org.egov.egf.utils.FinancialUtils;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.infra.workflow.multitenant.model.WorkflowBean;
import org.egov.infra.workflow.multitenant.model.WorkflowConstants;
import org.egov.infra.workflow.multitenant.service.BaseWorkFlow;
import org.egov.model.bills.EgBillregister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author venki
 *
 */

@Controller
@RequestMapping(value = "/expensebill")
public class CreateExpenseBillController extends BaseBillController {

    private static final String DESIGNATION = "designation";

    private static final String NET_PAYABLE_ID = "netPayableId";

    private static final String EXPENSEBILL_FORM = "expensebill-form";
    
    private static final String MESSAGE = "message";

    private static final String STATE_TYPE = "stateType";

    private static final String APPROVAL_POSITION = "approvalPosition";

    private static final String APPROVAL_DESIGNATION = "approvalDesignation";

    @Autowired
    private BaseWorkFlow baseWorkFlow;




    public CreateExpenseBillController(final AppConfigValueService appConfigValuesService) {
        super(appConfigValuesService);
    }

    @Autowired
    @Qualifier("messageSource")
    private MessageSource messageSource;

    @Autowired
    private ExpenseBillService expenseBillService;

    @Autowired
    private BudgetControlTypeService budgetControlTypeService;

    @Autowired
    private FinancialUtils financialUtils;

    @Override
    protected void setDropDownValues(final Model model) {
        super.setDropDownValues(model);
    }

    @RequestMapping(value = "/newform", method = RequestMethod.GET)
    public String showNewForm(@ModelAttribute("egBillregister") final EgBillregister egBillregister, final Model model) {

        setDropDownValues(model);
        model.addAttribute(STATE_TYPE, egBillregister.getClass().getSimpleName());
        WorkflowBean workflowBean=new WorkflowBean();
        workflowBean.setBusinessKey(egBillregister.getClass().getSimpleName());
        prepareWorkflow(model, egBillregister, workflowBean);
       
        egBillregister.setBilldate(new Date());
        return EXPENSEBILL_FORM;  
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(@ModelAttribute("egBillregister") final EgBillregister egBillregister, final Model model,
            final BindingResult resultBinder, final HttpServletRequest request,final RedirectAttributes redirectAttrs)
                    throws IOException {
        WorkflowBean workflowBean = baseWorkFlow.populateWorkflowBean(request);  
        populateBillDetails(egBillregister);
        validateBillNumber(egBillregister, resultBinder);
        validateLedgerAndSubledger(egBillregister, resultBinder);

        if (resultBinder.hasErrors()) {
            fillOnErrors(egBillregister, model, request,workflowBean);
            return EXPENSEBILL_FORM;
        }  

        EgBillregister savedEgBillregister;
        String msg="";
        try {
            savedEgBillregister = expenseBillService.create(egBillregister,workflowBean);
             msg=    generateMessage(egBillregister,workflowBean);
        } catch (final ValidationException e) {
            fillOnErrors(egBillregister, model, request,workflowBean);
            resultBinder.reject("", e.getErrors().get(0).getMessage());
            return EXPENSEBILL_FORM;
        }
        return "redirect:/expensebill/success?message="+msg;


    }

    private String generateMessage(EgBillregister egBillregister, WorkflowBean workflowBean) {
        String message="";
        switch(workflowBean.getWorkflowAction().toLowerCase())   
        {
        case  WorkflowConstants.ACTION_APPROVE :
            message=messageSource.getMessage("msg.expense.bill.approved.success",
                    new String[] {egBillregister.getBillnumber()},Locale.getDefault());
            break;
        case  WorkflowConstants.ACTION_REJECT :
            message=messageSource.getMessage("msg.expense.bill.reject", new String[] {egBillregister.getBillnumber(),workflowBean.getApproverName(),workflowBean.getApproverDesignationName()},null);
            break;   
        case  WorkflowConstants.ACTION_FORWARD :
            message=messageSource.getMessage("msg.expense.bill.create.success",
                    new String[] {egBillregister.getBillnumber(),workflowBean.getApproverName(),workflowBean.getApproverDesignationName()},null);
            break;    
        case  WorkflowConstants.ACTION_CANCEL :
            message=messageSource.getMessage("msg.expense.bill.cancel",
                    new String[] {egBillregister.getBillnumber()},Locale.getDefault());
            break; 
        case  WorkflowConstants.ACTION_SAVE :
            message=messageSource.getMessage("msg.expense.bill.saved.success",
                    new String[] {egBillregister.getBillnumber()},Locale.getDefault());
            break;   

        }
        return message;
    }

    private void fillOnErrors(final EgBillregister egBillregister, final Model model,HttpServletRequest request, WorkflowBean workflowBean) {
        setDropDownValues(model);
        model.addAttribute(STATE_TYPE, egBillregister.getClass().getSimpleName());
        prepareWorkflow(model, egBillregister, workflowBean);
        model.addAttribute(NET_PAYABLE_ID, request.getParameter(NET_PAYABLE_ID));
        model.addAttribute(APPROVAL_DESIGNATION, request.getParameter(APPROVAL_DESIGNATION));
        model.addAttribute(APPROVAL_POSITION, request.getParameter(APPROVAL_POSITION));
        model.addAttribute(DESIGNATION, request.getParameter(DESIGNATION));
        egBillregister.getBillPayeedetails().clear();
        prepareBillDetailsForView(egBillregister);
    }

    @RequestMapping(value = "/success", method = RequestMethod.GET)
    public String showSuccessPage(final Model model,
            final HttpServletRequest request ) {
              
             String message = request.getParameter(MESSAGE);
              model.addAttribute(MESSAGE, message);
        
        return "expensebill-success";
    }

}