package com.cygnus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cygnus.adapter.SubjectsAdapter;
import com.cygnus.dao.AttendanceDao;
import com.cygnus.model.Attendance;
import com.cygnus.model.Schedule;
import com.cygnus.model.Teacher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class St_ScheduledClass extends AppCompatActivity implements ZoomAutoAttendance{
    RecyclerView rv_subjects;
    ProgressBar pb_scheduledclass;
    SubjectsAdapter subjectsAdapter;
    String classname, user_schoolid;
    ArrayList<Schedule> subjectlist = new ArrayList<>();
    OnCompleteListener listener;
    Attendance attendance;

    Teacher currentTeacher;
    DatabaseReference reference, rootRef2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_st__scheduled_class);
        rv_subjects = findViewById(R.id.rv_subjects);
        pb_scheduledclass = findViewById(R.id.pb_scheduledclass);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        classname = getIntent().getStringExtra("user_classid");
        //  user_teacherid=getIntent().getStringExtra("user_teacherid");
        //  user_classid=getIntent().getStringExtra("user_classid");
        user_schoolid = getIntent().getStringExtra("user_schoolid");

        //subjectlist = getIntent().getStringArrayListExtra("subjectlist");

// Only allow a class teacher to access this page

        attendance = new Attendance(classname);
        pb_scheduledclass.setVisibility(View.VISIBLE);

        reference = FirebaseDatabase.getInstance().getReference().
                child(user_schoolid).child("scheduleclass");

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

                                if (classname.equalsIgnoreCase(datas1.child("classs").
                                        getValue().toString())) {
                                    try {
                                        String s = datas1.getKey();

                                        Schedule schedulemodel = new Schedule(datas1.child("email").getValue().toString(),
                                                datas1.child("classs").getValue().toString(), datas1.child("subject").getValue().toString(),
                                                datas1.child("date").getValue().toString(), datas1.child("starttime").getValue().toString(),
                                                datas1.child("endtime").getValue().toString(), datas1.child("zoomlink").getValue().toString());
                                        subjectlist.add(schedulemodel);
                                        //  Toast.makeText(St_ScheduledClass.this, ""+date, Toast.LENGTH_SHORT).show();


                                    } catch (Exception e) {
                                        pb_scheduledclass.setVisibility(View.GONE);
                                        Toast.makeText(St_ScheduledClass.this, "" + e.toString(), Toast.LENGTH_SHORT).show();

                                    }
                                } else {
                                   pb_scheduledclass.setVisibility(View.GONE);

                                    Toast.makeText(St_ScheduledClass.this, "No scheduled class", Toast.LENGTH_SHORT).show();
                                }

                            }

                            rv_subjects.setLayoutManager(new LinearLayoutManager(St_ScheduledClass.this, RecyclerView.VERTICAL, false));
                            subjectsAdapter = new SubjectsAdapter(St_ScheduledClass.this, subjectlist,
                                    St_ScheduledClass.this);
                            rv_subjects.setAdapter(subjectsAdapter);
                            pb_scheduledclass.setVisibility(View.GONE);


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                          //  Toast.makeText(St_ScheduledClass.this, "" + databaseError.toString(), Toast.LENGTH_SHORT).show();
                          //  pb_scheduledclass.setVisibility(View.GONE);

                        }
                    });


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
              //  pb_scheduledclass.setVisibility(View.GONE);

                //Toast.makeText(St_ScheduledClass.this, ""+databaseError.toString(), Toast.LENGTH_SHORT).show();

            }
        });




    }


    @Override
    public void zoomauto(String classid, int position) {
       /* AttendanceDao.add(user_schoolid, attendance, new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                Toast.makeText(St_ScheduledClass.this, "Saving attendance... Done!", Toast.LENGTH_SHORT).show();

            }
        });*/
    }

    @Override
    public void studentuploadfiles(String studentname, int position, String classid) {

    }
}
