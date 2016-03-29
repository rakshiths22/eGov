/**
 * eGov suite of products aim to improve the internal efficiency,transparency,
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
package org.egov.works.lineestimate.entity;

import java.math.BigDecimal;

public class LineEstimateForLoaSearchResult {
    private Long id;
    private String adminSanctionNumber;
    private String estimateNumber;
    private String nameOfWork;
    private String currentOwner;
    private String createdBy;
    private String adminSanctionBy;
    private BigDecimal estimateAmount;
    private BigDecimal actualEstimateAmount;

    public LineEstimateForLoaSearchResult() {
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getAdminSanctionNumber() {
        return adminSanctionNumber;
    }

    public void setAdminSanctionNumber(final String adminSanctionNumber) {
        this.adminSanctionNumber = adminSanctionNumber;
    }

    public String getEstimateNumber() {
        return estimateNumber;
    }

    public void setEstimateNumber(final String estimateNumber) {
        this.estimateNumber = estimateNumber;
    }

    public String getNameOfWork() {
        return nameOfWork;
    }

    public void setNameOfWork(final String nameOfWork) {
        this.nameOfWork = nameOfWork;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    public BigDecimal getEstimateAmount() {
        return estimateAmount;
    }

    public void setEstimateAmount(final BigDecimal estimateAmount) {
        this.estimateAmount = estimateAmount;
    }

    public String getCurrentOwner() {
        return currentOwner;
    }

    public void setCurrentOwner(final String currentOwner) {
        this.currentOwner = currentOwner;
    }

    public String getAdminSanctionBy() {
        return adminSanctionBy;
    }

    public void setAdminSanctionBy(final String adminSanctionBy) {
        this.adminSanctionBy = adminSanctionBy;
    }

    public BigDecimal getActualEstimateAmount() {
        return actualEstimateAmount;
    }

    public void setActualEstimateAmount(final BigDecimal actualEstimateAmount) {
        this.actualEstimateAmount = actualEstimateAmount;
    }
}