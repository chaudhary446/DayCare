package com.daycare.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteException;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.daycare.AllPhotoActivity;
import com.daycare.DownloadActivity;
import com.daycare.FullImageActivity;
import com.daycare.R;
import com.daycare.model.ModelImage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AllPhotoAdapter1 extends RecyclerView.Adapter<AllPhotoAdapter1.ViewHolder>
{
    Activity activity;
    ArrayList<ModelImage> al_menu = new ArrayList<>();
    int int_position;
    View view;
    LinearLayout rl_open,rl_cancle,rl_delete;


    public AllPhotoAdapter1(Activity activity, ArrayList<ModelImage> al_menu, int int_position) {
        this.activity=activity;
        this.al_menu = al_menu;
        this.int_position = int_position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_all_photo_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        int safePosition = viewHolder.getAdapterPosition();
        final ModelImage dataModel=al_menu.get(safePosition);

        Glide.with(activity).load("file://" + al_menu.get(int_position).getAl_imagepath().get(i))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(viewHolder.iv_image);

        viewHolder.iv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(int_position,i);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (al_menu.get(int_position).getAl_imagepath().size() > 0) {
            return al_menu.get(int_position).getAl_imagepath().size();
        } else {
            return 1;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView tv_foldern, tv_foldersize;
        ImageView iv_image;

        public ViewHolder(View carView) {
            super(carView);
            tv_foldern = (TextView) carView.findViewById(R.id.tv_folder);
            tv_foldersize = (TextView) carView.findViewById(R.id.tv_folder2);
            iv_image = (ImageView) carView.findViewById(R.id.iv_image);
        }
    }


    public void showPopup(final int currPos, final int pos)
    {
        final Dialog openDialog = new Dialog(activity);
        openDialog.setContentView(R.layout.popup_menu_layout);
        openDialog.setTitle("Custom Dialog Box");
        rl_cancle=(LinearLayout)openDialog.findViewById(R.id.rl_cancle);
        rl_open=(LinearLayout)openDialog.findViewById(R.id.rl_open);
        rl_delete=(LinearLayout)openDialog.findViewById(R.id.rl_delete);

        rl_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "Open", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(activity,FullImageActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(activity, R.anim.anim_right_to_left,R.anim.anim_left_to_right).toBundle();
                activity.startActivity(intent, bndlanimation);
                intent.putExtra("value" , currPos);
                intent.putExtra("imagepos",pos);
                activity.startActivity(intent, bndlanimation);
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
                Toast.makeText(activity, "Delete", Toast.LENGTH_SHORT).show();
                String path= DownloadActivity.al_images.get(pos).getAl_imagepath().get(currPos);
                File fdelete = new File(path);
                if (fdelete.exists()) {
                    if (fdelete.delete()) {
                        System.out.println("file Deleted :" + path);
                        openDialog.dismiss();
                    } else {
                        System.out.println("file not Deleted :" + path);
                    }
                }
            }
        });

        openDialog.show();
    }
}
