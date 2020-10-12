package com.cygnus;

import android.content.SharedPreferences;
import android.os.Bundle;
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
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NotificationActivity extends AppCompatActivity {
    DatabaseReference reference, rootRef2;
    SharedPreferences sp_loginsave;
    SharedPreferences.Editor ed_loginsave;
    String classid, schoolId;
    List<String> notificationlist = new ArrayList<>();
    RecyclerView rv_notifications;
    NotificationAdapter notificationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        sp_loginsave = getSharedPreferences("SAVELOGINDETAILS", MODE_PRIVATE);
        ed_loginsave = sp_loginsave.edit();

        classid = sp_loginsave.getString("studentclassId", "");
        schoolId = sp_loginsave.getString("studentschoolid", "");
        rv_notifications = findViewById(R.id.rv_notifications);


        reference = FirebaseDatabase.getInstance().getReference().
                child(schoolId).child("Notifications");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot datas : dataSnapshot.getChildren()) {

                    if (datas.child("classId").getValue().toString().equalsIgnoreCase(classid)) {
                        notificationlist.add(datas.child("message").getValue().toString());

                    }

                }
                rv_notifications.setLayoutManager(new LinearLayoutManager(NotificationActivity.this,
                        RecyclerView.VERTICAL, false));
                notificationAdapter = new NotificationAdapter(NotificationActivity.this, notificationlist);
                rv_notifications.setAdapter(notificationAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(NotificationActivity.this, "" + databaseError.toString(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}
