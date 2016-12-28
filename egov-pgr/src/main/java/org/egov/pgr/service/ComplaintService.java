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

package org.egov.pgr.service;

import org.egov.eis.entity.Assignment;
import org.egov.eis.service.AssignmentService;
import org.egov.eis.service.EisCommonService;
import org.egov.eis.service.PositionMasterService;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.admin.master.entity.Module;
import org.egov.infra.admin.master.entity.Role;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.BoundaryService;
import org.egov.infra.admin.master.service.RoleService;
import org.egov.infra.messaging.MessagingService;
import org.egov.infra.persistence.entity.enums.UserType;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.utils.ApplicationNumberGenerator;
import org.egov.infra.utils.StringUtils;
import org.egov.infra.workflow.entity.State;
import org.egov.infra.workflow.entity.StateHistory;
import org.egov.pgr.entity.Complaint;
import org.egov.pgr.entity.enums.ComplaintStatus;
import org.egov.pgr.entity.enums.ReceivingMode;
import org.egov.pgr.repository.ComplaintRepository;
import org.egov.pgr.service.es.ComplaintIndexService;
import org.egov.pgr.utils.constants.PGRConstants;
import org.egov.pims.commons.Position;
import org.egov.portal.entity.CitizenInbox;
import org.egov.portal.entity.CitizenInboxBuilder;
import org.egov.portal.entity.enums.MessageType;
import org.egov.portal.entity.enums.Priority;
import org.egov.portal.service.CitizenInboxService;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ValidationException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;

import static org.egov.pgr.entity.enums.ComplaintStatus.FORWARDED;
import static org.egov.pgr.entity.enums.ComplaintStatus.PROCESSING;
import static org.egov.pgr.entity.enums.ComplaintStatus.REGISTERED;
import static org.egov.pgr.entity.enums.ComplaintStatus.REOPENED;
import static org.egov.pgr.utils.constants.PGRConstants.COMMENT;
import static org.egov.pgr.utils.constants.PGRConstants.DATE;
import static org.egov.pgr.utils.constants.PGRConstants.DELIMITER_COLON;
import static org.egov.pgr.utils.constants.PGRConstants.DEPT;
import static org.egov.pgr.utils.constants.PGRConstants.NOASSIGNMENT;
import static org.egov.pgr.utils.constants.PGRConstants.STATUS;
import static org.egov.pgr.utils.constants.PGRConstants.UPDATEDBY;
import static org.egov.pgr.utils.constants.PGRConstants.UPDATEDUSERTYPE;
import static org.egov.pgr.utils.constants.PGRConstants.USER;
import static org.egov.pgr.utils.constants.PGRConstants.USERTYPE;

@Service
@Transactional(readOnly = true)
public class ComplaintService {

	private static final Logger LOG = LoggerFactory.getLogger(ComplaintService.class);
	public final String COMPLAINT_ALL = "ALL";
	public final String COMPLAINT_PENDING = "PENDING";
	public final String COMPLAINT_COMPLETED = "COMPLETED";
	public final String COMPLAINT_REJECTED = "REJECTED";
	public final String COMPLAINTS_FILED = "FILED";
	public final String COMPLAINTS_RESOLVED = "RESOLVED";
	public final String COMPLAINTS_UNRESOLVED = "UNRESOLVED";
	final String[] pendingStatus = {"REGISTERED", "FORWARDED", "PROCESSING", "NOTCOMPLETED", "REOPENED" };
	final String[] completedStatus = { "COMPLETED", "WITHDRAWN", "CLOSED" };
	final String[] rejectedStatus = { "REJECTED" };
	final String[] resolvedStatus = { "COMPLETED", "WITHDRAWN", "CLOSED", "REJECTED" };
	@Autowired
	private ComplaintRepository complaintRepository;
	@Autowired
	private ComplaintStatusService complaintStatusService;
	@Autowired
	private SecurityUtils securityUtils;
	@Autowired
	private ComplaintRouterService complaintRouterService;
	@Autowired
	private EisCommonService eisCommonService;
	@Autowired
	private CitizenInboxService citizenInboxService;
	@Autowired
	private BoundaryService boundaryService;
	@Autowired
	private MessagingService messagingService;
	@Autowired
	private ApplicationNumberGenerator applicationNumberGenerator;
	@Autowired
	private EscalationService escalationService;
	@Autowired
	private PositionMasterService positionMasterService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private AssignmentService assignmentService;
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private ComplaintIndexService complaintIndexService;

