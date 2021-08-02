package com.schoollms.View;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.schoollms.Adapter.AssessmentAdapter;
import com.schoollms.Adapter.ShowRemarkInfoAdapter;
import com.schoollms.GsonModel.Assessment.AssessmentModel;
import com.schoollms.GsonModel.Assessment.DatumAssess;
import com.schoollms.R;
import com.schoollms.WebService.RestApi;
import com.schoollms.WebService.RestClient;
import com.schoollms.interfaces.OnclickListener;
import com.schoollms.utility.Connectivity;
import com.schoollms.utility.GridSpacingItemDecoration;
import com.schoollms.utility.ProgressDialog;
import com.schoollms.utility.SharePrefs;
import com.schoollms.utility.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AssessmentActivity extends AppCompatActivity implements OnclickListener {

    private static final String TAG = AssessmentActivity.class.getSimpleName();

    ImageView header;
    TextView headerText, noAssess;
    Button view;
    RecyclerView listAssess;
    List<DatumAssess> mlist = new ArrayList<>();
    List<DatumAssess> mAssess = new ArrayList<>();
    AssessmentAdapter adapter;
    ShowRemarkInfoAdapter sAdapter;
    String courseId, subject, name;
    LinearLayout pending, complete, all, newLay;

    Integer flagAll=0,flagNew=0,flagSubmit=0,flagComplete=0;

    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment);

        header = findViewById(R.id.iv_back);
        view = findViewById(R.id.assess_view);
        headerText = findViewById(R.id.tv_header);
        listAssess = findViewById(R.id.assessment_list);
        noAssess = findViewById(R.id.assessment_gone);
        pending = findViewById(R.id.layPending);
        complete = findViewById(R.id.layComplete);
        all = findViewById(R.id.layAll);
        newLay = findViewById(R.id.layNew);

        calendar = Calendar.getInstance();

        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        date = dateFormat.format(calendar.getTime());

        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        courseId = getIntent().getStringExtra("class_id");
        subject = getIntent().getStringExtra("subjectId");

        headerText.setText("Assessment");

        listAssess.setHasFixedSize(true);
        listAssess.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.margin_15);
        listAssess.addItemDecoration(new GridSpacingItemDecoration(1, spacingInPixels, false));


        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                all.setBackgroundColor(getResources().getColor(R.color.light_gray));
                pending.setBackgroundColor(getResources().getColor(R.color.white));
                complete.setBackgroundColor(getResources().getColor(R.color.white));
                newLay.setBackgroundColor(getResources().getColor(R.color.white));
                flagComplete = 1;
                flagSubmit = 0;
                flagAll = 0;
                flagNew = 0;

                if (Connectivity.isConnected(AssessmentActivity.this)) {
                    callAssessmentPending();
                } else {
                    //show net not connected error
                    Toasty.warning(AssessmentActivity.this, getString(R.string.net_disconnected), Toast.LENGTH_SHORT).show();
                }
            }
        });

        pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                all.setBackgroundColor(getResources().getColor(R.color.white));
                pending.setBackgroundColor(getResources().getColor(R.color.light_gray));
                complete.setBackgroundColor(getResources().getColor(R.color.white));
                newLay.setBackgroundColor(getResources().getColor(R.color.white));
                flagNew = 1;
                flagComplete = 0;
                flagSubmit = 0;
                flagAll = 0;

                if (Connectivity.isConnected(AssessmentActivity.this)) {

                    callAssessment();
                } else {
                    //show net not connected error
                    Toasty.warning(AssessmentActivity.this, getString(R.string.net_disconnected), Toast.LENGTH_SHORT).show();
                }
            }
        });

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                all.setBackgroundColor(getResources().getColor(R.color.white));
                pending.setBackgroundColor(getResources().getColor(R.color.white));
                complete.setBackgroundColor(getResources().getColor(R.color.light_gray));
                newLay.setBackgroundColor(getResources().getColor(R.color.white));
                flagSubmit = 1;
                flagNew = 0;
                flagComplete = 0;
                flagAll = 0;

                if (Connectivity.isConnected(AssessmentActivity.this)) {

                    callAssessmentComplete();
                } else {
                    //show net not connected error
                    Toasty.warning(AssessmentActivity.this, getString(R.string.net_disconnected), Toast.LENGTH_SHORT).show();
                }
            }
        });

        newLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newLay.setBackgroundColor(getResources().getColor(R.color.light_gray));
                complete.setBackgroundColor(getResources().getColor(R.color.white));
                pending.setBackgroundColor(getResources().getColor(R.color.white));
                all.setBackgroundColor(getResources().getColor(R.color.white));
                flagSubmit = 0;
                flagNew = 0;
                flagComplete = 0;
                flagAll = 1;

                if (Connectivity.isConnected(AssessmentActivity.this)) {

                    callAllAssessment();
                } else {
                    //show net not connected error
                    Toasty.warning(AssessmentActivity.this, getString(R.string.net_disconnected), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void callAssessment() {

        ProgressDialog.showDialog(AssessmentActivity.this);
        RestApi mRestApi = RestClient.getClient(getApplicationContext()).create(RestApi.class);

        Observable<AssessmentModel> mdata = mRestApi.assessment("1", courseId, subject);
        mdata.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AssessmentModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(AssessmentModel value) {
                        ProgressDialog.hideDialog();
                        if (value != null)
                            Utils.Relogin(value.getStatus(), getApplicationContext());

                        if (value != null && value.getStatus() == 200) {
                            mAssess.clear();
                            listAssess.setVisibility(View.VISIBLE);
                            noAssess.setVisibility(View.GONE);
                            mlist = value.getData();

                            for (int j = 0; j < mlist.size(); j++) {

                                String[] dateCome = mlist.get(j).getDate().split("T");

                                if (dateCome[0].equalsIgnoreCase(date.toString())) {
                                    //mAssess.clear();
                                    mAssess.add(value.getData().get(j));
                                }
                                /*for (int i = 0; i< mlist.get(j).getAssesInfo().size(); i++) {

                                    if (mlist.get(j).getAssesInfo().get(i).getUserId().contains(userId))
                                    {
                                        //mAssess.clear();
                                        mAssess.add(value.getData().get(j));
                                    }
                                }*/
                            }
                            adapter = new AssessmentAdapter(AssessmentActivity.this, mAssess, AssessmentActivity.this);
                            listAssess.setAdapter(adapter);

                            /*if(mlist!=null)
                            {
                                adapter=new AssessmentAdapter(AssessmentActivity.this,mlist,AssessmentActivity.this);
                                listAssess.setAdapter(adapter);
                            }*/
                        } else if (value.getStatus() == 401) {
                            Toasty.error(AssessmentActivity.this, value.getMessage(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AssessmentActivity.this, LoginActivity.class);
                            startActivity(intent);
                            AssessmentActivity.this.finish();
                        } else {
                            listAssess.setVisibility(View.GONE);
                            noAssess.setVisibility(View.VISIBLE);
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

    private void callAssessmentComplete() {

        ProgressDialog.showDialog(AssessmentActivity.this);
        RestApi mRestApi = RestClient.getClient(getApplicationContext()).create(RestApi.class);

        final String userId = SharePrefs.getUserdetail(AssessmentActivity.this).getUser().getId();

        Observable<AssessmentModel> mdata = mRestApi.assessment("1", courseId, subject);
        mdata.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AssessmentModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(AssessmentModel value) {
                        ProgressDialog.hideDialog();
                        if (value != null)
                            Utils.Relogin(value.getStatus(), getApplicationContext());

                        if (value != null && value.getStatus() == 200) {
                            mAssess.clear();
                            listAssess.setVisibility(View.VISIBLE);
                            noAssess.setVisibility(View.GONE);
                            mlist = value.getData();

                            for (int j = 0; j < mlist.size(); j++) {
                                for (int i = 0; i < mlist.get(j).getAssesInfo().size(); i++) {

                                    if (mlist.get(j).getAssesInfo().get(i).getUserId().contains(userId)) {
                                        //mAssess.clear();
                                        mAssess.add(value.getData().get(j));
                                    }
                                }
                            }

                            adapter = new AssessmentAdapter(AssessmentActivity.this, mAssess, AssessmentActivity.this);
                            listAssess.setAdapter(adapter);
                        } else if (value.getStatus() == 401) {
                            Toasty.error(AssessmentActivity.this, value.getMessage(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AssessmentActivity.this, LoginActivity.class);
                            startActivity(intent);
                            AssessmentActivity.this.finish();
                        } else {
                            listAssess.setVisibility(View.GONE);
                            noAssess.setVisibility(View.VISIBLE);
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

    private void callAssessmentPending() {

        ProgressDialog.showDialog(AssessmentActivity.this);
        RestApi mRestApi = RestClient.getClient(getApplicationContext()).create(RestApi.class);

        final String userId = SharePrefs.getUserdetail(AssessmentActivity.this).getUser().getId();

        final Observable<AssessmentModel> mdata = mRestApi.assessment("1", courseId, subject);
        mdata.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AssessmentModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(AssessmentModel value) {
                        ProgressDialog.hideDialog();
                        if (value != null)
                            Utils.Relogin(value.getStatus(), getApplicationContext());

                        if (value != null && value.getStatus() == 200) {
                            mAssess.clear();
                            listAssess.setVisibility(View.VISIBLE);
                            noAssess.setVisibility(View.GONE);
                            mlist = value.getData();

                            for (int j = 0; j < mlist.size(); j++) {
                                for (int i = 0; i < mlist.get(j).getRemarksInfo().size(); i++) {

                                    if (mlist.get(j).getRemarksInfo().get(i).getAssesmentId().contains(mlist.get(j).getAssesInfo().get(i).getAssesmentId())) {
                                        mAssess.add(value.getData().get(j));
                                    }
                                }
                            }

                            adapter = new AssessmentAdapter(AssessmentActivity.this, mAssess, AssessmentActivity.this);
                            listAssess.setAdapter(adapter);
                        } else if (value.getStatus() == 401) {
                            Toasty.error(AssessmentActivity.this, value.getMessage(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AssessmentActivity.this, LoginActivity.class);
                            startActivity(intent);
                            AssessmentActivity.this.finish();
                        } else {
                            listAssess.setVisibility(View.GONE);
                            noAssess.setVisibility(View.VISIBLE);
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

    private void callAllAssessment() {

        ProgressDialog.showDialog(AssessmentActivity.this);
        RestApi mRestApi = RestClient.getClient(getApplicationContext()).create(RestApi.class);

        Observable<AssessmentModel> mdata = mRestApi.assessment("1", courseId, subject);
        mdata.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AssessmentModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(AssessmentModel value) {
                        ProgressDialog.hideDialog();
                        if (value != null)
                            Utils.Relogin(value.getStatus(), getApplicationContext());

                        if (value != null && value.getStatus() == 200) {
                            ProgressDialog.hideDialog();
                            //mAssess.clear();
                            listAssess.setVisibility(View.VISIBLE);
                            noAssess.setVisibility(View.GONE);
                            mlist = value.getData();

                            adapter = new AssessmentAdapter(AssessmentActivity.this, mlist, AssessmentActivity.this);
                            listAssess.setAdapter(adapter);

                        }
                        else if (value.getStatus() == 401) {
                            ProgressDialog.hideDialog();
                            Toasty.error(AssessmentActivity.this, value.getMessage(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AssessmentActivity.this, LoginActivity.class);
                            startActivity(intent);
                            AssessmentActivity.this.finish();
                        }
                        else {
                            ProgressDialog.hideDialog();
                            listAssess.setVisibility(View.GONE);
                            noAssess.setVisibility(View.VISIBLE);
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
                        ProgressDialog.hideDialog();
                        Log.e(TAG, "onComplete: All Done!");
                    }
                });

    }

    @Override
    public void onResume()
    {
        super.onResume();
        flagAll=1;
        flagNew=0;
        flagSubmit=0;
        flagComplete=0;
        //Toast.makeText(this, "all"+flagAll, Toast.LENGTH_SHORT).show();
        callAllAssessment();
    }

    @Override
    public void onBackPressed() {
        AssessmentActivity.this.finish();
    }

    @Override
    public void OnItemclick(int pos) {

        if (flagAll==1)
        {
            if (mlist != null && mlist.size() > pos && mlist.get(pos).getRemarksInfo().size() != 0 && mlist.get(pos).getAssesInfo().size() != 0) {
                Intent intent = new Intent(this, SubmitAssessmentActivity.class);
                intent.putExtra("Assessment_Id", mlist.get(pos).getId());
                intent.putExtra("Assessment_Topic", mlist.get(pos).getTopicName());
                intent.putExtra("Assessment_Subject", mlist.get(pos).getSubjectName());
                intent.putExtra("Assessment_Content", mlist.get(pos).getContentType());
                intent.putExtra("Assessment_Photo", mlist.get(pos).getPhoto());
                intent.putExtra("Assessment_Pdf", mlist.get(pos).getPdf());
                intent.putExtra("Assessment_Submission", mlist.get(pos).getSubmission());
                intent.putExtra("Assessment_Detail", mlist.get(pos).getDescription());
                intent.putExtra("Assessment_Text", mlist.get(pos).getText());
                intent.putExtra("Assessment_Class", mlist.get(pos).getCourseName());
                intent.putParcelableArrayListExtra("Assessment_list", (ArrayList<? extends Parcelable>) mlist.get(pos).getAssesInfo());
                intent.putExtra("Assessment_list_id", mlist.get(pos).getAssesInfo().get(0).getId());
                intent.putParcelableArrayListExtra("Assessment_list_remark", (ArrayList<? extends Parcelable>) mlist.get(pos).getRemarksInfo());
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(this, SubmitAssessmentActivity.class);
                intent.putExtra("Assessment_Id", mlist.get(pos).getId());
                intent.putExtra("Assessment_Topic", mlist.get(pos).getTopicName());
                intent.putExtra("Assessment_Subject", mlist.get(pos).getSubjectName());
                intent.putExtra("Assessment_Content", mlist.get(pos).getContentType());
                intent.putExtra("Assessment_Photo", mlist.get(pos).getPhoto());
                intent.putExtra("Assessment_Pdf", mlist.get(pos).getPdf());
                intent.putExtra("Assessment_Submission", mlist.get(pos).getSubmission());
                intent.putExtra("Assessment_Detail", mlist.get(pos).getDescription());
                intent.putExtra("Assessment_Text", mlist.get(pos).getText());
                intent.putExtra("Assessment_Class", mlist.get(pos).getCourseName());
                intent.putParcelableArrayListExtra("Assessment_list", (ArrayList<? extends Parcelable>) mlist.get(pos).getAssesInfo());
                //intent.putExtra("Assessment_list_id",mlist.get(pos).getAssesInfo().get(0).getId());
                intent.putParcelableArrayListExtra("Assessment_list_remark", (ArrayList<? extends Parcelable>) mlist.get(pos).getRemarksInfo());
                startActivity(intent);
            }
            //Toast.makeText(this, "All", Toast.LENGTH_SHORT).show();
        }

        else if (flagComplete == 1)
        {
            if (mAssess != null && mAssess.size() > pos && mAssess.get(pos).getRemarksInfo().size() != 0 && mAssess.get(pos).getAssesInfo().size() != 0) {
                Intent intent = new Intent(this, SubmitAssessmentActivity.class);
                intent.putExtra("Assessment_Id", mAssess.get(pos).getId());
                intent.putExtra("Assessment_Topic", mAssess.get(pos).getTopicName());
                intent.putExtra("Assessment_Subject", mAssess.get(pos).getSubjectName());
                intent.putExtra("Assessment_Content", mAssess.get(pos).getContentType());
                intent.putExtra("Assessment_Photo", mAssess.get(pos).getPhoto());
                intent.putExtra("Assessment_Pdf", mAssess.get(pos).getPdf());
                intent.putExtra("Assessment_Submission", mAssess.get(pos).getSubmission());
                intent.putExtra("Assessment_Detail", mAssess.get(pos).getDescription());
                intent.putExtra("Assessment_Text", mAssess.get(pos).getText());
                intent.putExtra("Assessment_Class", mAssess.get(pos).getCourseName());
                intent.putParcelableArrayListExtra("Assessment_list", (ArrayList<? extends Parcelable>) mAssess.get(pos).getAssesInfo());
                intent.putExtra("Assessment_list_id", mAssess.get(pos).getAssesInfo().get(0).getId());
                intent.putParcelableArrayListExtra("Assessment_list_remark", (ArrayList<? extends Parcelable>) mAssess.get(pos).getRemarksInfo());
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(this, SubmitAssessmentActivity.class);
                intent.putExtra("Assessment_Id", mAssess.get(pos).getId());
                intent.putExtra("Assessment_Topic", mAssess.get(pos).getTopicName());
                intent.putExtra("Assessment_Subject", mAssess.get(pos).getSubjectName());
                intent.putExtra("Assessment_Content", mAssess.get(pos).getContentType());
                intent.putExtra("Assessment_Photo", mAssess.get(pos).getPhoto());
                intent.putExtra("Assessment_Pdf", mAssess.get(pos).getPdf());
                intent.putExtra("Assessment_Submission", mAssess.get(pos).getSubmission());
                intent.putExtra("Assessment_Detail", mAssess.get(pos).getDescription());
                intent.putExtra("Assessment_Text", mAssess.get(pos).getText());
                intent.putExtra("Assessment_Class", mAssess.get(pos).getCourseName());
                intent.putParcelableArrayListExtra("Assessment_list", (ArrayList<? extends Parcelable>) mAssess.get(pos).getAssesInfo());
                //intent.putExtra("Assessment_list_id",mlist.get(pos).getAssesInfo().get(0).getId());
                intent.putParcelableArrayListExtra("Assessment_list_remark", (ArrayList<? extends Parcelable>) mAssess.get(pos).getRemarksInfo());
                startActivity(intent);
            }
            //Toast.makeText(this, "Complete", Toast.LENGTH_SHORT).show();
        }

        else if (flagSubmit == 1)
        {
            if (mAssess != null && mAssess.size() > pos && mAssess.get(pos).getRemarksInfo().size() != 0 && mAssess.get(pos).getAssesInfo().size() != 0) {
                Intent intent = new Intent(this, SubmitAssessmentActivity.class);
                intent.putExtra("Assessment_Id", mAssess.get(pos).getId());
                intent.putExtra("Assessment_Topic", mAssess.get(pos).getTopicName());
                intent.putExtra("Assessment_Subject", mAssess.get(pos).getSubjectName());
                intent.putExtra("Assessment_Content", mAssess.get(pos).getContentType());
                intent.putExtra("Assessment_Photo", mAssess.get(pos).getPhoto());
                intent.putExtra("Assessment_Pdf", mAssess.get(pos).getPdf());
                intent.putExtra("Assessment_Submission", mAssess.get(pos).getSubmission());
                intent.putExtra("Assessment_Detail", mAssess.get(pos).getDescription());
                intent.putExtra("Assessment_Text", mAssess.get(pos).getText());
                intent.putExtra("Assessment_Class", mAssess.get(pos).getCourseName());
                intent.putParcelableArrayListExtra("Assessment_list", (ArrayList<? extends Parcelable>) mAssess.get(pos).getAssesInfo());
                intent.putExtra("Assessment_list_id", mAssess.get(pos).getAssesInfo().get(0).getId());
                intent.putParcelableArrayListExtra("Assessment_list_remark", (ArrayList<? extends Parcelable>) mAssess.get(pos).getRemarksInfo());
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(this, SubmitAssessmentActivity.class);
                intent.putExtra("Assessment_Id", mAssess.get(pos).getId());
                intent.putExtra("Assessment_Topic", mAssess.get(pos).getTopicName());
                intent.putExtra("Assessment_Subject", mAssess.get(pos).getSubjectName());
                intent.putExtra("Assessment_Content", mAssess.get(pos).getContentType());
                intent.putExtra("Assessment_Photo", mAssess.get(pos).getPhoto());
                intent.putExtra("Assessment_Pdf", mAssess.get(pos).getPdf());
                intent.putExtra("Assessment_Submission", mAssess.get(pos).getSubmission());
                intent.putExtra("Assessment_Detail", mAssess.get(pos).getDescription());
                intent.putExtra("Assessment_Text", mAssess.get(pos).getText());
                intent.putExtra("Assessment_Class", mAssess.get(pos).getCourseName());
                intent.putParcelableArrayListExtra("Assessment_list", (ArrayList<? extends Parcelable>) mAssess.get(pos).getAssesInfo());
                //intent.putExtra("Assessment_list_id",mlist.get(pos).getAssesInfo().get(0).getId());
                intent.putParcelableArrayListExtra("Assessment_list_remark", (ArrayList<? extends Parcelable>) mAssess.get(pos).getRemarksInfo());
                startActivity(intent);
            }
            //Toast.makeText(this, "Submit", Toast.LENGTH_SHORT).show();
        }

        else if (flagNew == 1)
        {
            if (mAssess != null && mAssess.size() > pos && mAssess.get(pos).getRemarksInfo().size() != 0 && mAssess.get(pos).getAssesInfo().size() != 0) {
                Intent intent = new Intent(this, SubmitAssessmentActivity.class);
                intent.putExtra("Assessment_Id", mAssess.get(pos).getId());
                intent.putExtra("Assessment_Topic", mAssess.get(pos).getTopicName());
                intent.putExtra("Assessment_Subject", mAssess.get(pos).getSubjectName());
                intent.putExtra("Assessment_Content", mAssess.get(pos).getContentType());
                intent.putExtra("Assessment_Photo", mAssess.get(pos).getPhoto());
                intent.putExtra("Assessment_Pdf", mAssess.get(pos).getPdf());
                intent.putExtra("Assessment_Submission", mAssess.get(pos).getSubmission());
                intent.putExtra("Assessment_Detail", mAssess.get(pos).getDescription());
                intent.putExtra("Assessment_Text", mAssess.get(pos).getText());
                intent.putExtra("Assessment_Class", mAssess.get(pos).getCourseName());
                intent.putParcelableArrayListExtra("Assessment_list", (ArrayList<? extends Parcelable>) mAssess.get(pos).getAssesInfo());
                intent.putExtra("Assessment_list_id", mAssess.get(pos).getAssesInfo().get(0).getId());
                intent.putParcelableArrayListExtra("Assessment_list_remark", (ArrayList<? extends Parcelable>) mAssess.get(pos).getRemarksInfo());
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(this, SubmitAssessmentActivity.class);
                intent.putExtra("Assessment_Id", mAssess.get(pos).getId());
                intent.putExtra("Assessment_Topic", mAssess.get(pos).getTopicName());
                intent.putExtra("Assessment_Subject", mAssess.get(pos).getSubjectName());
                intent.putExtra("Assessment_Content", mAssess.get(pos).getContentType());
                intent.putExtra("Assessment_Photo", mAssess.get(pos).getPhoto());
                intent.putExtra("Assessment_Pdf", mAssess.get(pos).getPdf());
                intent.putExtra("Assessment_Submission", mAssess.get(pos).getSubmission());
                intent.putExtra("Assessment_Detail", mAssess.get(pos).getDescription());
                intent.putExtra("Assessment_Text", mAssess.get(pos).getText());
                intent.putExtra("Assessment_Class", mAssess.get(pos).getCourseName());
                intent.putParcelableArrayListExtra("Assessment_list", (ArrayList<? extends Parcelable>) mAssess.get(pos).getAssesInfo());
                //intent.putExtra("Assessment_list_id",mlist.get(pos).getAssesInfo().get(0).getId());
                intent.putParcelableArrayListExtra("Assessment_list_remark", (ArrayList<? extends Parcelable>) mAssess.get(pos).getRemarksInfo());
                startActivity(intent);
            }
            //Toast.makeText(this, "New", Toast.LENGTH_SHORT).show();
        }

        else
        {
            //
        }
    }

    @Override
    public void OnDesItemclick(int pos) {

    }
}