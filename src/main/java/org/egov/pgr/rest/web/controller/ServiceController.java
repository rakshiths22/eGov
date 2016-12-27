package org.egov.pgr.rest.web.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.egov.pgr.entity.ComplaintType;
import org.egov.pgr.rest.web.model.ResponseInfo;
import org.egov.pgr.rest.web.model.Service;
import org.egov.pgr.rest.web.model.ServiceRes;
import org.egov.pgr.service.ComplaintTypeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceController {

	@Autowired
	private ComplaintTypeService complaintTypeService;

	@RequestMapping(value = "/services", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
	public ServiceRes getServiceList(@RequestParam String jurisdictionId,
			@RequestParam String api_id, @RequestParam String ver,
			@RequestParam String ts, @RequestParam String action,
			@RequestParam String did, @RequestParam String msg_id,
			@RequestParam String requester_id, @RequestParam String auth_token)
			throws IllegalAccessException, InvocationTargetException {
		try {
			ServiceRes serviceRes = new ServiceRes();
			List<ComplaintType> complaintTypes = complaintTypeService.findAll();
			List<Service> serviceList = new ArrayList<>();

			for (ComplaintType complaintType : complaintTypes) {
				Service service = new Service();
				BeanUtils.copyProperties(complaintType, service);
				service.setCategory(complaintType.getCategory().getName());
				serviceList.add(service);
			}
			serviceRes.setServices(serviceList);
			serviceRes.setResposneInfo(new ResponseInfo(api_id, ver, new Date()
					.toString(), msg_id, requester_id, "Active"));
			return serviceRes;
		} catch (Exception exception) {
			throw exception;
		}
	}
}