	@Transactional
	public Complaint createComplaint(final Complaint complaint) throws ValidationException {

		if (StringUtils.isBlank(complaint.getCrn()))
			complaint.setCrn(applicationNumberGenerator.generate());
		//        final User user = securityUtils.getCurrentUser();
		//        complaint.getComplainant().setUserDetail(user);
		/*
		 * if (!SecurityUtils.isCurrentUserAnonymous() && securityUtils.currentUserType().equals(UserType.CITIZEN)) {
		 * complaint.getComplainant().setEmail(user.getEmailId()); complaint.getComplainant().setName(user.getName());
		 * complaint.getComplainant().setMobile(user.getMobileNumber()); }
		 */
		complaint.setStatus(complaintStatusService.getByName("REGISTERED"));
		if (complaint.getLocation() == null && complaint.getLat() != 0.0 && complaint.getLng() != 0.0)
			try {
				final Long bndryId = boundaryService.getBoundaryIdFromShapefile(complaint.getLat(), complaint.getLng());
				if (bndryId != null && bndryId != 0) {
					final Boundary location = boundaryService.getBoundaryById(bndryId);
					complaint.setLocation(location);
				} else
					throw new ValidationException("gis.location.info.not.found");
			} catch (final Exception e) {
				throw new ValidationException("gis.location.info.not.found");
			}

		final Position assignee = complaintRouterService.getAssignee(complaint);
		complaint.transition().start().withSenderName(complaint.getComplainant().getName())
		.withComments("Grievance registered with Complaint Number : " + complaint.getCrn())
		.withStateValue(complaint.getStatus().getName()).withOwner(assignee).withDateInfo(new Date());

		complaint.setAssignee(assignee);
		complaint.setEscalationDate(new Date());
		complaint.setEscalationDate(escalationService.getExpiryDate(complaint));

		if (null != complaint.getComplaintType() && null != complaint.getComplaintType().getDepartment())
			complaint.setDepartment(complaint.getComplaintType().getDepartment());
		else if (null != assignee)
			complaint.setDepartment(assignee.getDeptDesig().getDepartment());

		complaintRepository.save(complaint);
		pushMessage(complaint);
		sendEmailandSms(complaint);

		//complaintIndexService.createComplaintIndex(complaint);

		return complaint;
	}

	/**
	 * @param complaint
	 * @param approvalPosition
	 * @param approvalComent
	 * @return If the status is changed to completed/withdrawn then terminate/end the workflow. Even if the poistion is selected
	 * no need to consider position as it is end of workflow.else If position is found then it is forwarding only. Else it is
	 * update by official or citizen
	 */

