package com.schoollms.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.schoollms.Adapter.LiveVideoAdapter;
import com.schoollms.GsonModel.LiveVideo.LiveVideoDatum;
import com.schoollms.GsonModel.LiveVideo.LiveVideoModel;
import com.schoollms.R;
import com.schoollms.WebService.RestApi;
import com.schoollms.WebService.RestClient;
import com.schoollms.databinding.ActivityLiveClassBinding;
import com.schoollms.databinding.ActivityZoomLiveClassBinding;
import com.schoollms.interfaces.OnclickListener;
import com.schoollms.utility.Connectivity;
import com.schoollms.utility.GridSpacingItemDecoration;
import com.schoollms.utility.ProgressDialog;
import com.schoollms.utility.ThemeClass;
import com.schoollms.utility.Utils;

import java.util.List;

import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ZoomLiveClass extends AppCompatActivity implements OnclickListener {

    private static final String TAG = ZoomLiveClass.class.getSimpleName();
    private ActivityZoomLiveClassBinding activityBinding;

    LiveVideoAdapter adapter;
    List<LiveVideoDatum> mlist;

    String classIdName,className,category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.changeStatuscolor(ZoomLiveClass.this);

        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_zoom_live_class);

        classIdName = getIntent().getStringExtra("paramZoom");
        className = getIntent().getStringExtra("class_id");
        category = getIntent().getStringExtra("inside");

        ThemeClass.changeHeaderColor(activityBinding.llHeader, getApplicationContext());
        TextView tv_header = (TextView) activityBinding.llHeader.findViewById(R.id.tv_header);
        tv_header.setText("Zoom Live Classes");
        LinearLayout ll_back = (LinearLayout) activityBinding.llHeader.findViewById(R.id.ll_back);

        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        activityBinding.rvItemZoom.setHasFixedSize(true);
        activityBinding.rvItemZoom.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.margin_15);
        activityBinding.rvItemZoom.addItemDecoration(new GridSpacingItemDecoration(1,spacingInPixels,false));

        if (Connectivity.isConnected(ZoomLiveClass.this)) {

            if (category.equalsIgnoreCase("inside")|| category.equalsIgnoreCase("new"))
            {
                callLiveClassApi();
            }
            else
            {
                callLiveClassApiOut();
            }

        }
        else {
            //show net not connected error
            Toasty.warning(ZoomLiveClass.this, getString(R.string.net_disconnected), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        ZoomLiveClass.this.finish();
    }

    private void callLiveClassApi() {

        String classId = classIdName;

        ProgressDialog.showDialog(ZoomLiveClass.this);
        RestApi mRestApi = RestClient.getClient(getApplicationContext()).create(RestApi.class);

        Observable<LiveVideoModel> mdata = mRestApi.liveVideo("1",classId,className);
        mdata.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LiveVideoModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(LiveVideoModel value) {
                        ProgressDialog.hideDialog();

                        if (value != null && value.getStatus() == 200) {
                            mlist=value.getData();
                            if(mlist!=null){
                                adapter=new LiveVideoAdapter(ZoomLiveClass.this,mlist,ZoomLiveClass.this);
                                activityBinding.rvItemZoom.setAdapter(adapter);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        // display an error message
                        Log.e(TAG, "onError: ");
                        Toast.makeText(ZoomLiveClass.this, ""+e, Toast.LENGTH_SHORT).show();
                        ProgressDialog.hideDialog();
                        //   mDataListener.onError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete: All Done!");
                    }
                });

    }

    private void callLiveClassApiOut() {

        String classId = classIdName;
        String className = "null";

        //String className = SharePrefs.getClassId(ZoomLiveClass.this);

        ProgressDialog.showDialog(ZoomLiveClass.this);
        RestApi mRestApi = RestClient.getClient(getApplicationContext()).create(RestApi.class);

        Observable<LiveVideoModel> mdata = mRestApi.liveVideo1("1",classId,className);
        mdata.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LiveVideoModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(LiveVideoModel value) {
                        ProgressDialog.hideDialog();

                        if (value != null && value.getStatus() == 200) {
                            mlist=value.getData();
                            if(mlist!=null){
                                adapter=new LiveVideoAdapter(ZoomLiveClass.this,mlist,ZoomLiveClass.this);
                                activityBinding.rvItemZoom.setAdapter(adapter);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        // display an error message
                        Log.e(TAG, "onError: ");
                        Toast.makeText(ZoomLiveClass.this, ""+e, Toast.LENGTH_SHORT).show();
                        ProgressDialog.hideDialog();
                        //   mDataListener.onError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete: All Done!");
                    }
                });

    }

    public void OnItemclick(int pos) {

        if (mlist != null) {
            Intent intent = new Intent(ZoomLiveClass.this,ZoomImplement.class);
            intent.putExtra("zoomCode",mlist.get(pos).getCode());
            intent.putExtra("zoomId",mlist.get(pos).getMeetingId());
            //intent.putExtra("videoDetail",mlist.get(pos).getDescription());
            startActivity(intent);
        }

    }

    public void OnDesItemclick(int pos) {

    }
}