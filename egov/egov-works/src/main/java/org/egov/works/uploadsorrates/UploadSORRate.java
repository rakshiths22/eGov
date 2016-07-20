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
package org.egov.works.uploadsorrates;

import java.math.BigDecimal;
import java.util.Date;

import org.egov.common.entity.UOM;
import org.egov.works.models.masters.ScheduleCategory;
import org.egov.works.models.masters.ScheduleOfRate;

public class UploadSORRate {

    private String sorCode;

    private String sorCategoryCode;

    private String sorDescription;

    private String uomCode;

    private BigDecimal rate;

    private Date fromDate;

    private Date toDate;

    private BigDecimal marketRate;

    private Date marketFromDate;

    private Date marketToDate;

    private String finalStatus;

    private ScheduleOfRate scheduleOfRate;

    private ScheduleCategory scheduleCategory;

    private UOM uom;

    private String errorReason;

    private Boolean createSor;

    public String getSorCode() {
        return sorCode;
    }

    public void setSorCode(String sorCode) {
        this.sorCode = sorCode;
    }

    public String getSorCategoryCode() {
        return sorCategoryCode;
    }

    public void setSorCategoryCode(String sorCategoryCode) {
        this.sorCategoryCode = sorCategoryCode;
    }

    public String getSorDescription() {
        return sorDescription;
    }

    public void setSorDescription(String sorDescription) {
        this.sorDescription = sorDescription;
    }

    public String getUomCode() {
        return uomCode;
    }

    public void setUomCode(String uomCode) {
        this.uomCode = uomCode;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public BigDecimal getMarketRate() {
        return marketRate;
    }

    public void setMarketRate(BigDecimal marketRate) {
        this.marketRate = marketRate;
    }

    public Date getMarketFromDate() {
        return marketFromDate;
    }

    public void setMarketFromDate(Date marketFromDate) {
        this.marketFromDate = marketFromDate;
    }

    public Date getMarketToDate() {
        return marketToDate;
    }

    public void setMarketToDate(Date marketToDate) {
        this.marketToDate = marketToDate;
    }

    public String getFinalStatus() {
        return finalStatus;
    }

    public void setFinalStatus(String finalStatus) {
        this.finalStatus = finalStatus;
    }

    public ScheduleOfRate getScheduleOfRate() {
        return scheduleOfRate;
    }

    public void setScheduleOfRate(ScheduleOfRate scheduleOfRate) {
        this.scheduleOfRate = scheduleOfRate;
    }

    public ScheduleCategory getScheduleCategory() {
        return scheduleCategory;
    }

    public void setScheduleCategory(ScheduleCategory scheduleCategory) {
        this.scheduleCategory = scheduleCategory;
    }

    public UOM getUom() {
        return uom;
    }

    public void setUom(UOM uom) {
        this.uom = uom;
    }

    public String getErrorReason() {
        return errorReason;
    }

    public void setErrorReason(String errorReason) {
        this.errorReason = errorReason;
    }

    public Boolean getCreateSor() {
        return createSor;
    }

    public void setCreateSor(Boolean createSor) {
        this.createSor = createSor;
    }

}