	@Transactional
	public Complaint update(final Complaint complaint, final Long approvalPosition, final String approvalComent) {
		final Role goRole = roleService.getRoleByName(PGRConstants.GO_ROLE_NAME);
		String userName = null;

		//Temproary solution
		if(complaint.getComplainant().getUserDetail().getType().equals(UserType.CITIZEN))
			userName = complaint.getComplainant().getName();
		else
			userName = complaint.getComplainant().getName() + DELIMITER_COLON + complaint.getComplainant().getName();

		//        if (securityUtils.getCurrentUser().getType().equals(UserType.CITIZEN))
		//            userName = securityUtils.getCurrentUser().getName();
		//        else
		//            userName = securityUtils.getCurrentUser().getUsername() + DELIMITER_COLON + securityUtils.getCurrentUser().getName();

		if (complaint.getStatus().getName().equalsIgnoreCase(ComplaintStatus.COMPLETED.toString())
				|| complaint.getStatus().getName().equalsIgnoreCase(ComplaintStatus.WITHDRAWN.toString())
				|| complaint.getStatus().getName().equalsIgnoreCase(ComplaintStatus.REJECTED.toString())) {
			complaint.setDepartment(complaint.getAssignee().getDeptDesig().getDepartment());
			LOG.debug("Terminating Grievance Workflow");
			//            if (!securityUtils.getCurrentUser().getRoles().contains(goRole))
			if(!complaint.getComplainant().getUserDetail().getRoles().contains(goRole))	
				complaint.transition(true).end().withComments(approvalComent)
				.withStateValue(complaint.getStatus().getName()).withSenderName(userName)
				.withDateInfo(new Date());
			else
				complaint.transition(true).end().withComments(approvalComent)
				.withStateValue(complaint.getStatus().getName()).withSenderName(userName)
				.withDateInfo(new Date()).withOwner(complaint.getState().getOwnerPosition());
		} else if (null != approvalPosition && !approvalPosition.equals(Long.valueOf(0))) {
			final Position owner = positionMasterService.getPositionById(approvalPosition);
			complaint.setAssignee(owner);
			complaint.setDepartment(complaint.getAssignee().getDeptDesig().getDepartment());
			//            if (!securityUtils.getCurrentUser().getRoles().contains(goRole))
			if(!complaint.getComplainant().getUserDetail().getRoles().contains(goRole))	
				complaint.transition(true).withOwner(owner).withComments(approvalComent).withSenderName(userName)
				.withStateValue(complaint.getStatus().getName()).withDateInfo(new Date());
			else
				complaint.transition(true).withComments(approvalComent).withStateValue(complaint.getStatus().getName())
				.withSenderName(userName).withDateInfo(new Date()).withOwner(owner);
		} else {
			complaint.setDepartment(complaint.getAssignee().getDeptDesig().getDepartment());
			//            if (!securityUtils.getCurrentUser().getRoles().contains(goRole))
			if(!complaint.getComplainant().getUserDetail().getRoles().contains(goRole))
				complaint.transition(true).withComments(approvalComent).withSenderName(userName)
				.withStateValue(complaint.getStatus().getName()).withDateInfo(new Date());
			else
				complaint.transition(true).withComments(approvalComent).withSenderName(userName)
				.withStateValue(complaint.getStatus().getName()).withDateInfo(new Date())
				.withOwner(complaint.getState().getOwnerPosition());
		}

		complaintRepository.saveAndFlush(complaint);
		pushMessage(complaint);

		if (complaint.getStatus().getName().equalsIgnoreCase(ComplaintStatus.COMPLETED.toString()) ||
				complaint.getStatus().getName().equalsIgnoreCase(ComplaintStatus.REJECTED.toString()))
			sendSmsOnCompletion(complaint);
		if (!complaint.getStatus().getName().equalsIgnoreCase(ComplaintStatus.COMPLETED.toString()) &&
				!complaint.getStatus().getName().equalsIgnoreCase(ComplaintStatus.REJECTED.toString())
				&& !complaint.getStatus().getName().equalsIgnoreCase(ComplaintStatus.WITHDRAWN.toString()))
			sendSmsToOfficials(complaint);

		//        complaintIndexService.updateComplaintIndex(complaint, approvalPosition, approvalComent);

		return complaint;
	}

	public Complaint getComplaintById(final Long complaintID) {
		return complaintRepository.findOne(complaintID);
	}

	public Session getCurrentSession() {
		return entityManager.unwrap(Session.class);
	}

	public Complaint getComplaintByCRN(final String crn) {
		return complaintRepository.findByCrn(crn);
	}

	public List<Complaint> getComplaintsEligibleForEscalation() {
		final Criteria criteria = getCurrentSession().createCriteria(Complaint.class, "complaint")
				.createAlias("complaint.status", "complaintStatus");

		criteria.add(Restrictions.disjunction().add(Restrictions.eq("complaintStatus.name", REOPENED.name()))
				.add(Restrictions.eq("complaintStatus.name", FORWARDED.name()))
				.add(Restrictions.eq("complaintStatus.name", PROCESSING.name()))
				.add(Restrictions.eq("complaintStatus.name", REGISTERED.name())))
				.add(Restrictions.lt("complaint.escalationDate", new DateTime().toDate()))
				.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

		return criteria.list();
	}

	private void pushMessage(final Complaint savedComplaint) {

		final CitizenInboxBuilder citizenInboxBuilder = new CitizenInboxBuilder(MessageType.USER_MESSAGE,
				getHeaderMessage(savedComplaint), getDetailedMessage(savedComplaint),
				savedComplaint.getLastModifiedDate(), savedComplaint.getCreatedBy(), Priority.High);
		final String strQuery = "select md from Module md where md.name=:name";
		final Query hql = getCurrentSession().createQuery(strQuery);
		hql.setParameter("name", "PGR");

		citizenInboxBuilder.module((Module) hql.uniqueResult());
		citizenInboxBuilder.identifier(savedComplaint.getCrn());
		citizenInboxBuilder.link("/pgr/complaint/update/" + savedComplaint.getCrn());
		citizenInboxBuilder.state(savedComplaint.getState());
		citizenInboxBuilder.status(savedComplaint.getStatus().getName());

		final CitizenInbox citizenInbox = citizenInboxBuilder.build();
		citizenInboxService.pushMessage(citizenInbox);
	}

