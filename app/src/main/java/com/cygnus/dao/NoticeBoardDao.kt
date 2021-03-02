package com.cygnus.dao

import android.util.Log
import com.cygnus.CygnusApp
import com.cygnus.chatstaff.Sortchatmodel
import com.cygnus.feed.PostmodelBoard
import com.cygnus.model.NoticeBoardPost
import com.cygnus.model.Subject
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener

/**
 * A data access class to read posts on notice boards.
 *
 * Purpose of this class is to provide methods for communicating with the
 * Firebase backend to access data related to notice boards.
 *
 * @author https://github.com/Kushaggra
 * @since 1.0.0
 */
object NoticeBoardDao {

    /**
     * Adds a new post to a class notice board.
     *
     * @param schoolId The id of the school.
     * @param classId The id of the class.
     * @param post The post to add.
     * @param listener A listener for receiving response of the request.
     */
    fun add(schoolId: String, classId: String, post: NoticeBoardPost, listener: OnCompleteListener<Void?>) {
        CygnusApp.refToClassNoticeBoard(schoolId, classId)
                .push()
                .setValue(post)
                .addOnCompleteListener(listener)
    }

    /**
     * Adds a new post to a subject notice board.
     *
     * @param schoolId The id of the school.
     * @param classId The id of the class.
     * @param post The post to add.
     * @param listener A listener for receiving response of the request.
     */
    fun add(schoolId: String, subject: Subject, post: NoticeBoardPost, listener: OnCompleteListener<Void?>) {
        CygnusApp.refToSubjects(schoolId, subject.classId)
                .child(subject.name)
                .child("notices/")
                .push()
                .setValue(post)
                .addOnCompleteListener(listener)
    }

    /**
     * Retrieves a list of subjects taught in a school class.
     *
     * @param schoolId The id of the school.
     * @param classId The id of the school class
     * @param listener A listener for receiving response of the request.
     */
    fun getPostsBySubject(schoolId: String, subject: Subject, listener: OnSuccessListener<ArrayList<NoticeBoardPost>>) {
        CygnusApp.refToSubjects(schoolId, subject.classId)
                .child(subject.name)
                .child("notices/")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val posts = ArrayList<NoticeBoardPost>()

                        val t = object : GenericTypeIndicator<HashMap<String, NoticeBoardPost>>() {}
                        snapshot.getValue(t)?.values?.toList()?.forEach { posts.add(it) }
                        listener.onSuccess(posts)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        listener.onSuccess(ArrayList())
                    }
                })
    }

    /**
     * Retrieves a list of subjects taught in a school class.
     *
     * @param schoolId The id of the school.
     * @param classId The id of the school class
     * @param listener A listener for receiving response of the request.
     */
    fun getPostsByClass(schoolId: String, classId: String, listener: OnSuccessListener<ArrayList<NoticeBoardPost>>) {
        CygnusApp.refToClassNoticeBoard(schoolId, classId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val posts = ArrayList<NoticeBoardPost>()

                        val t = object : GenericTypeIndicator<HashMap<String, NoticeBoardPost>>() {}
                        snapshot.getValue(t)?.values?.toList()?.forEach { posts.add(it) }
                        listener.onSuccess(posts)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        listener.onSuccess(ArrayList())
                    }
                })
    }

    fun getPostsInstagram(schoolId: String, listener: OnSuccessListener<ArrayList<PostmodelBoard>>) {
        CygnusApp.refToSchoolPostsInstagram(schoolId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val posts = ArrayList<PostmodelBoard>()

                        val t = object : GenericTypeIndicator<HashMap<String, PostmodelBoard>>() {}
                        snapshot.getValue(t)?.values?.toList()?.forEach { posts.add(it) }
                        listener.onSuccess(posts)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        listener.onSuccess(ArrayList())
                    }
                })
    }

    fun getTimestampUsers(schoolId: String, listener: OnSuccessListener<ArrayList<Sortchatmodel>>) {
        CygnusApp.refToTimestampUsers(schoolId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val posts = ArrayList<Sortchatmodel>()

                        val t = object : GenericTypeIndicator<HashMap<String, Sortchatmodel>>() {}
                        snapshot.getValue(t)?.values?.toList()?.forEach { posts.add(it) }
                        listener.onSuccess(posts)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        listener.onSuccess(ArrayList())
                    }
                })
    }
    fun getAdminPosts(listener: OnSuccessListener<ArrayList<PostmodelBoard>>) {
        CygnusApp.refToAdminPostsInstagram()
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val posts = ArrayList<PostmodelBoard>()

                        val t = object : GenericTypeIndicator<HashMap<String, PostmodelBoard>>() {}
                        snapshot.getValue(t)?.values?.toList()?.forEach { posts.add(it) }
                        listener.onSuccess(posts)
                    }

                    override fun onCancelled(error: DatabaseError) {
                       // listener.onSuccess(error.toString())
                        Log.e("msg","ErrorAdminPostsss:"+error.toException())
                    }
                })
    }
    fun getAdminAds(listener: OnSuccessListener<ArrayList<PostmodelBoard>>) {
        CygnusApp.refToAdminAdsInstagram()
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val posts = ArrayList<PostmodelBoard>()

                        val t = object : GenericTypeIndicator<HashMap<String, PostmodelBoard>>() {}
                        snapshot.getValue(t)?.values?.toList()?.forEach { posts.add(it) }
                        listener.onSuccess(posts)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // listener.onSuccess(error.toString())
                        Log.e("msg","ErrorAdminPostsss:"+error.toException())
                    }
                })
    }

    fun getAdminQuizAds(listener: OnSuccessListener<ArrayList<PostmodelBoard>>) {
        CygnusApp.refToAdminQuizAdsInstagram()
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val posts = ArrayList<PostmodelBoard>()

                        val t = object : GenericTypeIndicator<HashMap<String, PostmodelBoard>>() {}
                        snapshot.getValue(t)?.values?.toList()?.forEach { posts.add(it) }
                        listener.onSuccess(posts)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // listener.onSuccess(error.toString())
                        Log.e("msg","ErrorAdminPostsss:"+error.toException())
                    }
                })
    }


}