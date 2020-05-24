package com.ksb.sahyog.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ksb.sahyog.R;
import com.ksb.sahyog.model.AdminMessage;

import java.util.Date;
import java.util.List;

public class HomeRecylAdapter extends RecyclerView.Adapter<HomeRecylAdapter.HomeRecyclHolder> {

    List<AdminMessage> adminList;
    Context ctx;
    List<String> timeSpam;

    public HomeRecylAdapter(Context ctx, List<AdminMessage> adminList, List<String> timeSpam) {
        this.adminList = adminList;
        this.ctx = ctx;
        this.timeSpam = timeSpam;
    }

    @NonNull
    @Override
    public HomeRecyclHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.post_item,parent,false);

        return new HomeRecyclHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeRecyclHolder holder, int position) {

        AdminMessage message = adminList.get(position);

        holder.postTitle.setText(message.getTitle());
        holder.postDescp.setText(message.getDescp());

        Glide.with(ctx).load(message.getImageURL()).into(holder.imageView);

        java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
        String formattedDate = dateFormat.format(new Date(Long.parseLong(timeSpam.get(position))).getTime());

        holder.postDate.setText(formattedDate);

    }

    @Override
    public int getItemCount() {
        return adminList.size();
    }

    public class HomeRecyclHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView postTitle;
        TextView postDescp;
        TextView postDate;
        public HomeRecyclHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imagepost);
            postTitle = itemView.findViewById(R.id.titlepost);
            postDescp = itemView.findViewById(R.id.desppost);
            postDate = itemView.findViewById(R.id.postDate);
        }
    }
}
