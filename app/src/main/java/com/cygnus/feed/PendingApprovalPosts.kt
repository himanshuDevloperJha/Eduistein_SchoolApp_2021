package com.cygnus.feed

import android.content.ActivityNotFoundException
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.cygnus.model.CourseFile
import com.cygnus.storage.FileManager
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_pending_approval_posts.*
import java.io.File
import com.cygnus.R
import com.cygnus.model.StoreNotifications
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.io.IOException
import java.util.HashMap


class PendingApprovalPosts : AppCompatActivity(), Approveinterface {
    private lateinit var materialManager1: FileManager
    private lateinit var materialManager: FileManager
    private lateinit var pendingadapter: PendingpostsAdapter
    private var postslist = ArrayList<Postsmodel>()
    private val material = ArrayList<CourseFile>()
    lateinit var teachername: String
    lateinit var schoolid: String
    lateinit var teacherid: String
    lateinit var ref:FirebaseDatabase
    lateinit var urii:Uri
    lateinit var storage: StorageReference
    lateinit var storage1: StorageReference
    lateinit var  downloadurl: String
    var tokenlist: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pending_approval_posts)


            teachername=intent.getStringExtra("studentname")
            schoolid=intent.getStringExtra("studentschoolid")
            teacherid=intent.getStringExtra("teacheridd")



        materialManager1 = FileManager.newInstance(this,
                "$schoolid/approvalposts/")
        materialManager = FileManager.newInstance(this,
                "$schoolid/posts/")
        storage= Firebase.storage.getReference("$schoolid/posts/")

        showPosts()
    }
    fun showPosts(){
        // pb_posts.visibility=View.VISIBLE
        material.clear()
        postslist.clear()

        getPendingPosts()



        //get like posts to show like button enable


    }
