/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 * accountability and the service delivery of the government  organizations.
 *
 *  Copyright (C) 2016  eGovernments Foundation
 *
 *  The updated version of eGov suite of products as by eGovernments Foundation
 *  is available at http://www.egovernments.org
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see http://www.gnu.org/licenses/ or
 *  http://www.gnu.org/licenses/gpl.html .
 *
 *  In addition to the terms of the GPL license to be adhered to in using this
 *  program, the following additional terms are to be complied with:
 *
 *      1) All versions of this program, verbatim or modified must carry this
 *         Legal Notice.
 *
 *      2) Any misrepresentation of the origin of the material is prohibited. It
 *         is required that all modified versions of this material be marked in
 *         reasonable ways as different from the original version.
 *
 *      3) This license does not grant any rights to any user of the program
 *         with regards to rights under trademark law for use of the trade names
 *         or trademarks of eGovernments Foundation.
 *
 *  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.infra.workflow.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.egov.infra.workflow.entity.State;
import org.egov.infra.workflow.entity.State.StateStatus;
import org.egov.infra.workflow.repository.StateRepository;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class StateService {

    private final StateRepository stateRepository;
    
    @PersistenceContext
    private EntityManager entityManager;


    @Autowired
    public StateService(final StateRepository stateRepository) {
        this.stateRepository = stateRepository;
    }

    public boolean isPositionUnderWorkflow(final Long posId) {
        return stateRepository.countByOwnerPosition_Id(posId) > 0;
    }

    public List<String> getAssignedWorkflowTypeNames(List<Long> ownerIds,List<String> enabledTypes) {
        return stateRepository.findAllTypeByOwnerAndStatus(ownerIds,enabledTypes);
    }

    public State getStateById(Long id) {
        return stateRepository.findOne(id);
    }
    
    @Transactional
    public State create(final State state) {
        return stateRepository.save(state);
    }

    @Transactional
    public State update(final State state) {
        return stateRepository.save(state);
    }
    public Session getSession() {
        return entityManager.unwrap(Session.class);
    }
    
   public  List<State>  getStates(List<Long> ownerIds,List<String> types,Long userId)
    {
     return getSession().createCriteria(State.class)
            .setFlushMode(FlushMode.MANUAL).setReadOnly(true).setCacheable(true)
            .add(Restrictions.in("type", types))
            .add(Restrictions.in("ownerPosition.id", ownerIds))
            .add(Restrictions.ne("status", StateStatus.ENDED))
            .add(Restrictions.not(Restrictions.conjunction().add(Restrictions.eq("status", StateStatus.STARTED))
                    .add(Restrictions.eq("createdBy.id", userId)))).addOrder(Order.desc("createdDate"))
                    .list();  
    
    }

}