	private String getHeaderMessage(final Complaint savedComplaint) {
		final StringBuilder headerMessage = new StringBuilder();
		if (savedComplaint.getStatus().getName().equals("REGISTERED"))
			headerMessage.append("Grievance Recorded");
		else
			headerMessage.append("Grievance Redressal");
		return headerMessage.toString();
	}

	private String getDetailedMessage(final Complaint savedComplaint) {
		final StringBuilder detailedMessage = new StringBuilder();
		detailedMessage.append("Grievance No. ").append(savedComplaint.getCrn()).append(" regarding ")
		.append(savedComplaint.getComplaintType().getName()).append(" in ")
		.append(savedComplaint.getStatus().getName()).append(" status.");
		return detailedMessage.toString();
	}

	public List<Hashtable<String, Object>> getHistory(final Complaint complaint) {
		User user = null;
		final List<Hashtable<String, Object>> historyTable = new ArrayList<Hashtable<String, Object>>();
		final State state = complaint.getState();
		final Hashtable<String, Object> map = new Hashtable<String, Object>(0);
		map.put(DATE, state.getDateInfo());
		map.put(COMMENT, state.getComments() != null ? state.getComments() : StringUtils.EMPTY);
		if (state.getLastModifiedBy().getType().equals(UserType.CITIZEN)
				|| state.getLastModifiedBy().getType().equals(UserType.SYSTEM))
			map.put(UPDATEDBY, complaint.getComplainant().getName());
		else
			map.put(UPDATEDBY, state.getSenderName() != null ? state.getSenderName()
					: state.getLastModifiedBy().getUsername() + DELIMITER_COLON + state.getLastModifiedBy().getName());
		map.put(UPDATEDUSERTYPE, state.getLastModifiedBy().getType());
		map.put(STATUS, state.getValue());
		final Position ownerPosition = state.getOwnerPosition();
		user = state.getOwnerUser();
		if (null != user) {
			map.put(USER, user.getUsername() + DELIMITER_COLON + user.getName());
			map.put(USERTYPE, null != user ? user.getType() : StringUtils.EMPTY);
			map.put(DEPT, null != eisCommonService.getDepartmentForUser(user.getId())
					? eisCommonService.getDepartmentForUser(user.getId()).getName() : StringUtils.EMPTY);
		} else if (null != ownerPosition && null != ownerPosition.getDeptDesig()) {
			final List<Assignment> assignmentList = assignmentService.getAssignmentsForPosition(ownerPosition.getId(),
					new Date());
			user = !assignmentList.isEmpty() ? assignmentList.get(0).getEmployee() : null;
			map.put(USER, null != user
					? user.getUsername() + DELIMITER_COLON + user.getName() + DELIMITER_COLON
							+ ownerPosition.getDeptDesig().getDesignation().getName()
							: NOASSIGNMENT + DELIMITER_COLON + ownerPosition.getName());
			map.put(USERTYPE, null != user ? user.getType() : StringUtils.EMPTY);
			map.put(DEPT, null != ownerPosition.getDeptDesig().getDepartment()
					? ownerPosition.getDeptDesig().getDepartment().getName() : StringUtils.EMPTY);
		}
		historyTable.add(map);
		if (!complaint.getStateHistory().isEmpty() && complaint.getStateHistory() != null) {
			final List<StateHistory> complaintStateHistory = complaint.getStateHistory();
			Collections.reverse(complaintStateHistory);
			for (final StateHistory stateHistory : complaintStateHistory) {
				final Hashtable<String, Object> HistoryMap = new Hashtable<String, Object>(0);
				HistoryMap.put(DATE, stateHistory.getDateInfo());
				HistoryMap.put(COMMENT, stateHistory.getComments() != null ? stateHistory.getComments()
						: StringUtils.EMPTY);
				if (stateHistory.getLastModifiedBy().getType().equals(UserType.CITIZEN)
						|| stateHistory.getLastModifiedBy().getType().equals(UserType.SYSTEM))
					HistoryMap.put(UPDATEDBY, complaint.getComplainant().getName());
				else
					HistoryMap.put(UPDATEDBY, stateHistory.getSenderName());
				HistoryMap.put(UPDATEDUSERTYPE, stateHistory.getLastModifiedBy().getType());
				HistoryMap.put(STATUS, stateHistory.getValue());
				final Position owner = stateHistory.getOwnerPosition();
				user = stateHistory.getOwnerUser();
				if (null != user) {
					HistoryMap.put(USER, user.getUsername() + DELIMITER_COLON + user.getName());
					HistoryMap.put(USERTYPE, null != user ? user.getType() : StringUtils.EMPTY);
					HistoryMap.put(DEPT, null != eisCommonService.getDepartmentForUser(user.getId())
							? eisCommonService.getDepartmentForUser(user.getId()).getName()
									: StringUtils.EMPTY);
				} else if (null != owner && null != owner.getDeptDesig()) {
					final List<Assignment> assignments = assignmentService.getAssignmentsForPosition(owner.getId(), new Date());
					user = !assignments.isEmpty() ? assignments.get(0).getEmployee() : null;
					HistoryMap
					.put(USER,
							null != user
							? user.getUsername() + DELIMITER_COLON + user.getName() + DELIMITER_COLON
									+ owner.getDeptDesig().getDesignation().getName()
									: NOASSIGNMENT + DELIMITER_COLON + owner.getName());
					HistoryMap.put(USERTYPE, null != user ? user.getType() : StringUtils.EMPTY);
					HistoryMap.put(DEPT, null != owner.getDeptDesig().getDepartment()
							? owner.getDeptDesig().getDepartment().getName() : StringUtils.EMPTY);
				}
				historyTable.add(HistoryMap);
			}
		}
		return historyTable;
	}

