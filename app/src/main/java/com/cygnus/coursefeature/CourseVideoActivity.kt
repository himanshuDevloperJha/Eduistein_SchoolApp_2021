package com.cygnus.coursefeature

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.cygnus.R
import kotlinx.android.synthetic.main.activity_course_video.*

class CourseVideoActivity : AppCompatActivity() {
lateinit var course_url:String
lateinit var course_desc:String
    var clickfullscreen=false
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_course_video)


        course_desc=intent.getStringExtra("course_desc")
        course_url=intent.getStringExtra("course_url")
        pb_crvideo.visibility=View.VISIBLE


        val mediaController = MediaController(this)
        mediaController.setAnchorView(vd_course)
        vd_course.setMediaController(mediaController)

        vd_course.setVideoURI(Uri.parse(course_url))
        vd_course.requestFocus()

        vd_course.setOnPreparedListener {
            pb_crvideo.visibility=View.GONE
          //  iv_fullscreen.visibility=View.VISIBLE
            vd_course.start()
        }
        vd_course.setOnCompletionListener {
            iv_crplay.visibility=View.VISIBLE
        }
        iv_crplay.setOnClickListener {
            pb_crvideo.visibility=View.GONE
            iv_crplay.visibility=View.GONE
          //  iv_fullscreen.visibility=View.VISIBLE
            vd_course.start()
        }

     /*   iv_fullscreen.setOnClickListener {
            if(clickfullscreen==false){
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN)
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                clickfullscreen=true
            }
            else if(clickfullscreen==true){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            }

        }*/

    }

}
