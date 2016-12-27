/* eGov suite of products aim to improve the internal efficiency,transparency,
   accountability and the service delivery of the government  organizations.

    Copyright (C) <2015>  eGovernments Foundation

    The updated version of eGov suite of products as by eGovernments Foundation
    is available at http://www.egovernments.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see http://www.gnu.org/licenses/ or
    http://www.gnu.org/licenses/gpl.html .

    In addition to the terms of the GPL license to be adhered to in using this
    program, the following additional terms are to be complied with:

        1) All versions of this program, verbatim or modified must carry this
           Legal Notice.

        2) Any misrepresentation of the origin of the material is prohibited. It
           is required that all modified versions of this material be marked in
           reasonable ways as different from the original version.

        3) This license does not grant any rights to any user of the program
           with regards to rights under trademark law for use of the trade names
           or trademarks of eGovernments Foundation.

  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.mrs.web.controller.application.registration;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.egov.infra.filestore.entity.FileStoreMapper;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.reporting.engine.ReportConstants.FileFormat;
import org.egov.infra.reporting.engine.ReportOutput;
import org.egov.infra.web.utils.WebUtils;
import org.egov.mrs.application.MarriageConstants;
import org.egov.mrs.application.service.MarriageCertificateService;
import org.egov.mrs.domain.service.MarriageRegistrationService;
import org.egov.mrs.domain.service.ReIssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Handles Marriage Registration/Rejection Certificate generation
 * 
 * @author nayeem
 *
 */
@Controller
@RequestMapping(value = "/certificate")
public class MarriageCertificateController {
	@Autowired
	private MarriageCertificateService marriageCertificateService;

	@Autowired
	private MarriageRegistrationService marriageRegistrationService;
	
	@Autowired
	private ReIssueService reIssueService;
	
	@Autowired
        @Qualifier("fileStoreService")
        protected FileStoreService fileStoreService;
	
	private ReportOutput reportOutput = null;
	
	@RequestMapping(value = "/registration", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<byte[]> showRegistrationCertificate(HttpServletRequest request,
			@RequestParam final Long id, final Model model, final HttpSession session) throws IOException {
	    return getResponseEntity((marriageRegistrationService.get(id)).getMarriageCertificate().get(0).getFileStore());
	}
	
	@RequestMapping(value = "/reissue", method = RequestMethod.GET)
        public @ResponseBody ResponseEntity<byte[]> showReIssuedCertificate(HttpServletRequest request,
                        @RequestParam final Long id, final Model model, final HttpSession session) throws IOException {
            return getResponseEntity((reIssueService.get(id)).getMarriageCertificate().get(0).getFileStore()); 
        }

	/**
	 * @param fsm
	 * @return
	 * @throws IOException
	 */
	private ResponseEntity<byte[]> getResponseEntity(final FileStoreMapper fsm ) throws IOException {
	    reportOutput = new ReportOutput();
	    final File file = fileStoreService.fetch(fsm, MarriageConstants.FILESTORE_MODULECODE);
            final byte[] bFile = FileUtils.readFileToByteArray(file);
            reportOutput.setReportOutputData(bFile);
            reportOutput.setReportFormat(FileFormat.PDF);
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/pdf"));
            headers.add("content-disposition", "inline;filename="+file.getName());
            return new ResponseEntity<byte[]>(reportOutput.getReportOutputData(),headers,HttpStatus.CREATED);
	}

}