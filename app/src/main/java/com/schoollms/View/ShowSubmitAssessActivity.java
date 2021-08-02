package com.schoollms.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.schoollms.Adapter.ShowSubmitAssessAdapter;
import com.schoollms.GsonModel.ShowAssess.AssessInfo;
import com.schoollms.GsonModel.ShowAssess.DatumAssessShow;
import com.schoollms.GsonModel.ShowAssess.ShowAssessModel;
import com.schoollms.R;
import com.schoollms.WebService.RestApi;
import com.schoollms.WebService.RestClient;
import com.schoollms.interfaces.OnclickListener;
import com.schoollms.utility.Connectivity;
import com.schoollms.utility.GridSpacingItemDecoration;
import com.schoollms.utility.ProgressDialog;
import com.schoollms.utility.Utils;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ShowSubmitAssessActivity extends AppCompatActivity implements OnclickListener {

    private static final String TAG = ShowSubmitAssessActivity.class.getSimpleName();

    ImageView header;
    TextView headerText,noAssess;
    Button view;
    RecyclerView listAssess;
    List<DatumAssessShow> mlist;
    List<AssessInfo> mAssess;
    ShowSubmitAssessAdapter adapter;
    String courseId,subject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_submit_assess);

        header = findViewById(R.id.iv_back);
        view = findViewById(R.id.assess_view);
        headerText = findViewById(R.id.tv_header);
        listAssess = findViewById(R.id.show_assessment_list);
        noAssess = findViewById(R.id.show_assessment_gone);

        headerText.setText("Assessment List");

        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        courseId = getIntent().getStringExtra("assessment_id");
        Toast.makeText(this, ""+courseId, Toast.LENGTH_LONG).show();
        subject = getIntent().getStringExtra("user_id");

        listAssess.setHasFixedSize(true);
        listAssess.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.margin_15);
        listAssess.addItemDecoration(new GridSpacingItemDecoration(1,spacingInPixels,false));


        if (Connectivity.isConnected(ShowSubmitAssessActivity.this)) {
            //callAssessment();
        }
        else {
            //show net not connected error
            Toasty.warning(ShowSubmitAssessActivity.this, getString(R.string.net_disconnected), Toast.LENGTH_SHORT).show();
        }

    }

    private void callAssessment() {

        ProgressDialog.showDialog(ShowSubmitAssessActivity.this);
        RestApi mRestApi = RestClient.getClient(getApplicationContext()).create(RestApi.class);

        Observable<ShowAssessModel> mdata = mRestApi.showAssessment(courseId,subject);
        mdata.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ShowAssessModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(ShowAssessModel value) {
                        ProgressDialog.hideDialog();
                        if(value!=null)
                            Utils.Relogin(value.getStatus(),getApplicationContext());

                        if (value != null && value.getStatus() == 200)
                        {
                            listAssess.setVisibility(View.VISIBLE);
                            noAssess.setVisibility(View.GONE);
                            if(mlist!=null)
                            {
                                adapter= new ShowSubmitAssessAdapter(ShowSubmitAssessActivity.this,mlist,ShowSubmitAssessActivity.this);
                                listAssess.setAdapter(adapter);
                            }
                        }
                        else if (value.getStatus() == 401)
                        {
                            Toasty.error(ShowSubmitAssessActivity.this, value.getMessage(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ShowSubmitAssessActivity.this,LoginActivity.class);
                            startActivity(intent);
                            ShowSubmitAssessActivity.this.finish();
                        }

                        else
                        {
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


    @Override
    public void onBackPressed() {
        ShowSubmitAssessActivity.this.finish();
    }

    @Override
    public void OnItemclick(int pos) {
        if (mlist != null && mlist.size() > pos) {
            Intent intent = new Intent(this, AssessmentListShowActivity.class);
            intent.putExtra("Assessment_Id",mlist.get(pos).getId());
            intent.putExtra("Assessment_Topic",mlist.get(pos).getDate());
            //intent.putExtra("Assessment_Subject",mlist.get(pos).getSubjectName());
            intent.putExtra("Assessment_Content",mlist.get(pos).getContentType());
            intent.putExtra("Assessment_Photo",mlist.get(pos).getPhoto());
            intent.putExtra("Assessment_Pdf",mlist.get(pos).getPdf());
            intent.putExtra("Assessment_Submission",mlist.get(pos).getDate());
            //intent.putExtra("Assessment_Detail",mlist.get(pos).getDescription());
            intent.putExtra("Assessment_Text", mlist.get(pos).getText());
            //intent.putExtra("Assessment_Class", mlist.get(pos).getCourseName());
            intent.putParcelableArrayListExtra("Assessment_list", (ArrayList<? extends Parcelable>) mlist.get(pos).getRemarksInfo());
            startActivity(intent);
        }
    }

    @Override
    public void OnDesItemclick(int pos) {

    }

}