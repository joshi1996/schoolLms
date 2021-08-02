package com.schoollms.GsonModel.Assessment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AssessDetail implements Parcelable {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("assesmentId")
    @Expose
    private String assesmentId;
    @SerializedName("contentType")
    @Expose
    private String contentType;
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("pdf")
    @Expose
    private Object pdf;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("late_submission")
    @Expose
    private Integer lateSubmission;
    @SerializedName("upload_date")
    @Expose
    private String uploadDate;

    protected AssessDetail(Parcel in) {
        id = in.readString();
        userId = in.readString();
        assesmentId = in.readString();
        contentType = in.readString();
        photo = in.readString();
        text = in.readString();
        if (in.readByte() == 0) {
            lateSubmission = null;
        } else {
            lateSubmission = in.readInt();
        }
        uploadDate = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(userId);
        dest.writeString(assesmentId);
        dest.writeString(contentType);
        dest.writeString(photo);
        dest.writeString(text);
        if (lateSubmission == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(lateSubmission);
        }
        dest.writeString(uploadDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AssessDetail> CREATOR = new Creator<AssessDetail>() {
        @Override
        public AssessDetail createFromParcel(Parcel in) {
            return new AssessDetail(in);
        }

        @Override
        public AssessDetail[] newArray(int size) {
            return new AssessDetail[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAssesmentId() {
        return assesmentId;
    }

    public void setAssesmentId(String assesmentId) {
        this.assesmentId = assesmentId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Object getPdf() {
        return pdf;
    }

    public void setPdf(Object pdf) {
        this.pdf = pdf;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getLateSubmission() {
        return lateSubmission;
    }

    public void setLateSubmission(Integer lateSubmission) {
        this.lateSubmission = lateSubmission;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

}