	public void sendEmailandSms(final Complaint complaint) {
		final String formattedCreatedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm")
		.format(complaint.getCreatedDate());

		final StringBuffer emailBody = new StringBuffer().append("Dear ").append(complaint.getComplainant().getName())
				.append(",\n \n \tThank you for registering a grievance (").append(complaint.getCrn())
				.append("). Your grievance is registered successfully.\n \tPlease use this number for all future references.")
				.append("\n \n Grievance Details - \n \n Complaint type - ")
				.append(complaint.getComplaintType().getName());
		if (complaint.getLocation() != null)
			emailBody.append(" \n Location details - ").append(complaint.getLocation().getName());
		emailBody.append("\n Grievance description - ").append(complaint.getDetails()).append("\n Grievance status -")
		.append(complaint.getStatus().getName()).append("\n Grievance Registration Date - ")
		.append(formattedCreatedDate);
		final StringBuffer emailSubject = new StringBuffer().append("Registered Grievance -").append(complaint.getCrn())
				.append(" successfuly");
		final StringBuffer smsBody = new StringBuffer().append("Your grievance for ")
				.append(complaint.getComplaintType().getName())
				.append(" has been registered successfully with tracking number (").append(complaint.getCrn())
				.append("). Please use this number for all future references.");
		messagingService.sendEmail(complaint.getComplainant().getEmail(), emailSubject.toString(), emailBody.toString());
		messagingService.sendSMS(complaint.getComplainant().getMobile(), smsBody.toString());
		final Position owner = complaint.getState().getOwnerPosition();
		if (null != owner && null != owner.getDeptDesig()) {
			final List<Assignment> assignments = assignmentService.getAssignmentsForPosition(owner.getId(), new Date());
			if (!assignments.isEmpty()) {
				final User user = assignments.get(0).getEmployee();
				if (null != user) {
					final StringBuffer smsBodyOfficial = new StringBuffer().append("New Grievance for ")
							.append(complaint.getComplaintType().getName())
							.append(" is registered by ").append(complaint.getComplainant().getName() == null ? "Anonymous User"
									: complaint.getComplainant().getName())
									.append(", ")
									.append(complaint.getComplainant().getMobile() == null ? StringUtils.EMPTY
											: complaint.getComplainant().getMobile())
											.append(" at ").append(complaint.getLocation().getName());
					if (complaint.getLatlngAddress() != null)
						smsBodyOfficial.append(", " + complaint.getLatlngAddress());
					else
						smsBodyOfficial
						.append(complaint.getChildLocation() != null ? ", " + complaint.getChildLocation().getName()
								: StringUtils.EMPTY);
					smsBodyOfficial.append(complaint.getLandmarkDetails() != null ? ", " + complaint.getLandmarkDetails()
							: StringUtils.EMPTY);
					messagingService.sendSMS(user.getMobileNumber(), smsBodyOfficial.toString());
				}
			}
		}
	}

