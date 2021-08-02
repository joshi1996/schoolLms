package com.schoollms.GsonModel.uploadAssessment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class uploadDatum
{
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("contentType")
    @Expose
    private String contentType;
    @SerializedName("text")
    @Expose
    private Object text;
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("pdf")
    @Expose
    private String pdf;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("courseName")
    @Expose
    private String courseName;
    @SerializedName("subjectName")
    @Expose
    private String subjectName;
    @SerializedName("topicName")
    @Expose
    private String topicName;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("date")
    @Expose
    private String date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Object getText() {
        return text;
    }

    public void setText(Object text) {
        this.text = text;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPdf() {
        return pdf;
    }

    public void setPdf(String pdf) {
        this.pdf = pdf;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
