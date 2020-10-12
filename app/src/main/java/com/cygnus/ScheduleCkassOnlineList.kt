package com.cygnus

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cygnus.adapter.SubjectsAdapter
import com.cygnus.dao.AttendanceDao
import com.cygnus.dao.UsersDao
import com.cygnus.model.Attendance
import com.cygnus.model.AttendanceRecord
import com.cygnus.model.Schedule
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.*
import java.util.*


class ScheduleCkassOnlineList : AppCompatActivity(), ZoomAutoAttendance {
    lateinit var rv_subjects: RecyclerView;
    lateinit var subjectsAdapter: SubjectsAdapter;
    lateinit var classname: String;
    lateinit var name: String;
    lateinit var rollNo: String;
    lateinit var user_schoolid: String;
    var subjectlist = ArrayList<Schedule>()
    lateinit var attendance: Attendance;
    lateinit var reference: DatabaseReference;
    lateinit var rootRef2: DatabaseReference;
    lateinit var sp_loginsave: SharedPreferences;
    lateinit var ed_loginsave: SharedPreferences.Editor;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_ckass_online_list)

        sp_loginsave = getSharedPreferences("SAVELOGINDETAILS", MODE_PRIVATE);
        ed_loginsave = sp_loginsave.edit();

        rv_subjects = findViewById(R.id.rv_subjects)
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        classname = sp_loginsave.getString("studentclassId","").toString();
        //  user_teacherid=getIntent().getStringExtra("user_teacherid");
        //  user_classid=getIntent().getStringExtra("user_classid");
        //  user_teacherid=getIntent().getStringExtra("user_teacherid");
//  user_classid=getIntent().getStringExtra("user_classid");
        user_schoolid = sp_loginsave.getString("studentschoolid","").toString()
        name =sp_loginsave.getString("user_name","").toString()
        rollNo = sp_loginsave.getString("user_rollNo","").toString()


        attendance = Attendance(classname)

        reference = FirebaseDatabase.getInstance().reference.child(user_schoolid).
                child("scheduleclass")

        reference!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (datas in dataSnapshot.children) {
                    val subjectkey = datas.key
                    rootRef2 = reference!!.child(subjectkey!!)
                    rootRef2!!.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (datas1 in dataSnapshot.children) {
                                if (classname.equals(datas1.child("classs").value.toString(), ignoreCase = true)) {
                                    try {
                                        val s = datas1.key
                                        val schedulemodel = Schedule(datas1.child("email").value.toString(),
                                                datas1.child("classs").value.toString(), datas1.child("subject").value.toString(),
                                                datas1.child("date").value.toString(), datas1.child("starttime").value.toString(),
                                                datas1.child("endtime").value.toString(), datas1.child("zoomlink").value.toString())
                                        subjectlist.add(schedulemodel)
                                        //val compare: Comparator = Collections.reverseOrder()

// Sorting the list taking into account the comparator
                                        // Sorting the list taking into account the comparator
                                        //Collections.sort(subjectlist, compare)
                                        Collections.reverse(subjectlist)
                                     /*   Collections.sort(subjectlist, Comparator { lhs, rhs ->
                                            lhs.date.compareTo(rhs.date) })*/
                                        //subjectlist.sortByDescending { datas1.child("date").value.toString() }
                                        //  Toast.makeText(St_ScheduledClass.this, ""+date, Toast.LENGTH_SHORT).show();
                                    } catch (e: Exception) {
                                        // Toast.makeText(applicationContext, "" + e.toString(), Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    // Toast.makeText(applicationContext, "No scheduled class", Toast.LENGTH_SHORT).show()
                                }
                            }

                            rv_subjects.setLayoutManager(LinearLayoutManager(applicationContext,
                                    RecyclerView.VERTICAL, false))
                            subjectsAdapter = SubjectsAdapter(applicationContext, subjectlist, this@ScheduleCkassOnlineList)
                            rv_subjects.setAdapter(subjectsAdapter)
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            //Toast.makeText(applicationContext, "" + databaseError.toString(), Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                //Toast.makeText(applicationContext, ""+databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        })
    }

    override fun studentuploadfiles(studentname: String?, position: Int, classid: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun zoomauto(classid: String?, position: Int) {
        AttendanceDao.add(user_schoolid, attendance, OnCompleteListener {

           // classname?.let { classname ->
                UsersDao.getStudentsInClass(user_schoolid, classname, OnSuccessListener { it ->

                    it.forEach {
                        attendance.attendanceRecords.clear()
                        val record = AttendanceRecord().apply {
                            this.studentName = name
                            this.studentRollNo = rollNo
                            this.date = this@ScheduleCkassOnlineList.attendance.date
                            this.classId = classname

//                            if(it.rollNo.equals(rollNo)){
                                this.attendance = true
//                            }
//                            else{
//                                this.attendance = false
//
//                            }
                        }
                        attendance.attendanceRecords.add(record)
                    }

                    AttendanceDao.getByDate(user_schoolid, classname, attendance.date, OnSuccessListener {
                        savedAttendance ->
                        savedAttendance?.attendanceRecords?.forEach { savedRecord ->
                            attendance.updateRecord(rollNo, savedRecord.attendance)
                        }
                    })
                })
          //  }
        })


    }


}
