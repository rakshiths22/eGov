package org.egov.works.web.controller.contractorportal;

import java.io.IOException;
import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.egov.infra.exception.ApplicationException;
import org.egov.works.abstractestimate.service.EstimateService;
import org.egov.works.contractorportal.entity.ContractorMBHeader;
import org.egov.works.contractorportal.service.ContractorMBHeaderService;
import org.egov.works.letterofacceptance.service.LetterOfAcceptanceService;
import org.egov.works.milestone.entity.TrackMilestone;
import org.egov.works.milestone.service.TrackMilestoneService;
import org.egov.works.utils.WorksConstants;
import org.egov.works.workorder.entity.WorkOrder;
import org.egov.works.workorder.entity.WorkOrderEstimate;
import org.egov.works.workorder.service.WorkOrderEstimateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/contractorportal/mb")
public class CreateContractorMBController {

    @Autowired
    private WorkOrderEstimateService workOrderEstimateService;

    @Autowired
    private LetterOfAcceptanceService letterOfAcceptanceService;

    @Autowired
    private ContractorMBHeaderService contractorMBHeaderService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private EstimateService estimateService;

    @Autowired
    private TrackMilestoneService trackMilestoneService;

    @RequestMapping(value = "/search-loaform", method = RequestMethod.GET)
    public String showSearchLoaForm() {
        return "contractormb-loasearch";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String showContractorMBForm(@ModelAttribute("contractorMB") final ContractorMBHeader contractorMBHeader,
            @RequestParam final String loaNumber, final Model model) {
        final WorkOrder workOrder = letterOfAcceptanceService.getApprovedWorkOrder(loaNumber);
        final WorkOrderEstimate workOrderEstimate = workOrderEstimateService.getWorkOrderEstimateByWorkOrderId(workOrder.getId());
        loadViewData(contractorMBHeader, workOrderEstimate, model);

        return "contractormb-form";
    }

    private void loadViewData(final ContractorMBHeader contractorMBHeader, final WorkOrderEstimate workOrderEstimate,
            final Model model) {
        contractorMBHeader.setWorkOrderEstimate(workOrderEstimate);
        contractorMBHeaderService.populateContractorMBDetails(contractorMBHeader);
        model.addAttribute("contractorMB", contractorMBHeader);
        model.addAttribute("documentDetails", contractorMBHeader.getDocumentDetails());
        
        // TODO: Show only the amount where cheque/RTGS assignment is done for the below 2 variables
        model.addAttribute("totalBillsPaidSoFar",
                estimateService.getPaymentsReleasedForLineEstimate(workOrderEstimate.getEstimate().getLineEstimateDetails()));
        model.addAttribute("totalBillAmount", "");
        
        final TrackMilestone trackMilestone = trackMilestoneService.getTrackMilestoneTotalPercentage(workOrderEstimate.getId());
        String mileStoneStatus = "";
        if (trackMilestone == null || trackMilestone.getTotalPercentage().compareTo(BigDecimal.ZERO) < 0)
            mileStoneStatus = WorksConstants.MILESTONE_NOT_YET_STARTED;
        else if (trackMilestone.getTotalPercentage().compareTo(BigDecimal.ZERO) > 0
                && trackMilestone.getTotalPercentage().compareTo(new BigDecimal(100)) < 0)
            mileStoneStatus = WorksConstants.MILESTONE_IN_PROGRESS;
        else
            mileStoneStatus = WorksConstants.MILESTONE_COMPLETED;
        model.addAttribute("mileStoneStatus", mileStoneStatus);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(@ModelAttribute("contractorMBHeader") final ContractorMBHeader contractorMBHeader,
            final Model model, final BindingResult errors, final HttpServletRequest request, final BindingResult resultBinder,
            final HttpServletResponse response, @RequestParam("file") final MultipartFile[] files)
            throws ApplicationException, IOException {

        contractorMBHeader.setWorkOrderEstimate(
                workOrderEstimateService.getWorkOrderEstimateById(contractorMBHeader.getWorkOrderEstimate().getId()));

        final ContractorMBHeader savedContractorMBHeader = contractorMBHeaderService.create(contractorMBHeader, files);

        return "redirect:/contractorportal/mb/contractormb-success?contractormb=" + savedContractorMBHeader.getId();
    }

    @RequestMapping(value = "/contractormb-success", method = RequestMethod.GET)
    public ModelAndView successView(@ModelAttribute ContractorMBHeader contractorMBHeader,
            @RequestParam("contractormb") final Long id, final HttpServletRequest request, final Model model) {
        if (id != null)
            contractorMBHeader = contractorMBHeaderService.getContractorMBHeaderById(id);
        final String message = messageSource.getMessage("msg.contractor.mbheader.created",
                new String[] { contractorMBHeader.getMbRefNo() }, null);
        model.addAttribute("message", message);
        return new ModelAndView("contractormb-success", "contractorMBHeader", contractorMBHeader);
    }
}