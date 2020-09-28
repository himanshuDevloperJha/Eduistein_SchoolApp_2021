package com.cygnus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cygnus.adapter.Chaptersdapter;
import com.cygnus.model.Chapter;
import com.cygnus.model.Youtube;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class St_Chapters extends AppCompatActivity {
    GridView gv_chapters;
    DatabaseReference rootRef, rootRef1, rootRef2, rootRef3,rootRef4;
    String user_schoolid, subjectname, classid, teacher_emailid,user_standard;
    List<Chapter> chapterlist = new ArrayList<>();
    List<Chapter> chapterlist2 = new ArrayList<>();
    List<String> chapternamelist = new ArrayList<>();
    List<String> chapternolist = new ArrayList<>();

    ArrayList<String> chapterlinklist = new ArrayList<>();
    ProgressBar pb_chapters;
    List<String> ch1, ch2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_st__chapters);


        gv_chapters = findViewById(R.id.gv_chapters);
        pb_chapters = findViewById(R.id.pb_chapters);

        teacher_emailid = getIntent().getStringExtra("teacher_emailid");

        classid = getIntent().getStringExtra("user_classid");
        user_standard = getIntent().getStringExtra("user_standard");
        subjectname = getIntent().getStringExtra("subjectname");
        user_schoolid = getIntent().getStringExtra("user_schoolid");
     //  Toast.makeText(this, ""+teacher_emailid, Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, ""+subjectname, Toast.LENGTH_SHORT).show();
        rootRef3 = FirebaseDatabase.getInstance().getReference().child("admin-videos");

        rootRef3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot datas2 : dataSnapshot.getChildren()) {

                    if (user_standard.equalsIgnoreCase(datas2.child("standard").getValue().toString()) &&
                            subjectname.equalsIgnoreCase(datas2.getKey())) {
                        rootRef4 = FirebaseDatabase.getInstance().getReference().child("admin-videos").
                                child(datas2.getKey()).child("Chapters");

                        rootRef4.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (final DataSnapshot datas2 : dataSnapshot.getChildren()) {


                                    Log.e("msg", "onDataChangeeeee: " + datas2.getKey());
                                    Log.e("msg", "onDataChangeeeee1: " + datas2.child("name").getValue());
                                    final String chapterno = datas2.getKey();
                                    final String chaptername = datas2.child("name").getValue().toString();
                                    chapternolist.add(chapterno);
                                    chapternamelist.add(chaptername);


                                }


                                LinkedHashSet<String> chaptername = new LinkedHashSet<>(chapternamelist);
                                LinkedHashSet<String> chapterno = new LinkedHashSet<>(chapternolist);
                                ch1 = new ArrayList<String>(chaptername);

                                ch2 = new ArrayList<String>(chapterno);
                                Log.e("msg", "dist: " + chaptername + " = " + chapterno);
                                Log.e("msg", "dist: " + ch1 + " = " + ch2);


                                Chaptersdapter chaptersdapter = new Chaptersdapter(St_Chapters.this,
                                        teacher_emailid, subjectname, ch1, ch2, user_schoolid,classid,user_standard);
                                gv_chapters.setAdapter(chaptersdapter);
                                pb_chapters.setVisibility(View.GONE);


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(St_Chapters.this, "1:"+databaseError.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        Log.e("msg", "subjectnamee1112: hii" );

                    }




                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("msg", "subjectnamee1113: "+databaseError.toString() );
                Toast.makeText(St_Chapters.this, "2:"+databaseError.toString(), Toast.LENGTH_SHORT).show();

            }
        });


        rootRef = FirebaseDatabase.getInstance().getReference().child(user_schoolid).child("youtubevideos");

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

                                if ((datas1.child("email").getValue().toString()).equalsIgnoreCase(teacher_emailid)
                                        && (datas1.child("subject").getValue().toString()).equalsIgnoreCase(subjectname)) {

                                    Log.e("msg", "subjectnamee1: " + datas1.child("subject").getValue().toString());

                                    rootRef1 = FirebaseDatabase.getInstance().getReference().child(user_schoolid).
                                            child("youtubevideos").child(subjectkey).
                                            child(datas1.getKey()).child("Chapters");


                                    rootRef1.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (final DataSnapshot datas2 : dataSnapshot.getChildren()) {
                                                Log.e("msg", "onDataChangeeeee: " + datas2.getKey());
                                                Log.e("msg", "onDataChangeeeee1: " + datas2.child("name").getValue());
                                                final String chapterno = datas2.getKey();
                                                final String chaptername = datas2.child("name").getValue().toString();
                                                final String chapterurl = datas2.child("url").getValue().toString();
                                                final String chaptertitle = datas2.child("title").getValue().toString();
                                                Chapter c = new Chapter(chapterno, chaptername, chapterurl, chaptertitle);
                                                chapterlist.add(c);
                                                chapternolist.add(chapterno);
                                                chapternamelist.add(chaptername);


                                            }


                                            LinkedHashSet<String> chaptername = new LinkedHashSet<>(chapternamelist);
                                            LinkedHashSet<String> chapterno = new LinkedHashSet<>(chapternolist);
                                            ch1 = new ArrayList<String>(chaptername);

                                            ch2 = new ArrayList<String>(chapterno);
                                            Log.e("msg", "dist: " + chaptername + " = " + chapterno);
                                            Log.e("msg", "dist: " + ch1 + " = " + ch2);


                                            Chaptersdapter chaptersdapter = new Chaptersdapter(St_Chapters.this,
                                                     teacher_emailid, subjectname, ch1, ch2, user_schoolid,classid,user_standard);
                                            gv_chapters.setAdapter(chaptersdapter);
                                            pb_chapters.setVisibility(View.GONE);


                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Toast.makeText(St_Chapters.this, "3:"+databaseError.toString(), Toast.LENGTH_SHORT).show();

                                        }
                                    });

                                } else {

                                }

                            }


                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(St_Chapters.this, "4:"+databaseError.toString(), Toast.LENGTH_SHORT).show();


                        }
                    });


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(St_Chapters.this, "5:"+databaseError.toString(), Toast.LENGTH_SHORT).show();

            }
        });



        /*rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot datas1 : dataSnapshot.getChildren()) {
                    cls_teacherid = datas1.child("teacherId").getValue().toString();
                    if (cls_teacherid.equals(teacher_emailid)) {

                        //for subjects
                        rootRef1 = FirebaseDatabase.getInstance().getReference().
                                child("eq32pznJQ6MgHFu2jvyFSTmRBY72").
                                child("classes").child(datas1.child("name").getValue().toString())
                                .child("subjects");
                        rootRef1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot datas2 : dataSnapshot.getChildren()) {
                                    subject_teacherid = datas2.child("teacherId").getValue().toString();

                                    if (subject_teacherid.equals(teacher_emailid)) {
                                        subjectname = datas2.child("name").getValue().toString();

                                        rootRef2 = FirebaseDatabase.getInstance().getReference().
                                                child("eq32pznJQ6MgHFu2jvyFSTmRBY72").
                                                child("classes").child(datas1.child("name").getValue().toString())
                                                .child("subjects").child(datas2.getKey()).child("Chapters");

                                        rootRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for (DataSnapshot datas : dataSnapshot.getChildren()) {

                                                    Log.e("msg", "onDataChangeChapter: " +
                                                            datas.getKey() + "=" + datas.getValue());
                                                    String chapterno= datas.getKey();
                                                    String chaptername=  datas.getValue().toString();
                                                    Chapter c=new Chapter(chapterno,chaptername);
                                                    chapterlist.add(c);

                                                }
                                                Log.e("msg", "onDataChangeChapter1: " +
                                                       chapterlist.size());

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                            //    pb_chapters.setVisibility(View.GONE);

                                              //     Toast.makeText(St_Chapters.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                                            }
                                        });


                                    } else {
                                     //   pb_chapters.setVisibility(View.GONE);

                                    //    Toast.makeText(St_Chapters.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                                    }


                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                              //  pb_chapters.setVisibility(View.GONE);

                              //  Toast.makeText(St_Chapters.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                            }
                        });


                    } else {
                      //  pb_chapters.setVisibility(View.GONE);

                      //  Toast.makeText(St_Chapters.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
             //   pb_chapters.setVisibility(View.GONE);

              //  Toast.makeText(St_Chapters.this, "Something went wrong", Toast.LENGTH_SHORT).show();

            }
        });*/


    }
}
