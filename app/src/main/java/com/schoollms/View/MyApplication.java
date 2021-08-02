package com.schoollms.View;

import android.app.Application;

import com.schoollms.R;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;


@ReportsCrashes(mailTo = "himanshupareek2018@gmail.com", mode = ReportingInteractionMode.TOAST, resToastText = R.string.crash_toast_text)
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //ACRA.init(this);
    }
}