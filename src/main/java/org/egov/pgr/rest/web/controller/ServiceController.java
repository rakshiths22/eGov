package org.egov.pgr.rest.web.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.egov.pgr.entity.ComplaintType;
import org.egov.pgr.rest.web.model.Error;
import org.egov.pgr.rest.web.model.ResponseInfo;
import org.egov.pgr.rest.web.model.Service;
import org.egov.pgr.rest.web.model.ServiceDefinition;
import org.egov.pgr.rest.web.model.ServiceDefinitionRes;
import org.egov.pgr.rest.web.model.ServiceRes;
import org.egov.pgr.service.ComplaintTypeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

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


	@RequestMapping(value = "/servicedetail", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
	public String getServiceDetail(@RequestParam String jurisdictionId,
			@RequestParam String serviceCode, @RequestParam String api_id,
			@RequestParam String ver, @RequestParam String ts,
			@RequestParam String action, @RequestParam String did,
			@RequestParam String msg_id,@RequestParam String requester_id, 
			@RequestParam String auth_token)throws Exception {
			Gson gson = new Gson();
			ComplaintType complaintType = complaintTypeService.findByCode(serviceCode);
			if(Objects.nonNull(complaintType)){
				ServiceDefinitionRes response = new ServiceDefinitionRes();
				response.setServiceDefinition(new ServiceDefinition(complaintType.getCode(),complaintType.getAttributes()));
				response.setResposneInfo(new ResponseInfo(api_id, ver, new Date()
				.toString(), msg_id, requester_id, "Active"));

				return gson.toJson(response, ServiceDefinitionRes.class);
			}
			else
			{
				throw new Exception();
			}
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Error> exceptionHandler(Exception ex) {
		Error error = new Error();
		error.setCode(400);
		error.setDescription("General server problem");
		return new ResponseEntity<Error>(error, HttpStatus.BAD_REQUEST);
	}
}
