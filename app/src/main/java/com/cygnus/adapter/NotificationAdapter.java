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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cygnus.AttendanceActivity;
import com.cygnus.R;
import com.cygnus.ScheduleCkassOnlineList;
import com.cygnus.St_YoutubeVideos;
import com.cygnus.StudentDashboardActivity;
import com.cygnus.YoutubevideoPlay;
import com.cygnus.feed.PendingApprovalPosts;
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
    String teacher_email,SubjectTeacherName,teacherid,schoolId;
    List<String> notificationlist;
    String userrrtypee;
    public NotificationAdapter(Context context, List<String> notificationlist, String userrrtypee,
                               String SubjectTeacherName,String teacherid,String schoolId) {
        this.context = context;
        this.notificationlist = notificationlist;
        this.userrrtypee = userrrtypee;
        this.teacherid = teacherid;
        this.SubjectTeacherName = SubjectTeacherName;
        this.schoolId = schoolId;


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




        holder.ll_notifctn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(userrrtypee.equals("Teacher")){
                   Intent intent=new Intent(context, PendingApprovalPosts.class);
                   intent.putExtra("studentname",SubjectTeacherName);
                   intent.putExtra("teacheridd",teacherid);
                   intent.putExtra("studentschoolid",schoolId);
                   context.startActivity(intent);
                   ((Activity)context).finish();
               }
            }
        });


    }


    @Override
    public int getItemCount() {
        return notificationlist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_msg_noti;
LinearLayout ll_notifctn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_msg_noti = itemView.findViewById(R.id.tv_msg_noti);
            ll_notifctn = itemView.findViewById(R.id.ll_notifctn);

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