package com.cygnus.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cygnus.R;
import com.cygnus.model.Schedule;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SubjectsAdapter extends RecyclerView.Adapter<SubjectsAdapter.ViewHolder> {

Context context;
//String date,starttime,endtime,cls,zoomlink,s;
    ArrayList<Schedule> subjectlist;
    public SubjectsAdapter(Context context, ArrayList<Schedule> subjectlist) {
        this.context = context;
        this.subjectlist = subjectlist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.custom_subjects,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
holder.tv_subject.setText(subjectlist.get(position).getSubject()+" ("+subjectlist.get(position).getClasss()+")");
holder.tv_date.setText(holder.tv_date.getText().toString()+""+subjectlist.get(position).getDate());
holder.tv_starttime.setText(holder.tv_starttime.getText().toString()+""+subjectlist.get(position).getStarttime());
holder.tv_endtime.setText(holder.tv_endtime.getText().toString()+""+subjectlist.get(position).getEndtime());
holder.tv_zoomlink.setText(subjectlist.get(position).getZoomlink());
        holder.tv_zoomlink.setPaintFlags(holder.tv_zoomlink.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
holder.tv_zoomlink.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        try{
            String url = subjectlist.get(position).getZoomlink();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            context.startActivity(intent);
        }
        catch (Exception e){
            Toast.makeText(context, "Invalid Zoom Link", Toast.LENGTH_SHORT).show();
        }

    }
});

    }

    @Override
    public int getItemCount() {
        return subjectlist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_subject,tv_zoomlink,tv_date,tv_starttime,tv_endtime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_subject=itemView.findViewById(R.id.tv_subject);
            tv_zoomlink=itemView.findViewById(R.id.tv_zoomlink);
            tv_date=itemView.findViewById(R.id.tv_date);
            tv_starttime=itemView.findViewById(R.id.tv_starttime);
            tv_endtime=itemView.findViewById(R.id.tv_endtime);
        }
    }
}
