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
package org.egov.ptis.web.controller.reports;

import java.math.BigDecimal;

import org.egov.demand.model.EgBill;
import org.egov.ptis.domain.dao.property.BasicPropertyDAO;
import org.egov.ptis.domain.entity.property.BasicProperty;
import org.egov.ptis.domain.service.notice.RecoveryNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/esdnotice")
public class ESDNoticeController {

    @Autowired
    private RecoveryNoticeService recoveryNoticeService;

    @Autowired
    private BasicPropertyDAO basicPropertyDAO;

    @RequestMapping(value = "/searchform", method = RequestMethod.GET)
    public String searchForm(final Model model) {
        model.addAttribute("egBill", new EgBill());
        return "esdnotice-form";
    }

    @RequestMapping(value = "/searchform", method = RequestMethod.POST)
    public String searchProperty(@ModelAttribute("egBill") final EgBill egBill, final Model model,
            final BindingResult errors) {
        final BasicProperty basicProperty = basicPropertyDAO.getBasicPropertyByPropertyID(egBill.getConsumerId());
        boolean billExists = false;
        if (basicProperty == null)
            errors.reject("property.invalid", "property.invalid");
        else {
            final BigDecimal totalDue = recoveryNoticeService.getTotalPropertyTaxDue(basicProperty);
            if (totalDue.compareTo(BigDecimal.ZERO) == 0)
                errors.reject("no.propertytax.due", "no.propertytax.due");
            else {
                billExists = recoveryNoticeService.getDemandBillByAssessmentNo(basicProperty);
                if (!billExists)
                    errors.reject("demandbill.not.exists", "demandbill.not.exists");
            }
        }
        if (errors.hasErrors()) {
            model.addAttribute("esdNotice", new EgBill());
            return "esdnotice-form";
        } else
            return "redirect:/esdnotice/generatenotice/" + egBill.getConsumerId();
    }

    @RequestMapping(value = "/generatenotice/{assessmentNo}", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<byte[]> generateNotice(final Model model, @PathVariable final String assessmentNo) {
        final BasicProperty basicProperty = basicPropertyDAO.getBasicPropertyByPropertyID(assessmentNo);
        return recoveryNoticeService.generateESDNotice(basicProperty);
    }

}
