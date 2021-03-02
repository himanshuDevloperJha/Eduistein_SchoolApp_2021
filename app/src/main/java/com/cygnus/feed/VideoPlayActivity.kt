package com.cygnus.feed

import android.media.session.MediaController
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.cygnus.R
import kotlinx.android.synthetic.main.activity_video_play.*
import java.util.logging.Level.parse

class VideoPlayActivity : AppCompatActivity() {
lateinit var videourl:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_play)

        videourl=intent.getStringExtra("video_urlpost")
        toolbarr_video.setTitle("Video")
        Log.e("msg","IMAGEEPATHH112256"+videourl)

        vd_feed.setVideoURI(Uri.parse(videourl))
        vd_feed.requestFocus()


        vd_feed.setOnPreparedListener {
            vd_feed.start()
            pb_videopost.visibility=View.GONE
        }
    }
}
