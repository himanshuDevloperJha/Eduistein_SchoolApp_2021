package com.cygnus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.cygnus.adapter.Chaptersdapter;
import com.cygnus.adapter.ThumbnailAdapter;
import com.cygnus.model.Chapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class St_Youtubethumbnails extends AppCompatActivity {
    RecyclerView rv_youtubethumb;
    ProgressBar pb_thumbnail;
    String teacher_emailidd, chapter_name, sub_name;
    ThumbnailAdapter thumbnailAdapter;
    DatabaseReference rootRef, rootRef1, rootRef2, rootRef3;

    ArrayList<Chapter> chapter_urllist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_st__youtubethumbnails);

        rv_youtubethumb = findViewById(R.id.rv_youtubethumb);
        pb_thumbnail = findViewById(R.id.pb_thumbnail);


        teacher_emailidd = getIntent().getStringExtra("teacher_emailidd");
        chapter_name = getIntent().getStringExtra("chapter_name");
        sub_name = getIntent().getStringExtra("sub_name");
        //chapter_urllist = getIntent().getStringArrayListExtra("chapter_urllist");
        Log.e("msg", "onCreateList: " + chapter_urllist);

        Log.e("msg", "subjectnameeee1: " + sub_name);


        rootRef = FirebaseDatabase.getInstance().getReference().child("youtubevideos");

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot datas : dataSnapshot.getChildren()) {


                    final String subjectkey = datas.getKey();
                    rootRef2 = rootRef.child(subjectkey);

                    rootRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot datas1 : dataSnapshot.getChildren()) {

                                if ((datas1.child("email").getValue().toString()).equals(teacher_emailidd)
                                        && (datas1.child("subject").getValue().toString()).equals(sub_name)) {

                                    Log.e("msg", "subjectnamee1: " + datas1.child("subject").getValue().toString());

                                    rootRef1 = FirebaseDatabase.getInstance().getReference().
                                            child("youtubevideos").child(subjectkey).
                                            child(datas1.getKey()).child("Chapters");


                                    rootRef1.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            for (final DataSnapshot datas2 : dataSnapshot.getChildren()) {

                                                if (datas2.child("name").getValue().toString().equals(chapter_name)) {
                                                    Log.e("msg", "onDataChangeeeee: " + datas2.getKey());
                                                    Log.e("msg", "onDataChangeeeee1: " + datas2.child("name").getValue());
                                                    final String chapterno = datas2.getKey();
                                                    final String chaptername = datas2.child("name").getValue().toString();
                                                    final String chapterurl = datas2.child("url").getValue().toString();
                                                    final String chaptertitle = datas2.child("title").getValue().toString();
                                                    Chapter c = new Chapter(chapterno, chaptername, chapterurl, chaptertitle);
                                                    chapter_urllist.add(c);

                                                    rv_youtubethumb.setLayoutManager(new LinearLayoutManager(St_Youtubethumbnails.this, RecyclerView.VERTICAL, false));
                                                    thumbnailAdapter = new ThumbnailAdapter(St_Youtubethumbnails.this,
                                                            chapter_urllist);

                                                    rv_youtubethumb.setAdapter(thumbnailAdapter);
                                                    pb_thumbnail.setVisibility(View.GONE);
                                                } else {
                                                }





                                    /*rootRef2 = FirebaseDatabase.getInstance().getReference().
                                            child("youtubevideos").child(datas.getKey()).child("Chapters")
                                    .child(datas2.getKey()).child("videos-url");

                                    rootRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for(DataSnapshot datas3: dataSnapshot.getChildren()){


                                                String chapterurl=  datas3.child("url").getValue().toString();
                                                Log.e("msg", "onDataChangeeeee1: "+chapterurl);


                                            }


                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            //   Toast.makeText(St_YoutubeVideos.this, ""+databaseError.toString(), Toast.LENGTH_SHORT).show();

                                        }
                                    });*/


                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            //  pb_chapters.setVisibility(View.GONE);

                                            //  Toast.makeText(St_Chapters.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                                        }
                                    });

                                } else {

                                }

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //   Toast.makeText(St_YoutubeVideos.this, ""+databaseError.toString(), Toast.LENGTH_SHORT).show();

                        }
                    });


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //   Toast.makeText(St_YoutubeVideos.this, ""+databaseError.toString(), Toast.LENGTH_SHORT).show();

            }
        });


    }
}
