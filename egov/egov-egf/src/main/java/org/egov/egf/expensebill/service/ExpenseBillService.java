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
package org.egov.egf.expensebill.service;

import static org.egov.utils.FinancialConstants.BILLTYPE_FINAL_BILL;
import static org.egov.utils.FinancialConstants.CONTINGENCYBILL_APPROVED_STATUS;
import static org.egov.utils.FinancialConstants.CONTINGENCYBILL_CANCELLED_STATUS;
import static org.egov.utils.FinancialConstants.CONTINGENCYBILL_FIN;
import static org.egov.utils.FinancialConstants.STANDARD_EXPENDITURETYPE_CONTINGENT;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.script.ScriptContext;

import org.egov.commons.service.CheckListService;
import org.egov.commons.service.FundService;
import org.egov.egf.autonumber.ExpenseBillNumberGenerator;
import org.egov.egf.billsubtype.service.EgBillSubTypeService;
import org.egov.egf.expensebill.repository.ExpenseBillRepository;
import org.egov.egf.utils.FinancialUtils;
import org.egov.eis.entity.Assignment;
import org.egov.eis.service.AssignmentService;
import org.egov.eis.service.PositionMasterService;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.script.service.ScriptService;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.utils.autonumber.AutonumberServiceBeanResolver;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.infra.workflow.multitenant.model.WorkflowBean;
import org.egov.infra.workflow.multitenant.model.WorkflowConstants;
import org.egov.infra.workflow.multitenant.service.BaseWorkFlow;
import org.egov.infstr.models.EgChecklists;
import org.egov.model.bills.EgBillregister;
import org.egov.pims.commons.Position;
import org.egov.services.masters.SchemeService;
import org.egov.services.masters.SubSchemeService;
import org.egov.services.voucher.VoucherService;
import org.egov.utils.FinancialConstants;
import org.hibernate.Session;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author venki
 */

@Service
@Transactional(readOnly = true)
public class ExpenseBillService {

    private static final Logger LOG = LoggerFactory.getLogger(ExpenseBillService.class);

    @PersistenceContext
    private EntityManager entityManager;

    private final ExpenseBillRepository expenseBillRepository;

    @Autowired
    private EgBillSubTypeService egBillSubTypeService;

    @Autowired
    private SchemeService schemeService;

    @Autowired
    private SubSchemeService subSchemeService;

    @Autowired
    private FinancialUtils financialUtils;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    protected AppConfigValueService appConfigValuesService;

    @Autowired
    private AutonumberServiceBeanResolver beanResolver;

    private final ScriptService scriptExecutionService;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private PositionMasterService positionMasterService;

    @Autowired
    @Qualifier(value = "voucherService")
    private VoucherService voucherService;

    @Autowired
    private CheckListService checkListService;
    
    @Autowired
    private BaseWorkFlow baseWorkFlow;

    
    @Autowired
    @Qualifier("messageSource")
    private MessageSource messageSource;
     
    @Autowired
    private FundService fundService;

    public Session getCurrentSession() {
        return entityManager.unwrap(Session.class);
    }

    @Autowired
    public ExpenseBillService(final ExpenseBillRepository expenseBillRepository, final ScriptService scriptExecutionService) {
        this.expenseBillRepository = expenseBillRepository;
        this.scriptExecutionService = scriptExecutionService;
    }

    public EgBillregister getById(final Long id) {
        return expenseBillRepository.findOne(id);
    }

    public EgBillregister getByBillnumber(final String billNumber) {
        return expenseBillRepository.findByBillnumber(billNumber);
    }

    @Transactional
    public EgBillregister create(final EgBillregister egBillregister) {
        return expenseBillRepository.save(egBillregister);
    }

    @Transactional
    public EgBillregister create(EgBillregister bill,WorkflowBean workflowBean) {

        bill.setBilltype(BILLTYPE_FINAL_BILL);
        bill.setExpendituretype(STANDARD_EXPENDITURETYPE_CONTINGENT);
        bill.setPassedamount(bill.getBillamount());
        bill.getEgBillregistermis().setEgBillregister(bill);
        bill.getEgBillregistermis().setLastupdatedtime(new Date());

        if (bill.getEgBillregistermis().getFund() != null
                && bill.getEgBillregistermis().getFund().getId() != null)
            bill.getEgBillregistermis().setFund(
                    fundService.findOne(bill.getEgBillregistermis().getFund().getId()));
        if (bill.getEgBillregistermis().getEgBillSubType() != null
                && bill.getEgBillregistermis().getEgBillSubType().getId() != null)
            bill.getEgBillregistermis().setEgBillSubType(
                    egBillSubTypeService.getById(bill.getEgBillregistermis().getEgBillSubType().getId()));
        if (bill.getEgBillregistermis().getSchemeId() != null)
            bill.getEgBillregistermis().setScheme(
                    schemeService.findById(bill.getEgBillregistermis().getSchemeId().intValue(), false));
        else
            bill.getEgBillregistermis().setScheme(null);
        if (bill.getEgBillregistermis().getSubSchemeId() != null)
            bill.getEgBillregistermis().setSubScheme(
                    subSchemeService.findById(bill.getEgBillregistermis().getSubSchemeId().intValue(), false));
        else
            bill.getEgBillregistermis().setSubScheme(null);

        if (isBillNumberGenerationAuto())
            bill.setBillnumber(getNextBillNumber(bill));

        checkBudgetAndGenerateBANumber(bill);
        bill= expenseBillRepository.save(bill);
        createCheckList(bill, bill.getCheckLists());
        transitionWorkflow(bill,workflowBean);
        bill.getEgBillregistermis().setSourcePath("/EGF/expensebill/view/" + bill.getId().toString());
        return expenseBillRepository.save(bill);
    }

