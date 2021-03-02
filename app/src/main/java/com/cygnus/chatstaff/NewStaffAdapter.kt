package com.cygnus.chatstaff

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import co.aspirasoft.adapter.ModelViewAdapterrr
import com.cygnus.R
import com.cygnus.model.Teacher
import com.cygnus.view.CourseFileViewww
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView


class NewStaffAdapter(val context: Activity, val teacherList: ArrayList<Sortchatmodel>,
                 val schoolid:String, val username:String,
                   val chatmessageinterface:Chatmessageinterface)
    :  ModelViewAdapterrr<Sortchatmodel>(context, teacherList, StaffView::class)

{


    override fun notifyDataSetChanged() {
        // val item1= it.name
        teacherList.sortByDescending { it.postdate }
        super.notifyDataSetChanged()
    }



    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var tv_chatwithname: TextView?=null
        var tv_msgname:TextView? = null
        var tv_chatdateee:TextView? = null
        var iv_pprofile: CircleImageView? = null
        var ll_chatname: LinearLayout? = null

        val v:View=LayoutInflater.from(context).inflate(R.layout.custom_stafflayout, parent,false)

        tv_chatwithname=v.findViewById(R.id.tv_chatwithname)
        tv_chatdateee=v.findViewById(R.id.tv_chatdateee)
        tv_msgname=v.findViewById(R.id.tv_msgname)
        ll_chatname=v.findViewById(R.id.ll_chatname)
        iv_pprofile=v.findViewById(R.id.iv_pprofile)


        tv_chatwithname.setText(teacherList.get(position).chatusername)
        tv_chatdateee.setText(teacherList.get(position).time)
        tv_msgname.setText(teacherList.get(position).message)
       // chatmessageinterface.chat(position, teacherList.get(position).chatusername, tv_msgname,tv_chatdateee)


ll_chatname.setOnClickListener(View.OnClickListener {
            UserDetails.chatWith = teacherList.get(position).chatusername
            val i = Intent(context, Chat::class.java)
            i.putExtra("schoolid_chat", schoolid)
            i.putExtra("chat_username", username)
            i.putExtra("name_chatwith", teacherList.get(position).chatusername)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            context.startActivity(i)
        })

        return v
    }

}