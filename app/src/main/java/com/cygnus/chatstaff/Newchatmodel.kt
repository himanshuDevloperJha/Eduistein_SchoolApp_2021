package com.cygnus.chatstaff
import android.os.Parcelable
import co.aspirasoft.model.BaseModel
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
class Newchatmodel(
        var postdate: Date ,
        var date: String,
        var time: String ,
        var message: String ,
        var user: String ,
        var firebaseuid: String ,
        var msgstatus: String ,
        var countunread: String ,
        var type: String


) : BaseModel(), Parcelable {

   /* val postdatestring: String
        get() {
            val formatter = SimpleDateFormat("hh:mm a, EE dd MMM, yyyy", Locale.getDefault())
            return formatter.format(postdate)
        }*/

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Newchatmodel

        if (postdate != other.postdate) return false
        if (date != other.date) return false
        if (time != other.time) return false
        if (message != other.message) return false
        if (firebaseuid != other.firebaseuid) return false
        if (user != other.user) return false
        if (type != other.type) return false
        if (msgstatus != other.msgstatus) return false
        if (countunread != other.countunread) return false

        return true
    }

    override fun hashCode(): Int {
        return postdate.hashCode()
    }

}