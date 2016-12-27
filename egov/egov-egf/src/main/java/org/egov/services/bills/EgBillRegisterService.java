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
package org.egov.services.bills;

import org.apache.log4j.Logger;
import org.egov.commons.EgwStatus;
import org.egov.commons.dao.EgwStatusHibernateDAO;
import org.egov.eis.entity.Assignment;
import org.egov.eis.service.AssignmentService;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.validation.exception.ValidationError;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.infra.workflow.matrix.entity.WorkFlowMatrix;
import org.egov.infra.workflow.service.SimpleWorkflowService;
import org.egov.infstr.models.EgChecklists;
import org.egov.infstr.services.PersistenceService;
import org.egov.model.bills.EgBillregister;
import org.egov.infra.workflow.multitenant.model.WorkflowBean;
import org.egov.pims.commons.Position;
import org.egov.services.voucher.JournalVoucherActionHelper;
import org.egov.utils.CheckListHelper;
import org.egov.utils.FinancialConstants;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class EgBillRegisterService extends PersistenceService<EgBillregister, Long>
{
    final private static Logger LOGGER = Logger.getLogger(JournalVoucherActionHelper.class);
    private static final String FAILED = "Transaction failed";
    private static final String EXCEPTION_WHILE_SAVING_DATA = "Exception while saving data";
    @Autowired
    private SecurityUtils securityUtils;
    @Autowired
    private AssignmentService assignmentService;
     
    @Autowired
    @Qualifier("persistenceService")
    private PersistenceService persistenceService;

    @Autowired
    private EgwStatusHibernateDAO egwStatusHibernateDAO;

    public EgBillRegisterService() {
        super(EgBillregister.class);
    }

    public EgBillRegisterService(Class<EgBillregister> type) {
        super(type);
    }

    @Transactional
    public EgBillregister createBill(EgBillregister bill, WorkflowBean workflowBean, List<CheckListHelper> checkListsTable) {
        try {
            applyAuditing(bill);
            if (FinancialConstants.CREATEANDAPPROVE.equalsIgnoreCase(workflowBean.getWorkflowAction()) && bill.getCurrentTask() == null)
            {
                bill.setBillstatus("APPROVED");
                EgwStatus egwStatus = egwStatusHibernateDAO.getStatusByModuleAndCode(FinancialConstants.CONTINGENCYBILL_FIN,
                        FinancialConstants.CONTINGENCYBILL_APPROVED_STATUS);
                bill.setStatus(egwStatus);
            }
            
            persist(bill);
            bill.getEgBillregistermis().setSourcePath(
                    "/EGF/bill/contingentBill-beforeView.action?billRegisterId=" + bill.getId().toString());
            createCheckList(bill, checkListsTable);
        } catch (final Exception e) {

            final List<ValidationError> errors = new ArrayList<ValidationError>();
            errors.add(new ValidationError("exp", e.getMessage()));
            throw new ValidationException(errors);
        }
        return bill;
    }

    @Transactional
    public void createCheckList(final EgBillregister bill, List<CheckListHelper> checkListsTable) {
        try {
            if (checkListsTable != null)
                for (final CheckListHelper clh : checkListsTable)
                {
                    final EgChecklists checkList = new EgChecklists();
                    final AppConfigValues configValue = (AppConfigValues) persistenceService.find(
                            "from AppConfigValues where id=?",
                            clh.getId());
                    checkList.setObjectid(bill.getId());
                    checkList.setAppconfigvalue(configValue);
                    checkList.setChecklistvalue(clh.getVal());
                    persistenceService.getSession().saveOrUpdate(checkList);
                }
        } catch (final Exception e) {

            final List<ValidationError> errors = new ArrayList<ValidationError>();
            errors.add(new ValidationError("exp", e.getMessage()));
            throw new ValidationException(errors);
        }
    }

    @Transactional
    public EgBillregister sendForApproval(EgBillregister bill, WorkflowBean workflowBean)
    {
        try {
           
            persist(bill);

        } catch (final Exception e) {

            final List<ValidationError> errors = new ArrayList<ValidationError>();
            errors.add(new ValidationError("exp", e.getMessage()));
            throw new ValidationException(errors);
        }
        return bill;
    }

     

    private Assignment getWorkflowInitiator(final EgBillregister billregister) {
        Assignment wfInitiator = assignmentService.findByEmployeeAndGivenDate(billregister.getCreatedBy().getId(), new Date())
                .get(0);
        return wfInitiator;
    }
}