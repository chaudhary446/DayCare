package com.daycare.adapter;

import android.app.Activity;
import android.provider.CallLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.daycare.R;
import com.daycare.model.CallLogModel;

import java.util.ArrayList;

public class CallLogAdapter extends BaseAdapter
{

    Activity activity;
    ArrayList<CallLogModel> contactModelTests;

    public CallLogAdapter(Activity activity,ArrayList<CallLogModel> contactModelTests)
    {
        this.activity=activity;
        this.contactModelTests=contactModelTests;
    }

    @Override
    public int getCount() {
        return contactModelTests.size();
    }

    @Override
    public Object getItem(int position) {
        return contactModelTests.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=activity.getLayoutInflater();
        View rootView=inflater.inflate(R.layout.call_log_list_item,parent,false);
        CallLogModel contactModel=contactModelTests.get(position);
        TextView tv=(TextView)rootView.findViewById(R.id.number);
        TextView name=(TextView)rootView.findViewById(R.id.name);
        TextView type=(TextView)rootView.findViewById(R.id.type);
        TextView duration=(TextView)rootView.findViewById(R.id.duration);
        TextView date=(TextView)rootView.findViewById(R.id.date);

        date.setText(contactModel.getDate());
        duration.setText(contactModel.getDuration());

        switch (contactModel.getDirCode())
        {
            case CallLog.Calls.OUTGOING_TYPE:
                type.setText("OUTGOING");
                break;
            case CallLog.Calls.INCOMING_TYPE:
                type.setText("INCOMING");
                break;
            case CallLog.Calls.MISSED_TYPE:
                type.setText("MISSED");
                break;
        }

        tv.setText(contactModel.getPhonenumber());
        String Name=contactModel.getType();
        if(Name==null)
        {
            name.setText("Unknown");
        }else{
            name.setText(contactModel.getType());
        }
        return rootView;
    }
}
