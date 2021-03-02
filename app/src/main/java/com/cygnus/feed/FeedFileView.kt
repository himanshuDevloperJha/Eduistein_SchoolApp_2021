package com.cygnus.feed

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.VideoView
import co.aspirasoft.view.BaseView
import com.bumptech.glide.Glide
import com.cygnus.R
import com.cygnus.model.CourseFile


class FeedFileView : BaseView<CourseFile> {



    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)



    init {
        LayoutInflater.from(context).inflate(R.layout.view_feed_file, this)

    }

    override fun updateView(model: CourseFile) {

        try{
            val extracteddatetime = model.name.substring(model.name.length - 24, model.name.length)
/*if(model.name.contains("mp4",ignoreCase = true) || model.name.contains("3gpp",ignoreCase = true)){
    vd_feed.visibility= View.VISIBLE
    vd_feed.setVideoURI()
}*/

           // mFileView.text = model.name.substring(0,model.name.length-24)
           // postDatehome.text = extracteddatetime
            Log.e("msg","DATETIMEEE::"+extracteddatetime+","+model.name.substring(model.name.length - 24));



        }
        catch (e:Exception){
            Log.e("msg","DATETIMEEE11::"+e.toString());

        }

    }

    override fun setOnClickListener(l: OnClickListener?) {
      //  mFileView.setOnClickListener(l)
    }

    fun setStatus(status: FileStatus) {
       /* mFileView.setIconResource(when (status) {
            FileStatus.Cloud -> R.drawable.ic_cloud
            FileStatus.Local -> R.drawable.ic_downloaded
            FileStatus.Downloading -> R.drawable.ic_download
        })*/
    }

    enum class FileStatus {
        Cloud,
        Local,
        Downloading
    }

}