package com.cygnus.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cygnus.R;
import com.cygnus.St_Chapters;
import com.cygnus.YoutubevideoPlay;
import com.cygnus.model.Youtube;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


public class YoutubeAdapter extends RecyclerView.Adapter<YoutubeAdapter.ViewHolder> {

    Context context;
    String teacher_email;
    ArrayList<Youtube> youtubelist;
    List<String> subjctlist;

    public YoutubeAdapter(Context context, ArrayList<Youtube> youtubelist, String teacher_email,
                         List<String> subjctlist) {
        this.context = context;
        this.youtubelist = youtubelist;
        this.teacher_email = teacher_email;
        this.subjctlist = subjctlist;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.custom_youtube, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.tv_ytsubject.setText(subjctlist.get(position));

        holder.cd_youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, St_Chapters.class);
                intent.putExtra("teacher_emailid", teacher_email);
                intent.putExtra("subjectname", subjctlist.get(position));
                context.startActivity(intent);
            }
        });


    }

    public static String extractYTId1(String ytUrl) {
        String vId = null;
        Pattern pattern = Pattern.compile(
                "^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(ytUrl);
        if (matcher.matches()) {
            vId = matcher.group(1);
        }
        return vId;
    }

    public static String extractYoutubeVideoId2(String ytUrl) {

        String vId = null;

        String pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";

        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(ytUrl);

        if (matcher.find()) {
            vId = matcher.group();
        }
        return vId;
    }

    @Override
    public int getItemCount() {
        return subjctlist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_ytsubject, tv_yt_title, tv_ytlink;
        CardView cd_youtube;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_ytsubject = itemView.findViewById(R.id.tv_ytsubject);
            tv_yt_title = itemView.findViewById(R.id.tv_yt_title);
            tv_ytlink = itemView.findViewById(R.id.tv_ytlink);
            cd_youtube = itemView.findViewById(R.id.cd_youtube);
        }
    }
}
