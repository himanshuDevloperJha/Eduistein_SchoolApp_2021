package com.cygnus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.pierfrancescosoffritti.youtubeplayer.player.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.youtubeplayer.player.PlayerConstants;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerInitListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerView;

public class YoutubevideoPlay extends AppCompatActivity {
String youtubelink;
ProgressBar pb_youtube;
    private Context context;
    private com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayer youTubePlayer;
    VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_youtubevideo_play);

        youtubelink=getIntent().getStringExtra("youtube_link");
        pb_youtube =  findViewById(R.id.pb_youtube);

        YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_view);
        youTubePlayerView.getPlayerUIController().showFullscreenButton(false);
        youTubePlayerView.getPlayerUIController().showYouTubeButton(false);
        //youTubePlayerView.getPlayerUIController().enableLiveVideoUI(true);
        getLifecycle().addObserver(youTubePlayerView);



        youTubePlayerView.initialize(new YouTubePlayerInitListener() {
            @Override
            public void onInitSuccess(@NonNull final YouTubePlayer youTubePlayer) {

                CustomPlayerUIController customPlayerUIController = new CustomPlayerUIController(YoutubevideoPlay.this, youTubePlayer);

                youTubePlayer.addListener(customPlayerUIController);

                youTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady() {
                        youTubePlayer.loadVideo(youtubelink, 0f);
                    }
                });
            }
        }, true);




//        YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player_view);
//        getLifecycle().addObserver(youTubePlayerView);
//
//
//        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
//            @Override
//            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
//                pb_youtube.setVisibility(View.GONE);
//                String videoId = youtubelink;
//                youTubePlayer.loadVideo(videoId, 0f);
//            }
//        });



       /* final WebView webview = (WebView) findViewById(R.id.videoView);
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        webview.setWebChromeClient(new WebChromeClientCustomPoster());
        webview.setWebViewClient(new AutoPlayVideoWebViewClient());
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
webview.setVisibility(View.VISIBLE);
                    pb_youtube.setVisibility(View.GONE);

            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

            }
        });
        Log.e("msg", "onClickVideo1: "+youtubelink );

        webview.loadUrl("https://www.youtube.com/embed/"+youtubelink);*/


    }

    public class CustomPlayerUIController extends AbstractYouTubePlayerListener
            implements YouTubePlayerFullScreenListener {
        CustomPlayerUIController(Context ctx,
                                 com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayer youTubePlayer1) {

            context = ctx;
            youTubePlayer = youTubePlayer1;

        }

        @Override
        public void onReady() {
            pb_youtube.setVisibility(View.GONE);
        }

        @Override
        public void onStateChange(@PlayerConstants.PlayerState.State int state) {
            if(state == PlayerConstants.PlayerState.PLAYING || state == PlayerConstants.PlayerState.PAUSED || state == PlayerConstants.PlayerState.VIDEO_CUED)
              pb_youtube.setVisibility(View.GONE);
             else
               if(state == PlayerConstants.PlayerState.BUFFERING)
                   pb_youtube.setVisibility(View.VISIBLE);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onCurrentSecond(float second) {
            // videoCurrentTimeTextView.setText(second+"");
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onVideoDuration(float duration) {
            // videoDurationTextView.setText(duration+"");
        }

        @Override
        public void onYouTubePlayerEnterFullScreen() {

        }

        @Override
        public void onYouTubePlayerExitFullScreen() {


        }
    }

    private class WebChromeClientCustomPoster extends WebChromeClient {

        @Override
        public Bitmap getDefaultVideoPoster() {
            return Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
        }
    }
    private class AutoPlayVideoWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            // mimic onClick() event on the center of the WebView
            long delta = 100;
            long downTime = SystemClock.uptimeMillis();
            float x = view.getLeft() + (view.getWidth()/2);
            float y = view.getTop() + (view.getHeight()/2);

            MotionEvent tapDownEvent = MotionEvent.obtain(downTime, downTime + delta, MotionEvent.ACTION_DOWN, x, y, 0);
            tapDownEvent.setSource(InputDevice.SOURCE_CLASS_POINTER);
            MotionEvent tapUpEvent = MotionEvent.obtain(downTime, downTime + delta + 2, MotionEvent.ACTION_UP, x, y, 0);
            tapUpEvent.setSource(InputDevice.SOURCE_CLASS_POINTER);

            view.dispatchTouchEvent(tapDownEvent);
            view.dispatchTouchEvent(tapUpEvent);
        }
    }
}
