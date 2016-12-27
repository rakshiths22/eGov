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

package org.egov.pgr.rest.web.controller.common;

import static org.egov.infra.web.support.json.adapter.HibernateProxyTypeAdapter.FACTORY;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.egov.infra.admin.common.service.IdentityRecoveryService;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.admin.master.service.DepartmentService;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.utils.FileStoreUtils;
import org.egov.pgr.rest.web.adapter.DepartmentAdaptor;
import org.egov.pgr.rest.web.adapter.UserAdapter;
import org.egov.pgr.rest.web.controller.core.PgrRestController;
import org.egov.pgr.rest.web.controller.core.PgrRestResponse;
import org.egov.pgr.utils.constants.PGRConstants;
import org.egov.portal.entity.Citizen;
import org.egov.portal.service.CitizenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;

@RestController
@RequestMapping("/rest")
public class CommonController extends PgrRestController {

    private static final Logger LOGGER = Logger.getLogger(CommonController.class);

    @Autowired
    protected FileStoreUtils fileStoreUtils;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private CitizenService citizenService;

    @Autowired
    private IdentityRecoveryService identityRecoveryService;

    @RequestMapping(value = "/sendOTP", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<String> sendOTP(final HttpServletRequest request) {
        final PgrRestResponse res = PgrRestResponse.newInstance();
        final String mobileNo = request.getParameter("identity");
        try {
            if (!mobileNo.matches("\\d{10}"))
                return res.error(getMessage("msg.invalid.mobileno"));
            citizenService.sendOTPMessage(mobileNo);
            return res.setDataAdapter(new UserAdapter()).success(getMessage("sendOTP.success"));
        } catch (final Exception e) {
            LOGGER.error("EGOV-PGRREST ERROR ", e);
            return res.error(getMessage("server.error"));
        }
    }

    @RequestMapping(value = "/recoverPassword", method = RequestMethod.POST)
    public ResponseEntity<String> passwordRecover(final HttpServletRequest request) {
        final PgrRestResponse res = PgrRestResponse.newInstance();
        try {
            final String identity = request.getParameter("identity");
            final String redirectURL = request.getParameter("redirectURL");

            final String token = request.getParameter("token");
            String newPassword, confirmPassword;

            if (org.apache.commons.lang.StringUtils.isEmpty(identity))
                return res.error(getMessage("msg.invalid.request"));

            // for reset password with otp
            if (!org.apache.commons.lang.StringUtils.isEmpty(token)) {
                newPassword = request.getParameter("newPassword");
                confirmPassword = request.getParameter("confirmPassword");

                if (org.apache.commons.lang.StringUtils.isEmpty(newPassword))
                    return res.error(getMessage("msg.invalid.request"));
                else if (!newPassword.equals(confirmPassword))
                    return res.error(getMessage("msg.pwd.not.match"));
                else if (identityRecoveryService.validateAndResetPassword(token, newPassword))
                    return res.success("", getMessage("msg.pwd.reset.success"));
                else
                    return res.error(getMessage("msg.pwd.otp.invalid"));

            }

            if (identity.matches("\\d+")) {
                if (!identity.matches("\\d{10}"))
                    return res.error(getMessage("msg.invalid.mobileno"));
            } else if (!identity.matches("^[A-Za-z0-9+_.-]+@(.+)$"))
                return res.error(getMessage("msg.invalid.mail"));

            final Citizen citizen = citizenService.getCitizenByUserName(identity);

            if (citizen == null)
                return res.error(getMessage("user.not.found"));

            if (identityRecoveryService.generateAndSendUserPasswordRecovery(
                    identity, redirectURL + "/egi/login/password/reset?token=", true))
                return res.success("", "OTP for recovering password has been sent to your mobile"
                        + (org.apache.commons.lang.StringUtils.isEmpty(citizen.getEmailId()) ? "" : " and mail"));

            return res.error("Password send failed");
        } catch (final Exception e) {
            LOGGER.error("EGOV-PGRREST ERROR ", e);
            return res.error(getMessage("server.error"));
        }

    }

    @RequestMapping(value = "/common/downloadfile", method = RequestMethod.GET)
    public void download(@RequestParam("fileStoreId") final String fileStoreId, final HttpServletResponse response)
            throws IOException {
        fileStoreUtils.fetchFileAndWriteToStream(fileStoreId, PGRConstants.MODULE_NAME, false, response);
    }

    @RequestMapping(value = "/common/departments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getDepartments(@ModelAttribute("department") final Department department, final Model model)
            throws IOException {
        final String jsonResponse = toJSON(departmentService.getAllDepartments(), Department.class,
                DepartmentAdaptor.class);
        return jsonResponse;
    }

    public static <T> String toJSON(final Collection<T> objects, final Class<? extends T> objectClazz,
            final Class<? extends JsonSerializer<T>> adptorClazz) {
        try {
            return new GsonBuilder().registerTypeAdapterFactory(FACTORY)
                    .registerTypeAdapter(objectClazz, adptorClazz.newInstance()).create().toJson(objects);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ApplicationRuntimeException("Could not convert object list to json string", e);
        }
    }

}