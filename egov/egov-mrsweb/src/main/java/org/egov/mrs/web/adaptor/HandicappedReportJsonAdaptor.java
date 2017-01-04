/**
 * eGov suite of products aim to improve the internal efficiency,transparency,
   accountability and the service delivery of the government  organizations.

    Copyright (C) <2016>  eGovernments Foundation

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
package org.egov.mrs.web.adaptor;

import java.lang.reflect.Type;

import org.egov.mrs.entity.es.MarriageRegIndexSearchResult;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class HandicappedReportJsonAdaptor implements JsonSerializer<MarriageRegIndexSearchResult> {

    @Override
    public JsonElement serialize(final MarriageRegIndexSearchResult marriageRegIndexSearchResult, final Type type,
            final JsonSerializationContext jsc) {
        final JsonObject jsonObject = new JsonObject();
        if (marriageRegIndexSearchResult != null) {
            if (marriageRegIndexSearchResult.getRegistrationNumber() != null)
                jsonObject.addProperty("registrationNo", marriageRegIndexSearchResult.getRegistrationNumber());
            else
                jsonObject.addProperty("registrationNo", org.apache.commons.lang.StringUtils.EMPTY);
            if (marriageRegIndexSearchResult.getApplicationNumber() != null)
                jsonObject.addProperty("applicationNo", marriageRegIndexSearchResult.getApplicationNumber());
            else
                jsonObject.addProperty("applicationNo", org.apache.commons.lang.StringUtils.EMPTY);
            if (marriageRegIndexSearchResult.getRegistrationDate() != null)
                jsonObject.addProperty("registrationDate", marriageRegIndexSearchResult.getRegistrationDate().toString());
            else
                jsonObject.addProperty("registrationDate", org.apache.commons.lang.StringUtils.EMPTY);

            if (marriageRegIndexSearchResult.getMarriageDate() != null)
                jsonObject.addProperty("marriageDate", marriageRegIndexSearchResult.getMarriageDate().toString());
            else
                jsonObject.addProperty("marriageDate", org.apache.commons.lang.StringUtils.EMPTY);

            if (marriageRegIndexSearchResult.getHusbandName() != null)
                jsonObject.addProperty("husbandName", marriageRegIndexSearchResult.getHusbandName());
            else
                jsonObject.addProperty("husbandName", org.apache.commons.lang.StringUtils.EMPTY);
            if (marriageRegIndexSearchResult.getWifeName() != null)
                jsonObject.addProperty("wifeName", marriageRegIndexSearchResult.getWifeName());
            else
                jsonObject.addProperty("wifeName", org.apache.commons.lang.StringUtils.EMPTY);
            if (marriageRegIndexSearchResult.getZone() != null)
                jsonObject.addProperty("zone", marriageRegIndexSearchResult.getZone());
            else
                jsonObject.addProperty("zone", org.apache.commons.lang.StringUtils.EMPTY);
            if (marriageRegIndexSearchResult.getStatus() != null)
                jsonObject.addProperty("status", marriageRegIndexSearchResult.getStatus());
            else
                jsonObject.addProperty("status", org.apache.commons.lang.StringUtils.EMPTY);

        }
        return jsonObject;

    }
}
