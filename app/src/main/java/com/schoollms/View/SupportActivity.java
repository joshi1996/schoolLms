package com.schoollms.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.schoollms.GsonModel.Advertisment.AdvertismentModel;
import com.schoollms.GsonModel.settingdata.SettingModel;
import com.schoollms.R;
import com.schoollms.WebService.RestApi;
import com.schoollms.WebService.RestClient;
import com.schoollms.databinding.ActivityPrivacyPolicyBinding;
import com.schoollms.databinding.ActivitySupportBinding;
import com.schoollms.utility.Connectivity;
import com.schoollms.utility.ProgressDialog;
import com.schoollms.utility.ThemeClass;
import com.schoollms.utility.Utils;
import com.squareup.picasso.Picasso;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class SupportActivity extends AppCompatActivity {

    private static final String TAG = SupportActivity.class.getSimpleName();
    private ActivitySupportBinding activityBinding;

    String supportUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.changeStatuscolor(SupportActivity.this);

        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_support);

        ThemeClass.changeHeaderColor(activityBinding.headerSupport, getApplicationContext());
        TextView tv_header = (TextView) activityBinding.headerSupport.findViewById(R.id.tv_header);
        tv_header.setText(getString(R.string.support));
        LinearLayout ll_back = (LinearLayout) activityBinding.headerSupport.findViewById(R.id.ll_back);
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        activityBinding.ivAdverticement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent google=new Intent(Intent.ACTION_VIEW);
                google.setData(Uri.parse(supportUrl));
                startActivity(google);
            }
        });


        if (Connectivity.isConnected(SupportActivity.this)) {
            advertisement();
            CallSettingApi(SupportActivity.this);
        } else {

            //show net not connected error
            Toasty.warning(SupportActivity.this, getString(R.string.net_disconnected), Toast.LENGTH_SHORT).show();

        }

    }

    private void CallSettingApi(final Context context) {

        ProgressDialog.showDialog(context);

        RestApi mRestApi = RestClient.getClient(context).create(RestApi.class);
        Observable<SettingModel> mdata = mRestApi.Getsettings();
        mdata.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SettingModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "onSubscribe: ");
                    }

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onNext(SettingModel value) {
                        ProgressDialog.hideDialog();
                        if (value != null && value.getStatus() == 200) {
                            Utils.hideKeyboard(SupportActivity.this);

                            String str_without_html= Html.fromHtml(value.getData().getSupportContent()).toString();
                            activityBinding.tvSupport.setText(str_without_html);
                            activityBinding.tvSupport.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
                            activityBinding.tvSupport.setMovementMethod(LinkMovementMethod.getInstance());

                        }
                        else if (value != null) {
                            //mDataListener.onError(value.getMessage());
                            Toasty.error(SupportActivity.this, value.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        // display an error message
                        Log.e(TAG, "onError: " + e.getMessage());
                        ProgressDialog.hideDialog();
                        //   mDataListener.onError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete: All Done!");
                    }
                });
    }

    @Override
    public void onBackPressed() {
        SupportActivity.this.finish();
    }

    public void advertisement() {

        RestApi mRestApi = RestClient.getClient(SupportActivity.this).create(RestApi.class);

        Observable<AdvertismentModel> mdata = mRestApi.advertisement();
        mdata.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AdvertismentModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(AdvertismentModel value) {

                        if (value != null && value.getStatus() == 200) {

                            if (value.getData().get(5).getId() == "5f9d2d6ff701c65e7f707f17" || value.getData().get(5).getStatus() == 1) {

                                supportUrl = value.getData().get(5).getURL();
                                Picasso.get().load(value.getData().get(5).getImage()).into(activityBinding.ivAdverticement);
                            } else {
                                Toast.makeText(SupportActivity.this, "No Such Advertisement", Toast.LENGTH_SHORT).show();
                            }
                        } else if (value != null) {

                            Toasty.error(SupportActivity.this, value.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        // display an error message
                        Log.e(TAG, "onError: " + e.getMessage());
                        Toast.makeText(SupportActivity.this, "" + e, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete: All Done!");
                    }
                });
    }

}