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

package org.egov.pgr.rest.web.controller.citizen;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.egov.infra.admin.common.service.IdentityRecoveryService;
import org.egov.infra.admin.master.entity.Device;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.repository.DeviceRepository;
import org.egov.infra.admin.master.service.UserService;
import org.egov.infra.utils.FileStoreUtils;
import org.egov.pgr.rest.web.controller.core.PgrRestController;
import org.egov.pgr.rest.web.controller.core.PgrRestResponse;
import org.egov.pgr.rest.web.model.RequestInfo;
import org.egov.pgr.rest.web.model.ResponseInfo;
import org.egov.pgr.rest.web.model.UserRequest;
import org.egov.pgr.rest.web.model.UserResponse;
import org.egov.portal.entity.Citizen;
import org.egov.portal.service.CitizenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CitizenController extends PgrRestController {

    private static final Logger LOGGER = Logger.getLogger(CitizenController.class);

    @Autowired
    protected FileStoreUtils fileStoreUtils;

    @Autowired
    private CitizenService citizenService;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private UserService userservice;

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private IdentityRecoveryService identityRecoveryService;

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public ResponseEntity<String> logout(@RequestParam(value = "tenant_id", required = true) final String tenant_id,
            @RequestBody final RequestInfo request) {
        try {
            final OAuth2AccessToken token = null;// tokenStore.getAccessToken(authentication);
            if (token == null)
                return PgrRestResponse.newInstance().error(getMessage("msg.logout.unknown"));

            tokenStore.removeAccessToken(token);
            return PgrRestResponse.newInstance().success("", getMessage("msg.logout.success"));
        } catch (final Exception ex) {
            LOGGER.error("EGOV-PGRREST ERROR ", ex);
            return PgrRestResponse.newInstance().error(getMessage("server.error"));
        }
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public UserResponse registerCitizen(@RequestParam(value = "tenant_id", required = true) final String tenant_id,
            @RequestBody final UserRequest request) {
        final UserResponse response = new UserResponse();
        try {
            final Citizen citizenCreate = new Citizen();
            citizenCreate.setUsername(request.getUser().getMobileNo());
            citizenCreate.setMobileNumber(request.getUser().getMobileNo());
            citizenCreate.setName(request.getUser().getName());

            if (request.getUser().getEmail() != null && !request.getUser().getEmail().isEmpty())
                citizenCreate.setEmailId(request.getUser().getEmail());

            citizenCreate.setPassword(request.getUser().getPassword());
            Device device = deviceRepository.findByDeviceUId(request.getUser().getDeviceId());
            if (device == null) {
                device = new Device();
                device.setDeviceId(request.getUser().getDeviceId());
                device.setType(request.getUser().getDeviceType());
                device.setOSVersion(request.getUser().getOsVersion());
            }

            final User user = userservice.getUserByUsername(citizenCreate.getMobileNumber());

            if (user != null) {
                response.setResponseInfo(new ResponseInfo("", "", "", "", "", getMessage("user.register.duplicate.mobileno")));
                return response;
            }

            if (citizenCreate.getEmailId() != null && !citizenCreate.getEmailId().isEmpty()) {
                final User getUser = userservice.getUserByEmailId(citizenCreate.getEmailId());
                if (getUser != null) {
                    response.setResponseInfo(new ResponseInfo("", "", "", "", "", getMessage("user.register.duplicate.email")));
                    return response;
                }
            }

            if (request.getRequestInfo() != null && request.getRequestInfo().getAuthToken() != null &&
                    citizenService.isValidOTP(request.getRequestInfo().getAuthToken(), request.getUser().getMobileNo())) {
                citizenCreate.setActive(true);
                citizenCreate.getDevices().add(device);
                citizenService.create(citizenCreate);
                response.setResponseInfo(new ResponseInfo("", "", "", "", "", getMessage("msg.citizen.reg.success")));
                response.setUser(request.getUser());
                response.getUser().setUserName(request.getUser().getMobileNo());
                return response;
            } else {
                response.setResponseInfo(new ResponseInfo("", "", "", "", "", getMessage("msg.pwd.otp.invalid")));
                return response;
            }

        } catch (final Exception e) {
            LOGGER.error("EGOV-PGRREST ERROR ", e);
            response.setResponseInfo(new ResponseInfo("", "", "", "", "", getMessage("server.error")));
            return response;
        }
    }

    @RequestMapping(value = "/users", method = RequestMethod.PUT)
    public UserResponse updatePassword(@RequestParam(value = "tenant_id", required = true) final String tenant_id,
            @RequestBody final UserRequest request) {
        final UserResponse response = new UserResponse();

        try {
            final String token = request.getRequestInfo().getAuthToken();
            String newPassword, confirmPassword;

            if (StringUtils.isEmpty(request.getUser().getMobileNo())) {
                response.setResponseInfo(new ResponseInfo("", "", "", "", "", getMessage("msg.invalid.request")));
                return response;
            }

            // for reset password with otp
            if (!StringUtils.isEmpty(token)) {
                newPassword = request.getUser().getNewPassword();
                confirmPassword = request.getUser().getConfirmPassword();

                if (StringUtils.isEmpty(newPassword)) {
                    response.setResponseInfo(new ResponseInfo("", "", "", "", "", getMessage("msg.invalid.request")));
                    return response;
                } else if (!newPassword.equals(confirmPassword)) {
                    response.setResponseInfo(new ResponseInfo("", "", "", "", "", getMessage("msg.pwd.not.match")));
                    return response;
                } else if (identityRecoveryService.validateAndResetPassword(token, newPassword)) {
                    response.setResponseInfo(new ResponseInfo("", "", "", "", "", getMessage("msg.pwd.reset.success")));
                    return response;
                } else {
                    response.setResponseInfo(new ResponseInfo("", "", "", "", "", getMessage("msg.pwd.otp.invalid")));
                    return response;
                }

            } else {
                response.setResponseInfo(new ResponseInfo("", "", "", "", "", getMessage("msg.invalid.request")));
                return response;
            }

        } catch (final Exception e) {
            LOGGER.error("EGOV-PGRREST ERROR ", e);
            response.setResponseInfo(new ResponseInfo("", "", "", "", "", getMessage("server.error")));
            return response;
        }
    }
}