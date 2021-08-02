package com.schoollms.GsonModel.Assessment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AssessUserInfo {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("fullName")
    @Expose
    private String fullName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
