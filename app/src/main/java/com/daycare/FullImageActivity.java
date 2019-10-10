package com.daycare;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridView;

import com.daycare.adapter.FullImageAdapter;

public class FullImageActivity extends AppCompatActivity {

    ViewPager viewPager;
    FullImageAdapter fullImageAdapter;
    int pos,imagepos;
    String imageUrl;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            pos = extras.getInt("value");
            imagepos=extras.getInt("imagepos");
        }

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("FullImage Activity");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fullImageAdapter=new FullImageAdapter(this, DownloadActivity.al_images,pos);
        viewPager = (ViewPager)findViewById(R.id.pager);
        viewPager.setAdapter(fullImageAdapter);
        viewPager.setCurrentItem(imagepos);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                imageUrl= DownloadActivity.al_images.get(pos).getAl_imagepath().get(i);
            }

            @Override
            public void onPageSelected(int i) {
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FullImageActivity.this,AllPhotoActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anim_right_to_left,R.anim.anim_left_to_right).toBundle();
                startActivity(intent, bndlanimation);
            }
        });

    }
}
