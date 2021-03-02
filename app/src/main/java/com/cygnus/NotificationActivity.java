package com.cygnus;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cygnus.adapter.NotificationAdapter;
import com.cygnus.adapter.SubjectsAdapter;
import com.cygnus.model.Schedule;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NotificationActivity extends AppCompatActivity {
    DatabaseReference reference, rootRef2;
    SharedPreferences sp_loginsave;
    SharedPreferences.Editor ed_loginsave;
    String classid, schoolId,userrrtypee,SubjectTeacherClassId,SubjectTeacherName;
    List<String> notificationlist = new ArrayList<>();
    RecyclerView rv_notifications;
    NotificationAdapter notificationAdapter;
ProgressBar pb_notification;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        sp_loginsave = getSharedPreferences("SAVELOGINDETAILS", MODE_PRIVATE);
        ed_loginsave = sp_loginsave.edit();

        classid = sp_loginsave.getString("studentclassId", "");
        schoolId = sp_loginsave.getString("studentschoolid", "");
        userrrtypee = sp_loginsave.getString("userrrtypee", "");
        SubjectTeacherName = sp_loginsave.getString("SubjectTeacherName", "");
        SubjectTeacherClassId = sp_loginsave.getString("userrrtypee", "");
        rv_notifications = findViewById(R.id.rv_notifications);
        pb_notification = findViewById(R.id.pb_notification1);

if(userrrtypee.equals("Student")){
    reference = FirebaseDatabase.getInstance().getReference().
            child(schoolId).child("Notifications");

}
else if(userrrtypee.equals("Teacher")){
    reference = FirebaseDatabase.getInstance().getReference().
            child(schoolId).child("TeacherNotifications");

}

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot datas : dataSnapshot.getChildren()) {

                    if (datas.child("classId").getValue().toString().equalsIgnoreCase(classid)) {
                        notificationlist.add(datas.child("message").getValue().toString());

                    }

                }
                Collections.reverse(notificationlist);
                rv_notifications.setLayoutManager(new LinearLayoutManager(NotificationActivity.this,
                        RecyclerView.VERTICAL, false));
                notificationAdapter = new NotificationAdapter(NotificationActivity.this, notificationlist,userrrtypee,
                        SubjectTeacherName,SubjectTeacherClassId,schoolId);
                rv_notifications.setAdapter(notificationAdapter);
                pb_notification.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(NotificationActivity.this, "" + databaseError.toString(), Toast.LENGTH_SHORT).show();
                pb_notification.setVisibility(View.GONE);

            }
        });
    }
}
