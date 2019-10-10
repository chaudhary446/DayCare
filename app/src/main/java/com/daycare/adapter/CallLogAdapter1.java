package com.daycare.adapter;

import android.app.Activity;
import android.content.Intent;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.daycare.R;
import com.daycare.model.CallLogModel;

import java.util.ArrayList;
import java.util.List;

public class CallLogAdapter1 extends RecyclerView.Adapter<CallLogAdapter1.ViewHolder>
{

    ArrayList<CallLogModel> vegNameModels;
    Activity context;

    public CallLogAdapter1(Activity context, ArrayList<CallLogModel> vegNameModels) {
        this.context = context;
        this.vegNameModels = vegNameModels;
    }

    @NonNull
    @Override
    public CallLogAdapter1.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.call_log_list_item, parent, false);
        return new CallLogAdapter1.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CallLogAdapter1.ViewHolder holder, int position) {
        final CallLogModel callLogModel=vegNameModels.get(position);

        holder.tv_number.setText(callLogModel.getPhonenumber());
        holder.tv_date.setText(callLogModel.getDate());
        holder.tv_duration.setText(callLogModel.getDuration());

        String Name=callLogModel.getType();
        if(Name==null)
        {
            holder.tv_Name.setText("Unknown");
        }else{
            holder.tv_Name.setText(callLogModel.getType());
        }

        switch (callLogModel.getDirCode())
        {
            case CallLog.Calls.OUTGOING_TYPE:
                holder.tv_type.setText("OUTGOING");
                break;
            case CallLog.Calls.INCOMING_TYPE:
                holder.tv_type.setText("INCOMING");
                break;
            case CallLog.Calls.MISSED_TYPE:
                holder.tv_type.setText("MISSED");
                break;
        }

        holder.cb_CheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                CallLogModel contact = (CallLogModel) cb.getTag();
                CallLogModel callLogModel1=new CallLogModel();
                callLogModel1.setSelected(cb.isChecked());
                vegNameModels.get(position).setSelected(cb.isChecked());
            }
        });
    }

    @Override
    public int getItemCount() {
        return vegNameModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_Name,tv_number,tv_type,tv_date,tv_duration;
        private ImageView imageView;
        CheckBox cb_CheckBox;
        CardView cardView;
        public ViewHolder(View view) {
            super(view);
            tv_Name = (TextView)view.findViewById(R.id.name);
            tv_number = (TextView) view.findViewById(R.id.number);
            tv_type = (TextView) view.findViewById(R.id.type);
            tv_date = (TextView) view.findViewById(R.id.date);
            tv_duration = (TextView) view.findViewById(R.id.duration);
            cb_CheckBox=(CheckBox) view.findViewById(R.id.cb_checkBox);
        }
    }


    public ArrayList<CallLogModel> getStudentist() {
        return vegNameModels;
    }
}
