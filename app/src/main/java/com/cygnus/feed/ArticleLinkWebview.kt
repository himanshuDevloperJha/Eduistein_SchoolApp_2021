package com.cygnus.feed

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import com.cygnus.R
import kotlinx.android.synthetic.main.activity_article_link.*


class ArticleLinkWebview : AppCompatActivity() {
    lateinit var titleat:String
    lateinit var desc:String
    lateinit var link:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_link)



        titleat=intent.getStringExtra("article_title")
        link=intent.getStringExtra("article_link")
        desc=intent.getStringExtra("article_desc")

        toolbar_articlelink.setTitle(titleat)

        val wv_atlink: WebView = findViewById(R.id.wv_atlink);
        val webSettings = wv_atlink.settings
        webSettings.javaScriptEnabled = true
        webSettings.builtInZoomControls=true
        wv_atlink.loadUrl(link)
        WebView.setWebContentsDebuggingEnabled(false)

        wv_atlink.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url.toString()
                view?.loadUrl(url)
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
              pb_atlink.visibility=View.VISIBLE
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {

                pb_atlink.visibility=View.GONE

                super.onPageFinished(view, url)
            }

            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {

                val errorMessage = "Got Error! $error"

                super.onReceivedError(view, request, error)
            }
        }
    }
}