	public void sendSmsOnCompletion(final Complaint complaint) {
		final StringBuffer smsBody = new StringBuffer().append("Your Grievance regarding ")
				.append(complaint.getComplaintType().getName())
				.append(" with tracking number '").append(complaint.getCrn())
				.append("' is updated to ").append(complaint.getStatus().getName());
		messagingService.sendSMS(complaint.getComplainant().getMobile(), smsBody.toString());

	}

	public void sendSmsToOfficials(final Complaint complaint) {
		final Position owner = complaint.getState().getOwnerPosition();
		String senderName = StringUtils.EMPTY;
		senderName = complaint.getState().getSenderName().contains(DELIMITER_COLON)
				? complaint.getState().getSenderName().split(DELIMITER_COLON)[1]
						: complaint.getState().getSenderName();
				if (null != owner && null != owner.getDeptDesig()) {
					final User user = eisCommonService.getUserForPosition(owner.getId(), new Date());
					if (null != user) {
						final StringBuffer smsBodyOfficial = new StringBuffer().append(user.getName() + ", ")
								.append(complaint.getCrn() + " by ")
								.append(complaint.getComplainant().getName() == null ? "Anonymous User"
										: complaint.getComplainant().getName())
										.append(", ")
										.append(complaint.getComplainant().getMobile() == null ? StringUtils.EMPTY
												: complaint.getComplainant().getMobile())
												.append(" for " + complaint.getComplaintType().getName() + " from ")
												.append(complaint.getLocation().getName());
						if (complaint.getLatlngAddress() != null)
							smsBodyOfficial.append(", " + complaint.getLatlngAddress());
						else
							smsBodyOfficial
							.append(complaint.getChildLocation() != null ? ", " + complaint.getChildLocation().getName()
									: StringUtils.EMPTY);
						smsBodyOfficial.append(complaint.getLandmarkDetails() != null ? ", " + complaint.getLandmarkDetails()
								: StringUtils.EMPTY);
						smsBodyOfficial.append(" handled by " + senderName + " has been Forwarded to you.");
						messagingService.sendSMS(user.getMobileNumber(), smsBodyOfficial.toString());
					}
				}
	}

	public List<ReceivingMode> getAllReceivingModes() {
		return Arrays.asList(ReceivingMode.values());
	}

	public Page<Complaint> getLatest(final int page, final int pageSize) {
		final int offset = page - 1;
		return complaintRepository.findByLatestComplaint(securityUtils.getCurrentUser(), new PageRequest(offset, pageSize));
	}

	public Page<Complaint> getMyComplaint(final int page, final int pageSize) {
		final int offset = page - 1;
		return complaintRepository.findByMyComplaint(securityUtils.getCurrentUser(), new PageRequest(offset, pageSize));
	}

	public List<Complaint> getNearByComplaint(final int page, final float lat, final float lng, final int distance,
			final int pageSize) {
		final Long offset = new Long((page - 1) * pageSize);
		final Long limit = new Long(pageSize + 1);
		return complaintRepository.findByNearestComplaint(securityUtils.getCurrentUser().getId(), new Float(lat),
				new Float(lng), new Long(distance), limit, offset);
	}

	public String getEmailBody(final Complaint complaint) {
		final String formattedCreatedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm")
		.format(complaint.getCreatedDate());
		final StringBuffer emailBody = new StringBuffer()
		.append(" %0D%0A Grievance Details -  %0D%0A %0D%0A CRN - ").append(complaint.getCrn())
		.append(" %0D%0A Grievance Type -")
		.append(complaint.getComplaintType().getName());
		if (complaint.getDepartment() != null)
			emailBody.append("  %0D%0A Grievance department  - ").append(complaint.getDepartment().getName());
		if (complaint.getComplainant().getName() != null)
			emailBody.append("  %0D%0A Complainant name - ").append(complaint.getComplainant().getName());
		if (complaint.getComplainant().getMobile() != null)
			emailBody.append("  %0D%0A Complainant mobile number - ").append(complaint.getComplainant().getMobile());
		if (complaint.getLocation() != null)
			emailBody.append("  %0D%0A Location details - ").append(complaint.getLocation().getName());

		emailBody.append(" %0D%0A Grievance details - ").append(complaint.getDetails()).append(" %0D%0A Grievance status -")
		.append(complaint.getStatus().getName()).append(" %0D%0A Grievance Registration Date - ")
		.append(formattedCreatedDate);
		return emailBody.toString();
	}

