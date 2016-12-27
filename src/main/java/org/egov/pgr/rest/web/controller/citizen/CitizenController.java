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

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.egov.infra.admin.master.entity.Device;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.repository.DeviceRepository;
import org.egov.infra.admin.master.service.UserService;
import org.egov.infra.utils.FileStoreUtils;
import org.egov.pgr.rest.web.adapter.UserAdapter;
import org.egov.pgr.rest.web.controller.core.PgrRestController;
import org.egov.pgr.rest.web.controller.core.PgrRestResponse;
import org.egov.portal.entity.Citizen;
import org.egov.portal.service.CitizenService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest")
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

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public ResponseEntity<String> logout(final HttpServletRequest request, final OAuth2Authentication authentication) {
        try {
            final OAuth2AccessToken token = tokenStore.getAccessToken(authentication);
            if (token == null)
                return PgrRestResponse.newInstance().error(getMessage("msg.logout.unknown"));

            tokenStore.removeAccessToken(token);
            return PgrRestResponse.newInstance().success("", getMessage("msg.logout.success"));
        } catch (final Exception ex) {
            LOGGER.error("EGOV-PGRREST ERROR ", ex);
            return PgrRestResponse.newInstance().error(getMessage("server.error"));
        }
    }

    @RequestMapping(value = "/createCitizen", method = RequestMethod.POST, consumes = { "application/json" })
    public @ResponseBody ResponseEntity<String> register(@RequestBody final JSONObject citizen) {
        final PgrRestResponse res = PgrRestResponse.newInstance();
        try {
            final Citizen citizenCreate = new Citizen();
            citizenCreate.setUsername(citizen.get("mobileNumber").toString());
            citizenCreate.setMobileNumber(citizen.get("mobileNumber").toString());
            citizenCreate.setName(citizen.get("name").toString());

            if (citizen.get("emailId") != null && !citizen.get("emailId").toString().trim().equals(""))
                citizenCreate.setEmailId(citizen.get("emailId").toString());

            citizenCreate.setPassword(citizen.get("password").toString());
            Device device = deviceRepository.findByDeviceUId(citizen.get("deviceId").toString());
            if (device == null) {
                device = new Device();
                device.setDeviceId(citizen.get("deviceId").toString());
                device.setType(citizen.get("deviceType").toString());
                device.setOSVersion(citizen.get("OSVersion").toString());
            }

            final User user = userservice.getUserByUsername(citizenCreate.getMobileNumber());

            if (user != null)
                return res.error(getMessage("user.register.duplicate.mobileno"));

            if (citizenCreate.getEmailId() != null && !citizenCreate.getEmailId().isEmpty()) {
                final User getUser = userservice.getUserByEmailId(citizenCreate.getEmailId());
                if (getUser != null)
                    return res.error(getMessage("user.register.duplicate.email"));
            }

            if (citizen.get("activationCode") != null &&
                    citizenService.isValidOTP(citizen.get("activationCode").toString(), citizen.get("mobileNumber").toString())) {
                citizenCreate.setActive(true);
                citizenCreate.getDevices().add(device);
                citizenService.create(citizenCreate);
                return res.setDataAdapter(new UserAdapter()).success(citizenCreate, getMessage("msg.citizen.reg.success"));
            } else
                return res.error(getMessage("msg.pwd.otp.invalid"));

        } catch (final Exception e) {
            LOGGER.error("EGOV-PGRREST ERROR ", e);
            return res.error(getMessage("server.error"));
        }
    }

}