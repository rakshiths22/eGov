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
package org.egov.collection.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.egov.collection.constants.CollectionConstants;
import org.egov.collection.entity.CollectionSummaryHeadWiseReport;
import org.egov.collection.entity.CollectionSummaryHeadWiseReportResult;
import org.egov.collection.entity.OnlinePaymentResult;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.stereotype.Service;

@Service
public class CollectionReportHeadWiseService {

    @PersistenceContext
    EntityManager entityManager;

    private static final Logger LOGGER = Logger.getLogger(CollectionReportHeadWiseService.class);

    public Session getCurrentSession() {
        return entityManager.unwrap(Session.class);
    }

    public SQLQuery getOnlinePaymentReportData(final String districtName, final String ulbName, final String fromDate,
            final String toDate, final String transactionId) {
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        final StringBuilder queryStr = new StringBuilder(500);
        queryStr.append("select * from public.onlinepayment_view opv where 1=1");

        if (StringUtils.isNotBlank(districtName))
            queryStr.append(" and opv.districtName=:districtName ");
        if (StringUtils.isNotBlank(ulbName))
            queryStr.append(" and opv.ulbName=:ulbName ");
        if (StringUtils.isNotBlank(fromDate))
            queryStr.append(" and opv.transactiondate>=:fromDate ");
        if (StringUtils.isNotBlank(toDate))
            queryStr.append(" and opv.transactiondate<=:toDate ");
        if (StringUtils.isNotBlank(transactionId))
            queryStr.append(" and opv.transactionnumber like :transactionnumber ");
        queryStr.append(" order by receiptdate desc ");

        final SQLQuery query = getCurrentSession().createSQLQuery(queryStr.toString());

        if (StringUtils.isNotBlank(districtName))
            query.setString("districtName", districtName);
        if (StringUtils.isNotBlank(ulbName))
            query.setString("ulbName", ulbName);
        try {
            if (StringUtils.isNotBlank(fromDate))
                query.setDate("fromDate", dateFormatter.parse(fromDate));
            if (StringUtils.isNotBlank(toDate))
                query.setDate("toDate", dateFormatter.parse(toDate));
        } catch (final ParseException e) {
            LOGGER.error("Exception parsing Date" + e.getMessage());
        }
        if (StringUtils.isNotBlank(transactionId))
            query.setString("transactionnumber", "%" + transactionId + "%");
        queryStr.append(" order by opv.receiptdate desc");
        query.setResultTransformer(new AliasToBeanResultTransformer(OnlinePaymentResult.class));
        return query;
    }

    public List<Object[]> getUlbNames(final String districtName) {
        final StringBuilder queryStr = new StringBuilder("select distinct ulbname from public.onlinepayment_view opv where 1=1");
        if (StringUtils.isNotBlank(districtName))
            queryStr.append(" and opv.districtName=:districtName ");
        final SQLQuery query = getCurrentSession().createSQLQuery(queryStr.toString());
        if (StringUtils.isNotBlank(districtName))
            query.setString("districtName", districtName);
        return query.list();
    }

    public List<Object[]> getDistrictNames() {
        final StringBuilder queryStr = new StringBuilder("select distinct districtname from public.onlinepayment_view");
        final SQLQuery query = getCurrentSession().createSQLQuery(queryStr.toString());
        return query.list();
    }

