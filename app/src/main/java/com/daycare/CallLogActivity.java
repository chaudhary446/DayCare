package com.daycare;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.daycare.adapter.CallLogAdapter;
import com.daycare.adapter.CallLogAdapter1;
import com.daycare.download.DocumentActivity;
import com.daycare.model.CallLogModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.Manifest.permission.READ_CALL_LOG;
import static android.Manifest.permission.READ_CONTACTS;

public class CallLogActivity extends AppCompatActivity {

    private ListView listView;
    ArrayList<CallLogModel> test=new ArrayList<>();
    CallLogAdapter1 contactLogAdapter;
    Cursor managedCursor;
    private static final int PERMISSION_REQUEST_CODE = 200;
    Set<CallLogModel> hasset=new HashSet<>();
    String curstrDate,curtoDate;
    Toolbar toolbar;
    RecyclerView recyclerView;
    Button bt_saveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_log);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        bt_saveData=(Button)findViewById(R.id.bt_saveData);
        setSupportActionBar(toolbar);
        setTitle("Call Log");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CallLogActivity.this, SecondActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anim_right_to_left,R.anim.anim_left_to_right).toBundle();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent,bndlanimation);
            }
        });

        if(checkPermission())
        {
        }
        else
        {
            requestPermission();
        }

        recyclerView = (RecyclerView) findViewById(R.id.product_recylerview);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DateFormat dateFormat = new SimpleDateFormat("dd,MM,yyyy");
        Calendar cal = Calendar.getInstance();
        String fromDate1=dateFormat.format(cal.getTime());
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd,MM,yyyy");
            Date date = sdf.parse(fromDate1);
            curstrDate = String.valueOf(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }


        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String toDate1=dateFormat.format(cal.getTime());

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd,MM,yyyy");
            Date date = sdf.parse(toDate1);
            curtoDate = String.valueOf(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }


        String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
        Uri callUri = Uri.parse("content://call_log/calls");
        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, Calendar.MAY, 01);
        String fromDate = String.valueOf(calendar.getTimeInMillis());
        calendar.set(2019, Calendar.MAY, 22);
        String toDate = String.valueOf(calendar.getTimeInMillis());
        String[] whereValue = {fromDate,toDate};
        String strOrder1 = android.provider.CallLog.Calls.DATE + " DESC limit 100";
        //managedCursor = managedQuery(callUri, null, android.provider.CallLog.Calls.DATE+" BETWEEN ? AND ?", whereValue, strOrder1);

        managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null,
                null, null, CallLog.Calls.DATE + " DESC limit 100");

        getCallDetails();

        bt_saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = "";
                List<CallLogModel> stList = ((CallLogAdapter1) contactLogAdapter).getStudentist();
                for (int i = 0; i < stList.size(); i++) {
                    CallLogModel singleStudent = stList.get(i);
                    if (singleStudent.isSelected() == true) {
                        data = data + "\n" + singleStudent.getPhonenumber().toString();
                    }
                }
                Toast.makeText(CallLogActivity.this, "Selected Students: \n" + data, Toast.LENGTH_LONG).show();
            }
        });

    }


    private String getCallDetails() {
        StringBuffer sb = new StringBuffer();
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        while (managedCursor.moveToNext()) {
            CallLogModel contactModelTest=new CallLogModel();
            String phNumber = managedCursor.getString(number);


            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            String callDate = managedCursor.getString(date);
            long millisecond = Long.parseLong(callDate);
            String dateString = new SimpleDateFormat("MM/dd/yyyy").format(new Date(millisecond));
            contactModelTest.setDate(dateString);
            //int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
            //String callDuration = managedCursor.getString(duration);

            long duration1 = managedCursor.getLong(managedCursor.getColumnIndex(CallLog.Calls.DURATION));
            String durations=formatHHMMSS(duration1);
            contactModelTest.setDuration(durations);

            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            String callType = managedCursor.getString(type);
            int dircode = Integer.parseInt(callType);
            contactModelTest.setDirCode(dircode);

            String name= managedCursor.getString(managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME));// for name
            contactModelTest.setType(name);
            contactModelTest.setPhonenumber(phNumber);
            test.add(contactModelTest);
        }

        contactLogAdapter=new CallLogAdapter1(this,test);
        recyclerView.setItemViewCacheSize(test.size());
        recyclerView.setAdapter(contactLogAdapter);
        return sb.toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        managedCursor.close();
    }

    private String calculateDifference(int timeInMillis){
        int hours = timeInMillis / 3600;
        int minutes = (timeInMillis % 3600) / 60;
        int seconds = timeInMillis % 60;
        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        return timeString;
    }

    public String formatHHMMSS(long secondsCount){
        int seconds = (int) (secondsCount %60);
        secondsCount -= seconds;
        long minutesCount = secondsCount / 60;
        long minutes = minutesCount % 60;
        minutesCount -= minutes;
        long hoursCount = minutesCount / 60;
        return "" + hoursCount + ":" + minutes + ":" + seconds;
    }

    private boolean checkPermission() {
        int readContact = ContextCompat.checkSelfPermission(CallLogActivity.this, READ_CONTACTS);
        int readCallLog = ContextCompat.checkSelfPermission(CallLogActivity.this, READ_CALL_LOG);
        return readContact== PackageManager.PERMISSION_GRANTED && readCallLog==PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(CallLogActivity.this, new String[]{READ_CONTACTS,READ_CALL_LOG}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean readcontact = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean readcalllog = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (readcontact && readcalllog) {
                        Toast.makeText(CallLogActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(CallLogActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{READ_CONTACTS,READ_CALL_LOG},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(CallLogActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

}
