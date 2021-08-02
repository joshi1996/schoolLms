package com.schoollms.View;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.schoollms.Adapter.CitySpinnerAdapter;
import com.schoollms.Adapter.CountrySpinnerAdapter;
import com.schoollms.Adapter.StateSpinnerAdapter;
import com.schoollms.GsonModel.CountryModels.CountryModel;
import com.schoollms.GsonModel.CountryModels.DatumCountry;
import com.schoollms.GsonModel.CountryModels.stateModel.Datumstate;
import com.schoollms.GsonModel.CountryModels.stateModel.StateModel;
import com.schoollms.GsonModel.ProflieData.ProfileData;
import com.schoollms.GsonModel.citydata.CityModel;
import com.schoollms.GsonModel.citydata.Datumcity;
import com.schoollms.GsonModel.lmsData;
import com.schoollms.R;
import com.schoollms.WebService.RestApi;
import com.schoollms.WebService.RestClient;
import com.schoollms.databinding.FragmentProfileBinding;
import com.schoollms.utility.Connectivity;
import com.schoollms.utility.ImageFilePath;
import com.schoollms.utility.ProgressDialog;
import com.schoollms.utility.SharePrefs;
import com.schoollms.utility.ThemeClass;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ProfileFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {
    private static final int SELECT_FILE = 11;
    private FragmentProfileBinding mbinding;
    private String TAG = ProfileFragment.class.getSimpleName();
    final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1, MY_PERMISSIONS_REQUEST_CAMERA = 10;
    public static Uri imageUri = null;
    public static String imagepath = "";
    String userChoosenTask;

    String emailMain = "youremail@gmail.com";

    ProfileData mProfileData;

    CountrySpinnerAdapter mCountrySpinnerAdapter;
    StateSpinnerAdapter mStateSpinnerAdapter;
    List<Datumstate> Statelist;
    List<Datumcity> Citylist;
    List<DatumCountry> Countrylist;
    String statedata = null;
    String countrydata = null;
    String citydata = null;

    CitySpinnerAdapter mCitySpinnerAdapter;

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        View rootView = mbinding.getRoot();

        TextView tv_header = (TextView) mbinding.headerview.findViewById(R.id.tv_header);

        tv_header.setText(getString(R.string.profile));

        mbinding.inemailaddress.setText(emailMain);

        filldata();

        //getCitydata(getActivity(), (JSONArray) Citylist);

        LinearLayout ll_back = (LinearLayout) mbinding.headerview.findViewById(R.id.ll_back);
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();

            }
        });
        ThemeClass.changeHeaderColor(mbinding.headerview, getActivity());


        ThemeClass.changeButtonColor(mbinding.btnUpdate, getActivity());

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
        } else {
            //show camera
        }

        mbinding.clickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        mbinding.clickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        mbinding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mbinding.infirstname.getText().toString().trim().length() == 0) {
                    Toasty.warning(getActivity(), getString(R.string.blank_firstname), Toast.LENGTH_SHORT).show();
                } else if (mbinding.inmobileno.getText().toString().trim().length() == 0) {
                    Toasty.warning(getActivity(), getString(R.string.blank_moblieno), Toast.LENGTH_SHORT).show();
                } else if (mbinding.inmobileno.getText().toString().trim().length() < 10) {
                    Toasty.warning(getActivity(), getString(R.string.moblieno_invalid), Toast.LENGTH_SHORT).show();
                } else {
                    if (Connectivity.isConnected(getActivity())) {
                        if (mbinding.inemailaddress.length()==0)
                        {
                            mbinding.inemailaddress.setText(emailMain);
                        }
                        else
                        {
                            CallUpdateApi(getActivity());
                        }
                        //Toast.makeText(getActivity(), ""+emailMain, Toast.LENGTH_SHORT).show();

                    } else {
                        //show net not connected error
                        Toasty.warning(getActivity(), getString(R.string.net_disconnected), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
        // Setup Places Client
        return rootView;
    }

    private void selectImage() {
        final CharSequence[] items = {getString(R.string.takephoto), getString(R.string.fromgallery),
                getString(R.string.cancle)};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.addphoto));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.takephoto))) {
                    userChoosenTask = getString(R.string.takephoto);
                    cameraIntent();
                } else if (items[item].equals(getString(R.string.fromgallery))) {
                    userChoosenTask = getString(R.string.fromgallery);
                    galleryIntent();
                } else if (items[item].equals(getString(R.string.cancle))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        String Path = Environment.getExternalStorageDirectory().getPath().toString() + "/lsf/";

        File FPath = new File(Path);
        if (!FPath.exists()) {
            if (!FPath.mkdir()) {
                System.out.println("***Problem creating Image folder " + Path);
            }
        }

        String fileName = Calendar.getInstance().getTimeInMillis() + ".jpg";
        Log.d("path0", "" + Path + fileName);
        // Create parameters for Intent with filename

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.TITLE, fileName);
        values.put(MediaStore.Images.Media.DATA, Path + fileName);

        values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera");

        // imageUri is the current activity attribute, define and save it for later usage

        imageUri = getActivity().getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

    }

    public static RequestBody createRequestBody(@NonNull String s) {
        return RequestBody.create(
                MediaType.parse("multipart/form-data"), s);
    }

    private void CallUpdateApi(Context mcontext) {

        String firstname = mbinding.infirstname.getText().toString().trim();
        final String mobileno = mbinding.inmobileno.getText().toString().trim();
        emailMain = mbinding.inemailaddress.getText().toString().trim();
        final String address = mbinding.inaddress.getText().toString().trim();
        //final String state = mbinding.instate.getText().toString().trim();;
        //final String city = mbinding.incity.getText().toString().trim();;
        final String zipcode = mbinding.inzipcode.getText().toString().trim();

        ProgressDialog.showDialog(mcontext);
        RestApi mRestApi = RestClient.getClient(mcontext).create(RestApi.class);
        Map<String, RequestBody> mparam = new HashMap<String, RequestBody>();

        mparam.put("fullName", createRequestBody(firstname));
        mparam.put("address", createRequestBody(address));
        mparam.put("emailAddress", createRequestBody(emailMain));
        mparam.put("phoneNo", createRequestBody(mobileno));
        mparam.put("zipCode", createRequestBody(zipcode));

        if (countrydata != null) {
            mparam.put("country", createRequestBody(countrydata));
        } else {
            ProgressDialog.hideDialog();
            Toasty.warning(getActivity(), "Please select Country", Toast.LENGTH_SHORT).show();
            return;
        }
        if (statedata != null) {
            mparam.put("state", createRequestBody(statedata));
        } else {
            ProgressDialog.hideDialog();
            Toasty.warning(getActivity(), "Please select State", Toast.LENGTH_SHORT).show();
            return;
        }

        if (citydata != null) {
            mparam.put("city", createRequestBody(citydata));
        } else {
            ProgressDialog.hideDialog();

            Toasty.warning(getActivity(), "Please select City", Toast.LENGTH_SHORT).show();

            return;
        }

        MultipartBody.Part imagenPerfil = null;
        RequestBody requestFile = null;
        if (imagepath.length() > 0) {
            //show blank username alert
            File file = new File(imagepath);
            requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);
            //MultipartBody.Part is used to send also the actual file name
            imagenPerfil = MultipartBody.Part.createFormData("profilePhoto", file.getName(), requestFile);
        }

        Observable<ProfileData> mdata = mRestApi.updateProfile(mparam, imagenPerfil);
        mdata.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ProfileData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ProfileData value) {
                        ProgressDialog.hideDialog();
                        if (value != null && value.getStatus() == 200) {
                            Toasty.success(getActivity(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                            getProfile(getActivity(), true);
                        } else if (value != null) {
                            Toasty.error(getActivity(), value.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void filldata() {

        if (Connectivity.isConnected(getActivity())) {
            getProfile(getActivity(), false);

        } else {
            //show net not connected error
            Toasty.warning(getActivity(), getString(R.string.net_disconnected), Toast.LENGTH_SHORT).show();
        }
    }

    private void getProfile(final Context context, final boolean isfinish) {

        ProgressDialog.showDialog(context);
        RestApi mRestApi = RestClient.getClient(context).create(RestApi.class);
        HashMap<String, String> mparam = new HashMap<String, String>();
        mparam.put("userId", SharePrefs.getUserdetail(getActivity()).getUser().getId());

        Observable<ProfileData> mdata = mRestApi.getProfile(mparam);
        Log.d("data", mdata.toString());

        mdata.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ProfileData>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(ProfileData value) {
                        Log.d("value", "" + value);
                        ProgressDialog.hideDialog();
                        if (value != null && value.getStatus() == 200) {
                            mProfileData = value;
                            //String city = value.getData().getCity();
                            //Toast.makeText(context, ""+city, Toast.LENGTH_SHORT).show();
                            lmsData mUserDetails = SharePrefs.getUserdetail(getActivity());
                            mUserDetails.setUser(value.getData());
                            SharePrefs.saveUserDetail(getActivity(), mUserDetails);

                            Log.d("data:", value.getData().getFullName());

                            if (isfinish) {
                                ((MainActivity) getActivity()).onBackPressed();
                                return;
                            }

                            mbinding.infirstname.setText(value.getData().getFullName());
                            //Toasty.error(getActivity(), ""+value.getData().getFullName(), Toast.LENGTH_SHORT).show();
                            mbinding.inemailaddress.setText(value.getData().getEmailAddress());
                            //Toast.makeText(context, ""+mbinding.inemailaddress, Toast.LENGTH_SHORT).show();
                            mbinding.inmobileno.setText(value.getData().getPhoneNo());
                            getCountydata(context);
                            mbinding.inzipcode.setText(value.getData().getZipCode());
                            mbinding.inaddress.setText(value.getData().getAddress());

                            Glide.with(getActivity())
                                    .load(SharePrefs.getUserdetail(getActivity()).getUser().getProfilePhoto()).dontAnimate()
                                    .placeholder(R.drawable.appstore)
                                    .into(mbinding.profileImage);

                            /*Glide.with(getActivity())
                                    .load(value.getData().getProfilePhoto()).dontAnimate()
                                    .placeholder(R.drawable.appstore)
                                    .into(mbinding.profileImage);*/
                        }
                        else if (value.getStatus() == 401)
                        {
                            Toasty.error(getActivity(), value.getMessage(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(),LoginActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        }
                        else {
                            Toasty.error(getActivity(), value.getMessage(), Toast.LENGTH_SHORT).show();
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

    boolean isfirstimecountry = true;

    private void getCountydata(final Context context) {

        RestApi mRestApi = RestClient.getClient(context).create(RestApi.class);
        HashMap<String, String> mparam = new HashMap<String, String>();

        Observable<CountryModel> mdata = mRestApi.getCountry();
        mdata.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CountryModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CountryModel value) {
                        ProgressDialog.hideDialog();

                        if (value != null && value.getStatus() == 200) {
                            Countrylist = value.getData();
                            mCountrySpinnerAdapter = new CountrySpinnerAdapter(getActivity(), R.layout.spinner_rows, Countrylist);
                            mbinding.spCountry.setAdapter(mCountrySpinnerAdapter);

                            if (Countrylist != null && Countrylist.size() > 0 && mProfileData != null && mProfileData.getData().getCountry() != null && isfirstimecountry) {
                                for (int i = 0; i < Countrylist.size(); i++) {
                                    if (Countrylist.get(i).getId().equalsIgnoreCase(mProfileData.getData().getCountry())) {
                                        mbinding.spCountry.setSelection(i + 1);
                                        countrydata = Countrylist.get(i).getId();
                                        break;
                                    }
                                }

                                mbinding.spCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        if (isfirstimecountry == true) {
                                            isfirstimecountry = false;

                                            return;
                                        }
                                        if (position > -1) {
                                            countrydata = Countrylist.get(position).getId();
                                            getStatedata(getActivity(), countrydata);
                                        } else {
                                            countrydata = null;
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent)
                                    {

                                    }
                                });
                            }

                        }
                        else if (value != null)
                        {
                            Toasty.error(getActivity(), value.getMessage(), Toast.LENGTH_SHORT).show();
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

    boolean isfirsttime_state = true;

    private void getStatedata(final Context context, String countryid) {

        RestApi mRestApi = RestClient.getClient(context).create(RestApi.class);

        Observable<StateModel> mdata = mRestApi.getState(countryid, "1");
        mdata.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<StateModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(StateModel value) {
                        ProgressDialog.hideDialog();
                        if (value != null && value.getStatus() == 200) {
                            Statelist = value.getData();
                            mStateSpinnerAdapter = new StateSpinnerAdapter(getActivity(), R.layout.spinner_rows, Statelist);
                            mbinding.spState.setAdapter(mStateSpinnerAdapter);


                            if (isfirsttime_state) {


                                if (Statelist != null && Statelist.size() > 0 && mProfileData != null && mProfileData.getData().getState() != null) {
                                    for (int i = 0; i < Statelist.size(); i++) {

                                        if (Statelist.get(i).getId().equalsIgnoreCase(mProfileData.getData().getState())) {
                                            mbinding.spState.setSelection(i + 1);
                                            statedata = Statelist.get(i).getId();

                                            break;
                                        }
                                    }
                                }

                                mbinding.spState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        if (isfirsttime_state) {
                                            isfirsttime_state = false;

                                            return;
                                        }

                                        if (position > -1) {
                                            statedata = Statelist.get(position).getId();
                                            getCitydata(getActivity(),statedata);
                                            /*JSONArray marray;

                                            marray = new JSONArray();

                                            JSONObject mobject = new JSONObject();
                                            try {
                                                mobject.put("name", Statelist.get(position).getName());
                                                mobject.put("stateId", Statelist.get(position).getId());
                                                mobject.put("status", Statelist.get(position).getStatus());
                                                marray.put(mobject);


                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }*/


                                        } else {
                                            statedata = null;
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });

                            }


                        } else if (value != null) {
                            Toasty.error(getActivity(), value.getMessage(), Toast.LENGTH_SHORT).show();

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

    boolean isfirsttime_city = true;

    private void getCitydata(final Context context, String Id) {

        RestApi mRestApi = RestClient.getClient(context).create(RestApi.class);
        HashMap<String, String> mparam = new HashMap<String, String>();

        Observable<CityModel> mdata = mRestApi.getCity(Id,"1");
        mdata.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CityModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CityModel value) {
                        ProgressDialog.hideDialog();
                        if (value != null && value.getStatus() == 200) {

                            Citylist = value.getData();
                            mCitySpinnerAdapter = new CitySpinnerAdapter(getActivity(), R.layout.spinner_rows, Citylist);
                            mbinding.spCity.setAdapter(mCitySpinnerAdapter);
                            if (Citylist != null && Citylist.size() > 0 && mProfileData != null && mProfileData.getData().getCity() != null && isfirsttime_city) {
                                for (int i = 0; i < Citylist.size(); i++) {

                                    if (Citylist.get(i).getId().equalsIgnoreCase(mProfileData.getData().getCity())) {
                                        mbinding.spCity.setSelection(i + 1);
                                        break;
                                    }
                                }
                                isfirsttime_city = false;
                            }

                            mbinding.spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    if (position > -1) {
                                        citydata = Citylist.get(position).getId();
                                    } else {
                                        citydata = null;
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });


                        } else if (value != null) {
                            Toasty.error(getActivity(), value.getMessage(), Toast.LENGTH_SHORT).show();

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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {

            if (resultCode == getActivity().RESULT_OK && imageUri != null) {
                imagepath = convertImageUriToFile(imageUri, getActivity());

                try {
                    System.gc();

                    Bitmap bitmap = BitmapFactory.decodeFile(imagepath);


                    ExifInterface ei = new ExifInterface(imagepath);
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);

                    Bitmap rotatedBitmap = null;
                    switch (orientation) {

                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotatedBitmap = rotateImage(bitmap, 90);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotatedBitmap = rotateImage(bitmap, 180);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotatedBitmap = rotateImage(bitmap, 270);
                            break;

                        case ExifInterface.ORIENTATION_NORMAL:
                        default:
                            rotatedBitmap = bitmap;
                    }

                    mbinding.profileImage.setImageBitmap(rotatedBitmap);

                    mbinding.profileImage.setVisibility(View.VISIBLE);

                    System.gc();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } else if (resultCode == getActivity().RESULT_CANCELED) {

                Toast.makeText(getActivity(), " Picture was not taken ", Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(getActivity(), " Picture was not taken ", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == SELECT_FILE) {


            imageUri = data.getData();


            imagepath = ImageFilePath.getPath(getActivity(), data.getData());

            Bitmap bm = null;
            if (data != null) {
                try {
                    System.gc();

                    Bitmap bitmap = BitmapFactory.decodeFile(imagepath);

                    ExifInterface ei = new ExifInterface(imagepath);
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);

                    Bitmap rotatedBitmap = null;
                    switch (orientation) {

                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotatedBitmap = rotateImage(bitmap, 90);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotatedBitmap = rotateImage(bitmap, 180);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotatedBitmap = rotateImage(bitmap, 270);
                            break;

                        case ExifInterface.ORIENTATION_NORMAL:
                        default:
                            rotatedBitmap = bitmap;
                    }

                    mbinding.profileImage.setImageBitmap(rotatedBitmap);
                    mbinding.profileImage.setVisibility(View.VISIBLE);

                    System.gc();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public static String convertImageUriToFile(Uri imageUri, FragmentActivity activity) {

        Cursor cursor = null;
        int imageID = 0;
        String Path = "";
        try {

            /*********** Which columns values want to get *******/
            String[] proj = {
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Thumbnails._ID,
                    MediaStore.Images.ImageColumns.ORIENTATION
            };

            cursor = activity.getContentResolver().query(

                    imageUri,         //  Get data for specific image URI
                    proj,             //  Which columns to return
                    null,             //  WHERE clause; which rows to return (all rows)
                    null,             //  WHERE clause selection arguments (none)
                    null              //  Order-by clause (ascending by name)

            );

            //  Get Query Data

            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int columnIndexThumb = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);
            int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            //int orientation_ColumnIndex = cursor.
            //    getColumnIndexOrThrow(MediaStore.Images.ImageColumns.ORIENTATION);

            int size = cursor.getCount();

            /*******  If size is 0, there are no images on the SD Card. *****/

            if (size == 0) {

                Toast.makeText(activity, " No Image", Toast.LENGTH_SHORT).show();

            } else {

                int thumbID = 0;
                if (cursor.moveToFirst()) {

                    /**************** Captured image details ************/

                    /*****  Used to show image on view in LoadImagesFromSDCard class ******/
                    imageID = cursor.getInt(columnIndex);

                    thumbID = cursor.getInt(columnIndexThumb);

                    Path = cursor.getString(file_ColumnIndex);
                    Log.d("path1", "" + Path);

                    //String orientation =  cursor.getString(orientation_ColumnIndex);
                    String CapturedImageDetails = " CapturedImageDetails : \n\n"
                            + " ImageID :" + imageID + "\n"
                            + " ThumbID :" + thumbID + "\n"
                            + " Path :" + Path + "\n";

                    Log.d("path2", Path);

                    // Show Captured Image detail on activity
                    // Toast.makeText(activity, CapturedImageDetails, Toast.LENGTH_SHORT).show();


                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        // Return Captured Image ImageID ( By this ImageID Image will load from sdcard )

        return Path;
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}