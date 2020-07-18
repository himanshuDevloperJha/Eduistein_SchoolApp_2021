package com.cygnus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.cygnus.adapter.YoutubeAdapter;
import com.cygnus.model.Youtube;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

public class St_YoutubeVideos extends AppCompatActivity {
    RecyclerView rv_ytvideos;
    YoutubeAdapter youtubeAdapter;
    ArrayList<Youtube> youtubelist = new ArrayList<>();
    DatabaseReference reference, reference1, rootRef2;
    String classname, teacher_email;
    ArrayList<String> subjectnameList=new ArrayList<>();
    ArrayList<String> clsnameList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_st__youtube_videos);
        rv_ytvideos = findViewById(R.id.rv_ytvideos);

        classname = getIntent().getStringExtra("user_classid");
        teacher_email = getIntent().getStringExtra("user_teacherid");
        reference = FirebaseDatabase.getInstance().getReference().child("youtubevideos");
        reference1 = FirebaseDatabase.getInstance().getReference().child("admin-videos");


        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot datas : dataSnapshot.getChildren()) {

                    String subjectkey = datas.getKey();
                    rootRef2 = reference.child(subjectkey);

                    rootRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot datas1 : dataSnapshot.getChildren()) {

                                if (classname.equals(datas1.child("cls").getValue().toString())) {

                                        Youtube yt = new Youtube(datas1.child("email").getValue().toString(),
                                                datas1.child("subject").getValue().toString(),
                                                datas1.child("cls").getValue().toString()
                                        );

                                        youtubelist.add(yt);
                                        subjectnameList.add(datas1.child("subject").getValue().toString());
                                    clsnameList.add(datas1.child("cls").getValue().toString());


                                        LinkedHashSet<String> sb = new LinkedHashSet<>(subjectnameList);
                                        List<String> subjctlist = new ArrayList<String>(sb);




                                        rv_ytvideos.setLayoutManager(new LinearLayoutManager(St_YoutubeVideos.this, RecyclerView.VERTICAL, false));
                                        youtubeAdapter = new YoutubeAdapter(St_YoutubeVideos.this, youtubelist,
                                                teacher_email,subjctlist);
                                        rv_ytvideos.setAdapter(youtubeAdapter);


                            /*rootRef2 = FirebaseDatabase.getInstance().getReference().
                                    child("youtubevideos").child(datas.getKey()).child("Chapters");

                            rootRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {



                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    //  pb_chapters.setVisibility(View.GONE);

                                    //  Toast.makeText(St_Chapters.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                                }
                            });*/



                                } else {
                                    //Toast.makeText(St_YoutubeVideos.this, "No Videos", Toast.LENGTH_SHORT).show();
                                }


                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                               Toast.makeText(St_YoutubeVideos.this, ""+databaseError.toString(), Toast.LENGTH_SHORT).show();

                        }
                    });


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //   Toast.makeText(St_YoutubeVideos.this, ""+databaseError.toString(), Toast.LENGTH_SHORT).show();

            }
        });

       /* reference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot datas : dataSnapshot.getChildren()) {

                    if (classname.equals(datas.child("class").getValue().toString())) {
                        try {
                            Youtube yt = new Youtube(datas.child("email").getValue().toString(),
                                    datas.child("subject").getValue().toString(),
                                    datas.child("class").getValue().toString());

                            youtubelist.add(yt);


                        } catch (Exception e) {

                        }

                    } else {

                    }


                    rv_ytvideos.setLayoutManager(new LinearLayoutManager(St_YoutubeVideos.this, RecyclerView.VERTICAL, false));
                    youtubeAdapter = new YoutubeAdapter(St_YoutubeVideos.this, youtubelist, teacher_email);
                    rv_ytvideos.setAdapter(youtubeAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //   Toast.makeText(St_YoutubeVideos.this, ""+databaseError.toString(), Toast.LENGTH_SHORT).show();

            }
        });*/


    }
}