	public List<Complaint> getPendingGrievances() {
		final User user = securityUtils.getCurrentUser();
		final String[] pendingStatus = { "REGISTERED", "FORWARDED", "PROCESSING", "NOTCOMPLETED", "REOPENED" };
		final Criteria criteria = entityManager.unwrap(Session.class).createCriteria(Complaint.class, "complaint")
				.createAlias("complaint.state", "state").createAlias("complaint.status", "status");
		criteria.add(Restrictions.in("status.name", pendingStatus));
		criteria.add(Restrictions.eq("complaint.assignee", positionMasterService.getCurrentPositionForUser(user.getId())));
		return criteria.list();
	}

	public Page<Complaint> getMyPendingGrievances(final int page, final int pageSize) {
		final int offset = page - 1;
		return complaintRepository.findMyComplaintyByStatus(securityUtils.getCurrentUser(), pendingStatus,
				new PageRequest(offset, pageSize));
	}

	public Page<Complaint> getMyCompletedGrievances(final int page, final int pageSize) {
		final int offset = page - 1;
		return complaintRepository.findMyComplaintyByStatus(securityUtils.getCurrentUser(), completedStatus,
				new PageRequest(offset, pageSize));
	}

	public Page<Complaint> getMyRejectedGrievances(final int page, final int pageSize) {
		final int offset = page - 1;
		return complaintRepository.findMyComplaintyByStatus(securityUtils.getCurrentUser(), rejectedStatus,
				new PageRequest(offset, pageSize));
	}

	public HashMap<String, Integer> getMyComplaintsCount() {
		final HashMap<String, Integer> complaintsCount = new HashMap<String, Integer>();
		complaintsCount.put(COMPLAINT_ALL,
				complaintRepository.getMyComplaintsTotalCount(securityUtils.getCurrentUser()).intValue());
		complaintsCount.put(COMPLAINT_PENDING,
				complaintRepository.getMyComplaintCountByStatus(securityUtils.getCurrentUser(), pendingStatus).intValue());
		complaintsCount.put(COMPLAINT_COMPLETED,
				complaintRepository.getMyComplaintCountByStatus(securityUtils.getCurrentUser(), completedStatus).intValue());
		complaintsCount.put(COMPLAINT_REJECTED,
				complaintRepository.getMyComplaintCountByStatus(securityUtils.getCurrentUser(), rejectedStatus).intValue());
		return complaintsCount;
	}

	public HashMap<String, Integer> getComplaintsTotalCount() {
		final HashMap<String, Integer> complaintsCount = new HashMap<String, Integer>();
		complaintsCount.put(COMPLAINTS_FILED, complaintRepository.getTotalComplaintsCount().intValue());
		complaintsCount.put(COMPLAINTS_RESOLVED, complaintRepository.getComplaintsTotalCountByStatus(resolvedStatus).intValue());
		complaintsCount.put(COMPLAINTS_UNRESOLVED, complaintRepository.getComplaintsTotalCountByStatus(pendingStatus).intValue());
		return complaintsCount;
	}

	public List<Complaint> getOpenComplaints() {
		final List<String> statusList = Arrays.asList("REGISTERED", "FORWARDED", "REOPENED", "PROCESSING");
		return complaintRepository.findByStatusNameIn(statusList);
	}

	public List<Complaint> getBySearchInputs(String code, String status,String crn,String start_date) throws ParseException{



		final Criteria criteria = entityManager.unwrap(Session.class).createCriteria(Complaint.class, "complaint")
				.createAlias("complaint.status", "status").createAlias("complaint.complaintType", "complainttype");

		if(StringUtils.isNotBlank(status))
			criteria.add(Restrictions.eq("status.name", status));
		if(StringUtils.isNotBlank(code))
			criteria.add(Restrictions.eq("complainttype.code", code));
		if(StringUtils.isNotBlank(crn))
			criteria.add(Restrictions.eq("complaint.crn", crn));
		if(StringUtils.isNotBlank(start_date)){
			SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-dd");
			Date date = formatter.parse(start_date);
			criteria.add(Restrictions.le("complaint.createdDate", date));
		}

		return criteria.list();
	}

}
