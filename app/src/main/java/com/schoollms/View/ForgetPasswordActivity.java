package com.schoollms.View;


import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.schoollms.GsonModel.Advertisment.AdvertismentModel;
import com.schoollms.GsonModel.ForgetPassword.ForgotPassword;
import com.schoollms.R;
import com.schoollms.WebService.Constant_Tag;
import com.schoollms.WebService.RestApi;
import com.schoollms.WebService.RestClient;
import com.schoollms.databinding.ActivityForgotpasswordBinding;
import com.schoollms.utility.Connectivity;
import com.schoollms.utility.ProgressDialog;
import com.schoollms.utility.SharePrefs;
import com.schoollms.utility.ThemeClass;
import com.schoollms.utility.Utils;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class ForgetPasswordActivity extends AppCompatActivity {
    private static final String TAG = ForgetPasswordActivity.class.getSimpleName();
    private ActivityForgotpasswordBinding activityBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.changeStatuscolor(ForgetPasswordActivity.this);

        activityBinding = DataBindingUtil.setContentView(this,  R.layout.activity_forgotpassword);

        ThemeClass.changeButtonColor(activityBinding.btnResetpassword,ForgetPasswordActivity.this);

        advertisement();

        Glide.with(ForgetPasswordActivity.this)
                .load(SharePrefs.getSetting(ForgetPasswordActivity.this).getLogo())
                //.transform(new CircleTransform(..))
                .into(activityBinding.ivFree).onLoadFailed(getResources().getDrawable(R.drawable.school_icon));


        activityBinding.tvSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgetPasswordActivity.this.finish();
            }
        });

        activityBinding.tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgetPasswordActivity.this,SignupActivity.class));
                ForgetPasswordActivity.this.finish();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activityBinding.inmobileno.setCompoundDrawableTintList(ThemeClass.getcolorstate(ForgetPasswordActivity.this));


        }

        activityBinding.btnResetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activityBinding.inmobileno.getText().toString().trim().length() == 0) {
                    Toasty.warning(ForgetPasswordActivity.this, getString(R.string.blank_moblieno), Toast.LENGTH_SHORT).show();
                }
                else if (activityBinding.inmobileno.getText().toString().trim().length()<10) {
                    Toasty.warning(ForgetPasswordActivity.this, getString(R.string.moblieno_invalid), Toast.LENGTH_SHORT).show();
                }
                else {

                    if (Connectivity.isConnected(ForgetPasswordActivity.this)) {

                        CallForgetPasswordApi(ForgetPasswordActivity.this);
                    } else {

                        //show net not connected error
                        Toasty.warning(ForgetPasswordActivity.this, getString(R.string.net_disconnected), Toast.LENGTH_SHORT).show();

                    }

                }
            }
        });

    }


    @Override
    public void onBackPressed() {
        ForgetPasswordActivity.this.finish();
    }

    private void CallForgetPasswordApi(final Context context) {
        String mobileno = activityBinding.inmobileno.getText().toString().trim();
        ProgressDialog.showDialog(context);
        RestApi mRestApi = RestClient.getClient(context).create(RestApi.class);
        HashMap<String, String> mparam = new HashMap<String, String>();
        mparam.put(Constant_Tag.PHONE, mobileno);
        Observable<ForgotPassword> mdata = mRestApi.forgotPassword(mparam);
        mdata.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ForgotPassword>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "onSubscribe: ");
                    }
                    @Override
                    public void onNext(ForgotPassword value) {
                        ProgressDialog.hideDialog();

                        if (value != null && value.getStatus() == 200) {
                            //Toasty.success(ForgetPasswordActivity.this, value.getMessage(), Toast.LENGTH_LONG).show();
                            Utils.hideKeyboard(ForgetPasswordActivity.this);

                            Intent i=new Intent(ForgetPasswordActivity.this,ResetPassword.class);
                            Bundle b=new Bundle();
                            b.putString("otp",value.getData().getOtp().toString());
                            b.putParcelable("userdata",value.getData().getUser());
                            b.putString("mobile",activityBinding.inmobileno.getText().toString());
                            i.putExtras(b);

                            startActivity(i);
                            if(LoginActivity.activityobj!=null){
                                LoginActivity.activityobj.finish();
                            }
                            ForgetPasswordActivity.this.finish();
                        } else if (value != null) {
                            Toasty.error(ForgetPasswordActivity.this, value.getMessage(), Toast.LENGTH_LONG).show();
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

    public void advertisement()
    {
        ProgressDialog.showDialog(ForgetPasswordActivity.this);
        RestApi mRestApi = RestClient.getClient(ForgetPasswordActivity.this).create(RestApi.class);

        Observable<AdvertismentModel> mdata = mRestApi.advertisement();
        mdata.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AdvertismentModel>() {
                    @Override
                    public void onSubscribe(Disposable d)
                    {
                        Log.e(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(AdvertismentModel value) {
                        ProgressDialog.hideDialog();
                        if (value != null && value.getStatus() == 200)
                        {

                            if (value.getData().get(8).getId() == "5f9d49f8f701c65e7f707f1a" || value.getData().get(8).getStatus() == 1)
                            {
                                Picasso.get().load(value.getData().get(8).getImage()).into(activityBinding.ivAdvertisementForget);
                            }
                            else
                            {
                                Toast.makeText(ForgetPasswordActivity.this, "No Such Advertisement", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else if (value != null)
                        {
                            //mDataListener.onError(value.getMessage());
                            Toasty.error(ForgetPasswordActivity.this, value.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        // display an error message
                        Log.e(TAG, "onError: "+e.getMessage());
                        Toast.makeText(ForgetPasswordActivity.this, ""+e, Toast.LENGTH_SHORT).show();
                        ProgressDialog.hideDialog();
                        //   mDataListener.onError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete: All Done!");
                    }
                });
    }
}

