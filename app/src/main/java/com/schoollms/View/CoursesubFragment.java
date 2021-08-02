package com.schoollms.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.schoollms.Adapter.CoursesublistAdapter;
import com.schoollms.GsonModel.CourseDatum;
import com.schoollms.GsonModel.UserCourse.Datum;
import com.schoollms.GsonModel.UsercourseAdd.AddUsercourseModel;
import com.schoollms.GsonModel.coursedetail.CouresubModel;
import com.schoollms.R;
import com.schoollms.WebService.Constant_Tag;
import com.schoollms.WebService.RestApi;
import com.schoollms.WebService.RestClient;

import com.schoollms.databinding.FragmentCoursesubBinding;
import com.schoollms.interfaces.OnclickListener;
import com.schoollms.utility.Connectivity;
import com.schoollms.utility.FragmentTask;
import com.schoollms.utility.GridSpacingItemDecoration;
import com.schoollms.utility.ProgressDialog;
import com.schoollms.utility.SharePrefs;
import com.schoollms.utility.ThemeClass;
import com.schoollms.utility.Utils;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CoursesubFragment extends Fragment implements OnclickListener {
    private FragmentCoursesubBinding mbinding;
    private String TAG = CoursesubFragment.class.getSimpleName();
    CourseDatum mCourseDatum;
    CouresubModel mCouresubModel;
    Datum mCourseDatumUser;
    CoursesublistAdapter mCoursesublistAdapter;

    public static CoursesubFragment newInstance(CourseDatum mCourseDatum)
    {
        CoursesubFragment fragment = new CoursesubFragment();
        Bundle b = new Bundle();
        b.putParcelable(Constant_Tag.COURSEDATUM, mCourseDatum);
        fragment.setArguments(b);
        return fragment;
    }

    public static CoursesubFragment newInstance(Datum mCourseDatum) {
        CoursesubFragment fragment = new CoursesubFragment();
        Bundle b = new Bundle();
        b.putParcelable(Constant_Tag.COURSEDATUM_User, mCourseDatum);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();

        if (b != null) {
            if (b.containsKey(Constant_Tag.COURSEDATUM_User)) {
                mCourseDatumUser = b.getParcelable(Constant_Tag.COURSEDATUM_User);
            } else {
                mCourseDatum = b.getParcelable(Constant_Tag.COURSEDATUM);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_coursesub, container, false);
        View rootView = mbinding.getRoot();
        ThemeClass.changeHeaderColor(mbinding.llHeader, getActivity());

        TextView tv_header = (TextView) mbinding.llHeader.findViewById(R.id.tv_header);
        tv_header.setText(getString(R.string.coursedetail));
        LinearLayout ll_back = (LinearLayout) mbinding.llHeader.findViewById(R.id.ll_back);
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        ll_back.setVisibility(View.VISIBLE);

        mbinding.rvItem.setHasFixedSize(true);
        mbinding.rvItem.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.margin_15);
        mbinding.rvItem.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, false));

        mbinding.zoomLiveClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),ZoomLiveClass.class);
                intent.putExtra("paramZoom","zoom");
                intent.putExtra("class_id",SharePrefs.getLocale(getActivity()));
                intent.putExtra("inside","inside");
                startActivity(intent);
            }
        });

        mbinding.youtubeLiveClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),LiveClassActivity.class);
                intent.putExtra("paramYou","youtube");
                intent.putExtra("class_id",SharePrefs.getLocale(getActivity()));
                intent.putExtra("inside","inside");
                startActivity(intent);
            }
        });

        mbinding.submitAssessment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),AssessmentActivity.class);
                intent.putExtra("class_id",SharePrefs.getLocale(getActivity()));
                startActivity(intent);
            }
        });

        if (mCourseDatum != null) {
            mbinding.tvTitle.setText(mCourseDatum.getName());
            Glide.with(getActivity()).load(mCourseDatum.getImage())
                    .into(mbinding.ivCover);

        } else if (mCourseDatumUser != null && mCourseDatumUser.getCourseDetail() != null) {
            mbinding.tvTitle.setText(mCourseDatumUser.getCourseDetail().getName());
            Glide.with(getActivity()).load(mCourseDatumUser.getCourseDetail().getImage())
                    .error(R.drawable.main_background_image)
                    .into(mbinding.ivCover);
        }

        mbinding.tvReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ShowReviews.class);
                if ((mCouresubModel != null)) {
                    intent.putExtra("course_id",mCourseDatumUser.getCourseDetail().getId());
                    intent.putExtra("class_name", mCourseDatumUser.getCourseDetail().getName());
                    intent.putExtra("class_reviews", mCourseDatumUser.getCourseDetail().getRating());
                }
                startActivity(intent);
            }
        });

        mbinding.tvDesciptiontitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mbinding.wvDesciption.getVisibility() == View.VISIBLE) {
                    mbinding.tvDesciptiontitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                    mbinding.wvDesciption.setVisibility(View.GONE);
                } else {
                    mbinding.tvDesciptiontitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);

                    mbinding.wvDesciption.setVisibility(View.VISIBLE);

                }
            }
        });

        //mbinding.tvAddfree.setTextColor(SharePrefs.getSetting(getActivity()).getThemeColor());

        if (Connectivity.isConnected(getActivity())) {
            getsubCourselist(getActivity());
        } else {
            //show net not connected error
            Toasty.warning(getActivity(), getString(R.string.net_disconnected), Toast.LENGTH_SHORT).show();
        }

        /*mbinding.tvAddfree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });
        mbinding.tvBuynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });*/

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mbinding.tvPrice.setCompoundDrawableTintList(ThemeClass.getcolorstate(getActivity()));
        }*/
        return rootView;
    }


    public void showAlertDialog() {        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_addfreebuynow, null);
        builder.setView(customLayout);
        final AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView tvimportentpoints = (TextView) customLayout.findViewById(R.id.tvimportantpoints);
        LinearLayout llayout = (LinearLayout) customLayout.findViewById(R.id.llayout);
        ImageView ivclose = (ImageView) customLayout.findViewById(R.id.ivclose);
        LinearLayout rlmain = (LinearLayout) customLayout.findViewById(R.id.rlmain);

        TextView tv_title = (TextView) customLayout.findViewById(R.id.tv_title);

        tv_title.setTextColor(SharePrefs.getSetting(getActivity()).getThemeColor());

        AppCompatButton btn_free = (AppCompatButton) customLayout.findViewById(R.id.btn_free);
        AppCompatButton btn_buynow = (AppCompatButton) customLayout.findViewById(R.id.btn_buynow);

        if (mCouresubModel.getUsersCourseAdded() == 1) {
            btn_free.setVisibility(View.GONE);
            btn_buynow.setVisibility(View.VISIBLE);
        } else {
            btn_free.setVisibility(View.VISIBLE);
            btn_buynow.setVisibility(View.VISIBLE);
        }

        tvimportentpoints.setText(Html.fromHtml(mCouresubModel.getImportantPoints()));

        llayout.getLayoutParams().width = (int) (displayRectangle.width() * 0.9f);
        llayout.getLayoutParams().height = (int) (displayRectangle.height() * 0.7f);

        llayout.requestLayout();

        ThemeClass.changeButtonColor(btn_free, getActivity());

        ivclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        btn_free.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (Connectivity.isConnected(getActivity())) {
                    callAddFreecourse(getActivity());
                } else {
                    //show net not connected error
                    Toasty.warning(getActivity(), getString(R.string.net_disconnected), Toast.LENGTH_SHORT).show();

                }
            }
        });

        btn_buynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if (mCourseDatum != null) {
                    Intent i = new Intent(getActivity(), CheckoutActivity.class);
                    Bundle b = new Bundle();
                    b.putString("courseid", mCourseDatum.getId());
                    b.putString("imagepath", mCourseDatum.getImage());
                    b.putString("title", mCourseDatum.getName());

                    i.putExtras(b);
                    startActivity(i);
                } else if (mCourseDatumUser != null && mCourseDatumUser.getCourseDetail() != null) {
                    Intent i = new Intent(getActivity(), CheckoutActivity.class);
                    Bundle b = new Bundle();
                    b.putString("courseid", mCourseDatumUser.getCourseId());
                    b.putString("imagepath", mCourseDatumUser.getCourseDetail().getImage());
                    b.putString("title", mCourseDatumUser.getCourseDetail().getName());

                    i.putExtras(b);
                    startActivity(i);
                }

            }

        });
        dialog.show();
    }


    private void callAddFreecourse(final Context context) {

        ProgressDialog.showDialog(context);
        RestApi mRestApi = RestClient.getClient(context).create(RestApi.class);
        HashMap<String, String> mparam = new HashMap<String, String>();
        mparam.put("userId", SharePrefs.getUserdetail(getActivity()).getUser().getId());

        if (mCourseDatum != null) {
            mparam.put(Constant_Tag.COURSEID, mCourseDatum.getId());
            mparam.put(Constant_Tag.CONTENTPLANTYPE, "free");

        } else if (mCourseDatumUser != null && mCourseDatumUser.getCourseDetail() != null) {

            mparam.put(Constant_Tag.COURSEID, mCourseDatumUser.getCourseId());
            mparam.put(Constant_Tag.CONTENTPLANTYPE, "free");

        }

        Observable<AddUsercourseModel> mdata = mRestApi.AddUsercourse(mparam);
        mdata.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AddUsercourseModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(AddUsercourseModel value) {
                        ProgressDialog.hideDialog();
                        Log.d("value",""+value.toString());
                        if (value != null && value.getStatus() == 200) {
                            Utils.hideKeyboard(getActivity());
                            Toasty.success(getActivity(), value.getMessage(), Toast.LENGTH_SHORT).show();
                            ((MainActivity) getActivity()).mainActivityBinding.navigation.setSelectedItemId(R.id.navigation_classroom);


                        } else if (value != null) {
                            //mDataListener.onError(value.getMessage());
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


    private void getsubCourselist(Context mcontext) {

        ProgressDialog.showDialog(mcontext);
        RestApi mRestApi = RestClient.getClient(mcontext).create(RestApi.class);
        String id = "";
        if (mCourseDatum != null) {
            id = mCourseDatum.getId();


        } else if (mCourseDatumUser != null && mCourseDatumUser.getCourseDetail() != null) {
            id = mCourseDatumUser.getCourseId();
        }

        Observable<CouresubModel> mdata = mRestApi.coursesublist(id);
        mdata.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CouresubModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CouresubModel value) {
                        ProgressDialog.hideDialog();
                        if (value != null)
                            Utils.Relogin(value.getStatus(), getActivity());

                        if (value != null && value.getStatus() == 1) {
                            mCouresubModel = value;
                            if (mCouresubModel != null) {
                                mCoursesublistAdapter = new CoursesublistAdapter(getActivity(), mCouresubModel.getSubjectData(), CoursesubFragment.this);
                                mbinding.rvItem.setAdapter(mCoursesublistAdapter);
                                mbinding.wvDesciption.loadData("<p align='justify'>" + mCouresubModel.getDescription() + "</p>", "text/html; charset=UTF-8", null);
                                mbinding.ratingUs.setText("("+String.valueOf(mCouresubModel.getRating())+")");

                                if (mCouresubModel.getUsersCourseAdded() == 1) {
                                    //mbinding.tvAddfree.setVisibility(View.GONE);
                                    //mbinding.tvBuynow.setVisibility(View.VISIBLE);
                                    //mbinding.viewline1.setVisibility(View.GONE);
                                    //mbinding.vline2.setVisibility(View.VISIBLE);
                                    mbinding.tvReview.setVisibility(View.VISIBLE);
                                }
                                else if (mCouresubModel.getUsersCourseAdded() == 2) {
                                    //mbinding.tvAddfree.setVisibility(View.GONE);
                                    //mbinding.tvBuynow.setVisibility(View.GONE);
                                    //mbinding.viewline1.setVisibility(View.GONE);
                                    //mbinding.vline2.setVisibility(View.GONE);
                                    mbinding.tvReview.setVisibility(View.VISIBLE);
                                }
                                else {
                                    //mbinding.tvAddfree.setVisibility(View.VISIBLE);
                                    //mbinding.tvBuynow.setVisibility(View.VISIBLE);
                                    //mbinding.viewline1.setVisibility(View.VISIBLE);
                                    //mbinding.vline2.setVisibility(View.VISIBLE);
                                    mbinding.tvReview.setVisibility(View.GONE);
                                }
                            }

                            if(mCouresubModel != null && mCouresubModel.getPlanData() != null && mCouresubModel.getPlanData().size() > 0) {
                                //mbinding.tvSubcription.setText(mCouresubModel.getPlanData().get(0).getDurationTime() + " " + mCouresubModel.getPlanData().get(0).getDurationType());
                                //mbinding.tvPrice.setText(mCouresubModel.getPlanData().get(0).getOfferPrice() + " /-");

                            }
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

    @Override
    public void OnItemclick(int pos) {

        if (mCouresubModel != null && mCouresubModel.getSubjectData() != null && mCouresubModel.getSubjectData().size() > pos) {
            FragmentTask.replaceFrgament(CoursevideolistFragment.newInstance(mCouresubModel.getSubjectData().get(pos), mCouresubModel.getUsersCourseAdded()), getActivity().getSupportFragmentManager(), R.id.main_framelayout);
        }

    }

    @Override
    public void OnDesItemclick(int pos) {

    }
}