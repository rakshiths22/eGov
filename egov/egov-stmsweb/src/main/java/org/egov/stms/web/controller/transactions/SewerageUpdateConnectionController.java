/**
 * eGov suite of products aim to improve the internal efficiency,transparency,
   accountability and the service delivery of the government  organizations.

    Copyright (C) <2015>  eGovernments Foundation

    The updated version of eGov suite of products as by eGovernments Foundation
    is available at http://www.egovernments.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see http://www.gnu.org/licenses/ or
    http://www.gnu.org/licenses/gpl.html .

    In addition to the terms of the GPL license to be adhered to in using this
    program, the following additional terms are to be complied with:

        1) All versions of this program, verbatim or modified must carry this
           Legal Notice.

        2) Any misrepresentation of the origin of the material is prohibited. It
           is required that all modified versions of this material be marked in
           reasonable ways as different from the original version.

        3) This license does not grant any rights to any user of the program
           with regards to rights under trademark law for use of the trade names
           or trademarks of eGovernments Foundation.

  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.stms.web.controller.transactions;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.ValidationException;

import org.egov.commons.service.UOMService;
import org.egov.eis.entity.Assignment;
import org.egov.eis.service.AssignmentService;
import org.egov.eis.web.contract.WorkflowContainer;
import org.egov.eis.web.controller.workflow.GenericWorkFlowController;
import org.egov.infra.admin.master.service.DepartmentService;
import org.egov.infra.filestore.entity.FileStoreMapper;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.ptis.domain.model.AssessmentDetails;
import org.egov.ptis.domain.model.PropertyTaxDetails;
import org.egov.ptis.domain.service.property.PropertyExternalService;
import org.egov.stms.masters.entity.FeesDetailMaster;
import org.egov.stms.masters.entity.enums.OwnerOfTheRoad;
import org.egov.stms.masters.entity.enums.PropertyType;
import org.egov.stms.masters.service.FeesDetailMasterService;
import org.egov.stms.transactions.charges.SewerageChargeCalculationService;
import org.egov.stms.transactions.entity.SewerageApplicationDetails;
import org.egov.stms.transactions.entity.SewerageApplicationDetailsDocument;
import org.egov.stms.transactions.entity.SewerageConnectionEstimationDetails;
import org.egov.stms.transactions.entity.SewerageConnectionFee;
import org.egov.stms.transactions.entity.SewerageFieldInspection;
import org.egov.stms.transactions.entity.SewerageFieldInspectionDetails;
import org.egov.stms.transactions.service.SewerageApplicationDetailsService;
import org.egov.stms.transactions.service.SewerageConnectionFeeService;
import org.egov.stms.transactions.service.SewerageConnectionService;
import org.egov.stms.transactions.service.SewerageEstimationDetailsService;
import org.egov.stms.transactions.service.SewerageFieldInspectionDetailsService;
import org.egov.stms.transactions.service.SewerageThirdPartyServices;
import org.egov.stms.utils.SewerageInspectionDetailsComparatorById;
import org.egov.stms.utils.SewerageTaxUtils;
import org.egov.stms.utils.constants.SewerageTaxConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(value = "/transactions")
public class SewerageUpdateConnectionController extends GenericWorkFlowController {

    private final SewerageApplicationDetailsService sewerageApplicationDetailsService;

    private final DepartmentService departmentService;

    @Autowired
    private SewerageTaxUtils sewerageTaxUtils;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    @Qualifier("fileStoreService")
    protected FileStoreService fileStoreService;

    @Autowired
    private FeesDetailMasterService feesDetailMasterService;

    @Autowired
    private UOMService uOMService;
    
    @Autowired
    private SewerageChargeCalculationService sewerageChargeCalculationService;
    
    @Autowired
    private SewerageConnectionFeeService SewerageConnectionFeeService;
    
    @Autowired
    private SewerageConnectionService sewerageConnectionService;
    
    @Autowired
    private SewerageEstimationDetailsService sewerageEstimationDetailsService;
 
    @Autowired
    private SewerageFieldInspectionDetailsService sewerageFieldInspectionDetailsService;
    
    @Autowired
    private PropertyExternalService propertyExternalService;

    @Autowired
    private SewerageThirdPartyServices sewerageThirdPartyServices;
    
    @Autowired
    public SewerageUpdateConnectionController(
            final SewerageApplicationDetailsService sewerageApplicationDetailsService,
            final DepartmentService departmentService, final SmartValidator validator) {
        this.sewerageApplicationDetailsService = sewerageApplicationDetailsService;
        this.departmentService = departmentService;
    }

    @ModelAttribute("sewerageApplicationDetails")
    public SewerageApplicationDetails getSewerageApplicationDetails(@PathVariable final String applicationNumber) {
        final SewerageApplicationDetails sewerageApplicationDetails = sewerageApplicationDetailsService
                .findByApplicationNumber(applicationNumber);
        return sewerageApplicationDetails;
    }

    @RequestMapping(value = "/update/{applicationNumber}", method = RequestMethod.GET)
    public String view(final Model model, @PathVariable final String applicationNumber, final HttpServletRequest request) {
        final SewerageApplicationDetails sewerageApplicationDetails = sewerageApplicationDetailsService
                .findByApplicationNumber(applicationNumber);
        if(sewerageApplicationDetails.getApplicationType().getCode().equalsIgnoreCase(SewerageTaxConstants.CHANGEINCLOSETS))
            return "redirect:/transactions/modifyConnection-update/" + applicationNumber;
        else if(sewerageApplicationDetails.getApplicationType().getCode().equalsIgnoreCase(SewerageTaxConstants.CLOSESEWERAGECONNECTION))
            return "redirect:/transactions/closeSewerageConnection-update/" + applicationNumber;
        
        if(sewerageApplicationDetails.getEstimationDetails()!=null && !sewerageApplicationDetails.getEstimationDetails().isEmpty())
        sewerageApplicationDetails.setEstimationDetailsForUpdate(sewerageApplicationDetails.getEstimationDetails());
        
        if(sewerageApplicationDetails.getFieldInspections()!=null && !sewerageApplicationDetails.getFieldInspections().isEmpty() && sewerageApplicationDetails.getFieldInspections().get(0)!=null
                && sewerageApplicationDetails.getFieldInspections().get(0).getFieldInspectionDetails()!=null)
            sewerageApplicationDetails.getFieldInspections().get(0).setFieldInspectionDetailsForUpdate(
                    sewerageApplicationDetails.getFieldInspections().get(0).getFieldInspectionDetails());
        model.addAttribute("sewerageApplcationDetails", sewerageApplicationDetails);
        setCommonDetails(sewerageApplicationDetails, model, request);
        return loadViewData(model, request, sewerageApplicationDetails);
    }

    private String loadViewData(final Model model, final HttpServletRequest request,
            final SewerageApplicationDetails sewerageApplicationDetails) {
        model.addAttribute("stateType", sewerageApplicationDetails.getClass().getSimpleName());
        for(SewerageFieldInspection fieldInspection : sewerageApplicationDetails.getFieldInspections()){ 
           Collections.sort(fieldInspection.getFieldInspectionDetails(), new SewerageInspectionDetailsComparatorById());
        }
        model.addAttribute("additionalRule", sewerageApplicationDetails.getApplicationType().getCode());
        model.addAttribute("currentUser", sewerageTaxUtils.getCurrentUserRole(securityUtils.getCurrentUser()));
        model.addAttribute("currentState", sewerageApplicationDetails.getCurrentState().getValue());
        if(sewerageApplicationDetails.getCurrentState().getValue().equalsIgnoreCase(SewerageTaxConstants.WF_STATE_REJECTED))
            if (sewerageTaxUtils.isInspectionFeeCollectionRequired()) 
                model.addAttribute("pendingActions", SewerageTaxConstants.WFPA_REJECTED_INSPECTIONFEE_COLLECTION);
            else
                model.addAttribute("pendingActions", SewerageTaxConstants.WF_STATE_REJECTED); 

        prepareWorkflow(model, sewerageApplicationDetails, new WorkflowContainer());

        model.addAttribute("sewerageApplicationDetails", sewerageApplicationDetails);
        model.addAttribute("applicationHistory",
                sewerageApplicationDetailsService.getHistory(sewerageApplicationDetails));
        model.addAttribute("approvalDepartmentList", departmentService.getAllDepartments());

        model.addAttribute("pipeSize", SewerageTaxConstants.PIPE_SCREW_SIZE); 
        model.addAttribute("roadOwner", OwnerOfTheRoad.values());
        
        model.addAttribute("uomList", uOMService.findAllOrderByCategory());
        
        Map<String, String> modelParams = sewerageApplicationDetailsService.showApprovalDetailsByApplcationCurState(sewerageApplicationDetails);
        model.addAttribute("mode", modelParams.get("mode"));
        model.addAttribute("showApprovalDtls", modelParams.get("showApprovalDtls"));
        
        if (modelParams.get("mode").equalsIgnoreCase("edit")) {
            FeesDetailMaster fdm = feesDetailMasterService.findByCodeAndIsActive(
                    SewerageTaxConstants.FEES_ESTIMATIONCHARGES_CODE, true);
            List<SewerageConnectionFee> connectionFeeList = SewerageConnectionFeeService
                    .findAllByApplicationDetailsAndFeesDetail(sewerageApplicationDetails, fdm);
            if (connectionFeeList.isEmpty()) {
                createSewerageConnectionFee(sewerageApplicationDetails,
                        SewerageTaxConstants.FEES_ESTIMATIONCHARGES_CODE);
            }
        }
        
        if(sewerageApplicationDetails.getStatus()!=null && 
                sewerageApplicationDetails.getStatus().getCode().equalsIgnoreCase(SewerageTaxConstants.APPLICATION_STATUS_INITIALAPPROVED)){
            populateDonationSewerageTax(sewerageApplicationDetails);
        }
        
        // TODO :To Support Documents Re-Attachment on Edit mode
        if (modelParams.get("mode").equals("editOnReject")) {
            List<SewerageApplicationDetailsDocument> docList = sewerageConnectionService
                    .getSewerageApplicationDoc(sewerageApplicationDetails);
            model.addAttribute("documentNamesList", docList);

        } else {
            model.addAttribute("documentNamesList",
                    sewerageConnectionService.getSewerageApplicationDoc(sewerageApplicationDetails));
        }
        
        final BigDecimal sewerageTaxDue = sewerageApplicationDetailsService.getTotalAmount(sewerageApplicationDetails);
        model.addAttribute("sewerageTaxDue", sewerageTaxDue);
        model.addAttribute("propertyTypes", PropertyType.values());
        return "newconnection-edit";
    } 
    
    private void setCommonDetails(final SewerageApplicationDetails sewerageApplicationDetails,final Model model, final HttpServletRequest request) {
        final String assessmentNumber = sewerageApplicationDetails.getConnectionDetail()
                .getPropertyIdentifier();

        final AssessmentDetails propertyOwnerDetails = sewerageThirdPartyServices.getPropertyDetails(assessmentNumber,
                request);
        if (propertyOwnerDetails != null)
            model.addAttribute("propertyOwnerDetails", propertyOwnerDetails);
        final PropertyTaxDetails propertyTaxDetails = propertyExternalService.getPropertyTaxDetails(assessmentNumber,null);
        model.addAttribute("propertyTax", propertyTaxDetails.getTotalTaxAmt());
    }
    
    private void createSewerageConnectionFee(final SewerageApplicationDetails sewerageApplicationDetails, String feeCode) {
        List<FeesDetailMaster> inspectionFeeList = feesDetailMasterService.findAllActiveFeesDetailByFeesCode(feeCode);
        for (FeesDetailMaster feeDetailMaster : inspectionFeeList) {
            SewerageConnectionFee connectionFee = new SewerageConnectionFee();
            connectionFee.setFeesDetail(feeDetailMaster);
            if (feeDetailMaster.getIsFixedRate())
                connectionFee.setAmount(feeDetailMaster.getAmount().doubleValue());
            connectionFee.setApplicationDetails(sewerageApplicationDetails);
            sewerageApplicationDetails.getConnectionFees().add(connectionFee);

        }
    }
  
    @RequestMapping(value = "/update/{applicationNumber}", method = RequestMethod.POST)
    public String update(@Valid @ModelAttribute SewerageApplicationDetails sewerageApplicationDetails,
            final BindingResult resultBinder, final RedirectAttributes redirectAttributes,
            final HttpServletRequest request,final HttpSession session, final Model model, @RequestParam("files") final MultipartFile[] files, 
            @RequestParam final String removedInspectRowId, @RequestParam final String removedEstimationDtlRowId) {
        String mode = "";
        String workFlowAction = "";
        
        if (request.getParameter("mode") != null)
            mode = request.getParameter("mode");

        if (request.getParameter("workFlowAction") != null)
            workFlowAction = request.getParameter("workFlowAction");

        request.getSession().setAttribute(SewerageTaxConstants.WORKFLOW_ACTION, workFlowAction);

        if ((sewerageApplicationDetails.getStatus().getCode()
                .equalsIgnoreCase(SewerageTaxConstants.APPLICATION_STATUS_CREATED) || sewerageApplicationDetails.getStatus().getCode()
                        .equalsIgnoreCase(SewerageTaxConstants.APPLICATION_STATUS_INSPECTIONFEEPAID))
                && mode.equalsIgnoreCase("edit"))
            if (workFlowAction.equalsIgnoreCase(SewerageTaxConstants.WFLOW_ACTION_STEP_FORWARD)) {

                List<SewerageConnectionEstimationDetails> existingSewerage = populateEstimationDetails(
                        sewerageApplicationDetails);
         
                List<SewerageFieldInspectionDetails> existingInspection = populateInspectionDetails(
                        sewerageApplicationDetails, request, files);
                
                
                
                populateFeesDetails(sewerageApplicationDetails);
                sewerageApplicationDetailsService.save(sewerageApplicationDetails);
            if(existingSewerage!=null && !existingSewerage.isEmpty())  
                    sewerageEstimationDetailsService.deleteAllInBatch(existingSewerage);
            
            if(existingInspection!=null && !existingInspection.isEmpty()) 
                sewerageFieldInspectionDetailsService.deleteAllInBatch(existingInspection); 
                    
            } else if (workFlowAction.equalsIgnoreCase(SewerageTaxConstants.WFLOW_ACTION_STEP_REJECT)) {
                sewerageApplicationDetailsService.getCurrentSession().evict(sewerageApplicationDetails);
                sewerageApplicationDetails = sewerageApplicationDetailsService.findBy(sewerageApplicationDetails
                        .getId());
            }
        
        if(sewerageApplicationDetails.getStatus().getCode()!=null && 
                sewerageApplicationDetails.getStatus().getCode().equalsIgnoreCase(SewerageTaxConstants.APPLICATION_STATUS_INITIALAPPROVED)
                && !workFlowAction.equalsIgnoreCase(SewerageTaxConstants.WFLOW_ACTION_STEP_REJECT)){
            populateDonationSewerageTax(sewerageApplicationDetails);
            
        }
        
        Long approvalPosition = 0l;
        String approvalComment = "";

        if (request.getParameter("approvalComent") != null)
            approvalComment = request.getParameter("approvalComent");

        if (request.getParameter("approvalPosition") != null && !request.getParameter("approvalPosition").isEmpty())
            approvalPosition = Long.valueOf(request.getParameter("approvalPosition"));

        if ((approvalPosition == null || approvalPosition.equals(Long.valueOf(0)))
                && request.getParameter("approvalPosition") != null
                && !request.getParameter("approvalPosition").isEmpty())
            approvalPosition = Long.valueOf(request.getParameter("approvalPosition"));

        if (!resultBinder.hasErrors()) {
            try {
                if (null != workFlowAction)
                    if (workFlowAction.equalsIgnoreCase(SewerageTaxConstants.PREVIEWWORKFLOWACTION)
                            && sewerageApplicationDetails.getApplicationType().getCode()
                            .equals(SewerageTaxConstants.NEWSEWERAGECONNECTION))
                        return "redirect:/transactions/workorder?pathVar="
                        + sewerageApplicationDetails.getApplicationNumber();

                sewerageApplicationDetailsService.updateSewerageApplicationDetails(sewerageApplicationDetails,
                        approvalPosition, approvalComment, sewerageApplicationDetails.getApplicationType().getCode(),
                        workFlowAction, mode, null,request,session);
            } catch (final ValidationException e) {
                throw new ValidationException(e.getMessage());
            }
            if (workFlowAction != null && !workFlowAction.isEmpty()
                    && workFlowAction.equalsIgnoreCase(SewerageTaxConstants.WF_ESTIMATION_NOTICE_BUTTON))
                return "redirect:/transactions/estimationnotice?pathVar="
                + sewerageApplicationDetails.getApplicationNumber();
            if (workFlowAction != null && !workFlowAction.isEmpty()
                    && workFlowAction.equalsIgnoreCase(SewerageTaxConstants.WF_CLOSERACKNOWLDGEENT_BUTTON))
                return "redirect:/applications/acknowlgementNotice?pathVar="
                + sewerageApplicationDetails.getApplicationNumber();
            final Assignment currentUserAssignment = assignmentService.getPrimaryAssignmentForGivenRange(securityUtils
                    .getCurrentUser().getId(), new Date(), new Date());
            String nextDesign = "";
            Assignment assignObj = null;
            List<Assignment> asignList = null;
            
            if(approvalPosition==null || approvalPosition==0){
                approvalPosition=assignmentService.getPrimaryAssignmentForUser(sewerageApplicationDetails.getCreatedBy().getId()).getPosition().getId();
            }
            
            if (approvalPosition != null)
                assignObj = assignmentService.getPrimaryAssignmentForPositon(approvalPosition);
            if (assignObj != null) {
                asignList = new ArrayList<Assignment>();
                asignList.add(assignObj);
            } else if (assignObj == null && approvalPosition != null)
                asignList = assignmentService.getAssignmentsForPosition(approvalPosition, new Date());
            
            nextDesign = asignList != null && !asignList.isEmpty() ? asignList.get(0).getDesignation().getName() : "";
            
            final String pathVars = sewerageApplicationDetails.getApplicationNumber() + ","
                    + sewerageTaxUtils.getApproverName(approvalPosition) + ","
                    + (currentUserAssignment != null ? currentUserAssignment.getDesignation().getName() : "") + ","
                    + (nextDesign != null ? nextDesign : "");
            return "redirect:/transactions/application-success?pathVars=" + pathVars;   
              } else
            return loadViewData(model, request, sewerageApplicationDetails);
    }
    
    private void populateDonationSewerageTax(final SewerageApplicationDetails sewerageApplicationDetails) {

        FeesDetailMaster donationCharge = feesDetailMasterService.findByCodeAndIsActive(
                SewerageTaxConstants.FEES_DONATIONCHARGE_CODE, true);
        List<SewerageConnectionFee> donationChargeList = SewerageConnectionFeeService
                .findAllByApplicationDetailsAndFeesDetail(sewerageApplicationDetails, donationCharge);
        if (donationChargeList.isEmpty()) {

            SewerageConnectionFee connectionFee = new SewerageConnectionFee();
            connectionFee.setFeesDetail(donationCharge);
            connectionFee.setAmount(sewerageChargeCalculationService.calculateDonationCharges(
                    sewerageApplicationDetails).doubleValue());
            connectionFee.setApplicationDetails(sewerageApplicationDetails);
            sewerageApplicationDetails.getConnectionFees().add(connectionFee);
        }

        FeesDetailMaster sewerageTax = feesDetailMasterService.findByCodeAndIsActive(
                SewerageTaxConstants.FEES_SEWERAGETAX_CODE, true);
        List<SewerageConnectionFee> sewerageTaxFeeList = SewerageConnectionFeeService
                .findAllByApplicationDetailsAndFeesDetail(sewerageApplicationDetails, sewerageTax);
        if (sewerageTaxFeeList.isEmpty()) {
            SewerageConnectionFee connectionFee = new SewerageConnectionFee();
            connectionFee = new SewerageConnectionFee();
            connectionFee.setFeesDetail(sewerageTax);
            connectionFee.setAmount(sewerageChargeCalculationService.calculateSewerageCharges(
                    sewerageApplicationDetails).doubleValue());
            connectionFee.setApplicationDetails(sewerageApplicationDetails);
            sewerageApplicationDetails.getConnectionFees().add(connectionFee);
        }
    }

    private List<SewerageConnectionEstimationDetails> populateEstimationDetails(
            final SewerageApplicationDetails sewerageApplicationDetails) {

        final List<SewerageConnectionEstimationDetails> sewerageConnectionEstimationDetailList = new ArrayList<SewerageConnectionEstimationDetails>();
        final List<SewerageConnectionEstimationDetails> existingSewerage = new ArrayList<SewerageConnectionEstimationDetails>();
       
        if (!sewerageApplicationDetails.getEstimationDetailsForUpdate().isEmpty()) {
            for (final SewerageConnectionEstimationDetails estimationDetails : sewerageApplicationDetails
                    .getEstimationDetails()) {
                existingSewerage.add(estimationDetails);
            }
            for (final SewerageConnectionEstimationDetails estimationDetail : sewerageApplicationDetails
                    .getEstimationDetailsForUpdate()) {
                if (validSewerageConnectioEstimationDetail(estimationDetail)) {
                    SewerageConnectionEstimationDetails estimationDtl = new SewerageConnectionEstimationDetails();
                    estimationDtl.setAmount(estimationDetail.getAmount());
                    estimationDtl.setItemDescription(estimationDetail.getItemDescription());
                    estimationDtl.setQuantity(estimationDetail.getQuantity());
                    estimationDtl.setUnitOfMeasurement(estimationDetail.getUnitOfMeasurement());
                    estimationDtl.setUnitRate(estimationDetail.getUnitRate());
                    estimationDtl.setApplicationDetails(sewerageApplicationDetails);  
                    sewerageConnectionEstimationDetailList.add(estimationDtl);

                }
            }
        }
        sewerageApplicationDetails.setEstimationDetails(sewerageConnectionEstimationDetailList);
        if (!existingSewerage.isEmpty()) {
            return existingSewerage;
        }
        return null;
    }
    
    private void populateFeesDetails(final SewerageApplicationDetails sewerageApplicationDetails) {
        if (!sewerageApplicationDetails.getConnectionFees().isEmpty()){
            for (final SewerageConnectionFee scf : sewerageApplicationDetails.getConnectionFees()) {
                    scf.setApplicationDetails(sewerageApplicationDetails);
            }
        }
      } 

    private List<SewerageFieldInspectionDetails> populateInspectionDetails(final SewerageApplicationDetails sewerageApplicationDetails,
            final HttpServletRequest request, final MultipartFile[] files) {
        new ArrayList<SewerageFieldInspectionDetails>();
        final String inspectionDate = request.getParameter("inspectionDate");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

         List<SewerageFieldInspectionDetails> existingInspectionDtlList = new ArrayList<SewerageFieldInspectionDetails>();

        if (!sewerageApplicationDetails.getFieldInspections().isEmpty()) {

            for (final SewerageFieldInspection sewerageFieldInspection : sewerageApplicationDetails
                    .getFieldInspections()) {
                List<SewerageFieldInspectionDetails> sewerageFieldInspectionDetailList = new ArrayList<SewerageFieldInspectionDetails>();

                sewerageFieldInspection.setApplicationDetails(sewerageApplicationDetails);
                try {
                    sewerageFieldInspection.setInspectionDate(sdf.parse(inspectionDate));
                } catch (ParseException e) {


                }
                final Set<FileStoreMapper> fileStoreSet = sewerageTaxUtils.addToFileStore(files);
                Iterator<FileStoreMapper> fsIterator = null;
                if (fileStoreSet != null && !fileStoreSet.isEmpty())
                    fsIterator = fileStoreSet.iterator();

                if (fsIterator != null && fsIterator.hasNext())
                    sewerageFieldInspection.setFileStore(fsIterator.next());

                if (!sewerageFieldInspection.getFieldInspectionDetailsForUpdate().isEmpty()) {

                    for (SewerageFieldInspectionDetails fieldInspectionDtls : sewerageFieldInspection
                            .getFieldInspectionDetails()) {
                        existingInspectionDtlList.add(fieldInspectionDtls);

                    }

                    for (SewerageFieldInspectionDetails fieldInspectionDtl : sewerageFieldInspection
                            .getFieldInspectionDetailsForUpdate()) {
                        if (validSewerageFieldInspectionDetails(fieldInspectionDtl)) {

                            SewerageFieldInspectionDetails sewerageFieldInspectionDetails = new SewerageFieldInspectionDetails();
                            sewerageFieldInspectionDetails.setNoOfPipes(fieldInspectionDtl.getNoOfPipes());
                            sewerageFieldInspectionDetails.setPipeSize(fieldInspectionDtl.getPipeSize());
                            sewerageFieldInspectionDetails.setPipeLength(fieldInspectionDtl.getPipeLength());
                            sewerageFieldInspectionDetails.setScrewSize(fieldInspectionDtl.getScrewSize());
                            sewerageFieldInspectionDetails.setNoOfScrews(fieldInspectionDtl.getNoOfScrews());
                            sewerageFieldInspectionDetails.setDistance(fieldInspectionDtl.getDistance());
                            sewerageFieldInspectionDetails.setRoadDigging(fieldInspectionDtl.isRoadDigging());
                            sewerageFieldInspectionDetails.setRoadLength(fieldInspectionDtl.getRoadLength());
                            sewerageFieldInspectionDetails.setRoadOwner(fieldInspectionDtl.getRoadOwner());
                            sewerageFieldInspectionDetails.setFieldInspection(sewerageFieldInspection);
                            sewerageFieldInspectionDetailList.add(sewerageFieldInspectionDetails);

                        }
                    }

                }

                sewerageFieldInspection.setFieldInspectionDetails(sewerageFieldInspectionDetailList);
            }
        }

        if (existingInspectionDtlList != null) {
            return existingInspectionDtlList;
        }
        return null;
    }
    
    private boolean validSewerageFieldInspectionDetails(
            final SewerageFieldInspectionDetails sewerageFieldInspectionDetails) {
        if (sewerageFieldInspectionDetails == null || sewerageFieldInspectionDetails != null
                && (sewerageFieldInspectionDetails.getNoOfPipes() == null || sewerageFieldInspectionDetails.getNoOfPipes() == 0))
            return false;
        return true;
    }

    private boolean validSewerageConnectioEstimationDetail(
            final SewerageConnectionEstimationDetails sewerageConnectionEstimationDetails) {
        if (sewerageConnectionEstimationDetails == null || (sewerageConnectionEstimationDetails != null
                && sewerageConnectionEstimationDetails.getItemDescription() == null))
            return false;
        return true;
    }
}