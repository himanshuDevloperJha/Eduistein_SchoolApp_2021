package com.cygnus

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.View
import co.aspirasoft.adapter.ModelViewAdapter
import com.cygnus.core.DashboardChildActivity
import com.cygnus.dao.NoticeBoardDao
import com.cygnus.model.NoticeBoardPost
import com.cygnus.model.Subject
import com.cygnus.model.Teacher
import com.cygnus.model.User
import com.cygnus.view.MessageInputDialog
import com.cygnus.view.NoticeBoardPostView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_list.*

class NoticeActivity : DashboardChildActivity() {

    private lateinit var posts: ArrayList<NoticeBoardPost>
    private lateinit var adapter: PostAdapter

    private var subject: Subject? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val subject = intent.getSerializableExtra(CygnusApp.EXTRA_SCHOOL_SUBJECT) as Subject?
        val posts = intent.getParcelableArrayListExtra<NoticeBoardPost>(CygnusApp.EXTRA_NOTICE_POSTS)
        if (subject == null && posts == null) {
            finish()
            return
        }

        this.posts = posts ?: ArrayList()
        this.adapter = PostAdapter(this, this.posts)

        if (subject != null) {
            this.posts.clear()
            this.subject = subject
            NoticeBoardDao.getPostsBySubject(schoolId, subject, OnSuccessListener {
                this.posts.addAll(it)
                this.adapter.notifyDataSetChanged()
            })
        }

        addButton.visibility = when (currentUser) {
            is Teacher -> {
                addButton.setOnClickListener { onAddNoticeClicked() }
                View.VISIBLE
            }
            else -> View.GONE
        }
    }

    override fun updateUI(currentUser: User) {
        contentList.adapter = this.adapter
    }

    private fun onAddNoticeClicked() {
        MessageInputDialog(this)
                .setOnMessageReceivedListener { message ->
                    try {
                        val status = Snackbar.make(contentList, "Sending...", Snackbar.LENGTH_INDEFINITE)
                        status.show()

                        val post = NoticeBoardPost(postContent = message, postAuthor = currentUser.name)
                        val onCompleteListener = OnCompleteListener<Void?> {
                            if (it.isSuccessful) {
                                status.setText("Message sent!")
                                posts.add(post)
                                adapter.notifyDataSetChanged()
                            } else {
                                status.setText(it.exception?.message ?: "Could not send the message at this time.")
                            }

                            Handler().postDelayed({
                                status.dismiss()
                            }, 1500L)
                        }

                        if (subject != null) {
                            NoticeBoardDao.add(schoolId, subject!!, post, onCompleteListener)
                        } else {
                            NoticeBoardDao.add(schoolId, (currentUser as Teacher).classId!!, post, onCompleteListener)
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
                .show()
    }

    private inner class PostAdapter(context: Context, val posts: ArrayList<NoticeBoardPost>)
        : ModelViewAdapter<NoticeBoardPost>(context, posts, NoticeBoardPostView::class) {

        override fun notifyDataSetChanged() {
            posts.sortByDescending { it.postDate }
            super.notifyDataSetChanged()
        }

    }

}