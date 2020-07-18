package com.cygnus.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import co.aspirasoft.view.BaseView
import com.cygnus.R
import com.cygnus.model.NoticeBoardPost

class NoticeBoardPostView : BaseView<NoticeBoardPost> {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private val postContentView: TextView
    private val postDateView: TextView
    private val postAuthorView: Button

    init {
        LayoutInflater.from(context).inflate(R.layout.view_notice_board_post, this)
        postContentView = findViewById(R.id.postContent)
        postDateView = findViewById(R.id.postDate)
        postAuthorView = findViewById(R.id.postAuthor)
    }

    override fun updateView(model: NoticeBoardPost) {
        postContentView.text = model.postContent
        postDateView.text = model.postDateAsString
        postAuthorView.text = model.postAuthor
    }

}