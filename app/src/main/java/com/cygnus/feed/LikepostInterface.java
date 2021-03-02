package com.cygnus.feed;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

public interface LikepostInterface {
    public void getlikesTotal(VideoView vddview,String urlpath, String filename, int position, TextView tvLikes, ImageView ivLike);
    public void clicklikes(String urlpath,String filename,int position,TextView tvLikes, ImageView ivLike);
    public void getadsposition(int position,String urlpath,String filename);
}
