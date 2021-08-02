package com.schoollms.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.schoollms.Adapter.ShowRemarkInfoAdapter;
import com.schoollms.GsonModel.ShowAssess.DatumAssessShow;
import com.schoollms.R;
import com.schoollms.databinding.ActivityAssessmentListShowBinding;
import com.schoollms.interfaces.OnclickListener;
import com.schoollms.utility.GridSpacingItemDecoration;
import com.schoollms.utility.ThemeClass;
import com.schoollms.utility.Utils;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AssessmentListShowActivity extends AppCompatActivity implements OnclickListener {

    private static final String TAG = AssessmentListShowActivity.class.getSimpleName();
    private ActivityAssessmentListShowBinding activityBinding;

    List<DatumAssessShow> mlist;
    ShowRemarkInfoAdapter adapter;
    String list_id;
    String topic,subject,pdf,photo,submission,detail,contentType,text,id,courseName,courseId,subjectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.changeStatuscolor(AssessmentListShowActivity.this);

        courseId = getIntent().getStringExtra("class_id");
        subjectId = getIntent().getStringExtra("subjectId");

        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_assessment_list_show);

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
        courseName = getIntent().getStringExtra("Assessment_Class");
        mlist = (List<DatumAssessShow>) getIntent().getSerializableExtra("Assessment_list");
        //list_id = getIntent().getStringExtra("Assessment_list_id");

        getList();

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

        activityBinding.rvItem.setHasFixedSize(true);
        activityBinding.rvItem.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.margin_15);
        activityBinding.rvItem.addItemDecoration(new GridSpacingItemDecoration(1,spacingInPixels,false));

        activityBinding.tvTitle.setText(courseName);
        //activityBinding.assessSubjectName.setText(subject);
        activityBinding.tvSubjectname.setText(subject);

        activityBinding.tvSubjectname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activityBinding.showLayout.getVisibility() == View.VISIBLE) {
                    activityBinding.tvSubjectname.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                    activityBinding.showLayout.setVisibility(View.GONE);
                } else {
                    activityBinding.tvSubjectname.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
                    activityBinding.showLayout.setVisibility(View.VISIBLE);
                    activityBinding.showTopic.setText(topic);

                }
            }
        });

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
                        Toast.makeText(AssessmentListShowActivity.this, "No Image Found", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else
            {
                Picasso.get().load(photo).into(activityBinding.showImage);
                activityBinding.showImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog = new Dialog(AssessmentListShowActivity.this);
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
                        Toast.makeText(AssessmentListShowActivity.this, "No PDF Found", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "No text Found", Toast.LENGTH_SHORT).show();
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

    }

    public void getList()
    {
        if(mlist!=null)
        {
            //adapter= new ShowRemarkInfoAdapter(AssessmentListShowActivity.this,mlist,AssessmentListShowActivity.this);
            //activityBinding.rvItem.setAdapter(adapter);
        }
    }

    @Override
    public void OnItemclick(int pos) {

    }

    @Override
    public void OnDesItemclick(int pos) {

    }
}