    public CollectionSummaryHeadWiseReportResult getCollectionSummaryReport(final Date fromDate, final Date toDate,
            final String paymentMode, final String source, final String glCode, final int status) {
        final SimpleDateFormat fromDateFormatter = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        final SimpleDateFormat toDateFormatter = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        final StringBuilder defaultQueryStr = new StringBuilder(500);
        final StringBuilder queryStr = new StringBuilder(500);
        queryStr.append("SELECT  (CASE WHEN EGF_INSTRUMENTTYPE.TYPE='cash' THEN count(*) END) AS CASH_COUNT,  ")
        .append("(CASE WHEN EGF_INSTRUMENTTYPE.TYPE='cheque' THEN count(*) WHEN EGF_INSTRUMENTTYPE.TYPE='dd' THEN count(*) END) AS CHEQUEDD_COUNT, ")
        .append(" (CASE WHEN EGF_INSTRUMENTTYPE.TYPE= 'online' THEN count(*) END) AS ONLINE_COUNT, ")
        .append(" EGCL_COLLECTIONHEADER.SOURCE AS SOURCE, EG_LOCATION.NAME AS COUNTER_NAME, EG_USER.NAME AS EMPLOYEE_NAME,GLCODE,")
        .append(" (CASE WHEN EGF_INSTRUMENTTYPE.TYPE='cash' THEN SUM(EGF_INSTRUMENTHEADER.INSTRUMENTAMOUNT) END) AS CASH_AMOUNT, ")
        .append(" (CASE WHEN EGF_INSTRUMENTTYPE.TYPE='cheque' THEN SUM(EGCL_COLLECTIONDETAILS.CRAMOUNT) WHEN EGF_INSTRUMENTTYPE.TYPE='dd' THEN SUM(EGCL_COLLECTIONDETAILS.CRAMOUNT) END) AS CHEQUEDD_AMOUNT,")
        .append(" (CASE WHEN EGF_INSTRUMENTTYPE.TYPE= 'online' THEN SUM(EGCL_COLLECTIONDETAILS.CRAMOUNT) END) AS ONLINE_AMOUNT, EG_USER.ID AS USERID FROM")
        .append(" EGCL_COLLECTIONHEADER EGCL_COLLECTIONHEADER INNER JOIN EGCL_COLLECTIONINSTRUMENT EGCL_COLLECTIONINSTRUMENT ON EGCL_COLLECTIONHEADER.ID = EGCL_COLLECTIONINSTRUMENT.COLLECTIONHEADER ")
        .append(" INNER JOIN EGF_INSTRUMENTHEADER EGF_INSTRUMENTHEADER ON EGCL_COLLECTIONINSTRUMENT.INSTRUMENTHEADER = EGF_INSTRUMENTHEADER.ID ")
        .append(" INNER JOIN EGW_STATUS EGW_STATUS ON EGCL_COLLECTIONHEADER.STATUS = EGW_STATUS.ID ")
        .append(" INNER JOIN EG_LOCATION EG_LOCATION ON EGCL_COLLECTIONHEADER.LOCATION = EG_LOCATION.ID ")
        .append(" INNER JOIN EGF_INSTRUMENTTYPE EGF_INSTRUMENTTYPE ON EGF_INSTRUMENTHEADER.INSTRUMENTTYPE = EGF_INSTRUMENTTYPE.ID ")
        .append(" INNER JOIN EGCL_COLLECTIONMIS EGCL_COLLECTIONMIS ON EGCL_COLLECTIONHEADER.ID = EGCL_COLLECTIONMIS.COLLECTIONHEADER ")
        .append(" INNER JOIN EG_USER EG_USER ON EGCL_COLLECTIONHEADER.CREATEDBY = EG_USER.ID ")
        .append(" INNER JOIN EGEIS_EMPLOYEE EG_EMPLOYEE ON EG_USER.ID = EG_EMPLOYEE.ID ")
        .append(" INNER JOIN EGEIS_ASSIGNMENT EGEIS_ASSIGNMENT ON EGEIS_ASSIGNMENT.EMPLOYEE = EG_EMPLOYEE.ID ")
        .append("INNER JOIN EGCL_COLLECTIONDETAILS EGCL_COLLECTIONDETAILS ON EGCL_COLLECTIONHEADER.ID = EGCL_COLLECTIONDETAILS.COLLECTIONHEADER ")
        .append(" INNER JOIN CHARTOFACCOUNTS CAO ON CAO.ID = EGCL_COLLECTIONDETAILS.CHARTOFACCOUNT WHERE")
        .append(" EGW_STATUS.DESCRIPTION != 'Cancelled' AND EGCL_COLLECTIONDETAILS.CRAMOUNT>0");

        final StringBuilder onlineQueryStr = new StringBuilder();
        onlineQueryStr
        .append("SELECT  (CASE WHEN EGF_INSTRUMENTTYPE.TYPE='cash' THEN count(*) END) AS CASH_COUNT,  ")
        .append("(CASE WHEN EGF_INSTRUMENTTYPE.TYPE='cheque' THEN count(*) WHEN EGF_INSTRUMENTTYPE.TYPE='dd' THEN count(*) END) AS CHEQUEDD_COUNT, ")
        .append(" (CASE WHEN EGF_INSTRUMENTTYPE.TYPE= 'online' THEN count(*) END) AS ONLINE_COUNT, ")
        .append(" EGCL_COLLECTIONHEADER.SOURCE AS SOURCE, '' AS COUNTER_NAME, '' AS EMPLOYEE_NAME,CAO.NAME || '-' || CAO.GLCODE AS GLCODE,")
        .append(" (CASE WHEN EGF_INSTRUMENTTYPE.TYPE='cash' THEN SUM(EGCL_COLLECTIONDETAILS.CRAMOUNT) END) AS CASH_AMOUNT, ")
        .append(" (CASE WHEN EGF_INSTRUMENTTYPE.TYPE='cheque' THEN SUM(EGCL_COLLECTIONDETAILS.CRAMOUNT) WHEN EGF_INSTRUMENTTYPE.TYPE='dd' THEN SUM(EGF_INSTRUMENTHEADER.INSTRUMENTAMOUNT) END) AS CHEQUEDD_AMOUNT,")
        .append(" (CASE WHEN EGF_INSTRUMENTTYPE.TYPE= 'online' THEN SUM(EGCL_COLLECTIONDETAILS.CRAMOUNT) END) AS ONLINE_AMOUNT, 0 AS USERID FROM ")
        .append(" EGCL_COLLECTIONHEADER EGCL_COLLECTIONHEADER INNER JOIN EGCL_COLLECTIONINSTRUMENT EGCL_COLLECTIONINSTRUMENT ON EGCL_COLLECTIONHEADER.ID = EGCL_COLLECTIONINSTRUMENT.COLLECTIONHEADER ")
        .append(" INNER JOIN EGF_INSTRUMENTHEADER EGF_INSTRUMENTHEADER ON EGCL_COLLECTIONINSTRUMENT.INSTRUMENTHEADER = EGF_INSTRUMENTHEADER.ID ")
        .append(" INNER JOIN EGW_STATUS EGW_STATUS ON EGCL_COLLECTIONHEADER.STATUS = EGW_STATUS.ID")
        .append(" INNER JOIN EGF_INSTRUMENTTYPE EGF_INSTRUMENTTYPE ON EGF_INSTRUMENTHEADER.INSTRUMENTTYPE = EGF_INSTRUMENTTYPE.ID")
        .append(" INNER JOIN EGCL_COLLECTIONMIS EGCL_COLLECTIONMIS ON EGCL_COLLECTIONHEADER.ID = EGCL_COLLECTIONMIS.COLLECTIONHEADER ")
        .append("INNER JOIN EGCL_COLLECTIONDETAILS EGCL_COLLECTIONDETAILS ON EGCL_COLLECTIONHEADER.ID = EGCL_COLLECTIONDETAILS.COLLECTIONHEADER ")
        .append(" INNER JOIN CHARTOFACCOUNTS CAO ON CAO.ID = EGCL_COLLECTIONDETAILS.CHARTOFACCOUNT WHERE")
        .append(" EGW_STATUS.DESCRIPTION != 'Cancelled' AND EGCL_COLLECTIONDETAILS.CRAMOUNT>0");
        final StringBuilder queryStrGroup = new StringBuilder();
        queryStrGroup
        .append(" GROUP BY  SOURCE, COUNTER_NAME, EMPLOYEE_NAME, USERID,CAO.NAME,CAO.GLCODE,EGF_INSTRUMENTTYPE.TYPE");

        if (fromDate != null && toDate != null) {
            queryStr.append(" AND EGCL_COLLECTIONHEADER.RECEIPTDATE between to_timestamp('"
                    + fromDateFormatter.format(fromDate) + "', 'YYYY-MM-DD HH24:MI:SS') and " + " to_timestamp('"
                    + toDateFormatter.format(toDate) + "', 'YYYY-MM-DD HH24:MI:SS') ");
            onlineQueryStr.append(" AND EGCL_COLLECTIONHEADER.RECEIPTDATE between to_timestamp('"
                    + fromDateFormatter.format(fromDate) + "', 'YYYY-MM-DD HH24:MI:SS') and " + " to_timestamp('"
                    + toDateFormatter.format(toDate) + "', 'YYYY-MM-DD HH24:MI:SS') ");
        }

        if (!source.isEmpty() && !source.equals(CollectionConstants.ALL)) {
            queryStr.append(" AND EGCL_COLLECTIONHEADER.SOURCE=:source");
            onlineQueryStr.append(" AND EGCL_COLLECTIONHEADER.SOURCE=:source");
        } else {
            queryStr.setLength(0);
            queryStr.append(onlineQueryStr);
        }

        if (glCode != null ){
            queryStr.append(" AND CAO.GLCODE =:glCode");
            onlineQueryStr.append(" AND CAO.GLCODE =:glCode");
        }
        else
        {
            queryStr.setLength(0);
            queryStr.append(onlineQueryStr);
        }
        
        if (status != -1 ) {
            queryStr.append(" AND EGCL_COLLECTIONHEADER.STATUS =:searchStatus");
            onlineQueryStr.append(" AND EGCL_COLLECTIONHEADER.STATUS =:searchStatus");
        }

        if (StringUtils.isNotBlank(paymentMode) && !paymentMode.equals(CollectionConstants.ALL)) {
            if (paymentMode.equals(CollectionConstants.INSTRUMENTTYPE_ONLINE)) {
                queryStr.setLength(0);
                onlineQueryStr.append(" AND EGF_INSTRUMENTTYPE.TYPE in (:paymentMode)");
                queryStr.append(onlineQueryStr);
                queryStr.append(queryStrGroup);
            } else {
                queryStr.append(" AND EGF_INSTRUMENTTYPE.TYPE in (:paymentMode)");
                queryStr.append(queryStrGroup);
                onlineQueryStr.append(" AND EGF_INSTRUMENTTYPE.TYPE in (:paymentMode)");
            }
        } else {
            defaultQueryStr.append(queryStr);
            defaultQueryStr.append(" AND EGF_INSTRUMENTTYPE.TYPE = 'cash'");
            defaultQueryStr.append(queryStrGroup);
            defaultQueryStr.append(" union ");
            defaultQueryStr.append(queryStr);
            defaultQueryStr.append(" AND EGF_INSTRUMENTTYPE.TYPE in ('cheque', 'dd')");
            defaultQueryStr.append(queryStrGroup);
            defaultQueryStr.append(" union ");
            defaultQueryStr.append(onlineQueryStr);
            defaultQueryStr.append(" AND EGF_INSTRUMENTTYPE.TYPE = 'online'");
            defaultQueryStr.append(queryStrGroup);
            queryStr.setLength(0);
            queryStr.append(defaultQueryStr);
        }
        final StringBuilder aggregateQueryStr = new StringBuilder();
        aggregateQueryStr.append(onlineQueryStr);
        aggregateQueryStr.append(" AND EGF_INSTRUMENTTYPE.TYPE = 'cash'");
        aggregateQueryStr.append(queryStrGroup);
        aggregateQueryStr.append(" union ");
        aggregateQueryStr.append(onlineQueryStr);
        aggregateQueryStr.append(" AND EGF_INSTRUMENTTYPE.TYPE in( 'cheque','dd')");
        aggregateQueryStr.append(queryStrGroup);
        aggregateQueryStr.append(" union ");
        aggregateQueryStr.append(onlineQueryStr);
        aggregateQueryStr.append(" AND EGF_INSTRUMENTTYPE.TYPE = 'online'");
        aggregateQueryStr.append(queryStrGroup);

        final StringBuilder finalQueryStr = new StringBuilder();
        finalQueryStr
        .append("SELECT cast(sum(CASH_COUNT) AS NUMERIC) AS CASH_COUNT,cast(sum(CHEQUEDD_COUNT) AS NUMERIC) AS CHEQUEDD_COUNT,cast(sum(ONLINE_COUNT) AS NUMERIC) AS ONLINE_COUNT,SOURCE,COUNTER_NAME,EMPLOYEE_NAME,GLCODE,cast(sum(CASH_AMOUNT) AS DOUBLE PRECISION) AS CASH_AMOUNT, cast(sum(CHEQUEDD_AMOUNT) AS DOUBLE PRECISION) AS CHEQUEDD_AMOUNT, cast(sum(ONLINE_AMOUNT) AS DOUBLE PRECISION) AS ONLINE_AMOUNT ,USERID FROM (");
        finalQueryStr
        .append(queryStr)
        .append(" ) AS RESULT GROUP BY RESULT.SOURCE,RESULT.COUNTER_NAME,RESULT.EMPLOYEE_NAME,RESULT.USERID,RESULT.GLCODE order by SOURCE,EMPLOYEE_NAME,GLCODE");

        final StringBuilder finalAggregateQryStr = new StringBuilder();
        finalAggregateQryStr
        .append("SELECT sum(CASH_COUNT) AS CASH_COUNT,sum(CHEQUEDD_COUNT) AS CHEQUEDD_COUNT,sum(ONLINE_COUNT) AS ONLINE_COUNT,SOURCE,COUNTER_NAME,EMPLOYEE_NAME,GLCODE,sum(CASH_AMOUNT) AS CASH_AMOUNT, sum(CHEQUEDD_AMOUNT) AS CHEQUEDD_AMOUNT, sum(ONLINE_AMOUNT) AS ONLINE_AMOUNT ,USERID FROM (");
        finalAggregateQryStr
        .append(aggregateQueryStr)
        .append(" ) AS RESULT GROUP BY RESULT.SOURCE,RESULT.COUNTER_NAME,RESULT.EMPLOYEE_NAME,RESULT.USERID,RESULT.GLCODE order by SOURCE,EMPLOYEE_NAME, GLCODE");

        final SQLQuery query = getCurrentSession().createSQLQuery(finalQueryStr.toString());

        final SQLQuery aggrQuery = getCurrentSession().createSQLQuery(finalAggregateQryStr.toString());

        if (!source.isEmpty() && !source.equals(CollectionConstants.ALL)) {
            query.setString("source", source);
            aggrQuery.setString("source", source);
        }
        if (glCode != null ) {
            query.setString("glCode", glCode);
            aggrQuery.setString("glCode", glCode);
        }
        if (status != -1 ) {
            query.setLong("searchStatus", status);
            aggrQuery.setLong("searchStatus", status);
        }
      
        if (StringUtils.isNotBlank(paymentMode) && !paymentMode.equals(CollectionConstants.ALL))
            if (paymentMode.equals(CollectionConstants.INSTRUMENTTYPE_CHEQUEORDD)) {
                query.setParameterList("paymentMode", new ArrayList<>(Arrays.asList("cheque", "dd")));
                aggrQuery.setParameterList("paymentMode", new ArrayList<>(Arrays.asList("cheque", "dd")));
            } else
            {
                query.setString("paymentMode", paymentMode);
                aggrQuery.setString("paymentMode", paymentMode);
            }
        final List<CollectionSummaryHeadWiseReport> reportResults = populateQueryResults(query.list());
        final List<CollectionSummaryHeadWiseReport> aggrReportResults = populateQueryResults(aggrQuery.list());
        final CollectionSummaryHeadWiseReportResult collResult = new CollectionSummaryHeadWiseReportResult();
        collResult.setCollectionSummaryReportList(reportResults);
        collResult.setAggrCollectionSummaryReportList(aggrReportResults);
        return collResult;
    }

