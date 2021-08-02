package com.schoollms.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.schoollms.Adapter.AssessmentAdapter;
import com.schoollms.Adapter.NotificationAdapter;
import com.schoollms.GsonModel.Assessment.AssessmentModel;
import com.schoollms.GsonModel.Assessment.DatumAssess;
import com.schoollms.GsonModel.Notification.NotificationModel;
import com.schoollms.GsonModel.Notification.NotificationResponse;
import com.schoollms.R;
import com.schoollms.WebService.RestApi;
import com.schoollms.WebService.RestClient;
import com.schoollms.utility.Connectivity;
import com.schoollms.utility.GridSpacingItemDecoration;
import com.schoollms.utility.ProgressDialog;
import com.schoollms.utility.SharePrefs;
import com.schoollms.utility.Utils;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NotificationActivity extends AppCompatActivity {

    private static final String TAG = NotificationActivity.class.getSimpleName();
    ImageView header;
    TextView headerText;
    RecyclerView listNotification;
    List<NotificationModel> mlist = new ArrayList<>();
    String id;
    NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        header = findViewById(R.id.iv_back);
        headerText = findViewById(R.id.tv_header);
        listNotification = findViewById(R.id.notifyRecyclerView);

        headerText.setText("Notification");

        id = SharePrefs.getUserdetail(getApplicationContext()).getUser().getId();

        listNotification.setHasFixedSize(true);
        listNotification.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.margin_15);
        listNotification.addItemDecoration(new GridSpacingItemDecoration(1, spacingInPixels, false));


        if (Connectivity.isConnected(NotificationActivity.this)) {

            callNotification();
        }

    }

    private void callNotification() {

        ProgressDialog.showDialog(NotificationActivity.this);
        RestApi mRestApi = RestClient.getClient(getApplicationContext()).create(RestApi.class);

        Observable<NotificationResponse> mdata = mRestApi.notification(id);
        mdata.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NotificationResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(NotificationResponse value) {
                        ProgressDialog.hideDialog();
                        if (value != null)
                            Utils.Relogin(value.getStatus(), getApplicationContext());

                        if (value != null && value.getStatus() == 200) {
                            mlist.clear();
                            mlist = value.getData();

                            adapter = new NotificationAdapter(NotificationActivity.this, mlist);
                            listNotification.setAdapter(adapter);


                        } else if (value.getStatus() == 401) {
                            Toasty.error(NotificationActivity.this, value.getMessage(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(NotificationActivity.this, LoginActivity.class);
                            startActivity(intent);
                            NotificationActivity.this.finish();
                        }
                        else {
                            Toasty.error(NotificationActivity.this,value.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        // display an error message
                        Log.e(TAG, "onError: ");
                        ProgressDialog.hideDialog();
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete: All Done!");
                    }
                });

    }
}