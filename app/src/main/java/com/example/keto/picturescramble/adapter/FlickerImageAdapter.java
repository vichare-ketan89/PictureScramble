package com.example.keto.picturescramble.adapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.keto.picturescramble.ImageGuessListener;
import com.example.keto.picturescramble.R;
import com.example.keto.picturescramble.model.FlickrModel;
import com.example.keto.picturescramble.util.IConstants;

import java.util.ArrayList;

/**
 *
 */

public class FlickerImageAdapter extends RecyclerView.Adapter<FlickerImageAdapter.ImageViewHolder> {

    private static final int IMAGE_TAG = 1001;
    ArrayList<FlickrModel.Item> imageUrls;
    AppCompatActivity activity;
    boolean isReset;
    ImageGuessListener imageGuessListener;

    public FlickerImageAdapter(AppCompatActivity appCompatActivity, ArrayList<FlickrModel.Item> urls, boolean isReset){
        activity = appCompatActivity;
        imageUrls = urls;
        this.isReset = isReset;
        imageGuessListener = (ImageGuessListener)activity;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.grid_item,parent,false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {

        if(imageUrls.get(position) != null) {
            if (!activity.isFinishing()) {

                String url = imageUrls.get(position).getMedia().getM();
                Glide.with(activity)
                        .load(url)
                       .animate(R.animator.flip_anim)
                        .into(holder.imageView);
            }
        }else{
            //Reset Scenario, Update Scenario
            if (!activity.isFinishing()) {

                Glide.with(activity)
                        .load(R.drawable.question_image)
                        .animate(R.animator.flip_anim)
                        .into(holder.imageView);
                holder.imageView.setClickable(true);
                holder.imageView.setBackgroundColor(ContextCompat.getColor(activity,android.R.color.white));
            }
        }
        holder.imageView.setTag(R.id.imageview_item,position);

    }

    @Override
    public int getItemCount() {
        return IConstants.MAX_IMAGES;
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView imageView;
        public ImageViewHolder(View view){
            super(view);

            imageView = (ImageView) view.findViewById(R.id.imageview_item);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.imageview_item: {
                    Integer position = (Integer) v.getTag(R.id.imageview_item);

                    if(position != null) {
                        imageGuessListener.onImageGuessed(position);
                    }else{
                        Log.d("FlickrAdpter", "Item is NULL");
                    }
                    break;
                }

                default:
            }
        }
    }
}
