package com.cygnus.feed

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import co.aspirasoft.util.PermissionUtils
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.cygnus.R
import com.cygnus.dao.NoticeBoardDao
import com.cygnus.model.CourseFile
import com.cygnus.model.StoreNotifications
import com.cygnus.storage.FileManager
import com.cygnus.storage.FileUtils.getLastPathSegmentOnly
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_feed.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class FeedActivity : AppCompatActivity(), LikepostInterface {
    //   lateinit var standard: String
    lateinit var studentschoolid: String
    lateinit var student_teacheridd: String
    lateinit var usertype: String
   lateinit var studentschool_namee: String
    lateinit var studentname: String
    lateinit var studentclassId: String
    private var pickRequestCode = RESULT_ACTION_PICK_MATERIAL
    private lateinit var materialManager: FileManager
    private lateinit var materialManager1: FileManager
    private var feedAdapter: FeedAdapter? = null
    private val material = ArrayList<CourseFile>()
    private var postslist = ArrayList<PostmodelBoard>()
    private var postslist1 = ArrayList<PostmodelBoard>()
    private var postslist2 = ArrayList<PostmodelBoard>()
    private var postslistads = ArrayList<PostmodelBoard>()
 //   private lateinit var postslist: ArrayList<PostmodelBoard>

    private var likelist = ArrayList<LikePostsmodel>()
    private var adsviewslist = ArrayList<LikePostsmodel>()
      var adsviewslist2=ArrayList<LikePostsmodel>()
    lateinit var tv_postname:TextView
    lateinit var descriptiontxt:String
    lateinit var articledesc_txt:String
    lateinit var articlelink_txt:String
    lateinit var filename:String
    lateinit var urii:Uri
    lateinit var storage: StorageReference
    lateinit var storage1: StorageReference
    lateinit var  downloadurl: String
     var  likedalready: Boolean=false
    var tokenlist: ArrayList<String> = ArrayList()
    lateinit var vddview:VideoView
    lateinit var reference1: DatabaseReference
var positionpost=0
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        //  standard = intent.getStringExtra("user_standard")
        studentschoolid = intent.getStringExtra("studentschoolid")
        studentname = intent.getStringExtra("studentname")
        studentschool_namee = intent.getStringExtra("studentschool_namee")
        usertype = intent.getStringExtra("studenttype")
       // Toast.makeText(applicationContext,"studentschoolid:"+studentschoolid,Toast.LENGTH_LONG).show()

        try {
            studentclassId = intent.getStringExtra("studentclassId")
            student_teacheridd = intent.getStringExtra("student_teacheridd")
      //     Toast.makeText(applicationContext,"TeaccherId:"+student_teacheridd,Toast.LENGTH_LONG).show()
        }
        catch (e:Exception){}

        supportActionBar?.title = "Feed"


//             val current: LocalDateTime = LocalDateTime.now()

        //    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
        //  date_time = current.format(formatter)
        // date_time = ""

        materialManager = FileManager.newInstance(this,
                "$studentschoolid/posts/")
        materialManager1 = FileManager.newInstance(this,
                "$studentschoolid/approvalposts/")
        storage= Firebase.storage.getReference("$studentschoolid/posts/")
        storage1= Firebase.storage.getReference("$studentschoolid/approvalposts/")
        reference1 = FirebaseDatabase.getInstance().reference.child("admin").child("feedads")

        showPosts()

      //  swipeToRefresh.setColorSchemeResources(R.color.colorAccent)


       /* swipeToRefresh.setOnRefreshListener {
            showPosts()
            getlikesTotal()
            swipeToRefresh.isRefreshing = false
        //    pb_posts.visibility=View.GONE
        }*/


        addButtonpost.setOnClickListener{
            val dialog1 = Dialog(this)
            dialog1.setContentView(R.layout.custom_addpost)
            dialog1.show()

            val ed_title = dialog1.findViewById(R.id.ed_title) as EditText
            val ed_link = dialog1.findViewById(R.id.ed_link) as EditText
            val ed_articledescription = dialog1.findViewById(R.id.ed_description) as EditText
            val  tv_addpost = dialog1.findViewById(R.id.tv_addpost) as TextView
            val  tv_addarticle = dialog1.findViewById(R.id.tv_addarticle) as TextView
            val  ll_addpost = dialog1.findViewById(R.id.ll_addpost) as LinearLayout
            tv_postname = dialog1.findViewById(R.id.tv_postname) as TextView
            val btn_uploadpost = dialog1.findViewById(R.id.btn_uploadpost) as AppCompatButton

            btn_uploadpost.setOnClickListener {
                if(tv_addarticle.visibility == View.VISIBLE){
                    if (ed_title.text!!.isEmpty() &&
                            ed_articledescription.text!!.isEmpty()) {
                        Snackbar.make(ed_title,"Enter title and description",Snackbar.LENGTH_LONG).show()
                        //Toast.makeText(this,"Please fill mandatory fields",Toast.LENGTH_SHORT).show()
                    } else if (ed_title.text!!.isEmpty()) {
                        Snackbar.make(ed_title,"Enter title",Snackbar.LENGTH_LONG).show()

                    } else if (ed_articledescription.text!!.isEmpty()) {
                        Snackbar.make(ed_title,"Enter description",Snackbar.LENGTH_LONG).show()
                    }
                    else{
                        descriptiontxt=ed_title.text.toString()
                        articledesc_txt=ed_articledescription.text.toString()
                        articlelink_txt=ed_link.text.toString()
                        dialog1.dismiss()
                        val status = Snackbar.make(FeedListnew, "Uploading..", Snackbar.LENGTH_INDEFINITE)
                        status.show()

                        // add article to firebase
                        if(usertype.equals("Teacher") || usertype.equals("School")){
                            if(usertype.equals("School")){
                                student_teacheridd=studentschoolid
                            }
                            val post = PostmodelBoard(description = descriptiontxt, username = studentname,
                                    urlpath=articledesc_txt,userclassid=student_teacheridd,filename=articlelink_txt)
                            val missionsReference = FirebaseDatabase.getInstance().reference.child(studentschoolid).
                                    child("Posts").push()
                            missionsReference.setValue(post)
                            status.setText("Post Uploaded")
                            Handler().postDelayed({ status.dismiss() }, 3500L)
                            showPosts()
                        }
                        else{
                            val body = "Hey! Your Student "+studentname+" has uploaded a post.\nClick to approve or disapprove !"

                            val post = PostmodelBoard(description = descriptiontxt, username = studentname,
                                    urlpath=ed_articledescription.text.toString(),userclassid=studentclassId,filename=articlelink_txt)
                            val missionsReference = FirebaseDatabase.getInstance().reference.child(studentschoolid).
                                    child("ApprovalPosts").push()
                            missionsReference.setValue(post)
                            fetchTokens(body)

                            status.setText("Your Post has been submitted\nPlease wait for approval")
                            Handler().postDelayed({ status.dismiss() }, 3500L)
                        }
                    }
                }
                else if(tv_addpost.visibility == View.VISIBLE)
                    {
                        if (ed_title.text!!.isEmpty() &&
                         tv_postname.text!!.isEmpty()) {
                        Snackbar.make(ed_title,"Enter title and upload image or video",Snackbar.LENGTH_LONG).show()
    //Toast.makeText(this,"Please fill mandatory fields",Toast.LENGTH_SHORT).show()
                        } else if (ed_title.text!!.isEmpty()) {
                         Snackbar.make(ed_title,"Enter title",Snackbar.LENGTH_LONG).show()

                        } else if (tv_postname.text!!.isEmpty()) {
                        Snackbar.make(ed_title,"Upload image or video",Snackbar.LENGTH_LONG).show()
                        }
                        else{
                        descriptiontxt=ed_title.text.toString()
                        dialog1.dismiss()
                         uploadFile(materialManager,materialManager1, filename, urii, feedAdapter,descriptiontxt)
                        }
                    }

            }

            tv_addpost.setOnClickListener {
                ll_addpost.visibility= View.VISIBLE
                tv_addarticle.visibility= View.GONE

                if (PermissionUtils.requestPermissionIfNeeded(
                                this, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                getString(R.string.permission_storage),
                                RC_WRITE_PERMISSION)) {
                    pickFile(RESULT_ACTION_PICK_MATERIAL)
                }

            }

            tv_addarticle.setOnClickListener {
                ll_addpost.visibility= View.GONE
                tv_addarticle.visibility= View.VISIBLE
                ed_link.visibility= View.VISIBLE
                ed_articledescription.visibility= View.VISIBLE
            }
        }



    }



    fun showPosts(){
       // pb_posts.visibility=View.VISIBLE
        likelist.clear()
        val refflikeposts = FirebaseDatabase.getInstance().reference.child(studentschoolid).child("LikePosts")

        refflikeposts.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshotliked: DataSnapshot) {
                if (dataSnapshotliked.exists()) {
                    for (datasliked in dataSnapshotliked.children) {

                        likelist.add(LikePostsmodel(datasliked.child("username").value.toString(),
                                datasliked.child("postname").value.toString(),
                                datasliked.child("filename").value.toString()))

                    }
                }
                else {
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
        material.clear()
        postslist.clear()
        postslist1.clear()
        postslist2.clear()
        postslistads.clear()





        materialManager.listAll().addOnSuccessListener { result ->
            result?.items?.forEach { reference ->
                reference.metadata.addOnSuccessListener { metadata ->

                    material.add(CourseFile(reference.name, metadata))
                       feedAdapter?.notifyDataSetChanged()

                }
            }
        }
        NoticeBoardDao.getAdminAds(
                OnSuccessListener {
                    postslistads = it

                })

        NoticeBoardDao.getAdminPosts(
                OnSuccessListener {
                    postslist1 = it

                })
        NoticeBoardDao.getPostsInstagram(
                studentschoolid,
                OnSuccessListener {

                    postslist2 = it

                    postslist.addAll(postslistads)
                    postslist.addAll(postslist1)
                    postslist.addAll(postslist2)
Collections.shuffle(postslist)

                    feedAdapter?.notifyDataSetChanged()

                  //  postslist.sortByDescending { it.postdate }



                }


        )
        feedAdapter = FeedAdapter(this@FeedActivity, material,
                materialManager,studentschoolid,studentname,postslist,likelist,this)
          // FeedListnew.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL ,false)
        //FeedListnew.setScrollingCacheEnabled(false)
        FeedListnew.adapter = feedAdapter



     /*   FeedListnew.setRecyclerListener {
            vddview.stopPlayback()
        }*/
        //get posts to show description of post
  /* val ref = FirebaseDatabase.getInstance().reference.child(studentschoolid).child("Posts")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshotposts: DataSnapshot) {
                if(dataSnapshotposts.exists()){
                    for (datasposts in dataSnapshotposts.children) {
                        val key = datasposts.key
                        Log.e("msg","urlpathhhhhhhhh144:"+datasposts.child("urlpath").value.toString())


                       *//* postslist.add(Postsmodel(datasposts.child("description").value.toString(),
                                datasposts.child("username").value.toString(),
                                datasposts.child("urlpath").value.toString(),
                                datasposts.child("postdate").value.toString()))*//*
                       // feedAdapter?.notifyDataSetChanged()

                        val t = object : GenericTypeIndicator<HashMap<String, PostmodelBoard>>() {}
                        datasposts.getValue(t)?.values?.toList()?.forEach { postslist.add(it) }

                    }
                   // Collections.reverse(postslist)
                //   feedAdapter?.notifyDataSetChanged()



                }

            }
            override fun onCancelled(p0: DatabaseError) {
            //   Toast.makeText(applicationContext,"2:"+p0.toString(),Toast.LENGTH_LONG).show()

            }
        })*/



        //get like posts to show like button enable


    }

    private fun pickFile(requestCode: Int) {
        this.pickRequestCode = requestCode
        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.type = "*/*"
        i.addCategory(Intent.CATEGORY_OPENABLE)
        val mimetypes = arrayOf("image/*", "video/*")
        i.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
        startActivityForResult(i, requestCode)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                RESULT_ACTION_PICK_MATERIAL -> {
                    data?.data?.getLastPathSegmentOnly(this)?.let { filename ->

                        //get file name and uri and set image name to textview
                        this.filename=filename
                        urii=data.data!!
                        tv_postname.setText(filename)

                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RC_WRITE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickFile(pickRequestCode)
            }
        }
    }
    @SuppressLint("NewApi")
    private fun uploadFile(fm: FileManager, fm1: FileManager,filename: String, data: Uri,
                           feedAdapter: FeedAdapter?, descriptiontxt: String) {
        try {
            MaterialAlertDialogBuilder(this)
                    .setTitle(String.format(getString(R.string.upload_title),"Posts of School",studentschool_namee))
                    .setMessage(String.format(getString(R.string.upload_confirm), filename))
                    .setPositiveButton(android.R.string.yes) { dialog, _ ->
                        dialog.dismiss()

                        if(usertype.equals("Teacher") || usertype.equals("School")){
                            if(usertype.equals("School")){
                                student_teacheridd=studentschoolid
                            }
                            val status = Snackbar.make(FeedListnew, getString(R.string.upload_started), Snackbar.LENGTH_INDEFINITE)
                            status.show()
                            fm.upload(filename, data)
                                .addOnSuccessListener {
                                    it.metadata?.let { metadata ->


                                        material?.add(CourseFile(filename, metadata))
                                        //feedAdapter?.notifyDataSetChanged()


                                        storage.child(filename).downloadUrl.addOnSuccessListener {
                                            downloadurl=it.toString()
                                           // val post = PostmodelBoard(,studentname,)
                                            val post = PostmodelBoard(description = descriptiontxt, username = studentname,
                                                    urlpath=downloadurl,userclassid=student_teacheridd,filename=filename)
                                            val missionsReference = FirebaseDatabase.getInstance().reference.child(studentschoolid).
                                                    child("Posts").push()
                                            missionsReference.setValue(post)

                                        }.addOnFailureListener {
                                            Log.e("msg","FeedErrorrrrrrrrrrrr12:"+it.toString())

                                        }

                                    }

                                    status.setText(getString(R.string.upload_success))
                                    showPosts()

                                    Handler().postDelayed({ status.dismiss() }, 2500L)
                                }
                                .addOnFailureListener {
                                    Log.e("msg","FeedError1:"+it.message)

                                    status.setText(it.message ?: getString(R.string.upload_failure))
                                    Handler().postDelayed({ status.dismiss() }, 2500L)
                                }
                        }
                        else{
                            val status = Snackbar.make(FeedListnew, getString(R.string.upload_started), Snackbar.LENGTH_INDEFINITE)
                            status.show()
                            val body = "Hey! Your Student "+studentname+" has uploaded a post.\nClick to approve or disapprove !"
                            fm1.upload(filename, data)
                                    .addOnSuccessListener {
                                        it.metadata?.let { metadata ->

                                            storage1.child(filename).downloadUrl.addOnSuccessListener {
                                                downloadurl=it.toString()
                                                // val post = PostmodelBoard(,studentname,)
                                                val post = PostmodelBoard(description = descriptiontxt, username = studentname,
                                                        urlpath=downloadurl,userclassid=studentclassId,filename=filename)
                                                val missionsReference = FirebaseDatabase.getInstance().reference.child(studentschoolid).
                                                        child("ApprovalPosts").push()
                                                missionsReference.setValue(post)
                                                fetchTokens(body)

                                            }.addOnFailureListener {
                                                Log.e("msg","FeedErrorrrrrrrrrrrr12:"+it.toString())

                                            }

                                        }

                                        status.setText("Your Post has been submitted\nPlease wait for approval")
                                      //  showPosts()

                                        Handler().postDelayed({ status.dismiss() }, 2500L)
                                    }
                                    .addOnFailureListener {
                                        Log.e("msg","FeedError1:"+it.message)

                                        status.setText(it.message ?: getString(R.string.upload_failure))
                                        Handler().postDelayed({ status.dismiss() }, 2500L)
                                    }
                        }


                    }
                    .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                        dialog.cancel()
                    }
                    .show()
        } catch (e: Exception) {
            Log.e("msg","FeedError:"+e.toString())
            Toast.makeText(applicationContext, "" + e.toString(), Toast.LENGTH_SHORT).show()
        }

    }


    companion object {
        private const val RESULT_ACTION_PICK_MATERIAL = 100
        private const val RESULT_ACTION_PICK_HOMEWORK = 150
        private const val RESULT_ACTION_PICK_FILES = 250
        private const val RC_WRITE_PERMISSION = 200
    }

    private fun fetchTokens(body: String) {
        tokenlist.clear()
        val reference: DatabaseReference
        reference = FirebaseDatabase.getInstance().reference.child(studentschoolid).
                child("TeacherTokens")
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (datas in dataSnapshot.children) {
                   // Log.e("msg", "TOKENCLASS" + datas.child("classId").value.toString())
                    //Log.e("msg", "TOKENCLASS1" + datas.child("token").value.toString())
                    if (student_teacheridd.equals(datas.child("teacherId").
                                    value.toString(),ignoreCase = true)) {
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

                sendFCMPush(tokenlist,body);
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }
    private fun sendFCMPush(tokenlist:  ArrayList<String>, body: String) {
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
                    val post = StoreNotifications(studentclassId, body)

                    val missionsReference =
                            FirebaseDatabase.getInstance().reference.child(studentschoolid).
                                    child("TeacherNotifications").push()
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



    public fun getlikesTotal() {
      likelist.clear()
        val refflikeposts = FirebaseDatabase.getInstance().reference.child(studentschoolid).child("LikePosts")

        refflikeposts.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshotliked: DataSnapshot) {
                if (dataSnapshotliked.exists()) {
                    for (datasliked in dataSnapshotliked.children) {

                        likelist.add(LikePostsmodel(datasliked.child("username").value.toString(),
                                datasliked.child("postname").value.toString(),
                                datasliked.child("filename").value.toString()))

                    }
                }
                else {
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    override fun getadsposition(position: Int,  urlpath: String, filename: String) {
        try{
            if(postslist.get(position).type.equals("ad")){
                Log.e( ":msg", "DATABASEKEYidddd8899991:" + adsviewslist2.size)

                adsviewslist.clear()
                adsviewslist2.clear()
                val refflikeposts = FirebaseDatabase.getInstance().reference.child("ViewAdsCount")

                refflikeposts.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshotliked: DataSnapshot) {
                        if (dataSnapshotliked.exists()) {
                            for (datasliked in dataSnapshotliked.children) {

                                adsviewslist.add(LikePostsmodel(datasliked.child("username").value.toString(),
                                        datasliked.child("postname").value.toString(),
                                        datasliked.child("filename").value.toString()))

                            }

                            val filter: HashSet<LikePostsmodel> =  HashSet(adsviewslist)
                            adsviewslist2 =  ArrayList<LikePostsmodel>(filter)
                            Log.e( ":msg", "DATABASEKEYidddd8899991:" + adsviewslist2.size)

                            var countposts=0
                            for(adsviews in adsviewslist2){
                                try{
                                    Log.e( ":msg", "DATABASEKEYidddd1:" + adsviews.username+",   "+studentname )
                                    //Log.e( ":msg", "DATABASEKEYidddd2:" + adsviews.postname+",   "+urlpath )
                                    Log.e( ":msg", "DATABASEKEYidddd3:" + adsviews.filename+",   "+filename )
                                    if(adsviews.username.equals(studentname) && adsviews.postname.equals(urlpath) &&
                                            adsviews.filename.equals(filename)){
                                        countposts++
                                        reference1.orderByChild("urlpath").equalTo(urlpath).
                                                addListenerForSingleValueEvent(object : ValueEventListener {
                                                    override fun onDataChange(dataSnapshot1: DataSnapshot) {
                                                        for (snapshot1 in dataSnapshot1.children) {

                                                            Log.e( ":msg", "DATABASEKEYid:" + filename+",     position"+position );
                                                            val idKey = snapshot1.key
                            val viewss = snapshot1.child("views").value.toString()
                            val v = viewss.toInt() + 1
                                                            reference1.child(idKey!!).child("views").setValue(v.toString())

                                                        }
                                                    }

                                                    override fun onCancelled(error: DatabaseError) {
                                                        Toast.makeText(this@FeedActivity, "error$error", Toast.LENGTH_LONG).show()
                                                    }
                                                })

                                    }
                                }catch (e:Exception){
                                    Log.e("msg","FeedError:"+e.toString())

                                }
                            }
                        }
                        else {
                            Log.e("msg","FeedError:")

                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {
                        Log.e("msg","FeedError:"+p0.toString())

                    }
                })
            }

        }
        catch (e:Exception){}
    }

    override fun getlikesTotal(vddview:VideoView,urlpath:String,filename:String,position: Int,
                              tvLikes: TextView, ivLike: ImageView) {
        var countposts=0
       this.vddview=vddview
        for(likes in likelist){
            try{
                Log.e("msg","usernameeeeeee1:"+likes.username+"   ,usernamee2::"+studentname)
                Log.e("msg","usernameeeeeee2:"+likes.postname+"   ,postname::"+postslist.get(position).urlpath)

                if (likes.username.equals(studentname)
                        &&  likes.postname.equals(urlpath) && likes.filename.equals(filename))
                {
                    likedalready=true


                        ivLike.setImageResource(R.drawable.like)


                    //feedAdapter?.notifyDataSetChanged()
                }
            }catch (e:Exception){
                Log.e("msg","FeedError:"+e.toString())

            }
            try{
                if(likes.postname.equals(urlpath)
                        && likes.filename.equals(filename)){
                    countposts++
                    Log.e("msg","POSTLIKESSSSSS:"+countposts.toString()+",,,desc::"+likes.postname)
                    if(countposts==1){
                        tvLikes.setText(countposts.toString()+" Like")
                    }
                    else if(countposts>1){

                        tvLikes.setText(countposts.toString()+" Likes")

                    }
                }

            }catch (e:Exception){
                Log.e("msg","FeedError:"+e.toString())

            }
        }

       // FeedListnew.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL ,false)
      // FeedListnew.adapter = feedAdapter
    }

    override fun clicklikes(urlpath:String,filename:String,position: Int, tvLikes: TextView, ivLike: ImageView) {
        getlikesTotal()

        object : CountDownTimer(100, 100) {
            override fun onTick(millisUntilFinished: Long) { // You don't need to use this.
            }

            override fun onFinish() {
                var countposts=0
                for(likes in likelist){
                    try{

                        if (likes.username.equals(studentname)
                                &&  likes.postname.equals(urlpath) && likes.filename.equals(filename))
                        {
                            likedalready=true
                            ivLike.setImageResource(R.drawable.like)

                            //feedAdapter?.notifyDataSetChanged()
                        }
                    }catch (e:Exception){
                        Log.e("msg","FeedError:"+e.toString())

                    }
                    try{
                        if(likes.postname.equals(urlpath) && likes.filename.equals(filename)){
                            countposts++
                            Log.e("msg","POSTLIKESSSSSS:"+countposts.toString()+",,,desc::"+likes.postname)
                            if(countposts==1){
                                tvLikes.setText(countposts.toString()+" Like")
                            }
                            else if(countposts>1){

                                tvLikes.setText(countposts.toString()+" Likes")

                            }
                        }
                    }catch (e:Exception){
                        Log.e("msg","FeedError:"+e.toString())

                    }
                }
            }
        }.start()
    }




    override fun onPause() {
        super.onPause()
        if(null!=vddview){
            vddview.stopPlayback()
            vddview.resume()
        }
    }
    override fun onStop() {
        super.onStop()
        if(null!=vddview){
            vddview.stopPlayback()
            vddview.resume()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        if(null!=vddview){
            vddview.stopPlayback()
            vddview.resume()
        }
    }
}