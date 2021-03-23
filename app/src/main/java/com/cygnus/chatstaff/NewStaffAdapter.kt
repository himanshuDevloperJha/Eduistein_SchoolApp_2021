package com.cygnus.chatstaff

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import co.aspirasoft.adapter.ModelViewAdapterrr
import com.cygnus.R
import com.firebase.client.Firebase
import com.firebase.client.FirebaseError
import com.firebase.client.Query
import de.hdodenhof.circleimageview.CircleImageView


class NewStaffAdapter(val context: Activity, val timestampList: ArrayList<Sortchatmodel>,
                 val schoolid:String, val username:String,val userrtypeeee:String,
                   val chatmessageinterface:Chatmessageinterface)
    :  ModelViewAdapterrr<Sortchatmodel>(context, timestampList, StaffView::class)

{


    override fun notifyDataSetChanged() {
        // val item1= it.name
        timestampList.sortByDescending { it.postdate }
        super.notifyDataSetChanged()
    }



    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var tv_chatwithname: TextView?=null
        var tv_msgname:TextView? = null
        var tv_msgcounter:TextView? = null
        var tv_chatdateee:TextView? = null
        var iv_pprofile: CircleImageView? = null
        var ll_chatname: LinearLayout? = null

        val v:View=LayoutInflater.from(context).inflate(R.layout.custom_stafflayout, parent,false)

        tv_chatwithname=v.findViewById(R.id.tv_chatwithname)
        tv_chatdateee=v.findViewById(R.id.tv_chatdateee)
        tv_msgname=v.findViewById(R.id.tv_msgname)
        ll_chatname=v.findViewById(R.id.ll_chatname)
        iv_pprofile=v.findViewById(R.id.iv_pprofile)
        tv_msgcounter=v.findViewById(R.id.tv_msgcounter)


        tv_chatwithname.setText(timestampList.get(position).chatusername)
        tv_chatdateee.setText(timestampList.get(position).time)
     //   tv_msgname.setText(teacherList.get(position).message)
        chatmessageinterface.chat(position, timestampList.get(position).chatusername, tv_msgname,tv_chatdateee,tv_msgcounter)


ll_chatname.setOnClickListener(View.OnClickListener {
    try{
        val reference1 = Firebase("https://cygnus-3554a.firebaseio.com/" + schoolid + "/messages/" +
                UserDetails.username + "_" + timestampList.get(position).chatusername)
        val query: Query = reference1
        query.addListenerForSingleValueEvent(object :
                com.firebase.client.ValueEventListener {
            override fun onCancelled(p0: FirebaseError?) {

            }

            override fun onDataChange(p0: com.firebase.client.DataSnapshot?) {
                for(d in p0!!.children){
                    val key =d?.key
                    reference1.child(key).child("msgstatus").setValue("read")

                }

            }

        })

        val reference2 = Firebase("https://cygnus-3554a.firebaseio.com/" + schoolid + "/messages/" +
                timestampList.get(position).chatusername + "_" +   UserDetails.username)
        val query2: Query = reference2
        query.addListenerForSingleValueEvent(object :
                com.firebase.client.ValueEventListener {
            override fun onCancelled(p0: FirebaseError?) {

            }

            override fun onDataChange(p0: com.firebase.client.DataSnapshot?) {
                for(d in p0!!.children){
                    val key =d?.key
                    reference1.child(key).child("msgstatus").setValue("read")

                }

            }

        })

        UserDetails.chatWith = timestampList.get(position).chatusername
        val i = Intent(context, Chat::class.java)
        i.putExtra("schoolid_chat", schoolid)
        i.putExtra("chat_username", username)
        i.putExtra("name_chatwith", timestampList.get(position).chatusername)
           i.putExtra("userrtypeeee", userrtypeeee)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        context.startActivity(i)
    }
    catch (e:Exception){}


        })

        return v
    }

}