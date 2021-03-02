package com.cygnus.chatstaff

import android.os.Parcelable
import co.aspirasoft.model.BaseModel
import kotlinx.android.parcel.Parcelize

@Parcelize
class Sortchatmodel(
        var postdate: String="",
        var chatusername: String="",
        var date: String="",
        var time: String="",
        var message: String=""


) : BaseModel(), Parcelable {

    /* val postdatestring: String
         get() {
             val formatter = SimpleDateFormat("hh:mm a, EE dd MMM, yyyy", Locale.getDefault())
             return formatter.format(postdate)
         }*/

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Sortchatmodel

        if (postdate != other.postdate) return false
        if (date != other.date) return false
        if (chatusername != other.chatusername) return false
        if (message != other.message) return false
        if (time != other.time) return false

        return true
    }

    override fun hashCode(): Int {
        return postdate.hashCode()
    }

}