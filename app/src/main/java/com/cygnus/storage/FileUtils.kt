package com.cygnus.storage

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException

object FileUtils {

    @Throws(IOException::class)
    fun File.openInExternalApp(context: Activity) {
        try {
            val extension = this.extension
            val mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "*/*"
            val uri = FileProvider.getUriForFile(context, "com.cygnus.fileprovider", this)

            val i = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mime)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(i)
        } catch (ex: Exception) {
            throw IOException(ex.message)
        }
    }

    fun Uri.getLastPathSegmentOnly(context: Context): String? {
        var name: String? = null
        when {
            ContentResolver.SCHEME_FILE == this.scheme -> name = this.lastPathSegment
            ContentResolver.SCHEME_CONTENT == this.scheme -> {
                val returnCursor: Cursor? = context.contentResolver.query(this, null, null, null, null)
                if (returnCursor != null && returnCursor.moveToFirst()) {
                    val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    name = returnCursor.getString(nameIndex)
                    returnCursor.close()
                }
            }
        }
        return name
    }

}