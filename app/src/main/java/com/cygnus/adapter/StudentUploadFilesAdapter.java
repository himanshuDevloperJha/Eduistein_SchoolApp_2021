package com.cygnus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cygnus.R;
import com.cygnus.ZoomAutoAttendance;
import com.cygnus.model.StudentUploadFiles;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StudentUploadFilesAdapter extends RecyclerView.Adapter<StudentUploadFilesAdapter.ViewHolder> {
    String videoid;

    Context context;
    String teacher_email;
    List<StudentUploadFiles> studentfilesList;
    ZoomAutoAttendance za;

    public StudentUploadFilesAdapter(Context context, List<StudentUploadFiles> studentfilesList,
                                     ZoomAutoAttendance za) {
        this.context = context;
        this.studentfilesList = studentfilesList;
        this.za = za;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.custom_studentfiles,
                parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {


        holder.tv_studentname.setText(studentfilesList.get(position).getStudentname());


        holder.viewFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              za.studentuploadfiles(studentfilesList.get(position).getStudentname(),
                      position,studentfilesList.get(position).getClassId());
            }
        });

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
        return studentfilesList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_studentname;
        TextView viewFilesButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_studentname = itemView.findViewById(R.id.tv_studentname);
            viewFilesButton = itemView.findViewById(R.id.viewFilesButton);

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