fun getPendingPosts(){
    //get pending posts to show description of post
    val ref = FirebaseDatabase.getInstance().reference.child(schoolid).child("ApprovalPosts")
    ref.addListenerForSingleValueEvent(object : ValueEventListener {

        override fun onDataChange(dataSnapshotposts: DataSnapshot) {
            if(dataSnapshotposts.exists()){
                for (datasposts in dataSnapshotposts.children) {

                    val key = datasposts.key
                    Log.e("msg","urlpathhhhhhhhh144:"+datasposts.child("urlpath").value.toString())


                    postslist.add(Postsmodel(datasposts.child("description").value.toString(),
                            datasposts.child("username").value.toString(),
                            datasposts.child("urlpath").value.toString(),
                            datasposts.child("userclassid").value.toString(),
                            datasposts.child("filename").value.toString(),
                            datasposts.child("postdate").value.toString(), datasposts.key))
                    // feedAdapter?.notifyDataSetChanged()
                    materialManager1.listAll().addOnSuccessListener { result ->
                        result?.items?.forEach { reference ->
                            reference.metadata.addOnSuccessListener { metadata ->
                                // pb_posts.visibility= View.VISIBLE

                                    if(datasposts.child("filename").value.toString().equals(reference.name,ignoreCase = true)){
                                       Log.e("msg", "onCancelledreferencename1"+datasposts.child("filename").value.toString())
                                       Log.e("msg", "onCancelledreferencename2"+reference.name)
                                   //     Toast.makeText(applicationContext,"2:"+reference.name,Toast.LENGTH_LONG).show()
                                        material.add(CourseFile(reference.name, metadata))
                                    }



                            }
                        }

                    }



                }
                Log.e("msg","PostListSize:::"+postslist.size)
                pendingadapter = PendingpostsAdapter(this@PendingApprovalPosts, material,
                        materialManager1,schoolid,teachername,teacherid,postslist,this@PendingApprovalPosts)
                rv_pendingposts.layoutManager = LinearLayoutManager(this@PendingApprovalPosts,
                        LinearLayoutManager.VERTICAL ,false)
                rv_pendingposts.adapter = pendingadapter
                // Collections.reverse(postslist)
                //   feedAdapter?.notifyDataSetChanged()



            }
            else{
                Snackbar.make(rv_pendingposts, "No Pending Posts", Snackbar.LENGTH_SHORT).show()

            }

        }
        override fun onCancelled(p0: DatabaseError) {
            //   Toast.makeText(applicationContext,"2:"+p0.toString(),Toast.LENGTH_LONG).show()

        }
    })
}

    private fun uploadFiletoPosts(url: File, materialManager: FileManager, filename: String, desc: String, username: String,
                                  userclassId: String, btnApprove: AppCompatButton) {
        try {
            // val uri: Uri = Uri.fromFile(url)
            val uri = FileProvider.getUriForFile(this,
                    "com.cygnus.fileprovider", url)
            urii=uri
            val status = Snackbar.make(rv_pendingposts, "Approving..", Snackbar.LENGTH_INDEFINITE)
            status.show()
            materialManager.upload(filename, urii)
                    .addOnSuccessListener {
                        it.metadata?.let { metadata ->

                            storage1= Firebase.storage.getReference("$schoolid/approvalposts/$filename")
                            storage1.delete().addOnSuccessListener {
                                // File deleted successfully
                                //download url from posts
                                storage.child(filename).downloadUrl.addOnSuccessListener {
                                    downloadurl=it.toString()
                                    // val post = PostmodelBoard(,studentname,)
                                    val post = PostmodelBoard(description = desc, username = username,
                                            urlpath=downloadurl,userclassid=userclassId,filename =filename )
                                    val missionsReference = FirebaseDatabase.getInstance().reference.child(schoolid).
                                            child("Posts").push()
                                    missionsReference.setValue(post)
                                    fetchTokens("Your post has been approved",userclassId,username)



                                    pendingadapter.notifyDataSetChanged()
                                    showPosts()

                                }.addOnFailureListener {
                                    status.setText("Failed::"+it.toString())
                                    btnApprove.isEnabled=true
                                }
                            }.addOnFailureListener {
                                Log.d("msg", "onFailure: did not delete file")
                                status.setText("Failed2::"+it.toString())
                                btnApprove.isEnabled=true
                            }


                        }

                        status.setText("Post has been approved..")

                        Handler().postDelayed({ status.dismiss() }, 2500L)
                    }
                    .addOnFailureListener {
                        Log.e("msg","FeedError1:"+it.message)
                        btnApprove.isEnabled=true
                        status.setText(it.message ?: getString(R.string.upload_failure))
                        Handler().postDelayed({ status.dismiss() }, 2500L)
                    }
        } catch (e: ActivityNotFoundException) {
            btnApprove.isEnabled=true
            Toast.makeText(applicationContext, "No application found which can open the file", Toast.LENGTH_SHORT).show()
        }
    }

    override fun approvepost(position: Int, key: String, btn_approve: AppCompatButton,
                         desc: String, username: String, url: String, userclassId:String) {

        try{
            for(posts in postslist){
                for(material in material){
                    //check click on approved post key with key of databse and remove that key from approvalposts databse band add it to posts database
                    if(posts.key.equals(key) && posts.filename.equals(material.name)){
                        val ref = FirebaseDatabase.getInstance().reference
                        val applesQuery = ref.child(schoolid).child("ApprovalPosts").
                                orderByKey().equalTo(key)
                        Log.e("msg", "onCancelledreferencename4"+posts.filename)
                        Log.e("msg", "onCancelledreferencename45"+material.name)

                        applesQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                for (appleSnapshot in dataSnapshot.children) {
                                    //add approved post in posts databse so that show it to all

                                    materialManager1.download(
                                            material.name,
                                            OnSuccessListener { file ->
                                                try {

                                                    uploadFiletoPosts(file,materialManager,material.name,desc,
                                                            username,userclassId,btn_approve)
                                                    appleSnapshot.ref.removeValue()
                                                    btn_approve.isEnabled=true
                                                } catch (ex: IOException) {
                                                    btn_approve.isEnabled=true
                                                    Snackbar.make(rv_pendingposts, ex.message ?: "Could not open file.", Snackbar.LENGTH_SHORT).show()
                                                }
                                            },
                                            OnFailureListener {
                                                // v.setStatus(CourseFileView.FileStatus.Cloud)
                                                btn_approve.isEnabled=true
                                                Snackbar.make(rv_pendingposts, "hlo:"+it.message ?: "Could not download file."+it.toString(), Snackbar.LENGTH_SHORT).show()
                                            }
                                    )
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                btn_approve.isEnabled=true
                                Log.e("msg", "onCancelled", databaseError.toException())
                            }
                        })

                    }
                }

            }

        }
        catch (e:Exception){
            btn_approve.isEnabled=true
        }
    }



    override fun approvearticle(position: Int, key: String, btn_approve: AppCompatButton, desc: String, username: String,
                                url: String, filename:String,userclassId: String) {
        val status = Snackbar.make(rv_pendingposts, "Approving..", Snackbar.LENGTH_INDEFINITE)
        status.show()
        try{
            for(posts in postslist){
                    //check click on approved post key with key of databse and remove that key from approvalposts databse band add it to posts database
                    if(posts.key.equals(key) && posts.urlpath.equals(url)){
                        val ref = FirebaseDatabase.getInstance().reference
                        val applesQuery = ref.child(schoolid).child("ApprovalPosts").
                                orderByKey().equalTo(key)
                        Log.e("msg", "onCancelledreferencename4"+posts.filename)

                        applesQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                for (appleSnapshot in dataSnapshot.children) {
                                    //add approved post in posts databse so that show it to all

                                    val post = PostmodelBoard(description = desc, username = username,
                                            urlpath=url,userclassid=userclassId,filename =filename )
                                    val missionsReference = FirebaseDatabase.getInstance().reference.child(schoolid).
                                            child("Posts").push()
                                    missionsReference.setValue(post)
                                    fetchTokens("Your post has been approved",userclassId,username)

                                    appleSnapshot.ref.removeValue()
                                    btn_approve.isEnabled=true

                                    status.setText("Post has been approved..")

                                    Handler().postDelayed({ status.dismiss() }, 2500L)

                                    pendingadapter.notifyDataSetChanged()
                                    showPosts()

                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                btn_approve.isEnabled=true
                                Log.e("msg", "onCancelled", databaseError.toException())
                            }
                        })

                    }


            }

        }
        catch (e:Exception){
            btn_approve.isEnabled=true
        }
    }


    override fun disapprovepost(position: Int, key: String, btn_disapprove: AppCompatButton, desc: String, username: String,
                            url: String, userclassId:String) {
        val status = Snackbar.make(rv_pendingposts, "Disapproving..", Snackbar.LENGTH_INDEFINITE)
        status.show()
       try{
           for(posts in postslist){
               for(material in material){
                   //check click on approved post key with key of databse and remove that key from approvalposts databse band add it to posts database
                   if(posts.key.equals(key)&& posts.filename.equals(material.name)){
                       val filename=material.name
                       val ref = FirebaseDatabase.getInstance().reference
                       val applesQuery = ref.child(schoolid).child("ApprovalPosts").
                               orderByKey().equalTo(key)

                       applesQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                           override fun onDataChange(dataSnapshot: DataSnapshot) {
                               if(dataSnapshot.exists()){
                                   for (appleSnapshot in dataSnapshot.children) {


                                       fetchTokens("Your post has been disapproved",userclassId,username)
                                       storage1= Firebase.storage.getReference("$schoolid/approvalposts/$filename")
                                       storage1.delete().addOnSuccessListener {
                                           // File deleted successfully
                                           appleSnapshot.ref.removeValue()
                                           btn_disapprove.isEnabled=true
                                           pendingadapter.notifyDataSetChanged()
                                           showPosts()
                                       }.addOnFailureListener {
                                          // status.setText(it.message ?: getString(R.string.upload_failure))
                                           Handler().postDelayed({ status.dismiss() }, 2500L)

                                       }

                                   }
                               }

                           }

                           override fun onCancelled(databaseError: DatabaseError) {
                               btn_disapprove.isEnabled=true
                              // status.setText(databaseError.message ?: getString(R.string.upload_failure))
                               Handler().postDelayed({ status.dismiss() }, 2500L)
                           }
                       })

                   }
               }

           }
       }
       catch (e:Exception){
           btn_disapprove.isEnabled=true
       }

    }

    override fun disapprovearticle(position: Int, key: String, btn_disapprove: AppCompatButton, desc: String,
                                   username: String, url: String, filename:String,userclassId: String) {
        val status = Snackbar.make(rv_pendingposts, "Disapproving..", Snackbar.LENGTH_INDEFINITE)
        status.show()
        try{
            for(posts in postslist){
                //check click on approved post key with key of databse and remove that key from approvalposts databse band add it to posts database
                if(posts.key.equals(key)&& posts.urlpath.equals(url)){
                    val ref = FirebaseDatabase.getInstance().reference
                    val applesQuery = ref.child(schoolid).child("ApprovalPosts").
                            orderByKey().equalTo(key)

                    applesQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if(dataSnapshot.exists()){
                                for (appleSnapshot in dataSnapshot.children) {


                                    fetchTokens("Your post has been disapproved",userclassId,username)
                                    appleSnapshot.ref.removeValue()
                                    btn_disapprove.isEnabled=true
                                    pendingadapter.notifyDataSetChanged()
                                    showPosts()

                                }
                            }

                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            btn_disapprove.isEnabled=true
                            // status.setText(databaseError.message ?: getString(R.string.upload_failure))
                            Handler().postDelayed({ status.dismiss() }, 2500L)
                        }
                    })

                }


            }
        }
        catch (e:Exception){
            btn_disapprove.isEnabled=true
        }
    }

    private fun fetchTokens(body: String, userclassId: String, username: String) {
        tokenlist.clear()
        val reference: DatabaseReference
        reference = FirebaseDatabase.getInstance().reference.child(schoolid).
                child("StudentTokens")
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (datas in dataSnapshot.children) {
                    // Log.e("msg", "TOKENCLASS" + datas.child("classId").value.toString())
                    //Log.e("msg", "TOKENCLASS1" + datas.child("token").value.toString())
                    if (userclassId.equals(datas.child("classId").value.toString(),ignoreCase = true)
                            && username.equals(datas.child("username").value.toString(),ignoreCase = true)) {
                        try {
                            val s = datas.key
                            val studenttoken = datas.child("token").value.toString()
                            tokenlist.add(studenttoken)




                        } catch (e: Exception) {
                            Toast.makeText(applicationContext, ""+e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Toast.makeText(applicationContext, "No scheduled class", Toast.LENGTH_SHORT).show();
                    }
                }

                sendFCMPush(tokenlist,body,userclassId)
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }
    private fun sendFCMPush(tokenlist: ArrayList<String>, body: String, userclassId: String) {
        val SERVER_KEY = "AAAAav-QFhw:APA91bG7ChbWR2kwz_FBMKgaDV8IZ_PMmED0Rp_sy7f0PtlZm37t-uAJRnUwyLYSM4Z-kSg_Jj9Xv9O8x4r_L5iQC9JAKhhTPt-ga5nmEqCBMcqgaUMtDnF5ponwXi8mD31k481DWHoF"
        var obj: JSONObject? = null
        var objData: JSONObject? = null
        var dataobjData: JSONObject? = null
        val jsonArray = JSONArray()

        var regId: JSONArray? = null;


        try {
            obj = JSONObject()
            objData = JSONObject()
            regId = JSONArray()

            for (item in tokenlist) {
                regId.put(item);
            }

            objData.put("body", body)
            objData.put("title", "Eduistein")
            objData.put("sound", "default")
            objData.put("icon", "icon_name") //   icon_name
            // objData.put("tag", token)
            objData.put("priority", "high")
            dataobjData = JSONObject()
            dataobjData.put("title", "Eduistein")
            dataobjData.put("text", body)

            // obj.put("to", token)
            obj.put("registration_ids", regId);
            obj.put("notification", objData)
            obj.put("data", dataobjData)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val jsObjRequest: JsonObjectRequest = object : JsonObjectRequest(Method.POST,
                "https://fcm.googleapis.com/fcm/send", obj,
                Response.Listener { response ->
                    Log.e("msg", "onResponse111111: $response")
                    val post = StoreNotifications(userclassId, body)

                    val missionsReference =
                            FirebaseDatabase.getInstance().reference.child(schoolid).
                                    child("Notifications").push()
                    missionsReference.setValue(post)


                },
                Response.ErrorListener { error ->
                    Log.e("msg", "onResponse1111112: $error")
                    // Toast.makeText(applicationContext, "1:" + error.toString(), Toast.LENGTH_SHORT).show()
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = "key=$SERVER_KEY"
                params["Content-Type"] = "application/json"
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(jsObjRequest)
    }
}
