package com.schoollms.GsonModel.Assessment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DatumAssess implements Parcelable {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("course")
    @Expose
    private String course;
    @SerializedName("subject")
    @Expose
    private String subject;
    @SerializedName("topic")
    @Expose
    private String topic;
    @SerializedName("teacher")
    @Expose
    private String teacher;
    @SerializedName("contentType")
    @Expose
    private String contentType;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("text")
    @Expose
    private String text;
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
    @SerializedName("submission")
    @Expose
    private String submission;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("assesInfo")
    @Expose
    private List<AssessDetail> assesInfo = null;
    @SerializedName("userInfo")
    @Expose
    private List<AssessUserInfo> userInfo = null;
    @SerializedName("remarksInfo")
    @Expose
    private List<AssessRemarkInfo> remarksInfo = null;

    protected DatumAssess(Parcel in) {
        id = in.readString();
        course = in.readString();
        subject = in.readString();
        topic = in.readString();
        teacher = in.readString();
        contentType = in.readString();
        title = in.readString();
        text = in.readString();
        photo = in.readString();
        pdf = in.readString();
        description = in.readString();
        courseName = in.readString();
        subjectName = in.readString();
        topicName = in.readString();
        if (in.readByte() == 0) {
            status = null;
        } else {
            status = in.readInt();
        }
        submission = in.readString();
        date = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(course);
        dest.writeString(subject);
        dest.writeString(topic);
        dest.writeString(teacher);
        dest.writeString(contentType);
        dest.writeString(title);
        dest.writeString(text);
        dest.writeString(photo);
        dest.writeString(pdf);
        dest.writeString(description);
        dest.writeString(courseName);
        dest.writeString(subjectName);
        dest.writeString(topicName);
        if (status == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(status);
        }
        dest.writeString(submission);
        dest.writeString(date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DatumAssess> CREATOR = new Creator<DatumAssess>() {
        @Override
        public DatumAssess createFromParcel(Parcel in) {
            return new DatumAssess(in);
        }

        @Override
        public DatumAssess[] newArray(int size) {
            return new DatumAssess[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
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

    public String getSubmission() {
        return submission;
    }

    public void setSubmission(String submission) {
        this.submission = submission;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<AssessDetail> getAssesInfo() {
        return assesInfo;
    }

    public void setAssesInfo(List<AssessDetail> assesInfo) {
        this.assesInfo = assesInfo;
    }

    public List<AssessUserInfo> getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(List<AssessUserInfo> userInfo) {
        this.userInfo = userInfo;
    }

    public List<AssessRemarkInfo> getRemarksInfo() {
        return remarksInfo;
    }

    public void setRemarksInfo(List<AssessRemarkInfo> remarksInfo) {
        this.remarksInfo = remarksInfo;
    }

}
