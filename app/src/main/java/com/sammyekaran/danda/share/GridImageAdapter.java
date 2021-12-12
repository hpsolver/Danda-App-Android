package com.sammyekaran.danda.share;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.sammyekaran.danda.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class GridImageAdapter extends RecyclerView.Adapter<GridImageAdapter.ViewHolder> {


    private ArrayList<String> imgURLs;
    private FragmentActivity mActivity;
    private ItemClick itemClick;


    public GridImageAdapter(FragmentActivity activity, ArrayList<String> imgURLs, ItemClick itemClick) {
        mActivity = activity;
        this.imgURLs = imgURLs;
        this.itemClick=itemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_grid_view, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Glide.with(mActivity).load(imgURLs.get(position)).into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClick.onItemClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return imgURLs.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.gridImageView);

        }
    }

    interface ItemClick{
        void  onItemClick(int position);
    }
}



















