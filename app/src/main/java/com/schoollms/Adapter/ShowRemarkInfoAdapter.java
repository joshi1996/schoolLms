package com.schoollms.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.schoollms.GsonModel.Assessment.AssessDetail;
import com.schoollms.R;
import com.schoollms.databinding.AssessmentLayoutBinding;
import com.schoollms.databinding.ShowRemarksBinding;
import com.schoollms.interfaces.OnclickListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShowRemarkInfoAdapter extends RecyclerView.Adapter<ShowRemarkInfoAdapter.MainViewHolder>{

    private LayoutInflater inflater;
    private Context context;
    private List<AssessDetail> listItemArrayList;
    OnclickListener mOnclickListener;
    public List<AssessDetail> exampleListFull=new ArrayList<>();

    public ShowRemarkInfoAdapter(Context context, List<AssessDetail> listItemArrayList, OnclickListener mOnclickListener){

        inflater = LayoutInflater.from(context);
        this.context = context;
        this.listItemArrayList = listItemArrayList;
        if(listItemArrayList!=null)
            this.exampleListFull.addAll(listItemArrayList);

        this.mOnclickListener=mOnclickListener;
    }

    @Override
    public int getItemCount() {
        return exampleListFull.size();
    }


    @Override
    public ShowRemarkInfoAdapter.MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        AssessmentLayoutBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.assessment_layout, parent, false);
        return new ShowRemarkInfoAdapter.MainViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ShowRemarkInfoAdapter.MainViewHolder holder, final int position) {

        AssessmentLayoutBinding binding = holder.mbinding;

        binding.assessmentTopic.setText(exampleListFull.get(position).getUserId());

        binding.assessmentLayout.setTag(position);
        binding.assessmentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos=(int)v.getTag();
                if(mOnclickListener!=null){
                    mOnclickListener.OnItemclick(pos);
                }
            }
        });

        if(exampleListFull.get(position).getUploadDate()!=null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

            Date convertedDate = new Date();
            try {
                convertedDate = dateFormat.parse(exampleListFull.get(position).getUploadDate());
                SimpleDateFormat sdfmonth = new SimpleDateFormat("dd-MM-yyyy");
                String date_text = sdfmonth.format(convertedDate);

                binding.assessmentDate.setText("" + date_text);

            } catch (ParseException e) {
                e.printStackTrace();
                try {
                    convertedDate = dateFormat.parse(exampleListFull.get(position).getUploadDate());
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }
                SimpleDateFormat sdfmonth = new SimpleDateFormat("dd-MM-yyyy");
                String date_text = sdfmonth.format(convertedDate);
                binding.assessmentDate.setText("" + exampleListFull.get(position).getUploadDate());
            }
        }
    }

    public void setData(List<AssessDetail> mlist) {
        this.listItemArrayList = mlist;
        exampleListFull.clear();

        this.exampleListFull.addAll(mlist);
        notifyDataSetChanged();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {
        private  AssessmentLayoutBinding mbinding;

        public MainViewHolder(AssessmentLayoutBinding mbinding) {
            super(mbinding.assessmentLayout);
            this.mbinding = mbinding;
        }
    }
}