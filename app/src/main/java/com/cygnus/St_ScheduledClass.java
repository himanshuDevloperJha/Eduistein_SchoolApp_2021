package com.cygnus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.cygnus.adapter.SubjectsAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class St_ScheduledClass extends AppCompatActivity {
RecyclerView rv_subjects;
SubjectsAdapter subjectsAdapter;
String classname;
ArrayList<String> subjectlist=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_st__scheduled_class);
        rv_subjects=findViewById(R.id.rv_subjects);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        classname=getIntent().getStringExtra("user_classid");
      //  user_teacherid=getIntent().getStringExtra("user_teacherid");
      //  user_classid=getIntent().getStringExtra("user_classid");
      //  user_schoolid=getIntent().getStringExtra("user_schoolid");

        //subjectlist = getIntent().getStringArrayListExtra("subjectlist");



        DatabaseReference reference ;
        reference = FirebaseDatabase.getInstance().getReference().child("scheduleclass");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot datas: dataSnapshot.getChildren()){
                    if(classname.equals(datas.child("classs").getValue().toString())){
                        try{
                            String s=datas.getKey();
                            String date=datas.child("date").getValue().toString();
                            String starttime=datas.child("starttime").getValue().toString();
                            String endtime=datas.child("endtime").getValue().toString();
                            String cls=datas.child("classs").getValue().toString();
                            String zoomlink=datas.child("zoomlink").getValue().toString();
                            subjectlist.add(s);
                            //  Toast.makeText(St_ScheduledClass.this, ""+date, Toast.LENGTH_SHORT).show();

                            rv_subjects.setLayoutManager(new LinearLayoutManager(St_ScheduledClass.this,RecyclerView.VERTICAL,false));
                            subjectsAdapter=new SubjectsAdapter(St_ScheduledClass.this,subjectlist,
                                    date,starttime,endtime,cls,zoomlink,s) ;
                            rv_subjects.setAdapter(subjectsAdapter);


                        }
                        catch (Exception e){
                          //  Toast.makeText(St_ScheduledClass.this, ""+e.toString(), Toast.LENGTH_SHORT).show();

                        }
                    }
                    else {
                        Toast.makeText(St_ScheduledClass.this, "No scheduled class", Toast.LENGTH_SHORT).show();
                    }




                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Toast.makeText(St_ScheduledClass.this, ""+databaseError.toString(), Toast.LENGTH_SHORT).show();

            }
        });





    }


}
