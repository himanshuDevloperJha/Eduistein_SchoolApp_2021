package com.cygnus.feed

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import com.cygnus.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_article_screen.*
import java.lang.Exception

class ArticleScreen : AppCompatActivity() {
lateinit var titleat:String
lateinit var desc:String
lateinit var link:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_screen)

        titleat=intent.getStringExtra("article_title")
        link=intent.getStringExtra("article_link")
        desc=intent.getStringExtra("article_desc")

        toolbar_article.setTitle(titleat)
        tv_attitle.setText(titleat)
        tv_atdescription.setText(desc)
        tv_atlink.setText(link)

        val urlcorrect= Patterns.WEB_URL.matcher(link).matches()

        tv_atlink.setPaintFlags(tv_atlink.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
        tv_atlink.setOnClickListener(View.OnClickListener {
            try { // String url = "https://twitter.com/parmeshar_tv";
                val url: String = link
                val intent = Intent(Intent.ACTION_VIEW)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                intent.data = Uri.parse(url)
                startActivity(intent)
            } catch (e: Exception) {
                Snackbar.make(tv_atlink, "Invalid Link", Snackbar.LENGTH_LONG).show()
                //   Toast.makeText(context, "Invalid Link", Toast.LENGTH_SHORT).show()
            }
        })

        tv_atlink.setOnClickListener {
            if(urlcorrect==true){
            val intent=Intent(applicationContext,ArticleLinkWebview::class.java)
            intent.putExtra("article_title",titleat)
            intent.putExtra("article_link",link)
            intent.putExtra("article_desc",desc)
            startActivity(intent)}
            else{
                Snackbar.make(tv_atlink,"Invalid Link",Snackbar.LENGTH_LONG).show()

            }
        }

    }
}
