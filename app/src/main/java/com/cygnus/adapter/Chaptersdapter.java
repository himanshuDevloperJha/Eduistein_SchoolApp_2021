package com.cygnus.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.cygnus.R;
import com.cygnus.St_Youtubethumbnails;
import com.cygnus.model.Chapter;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import androidx.cardview.widget.CardView;

public class Chaptersdapter extends BaseAdapter {
   // private List<Chapter> chapterlistt;
    //ArrayList<String> chapterlinklistt;
Context mcontext;
String teacher_emailidd,subjectnamee,user_schoolidd,classidd,user_standardd;

    private LayoutInflater mInflater;

    List<String> chapternamee;
    List<String> chapternoo;
    public Chaptersdapter(Context context,
                          String teacherEmailid, String subjectname,
                          List<String> chaptername, List<String> chapterno, String user_schoolid,
                          String classid,String user_standard) {
        mInflater = LayoutInflater.from(context);
        mcontext=context;
        teacher_emailidd=teacherEmailid;
        chapternoo=chapterno;
        chapternamee=chaptername;
        subjectnamee=subjectname;
        user_schoolidd=user_schoolid;
        classidd=classid;
        user_standardd=user_standard;

    }

    public int getCount() {
        return chapternoo.size();
    }

    public Object getItem(int position) {
        return chapternoo.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent)
    {
        convertView = mInflater.inflate(R.layout.custom_student_chapters, null);

        MaterialTextView tv_chapterno = (MaterialTextView) convertView.findViewById(R.id.tv_chapterno);
        CardView cd_layout =  convertView.findViewById(R.id.cd_layout);
        MaterialTextView tv_chaptername = (MaterialTextView) convertView.findViewById(R.id.tv_chaptername);
        tv_chapterno.setText(chapternoo.get(position));
        tv_chaptername.setText(chapternamee.get(position));

        cd_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                    Intent  intent=new Intent(mcontext, St_Youtubethumbnails.class);
                    intent.putExtra("teacher_emailidd",teacher_emailidd);
                    intent.putExtra("sub_name",subjectnamee);
                    intent.putExtra("chapter_name",chapternamee.get(position));
                    intent.putExtra("chapter_num",chapternoo.get(position));
                    intent.putExtra("user_schoolid",user_schoolidd);
                    intent.putExtra("user_classid",classidd);
                    intent.putExtra("user_standard", user_standardd);
                    mcontext.startActivity(intent);


            }
        });

        return convertView;
    }
}