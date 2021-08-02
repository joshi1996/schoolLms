package com.schoollms.View;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.schoollms.GsonModel.CourseContentList;
import com.schoollms.R;
import com.schoollms.utility.AlertClass;
import com.schoollms.utility.SharePrefs;
import com.schoollms.utility.Utils;

import java.util.concurrent.TimeUnit;

public class YouTubeVideoPlayer extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener, View.OnClickListener{
    YouTubePlayerView player;
    String videoURL = "http://blueappsoftware.in/layout_design_android_blog.mp4";
    CourseContentList mCourseContentList;
    YouTubePlayer.OnInitializedListener onInitializedListener;
    private Handler mHandler;
    int videolength;  //in 30 second
    int buystatus;

    private View mPlayButtonLayout;
    private TextView mPlayTimeTextView;

    private SeekBar mSeekBar;

    YouTubePlayer mYouTubePlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow ().setFlags (WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.youtubeactivity);

        player = findViewById(R.id.youtube_player);

        mPlayButtonLayout = findViewById(R.id.video_control);
        findViewById(R.id.play_video).setOnClickListener(this);
        findViewById(R.id.pause_video).setOnClickListener(this);

        mPlayTimeTextView = (TextView) findViewById(R.id.play_time);
        mSeekBar = (SeekBar) findViewById(R.id.video_seekbar);
        mSeekBar.setOnSeekBarChangeListener(mVideoSeekBarChangeListener);


        if(getIntent().getExtras() !=null){
          Bundle b=getIntent().getExtras();
            mCourseContentList= b.getParcelable("data");
            videoURL=b.getString("videourl");
            buystatus=b.getInt("buystatus");
        }
        initializePlayer();

    }

    private void initializePlayer() {
        /*if(mCourseContentList.getContentType().equalsIgnoreCase("video") && mCourseContentList.getContentPlanType().equalsIgnoreCase("free") && mCourseContentList.getVideoType().equalsIgnoreCase("upload")){
            videolength=10;
        }else{
            videolength=-1;
        }*/

        onInitializedListener = new YouTubePlayer.OnInitializedListener(){

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                mYouTubePlayer=youTubePlayer;
                videoURL=videoURL.replace("https://www.youtube.com/watch?v=","");
                youTubePlayer.loadVideo(videoURL.trim());
                youTubePlayer.seekToMillis(0);
                youTubePlayer.play();
                youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);

                mHandler = new Handler();
                mHandler.post(updateProgressAction);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };

        player.initialize("AIzaSyBQYR9rDQcMjPOaWdeZEIa2E0_RtKOHerA",onInitializedListener);



    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        View decorView = getWindow().getDecorView();
// Hide both the navigation bar and the status bar.
// SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
// a general rule, you should design your app to hide the status bar whenever you
// hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        /*if (mPlayButtonLayout.isShown())
        {
            mPlayButtonLayout.setVisibility(View.GONE);
        }
        else
        {
            mPlayButtonLayout.setVisibility(View.VISIBLE);
        }*/
        return super.dispatchTouchEvent(ev);
    }

    private final Runnable updateProgressAction = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    private void updateProgress() {
        if(mYouTubePlayer!=null &&mYouTubePlayer.isPlaying()) {
            if (videolength == (mYouTubePlayer.getCurrentTimeMillis() / 1000)) {

                mYouTubePlayer.pause();
                AlertClass.BaseAlert_done(YouTubeVideoPlayer.this, SharePrefs.getSetting(YouTubeVideoPlayer.this).getOrganizationName(), "please buy this course to watch complete video", getString(R.string.done), getString(R.string.no), false, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        mHandler.removeCallbacks(updateProgressAction);
                        Utils.hideKeyboard(YouTubeVideoPlayer.this);
                        YouTubeVideoPlayer.this.finish();

                    }
                }, null);
            } else {

                long delayMs = TimeUnit.SECONDS.toMillis(1);
                mHandler.postDelayed(updateProgressAction, delayMs);
            }
        }
    }

    YouTubePlayer.PlaybackEventListener mPlaybackEventListener = new YouTubePlayer.PlaybackEventListener() {
        @Override
        public void onBuffering(boolean arg0) {
        }

        @Override
        public void onPaused() {
            mHandler.removeCallbacks(updateProgressAction);
        }

        @Override
        public void onPlaying() {
            mHandler.postDelayed(updateProgressAction, 100);
            displayCurrentTime();
        }

        @Override
        public void onSeekTo(int arg0) {
            mHandler.postDelayed(updateProgressAction, 100);
        }

        @Override
        public void onStopped() {
            mHandler.removeCallbacks(updateProgressAction);
        }
    };

    YouTubePlayer.PlayerStateChangeListener mPlayerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {
        @Override
        public void onAdStarted() {
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason arg0) {
        }

        @Override
        public void onLoaded(String arg0) {
        }

        @Override
        public void onLoading() {
        }

        @Override
        public void onVideoEnded() {
        }

        @Override
        public void onVideoStarted() {
            displayCurrentTime();
        }
    };

    SeekBar.OnSeekBarChangeListener mVideoSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            long lengthPlayed = (mYouTubePlayer.getDurationMillis() * progress) / 100;
            mYouTubePlayer.seekToMillis((int) lengthPlayed);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_video:
                if (null != mYouTubePlayer && !mYouTubePlayer.isPlaying())
                    mYouTubePlayer.play();
                break;
            case R.id.pause_video:
                if (null != mYouTubePlayer && mYouTubePlayer.isPlaying())
                    mYouTubePlayer.pause();
                break;
        }
    }

    private void displayCurrentTime() {
        if (null == mYouTubePlayer) return;
        String formattedTime = formatTime(mYouTubePlayer.getDurationMillis() - mYouTubePlayer.getCurrentTimeMillis());
        mPlayTimeTextView.setText(formattedTime);
    }

    @SuppressLint("DefaultLocale")
    private String formatTime(int millis) {
        int seconds = millis / 1000;
        int minutes = seconds / 60;
        int hours = minutes / 60;

        return (hours == 0 ? "--:" : hours + ":") + String.format("%02d:%02d", minutes % 60, seconds % 60);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(mYouTubePlayer!=null && mYouTubePlayer.isPlaying()){
            mYouTubePlayer.pause();}

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if (null == youTubePlayer) return;
        mYouTubePlayer = youTubePlayer;

        displayCurrentTime();

        // Start buffering
        /*if (!b) {
            player.cueVideo(VIDEO_ID);
        }

        player.setPlayerStyle(PlayerStyle.CHROMELESS);*/
        mPlayButtonLayout.setVisibility(View.VISIBLE);

        // Add listeners to YouTubePlayer instance
        mYouTubePlayer.setPlayerStateChangeListener(mPlayerStateChangeListener);
        mYouTubePlayer.setPlaybackEventListener(mPlaybackEventListener);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this, "Failed to initialize.", Toast.LENGTH_LONG).show();
    }
}