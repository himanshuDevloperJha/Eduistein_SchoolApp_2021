package com.cygnus.chatstaff;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cygnus.R;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Chat extends AppCompatActivity {
    RecyclerView layout;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    Firebase reference1, reference2;
    String schoolid,chatwith,chat_username,userrtypeeee;
    Toolbar toolbar_chat;
    ChatAdapter chatAdapter;
    List<Newchatmodel> chatlist=new ArrayList<>();
    List<String> datelist=new ArrayList();
    String chatwithtoken="";
   ArrayList<String> tokenlist = new ArrayList();
    String user_tokennnnn,currentUser;
 SharedPreferences sp_loginsave;
     SharedPreferences.Editor ed_loginsave;
     int messagecounter=0;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        layout = (RecyclerView) findViewById(R.id.layout1);
        sendButton = (ImageView)findViewById(R.id.sendButton);
        messageArea = (EditText)findViewById(R.id.messageArea);
        scrollView = (ScrollView)findViewById(R.id.scrollView);
        toolbar_chat = (Toolbar) findViewById(R.id.toolbar_chat);
        Firebase.setAndroidContext(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        schoolid=getIntent().getStringExtra("schoolid_chat");
        chatwith=getIntent().getStringExtra("name_chatwith");
        chat_username=getIntent().getStringExtra("chat_username");
        userrtypeeee=getIntent().getStringExtra("userrtypeeee");
        user_tokennnnn=getIntent().getStringExtra("user_tokennnnn");
        currentUser=getIntent().getStringExtra("currentUser");
        //firebaseuid  = FirebaseAuth.getInstance().getCurrentUser().getUid();

        toolbar_chat.setTitle(chatwith);


        if(userrtypeeee.equals("Student")){
            chat_username=getIntent().getStringExtra("name_chatwith");
            chatwith=getIntent().getStringExtra("chat_groupuser");
            toolbar_chat.setTitle(chat_username);
        }
else if(userrtypeeee.equals("Teacher")){
    chatwith=getIntent().getStringExtra("name_chatwith");
    chat_username=getIntent().getStringExtra("chat_username");
    toolbar_chat.setTitle(chatwith);
    Log.e("msg","UserMessagewwwwe1233:"+chat_username);
}
        UserDetails.username =chat_username;
        UserDetails.chatWith =chatwith;

        Log.e("msg","UserMessagewwwwe12:"+chat_username);
        Log.e("msg","UserMessagewwwwe13:"+UserDetails.username);
        Log.e("msg","UserMessagewwwwe14:"+UserDetails.chatWith);
        sp_loginsave = getSharedPreferences("SAVELOGINDETAILS", MODE_PRIVATE);
        ed_loginsave = sp_loginsave.edit();
        /*LocalDateTime current = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
        String formatted = current.format(formatter);*/

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

       Calendar calander = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
        String time = simpleDateFormat.format(calander.getTime());

//       LocalDateTime current = LocalDateTime.now();
    //   DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
       String datetime  = formattedDate+" "+time;

        reference1 = new Firebase("https://cygnus-3554a.firebaseio.com/"+schoolid+"/messages/" + UserDetails.username + "_" + UserDetails.chatWith);
        reference2 = new Firebase("https://cygnus-3554a.firebaseio.com/"+schoolid+"/messages/" + UserDetails.chatWith + "_" + UserDetails.username);
        //reference3 = new Firebase("https://cygnus-3554a.firebaseio.com/"+schoolid+"/messages/" +  UserDetails.username  + "_" +"GroupChatStudents");
      //  reference4 = new Firebase("https://cygnus-3554a.firebaseio.com/"+schoolid+"/messages/" + "GroupChatStudents" + "_" + UserDetails.username);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if(!messageText.equals("")){


//                   if(userrtypeeee.equals("Student")){
//                       messagecounter++;
//                       Newchatmodel post1 = new Newchatmodel(new Date(System.currentTimeMillis()),formattedDate,
//                               time, messageText, chat_username,firebaseuid,"unread",String.valueOf(messagecounter),"1");
//                       reference3.push().setValue(post1);
//
//
//
//
//                   }
//                   else{
                       messagecounter++;
                       Newchatmodel post1 = new Newchatmodel(new Date(System.currentTimeMillis()),formattedDate,
                               time, messageText, getIntent().getStringExtra("chat_username"),user_tokennnnn,"unread",String.valueOf(messagecounter),"1");
                       Newchatmodel post2 = new Newchatmodel(new Date(System.currentTimeMillis()),formattedDate,
                               time, messageText, getIntent().getStringExtra("chat_username"),user_tokennnnn,"unread",String.valueOf(messagecounter),"2");
                       reference1.push().setValue(post1);
                       reference2.push().setValue(post2);
                     //  reference3.push().setValue(post2);
                       //reference4.push().setValue(post2);



                       DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child(schoolid).child("NamewithTimestampMessages");
                       reference1.addListenerForSingleValueEvent(new ValueEventListener() {

                           @Override
                           public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshots1) {
                               for (final com.google.firebase.database.DataSnapshot datas1 : dataSnapshots1.getChildren()) {
                                   if (chatwith.equalsIgnoreCase(datas1.child("chatusername").getValue().toString())) {
                                       try {
                                           Long tsLong = System.currentTimeMillis()/1000;
                                           String ts = tsLong.toString();
                                           Sortchatmodel post = new Sortchatmodel(ts, chatwith,datetime,time,messageText);
                                           String timekey = datas1.getKey();
                                           Log.e("msg", "VALUESQUIZ113336668889987666876:" + timekey);
                                           reference1.child(timekey).child("postdate").setValue(post.getPostdate());
                                           reference1.child(timekey).child("date").setValue(post.getDate());
                                           reference1.child(timekey).child("message").setValue(post.getMessage());
                                           reference1.child(timekey).child("chatusername").setValue(post.getChatusername());
                                           reference1.child(timekey).child("time").setValue(post.getTime());

                                       } catch ( Exception e) {
                                           //Toast.makeText(applicationContext, ""+e.toString(), Toast.LENGTH_SHORT).show();
                                       }
                                   }

                               }

                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError databaseError) {

                           }





                       });
                    Log.e("msg", "CHATUSERNAMEEE1:" + chatwith);
                       //get student tokens and add it to list for notifications
                       DatabaseReference reference = FirebaseDatabase.getInstance().getReference().
                               child(schoolid).child("ChatTokens");
                       reference.addListenerForSingleValueEvent(new ValueEventListener() {

                           @Override
                           public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshots) {
                               for (final com.google.firebase.database.DataSnapshot datas2 : dataSnapshots.getChildren()) {


                                    if(userrtypeeee.equals("Teacher")){
                                            if (chatwith.equalsIgnoreCase(datas2.child("groupname").getValue().toString())) {
                                                     try {
                                                chatwithtoken = datas2.child("token").getValue().toString();
                                                tokenlist.add(chatwithtoken);

                                                     } catch ( Exception e) {
            //Toast.makeText(applicationContext, ""+e.toString(), Toast.LENGTH_SHORT).show();
                                                     }
                                                         }
                                            }

                                    else if(userrtypeeee.equals("Student")){
                                        if (chatwith.equalsIgnoreCase(datas2.child("username").getValue().toString()) ||
                                        ! currentUser.equalsIgnoreCase(datas2.child("username").getValue().toString())) {
                                                    try {
                                                chatwithtoken = datas2.child("token").getValue().toString();

                                                tokenlist.add(chatwithtoken);
                                                        Log.e("msg", "CHATUSERNAMEEETOKENNNNSSS:" + datas2.child("token").getValue().toString());

                                                    } catch ( Exception e) {
                                             }
                                             }
                                            }


                               }
                            //   Toast.makeText(Chat.this, ""+tokenlist.size(), Toast.LENGTH_SHORT).show();

                               String body = "New message arrived from " +chat_username+ ".\nClick to Check Now !";
                               sendFCMPush(tokenlist, body);
                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError databaseError) {

                           }
                       });




                }
            }


        });

        messageArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // chatAdapter.notifyDataSetChanged();
                // layout.getLayoutManager().scrollToPosition(chatlist.size()-1);

                //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

            }
        });
        getChat();







    }


    public void getChat(){
        chatlist.clear();
        //tokenlist.clear();
        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String userName = map.get("user").toString();
                String date = map.get("date").toString();


                datelist.add(date);
                List<String> datefinallist=new ArrayList();

                for (int x=0;x<datelist.size();x++) {
                    if (datefinallist.contains(datelist.get(x))) {
                        datefinallist.add("");

                    } else {
                        datefinallist.add(datelist.get(x));

                    }
                    Log.e("msg","UserMessagewwwweMessage12:"+datefinallist.get(x));
                  //  Log.e("msg","UserMessagewwwweMessage12:"+datelist.size());

                }

                if(userName.equals(getIntent().getStringExtra("chat_username"))){
                    //  addMessageBox( message, 1);

                    chatlist.add(new Newchatmodel(new Date(System.currentTimeMillis()),map.get("date").toString(),
                            map.get("time").toString(), map.get("message").toString(),map.get("user").toString(),
                            user_tokennnnn,map.get("msgstatus").toString(),  map.get("countunread").toString(),"1"));

                    LinearLayoutManager ll=new LinearLayoutManager(Chat.this);
                    layout.setLayoutManager(ll);
                    ll.setStackFromEnd(true);
                    chatAdapter = new ChatAdapter(Chat.this,chatlist,datefinallist);
                    layout.setAdapter(chatAdapter);
                    layout.getLayoutManager().scrollToPosition(chatlist.size()-1);
                    //   layout.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
                }
                else{
                    //  addMessageBox( message, 2);
                    chatlist.add(new Newchatmodel(new Date(System.currentTimeMillis()),map.get("date").toString(),
                            map.get("time").toString(), map.get("message").toString(),map.get("user").toString(),
                            user_tokennnnn,map.get("msgstatus").toString(),  map.get("countunread").toString(),"2"));

                       /* DatabaseReference reference = FirebaseDatabase.getInstance().getReference().
                                child(schoolid).child("ChatTokens");
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshots) {
                                for (final com.google.firebase.database.DataSnapshot datas2 : dataSnapshots.getChildren()) {
                                    Log.e("msg", "CHATUSERNAMEEE12:" + datas2.child("username").getValue().toString());

                                        if (map.get("user").toString().equalsIgnoreCase(datas2.child("username").getValue().toString())) {
                                            try {
                                                chatwithtoken = datas2.child("token").getValue().toString();
                                                tokenlist.add(chatwithtoken);

                                            } catch ( Exception e) {
                                            }
                                        }


                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });*/


                    LinearLayoutManager ll=new LinearLayoutManager(Chat.this);
                    layout.setLayoutManager(ll);
                    ll.setStackFromEnd(true);
                    chatAdapter = new ChatAdapter(Chat.this,chatlist,datefinallist);
                    layout.setAdapter(chatAdapter);
                    layout.getLayoutManager().scrollToPosition(chatlist.size()-1);


                    // layout.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
                }




            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
    class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{
        Context context;
        List<Newchatmodel> chatlist;
        List<String> datelist;
        public ChatAdapter(Context context,   List<Newchatmodel> chatlist,List<String> datelist) {
            this.context = context;
            this.chatlist = chatlist;
            this.datelist = datelist;

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(context).inflate(R.layout.custom_chatlayout,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            if(chatlist.get(position).getType().equals("1")) {

                holder.tv_msgtime.setTextSize(10);
                holder.tv_msgtime.setText(chatlist.get(position).getTime());

                holder.tv_chatmsg.setTextColor(Color.WHITE);
                //   holder.tv_chatmsg.setText(chatlist.get(position).getMessage());
                holder.tv_chatmsg.setText(Html.fromHtml(chatlist.get(position).getMessage()));
                holder.tv_chatmsg.setTextSize(16);
                holder.fr_chat.setBackgroundResource(R.drawable.rounded_corner1);
                ((LinearLayout) holder.ll_chat).setGravity(Gravity.RIGHT);
                messageArea.setText("");
            }
            else{
                if(chatwith.toLowerCase().contains("class")){
                    holder.tv_msguser.setVisibility(View.VISIBLE);
                    holder.tv_msguser.setText(chatlist.get(position).getUser());
                }
                if(userrtypeeee.equals("Student")){
                    holder.tv_msguser.setVisibility(View.VISIBLE);
                    holder.tv_msguser.setText(chatlist.get(position).getUser());
                }


                holder.tv_msgtime.setTextSize(10);
                holder.tv_msgtime.setText(chatlist.get(position).getTime());

                holder.tv_chatmsg.setTextColor(Color.BLACK);
                holder.tv_chatmsg.setText(chatlist.get(position).getMessage());
                // holder.tv_chatdate.setText(chatlist.get(position).getDate());
                holder.tv_chatmsg.setText(Html.fromHtml(chatlist.get(position).getMessage()));
                holder.tv_chatmsg.setTextSize(16);
                holder.fr_chat.setBackgroundResource(R.drawable.rounded_corner2);
                ((LinearLayout)  holder.ll_chat).setGravity(Gravity.LEFT);
                messageArea.setText("");
            }

           if(datelist.get(position).isEmpty()){
                holder.header_date.setVisibility(View.GONE) ;
            }else{
                holder.header_date.setText(datelist.get(position));
            }

        }

        @Override
        public int getItemCount() {
            return chatlist.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            TextView tv_chatmsg,tv_msgtime,header_date,tv_msguser;
            LinearLayout ll_chat;
            LinearLayout fr_chat;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tv_chatmsg=itemView.findViewById(R.id.tv_chatmsg);
                ll_chat=itemView.findViewById(R.id.ll_chat);
                tv_msgtime=itemView.findViewById(R.id.tv_msgtime);
                fr_chat=itemView.findViewById(R.id.fr_chat);
                header_date=itemView.findViewById(R.id.header_date);
                tv_msguser=itemView.findViewById(R.id.tv_msguser);
                // tv_chatdate=itemView.findViewById(R.header_date.tv_chatdate);
            }
        }
    }

    private void sendFCMPush( ArrayList<String> tokenlist, String body) {

        final String SERVER_KEY= "AAAAav-QFhw:APA91bG7ChbWR2kwz_FBMKgaDV8IZ_PMmED0Rp_sy7f0PtlZm37t-uAJRnUwyLYSM4Z-kSg_Jj9Xv9O8x4r_L5iQC9JAKhhTPt-ga5nmEqCBMcqgaUMtDnF5ponwXi8mD31k481DWHoF";

        JSONObject obj = new JSONObject();
        JSONObject objData =new JSONObject();
        JSONObject dataobjData = new JSONObject();

        JSONArray regId=new JSONArray();


        try {

            for (String item : tokenlist) {
                regId.put(item);
            }

            objData.put("body", body);
            objData.put("title", "Eduistein");
            objData.put("sound", "default");
            objData.put("icon", "icon_name"); //   icon_name
            // objData.put("tag", token)
            objData.put("priority", "high");
            dataobjData = new JSONObject();
            dataobjData.put("title", "Eduistein");
            dataobjData.put("text", body);

            // obj.put("to", token)
            obj.put("registration_ids", regId);
            obj.put("notification", objData);
            obj.put("data", dataobjData);
        } catch ( JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST,
                "https://fcm.googleapis.com/fcm/send", obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("msg", "onResponse111111Chattmessage: "+response.toString() );
                       // Toast.makeText(Chat.this, ""+response.toString(), Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("msg", "onResponse1111112: "+error.toString() );
                        Toast.makeText(Chat.this, "Chat Error Notification:"+error.toString(), Toast.LENGTH_SHORT).show();

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "key=" + SERVER_KEY);
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        requestQueue.add(jsObjRequest);
    }

}