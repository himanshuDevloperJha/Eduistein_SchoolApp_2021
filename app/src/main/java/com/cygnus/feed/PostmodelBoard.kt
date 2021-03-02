package com.cygnus.feed

import android.os.Parcelable
import co.aspirasoft.model.BaseModel
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
class PostmodelBoard(
        var postdate: Date = Date(System.currentTimeMillis()),
        var description: String = "",
        var username: String = "",
        var urlpath: String = "",
        var userclassid: String = "",
        var filename: String = "",
        var type: String = "post",
        var views: String = "0"

) : BaseModel(), Parcelable {

    val postdatestring: String
        get() {
            val formatter = SimpleDateFormat("hh:mm a, EE dd MMM, yyyy", Locale.getDefault())
            return formatter.format(postdate)
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PostmodelBoard

        if (postdate != other.postdate) return false
        if (urlpath != other.urlpath) return false
        if (userclassid != other.userclassid) return false
        if (filename != other.filename) return false
        if (type != other.type) return false
        if (views != other.views) return false
        if (username != other.username) return false
        if (description != other.description) return false

        return true
    }

    override fun hashCode(): Int {
        return postdate.hashCode()
    }

}