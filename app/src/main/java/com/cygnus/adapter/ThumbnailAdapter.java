package com.cygnus.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cygnus.R;
import com.cygnus.St_Chapters;
import com.cygnus.YoutubevideoPlay;
import com.cygnus.model.Chapter;
import com.cygnus.model.Youtube;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailAdapter.ViewHolder> {
    String videoid;

    Context context;
    String teacher_email;
    ArrayList<Chapter> thumbnaillist;

    public ThumbnailAdapter(Context context, ArrayList<Chapter> thumbnaillist) {
        this.context = context;
        this.thumbnaillist = thumbnaillist;
        this.thumbnaillist = thumbnaillist;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.custom_thumbnail, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
holder.tv_titlee.setText(holder.tv_titlee.getText()+""+thumbnaillist.get(position).getChaptertitle());
        if (thumbnaillist.get(position).getChapterurl().startsWith("https://youtu")) {
          String videoid = extractYTId1(thumbnaillist.get(position).getChapterurl());
            String url = "http://img.youtube.com/vi/"+videoid + "/0.jpg";
            Glide.with(context).load(url).into(holder.iv_thumbnail);
        } else if (thumbnaillist.get(position).getChapterurl().startsWith("https://www.youtube.com")) {
            String videoid = extractYoutubeVideoId2(thumbnaillist.get(position).getChapterurl());
            String url = "http://img.youtube.com/vi/"+videoid + "/0.jpg";
            Glide.with(context).load(url).into(holder.iv_thumbnail);
        }
        else {
            Glide.with(context).load("https://www.yumyumvideos.com/wp-content/uploads/2018/09/No-YouTube.jpg").into(holder.iv_thumbnail);
        }

       // holder.iv_thumbnail.setPaintFlags(holder.iv_thumbnail.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
       // holder.iv_thumbnail.setText(thumbnaillist.get(position));
        holder.iv_thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (thumbnaillist.get(position).getChapterurl().startsWith("https://youtu")) {
                    String videoid = extractYTId1(thumbnaillist.get(position).getChapterurl());
                    Intent intent = new Intent(context, YoutubevideoPlay.class);
                    intent.putExtra("youtube_link", videoid);
                    intent.putExtra("teacher_emailid", teacher_email);
                    context.startActivity(intent);
                    Log.e("msg", "youtubevideo11: "+videoid );

                    //Bitmap bitmap = ThumbnailUtils.createVideoThumbnail("https://www.youtube.com/embed/"+videoid, MediaStore.Video.Thumbnails.MINI_KIND);
                    //holder.iv_thumbnail.setImageBitmap(bitmap);

                } else if (thumbnaillist.get(position).getChapterurl().startsWith("https://www.youtube.com")) {
                    String videoid = extractYoutubeVideoId2(thumbnaillist.get(position).getChapterurl());
                    Intent intent = new Intent(context, YoutubevideoPlay.class);
                    intent.putExtra("youtube_link", videoid);
                    intent.putExtra("teacher_emailid", teacher_email);
                    context.startActivity(intent);
                    Log.e("msg", "youtubevideo112: "+videoid );

                    //Bitmap bitmap = ThumbnailUtils.createVideoThumbnail("https://www.youtube.com/embed/"+videoid, MediaStore.Video.Thumbnails.MINI_KIND);
                    // holder.iv_thumbnail.setImageBitmap(bitmap);
                } else {
                    Toast.makeText(context, "Invalid Youtube Link", Toast.LENGTH_SHORT).show();
                }
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
        return thumbnaillist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_thumbnail;
TextView tv_titlee;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_thumbnail = itemView.findViewById(R.id.iv_thumbnail);
            tv_titlee = itemView.findViewById(R.id.tv_titlee);

        }
    }

    public static Bitmap retriveVideoFrameFromVideo(String videoPath) throws Throwable {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.getMessage());

        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }
}