package com.schoollms.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.schoollms.GsonModel.Notification.NotificationModel;
import com.schoollms.R;
import com.schoollms.View.LiveClassActivity;
import com.schoollms.View.ZoomLiveClass;
import com.schoollms.databinding.NotificationLayoutBinding;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MainViewHolder>{

    private LayoutInflater inflater;
    private Context context;
    private List<NotificationModel> listItemArrayList;
    public List<NotificationModel> exampleListFull=new ArrayList<>();

    public NotificationAdapter(Context context, List<NotificationModel> listItemArrayList){

        inflater = LayoutInflater.from(context);
        this.context = context;
        this.listItemArrayList = listItemArrayList;
        if(listItemArrayList!=null)
            this.exampleListFull.addAll(listItemArrayList);
    }

    @Override
    public int getItemCount() {
        return exampleListFull.size();
    }


    @Override
    public NotificationAdapter.MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        NotificationLayoutBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.notification_layout, parent, false);
        return new NotificationAdapter.MainViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(NotificationAdapter.MainViewHolder holder, final int position) {
        NotificationLayoutBinding binding = holder.mbinding;

        String dateTime = exampleListFull.get(position).getDateAndTime();
        String[] separated = dateTime.split("T");
        String date_server = separated[0];
        String[] parts = date_server.split("-");
        String yyyy = parts[0];
        String mm = parts[1];
        String dd = parts[2];
        String print_date = dd+"-"+mm+"-"+yyyy;

        String time = separated[1];
        String[] t_part = time.split(":");
        String time1 = t_part[0];
        String time2 = t_part[1];
        String print_time = time1+":"+time2;

        binding.notificationDate.setText("Date:- "+print_date);
        binding.notificationTime.setText("Time:- "+print_time);

        binding.notificationTitle.setText("Title:- "+exampleListFull.get(position).getTitle());
        binding.notificationType.setText("Video Type:- "+exampleListFull.get(position).getCourseType());
        binding.notificationCourse.setText("Course:- "+exampleListFull.get(position).getCourseName());

        binding.moveNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exampleListFull.get(position).getCourseType().equalsIgnoreCase("zoom"))
                {
                    Intent intent = new Intent(context, ZoomLiveClass.class);
                    intent.putExtra("inside","new");
                    intent.putExtra("class_id", exampleListFull.get(position).getCourseId());
                    intent.putExtra("paramZoom","zoom");
                    context.startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(context, LiveClassActivity.class);
                    intent.putExtra("inside","new");
                    intent.putExtra("class_id", exampleListFull.get(position).getCourseId());
                    intent.putExtra("paramYou","youtube");
                    context.startActivity(intent);
                }
            }
        });

    }

    public void setData(List<NotificationModel> mlist) {
        this.listItemArrayList = mlist;
        exampleListFull.clear();

        this.exampleListFull.addAll(mlist);
        notifyDataSetChanged();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {
        private  NotificationLayoutBinding mbinding;

        public MainViewHolder(NotificationLayoutBinding mbinding) {
            super(mbinding.notificationLayout);
            this.mbinding = mbinding;
        }
    }
}