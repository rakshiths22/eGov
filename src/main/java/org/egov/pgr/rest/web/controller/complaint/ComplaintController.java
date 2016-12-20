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

package org.egov.pgr.rest.web.controller.complaint;

import javax.validation.ValidationException;

import org.apache.log4j.Logger;
import org.egov.infra.admin.master.entity.CrossHierarchy;
import org.egov.infra.admin.master.service.CrossHierarchyService;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.utils.FileStoreUtils;
import org.egov.pgr.entity.Complaint;
import org.egov.pgr.entity.ComplaintType;
import org.egov.pgr.entity.enums.ReceivingMode;
import org.egov.pgr.rest.web.adapter.ComplaintAdapter;
import org.egov.pgr.rest.web.controller.core.PgrRestController;
import org.egov.pgr.service.ComplaintService;
import org.egov.pgr.service.ComplaintStatusMappingService;
import org.egov.pgr.service.ComplaintStatusService;
import org.egov.pgr.service.ComplaintTypeCategoryService;
import org.egov.pgr.service.ComplaintTypeService;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ComplaintController extends PgrRestController {

    private static final Logger LOGGER = Logger.getLogger(ComplaintController.class);

    @Autowired
    protected ComplaintStatusService complaintStatusService;

    @Autowired(required = true)
    protected ComplaintService complaintService;

    @Autowired
    protected ComplaintTypeCategoryService complaintTypeCategoryService;

    @Autowired
    protected ComplaintTypeService complaintTypeService;

    @Autowired
    protected CrossHierarchyService crossHierarchyService;

    @Autowired
    protected FileStoreUtils fileStoreUtils;

    @Autowired
    protected ComplaintStatusMappingService complaintStatusMappingService;

    @Autowired
    private SecurityUtils securityUtils;

    @RequestMapping(value = "/complaint/create", method = RequestMethod.POST)
    public ResponseEntity<String> complaintCreate(
            @RequestParam(value = "complaintJSON", required = false) final String complaintJSON/*
                                                                                                * ,
                                                                                                * @RequestParam("files") final
                                                                                                * MultipartFile[] files
                                                                                                */) {
        try {

            final JSONObject complaintRequest = (JSONObject) JSONValue.parse(complaintJSON);

            final Complaint complaint = new Complaint();
            /*
             * if (securityUtils.currentUserType().equals(UserType.EMPLOYEE)) if (complaintRequest.containsKey("complainantName")
             * && complaintRequest.containsKey("complainantMobileNo")) { if
             * (org.apache.commons.lang.StringUtils.isEmpty(complaintRequest.get("complainantName").toString()) ||
             * org.apache.commons.lang.StringUtils .isEmpty(complaintRequest.get("complainantMobileNo").toString())) return
             * getResponseHandler().error(getMessage("msg.complaint.reg.failed.user"));
             * complaint.getComplainant().setName(complaintRequest.get("complainantName").toString());
             * complaint.getComplainant().setMobile(complaintRequest.get("complainantMobileNo").toString()); if
             * (complaintRequest.containsKey("complainantEmail")) { final String email =
             * complaintRequest.get("complainantEmail").toString(); if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) return
             * getResponseHandler().error(getMessage("msg.invalid.mail")); complaint.getComplainant().setEmail(email); } } else if
             * (!complaintRequest.containsKey("complainantName") && !complaintRequest.containsKey("complainantMobileNo") &&
             * !complaintRequest.containsKey("complainantEmail")) { final User currentUser = securityUtils.getCurrentUser();
             * complaint.getComplainant().setName(currentUser.getName());
             * complaint.getComplainant().setMobile(currentUser.getMobileNumber()); if
             * (!org.apache.commons.lang.StringUtils.isEmpty(currentUser.getEmailId()))
             * complaint.getComplainant().setEmail(currentUser.getEmailId()); } else return
             * getResponseHandler().error(getMessage("msg.complaint.reg.failed.user"));
             */

            complaint.getComplainant().setName("Testing");
            complaint.getComplainant().setMobile("0000000000");

            final long complaintTypeId = Long.valueOf((String) complaintRequest.get("service_code"));
            if (complaintRequest.get("crosshierarchyId") != null
                    && Long.valueOf((String) complaintRequest.get("crosshierarchyId")) > 0) {
                final long locationId = Long.valueOf((String) complaintRequest.get("crosshierarchyId"));
                final CrossHierarchy crosshierarchy = crossHierarchyService.findById(locationId);
                complaint.setLocation(crosshierarchy.getParent());
                complaint.setChildLocation(crosshierarchy.getChild());
                complaint.getChildLocation().setParent(crosshierarchy.getChild().getParent());
            }
            if (complaintRequest.get("lng") != null && Double.valueOf((String) complaintRequest.get("lng")) > 0) {
                final double lng = Double.valueOf((String) complaintRequest.get("lng"));
                complaint.setLng(lng);
            }
            if (complaintRequest.get("lat") != null && Double.valueOf((String) complaintRequest.get("lat")) > 0) {
                final double lat = Double.valueOf((String) complaintRequest.get("lat"));
                complaint.setLat(lat);
            }
            if (complaint.getLocation() == null && (complaint.getLat() == 0 || complaint.getLng() == 0))
                return getResponseHandler().error(getMessage("location.required"));
            complaint.setDetails(complaintRequest.get("details").toString());
            complaint.setLandmarkDetails(complaintRequest.get("landmarkDetails").toString());
            if (complaintTypeId > 0) {
                final ComplaintType complaintType = complaintTypeService.findBy(complaintTypeId);
                complaint.setComplaintType(complaintType);
            }
            complaint.setReceivingMode(ReceivingMode.MOBILE);
            /*
             * if (files.length > 0) complaint.setSupportDocs(addToFileStore(files));
             */
            complaintService.createComplaint(complaint);
            return getResponseHandler().setDataAdapter(new ComplaintAdapter()).success(complaint,
                    getMessage("msg.complaint.reg.success"));
        } catch (final ValidationException e) {
            return getResponseHandler().error(getMessage(e.getMessage()));
        } catch (final Exception e) {
            LOGGER.error("EGOV-API ERROR ", e);
            return getResponseHandler().error(getMessage("server.error"));
        }
    }

}