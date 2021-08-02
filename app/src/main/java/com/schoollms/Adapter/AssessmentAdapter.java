package com.schoollms.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.schoollms.GsonModel.Assessment.DatumAssess;
import com.schoollms.R;
import com.schoollms.databinding.AssessmentLayoutBinding;
import com.schoollms.databinding.ReviewLayoutBinding;
import com.schoollms.interfaces.OnclickListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AssessmentAdapter extends RecyclerView.Adapter<AssessmentAdapter.MainViewHolder>{

    private LayoutInflater inflater;
    private Context context;
    private List<DatumAssess> listItemArrayList;
    OnclickListener mOnclickListener;
    public List<DatumAssess> exampleListFull=new ArrayList<>();

    public AssessmentAdapter(Context context, List<DatumAssess> listItemArrayList,OnclickListener mOnclickListener){

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
    public AssessmentAdapter.MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        AssessmentLayoutBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.assessment_layout, parent, false);
        return new AssessmentAdapter.MainViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(AssessmentAdapter.MainViewHolder holder, final int position) {

        AssessmentLayoutBinding binding = holder.mbinding;

        binding.assessmentTopic.setText(exampleListFull.get(position).getTitle());

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

        if (exampleListFull.get(position).getAssesInfo().size() == 0)
        {
            binding.assessViewFlag.setVisibility(View.GONE);
        }
        else
        {
            try
            {
                for (int i=0; i<exampleListFull.get(position).getAssesInfo().size(); i++)
                {
                    for (int j=0; j<exampleListFull.get(position).getRemarksInfo().size(); j++)
                    {
                        if (exampleListFull.get(position).getAssesInfo().get(i).getId().equalsIgnoreCase(exampleListFull.get(position).getRemarksInfo().get(j).getSubmitAssesment()))
                        {
                            binding.assessViewFlag.setVisibility(View.VISIBLE);
                            binding.assessViewFlag.setText("Remarked");
                        }
                        else
                        {
                            binding.assessViewFlag.setVisibility(View.GONE);
                        }
                    }

                }

            }
            catch (IndexOutOfBoundsException e)
            {
                Toast.makeText(context, ""+e, Toast.LENGTH_SHORT).show();
            }

        }

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

    public void setData(List<DatumAssess> mlist) {
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