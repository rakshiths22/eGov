package org.egov.pgr.rest.web.model;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Service implements Serializable {

    private static final long serialVersionUID = 4218331701543171474L;

    @SerializedName("service_code")
    @Expose
    private String code;

    @SerializedName("service_name")
    @Expose
    private String name;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("metadata")
    @Expose
    private Boolean metadata;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("keywords")
    @Expose
    private String keywords;

    @SerializedName("group")
    @Expose
    private String category;

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
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

    public Boolean getMetadata() {
        return metadata;
    }

    public void setMetadata(final Boolean metadata) {
        this.metadata = metadata;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(final String keywords) {
        this.keywords = keywords;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(final String category) {
        this.category = category;
    }
}
