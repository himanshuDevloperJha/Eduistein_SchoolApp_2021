package com.cygnus.feed;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cygnus.R;
import com.cygnus.model.CourseFile;
import com.cygnus.storage.FileManager;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class PendingpostsAdapter extends RecyclerView.Adapter<PendingpostsAdapter.ViewHolder> {
Context context;
    ArrayList<CourseFile> material;
    FileManager materialManager;
    String schoolid, teachername, teacherid;
    ArrayList<Postsmodel> postslist;
Approveinterface approveinterface;

    public PendingpostsAdapter(Context context, ArrayList<CourseFile> material, FileManager materialManager,
                               String schoolid, String teachername, String teacherid, ArrayList<Postsmodel> postslist,
                               Approveinterface approveinterface) {
        this.context = context;
        this.material= material;
        this.materialManager = materialManager;
        this.schoolid = schoolid;
        this.teachername = teachername;
        this.teacherid = teacherid;
        this.postslist = postslist;
        this.approveinterface = approveinterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.custom_pendingposts,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            holder.tv_penddescc.setText(postslist.get(position).getDescription());
            if (postslist.get(position).getUrlpath().toLowerCase().contains("jpg") ||
                    postslist.get(position).getUrlpath().toLowerCase().contains("png")
                    || postslist.get(position).getUrlpath().toLowerCase().contains("jpeg")) {
                holder.ll_pendingarticle.setVisibility(GONE);
                holder.fm_pendingpost.setVisibility(VISIBLE);

                Picasso.get().load(postslist.get(position).getUrlpath()).into(holder.iv_pendingfeed);
            } else if (postslist.get(position).getUrlpath().toLowerCase().contains("mp4") ||
                    postslist.get(position).getUrlpath().toLowerCase().contains("3gp")||
                    postslist.get(position).getUrlpath().toLowerCase().contains("gif") ||
                    postslist.get(position).getUrlpath().toLowerCase().contains("avi")) {

                holder.ll_pendingarticle.setVisibility(GONE);
                holder.fm_pendingpost.setVisibility(VISIBLE);
                holder.iv_pendingplay.setVisibility(VISIBLE);

                Glide.with(context)
                        .load(postslist.get(position).getUrlpath())
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .thumbnail(0.5f)
                        .centerCrop()
                        .into(holder.iv_pendingfeed);

            }
            else{
                holder.ll_pendingarticle.setVisibility(VISIBLE);
                holder.fm_pendingpost.setVisibility(GONE);
                holder.txt_pnd_articledesc.setText(postslist.get(position).urlpath);
                holder.txt_pnd_link.setText(postslist.get(position).filename);

                holder.txt_pnd_link.setPaintFlags(holder.txt_pnd_link.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                holder.txt_pnd_link.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try {
                            // String url = "https://twitter.com/parmeshar_tv";
                            String url = postslist.get(position).filename;
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                            intent.setData(Uri.parse(url));
                            context.startActivity(intent);
                        } catch (Exception e) {
                            Snackbar.make(holder.txt_pnd_link,"Invalid Link",Snackbar.LENGTH_LONG).show();

                        }


                    }
                });

            }


        }
        catch (Exception e){

        }

        holder.iv_pendingplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.iv_pendingfeed.setVisibility(GONE);
                holder.iv_pendingplay.setVisibility(GONE);
                holder.vdd_pendingfeed.setVisibility(VISIBLE);
                holder.placeholderpending.setVisibility(VISIBLE);


                holder.vdd_pendingfeed.setVideoURI(Uri.parse(postslist.get(position).getUrlpath()));
                holder.vdd_pendingfeed.requestFocus();

                //vdd_feed.setZOrderOnTop(true)

                holder.vdd_pendingfeed.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        holder.placeholderpending.setVisibility(GONE);
                        holder.vdd_pendingfeed.start() ;
                    }
                });



                holder.vdd_pendingfeed.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        holder.iv_pendingfeed.setVisibility(VISIBLE);
                        holder.iv_pendingplay.setVisibility(VISIBLE);
                        holder.vdd_pendingfeed.setVisibility(GONE);
                        holder.placeholderpending.setVisibility(GONE);
                    }
                });
            }
        });

        PushDownAnim.setPushDownAnimTo(holder.btn_approve)
        .setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick( View view ){
                holder.btn_approve.setEnabled(false);
if(postslist.get(position).getUrlpath().toLowerCase().contains("jpg") ||
        postslist.get(position).getUrlpath().toLowerCase().contains("png")
        || postslist.get(position).getUrlpath().toLowerCase().contains("jpeg")
|| postslist.get(position).getUrlpath().toLowerCase().contains("mp4") ||
        postslist.get(position).getUrlpath().toLowerCase().contains("3gpp")){
    approveinterface.approvepost(position,postslist.get(position).getKey(),holder.btn_approve,
            postslist.get(position).getDescription(),postslist.get(position).getUsername(),
            postslist.get(position).getUrlpath(),postslist.get(position).getUserclassId());
}
else{
    approveinterface.approvearticle(position,postslist.get(position).getKey(),holder.btn_approve,
            postslist.get(position).getDescription(),postslist.get(position).getUsername(),
            postslist.get(position).getUrlpath(), postslist.get(position).getFilename(),postslist.get(position).getUserclassId());
}

            }

        } );
        PushDownAnim.setPushDownAnimTo(holder.btn_disapprove)
                .setOnClickListener( new View.OnClickListener(){
                    @Override
                    public void onClick( View view ){
                        holder.btn_disapprove.setEnabled(false);

                        if(postslist.get(position).getUrlpath().toLowerCase().contains("jpg") ||
                                postslist.get(position).getUrlpath().toLowerCase().contains("png")
                                || postslist.get(position).getUrlpath().toLowerCase().contains("jpeg")
                                || postslist.get(position).getUrlpath().toLowerCase().contains("mp4") ||
                                postslist.get(position).getUrlpath().toLowerCase().contains("3gpp")){
                            approveinterface.disapprovepost(position,postslist.get(position).getKey(),holder.btn_disapprove,
                                    postslist.get(position).getDescription(),postslist.get(position).getUsername(),
                                    postslist.get(position).getUrlpath(),postslist.get(position).getUserclassId());
                        }
                        else{
                            approveinterface.disapprovearticle(position,postslist.get(position).getKey(),holder.btn_disapprove,
                                    postslist.get(position).getDescription(),postslist.get(position).getUsername(),
                                    postslist.get(position).getUrlpath(),postslist.get(position).getFilename(),postslist.get(position).getUserclassId());
                        }


                    }

                } );

    }

    @Override
    public int getItemCount() {
        return postslist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_penddescc,txt_pnd_link,txt_pnd_articledesc;
        VideoView vdd_pendingfeed;
        ImageView iv_pendingfeed,iv_pendingplay;
        FrameLayout placeholderpending;
        AppCompatButton btn_approve,btn_disapprove;
        FrameLayout fm_pendingpost;
        LinearLayout ll_pendingarticle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_pendingfeed = itemView.findViewById(R.id.iv_pendingfeed);
            tv_penddescc = itemView.findViewById(R.id.tv_penddescc);
            vdd_pendingfeed = itemView.findViewById(R.id.vdd_pendingfeed);
            placeholderpending = itemView.findViewById(R.id.placeholderpending);
            iv_pendingplay = itemView.findViewById(R.id.iv_pendingplay);
            btn_disapprove = itemView.findViewById(R.id.btn_disapprove);
            btn_approve = itemView.findViewById(R.id.btn_approve);
            ll_pendingarticle = itemView.findViewById(R.id.ll_pendingarticle);
            fm_pendingpost = itemView.findViewById(R.id.fm_pendingpost);
            txt_pnd_articledesc = itemView.findViewById(R.id.txt_pnd_articledesc);
            txt_pnd_link = itemView.findViewById(R.id.txt_pnd_link);
        }
    }
}
