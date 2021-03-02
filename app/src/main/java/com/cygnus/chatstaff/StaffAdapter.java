package com.cygnus.chatstaff;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cygnus.R;
import com.cygnus.model.Teacher;

import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.ViewHolder> {
    SharedPreferences sp_loginsave;
    SharedPreferences.Editor ed_loginsave;
Context context;
    Integer chatclick_position;
ArrayList<Sortchatmodel> teacherList;
ArrayList<Chatmodel> listmessage;
String schoolid,username,sendmessage_successful;
    Chatmessageinterface chatmessageinterface;
    public StaffAdapter(Context context, ArrayList<Sortchatmodel> teacherList,
                        String schoolid,String username,String sendmessage_successful,
                        Integer chatclick_position,Chatmessageinterface chatmessageinterface) {
        this.context = context;
        this.teacherList = teacherList;
        this.schoolid = schoolid;
        this.chatclick_position = chatclick_position;
        this.username = username;
        this.sendmessage_successful = sendmessage_successful;
        this.chatmessageinterface=chatmessageinterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.custom_stafflayout,parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
holder.tv_chatwithname.setText(teacherList.get(position).getChatusername());
chatmessageinterface.chat(position,teacherList.get(position).getChatusername(),holder.tv_msgname,holder.tv_chatdateee);
/*if((teacherList.get(position).getName().equalsIgnoreCase(listmessage.get(position).getUser()))){
    holder.tv_msgname.setText(listmessage.get(position).getMessage());

}*/
//         if(sendmessage_successful.equalsIgnoreCase("true")){
//             Collections.swap(teacherList, chatclick_position, 0);
//             notifyItemMoved(chatclick_position, 0);
//          }

        holder.ll_chatname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserDetails.chatWith = teacherList.get(position).getChatusername();
                Intent i = new Intent(context, Chat.class);
                i.putExtra("schoolid_chat",schoolid);
                i.putExtra("chat_username",username);
                i.putExtra("name_chatwith", teacherList.get(position).getChatusername());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                context.startActivity(i);
                 Log.e("msg","REcentMessageUserr:"+sendmessage_successful);
//                ed_loginsave.putInt("chatclick_position",position);
//                ed_loginsave.commit();



            }
        });

    }

    @Override
    public int getItemCount() {
        return teacherList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_chatwithname,tv_msgname,tv_chatdateee;
        CircleImageView iv_pprofile;
        LinearLayout ll_chatname;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_chatwithname=itemView.findViewById(R.id.tv_chatwithname);
            iv_pprofile=itemView.findViewById(R.id.iv_pprofile);
            tv_msgname=itemView.findViewById(R.id.tv_msgname);
            tv_chatdateee=itemView.findViewById(R.id.tv_chatdateee);
            ll_chatname=itemView.findViewById(R.id.ll_chatname);
            sp_loginsave = context.getSharedPreferences("SAVELOGINDETAILS", MODE_PRIVATE);
            ed_loginsave = sp_loginsave.edit();
        }
    }
}
