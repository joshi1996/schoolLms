package com.schoollms.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.schoollms.R;
import com.schoollms.WebService.RestApi;
import com.schoollms.WebService.RestClient;
import com.schoollms.databinding.ActivityUploadAssessmentBinding;
import com.schoollms.GsonModel.uploadAssessment.uploadModel;
import com.schoollms.utility.Connectivity;
import com.schoollms.utility.ImageFilePath;
import com.schoollms.utility.ProgressDialog;
import com.schoollms.utility.ThemeClass;
import com.schoollms.utility.Utils;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
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

public class UploadAssessmentActivity extends AppCompatActivity {

    private static final String TAG = UploadAssessmentActivity.class.getSimpleName();
    private ActivityUploadAssessmentBinding activityBinding;

    String contentType,userId,assessmentId,date;

    private static final int SELECT_FILE = 11;
    final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1, MY_PERMISSIONS_REQUEST_CAMERA = 10;
    public static Uri imageUri = null;
    public static String imagepath="";
    String userChoosenTask;
    String showImageLay,showTextLay = "image";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.changeStatuscolor(UploadAssessmentActivity.this);

        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_upload_assessment);

        ThemeClass.changeHeaderColor(activityBinding.headerSupport, getApplicationContext());
        TextView tv_header = (TextView) activityBinding.headerSupport.findViewById(R.id.tv_header);
        tv_header.setText("Submit Assessment");
        LinearLayout ll_back = (LinearLayout) activityBinding.headerSupport.findViewById(R.id.ll_back);

        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        contentType = getIntent().getStringExtra("content");
        userId = getIntent().getStringExtra("user_id");
        assessmentId = getIntent().getStringExtra("assessment_id");
        date = getIntent().getStringExtra("submission_date");

        if (ContextCompat.checkSelfPermission(UploadAssessmentActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(UploadAssessmentActivity.this,
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(UploadAssessmentActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(UploadAssessmentActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(UploadAssessmentActivity.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_CAMERA);

            }
        } else {
            //show camera
        }

        activityBinding.chooseUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(UploadAssessmentActivity.this);
                dialog.setContentView(R.layout.choose_assess_type);

                Window window = dialog.getWindow();
                window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                //RadioButton image = dialog.findViewById(R.id.imageRadio);
                //RadioButton text = dialog.findViewById(R.id.textRadio);

                final RadioButton image = dialog.findViewById(R.id.imageRadio);
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean checked = ((RadioButton) v).isChecked();
                        // Check which radiobutton was pressed
                        if (checked){
                            // Do your coding
                            showImageLay = image.getText().toString();
                            dialog.dismiss();
                        }
                        else{
                            // Do your coding
                        }
                    }
                });

                final RadioButton text = dialog.findViewById(R.id.textRadio);
                text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean checked = ((RadioButton) v).isChecked();
                        // Check which radiobutton was pressed
                        if (checked){
                            // Do your coding
                            showTextLay = text.getText().toString();
                            dialog.dismiss();
                        }
                        else{
                            // Do your coding
                        }
                    }
                });

                dialog.show();
            }
        });

        activityBinding.cameraUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraIntent();
            }
        });

        activityBinding.galleryUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (showTextLay.equalsIgnoreCase("PDF"))
                {
                    Intent intent = new Intent();
                    intent.setType("application/pdf*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);//
                    startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
                }
                else
                {
                    galleryIntent();
                }
            }
        });

        /*activityBinding.uploadAssessment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (showTextLay.equalsIgnoreCase("PDF"))
                {
                    Intent intent = new Intent();
                    intent.setType("application/pdf*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);//
                    startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
                }
                else
                {
                    selectImage();
                }
            }
        });*/

        activityBinding.uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Connectivity.isConnected(UploadAssessmentActivity.this)) {

                    if (activityBinding.uploadPhotoTextAssess.getText().toString().length() == 0)
                    {
                        Toasty.error(UploadAssessmentActivity.this,"Type something field couldn't be empty",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        CallUpdateApi(UploadAssessmentActivity.this);
                    }

                } else {

                    //show net not connected error
                    Toasty.warning(UploadAssessmentActivity.this, getString(R.string.net_disconnected), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void selectImage() {
        activityBinding.cameraUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraIntent();
            }
        });

        /*final CharSequence[] items = {getString(R.string.takephoto), getString(R.string.fromgallery),
                getString(R.string.cancle)};
        AlertDialog.Builder builder = new AlertDialog.Builder(UploadAssessmentActivity.this);
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
        builder.show();*/
    }


    private void galleryIntent() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
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
        imageUri = UploadAssessmentActivity.this.getContentResolver().insert(
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

        String textSubmit = activityBinding.uploadPhotoTextAssess.getText().toString();

        ProgressDialog.showDialog(mcontext);
        RestApi mRestApi = RestClient.getClient(mcontext).create(RestApi.class);
        Map<String, RequestBody> mparam = new HashMap<String, RequestBody>();

        mparam.put("userId", createRequestBody(userId));
        mparam.put("assesmentId", createRequestBody(assessmentId));
        mparam.put("submission_date", createRequestBody(date));
        mparam.put("text", createRequestBody(textSubmit));
        mparam.put("contentType", createRequestBody(contentType));
        //mparam.put("zipCode", createRequestBody(zipcode));

        MultipartBody.Part imagenPerfil = null;
        RequestBody requestFile = null;
        if (imagepath.length() > 0) {
            //show blank username alert
            File file = new File(imagepath);
            requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            //MultipartBody.Part is used to send also the actual file name
            imagenPerfil = MultipartBody.Part.createFormData("photo", file.getName(), requestFile);
        }

        Observable<uploadModel> mdata = mRestApi.uploadAssessment(mparam, imagenPerfil);
        mdata.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<uploadModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(uploadModel value) {
                        ProgressDialog.hideDialog();
                        if (value != null && value.getStatus() == 200) {
                            Toasty.success(UploadAssessmentActivity.this, value.getMessage(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(UploadAssessmentActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();

                        }
                        else if (value != null)
                        {
                            Toasty.error(UploadAssessmentActivity.this, value.getMessage(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(UploadAssessmentActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        // display an error message
                        Log.e(TAG, "onError: " + e.getMessage());
                        ProgressDialog.hideDialog();
                        //Toasty.success(UploadAssessmentActivity.this, "Assessment Uploaded Successfully"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        /*Intent intent = new Intent(UploadAssessmentActivity.this,AssessmentActivity.class);
                        startActivity(intent);
                        finish();*/
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

            if (resultCode == UploadAssessmentActivity.this.RESULT_OK && imageUri != null) {
                imagepath = convertImageUriToFile(imageUri, UploadAssessmentActivity.this);

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

                    activityBinding.cameraUpload.setImageBitmap(rotatedBitmap);

                    activityBinding.cameraUpload.setVisibility(View.VISIBLE);

                    System.gc();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } else if (resultCode == UploadAssessmentActivity.this.RESULT_CANCELED) {

                Toast.makeText(UploadAssessmentActivity.this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(UploadAssessmentActivity.this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == SELECT_FILE) {

            try {
                imageUri = data.getData();
            }
            catch (Exception e)
            {
                Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
            }

            imagepath = ImageFilePath.getPath(UploadAssessmentActivity.this, data.getData());

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

                    activityBinding.galleryUpload.setImageBitmap(rotatedBitmap);
                    activityBinding.galleryUpload.setVisibility(View.VISIBLE);

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
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        // Return Captured Image ImageID ( By this ImageID Image will load from sdcard )

        return Path;
    }


}