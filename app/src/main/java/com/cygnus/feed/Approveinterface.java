package com.cygnus.feed;

import androidx.appcompat.widget.AppCompatButton;

public interface Approveinterface {
    public void approvepost(int position, String key, AppCompatButton btn_approve, String desc, String username, String url,
                        String userclassId);
    public void approvearticle(int position, String key, AppCompatButton btn_approve, String desc, String username, String url,String filename,
                            String userclassId);
    public void disapprovepost(int position, String key, AppCompatButton btn_disapprove, String desc, String username, String url,
                           String userclassId);
    public void disapprovearticle(int position, String key, AppCompatButton btn_disapprove, String desc, String username, String url,String filename,
                               String userclassId);
}
