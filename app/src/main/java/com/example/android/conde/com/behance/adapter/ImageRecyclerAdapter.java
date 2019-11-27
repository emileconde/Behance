package com.example.android.conde.com.behance.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.android.conde.com.behance.R;
import com.example.android.conde.com.behance.models.Image;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ImageRecyclerAdapter extends RecyclerView.Adapter<ImageRecyclerAdapter.ImageViewHolder> {
    private List<Image> mImageList;
    private Context mContext;

    public ImageRecyclerAdapter(List<Image> imageList, Context context) {
        mImageList = imageList;
        mContext = context;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Image image = mImageList.get(position);
        Glide.with(mContext)
                .load(image.getImageUrl())
                .into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mImageList.size();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder{
        private ImageView mImageView;
        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.image_view);
        }
    }

}
