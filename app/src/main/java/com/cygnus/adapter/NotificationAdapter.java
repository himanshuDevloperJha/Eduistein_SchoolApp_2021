package com.cygnus.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cygnus.AttendanceActivity;
import com.cygnus.R;
import com.cygnus.ScheduleCkassOnlineList;
import com.cygnus.St_YoutubeVideos;
import com.cygnus.StudentDashboardActivity;
import com.cygnus.YoutubevideoPlay;
import com.cygnus.model.Chapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    String videoid;

    Context context;
    String teacher_email;
    List<String> notificationlist;

    public NotificationAdapter(Context context, List<String> notificationlist) {
        this.context = context;
        this.notificationlist = notificationlist;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.custom_notification, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {


        holder.tv_msg_noti.setText(notificationlist.get(position));




       /* holder.tv_msg_noti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(notificationlist.get(position).contains("scheduled an Online class")){
                   context.startActivity(new Intent(context, ScheduleCkassOnlineList.class));
                   ((Activity)context).finish();
               }
               else if(notificationlist.get(position).contains("has shared a Video on")){
                   context.startActivity(new Intent(context, St_YoutubeVideos.class));
                   ((Activity)context).finish();
               }

               else {
                   context.startActivity(new Intent(context, StudentDashboardActivity.class));
                   ((Activity)context).finish();
               }
            }
        });*/


    }


    @Override
    public int getItemCount() {
        return notificationlist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_msg_noti;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_msg_noti = itemView.findViewById(R.id.tv_msg_noti);

        }
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}