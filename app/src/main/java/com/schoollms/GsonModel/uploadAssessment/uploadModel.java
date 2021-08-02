package com.schoollms.GsonModel.uploadAssessment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class uploadModel
{
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private uploadDatum data;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public uploadDatum getData() {
        return data;
    }

    public void setData(uploadDatum data) {
        this.data = data;
    }

}
