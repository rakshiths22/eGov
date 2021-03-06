package org.egov.council.entity;

import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.filestore.entity.FileStoreMapper;
import org.egov.infra.persistence.entity.AbstractAuditable;
import org.egov.infra.persistence.entity.enums.Gender;
import org.egov.infra.persistence.validator.annotation.Unique;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.util.Date;

import static org.egov.council.entity.CouncilMember.SEQ_COUNCILMEMBER;

@Entity
@Unique(fields = {"name"}, enableDfltMsg = true)
@Table(name = "egcncl_members")
@SequenceGenerator(name = SEQ_COUNCILMEMBER, sequenceName = SEQ_COUNCILMEMBER, allocationSize = 1)
public class CouncilMember extends AbstractAuditable {

    public static final String SEQ_COUNCILMEMBER = "seq_egcncl_members";
    private static final long serialVersionUID = 8227838067322332444L;
    @Id
    @GeneratedValue(generator = SEQ_COUNCILMEMBER, strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "electionWard")
    private Boundary electionWard;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "designation")
    private CouncilDesignation designation;

    @ManyToOne
    @JoinColumn(name = "qualification")
    private CouncilQualification qualification;
    @ManyToOne
    @JoinColumn(name = "caste")
    private CouncilCaste caste;

    @ManyToOne
    @JoinColumn(name = "partyAffiliation")
    private CouncilParty partyAffiliation;
    
   
    

    @Enumerated(EnumType.ORDINAL)
    @NotNull
    private Gender gender;


    private Date birthDate;

    private Date electionDate;

    private Date oathDate;


    @Length(max = 15)
    private String mobileNumber;


    @Length(max = 52)
    private String emailId;

    @NotNull
    @Length(min = 2, max = 100)
    private String name;

    @NotNull
    @Enumerated(EnumType.ORDINAL)
    private CouncilMemberStatus status = CouncilMemberStatus.ACTIVE;

    @NotNull
    private String residentialAddress;


    @Transient
    private MultipartFile attachments;

    @Transient
    private Boolean checked;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "filestoreid")
    private FileStoreMapper photo;


    public FileStoreMapper getPhoto() {
        return photo;
    }

    public void setPhoto(FileStoreMapper photo) {
        this.photo = photo;
    }

    public MultipartFile getAttachments() {
        return attachments;
    }

    public void setAttachments(MultipartFile attachments) {
        this.attachments = attachments;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boundary getElectionWard() {
        return electionWard;
    }

    public void setElectionWard(Boundary electionWard) {
        this.electionWard = electionWard;
    }

    public CouncilDesignation getDesignation() {
        return designation;
    }

    public void setDesignation(CouncilDesignation designation) {
        this.designation = designation;
    }

    public CouncilQualification getQualification() {
        return qualification;
    }

    public void setQualification(CouncilQualification qualification) {
        this.qualification = qualification;
    }

    public CouncilCaste getCaste() {
        return caste;
    }

    public void setCaste(CouncilCaste caste) {
        this.caste = caste;
    }

    public CouncilParty getPartyAffiliation() {
        return partyAffiliation;
    }

    public void setPartyAffiliation(CouncilParty partyAffiliation) {
        this.partyAffiliation = partyAffiliation;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Date getElectionDate() {
        return electionDate;
    }

    public void setElectionDate(Date electionDate) {
        this.electionDate = electionDate;
    }

    public Date getOathDate() {
        return oathDate;
    }

    public void setOathDate(Date oathDate) {
        this.oathDate = oathDate;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResidentialAddress() {
        return residentialAddress;
    }

    public void setResidentialAddress(String residentialAddress) {
        this.residentialAddress = residentialAddress;
    }


    public CouncilMemberStatus getStatus() {
        return status;
    }

    public void setStatus(CouncilMemberStatus status) {
        this.status = status;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    
}
