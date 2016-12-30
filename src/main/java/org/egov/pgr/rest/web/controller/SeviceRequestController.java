package org.egov.pgr.rest.web.controller;

import java.util.List;
import java.util.Objects;

import org.egov.infra.admin.master.entity.CrossHierarchy;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.CrossHierarchyService;
import org.egov.infra.admin.master.service.UserService;
import org.egov.infra.utils.StringUtils;
import org.egov.pgr.entity.Complaint;
import org.egov.pgr.entity.ComplaintType;
import org.egov.pgr.entity.enums.ComplaintStatus;
import org.egov.pgr.entity.enums.ReceivingMode;
import org.egov.pgr.rest.web.model.Error;
import org.egov.pgr.rest.web.model.ErrorRes;
import org.egov.pgr.rest.web.model.RequestInfo;
import org.egov.pgr.rest.web.model.ResponseInfo;
import org.egov.pgr.rest.web.model.ServiceRequest;
import org.egov.pgr.rest.web.model.ServiceRequestReq;
import org.egov.pgr.rest.web.model.ServiceRequestRes;
import org.egov.pgr.service.ComplaintService;
import org.egov.pgr.service.ComplaintStatusService;
import org.egov.pgr.service.ComplaintTypeService;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = {"/v1", "/a1"})
public class SeviceRequestController {

	@Autowired
	private CrossHierarchyService crossHierarchyService;

	@Autowired
	private ComplaintTypeService complaintTypeService;

	@Autowired
	private ComplaintStatusService complaintStatusService;

	@Autowired
	private UserService userService;

	@Autowired
	private ComplaintService complaintService;

	private ResponseInfo resInfo = null;

