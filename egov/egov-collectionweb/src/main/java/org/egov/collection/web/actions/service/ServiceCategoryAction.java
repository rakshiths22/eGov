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
package org.egov.collection.web.actions.service;

import java.util.Collection;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.egov.collection.constants.CollectionConstants;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infstr.models.ServiceCategory;
import org.egov.infstr.services.PersistenceService;

@ParentPackage("egov")
@Results({ @Result(name = ServiceCategoryAction.NEW, location = "serviceCategory-new.jsp"),
        @Result(name = ServiceCategoryAction.EDIT, location = "serviceCategory-edit.jsp"),
        @Result(name = ServiceCategoryAction.INDEX, location = "serviceCategory-index.jsp") })
public class ServiceCategoryAction extends BaseFormAction {

    private static final long serialVersionUID = 1L;
    private PersistenceService<ServiceCategory, Long> serviceCategoryService;
    private Collection<ServiceCategory> serviceCategoryList = null;
    private ServiceCategory serviceCategoryInstance = new ServiceCategory();
    private String code;

    @Action(value = "/service/serviceCategory-newform")
    public String newform() {
        return NEW;
    }

    @Action(value = "/service/serviceCategory-list")
    public String list() {
        serviceCategoryList = serviceCategoryService.findAll(CollectionConstants.SERVICECATEGORY_CODE);
        return INDEX;
    }

    @Action(value = "/service/serviceCategory-edit")
    public String edit() {
        serviceCategoryInstance = serviceCategoryService.findByNamedQuery(CollectionConstants.QUERY_SERVICE_CATEGORY_BY_CODE,
                code);
        return EDIT;
    }

    @Action(value = "/service/serviceCategory-save")
    public String save() {
        serviceCategoryService.update(serviceCategoryInstance);
        return list();
    }

    @Action(value = "/service/serviceCategory-create")
    public String create() {
        serviceCategoryService.create(serviceCategoryInstance);
        return list();
    }

    @Override
    public Object getModel() {
        return serviceCategoryInstance;
    }

    /**
     * @return the serviceCategoryList
     */
    public Collection<ServiceCategory> getServiceCategoryList() {
        return serviceCategoryList;
    }

    public void setServiceCategoryService(final PersistenceService<ServiceCategory, Long> serviceCategoryService) {
        this.serviceCategoryService = serviceCategoryService;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }
}
