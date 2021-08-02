package com.schoollms.View;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.schoollms.Adapter.CourseSpinnerAdapter;
import com.schoollms.GsonModel.CourelistModel;
import com.schoollms.GsonModel.CourseDatum;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.schoollms.GsonModel.Advertisment.AdvertismentModel;
import com.schoollms.GsonModel.lms_signup;
import com.schoollms.R;
import com.schoollms.WebService.Constant_Tag;
import com.schoollms.WebService.RestApi;
import com.schoollms.WebService.RestClient;
import com.schoollms.databinding.ActivityRegistrationBinding;
import com.schoollms.utility.Connectivity;
import com.schoollms.utility.ProgressDialog;
import com.schoollms.utility.SharePrefs;
import com.schoollms.utility.ThemeClass;
import com.schoollms.utility.Utils;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SignupActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = SignupActivity.class.getSimpleName();
    private ActivityRegistrationBinding activityBinding;
    public static SignupActivity activity;

    String signUpUrl;
    CourseSpinnerAdapter mCountrySpinnerAdapter;
    List<CourseDatum> Countrylist;
    String countrydata=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Utils.changeStatuscolor(SignupActivity.this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_registration);
        activity = this;

        activityBinding.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int start,end;
                Log.i("inside checkbox chnge",""+b);
                if(!b){
                    start=activityBinding.inpassword.getSelectionStart();
                    end=activityBinding.inpassword.getSelectionEnd();
                    activityBinding.inpassword.setTransformationMethod(new PasswordTransformationMethod());;
                    activityBinding.inpassword.setSelection(start,end);
                }else{
                    start=activityBinding.inpassword.getSelectionStart();
                    end=activityBinding.inpassword.getSelectionEnd();
                    activityBinding.inpassword.setTransformationMethod(null);
                    activityBinding.inpassword.setSelection(start,end);
                }
            }
        });

        activityBinding.checkBoxVisible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int start,end;
                Log.i("inside checkbox chnge",""+b);
                if(!b){
                    start=activityBinding.inconfirmpassword.getSelectionStart();
                    end=activityBinding.inconfirmpassword.getSelectionEnd();
                    activityBinding.inconfirmpassword.setTransformationMethod(new PasswordTransformationMethod());;
                    activityBinding.inconfirmpassword.setSelection(start,end);
                }else{
                    start=activityBinding.inconfirmpassword.getSelectionStart();
                    end=activityBinding.inconfirmpassword.getSelectionEnd();
                    activityBinding.inconfirmpassword.setTransformationMethod(null);
                    activityBinding.inconfirmpassword.setSelection(start,end);
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activityBinding.inmobileno.setCompoundDrawableTintList(ThemeClass.getcolorstate(SignupActivity.this));
            activityBinding.inpassword.setCompoundDrawableTintList(ThemeClass.getcolorstate(SignupActivity.this));
            activityBinding.infirstname.setCompoundDrawableTintList(ThemeClass.getcolorstate(SignupActivity.this));
            activityBinding.inconfirmpassword.setCompoundDrawableTintList(ThemeClass.getcolorstate(SignupActivity.this));
            //activityBinding.infriendCode.setCompoundDrawableTintList(ThemeClass.getcolorstate(SignupActivity.this));

        }
        ThemeClass.changeButtonColor(activityBinding.btnCreateaccount, SignupActivity.this);

        advertisement();
        getCountydata(SignupActivity.this);

        activityBinding.ivAdverticement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent google=new Intent(Intent.ACTION_VIEW);
                google.setData(Uri.parse(signUpUrl));
                startActivity(google);
            }
        });

        Glide.with(SignupActivity.this)
                .load(SharePrefs.getSetting(SignupActivity.this).getLogo())
                //.transform(new CircleTransform(..))
                .into(activityBinding.ivFree).onLoadFailed(getResources().getDrawable(R.drawable.school_icon));


        activityBinding.tvSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignupActivity.this.finish();

            }
        });


        activityBinding.btnCreateaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (activityBinding.infirstname.getText().toString().trim().length() == 0) {
                    Toasty.warning(SignupActivity.this, getString(R.string.blank_firstname), Toast.LENGTH_SHORT).show();
                } else if (activityBinding.inmobileno.getText().toString().trim().length() == 0) {
                    Toasty.warning(SignupActivity.this, getString(R.string.blank_moblieno), Toast.LENGTH_SHORT).show();
                } else if (activityBinding.inmobileno.getText().toString().trim().length() < 10) {
                    Toasty.warning(SignupActivity.this, getString(R.string.moblieno_invalid), Toast.LENGTH_SHORT).show();
                } else if (activityBinding.inpassword.getText().toString().trim().length() == 0) {
                    Toasty.warning(SignupActivity.this, getString(R.string.blank_password), Toast.LENGTH_SHORT).show();
                } else if (activityBinding.inpassword.getText().toString().trim().length() < 8) {
                    Toasty.warning(SignupActivity.this, getString(R.string.invalid_password), Toast.LENGTH_SHORT).show();
                } else if (activityBinding.inconfirmpassword.getText().toString().trim().length() == 0) {
                    Toasty.warning(SignupActivity.this, getString(R.string.blank_confirmpassword), Toast.LENGTH_SHORT).show();
                } else if (!activityBinding.inpassword.getText().toString().trim().equalsIgnoreCase(activityBinding.inconfirmpassword.getText().toString().trim())) {
                    Toasty.warning(SignupActivity.this, getString(R.string.password_notmatched), Toast.LENGTH_SHORT).show();
                } else {

                    if (Connectivity.isConnected(SignupActivity.this)) {

                        CallSignupApi(SignupActivity.this);
                    } else {

                        //show net not connected error
                        Toasty.warning(SignupActivity.this, getString(R.string.net_disconnected), Toast.LENGTH_SHORT).show();

                    }

                }

            }
        });
    }


    @Override
    public void onBackPressed() {
        SignupActivity.this.finish();
    }

    private void CallSignupApi(final SignupActivity context) {
        String firstname = activityBinding.infirstname.getText().toString().trim();

        String mobileno = activityBinding.inmobileno.getText().toString().trim();
        String password = activityBinding.inpassword.getText().toString().trim();

        ProgressDialog.showDialog(context);
        RestApi mRestApi = RestClient.getClient(context).create(RestApi.class);
        HashMap<String, String> mparam = new HashMap<String, String>();
        mparam.put(Constant_Tag.Name, firstname);
        mparam.put(Constant_Tag.PHONE, mobileno);
        mparam.put(Constant_Tag.PASSWORD, password);
        /*if (activityBinding.infriendCode.getText().toString().trim().length() > 0)
            mparam.put(Constant_Tag.FRIENDCODE, activityBinding.infriendCode.getText().toString());*/

        String androidId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        mparam.put("deviceid", androidId);

        //Toast.makeText(context, ""+androidId, Toast.LENGTH_SHORT).show();

        if (countrydata != null)
        {
            mparam.put("courseId", countrydata);
            SharePrefs.setClassId(countrydata);
            //Toast.makeText(context, ""+countrydata, Toast.LENGTH_SHORT).show();
        }
        else {
            ProgressDialog.hideDialog();
            Toasty.warning(SignupActivity.this, "Please select Course", Toast.LENGTH_SHORT).show();
            return;
        }

        Observable<lms_signup> mdata = mRestApi.getSignup(mparam);
        mdata.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<lms_signup>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(lms_signup value) {
                        ProgressDialog.hideDialog();
                        if (value != null && value.getStatus() == 200)
                        {
                            Toasty.success(SignupActivity.this, value.getMessage(), Toast.LENGTH_LONG).show();

                            Intent i = new Intent(SignupActivity.this, OtpActivity.class);
                            Bundle b = new Bundle();
                            b.putString("signOtp", value.getData().getOtp().toString());
                            b.putString("type","signUp");
                            b.putParcelable("userdata", value.getData().getUserData());
                            i.putExtras(b);
                            startActivity(i);
                        }
                        else if (value != null)
                        {
                            //mDataListener.onError(value.getMessage());
                            Toasty.error(SignupActivity.this, value.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        // display an error message
                        Log.e(TAG, "onError: ");
                        ProgressDialog.hideDialog();
                        Toast.makeText(getApplicationContext(), ""+e, Toast.LENGTH_SHORT).show();
                        //   mDataListener.onError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete: All Done!");
                    }
                });
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void advertisement()
    {

        RestApi mRestApi = RestClient.getClient(SignupActivity.this).create(RestApi.class);

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

                        if (value != null && value.getStatus() == 200)
                        {

                            if (value.getData().get(7).getId() == "5f9d49e5f701c65e7f707f19" || value.getData().get(7).getStatus() == 1)
                            {
                                signUpUrl = value.getData().get(7).getURL();
                                Picasso.get().load(value.getData().get(7).getImage()).into(activityBinding.ivAdverticement);
                            }
                            else
                            {
                                Toast.makeText(SignupActivity.this, "No Such Advertisement", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else if (value != null)
                        {
                            //mDataListener.onError(value.getMessage());
                            Toasty.error(SignupActivity.this, value.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        // display an error message
                        Log.e(TAG, "onError: "+e.getMessage());
                        Toast.makeText(SignupActivity.this, ""+e, Toast.LENGTH_SHORT).show();

                        //   mDataListener.onError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete: All Done!");
                    }
                });
    }

    boolean isfirstimecountry = true;

    private void getCountydata(final Context context) {

        RestApi mRestApi = RestClient.getClient(context).create(RestApi.class);
        HashMap<String, String> mparam = new HashMap<String, String>();
        mparam.put(Constant_Tag.STATUS, "1");

        Observable<CourelistModel> mdata = mRestApi.courselist(mparam);
        mdata.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CourelistModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CourelistModel value) {
                        ProgressDialog.hideDialog();

                        if (value != null && value.getStatus() == 200) {
                            Countrylist = value.getData();
                            mCountrySpinnerAdapter = new CourseSpinnerAdapter(SignupActivity.this, R.layout.spinner_rows, Countrylist);
                            activityBinding.registrationSpCountry.setAdapter(mCountrySpinnerAdapter);


                            for (int i = Countrylist.size() - 1; i >= 0; i--) {
                                if (Countrylist.get(i).getId().contains("5f8967e3f701c65e7f707ef9"))
                                {
                                    Countrylist.remove(i);
                                }
                                else if (Countrylist.get(i).getId().contains("5f8968f4f701c65e7f707efa"))
                                {
                                    Countrylist.remove(i);
                                }
                            }

                            activityBinding.registrationSpCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    if (isfirstimecountry == true) {
                                        isfirstimecountry = false;

                                        return;
                                    }
                                        if (position > -1) {
                                    countrydata = Countrylist.get(position).getId();
                                } else {
                                countrydata = null;
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });


                            /*if (Countrylist != null && Countrylist.size() > 0 && mProfileData != null && mProfileData.getData().getCountry() != null && isfirstimecountry) {
                                for (int i = 0; i < Countrylist.size(); i++) {
                                    if (Countrylist.get(i).getId().equalsIgnoreCase(mProfileData.getData().getCountry())) {
                                        activityBinding.completeSpCountry.setSelection(i + 1);
                                        countrydata = Countrylist.get(i).getId();
                                        break;
                                    }
                                }

                                activityBinding.registrationSpCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        if (isfirstimecountry == true) {
                                            isfirstimecountry = false;

                                            return;
                                        }
                                        *//*if (position > -1) {
                                            countrydata = Countrylist.get(position).getId();
                                            getStatedata(CompleteProfile.this, countrydata);
                                        }*//* else {
                                            countrydata = null;
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                            }


                        } else if (value != null) {
                            Toasty.error(SignupActivity.this, value.getMessage(), Toast.LENGTH_SHORT).show();*/

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
                        Log.e(TAG, "onComplete: All Done!");
                    }
                });
    }

}

