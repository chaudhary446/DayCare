package com.daycare;

import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.daycare.adapter.AdapterPhoto;
import com.daycare.model.ModelImage;

import java.io.File;
import java.util.ArrayList;

public class DownloadActivity extends AppCompatActivity {

    Toolbar toolbar;
    public static ArrayList<ModelImage> al_images = new ArrayList<>();
    boolean boolean_folder;
    AdapterPhoto obj_adapter;
    GridView gv_folder;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        gv_folder = (GridView)findViewById(R.id.gv_folder);
        setSupportActionBar(toolbar);
        setTitle("Downloaded File");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DownloadActivity.this,SecondActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anim_right_to_left,R.anim.anim_left_to_right).toBundle();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent,bndlanimation);
                if(cursor!=null){
                    cursor.close();
                }
            }
        });


        gv_folder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AllPhotoActivity productFragment = new AllPhotoActivity();
                Intent intent=new Intent(DownloadActivity.this,AllPhotoActivity.class);
                intent.putExtra("value",i);
                startActivity(intent);
            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fn_imagespath();
            }
        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public ArrayList<ModelImage> fn_imagespath() {
        try {
            al_images.clear();
            int int_position = 0;
            Uri uri;
            int column_index_data, column_index_folder_name;
            String absolutePathOfImage = null;
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
            final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
            //cursor = getContentResolver().query(uri, projection, MediaStore.Images.Media.DATA + " like ? ",new String[] {"%/DayCare/%"}, null, null);
            cursor = getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);
                for (int i = 0; i < al_images.size(); i++) {
                    if (al_images.get(i).getStr_folder().equals(cursor.getString(column_index_folder_name))) {
                        boolean_folder = true;
                        int_position = i;
                        break;
                    } else {
                        boolean_folder = false;
                    }
                }


                if (boolean_folder) {
                    ArrayList<String> al_path = new ArrayList<>();
                    al_path.addAll(al_images.get(int_position).getAl_imagepath());
                    al_path.add(absolutePathOfImage);
                    al_images.get(int_position).setAl_imagepath(al_path);
                } else {
                    ArrayList<String> al_path = new ArrayList<>();
                    al_path.add(absolutePathOfImage);
                    ModelImage obj_model = new ModelImage();
                    if (cursor.getString(column_index_folder_name).equals("Photos")) {
                        obj_model.setStr_folder(cursor.getString(column_index_folder_name));
                        obj_model.setAl_imagepath(al_path);
                        al_images.add(obj_model);
                    }
                }
            }

            obj_adapter = new AdapterPhoto(this, al_images);
            gv_folder.setAdapter(obj_adapter);

        }catch (Exception e){
        }
        finally {
            cursor.close();
        }
        return al_images;
    }
}
