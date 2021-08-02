package com.schoollms.GsonModel.Job2;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Job2Model
{
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Job2data data;

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

    public Job2data getData() {
        return data;
    }

    public void setData(Job2data data) {
        this.data = data;
    }
}