    public List<CollectionSummaryHeadWiseReport> populateQueryResults(final List<Object[]> queryResults) {
        final List<CollectionSummaryHeadWiseReport> reportResults = new LinkedList<CollectionSummaryHeadWiseReport>();
        for (int i = 0; i < queryResults.size(); i++) {
            final Object[] arrayObjectInitialIndex = queryResults.get(i);
            final CollectionSummaryHeadWiseReport collSummaryReportResult = new CollectionSummaryHeadWiseReport();
            BigDecimal cashCnt = BigDecimal.ZERO, chequeddCnt = BigDecimal.ZERO, onlineCnt = BigDecimal.ZERO;
            cashCnt = (BigDecimal) arrayObjectInitialIndex[0] == null ? BigDecimal.ZERO
                    : (BigDecimal) arrayObjectInitialIndex[0];
            chequeddCnt = (BigDecimal) arrayObjectInitialIndex[1] == null ? BigDecimal.ZERO
                    : (BigDecimal) arrayObjectInitialIndex[1];
            onlineCnt = (BigDecimal) arrayObjectInitialIndex[2] == null ? BigDecimal.ZERO
                    : (BigDecimal) arrayObjectInitialIndex[2];
            collSummaryReportResult.setCashCount(cashCnt.equals(BigDecimal.ZERO) ? "" : cashCnt.toString());
            collSummaryReportResult.setChequeddCount(chequeddCnt.equals(BigDecimal.ZERO) ? "" : chequeddCnt.toString());
            collSummaryReportResult.setOnlineCount(onlineCnt.equals(BigDecimal.ZERO) ? "" : onlineCnt.toString());
            collSummaryReportResult.setSource((String) arrayObjectInitialIndex[3]);
            collSummaryReportResult.setCounterName((String) arrayObjectInitialIndex[4]);
            collSummaryReportResult.setEmployeeName((String) arrayObjectInitialIndex[5]);
            collSummaryReportResult.setGlCode((String) arrayObjectInitialIndex[6]);
            collSummaryReportResult.setCashAmount((Double) arrayObjectInitialIndex[7]);
            collSummaryReportResult.setChequeddAmount((Double) arrayObjectInitialIndex[8]);
            collSummaryReportResult.setOnlineAmount((Double) arrayObjectInitialIndex[9]);
            final BigDecimal receiptCount = cashCnt.add(chequeddCnt).add(onlineCnt);
            collSummaryReportResult
            .setTotalReceiptCount(receiptCount.equals(BigDecimal.ZERO) ? "" : receiptCount.toString());
            collSummaryReportResult.setTotalAmount((new Double(0.0).sum((Double) arrayObjectInitialIndex[7] == null ? new Double(0.0):(Double) arrayObjectInitialIndex[7],(
                    new Double(0.0).sum((Double) arrayObjectInitialIndex[8] != null ? (Double) arrayObjectInitialIndex[8]
                                    : new Double(0.0),
                                    (Double) arrayObjectInitialIndex[9] != null ? (Double) arrayObjectInitialIndex[9]
                                            : new Double(0.0))))));

            reportResults.add(collSummaryReportResult);
        }
        return reportResults;
    }
}