    private void transitionWorkflow(EgBillregister savedEgBillregister, WorkflowBean workflowBean) {
            baseWorkFlow.transitionWorkFlow(savedEgBillregister, workflowBean);
            switch(workflowBean.getWorkflowAction().toLowerCase())   
            {
            case  WorkflowConstants.ACTION_APPROVE :
                savedEgBillregister.setStatus(financialUtils.getStatusByModuleAndCode(CONTINGENCYBILL_FIN,CONTINGENCYBILL_APPROVED_STATUS));
                break;
            case  WorkflowConstants.ACTION_CANCEL :
                savedEgBillregister.setStatus(financialUtils.getStatusByModuleAndCode(CONTINGENCYBILL_FIN,CONTINGENCYBILL_CANCELLED_STATUS));
                break; 
            }
    }

    @Transactional
    public void deleteCheckList(final EgBillregister egBillregister) {
        final List<EgChecklists> checkLists = checkListService.getByObjectId(egBillregister.getId());
        for (final EgChecklists check : checkLists)
            checkListService.delete(check);

    }

    @Transactional
    public void createCheckList(final EgBillregister egBillregister, final List<EgChecklists> checkLists) {
        for (final EgChecklists check : checkLists) {
            check.setObjectid(egBillregister.getId());
            final AppConfigValues configValue = appConfigValuesService.getById(check.getAppconfigvalue().getId());
            check.setAppconfigvalue(configValue);
            checkListService.create(check);
        }

    }

    public void checkBudgetAndGenerateBANumber(final EgBillregister egBillregister) {
        final ScriptContext scriptContext = ScriptService.createContext("voucherService", voucherService, "bill",
                egBillregister);
        scriptExecutionService.executeScript("egf.bill.budgetcheck", scriptContext);
    }

    @Transactional
    public EgBillregister update(final EgBillregister egBillregister, final String mode,WorkflowBean workflowBean) throws ValidationException, IOException {
        EgBillregister updatedegBillregister = null;
        if ("edit".equals(mode)) {
            egBillregister.setPassedamount(egBillregister.getBillamount());
            egBillregister.getEgBillregistermis().setLastupdatedtime(new Date());

            if (egBillregister.getEgBillregistermis().getFund() != null
                    && egBillregister.getEgBillregistermis().getFund().getId() != null)
                egBillregister.getEgBillregistermis().setFund(
                        fundService.findOne(egBillregister.getEgBillregistermis().getFund().getId()));
            if (egBillregister.getEgBillregistermis().getSchemeId() != null)
                egBillregister.getEgBillregistermis().setScheme(
                        schemeService.findById(egBillregister.getEgBillregistermis().getSchemeId().intValue(), false));
            else
                egBillregister.getEgBillregistermis().setScheme(null);
            if (egBillregister.getEgBillregistermis().getSubSchemeId() != null)
                egBillregister.getEgBillregistermis().setSubScheme(
                        subSchemeService.findById(egBillregister.getEgBillregistermis().getSubSchemeId().intValue(), false));
            else
                egBillregister.getEgBillregistermis().setSubScheme(null);

            if (isBillNumberGenerationAuto())
                egBillregister.setBillnumber(getNextBillNumber(egBillregister));

            final List<EgChecklists> checkLists = egBillregister.getCheckLists();

            updatedegBillregister = expenseBillRepository.save(egBillregister);

            deleteCheckList(updatedegBillregister);
            createCheckList(updatedegBillregister, checkLists);
            egBillregister.getEgBillregistermis().setBudgetaryAppnumber(null);
            checkBudgetAndGenerateBANumber(egBillregister);
            

        }
        
            updatedegBillregister = expenseBillRepository.save(egBillregister);
            transitionWorkflow(updatedegBillregister,workflowBean);
          

        return updatedegBillregister;
    }

   

    @Transactional(readOnly = true)
    public boolean isBillNumberGenerationAuto() {
        final List<AppConfigValues> configValuesByModuleAndKey = appConfigValuesService.getConfigValuesByModuleAndKey(
                FinancialConstants.MODULE_NAME_APPCONFIG, FinancialConstants.KEY_BILLNUMBER_APPCONFIG);
        if (!configValuesByModuleAndKey.isEmpty())
            return "Y".equals(configValuesByModuleAndKey.get(0).getValue());
        else
            return false;
    }

    private String getNextBillNumber(final EgBillregister bill) {
        final ExpenseBillNumberGenerator b = beanResolver.getAutoNumberServiceFor(ExpenseBillNumberGenerator.class);
        return b.getNextNumber(bill);
    }
 
   
    public boolean isBillNumUnique(final String billnumber) {
        final EgBillregister bill = getByBillnumber(billnumber);
        return bill == null;
    }
    
    
     
    
    
}
