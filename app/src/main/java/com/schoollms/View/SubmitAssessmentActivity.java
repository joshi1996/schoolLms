package com.schoollms.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.schoollms.GsonModel.Assessment.AssessDetail;
import com.schoollms.GsonModel.Assessment.AssessRemarkInfo;
import com.schoollms.GsonModel.ShowAssess.DatumAssessShow;
import com.schoollms.GsonModel.ShowAssess.ShowAssessModel;
import com.schoollms.R;
import com.schoollms.WebService.RestApi;
import com.schoollms.WebService.RestClient;
import com.schoollms.databinding.ActivitySubmitAssessmentBinding;
import com.schoollms.utility.Connectivity;
import com.schoollms.utility.ProgressDialog;
import com.schoollms.utility.SharePrefs;
import com.schoollms.utility.ThemeClass;
import com.schoollms.utility.Utils;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SubmitAssessmentActivity extends AppCompatActivity {

    private static final String TAG = SubmitAssessmentActivity.class.getSimpleName();
    private ActivitySubmitAssessmentBinding activityBinding;

    List<DatumAssessShow> showRemark;
    List<AssessDetail> mlist;
    List<AssessRemarkInfo> mRemark;
    String ass_id;
    String topic,subject,pdf,photo,submission,detail,contentType,text,id,courseName,courseId,subjectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.changeStatuscolor(SubmitAssessmentActivity.this);

        courseId = getIntent().getStringExtra("class_id");
        subjectId = getIntent().getStringExtra("subjectId");

        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_submit_assessment);

        ThemeClass.changeHeaderColor(activityBinding.llHeader, getApplicationContext());
        TextView tv_header = (TextView) activityBinding.llHeader.findViewById(R.id.tv_header);
        tv_header.setText("Show Assessment");
        LinearLayout ll_back = (LinearLayout) activityBinding.llHeader.findViewById(R.id.ll_back);

        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        topic = getIntent().getStringExtra("Assessment_Topic");
        subject = getIntent().getStringExtra("Assessment_Subject");
        text = getIntent().getStringExtra("Assessment_Text");
        submission = getIntent().getStringExtra("Assessment_Submission");
        detail = getIntent().getStringExtra("Assessment_Detail");
        pdf = getIntent().getStringExtra("Assessment_Pdf");
        photo = getIntent().getStringExtra("Assessment_Photo");
        contentType = getIntent().getStringExtra("Assessment_Content");
        id = getIntent().getStringExtra("Assessment_Id");
        //ass_id = getIntent().getStringExtra("Assessment_list_id");
        courseName = getIntent().getStringExtra("Assessment_Class");
        mlist = (List<AssessDetail>) getIntent().getSerializableExtra("Assessment_list");
        mRemark = (List<AssessRemarkInfo>) getIntent().getSerializableExtra("Assessment_list_remark");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date convertedDate = new Date();

        try {
            convertedDate = dateFormat.parse(submission);
            SimpleDateFormat sdfmonth = new SimpleDateFormat("dd-MM-yyyy");
            String date_text = sdfmonth.format(convertedDate);

            activityBinding.showSubmitDate.setText("" + date_text);

        } catch (ParseException e) {
            e.printStackTrace();
            try {
                convertedDate = dateFormat.parse(submission);
            } catch (ParseException parseException) {
                parseException.printStackTrace();
            }
            SimpleDateFormat sdfmonth = new SimpleDateFormat("dd-MM-yyyy");
            String date_text = sdfmonth.format(convertedDate);
            activityBinding.showSubmitDate.setText("" + submission);
        }

        activityBinding.tvTitle.setText(courseName);
        //activityBinding.assessSubjectName.setText(subject);
        activityBinding.tvSubjectname.setText(subject);
        activityBinding.tvClassname.setText(topic);

        if (pdf.equalsIgnoreCase(""))
        {
            activityBinding.showPdf.setVisibility(View.GONE);
        }
        else
        {
            activityBinding.showPdf.setVisibility(View.VISIBLE);
        }

        if (photo.equalsIgnoreCase(""))
        {
            activityBinding.showImage.setVisibility(View.GONE);
        }
        else
        {
            activityBinding.showImage.setVisibility(View.VISIBLE);
        }

        activityBinding.tvDescriptionTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activityBinding.wvDescription.getVisibility() == View.VISIBLE) {
                    activityBinding.tvDescriptionTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                    activityBinding.wvDescription.setVisibility(View.GONE);
                } else {
                    activityBinding.tvDescriptionTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);

                    activityBinding.wvDescription.setVisibility(View.VISIBLE);
                    activityBinding.wvDescription.loadData("<p align='justify'>" + detail + "</p>", "text/html; charset=UTF-8", null);

                }
            }
        });

        try {
            if (photo.equals(""))
            {
                activityBinding.showImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(SubmitAssessmentActivity.this, "No Image Found", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else
            {
                Picasso.get().load(photo).into(activityBinding.showImage);
                activityBinding.showImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog = new Dialog(SubmitAssessmentActivity.this);
                        dialog.setContentView(R.layout.choose_assessment_type);
                        ImageView imageView = dialog.findViewById(R.id.show_image_assess);

                            Picasso.get().load(photo).into(imageView);

                        dialog.show();
                    }
                });
            }

            if (pdf.equals(""))
            {
                activityBinding.showPdf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(SubmitAssessmentActivity.this, "No PDF Found", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else
            {
                activityBinding.showPdf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pdf));
                        startActivity(browserIntent);
                    }
                });
            }

            if (text.equals(""))
            {
                activityBinding.wvTextDescription.setVisibility(View.GONE);
                //Toast.makeText(this, "No text Found", Toast.LENGTH_SHORT).show();
            }

            else
            {
                activityBinding.wvTextDescription.setVisibility(View.VISIBLE);
                activityBinding.wvTextDescription.loadData("<p align='justify'>" + text + "</p>", "text/html; charset=UTF-8", null);
            }
        }

        catch (NullPointerException e)
        {
            //
        }

        activityBinding.submitAssess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SubmitAssessmentActivity.this,UploadAssessmentActivity.class);
                intent.putExtra("content",contentType);
                intent.putExtra("submission_date",submission);
                intent.putExtra("assessment_id",id);
                intent.putExtra("user_id", SharePrefs.getUserdetail(SubmitAssessmentActivity.this).getUser().getId());
                startActivity(intent);
            }
        });

        if (mlist.isEmpty())
        {
            activityBinding.studentAssess.setVisibility(View.GONE);
            activityBinding.submitAssess.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SubmitAssessmentActivity.this,UploadAssessmentActivity.class);
                    intent.putExtra("content",contentType);
                    intent.putExtra("submission_date",submission);
                    intent.putExtra("assessment_id",id);
                    intent.putExtra("user_id", SharePrefs.getUserdetail(SubmitAssessmentActivity.this).getUser().getId());
                    startActivity(intent);
                }
            });
        }

        else
        {
            activityBinding.submitAssess.setVisibility(View.GONE);
            activityBinding.studentAssess.setVisibility(View.VISIBLE);

            if (Connectivity.isConnected(SubmitAssessmentActivity.this)) {
                callJob1(SubmitAssessmentActivity.this);
            }

        }

    }
    private void callJob1(Context context) {

        ProgressDialog.showDialog(context);
        RestApi mRestApi = RestClient.getClient(context).create(RestApi.class);

        String user = SharePrefs.getUserdetail(SubmitAssessmentActivity.this).getUser().getId();
        String ass_id = mlist.get(0).getId();
        Observable<ShowAssessModel> mdata = mRestApi.showAssessment(user,ass_id);
        mdata.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ShowAssessModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(ShowAssessModel value) {
                        ProgressDialog.hideDialog();

                        if (value != null && value.getStatus() == 200) {
                            showRemark = value.getData();

                            if (showRemark!=null)
                            {
                                try {
                                    if (showRemark.get(0).getPhoto().equals(""))
                                    {
                                        activityBinding.showImage1.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Toast.makeText(SubmitAssessmentActivity.this, "No Image Found", Toast.LENGTH_SHORT).show();
                                                //ProgressDialog.hideDialog();
                                            }
                                        });
                                    }
                                    else
                                    {
                                        Picasso.get().load(showRemark.get(0).getPhoto()).into(activityBinding.showImage1);
                                        activityBinding.showImage1.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                final Dialog dialog = new Dialog(SubmitAssessmentActivity.this);
                                                dialog.setContentView(R.layout.choose_assessment_type);
                                                ImageView imageView = dialog.findViewById(R.id.show_image_assess);

                                                Picasso.get().load(showRemark.get(0).getPhoto()).into(imageView);

                                                dialog.show();
                                            }
                                        });
                                    }

                                    if (showRemark.get(0).getPdf().equals(""))
                                    {
                                        activityBinding.showPdf1.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Toast.makeText(SubmitAssessmentActivity.this, "No PDF Found", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    else
                                    {
                                        activityBinding.showPdf1.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(showRemark.get(0).getPdf()));
                                                startActivity(browserIntent);
                                            }
                                        });
                                    }

                                    if (showRemark.get(0).getText().equals(""))
                                    {
                                        activityBinding.wvTextDescription.setVisibility(View.GONE);
                                    }

                                    else
                                    {
                                        activityBinding.wvTextDescription.setVisibility(View.VISIBLE);
                                        activityBinding.wvTextDescription.loadData("<p align='justify'>" + showRemark.get(0).getText() + "</p>", "text/html; charset=UTF-8", null);
                                    }

                                    try {

                                        if (!(showRemark.isEmpty()))
                                        {
                                            activityBinding.assessmentLayout.setVisibility(View.VISIBLE);
                                            for (int i = 0; i< showRemark.size(); i++)
                                            {
                                                for (int j = 0; j< showRemark.get(i).getRemarksInfo().size(); j++) {
                                                    activityBinding.assessmentTopic.setText(showRemark.get(i).getRemarksInfo().get(j).getDescription());
                                                }
                                            }
                                        }
                                        else
                                        {
                                            activityBinding.assessmentLayout.setVisibility(View.GONE);
                                        }

                                /*if (showRemark.get(0).getRemarksInfo().get(0).getDescription() != null)
                                {
                                    activityBinding.assessmentTopic.setText(showRemark.get(0).getRemarksInfo().get(0).getDescription());
                                }
                                else
                                {
                                    activityBinding.assessmentTopic.setText("No Data Found");
                                }*/
                                    }
                                    catch (IndexOutOfBoundsException e)
                                    {
                                        //write something
                                    }

                                    Integer late = showRemark.get(0).getLateSubmission();

                                    if (late == 0)
                                    {
                                        activityBinding.lateSubmission.setText("ON TIME");
                                    }
                                    else
                                    {
                                        activityBinding.lateSubmission.setText("LATE");
                                    }
                                }
                                catch (IndexOutOfBoundsException e)
                                {

                                }
                            }
                            else
                            {
                                activityBinding.assessmentLayout.setVisibility(View.GONE);
                            }

                        }
                        else if (value != null)
                        {
                            //mDataListener.onError(value.getMessage());
                            Toasty.error(SubmitAssessmentActivity.this, value.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        // display an error message
                        Log.e(TAG, "onError: ");
                        ProgressDialog.hideDialog();
                        //   mDataListener.onError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        ProgressDialog.hideDialog();
                        Log.e(TAG, "onComplete: All Done!");
                    }
                });
    }
}