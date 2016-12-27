package org.egov.pgr.rest.web.controller;

import java.util.Objects;

import org.egov.infra.admin.master.entity.CrossHierarchy;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.CrossHierarchyService;
import org.egov.infra.admin.master.service.UserService;
import org.egov.infra.utils.StringUtils;
import org.egov.pgr.entity.Complaint;
import org.egov.pgr.entity.ComplaintType;
import org.egov.pgr.entity.enums.ReceivingMode;
import org.egov.pgr.rest.web.model.RequestInfo;
import org.egov.pgr.rest.web.model.ResponseInfo;
import org.egov.pgr.rest.web.model.ServiceRequest;
import org.egov.pgr.rest.web.model.ServiceRequestReq;
import org.egov.pgr.rest.web.model.ServiceRequestRes;
import org.egov.pgr.service.ComplaintService;
import org.egov.pgr.service.ComplaintTypeService;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SeviceRequestController {

	@Autowired
	private CrossHierarchyService crossHierarchyService;

	@Autowired
	private ComplaintTypeService complaintTypeService;
	
	@Autowired
	private UserService userService;

	@Autowired
	private ComplaintService complaintService;

	@RequestMapping(value="/requests", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ServiceRequestRes createServiceRequest(@RequestParam String jurisdictionId,
			@RequestBody ServiceRequestReq request) throws Exception{

		if(request.validate()){
			ServiceRequest serviceRequest = request.getServiceRequest();
			RequestInfo requestInfo = request.getRequestInfo();
			Complaint complaint = new Complaint();
			BeanUtils.copyProperties(serviceRequest, complaint);

			if(getUserRole(requestInfo.getRequesterId()).equals("ANONYMOUS")){
				complaint.getComplainant().setName(serviceRequest.getFirstName());
				complaint.getComplainant().setMobile(serviceRequest.getPhone());
				complaint.getComplainant().setEmail(serviceRequest.getEmail());
			}
			else{
				User user = userService.getUserByUsername(serviceRequest.getFirstName());
				complaint.getComplainant().setUserDetail(user);
			}
				
			if(Objects.nonNull(serviceRequest.getCrossHierarchyId()) && Long.parseLong(serviceRequest.getCrossHierarchyId()) > 0){
				final Long locationId = Long.parseLong(serviceRequest.getCrossHierarchyId());
				final CrossHierarchy crosshierarchy = crossHierarchyService.findById(locationId);
				complaint.setLocation(crosshierarchy.getParent());
				complaint.setChildLocation(crosshierarchy.getChild());
				complaint.getChildLocation().setParent(crosshierarchy.getChild().getParent());
			}
			if (Objects.nonNull(serviceRequest.getComplaintTypeCode())) {
				final ComplaintType complaintType = complaintTypeService.findByCode(serviceRequest.getComplaintTypeCode());
				complaint.setComplaintType(complaintType);
			}

			complaint.setReceivingMode(ReceivingMode.MOBILE);

			Complaint savedComplaint = complaintService.createComplaint(complaint);

			serviceRequest.setCrn(savedComplaint.getCrn());
			serviceRequest.setEscalationDate(savedComplaint.getEscalationDate().toString());
			serviceRequest.setLastModifiedDate(savedComplaint.getLastModifiedDate().toString());



			ServiceRequestRes serviceRequestResponse = new ServiceRequestRes();
			ResponseInfo responseInfo = new ResponseInfo(requestInfo.getApiId(),requestInfo.getVer(),new DateTime().toString(),
					"uief87324",requestInfo.getMsgId(),"true");
			serviceRequestResponse.setResposneInfo(responseInfo);
			serviceRequestResponse.getServiceRequests().add(serviceRequest);

			return serviceRequestResponse;

		}
		else{
			//Error handling part to be done.
			throw new Exception("Please send All Mandatory fields in request");
		}

	}


	private String getUserRole(String requesterId){
		if(StringUtils.equals("9531489645", requesterId))
			return "ANONYMOUS";
		else if(StringUtils.equals("9654522615", requesterId))
			return "CITIZEN";
		else
			return "EMPLOYEE";
	}






}
