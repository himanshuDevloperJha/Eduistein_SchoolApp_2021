package com.cygnus.coursefeature

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cygnus.R
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_single_course.*

class SingleCourseActivity : AppCompatActivity() {
lateinit var datakeycourse:String
lateinit var course_name:String
lateinit var course_std:String
     var courseofferlist = ArrayList<Courseofferedmodel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_course)

        datakeycourse=intent.getStringExtra("datakeycourse")
        course_name=intent.getStringExtra("course_name")
        course_std=intent.getStringExtra("course_std")
        Log.e("msg111122","StudentDashboard3:"+course_name)
        Log.e("msg111122","StudentDashboard4:"+datakeycourse)

        toolbar_course.setTitle(course_name)

        courseofferlist.clear()


                                                // val coursename= datasliked.child("name").value.toString()
        val rootReff5= FirebaseDatabase.getInstance().reference.child("coursesoffered").
                child(datakeycourse).child("courselist").child(course_name)
        rootReff5.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshottutorial: DataSnapshot) {
                if (dataSnapshottutorial.exists()) {
                  //  Log.e("msg111122","StudentDashboard5:"+dataSnapshottutorial.key.toString())

                    for (datastutorial in dataSnapshottutorial.children) {
                                 courseofferlist.add(Courseofferedmodel(
                                   datastutorial.key.toString(), datastutorial.child("description").value.toString(),
                                   datastutorial.child("url").value.toString()))
                    Log.e("msg111122","StudentDashboard5:"+datastutorial.child("description").value.toString())

                             }
                  rv_coursetutorial.layoutManager = GridLayoutManager(this@SingleCourseActivity, 2)
                 rv_coursetutorial.adapter = TutorialAdapter(this@SingleCourseActivity,
                                  courseofferlist)
                    // Toast.makeText(applicationContext,"Course name2: "+courseofferlist.size,Toast.LENGTH_LONG).show()
                    Log.e("msg111122","StudentDashboard4:"+datakeycourse)




                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e("msg","Courseerror 1:"+p0.toString())

            }
        })



    }

    internal class TutorialAdapter(val context: Activity,
                                 val courseofferlist: ArrayList<Courseofferedmodel>
    ) :
            RecyclerView.Adapter<TutorialAdapter.MyViewHolder>() {
        internal inner class MyViewHolder(v: View) : RecyclerView.ViewHolder(v) {

            val tutorialdesc: MaterialTextView = v.findViewById(R.id.tutorialdesc)
            val tutorialno: MaterialTextView = v.findViewById(R.id.tutorialno)
//            val tutorialcolor: View = v.findViewById(R.id.tutorialcolor)
            val tutorialCard: MaterialCardView = v.findViewById(R.id.tutorialCard)
        }
        @NonNull
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: View = LayoutInflater.from(context).inflate(R.layout.custom_coursetutorial, parent,false)
            return MyViewHolder(v)
        }
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.tutorialdesc.setText(courseofferlist.get(position).description)
            holder.tutorialno.setText("Tutorial "+courseofferlist.get(position).tutorialno.replace("tutorial",""))
//            holder.tutorialcolor.setBackgroundColor(convertToColor(courseofferlist.get(position)))
            holder.tutorialCard.setOnClickListener {

                if(courseofferlist.get(position).url.contains(".mp4") || courseofferlist.get(position).url.contains(".3gp")||
                        courseofferlist.get(position).url.contains(".gif") || courseofferlist.get(position).url.contains(".avi")){
                    val intent= Intent(context,CourseVideoActivity::class.java)
                    intent.putExtra("course_desc",courseofferlist.get(position).description)
                    intent.putExtra("course_url",courseofferlist.get(position).url)
                    context.startActivity(intent)
                }
               else if(courseofferlist.get(position).url.contains(".pdf")){
                    val intent= Intent(context,CoursePdf::class.java)
                    intent.putExtra("course_desc",courseofferlist.get(position).description)
                    intent.putExtra("course_url",courseofferlist.get(position).url)
                    context.startActivity(intent)
                }

            }

        }
        override fun getItemCount(): Int {
            return courseofferlist.size
        }

        @SuppressLint("NewApi")
        private fun convertToColor(o: Any): Int {
            return try {
                val i = o.hashCode()
                Color.parseColor("#FF" + Integer.toHexString(i shr 16 and 0xFF) +
                        Integer.toHexString(i shr 8 and 0xFF) +
                        Integer.toHexString(i and 0xFF))
            } catch (ignored: Exception) {
                context.getColor(R.color.colorAccent)
            }
        }
    }

}
