package com.schoollms.WebService;

import com.schoollms.GsonModel.Advertisment.AdvertismentModel;
import com.schoollms.GsonModel.Assessment.AssessmentModel;
import com.schoollms.GsonModel.BasicModel;
import com.schoollms.GsonModel.CountryModels.CountryModel;
import com.schoollms.GsonModel.CountryModels.stateModel.StateModel;
import com.schoollms.GsonModel.CourelistModel;
import com.schoollms.GsonModel.EarningModels.EarningModel;
import com.schoollms.GsonModel.ForgetPassword.ForgotPassword;
import com.schoollms.GsonModel.InviteModel.InviteModel;
import com.schoollms.GsonModel.Job1.Job1Model;
import com.schoollms.GsonModel.Job2.Job2Model;
import com.schoollms.GsonModel.JobDetailModels.JobDetailModel;
import com.schoollms.GsonModel.LiveVideo.LiveVideoModel;
import com.schoollms.GsonModel.LmsLogindata;

import com.schoollms.GsonModel.Notification.NotificationResponse;
import com.schoollms.GsonModel.ProflieData.ProfileData;
import com.schoollms.GsonModel.ResetPasswordModel.ResetPasswordDataModel;
import com.schoollms.GsonModel.ReviewsModel.ReviewModel;
import com.schoollms.GsonModel.ShowAssess.ShowAssessModel;
import com.schoollms.GsonModel.SubmitReviews.SubmitReviewModel;
import com.schoollms.GsonModel.TopicModel.LessionModel;
import com.schoollms.GsonModel.UserCourse.UsercourseModel;
import com.schoollms.GsonModel.UsercourseAdd.AddUsercourseModel;
import com.schoollms.GsonModel.citydata.CityModel;
import com.schoollms.GsonModel.coursedetail.CouresubModel;
import com.schoollms.GsonModel.lms_OtpVerify;
import com.schoollms.GsonModel.lms_signup;
import com.schoollms.GsonModel.settingdata.SettingModel;
import com.schoollms.GsonModel.uploadAssessment.uploadModel;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface RestApi {
    //public String BaseUrl = "http://ec2-13-127-215-96.ap-south-1.compute.amazonaws.com:3000/v1/";
    //public String BaseUrl = "http://34.221.173.57:5000/v1/";
    public String BaseUrl ="https://schoollms.adiyogitechnology.com/v1/";

    //setting
    @GET("settings")
    Observable<SettingModel> Getsettings();

    //signUp
    @POST("register")
    @FormUrlEncoded
    Observable<lms_signup> getSignup(@FieldMap Map<String, String> body);

    //reviews
    @POST("review")
    @FormUrlEncoded
    Observable<SubmitReviewModel> getReviews(@FieldMap Map<String, String> body);

    //otpVerify
    @POST("verify")
    @FormUrlEncoded
    Observable<lms_OtpVerify> verifyOtp(@FieldMap Map<String, String> body);

    //login
    @POST("login")
    @FormUrlEncoded
    Observable<LmsLogindata> getLogin(@FieldMap Map<String, String> body);

    //resendOtp
    @POST("resend-otp")
    @FormUrlEncoded
    Observable<ForgotPassword> resendOtp(@FieldMap Map<String, String> body);

    //forgetPassword
    @POST("password/forgot")
    @FormUrlEncoded
    Observable<ForgotPassword> forgotPassword(@FieldMap Map<String, String> body);

    //resetPassword
    @POST("password/reset")
    @FormUrlEncoded
    Observable<ResetPasswordDataModel> resetPassword(@FieldMap Map<String, String> body);

    //changePassword
    @POST("password/change")
    @FormUrlEncoded
    Observable<BasicModel> changePassword(@FieldMap Map<String, String> body);

    //getAllcourse
    @GET("courses")
    Observable<CourelistModel> courselist(@QueryMap Map<String, String> body);

    //userCourseList
    @GET("user_courses")
    Observable<UsercourseModel> UsercourseList(@QueryMap Map<String, String> body);

    //addCourses
    @POST("user_courses")
    @FormUrlEncoded
    Observable<AddUsercourseModel>AddUsercourse(@FieldMap Map<String, String> body);

    //courseDetails
    @GET("course-details/{id}")
    Observable<CouresubModel> coursesublist(@Path("id") String id);

    //advertisement
    @GET("advertisement")
    Observable<AdvertismentModel> advertisement();

    //subjectDetails
    /*@GET("subject-detail/{id}/{sortDate}")
    Observable<LessionModel> Topiclist(@Path("id") String id,
                                       @Path("sortDate") String date);*/

    @GET("subject-detail/{id}")
    Observable<LessionModel> Topiclist(@Path("id") String id);

    //profile
    @GET("profile")
    Observable<ProfileData> getProfile(@QueryMap Map<String,String> body);

    //updateProfile
    @Multipart
    @POST("profile/update")
    Observable<ProfileData> updateProfile(@PartMap Map<String, RequestBody> body,@Part MultipartBody.Part profilePhoto);

    //countries
    @GET("countries")
    Observable<CountryModel> getCountry();

    //state
    @GET("countries/{countryid}/states?status")
    Observable<StateModel> getState(@Path("countryid") String id,@Query("status") String status);

    //city
    @GET("states/{stateid}/cities?status")
    Observable<CityModel> getCity(@Path("stateid") String id,@Query("status") String status);

    //invite
    @GET("users-by-code/{usercode}")
    Observable<InviteModel> invitlist(@Path("usercode") String usercode, @Query("contentPlanType") String contentPlanType);

    //earning
    @GET("user-earning/{id}")
    Observable<EarningModel> earningData(@Path("id") String id);

    //job
    @GET("courses/{id}")
    Observable<Job1Model> job1(@Path("id") String id);

    //job
    @GET("courses/{id}")
    Observable<Job2Model> job2(@Path("id") String id);

    //jobDetail
    @GET("course-details/{id}")
    Observable<JobDetailModel> jobSublist(@Path("id") String id);

    //getReviews
    @GET("review")
    Observable<ReviewModel> reviewShow(@Query("status") String status,
                                       @Query("courseId") String course);

    //zoom_youtubeList
    @GET("course-content")
    Observable<LiveVideoModel> liveVideo(@Query("status") String status,
                                          @Query("courseType") String course,
                                          @Query("courseId") String id);


    //zoom_youtubeList
    @GET("course-content")
    Observable<LiveVideoModel> liveVideo1(@Query("status") String status,
                                         @Query("courseType") String course,
                                         @Query("courseId") String id);


    //assessmentList
    @GET("assesment")
    Observable<AssessmentModel> assessment(@Query("status") String status,
                                           @Query("course") String course,
                                           @Query("subject") String subject);

    //assessmentList
    /*@GET("assesment")
    Observable<AssessmentModel> assessment1(@Query("status") String status,
                                           @Query("course") String course,
                                           @Query("subject") String subject);*/

    /*@GET("assesment/{id}")
    Observable<ShowAssessModel> showAssessment(@Path("id") String user);*/

    //submitAssessment
    @Multipart
    @POST("assesment/submit")
    Observable<uploadModel> uploadAssessment(@PartMap Map<String, RequestBody> body,@Part MultipartBody.Part photo);

    //showStudentSubmitAssessment
    @GET("student_assesment")
    Observable<ShowAssessModel> showAssessment(@Query("userId") String user,
                                               @Query("assessmentId") String assessmentId);

    @GET("user_notification/{id}")
    Observable<NotificationResponse> notification(@Path("id")String id);
}
