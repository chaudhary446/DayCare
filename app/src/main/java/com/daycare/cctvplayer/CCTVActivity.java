package com.daycare.cctvplayer;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.daycare.R;
import com.daycare.SecondActivity;
import com.daycare.download.DocumentActivity;
import com.playerlibrary.commonutils.PlayerEnum;
import com.playerlibrary.commonutils.VideoInterface;
import com.playerlibrary.view.IjkVideoView;

import kotlin.jvm.internal.Intrinsics;

public class CCTVActivity extends AppCompatActivity {


    private VideoInterface videoController;
    private static final PlayerEnum playerEnum;
    IjkVideoView ijkVideoView;
    String url;

    static {
        playerEnum = PlayerEnum.IJKPLAYER;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cctv);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        setTitle("CCTV");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CCTVActivity.this, SecondActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anim_right_to_left,R.anim.anim_left_to_right).toBundle();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent,bndlanimation);
            }
        });

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                url= null;
            } else {
                url= extras.getString("url");
            }
        }
        init();
    }


    private final void init() {
        if(playerEnum == PlayerEnum.IJKPLAYER) {
            Activity activity = (Activity)this;
            ijkVideoView = (IjkVideoView)findViewById(R.id.ijkVideoView);
            //Intrinsics.checkExpressionValueIsNotNull(var10004, "ijkVideoView");
            this.videoController = (VideoInterface)(new IJKVideoController(activity, ijkVideoView));
        }

        VideoInterface videoInterface = CCTVActivity.this.videoController;
        if(videoInterface == null) {
        }
        videoController.start(url);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(this.videoController != null) {
            VideoInterface videoInterface = this.videoController;
            if(this.videoController == null) {
                //Intrinsics.throwNpe();
            }
            videoInterface.releasePlayer();
        }
    }
}
