package com.daycare;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daycare.adapter.AllPhotoAdapter;
import com.daycare.download.FileInfo;

import java.io.File;
import java.util.List;

public class AllPhotoActivity extends AppCompatActivity {

    int position;
    private GridView gridView;
    AllPhotoAdapter allPhotoAdapter;
    Toolbar toolbar;
    LinearLayout rl_open,rl_cancle,rl_delete,rl_share;
    boolean isDeleted=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_photo);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("All Document");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            position = extras.getInt("value");
        }


        gridView = (GridView)findViewById(R.id.gv_all_photo);
        allPhotoAdapter = new AllPhotoAdapter(this, DownloadActivity.al_images,position);
        gridView.setAdapter(allPhotoAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showPopup(position,i);
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AllPhotoActivity.this,DownloadActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anim_right_to_left,R.anim.anim_left_to_right).toBundle();
                startActivity(intent, bndlanimation);
            }
        });
    }


    public void showPopup(final int currPos, final int pos)
    {
        final Dialog openDialog = new Dialog(this);
        openDialog.setContentView(R.layout.popup_menu_layout);
        openDialog.setTitle("Custom Dialog Box");
        rl_cancle=(LinearLayout)openDialog.findViewById(R.id.rl_cancle);
        rl_open=(LinearLayout)openDialog.findViewById(R.id.rl_open);
        rl_delete=(LinearLayout)openDialog.findViewById(R.id.rl_delete);
        rl_share=(LinearLayout)openDialog.findViewById(R.id.rl_share);

        rl_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AllPhotoActivity.this, "Open", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(AllPhotoActivity.this,FullImageActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anim_right_to_left,R.anim.anim_left_to_right).toBundle();
                startActivity(intent, bndlanimation);
                intent.putExtra("value" , currPos);
                intent.putExtra("imagepos",pos);
                startActivity(intent, bndlanimation);
                openDialog.dismiss();
            }
        });

        rl_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog.dismiss();
            }
        });


        rl_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path=DownloadActivity.al_images.get(currPos).getAl_imagepath().get(pos);
                File fdelete = new File(path);
                if (fdelete.exists()) {
                    if (fdelete.delete()) {
                        isDeleted=true;
                        gridView.invalidate();
                        Toast.makeText(AllPhotoActivity.this, "Delete Method", Toast.LENGTH_SHORT).show();
                        openDialog.dismiss();
                        allPhotoAdapter.notifyDataSetChanged();
                        gridView.invalidateViews();
                        MediaScannerConnection.scanFile (AllPhotoActivity.this, new String[] {fdelete.toString()}, null, null);
                        scanFile(path,isDeleted);
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.DATA, fdelete.getAbsolutePath());
                        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg"); // or image/png
                        getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    } else {
                        System.out.println("file not Deleted :" + path);
                    }
                }else{
                    Toast.makeText(AllPhotoActivity.this, "Not Exist", Toast.LENGTH_SHORT).show();
                }
            }
        });

        rl_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path=DownloadActivity.al_images.get(currPos).getAl_imagepath().get(pos);
                File fdelete = new File(path);
                Uri uri = Uri.parse("file://"+fdelete.getPath());
                Intent share = new Intent();
                share.setAction(Intent.ACTION_SEND);
                share.setType("application/pdf");
                share.putExtra(Intent.EXTRA_STREAM, uri);
                share.setPackage("com.whatsapp");
                startActivity(share);
                openDialog.dismiss();
            }
        });

        openDialog.show();
    }

    private void scanFile(String path, final boolean isDelete) {
        try {
            MediaScannerConnection.scanFile(this, new String[] { path },
                    null, new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            if (isDelete) {
                                if (uri != null) {
                                    getContentResolver().delete(uri, null, null);
                                    gridView.invalidate();
                                    allPhotoAdapter.notifyDataSetChanged();
                                    gridView.setAdapter(allPhotoAdapter);
                                    Toast.makeText(AllPhotoActivity.this, "Scan Method Delete", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
