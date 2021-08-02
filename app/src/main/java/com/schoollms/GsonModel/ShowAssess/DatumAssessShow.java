package com.schoollms.GsonModel.ShowAssess;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DatumAssessShow implements Parcelable {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("contentType")
    @Expose
    private String contentType;
    @SerializedName("assesmentId")
    @Expose
    private String assesmentId;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("pdf")
    @Expose
    private String pdf;
    @SerializedName("late_submission")
    @Expose
    private Integer lateSubmission;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("userInfo")
    @Expose
    private UserInfo userInfo;
    @SerializedName("assessInfo")
    @Expose
    private List<AssessInfo> assessInfo = null;
    @SerializedName("remarksInfo")
    @Expose
    private List<RemarksInfo> remarksInfo = null;

    protected DatumAssessShow(Parcel in) {
        id = in.readString();
        userId = in.readString();
        contentType = in.readString();
        assesmentId = in.readString();
        text = in.readString();
        photo = in.readString();
        pdf = in.readString();
        if (in.readByte() == 0) {
            lateSubmission = null;
        } else {
            lateSubmission = in.readInt();
        }
        date = in.readString();
        remarksInfo = in.createTypedArrayList(RemarksInfo.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(userId);
        dest.writeString(contentType);
        dest.writeString(assesmentId);
        dest.writeString(text);
        dest.writeString(photo);
        dest.writeString(pdf);
        if (lateSubmission == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(lateSubmission);
        }
        dest.writeString(date);
        dest.writeTypedList(remarksInfo);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DatumAssessShow> CREATOR = new Creator<DatumAssessShow>() {
        @Override
        public DatumAssessShow createFromParcel(Parcel in) {
            return new DatumAssessShow(in);
        }

        @Override
        public DatumAssessShow[] newArray(int size) {
            return new DatumAssessShow[size];
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

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getAssesmentId() {
        return assesmentId;
    }

    public void setAssesmentId(String assesmentId) {
        this.assesmentId = assesmentId;
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

    public Integer getLateSubmission() {
        return lateSubmission;
    }

    public void setLateSubmission(Integer lateSubmission) {
        this.lateSubmission = lateSubmission;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public List<AssessInfo> getAssessInfo() {
        return assessInfo;
    }

    public void setAssessInfo(List<AssessInfo> assessInfo) {
        this.assessInfo = assessInfo;
    }

    public List<RemarksInfo> getRemarksInfo() {
        return remarksInfo;
    }

    public void setRemarksInfo(List<RemarksInfo> remarksInfo) {
        this.remarksInfo = remarksInfo;
    }

}

