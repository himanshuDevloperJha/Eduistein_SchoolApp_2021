package com.cygnus.adapter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cygnus.R;
import com.cygnus.ZoomAutoAttendance;
import com.cygnus.model.Schedule;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SubjectsAdapter extends RecyclerView.Adapter<SubjectsAdapter.ViewHolder> {

    Context context;
    ZoomAutoAttendance zoomAutoAttendance;
    //String date,starttime,endtime,cls,zoomlink,s;
    ArrayList<Schedule> subjectlist;

    public SubjectsAdapter(Context context, ArrayList<Schedule> subjectlist, ZoomAutoAttendance zoomAutoAttendance) {
        this.context = context;
        this.subjectlist = subjectlist;
        this.zoomAutoAttendance = zoomAutoAttendance;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.custom_subjects, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.tv_subject.setText(subjectlist.get(position).getSubject() + " (" + subjectlist.get(position).getClasss() + ")");
        holder.tv_date.setText("Date: " + subjectlist.get(position).getDate());
        holder.tv_starttime.setText("Start Time: " + subjectlist.get(position).getStarttime());
        holder.tv_endtime.setText("End Time: " + subjectlist.get(position).getEndtime());
        holder.tv_zoomlink.setText(subjectlist.get(position).getZoomlink());
        holder.tv_zoomlink.setPaintFlags(holder.tv_zoomlink.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        holder.tv_zoomlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    // String url = "https://twitter.com/parmeshar_tv";
                    zoomAutoAttendance.zoomauto(subjectlist.get(position).getClasss(),position);
                    String url = subjectlist.get(position).getZoomlink();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    intent.setData(Uri.parse(url));
                    context.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(context, "Invalid Zoom Link"+e.toString(), Toast.LENGTH_SHORT).show();
                }


            }
        });

    }


    @Override
    public int getItemCount() {
        return subjectlist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_subject, tv_zoomlink, tv_date, tv_starttime, tv_endtime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_subject = itemView.findViewById(R.id.tv_subject);
            tv_zoomlink = itemView.findViewById(R.id.tv_zoomlink);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_starttime = itemView.findViewById(R.id.tv_starttime);
            tv_endtime = itemView.findViewById(R.id.tv_endtime);
        }
    }
}
