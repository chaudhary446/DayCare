package com.daycare.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.daycare.R;
import com.daycare.model.ModelImage;
import com.daycare.util.TouchImageView;


import java.util.ArrayList;


public class FullImageAdapter extends PagerAdapter {
    Context context;
    ArrayList<ModelImage> al_menu = new ArrayList<>();
    LayoutInflater layoutInflater;
    int int_position;



    public FullImageAdapter(Context context, ArrayList<ModelImage> al_menu, int int_position) {
        this.context = context;
        this.al_menu = al_menu;
        this.int_position = int_position;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return al_menu.get(int_position).getAl_imagepath().size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((FrameLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = layoutInflater.inflate(R.layout.slidingimages_layout, container, false);
        TouchImageView imageView = (TouchImageView) itemView.findViewById(R.id.image);
        Glide.with(context).load("file://" + al_menu.get(int_position).getAl_imagepath().get(position))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageView);
        container.addView(itemView);


        //listening to image click
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "you clicked image " + (position + 1), Toast.LENGTH_LONG).show();
            }
        });
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((FrameLayout) object);
    }

}
