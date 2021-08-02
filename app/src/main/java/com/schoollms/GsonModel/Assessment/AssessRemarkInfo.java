package com.schoollms.GsonModel.Assessment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AssessRemarkInfo implements Parcelable {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("teacherId")
    @Expose
    private Object teacherId;
    @SerializedName("assesmentId")
    @Expose
    private String assesmentId;
    @SerializedName("submitAssesment")
    @Expose
    private String submitAssesment;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("__v")
    @Expose
    private Integer v;

    protected AssessRemarkInfo(Parcel in) {
        id = in.readString();
        userId = in.readString();
        assesmentId = in.readString();
        submitAssesment = in.readString();
        if (in.readByte() == 0) {
            status = null;
        } else {
            status = in.readInt();
        }
        description = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
        if (in.readByte() == 0) {
            v = null;
        } else {
            v = in.readInt();
        }
    }

    public static final Creator<AssessRemarkInfo> CREATOR = new Creator<AssessRemarkInfo>() {
        @Override
        public AssessRemarkInfo createFromParcel(Parcel in) {
            return new AssessRemarkInfo(in);
        }

        @Override
        public AssessRemarkInfo[] newArray(int size) {
            return new AssessRemarkInfo[size];
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

    public Object getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Object teacherId) {
        this.teacherId = teacherId;
    }

    public String getAssesmentId() {
        return assesmentId;
    }

    public void setAssesmentId(String assesmentId) {
        this.assesmentId = assesmentId;
    }

    public String getSubmitAssesment() {
        return submitAssesment;
    }

    public void setSubmitAssesment(String submitAssesment) {
        this.submitAssesment = submitAssesment;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(userId);
        dest.writeString(assesmentId);
        dest.writeString(submitAssesment);
        if (status == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(status);
        }
        dest.writeString(description);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        if (v == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(v);
        }
    }
}
