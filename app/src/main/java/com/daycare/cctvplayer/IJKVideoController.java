package com.daycare.cctvplayer;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.playerlibrary.commonutils.VideoInterface;
import com.playerlibrary.view.IjkVideoView;

/**
 * Created by CHAUDHARY on 01-Jun-19.
 */

public class IJKVideoController implements VideoInterface {

    private Activity activity;
    private IjkVideoView ijkVideoView;
    @Nullable
    private String rtspUrl;
    private static String TAG = "IJKVideoController";

    public IJKVideoController(Activity activity, IjkVideoView ijkVideoViewObj) {
        this.activity = activity;
        this.ijkVideoView = ijkVideoViewObj;
    }

    @Override
    public void releasePlayer() {
        if(this.ijkVideoView != null) {
            IjkVideoView var10000 = this.ijkVideoView;
            if(this.ijkVideoView == null) {
                //Intrinsics.throwNpe();
            }

            if(var10000.isPlaying()) {
                Log.i(TAG, "Releasing player");
                var10000 = this.ijkVideoView;
                if(this.ijkVideoView == null) {
                    //Intrinsics.throwNpe();
                }
                var10000.release(true);
                return;
            }
        }
        Log.i(TAG, "releasePlayer() Video view is null or is not playing");
    }

    @Override
    public void start(String url) {
        if(url != null && url.length() > 0) {
            Log.e(TAG, "rtsp: " + url);
            IjkVideoView var10000 = this.ijkVideoView;
            if(this.ijkVideoView == null) {
                //Intrinsics.throwNpe();
            }

            var10000.setVideoURI(Uri.parse(url));
            var10000 = this.ijkVideoView;
            if(this.ijkVideoView == null) {
                //Intrinsics.throwNpe();
            }

            var10000.start();
        } else {
            Log.e(TAG, "empty rtsp");
        }
    }

    @Override
    public void start(String url, int width, int height) {
        this.rtspUrl = url;
        this.start(url);
    }

    @Override
    public void showFullScreen(String ip) {

    }

    @Override
    public void mute() {
        if(this.ijkVideoView != null) {
            IjkVideoView var10000 = this.ijkVideoView;
            if(this.ijkVideoView == null) {
                //Intrinsics.throwNpe();
            }

            if(var10000.isPlaying()) {
                Log.i(TAG, "mute called");
                var10000 = this.ijkVideoView;
                if(this.ijkVideoView == null) {
                    //Intrinsics.throwNpe();
                }

                var10000.mute();
                return;
            }
        }

        Log.i(TAG, "mute() Video view is null or is not playing");
    }

    @Override
    public void unMute() {
        if(this.ijkVideoView != null) {
            IjkVideoView var10000 = this.ijkVideoView;
            if(this.ijkVideoView == null) {
                //Intrinsics.throwNpe();
            }

            if(var10000.isPlaying()) {
                Log.i(TAG, "unmute called");
                var10000 = this.ijkVideoView;
                if(this.ijkVideoView == null) {
                   // Intrinsics.throwNpe();
                }

                var10000.unmute();
                return;
            }
        }

        Log.i(TAG, "unmute() Video view is null or is not playing");
    }
}
