package com.schoollms.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.schoollms.GsonModel.ShowAssess.DatumAssessShow;
import com.schoollms.R;
import com.schoollms.databinding.AssessmentLayoutBinding;
import com.schoollms.interfaces.OnclickListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShowSubmitAssessAdapter extends RecyclerView.Adapter<ShowSubmitAssessAdapter.MainViewHolder>{

    private LayoutInflater inflater;
    private Context context;
    private List<DatumAssessShow> listItemArrayList;
    OnclickListener mOnclickListener;
    public List<DatumAssessShow> exampleListFull=new ArrayList<>();

    public ShowSubmitAssessAdapter(Context context, List<DatumAssessShow> listItemArrayList, OnclickListener mOnclickListener){

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
    public ShowSubmitAssessAdapter.MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        AssessmentLayoutBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.assessment_layout, parent, false);
        return new ShowSubmitAssessAdapter.MainViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ShowSubmitAssessAdapter.MainViewHolder holder, final int position) {

        AssessmentLayoutBinding binding = holder.mbinding;

        /*if (exampleListFull.get(position).getAssesInfo().get(position).getLateSubmission() == 0)
        {
            binding.assessmentTopic.setText("On Time Submission");
        }
        else
        {
            binding.assessmentTopic.setText("Late Submission");
        }*/

        binding.assessView.setTag(position);
        binding.assessView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos=(int)v.getTag();
                if(mOnclickListener!=null){
                    mOnclickListener.OnItemclick(pos);
                }
            }
        });

        if(exampleListFull.get(position).getDate()!=null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

            Date convertedDate = new Date();
            try {
                convertedDate = dateFormat.parse(exampleListFull.get(position).getDate());
                SimpleDateFormat sdfmonth = new SimpleDateFormat("dd-MM-yyyy");
                String date_text = sdfmonth.format(convertedDate);

                binding.assessmentDate.setText("" + date_text);

            } catch (ParseException e) {
                e.printStackTrace();
                try {
                    convertedDate = dateFormat.parse(exampleListFull.get(position).getDate());
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }
                SimpleDateFormat sdfmonth = new SimpleDateFormat("dd-MM-yyyy");
                String date_text = sdfmonth.format(convertedDate);
                binding.assessmentDate.setText("" + exampleListFull.get(position).getDate());
            }
        }
    }

    public void setData(List<DatumAssessShow> mlist) {
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