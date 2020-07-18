package com.cygnus.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import co.aspirasoft.view.BaseView
import com.cygnus.R
import com.cygnus.model.CourseFile
import com.google.android.material.button.MaterialButton

class CourseFileView : BaseView<CourseFile> {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private val mFileView: MaterialButton

    init {
        LayoutInflater.from(context).inflate(R.layout.view_course_file, this)
        mFileView = findViewById(R.id.fileView)
    }

    override fun updateView(model: CourseFile) {
        mFileView.text = model.name
    }

    override fun setOnClickListener(l: OnClickListener?) {
        mFileView.setOnClickListener(l)
    }

    fun setStatus(status: FileStatus) {
        mFileView.setIconResource(when (status) {
            FileStatus.Cloud -> R.drawable.ic_cloud
            FileStatus.Local -> R.drawable.ic_downloaded
            FileStatus.Downloading -> R.drawable.ic_download
        })
    }

    enum class FileStatus {
        Cloud,
        Local,
        Downloading
    }

}