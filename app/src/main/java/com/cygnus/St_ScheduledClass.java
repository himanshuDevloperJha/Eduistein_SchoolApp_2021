package com.cygnus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.cygnus.adapter.SubjectsAdapter;
import com.cygnus.model.Schedule;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class St_ScheduledClass extends AppCompatActivity {
RecyclerView rv_subjects;
SubjectsAdapter subjectsAdapter;
String classname,user_schoolid;
ArrayList<Schedule> subjectlist=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_st__scheduled_class);
        rv_subjects=findViewById(R.id.rv_subjects);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        classname=getIntent().getStringExtra("user_classid");
      //  user_teacherid=getIntent().getStringExtra("user_teacherid");
      //  user_classid=getIntent().getStringExtra("user_classid");
        user_schoolid=getIntent().getStringExtra("user_schoolid");

        //subjectlist = getIntent().getStringArrayListExtra("subjectlist");



        DatabaseReference reference ;
        reference = FirebaseDatabase.getInstance().getReference().child(user_schoolid).child("scheduleclass");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot datas: dataSnapshot.getChildren()){
                    if(classname.equals(datas.child("classs").getValue().toString())){
                        try{
                            String s=datas.getKey();

                            Schedule schedulemodel=new Schedule(datas.child("email").getValue().toString(),
                                    datas.child("classs").getValue().toString(), datas.getKey(),
                                    datas.child("date").getValue().toString(), datas.child("starttime").getValue().toString(),
                                    datas.child("endtime").getValue().toString(), datas.child("zoomlink").getValue().toString());
                            subjectlist.add(schedulemodel);
                            //  Toast.makeText(St_ScheduledClass.this, ""+date, Toast.LENGTH_SHORT).show();

                            rv_subjects.setLayoutManager(new LinearLayoutManager(St_ScheduledClass.this,RecyclerView.VERTICAL,false));
                            subjectsAdapter=new SubjectsAdapter(St_ScheduledClass.this,subjectlist) ;
                            rv_subjects.setAdapter(subjectsAdapter);


                        }
                        catch (Exception e){
                          //  Toast.makeText(St_ScheduledClass.this, ""+e.toString(), Toast.LENGTH_SHORT).show();

                        }
                    }
                    else {
                       // Toast.makeText(St_ScheduledClass.this, "No scheduled class", Toast.LENGTH_SHORT).show();
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
