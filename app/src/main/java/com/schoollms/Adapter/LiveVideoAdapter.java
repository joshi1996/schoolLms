package com.schoollms.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.schoollms.GsonModel.LiveVideo.LiveVideoDatum;
import com.schoollms.R;
import com.schoollms.databinding.RawLiveItemBinding;
import com.schoollms.interfaces.OnclickListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LiveVideoAdapter extends RecyclerView.Adapter<LiveVideoAdapter.MainViewHolder> {


    private LayoutInflater inflater;
    private Context context;
    private List<LiveVideoDatum> listItemArrayList;
    public List<LiveVideoDatum> exampleListFull=new ArrayList<>();
    OnclickListener mOnclickListener;
    ArrayList <Integer> color = new ArrayList<>();
    ArrayList <Integer> image = new ArrayList<>();
    int count = 0;

    public LiveVideoAdapter(Context context, List<LiveVideoDatum> listItemArrayList, OnclickListener mOnclickListener){

        inflater = LayoutInflater.from(context);
        this.context = context;
        this.listItemArrayList = listItemArrayList;
        if(listItemArrayList!=null)
            this.exampleListFull.addAll(listItemArrayList);
        this.mOnclickListener=mOnclickListener;

        color.add(R.drawable.subject_background);
        color.add(R.drawable.subject_background_1);
        color.add(R.drawable.subject_background_2);
        color.add(R.drawable.subject_background_3);

        image.add(R.drawable.english);
        image.add(R.drawable.physics);
        image.add(R.drawable.chemistry);
        image.add(R.drawable.biology);
    }

    @Override
    public int getItemCount() {
        return exampleListFull.size();
    }

    @Override
    public LiveVideoAdapter.MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RawLiveItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.raw_live_item, parent, false);
        return new LiveVideoAdapter.MainViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(LiveVideoAdapter.MainViewHolder holder, final int position) {

        RawLiveItemBinding binding = holder.mbinding;

        if (count != 4)
        {
            binding.liveImage.setBackgroundResource(color.get(count));
            binding.ivVideframe.setBackgroundResource(image.get(count));
            count++;
        }
        else
        {
            binding.liveImage.setBackgroundResource(color.get(count));
            binding.ivVideframe.setBackgroundResource(image.get(count));
            count = 0;
        }


        binding.tvTitleLive.setText(exampleListFull.get(position).getTitle());

        if(exampleListFull.get(position).getDateAndTime()!=null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy  HH:mm:ss");

            Date convertedDate = new Date();
            try {
                convertedDate = dateFormat.parse(exampleListFull.get(position).getDateAndTime());
                SimpleDateFormat sdfmonth = new SimpleDateFormat("dd-MM-yyyy  HH:mm:ss");
                String date_text = sdfmonth.format(convertedDate);
                String[] separated = date_text.split(" ");
                binding.tvTimeLive.setText(separated[2]);
                String date_server = separated[0];
                String[] parts = date_server.split("-");
                String yyyy = parts[0];
                String mm = parts[1];
                String dd = parts[2];
                String print_date = yyyy+"-"+mm+"-"+dd;
                binding.tvDateLive.setText(print_date);
            }
            catch (ParseException e)
            {
                e.printStackTrace();
                try {
                    convertedDate = dateFormat.parse(exampleListFull.get(position).getDateAndTime());
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }
                SimpleDateFormat sdfmonth = new SimpleDateFormat("dd-MM-yyyy  HH:mm:ss");
                String date_text = sdfmonth.format(convertedDate);
                String[] separated = date_text.split(" ");
                binding.tvTimeLive.setText(separated[2]);
                String date_server = separated[0];
                String[] parts = date_server.split("-");
                String yyyy = parts[0];
                String mm = parts[1];
                String dd = parts[2];
                String print_date = yyyy+"-"+mm+"-"+dd;
                binding.tvDateLive.setText(print_date);
            }
        }

        binding.llMain.setTag(position);
        binding.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos=(int)v.getTag();
                if(mOnclickListener!=null){
                    mOnclickListener.OnItemclick(pos);
                }
            }
        });
    }

    public void setData(List<LiveVideoDatum> mlist) {
        this.listItemArrayList = mlist;
        exampleListFull.clear();

        this.exampleListFull.addAll(mlist);
        notifyDataSetChanged();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {
        private RawLiveItemBinding mbinding;

        public MainViewHolder(RawLiveItemBinding mbinding) {
            super(mbinding.llMain);
            this.mbinding = mbinding;
        }
    }

}
