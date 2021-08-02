package com.schoollms.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.schoollms.GsonModel.LiveVideo.LiveVideoDatum;
import com.schoollms.R;
import com.schoollms.databinding.ActivityLiveClassBinding;
import com.schoollms.databinding.ActivityLiveSessionVideoDetailBinding;
import com.schoollms.utility.ThemeClass;
import com.schoollms.utility.Utils;

public class LiveSessionVideoDetail extends AppCompatActivity {

    private static final String TAG = TermsAndCondition.class.getSimpleName();
    private ActivityLiveSessionVideoDetailBinding activityBinding;

    LiveVideoDatum liveVideoDatum;
    String description,title,videoLiveUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.changeStatuscolor(LiveSessionVideoDetail.this);

        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_live_session_video_detail);

        ThemeClass.changeHeaderColor(activityBinding.llHeader, getApplicationContext());
        TextView tv_header = (TextView) activityBinding.llHeader.findViewById(R.id.tv_header);
        tv_header.setText("Live Class Detail");
        LinearLayout ll_back = (LinearLayout) activityBinding.llHeader.findViewById(R.id.ll_back);

        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        title = getIntent().getStringExtra("videoTitle");
        description = getIntent().getStringExtra("videoDetail");
        videoLiveUrl = getIntent().getStringExtra("videoUrlLive");

        activityBinding.tvTitleLive.setText(title);
        activityBinding.tvDesciptionLive.setText(description);

        activityBinding.ivPlayLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LiveSessionVideoDetail.this,liveVideoSession.class);
                intent.putExtra("playVideo",videoLiveUrl);
                startActivity(intent);
            }
        });
    }
}