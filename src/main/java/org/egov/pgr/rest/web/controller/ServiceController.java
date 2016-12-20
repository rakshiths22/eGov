package org.egov.pgr.rest.web.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.egov.pgr.entity.ComplaintType;
import org.egov.pgr.rest.web.model.Service;
import org.egov.pgr.rest.web.model.Services;
import org.egov.pgr.service.ComplaintTypeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

@RestController
public class ServiceController {

    @Autowired
    private ComplaintTypeService complaintTypeService;

    @RequestMapping(value = "/services", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String createService(@RequestParam final String jurisdictionId)
            throws IllegalAccessException, InvocationTargetException {
        final Gson gson = new Gson();
        final Services services = new Services();
        final List<ComplaintType> complaintTypes = complaintTypeService.findAll();
        final List<Service> serviceList = new ArrayList<Service>();

        for (final ComplaintType complaintType : complaintTypes) {
            final Service service = new Service();
            BeanUtils.copyProperties(complaintType, service);
            service.setCategory(complaintType.getCategory().getName());
            serviceList.add(service);
        }
        services.setServices(serviceList);
        return gson.toJson(services, Services.class);
    }

}
