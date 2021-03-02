package com.cygnus.feed

import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.CountDownTimer
import android.util.Log
import android.util.Patterns
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.View.*
import android.widget.*
import co.aspirasoft.adapter.ModelViewAdapter
import co.aspirasoft.adapter.ModelViewAdapterrr
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.cygnus.R
import com.cygnus.model.CourseFile
import com.cygnus.storage.FileManager
import com.cygnus.view.CourseFileView
import com.cygnus.view.CourseFileViewww
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_article_screen.*


class FeedAdapter(val context: Activity, val material: ArrayList<CourseFile>,
                  val fileManager: FileManager, val schoolid:String, val studentname:String,
                  val postslist:ArrayList<PostmodelBoard>, val likelist:ArrayList<LikePostsmodel>,
                val liketotalinterface:LikepostInterface)
    :  ModelViewAdapterrr<PostmodelBoard>(context, postslist, CourseFileViewww::class)

    {
    lateinit var storage:StorageReference


        override fun notifyDataSetChanged() {
            // val item1= it.name
            postslist.sortByDescending { it.postdate }
            super.notifyDataSetChanged()
         }

        

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val iv_feed: ImageView
        val iv_play: ImageView
        val iv_like: ImageView
    //    val giflike: GifImageView
        val txt_descc: TextView
        val tv_likes: TextView
        val txt_articledesc: TextView
        val fm_post: FrameLayout
        val ll_article: LinearLayout
        val txt_articlefull: TextView
        val txt_link: TextView
        val vdd_feed: VideoView
        //val rl_videofeed: RelativeLayout
        val placeholder: View
            var checklikedposts:Boolean=false
            var checklikedposts2:Boolean=false

        //  var downloadurl:String=""



        storage= Firebase.storage.getReference(schoolid+"/posts/")
        //  val v = super.getView(position, convertView, parent) as FeedFileView
        //val item = material[position]

        val v:View=LayoutInflater.from(context).inflate(R.layout.view_feed_file, parent,false)
       // val v1:View=LayoutInflater.from(context).inflate(R.layout.view_feed_file, parent,false)

        iv_like = v.findViewById(R.id.iv_like)
        iv_feed = v.findViewById(R.id.iv_feed)
       // giflike = v.findViewById(R.id.giflike)
        tv_likes = v.findViewById(R.id.tv_likes)
        txt_descc = v.findViewById(R.id.txt_descc)
        vdd_feed = v.findViewById(R.id.vdd_feed)
            //rl_videofeed = v.findViewById(R.id.rl_videofeed)
         placeholder = v.findViewById(R.id.placeholder)
            iv_play = v.findViewById(R.id.iv_play)
            txt_link = v.findViewById(R.id.txt_link)
            txt_articledesc = v.findViewById(R.id.txt_articledesc)
            ll_article = v.findViewById(R.id.ll_article)
            fm_post = v.findViewById(R.id.fm_post)
            txt_articlefull = v.findViewById(R.id.txt_articlefull)

            //diaplay ads otherwise post

            try{
                txt_descc.setText(postslist.get(position).description)
                if(postslist.get(position).urlpath.contains("jpg",ignoreCase = true) ||
                        postslist.get(position).urlpath.contains("png",ignoreCase = true)
                        || postslist.get(position).urlpath.contains("jpeg",ignoreCase = true)){
                    iv_feed.visibility= View.VISIBLE
                    vdd_feed.visibility= GONE
                    // rl_videofeed.visibility= GONE
                    ll_article.visibility= GONE
                    fm_post.visibility= VISIBLE
                    Picasso.get().load(postslist.get(position).urlpath).into(iv_feed)
                }
                else if(postslist.get(position).urlpath.contains("mp4",ignoreCase = true) ||
                        postslist.get(position).urlpath.contains("3gp",ignoreCase = true)
                        || postslist.get(position).urlpath.contains("gif",ignoreCase = true)
                        || postslist.get(position).urlpath.contains("avi",ignoreCase = true)){

                    ll_article.visibility= GONE
                    fm_post.visibility= VISIBLE
                    iv_feed.setImageResource(0)
                    if(null!=vdd_feed){
                        vdd_feed.stopPlayback()
                        vdd_feed.resume()
                    }

                    //iv_play.visibility= VISIBLE
                    Glide.with(context)
                            .load(postslist.get(position).urlpath)
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .thumbnail(0.5f)
                            .centerInside()
                            .into(iv_feed)
                    placeholder.visibility= View.VISIBLE
                    vdd_feed.visibility= VISIBLE
                    //rl_videofeed.visibility= VISIBLE

                    vdd_feed.setVideoURI(Uri.parse(postslist.get(position).urlpath))
                    vdd_feed.requestFocus()

                    //vdd_feed.setZOrderOnTop(true)

                    vdd_feed.setOnPreparedListener {

                        placeholder.visibility= GONE
                        iv_feed.visibility= View.GONE
                        vdd_feed.start()
                        it.isLooping=true
                        // pb_videopost.visibility=View.GONE
                    }




                    /* vdd_feed.setOnCompletionListener {
                         iv_feed.visibility= VISIBLE
                         iv_play.visibility= VISIBLE
                         vdd_feed.visibility= View.GONE
                         placeholder.visibility= View.GONE
                     }*/

                    /*Glide.with(context)
                            .load(postslist.get(position).urlpath)
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .thumbnail(0.5f)
                            .centerCrop()
                            .into(iv_feed)*/
                }
                else{
                    ll_article.visibility= VISIBLE
                    fm_post.visibility= GONE
//                txt_link.setMovementMethod(LinkMovementMethod.getInstance());
                    txt_articledesc.setText(postslist.get(position).urlpath)
                    txt_link.setText(postslist.get(position).filename)
                    txt_articlefull.setPaintFlags(txt_articlefull.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
                    txt_link.setPaintFlags(txt_link.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)

                    txt_articlefull.setOnClickListener {
                        val intent=Intent(context,ArticleScreen::class.java)
                        intent.putExtra("article_title",postslist.get(position).description)
                        intent.putExtra("article_link",postslist.get(position).filename)
                        intent.putExtra("article_desc",postslist.get(position).urlpath)
                        context.startActivity(intent)
                    }

                    val urlcorrect= Patterns.WEB_URL.matcher(postslist.get(position).filename).matches()

                    txt_link.setOnClickListener {
                        if(urlcorrect==true){
                            val intent=Intent(context,ArticleLinkWebview::class.java)
                            intent.putExtra("article_title",postslist.get(position).description)
                            intent.putExtra("article_link",postslist.get(position).filename)
                            intent.putExtra("article_desc",postslist.get(position).urlpath)
                            context.startActivity(intent)}
                        else{
                            Snackbar.make(txt_link,"Invalid Link",Snackbar.LENGTH_LONG).show()
                        }
                    }


                }

                //sho0w likecount
                liketotalinterface.getlikesTotal(vdd_feed,postslist.get(position).urlpath,postslist.get(position).filename,position,tv_likes,iv_like)

                if(postslist.get(position).type.equals("ad")){
                        iv_feed.isEnabled=false
                        vdd_feed.isEnabled=false
                        iv_like.isEnabled=false
                        ll_article.isEnabled=false
                        iv_like.visibility= GONE
                        tv_likes.visibility= GONE

//store ads count if not stored

                    val reff2 = FirebaseDatabase.getInstance().reference.child("ViewAdsCount")

                    reff2.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshotliked: DataSnapshot) {
                            if (dataSnapshotliked.exists()) {
                                for (datasliked in dataSnapshotliked.children) {

                                    //check whether same ad viewed by same student exists in database
                                    if (datasliked.child("username").value.toString().equals(studentname)
                                            && datasliked.child("postname").value.toString().equals(postslist.get(position).urlpath))
                                    {
                                        Log.e("onSingleTapConfirmed", "onSingleTap11"+datasliked.child("postname").value.toString())
                                        Log.e("onSingleTapConfirmed", "onSingleTap11"+datasliked.child("username").value.toString())
                                        Log.e("onSingleTapConfirmed", "onSingleTap11"+studentname)
                                        Log.e("onSingleTapConfirmed", "onSingleTap11"+postslist.get(position).urlpath)
                                        checklikedposts2=true

                                    }

                                }

                                object : CountDownTimer(100, 100) {
                                    override fun onTick(millisUntilFinished: Long) { // You don't need to use this.
                                    }

                                    override fun onFinish() {
                                        if(checklikedposts2==false){
                                            val post = LikePostsmodel(studentname,postslist.get(position).urlpath,postslist.get(position).filename)
                                            val missionsReference = FirebaseDatabase.getInstance().reference.child("ViewAdsCount").push()
                                            missionsReference.setValue(post)
                                            liketotalinterface.getadsposition(position,
                                                    postslist.get(position).urlpath,postslist.get(position).filename)




                                        }
                                    }
                                }.start()



                            }
                            else {
                                val post = LikePostsmodel(studentname,postslist.get(position).urlpath,postslist.get(position).filename)
                                val missionsReference = FirebaseDatabase.getInstance().reference.child("ViewAdsCount").push()
                                missionsReference.setValue(post)
                                liketotalinterface.getadsposition(position,
                                        postslist.get(position).urlpath,postslist.get(position).filename)

                            }
                        }

                        override fun onCancelled(p0: DatabaseError) {
                        }
                    })


                }

                if(iv_like.visibility== GONE){

                }
                ll_article.setOnClickListener {
                    //            iv_like.setImageResource(R.drawable.like)
                    ll_article.isEnabled=false
                    iv_like.isEnabled=false
                    iv_like.setImageResource(R.drawable.like)

                    val reff2 = FirebaseDatabase.getInstance().reference.
                            child(schoolid).child("LikePosts")

                    reff2.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshotliked: DataSnapshot) {
                            if (dataSnapshotliked.exists()) {
                                for (datasliked in dataSnapshotliked.children) {
                                    //Log.e("onSingleTapConfirmed", "onSingleTap11"+datasliked.child("postname").toString())
                                    //      Toast.makeText(context,"onSingleTap112"+datasliked.child("username").value.toString(),Toast.LENGTH_LONG).show()
                                    //     Toast.makeText(context,"11:"+postslist.get(position).getUrlpath(),Toast.LENGTH_LONG).show()

                                    //check whether same post liked by same student exists in database
                                    if (datasliked.child("username").value.toString().equals(studentname)
                                            && datasliked.child("postname").value.toString().equals(postslist.get(position).urlpath))
                                    {
                                        Log.e("onSingleTapConfirmed", "onSingleTap11"+datasliked.child("postname").value.toString())
                                        Log.e("onSingleTapConfirmed", "onSingleTap11"+datasliked.child("username").value.toString())
                                        Log.e("onSingleTapConfirmed", "onSingleTap11"+studentname)
                                        Log.e("onSingleTapConfirmed", "onSingleTap11"+postslist.get(position).urlpath)
                                        checklikedposts=true

                                        iv_like.setImageResource(R.drawable.like)



                                    }

                                }

                                object : CountDownTimer(100, 100) {
                                    override fun onTick(millisUntilFinished: Long) { // You don't need to use this.
                                    }

                                    override fun onFinish() {
                                        if(checklikedposts==false){
                                            val post = LikePostsmodel(studentname,postslist.get(position).urlpath,postslist.get(position).filename)
                                            val missionsReference = FirebaseDatabase.getInstance().reference.child(schoolid).
                                                    child("LikePosts").push()
                                            missionsReference.setValue(post)
                                            val c:String
                                            if(tv_likes.text.toString().equals("1 Like")){
                                                c=tv_likes.text.toString().replace(" Like", "")
                                            }
                                            else{
                                                c=tv_likes.text.toString().replace(" Likes", "")
                                            }

                                            val c1=c.toInt()+1
                                            if(c1==1){
                                                tv_likes.setText(c1.toString()+" Like")
                                            }
                                            else if(c1>1){

                                                tv_likes.setText(c1.toString()+" Likes")

                                            }

                                            liketotalinterface.clicklikes(postslist.get(position).urlpath,postslist.get(position).filename,position,tv_likes,iv_like)
                                            ll_article.isEnabled=true
                                            iv_like.isEnabled=true

                                        }
                                    }
                                }.start()



                            }
                            else {
                                val post = LikePostsmodel(studentname,postslist.get(position).urlpath,postslist.get(position).filename)
                                val missionsReference = FirebaseDatabase.getInstance().reference.child(schoolid).
                                        child("LikePosts").push()
                                missionsReference.setValue(post)

                                tv_likes.setText("1 Like")
                                liketotalinterface.clicklikes(postslist.get(position).urlpath,postslist.get(position).filename,
                                        position,tv_likes,iv_like)

                            }
                        }

                        override fun onCancelled(p0: DatabaseError) {
                        }
                    })
                }

                iv_like.setOnClickListener {
                    iv_like.setImageResource(R.drawable.like)
                    iv_feed.isEnabled=false
                    iv_like.isEnabled=false


                    val reff2 = FirebaseDatabase.getInstance().reference.
                            child(schoolid).child("LikePosts")

                    reff2.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshotliked: DataSnapshot) {
                            if (dataSnapshotliked.exists()) {
                                for (datasliked in dataSnapshotliked.children) {
                                    //Log.e("onSingleTapConfirmed", "onSingleTap11"+datasliked.child("postname").toString())
                                    //      Toast.makeText(context,"onSingleTap112"+datasliked.child("username").value.toString(),Toast.LENGTH_LONG).show()
                                    //     Toast.makeText(context,"11:"+postslist.get(position).getUrlpath(),Toast.LENGTH_LONG).show()

                                    //check whether same post liked by same student exists in database
                                    if (datasliked.child("username").value.toString().equals(studentname)
                                            && datasliked.child("postname").value.toString().equals(postslist.get(position).urlpath))
                                    {
                                        Log.e("onSingleTapConfirmed", "onSingleTap11"+datasliked.child("postname").value.toString())
                                        Log.e("onSingleTapConfirmed", "onSingleTap11"+datasliked.child("username").value.toString())
                                        Log.e("onSingleTapConfirmed", "onSingleTap11"+studentname)
                                        Log.e("onSingleTapConfirmed", "onSingleTap11"+postslist.get(position).urlpath)
                                        checklikedposts=true

                                        iv_like.setImageResource(R.drawable.like)



                                    }

                                }

                                object : CountDownTimer(100, 100) {
                                    override fun onTick(millisUntilFinished: Long) { // You don't need to use this.
                                    }

                                    override fun onFinish() {
                                        if(checklikedposts==false){
                                            val post = LikePostsmodel(studentname,postslist.get(position).urlpath,postslist.get(position).filename)
                                            val missionsReference = FirebaseDatabase.getInstance().reference.child(schoolid).
                                                    child("LikePosts").push()
                                            missionsReference.setValue(post)
                                            val c:String
                                            if(tv_likes.text.toString().equals("1 Like")){
                                                c=tv_likes.text.toString().replace(" Like", "")
                                            }
                                            else{
                                                c=tv_likes.text.toString().replace(" Likes", "")
                                            }

                                            val c1=c.toInt()+1
                                            if(c1==1){
                                                tv_likes.setText(c1.toString()+" Like")
                                            }
                                            else if(c1>1){

                                                tv_likes.setText(c1.toString()+" Likes")

                                            }
                                            liketotalinterface.clicklikes(postslist.get(position).urlpath,postslist.get(position).filename,
                                                    position,tv_likes,iv_like)
                                            iv_feed.isEnabled=true
                                            iv_like.isEnabled=true

                                        }
                                    }
                                }.start()



                            }
                            else {
                                val post = LikePostsmodel(studentname,postslist.get(position).urlpath,postslist.get(position).filename)
                                val missionsReference = FirebaseDatabase.getInstance().reference.child(schoolid).
                                        child("LikePosts").push()
                                missionsReference.setValue(post)

                                tv_likes.setText("1 Like")
                                liketotalinterface.clicklikes(postslist.get(position).urlpath,postslist.get(position).filename,
                                        position,tv_likes,iv_like)

                            }
                        }

                        override fun onCancelled(p0: DatabaseError) {
                        }
                    })
                }


                iv_feed.setOnTouchListener(object : OnTouchListener {
                    private val gestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {
                        override fun onDoubleTap(e: MotionEvent): Boolean {
                            // Toast.makeText(getContext(), "onDoubleTap", Toast.LENGTH_SHORT).show()
                            iv_like.setImageResource(R.drawable.like)
                            iv_feed.isEnabled=false
                            iv_like.isEnabled=false

                            val reff2 = FirebaseDatabase.getInstance().reference.
                                    child(schoolid).child("LikePosts")

                            reff2.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshotliked: DataSnapshot) {
                                    if (dataSnapshotliked.exists()) {
                                        for (datasliked in dataSnapshotliked.children) {
                                            //check whether same post liked by same student exists in database
                                            if (datasliked.child("username").value.toString().equals(studentname)
                                                    && datasliked.child("postname").value.toString().equals(postslist.get(position).urlpath))
                                            {
                                                Log.e("onSingleTapConfirmed", "onSingleTap11"+datasliked.child("postname").value.toString())
                                                Log.e("onSingleTapConfirmed", "onSingleTap11"+datasliked.child("username").value.toString())
                                                Log.e("onSingleTapConfirmed", "onSingleTap11"+studentname)
                                                Log.e("onSingleTapConfirmed", "onSingleTap11"+postslist.get(position).urlpath)
                                                checklikedposts=true

                                                iv_like.setImageResource(R.drawable.like)




                                            }


                                        }

                                        object : CountDownTimer(100, 100) {
                                            override fun onTick(millisUntilFinished: Long) { // You don't need to use this.
                                            }

                                            override fun onFinish() {
                                                if(checklikedposts==false){
                                                    val post = LikePostsmodel(studentname,postslist.get(position).urlpath,postslist.get(position).filename)
                                                    val missionsReference = FirebaseDatabase.getInstance().reference.child(schoolid).
                                                            child("LikePosts").push()
                                                    missionsReference.setValue(post)

                                                    val c:String
                                                    if(tv_likes.text.toString().equals("1 Like")){
                                                        c=tv_likes.text.toString().replace(" Like", "")
                                                    }
                                                    else{
                                                        c=tv_likes.text.toString().replace(" Likes", "")
                                                    }

                                                    val c1=c.toInt()+1
                                                    if(c1==1){
                                                        tv_likes.setText(c1.toString()+" Like")
                                                    }
                                                    else if(c1>1){

                                                        tv_likes.setText(c1.toString()+" Likes")

                                                    }
                                                    liketotalinterface.clicklikes(postslist.get(position).urlpath,postslist.get(position).filename,
                                                            position,tv_likes,iv_like)
                                                    iv_feed.isEnabled=true
                                                    iv_like.isEnabled=true

                                                }

                                                //check if post is liked by student already and



                                            }
                                        }.start()
                                    }
                                    else {
                                        val post = LikePostsmodel(studentname,postslist.get(position).urlpath,postslist.get(position).filename)
                                        val missionsReference = FirebaseDatabase.getInstance().reference.child(schoolid).
                                                child("LikePosts").push()
                                        missionsReference.setValue(post)


                                        tv_likes.setText("1 Like")
                                        liketotalinterface.clicklikes(postslist.get(position).urlpath,postslist.get(position).filename,
                                                position,tv_likes,iv_like)

                                    }
                                }

                                override fun onCancelled(p0: DatabaseError) {
                                }
                            })


                            return super.onDoubleTap(e)
                        }

                        override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
                            Log.d("onSingleTapConfirmed", "onSingleTap")
                            return false
                        }
                    })

                    override fun onTouch(v: View, event: MotionEvent): Boolean {
                        gestureDetector.onTouchEvent(event)
                        return true
                    }
                })

                vdd_feed.setOnTouchListener(object : OnTouchListener {
                    private val gestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {
                        override fun onDoubleTap(e: MotionEvent): Boolean {
                            iv_like.setImageResource(R.drawable.like)
                            vdd_feed.isEnabled=false
                            iv_like.isEnabled=false

                            val reff2 = FirebaseDatabase.getInstance().reference.
                                    child(schoolid).child("LikePosts")

                            reff2.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshotliked: DataSnapshot) {
                                    if (dataSnapshotliked.exists()) {
                                        for (datasliked in dataSnapshotliked.children) {

                                            //check whether same post liked by same student exists in database
                                            if (datasliked.child("username").value.toString().equals(studentname)
                                                    && datasliked.child("postname").value.toString().equals(postslist.get(position).urlpath))
                                            {
                                                Log.e("onSingleTapConfirmed", "onSingleTap11"+datasliked.child("postname").value.toString())
                                                Log.e("onSingleTapConfirmed", "onSingleTap11"+datasliked.child("username").value.toString())
                                                Log.e("onSingleTapConfirmed", "onSingleTap11"+studentname)
                                                Log.e("onSingleTapConfirmed", "onSingleTap11"+postslist.get(position).urlpath)
                                                checklikedposts=true


                                                iv_like.setImageResource(R.drawable.like)

                                            }


                                        }

                                        object : CountDownTimer(100, 100) {
                                            override fun onTick(millisUntilFinished: Long) { // You don't need to use this.
                                            }

                                            override fun onFinish() {
                                                if(checklikedposts==false){
                                                    val post = LikePostsmodel(studentname,postslist.get(position).urlpath,postslist.get(position).filename)
                                                    val missionsReference = FirebaseDatabase.getInstance().reference.child(schoolid).
                                                            child("LikePosts").push()
                                                    missionsReference.setValue(post)

                                                    val c:String
                                                    if(tv_likes.text.toString().equals("1 Like")){
                                                        c=tv_likes.text.toString().replace(" Like", "")
                                                    }
                                                    else{
                                                        c=tv_likes.text.toString().replace(" Likes", "")
                                                    }

                                                    val c1=c.toInt()+1
                                                    if(c1==1){
                                                        tv_likes.setText(c1.toString()+" Like")
                                                    }
                                                    else if(c1>1){

                                                        tv_likes.setText(c1.toString()+" Likes")

                                                    }
                                                    liketotalinterface.clicklikes(postslist.get(position).urlpath,postslist.get(position).filename,
                                                            position,tv_likes,iv_like)
                                                    vdd_feed.isEnabled=true
                                                    iv_like.isEnabled=true
                                                }
                                            }
                                        }.start()


                                    }
                                    else {
                                        val post = LikePostsmodel(studentname,postslist.get(position).urlpath,postslist.get(position).filename)
                                        val missionsReference = FirebaseDatabase.getInstance().reference.child(schoolid).
                                                child("LikePosts").push()
                                        missionsReference.setValue(post)

                                        tv_likes.setText("1 Like")
                                        liketotalinterface.clicklikes(postslist.get(position).urlpath,postslist.get(position).filename,
                                                position,tv_likes,iv_like)


                                    }
                                }

                                override fun onCancelled(p0: DatabaseError) {
                                }
                            })


                            return super.onDoubleTap(e)
                        }

                        override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
                            Log.d("onSingleTapConfirmed", "onSingleTap")
                            return false
                        }
                    })

                    override fun onTouch(v: View, event: MotionEvent): Boolean {
                        gestureDetector.onTouchEvent(event)
                        return true
                    }
                })






        }
        catch (e:Exception){

        }

            /*iv_play.setOnClickListener {
           iv_feed.visibility= GONE
           iv_play.visibility= GONE
           vdd_feed.visibility= View.VISIBLE
           placeholder.visibility= View.VISIBLE

           vdd_feed.setVideoURI(Uri.parse(postslist.get(position).urlpath))
           vdd_feed.requestFocus()

           //vdd_feed.setZOrderOnTop(true)

           vdd_feed.setOnPreparedListener {


               placeholder.visibility= GONE

               vdd_feed.start()
               // pb_videopost.visibility=View.GONE
           }




           vdd_feed.setOnCompletionListener {
               iv_feed.visibility= VISIBLE
               iv_play.visibility= VISIBLE
               vdd_feed.visibility= View.GONE
               placeholder.visibility= View.GONE
           }
        }*/


        return v
    }

    }