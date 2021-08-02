package com.schoollms.View;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.schoollms.Adapter.UserCourseAdapter;
import com.schoollms.GsonModel.Advertisment.AdvertismentModel;
import com.schoollms.GsonModel.Notification.NotificationResponse;
import com.schoollms.GsonModel.UserCourse.Datum;
import com.schoollms.GsonModel.UserCourse.UsercourseModel;
import com.schoollms.R;
import com.schoollms.WebService.RestApi;
import com.schoollms.WebService.RestClient;
import com.schoollms.databinding.FragmentClassroomBinding;
import com.schoollms.databinding.FragmentCourselistBinding;
import com.schoollms.interfaces.OnclickListener;
import com.schoollms.utility.Connectivity;
import com.schoollms.utility.FragmentTask;
import com.schoollms.utility.GridSpacingItemDecoration;
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
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ClassRoomFragment extends Fragment implements OnclickListener {
    private FragmentClassroomBinding mbinding;
    private String TAG = ClassRoomFragment.class.getSimpleName();
    List<Datum> mlist;
    UserCourseAdapter adapter;

    String classUrl,id;

    public static ClassRoomFragment newInstance() {
        ClassRoomFragment fragment = new ClassRoomFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_classroom, container, false);
        View rootView = mbinding.getRoot();

        ThemeClass.changeHeaderColor(mbinding.llHeader, getActivity());
        ThemeClass.changeButtonColor(mbinding.btnTakecourse, getActivity());

        //ThemeClass.setAdvertisment(mbinding.ivAdverticementClassroom, getActivity(), "Class Room Page - No Courses", Utils.getDeviceWidth(getActivity()), (int) getResources().getDimension(R.dimen.size_200));

        Glide.with(getActivity())
                .load(SharePrefs.getSetting(getActivity()).getLogo())
                .placeholder(R.drawable.main_background_image).into(mbinding.tvOrgnizationname);


        TextView tv_header = (TextView) mbinding.llHeader.findViewById(R.id.tv_header);
        tv_header.setText(getString(R.string.classroom));
        LinearLayout ll_back = (LinearLayout) mbinding.llHeader.findViewById(R.id.ll_back);
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        ll_back.setVisibility(View.GONE);


        mbinding.tvWelcome.setText(SharePrefs.getSetting(getActivity()).getSlogan());

        id = SharePrefs.getUserdetail(getActivity()).getUser().getId();

        /*mbinding.youtubeLiveClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),LiveClassActivity.class);
                startActivity(intent);
            }
        });

        mbinding.zoomLiveClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),ZoomLiveClass.class);
                startActivity(intent);
            }
        });*/

        mbinding.rvItem.setHasFixedSize(true);
        mbinding.rvItem.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.margin_15);
        mbinding.rvItem.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, false));


        mbinding.btnTakecourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).mainActivityBinding.navigation.setSelectedItemId(R.id.navigation_course);
            }
        });

        mbinding.ivAdverticementClassroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent google=new Intent(Intent.ACTION_VIEW);
                google.setData(Uri.parse(classUrl));
                startActivity(google);
            }
        });

        if (Connectivity.isConnected(getActivity())) {
            callAdvertisementApiDialog(getContext());
            callCourseList(getActivity());
            //showAlertDialog();
        }
        else {
            //show net not connected error
            Toasty.warning(getActivity(), getString(R.string.net_disconnected), Toast.LENGTH_SHORT).show();
        }

        return rootView;
    }


    private void callCourseList(Context context) {

        //ProgressDialog.showDialog(context);
        RestApi mRestApi = RestClient.getClient(context).create(RestApi.class);
        HashMap<String, String> mparam = new HashMap<String, String>();
        mparam.put("userId", SharePrefs.getUserdetail(getActivity()).getUser().getId().toString());
        //Toast.makeText(context, ""+SharePrefs.getUserdetail(getActivity()).getUser().getId().toString(), Toast.LENGTH_SHORT).show();

        Observable<UsercourseModel> mdata = mRestApi.UsercourseList(mparam);
        System.out.println("value--"+mdata.toString());
        mdata.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UsercourseModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(UsercourseModel value) {
                        ProgressDialog.hideDialog();
                        if (value != null)
                            Utils.Relogin(value.getStatus(), getActivity());

                        if (value != null && value.getStatus() == 200) {

                            mlist = value.getData();
                            if (mlist != null) {
                                adapter = new UserCourseAdapter(getActivity(), mlist, ClassRoomFragment.this);
                                mbinding.rvItem.setAdapter(adapter);
                            }
                            callAdvertismentApi(getActivity());
                            mbinding.rvItem.setVisibility(View.VISIBLE);
                            mbinding.llnocourse.setVisibility(View.GONE);
                            mbinding.ivAdverticementClassroom.setVisibility(View.VISIBLE);
                        }
                        /*else if (value != null) {
                            //mDataListener.onError(value.getMessage());
                            Toasty.error(getActivity(), value.getMessage(), Toast.LENGTH_SHORT).show();
                            mbinding.llnocourse.setVisibility(View.VISIBLE);
                            mbinding.rvItem.setVisibility(View.GONE);
                            mbinding.ivAdverticementClassroom.setVisibility(View.VISIBLE);
                            //mbinding.llfilter.setVisibility(View.GONE);
                        }*/

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

    public void showAlertDialog() {        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_notification, null);
        builder.setView(customLayout);
        final AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button viewAll = (Button) customLayout.findViewById(R.id.notificationButton);

        CardView mCardView = (CardView) customLayout.findViewById(R.id.notify_cardView);

        /*mCardView.getLayoutParams().width = (int) (displayRectangle.width() * 0.9f);
        mCardView.getLayoutParams().height = (int) (displayRectangle.width() * 0.9f);*/

        viewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),NotificationActivity.class);
                startActivity(intent);
            }
        });

        //mCardView.requestLayout();

        dialog.show();
    }

    @Override
    public void OnItemclick(int pos) {
        if (mlist != null && mlist.size() > pos) {
            FragmentTask.replaceFrgament(CoursesubFragment.newInstance(mlist.get(pos)), getActivity().getSupportFragmentManager(), R.id.main_framelayout);
        }
    }

    @Override
    public void OnDesItemclick(int pos) {

    }

    private void callAdvertismentApi(Context context) {

        ProgressDialog.showDialog(getActivity());

        RestApi mRestApi = RestClient.getClient(context).create(RestApi.class);

        Observable<AdvertismentModel> mdata = mRestApi.advertisement();
        Log.d("test", mdata.toString());
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
                            Utils.hideKeyboard(getActivity());
                            ProgressDialog.hideDialog();
                            if (value.getData().get(4).getId() == "5f9d2baef701c65e7f707f16" || value.getData().get(0).getStatus() == 1) {
                                mbinding.cardview.setVisibility(View.VISIBLE);
                                classUrl = value.getData().get(0).getURL();
                                Picasso.get().load(value.getData().get(4).getImage()).into(mbinding.ivAdverticementClassroom);
                            } else {
                                Toast.makeText(getActivity(), "No Such Advertisement", Toast.LENGTH_SHORT).show();
                            }

                        }
                        else {
                            ProgressDialog.hideDialog();
                            //mDataListener.onError(value.getMessage());
                            Toasty.error(getActivity(), value.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        // display an error message
                        ProgressDialog.hideDialog();
                        Toast.makeText(getActivity(), ""+e, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onError: " + e.getMessage());

                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete: All Done!");
                    }
                });
    }

    private void callAdvertisementApiDialog(Context context) {

        RestApi mRestApi = RestClient.getClient(context).create(RestApi.class);

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
                        ProgressDialog.hideDialog();
                        if (value != null && value.getStatus() == 200) {
                            Utils.hideKeyboard(getActivity());

                            if (value.getData().get(1).getId() == "5f9d2bf2f701c65e7f707f13" || value.getData().get(1).getStatus() == 1) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                Log.d("Ads",value.getData().get(1).getPage());
                                Rect displayRectangle = new Rect();
                                Window window = getActivity().getWindow();
                                window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
                                final View customLayout = getLayoutInflater().inflate(R.layout.dialog_advertisment, null);
                                builder.setView(customLayout);
                                final AlertDialog dialog = builder.create();
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                ImageView ivaddvertisment = (ImageView) customLayout.findViewById(R.id.iv_advertisment);
                                ImageView ivclose = (ImageView) customLayout.findViewById(R.id.ivclose);
                                RelativeLayout rlmain = (RelativeLayout) customLayout.findViewById(R.id.rlmain);
                                CardView mCardView = (CardView) customLayout.findViewById(R.id.cardview);
                                LinearLayout llclose = (LinearLayout) customLayout.findViewById(R.id.llclose);

                                Picasso.get().load(value.getData().get(1).getImage()).into(ivaddvertisment);

                                //boolean isshow = ThemeClass.setAdvertisment(ivaddvertisment, getActivity(), "Class Room - Popup Ads", (int) (displayRectangle.width() * 0.9f), (int) (displayRectangle.width() * 0.9f));


                                ivaddvertisment.getLayoutParams().width = (int) (displayRectangle.width() * 0.9f);
                                ivaddvertisment.getLayoutParams().height = (int) (displayRectangle.width() * 0.9f);

                                ivaddvertisment.requestLayout();

                                mCardView.getLayoutParams().width = (int) (displayRectangle.width() * 0.9f);
                                mCardView.getLayoutParams().height = (int) (displayRectangle.width() * 0.9f);

                                mCardView.requestLayout();

                                llclose.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });

                                dialog.show();

                                callNotification();
                            } else {
                                Toast.makeText(getActivity(), "No Such Advertisement", Toast.LENGTH_SHORT).show();
                            }


                        } else {
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

    private void callNotification() {

        ProgressDialog.showDialog(getContext());
        RestApi mRestApi = RestClient.getClient(getContext()).create(RestApi.class);

        Observable<NotificationResponse> mdata = mRestApi.notification(id);
        mdata.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NotificationResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "onSubscribe: ");
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onNext(NotificationResponse value) {
                        ProgressDialog.hideDialog();

                        if (value != null && value.getStatus() == 200) {

                            try {
                                if (value.getData().get(0).getCourseId()!=null)
                                {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                                    Rect displayRectangle = new Rect();
                                    Window window = getActivity().getWindow();
                                    window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
                                    final View customLayout = getLayoutInflater().inflate(R.layout.dialog_notification, null);
                                    builder.setView(customLayout);
                                    final AlertDialog dialog = builder.create();
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                                    Button viewAll = (Button) customLayout.findViewById(R.id.notificationButton);
                                    TextView notification = (TextView) customLayout.findViewById(R.id.notification_text);
                                    TextView notification1 = (TextView) customLayout.findViewById(R.id.notification_text_1);

                                    notification.setText(getResources().getString(R.string.notification_text_1));
                                    notification1.setText(getResources().getString(R.string.notification_text_2)+SharePrefs.getSetting(getActivity()).getSupportNumber());

                                    viewAll.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(getActivity(),NotificationActivity.class);
                                            startActivity(intent);
                                        }
                                    });

                                    dialog.show();
                                }
                            }
                            catch (Exception e)
                            {
                                Toast.makeText(getActivity(), "No data", Toast.LENGTH_SHORT).show();
                            }


                        }
                        else {
                            Toasty.error(getActivity(),value.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        // display an error message
                        Toast.makeText(getActivity(), ""+e, Toast.LENGTH_SHORT).show();
                        ProgressDialog.hideDialog();
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete: All Done!");
                    }
                });

    }
}