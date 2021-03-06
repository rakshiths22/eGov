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

package org.egov.commons;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.egov.infra.persistence.entity.AbstractPersistable;
import org.egov.infra.persistence.validator.annotation.Unique;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "ACCOUNTDETAILTYPE")
@SequenceGenerator(name = Accountdetailtype.SEQ_ACCOUNTDETAILTYPE, sequenceName = Accountdetailtype.SEQ_ACCOUNTDETAILTYPE, allocationSize = 1)
@Unique(id = "id", tableName = "accountdetailtype", fields = { "name" }, columnName = { "name" }, enableDfltMsg = true)
@Audited
public class Accountdetailtype extends AbstractPersistable<Integer> implements java.io.Serializable {

    private static final long serialVersionUID = 3499589983886551123L;

    public static final String SEQ_ACCOUNTDETAILTYPE = "SEQ_ACCOUNTDETAILTYPE";

    @Id
    @GeneratedValue(generator = SEQ_ACCOUNTDETAILTYPE, strategy = GenerationType.SEQUENCE)
    @NotAudited
    private Integer id;

    @Column(nullable = false, unique = true)
    @Length(max = 50)
    @NotAudited
    private String name;

    @NotNull
    @Length(max = 50)
    @NotAudited
    private String description;

    @Length(max = 25)
    @NotAudited
    private String tablename;

    @Length(max = 25)
    @NotAudited
    private String columnname;

    @Column(nullable = false, unique = true)
    @Length(max = 50)
    @NotAudited
    private String attributename;

    @NotNull
    @NotAudited
    private BigDecimal nbroflevels = BigDecimal.ZERO;

    @NotAudited
    private Boolean isactive;

    @Column(name = "FULL_QUALIFIED_NAME")
    @Length(max = 250)
    @NotAudited
    private String fullQualifiedName;

    @NotAudited
    private Date created;

    @NotAudited
    private Date lastmodified;

    @NotAudited
    private Long modifiedby;

    public Accountdetailtype() {
        // For hibernate to work
    }

    public Accountdetailtype(final String name, final String description, final String attributename,
            final BigDecimal nbroflevels) {
        this.name = name;
        this.description = description;
        this.attributename = attributename;
        this.nbroflevels = nbroflevels;
    }

    public Accountdetailtype(final String name, final String description, final String tablename, final String columnname,
            final String attributename,
            final BigDecimal nbroflevels, final Boolean isactive) {
        this.name = name;
        this.description = description;
        this.tablename = tablename;
        this.columnname = columnname;
        this.attributename = attributename;
        this.nbroflevels = nbroflevels;
        this.isactive = isactive;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(final Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getTablename() {
        return tablename;
    }

    public void setTablename(final String tablename) {
        this.tablename = tablename;
    }

    public String getColumnname() {
        return columnname;
    }

    public void setColumnname(final String columnname) {
        this.columnname = columnname;
    }

    public String getAttributename() {
        return attributename;
    }

    public void setAttributename(final String attributename) {
        this.attributename = attributename;
    }

    public BigDecimal getNbroflevels() {
        return nbroflevels;
    }

    public void setNbroflevels(final BigDecimal nbroflevels) {
        this.nbroflevels = nbroflevels;
    }

    public Boolean getIsactive() {
        return isactive;
    }

    public void setIsactive(final Boolean isactive) {
        this.isactive = isactive;
    }

    public String getFullQualifiedName() {
        return fullQualifiedName;
    }

    public void setFullQualifiedName(final String fullQualifiedName) {
        this.fullQualifiedName = fullQualifiedName;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(final Date created) {
        this.created = created;
    }

    public Date getLastmodified() {
        return lastmodified;
    }

    public void setLastmodified(final Date lastmodified) {
        this.lastmodified = lastmodified;
    }

    public Long getModifiedby() {
        return modifiedby;
    }

    public void setModifiedby(final Long modifiedby) {
        this.modifiedby = modifiedby;
    }

}