	@RequestMapping(value = "/requests", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ServiceRequestRes createServiceRequest(
			@RequestParam String jurisdiction_id,
			@RequestBody ServiceRequestReq request) throws Exception {
		try
		{
			RequestInfo requestInfo = request.getRequestInfo();
			resInfo = new ResponseInfo(requestInfo.getApiId(), requestInfo.getVer(),
					new DateTime().toString(), "uief87324",requestInfo.getMsgId(), "true");
			Authentication authUser = SecurityContextHolder.getContext().getAuthentication();
			if (request.validate()) {
				ServiceRequest serviceRequest = request.getServiceRequest();
				Complaint complaint = new Complaint();
				BeanUtils.copyProperties(serviceRequest, complaint);

				if (getUserRole(requestInfo.getRequesterId()).equals("ANONYMOUS")) {
					complaint.getComplainant().setName(
							serviceRequest.getFirstName());
					complaint.getComplainant().setMobile(serviceRequest.getPhone());
					complaint.getComplainant().setEmail(serviceRequest.getEmail());
				} else {
					User user = userService.getUserById(Long.valueOf(requestInfo
							.getRequesterId()));
					complaint.getComplainant().setUserDetail(user);
				}

				if (Objects.nonNull(serviceRequest.getCrossHierarchyId())
						&& Long.parseLong(serviceRequest.getCrossHierarchyId()) > 0) {
					final Long locationId = Long.parseLong(serviceRequest
							.getCrossHierarchyId());
					final CrossHierarchy crosshierarchy = crossHierarchyService
							.findById(locationId);
					complaint.setLocation(crosshierarchy.getParent());
					complaint.setChildLocation(crosshierarchy.getChild());
					complaint.getChildLocation().setParent(
							crosshierarchy.getChild().getParent());
				}
				if (Objects.nonNull(serviceRequest.getComplaintTypeCode())) {
					final ComplaintType complaintType = complaintTypeService
							.findBy(Long.valueOf(serviceRequest
									.getComplaintTypeCode()));
					complaint.setComplaintType(complaintType);
				}

				complaint.setReceivingMode(ReceivingMode.MOBILE);

				Complaint savedComplaint = complaintService
						.createComplaint(complaint);

				serviceRequest.setCrn(savedComplaint.getCrn());
				serviceRequest.setEscalationDate(savedComplaint.getEscalationDate()
						.toString());
				serviceRequest.setLastModifiedDate(savedComplaint
						.getLastModifiedDate().toString());
				serviceRequest.setStatusDetails(ComplaintStatus.valueOf(complaint
						.getStatus().getName()));

				ServiceRequestRes serviceRequestResponse = new ServiceRequestRes();
				ResponseInfo responseInfo = resInfo;
				serviceRequestResponse.setResposneInfo(responseInfo);
				serviceRequestResponse.getServiceRequests().add(serviceRequest);

				return serviceRequestResponse;

			} else {
				// Error handling part to be done.
				throw new Exception("Please send All Mandatory fields in request");
			}
		}
		catch(Exception exception){
			throw exception;
		}
	}

	@RequestMapping(value = "/requests/{service_request_id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ServiceRequestRes updateServiceRequest(
			@PathVariable String service_request_id,
			@RequestParam String jurisdictionId,
			@RequestBody ServiceRequestReq request) throws Exception {
		try
		{
			RequestInfo requestInfo = request.getRequestInfo();
			resInfo = new ResponseInfo(
					requestInfo.getApiId(), requestInfo.getVer(),
					new DateTime().toString(), "7gduf46t3erhg",
					requestInfo.getMsgId(), "true");
			if (request.validate()) {
				ServiceRequest serviceRequest = request.getServiceRequest();
				Complaint complaint = complaintService
						.getComplaintByCRN(service_request_id);
				if (Objects.nonNull(complaint)) {
					BeanUtils.copyProperties(serviceRequest, complaint);
					User user = userService.getUserByUsername(requestInfo
							.getRequesterId());
					complaint.getComplainant().setUserDetail(user);
					complaint.getComplainant().setName(user.getName());
					complaint.getComplainant().setMobile(user.getMobileNumber());
					complaint.getComplainant().setEmail(user.getEmailId());
					complaint.setStatus(complaintStatusService
							.getByName(serviceRequest.getStatusDetails().name()));

					if (Objects.nonNull(serviceRequest.getCrossHierarchyId())
							&& Long.parseLong(serviceRequest.getCrossHierarchyId()) > 0) {
						final Long locationId = Long.parseLong(serviceRequest
								.getCrossHierarchyId());
						final CrossHierarchy crosshierarchy = crossHierarchyService
								.findById(locationId);
						complaint.setLocation(crosshierarchy.getParent());
						complaint.setChildLocation(crosshierarchy.getChild());
						complaint.getChildLocation().setParent(
								crosshierarchy.getChild().getParent());
					}
					if (Objects.nonNull(serviceRequest.getComplaintTypeCode())) {
						final ComplaintType complaintType = complaintTypeService
								.findByCode(serviceRequest.getComplaintTypeCode());
						complaint.setComplaintType(complaintType);
					}
					complaint.setReceivingMode(ReceivingMode.WEBSITE);

					Complaint savedComplaint = complaintService.update(complaint,
							serviceRequest.getApprovalPosition(),
							serviceRequest.getApprovalComment());

					serviceRequest.setEscalationDate(savedComplaint
							.getEscalationDate().toString());
					serviceRequest.setLastModifiedDate(savedComplaint
							.getLastModifiedDate().toString());

					ServiceRequestRes serviceRequestResponse = new ServiceRequestRes();
					ResponseInfo responseInfo = resInfo;
					serviceRequestResponse.setResposneInfo(responseInfo);
					serviceRequestResponse.getServiceRequests().add(serviceRequest);

					return serviceRequestResponse;
				} else
					throw new Exception("Invalid sevice request id");

			} else {
				// Error handling part to be done.
				throw new Exception("Please send All Mandatory fields in request");
			}
		}
		catch(Exception exception)
		{
			throw exception;
		}
	}

	@RequestMapping(value = "/requests", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ServiceRequestRes updateServiceRequest(
			@RequestParam String jurisdiction_id,
			@RequestParam String service_code, @RequestParam String status,
			@RequestParam String service_request_id,
			@RequestParam String api_id, @RequestParam String ver,
			@RequestParam String ts, @RequestParam String start_date)
					throws Exception {
		try
		{
			Authentication authUser = SecurityContextHolder.getContext().getAuthentication();
			List<Complaint> complaintsList = complaintService.getBySearchInputs(
					service_code, status, service_request_id, start_date);

			ServiceRequestRes serviceRequestResponse = new ServiceRequestRes();
			ResponseInfo responseInfo = new ResponseInfo(api_id, ver,
					new DateTime().toString(), "7gduf46t3erhg", "", "true");
			serviceRequestResponse.setResposneInfo(responseInfo);

			for (Complaint complaint : complaintsList) {
				ServiceRequest serviceRequest = new ServiceRequest();
				BeanUtils.copyProperties(complaint, serviceRequest);

				serviceRequest.setEscalationDate(complaint.getEscalationDate()
						.toString());
				serviceRequest.setLastModifiedDate(complaint.getLastModifiedDate()
						.toString());
				serviceRequest
				.setCreatedDate(complaint.getCreatedDate().toString());
				serviceRequest.setStatusDetails(ComplaintStatus.valueOf(complaint
						.getStatus().getName()));
				serviceRequest.setComplaintTypeName(complaint.getComplaintType()
						.getName());
				serviceRequest.setComplaintTypeCode(complaint.getComplaintType()
						.getCode());
				serviceRequest.setFirstName(complaint.getComplainant().getName());
				serviceRequest.setPhone(complaint.getComplainant().getMobile());
				serviceRequest.setEmail(complaint.getComplainant().getEmail());

				serviceRequestResponse.getServiceRequests().add(serviceRequest);
			}
			return serviceRequestResponse;
		}
		catch(Exception exception){
			throw exception;
		}
	}

	private String getUserRole(String requesterId) {
		if (StringUtils.isBlank(requesterId))
			return "ANONYMOUS";
		else
			return "CITIZEN";
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorRes> handleError(Exception ex) {
		ErrorRes response = new ErrorRes();
		response.setResposneInfo(resInfo);
		Error error = new Error();
		error.setCode(400);
		error.setDescription("General Server Error");
		response.setError(error);
		return new ResponseEntity<ErrorRes>(response, HttpStatus.BAD_REQUEST);
	}
}