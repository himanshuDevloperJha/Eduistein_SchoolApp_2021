package com.cygnus.storage

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import co.aspirasoft.adapter.ModelViewAdapter
import com.cygnus.model.CourseFile
import com.cygnus.storage.FileUtils.openInExternalApp
import com.cygnus.view.CourseFileView
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.IOException


class MaterialAdapter(val context: Activity, val material: ArrayList<CourseFile>,
                      val fileManager: FileManager)
    : ModelViewAdapter<CourseFile>(context, material, CourseFileView::class) {

    override fun notifyDataSetChanged() {


       // val item1= it.name
        material.sortByDescending { it.metadata.creationTimeMillis}
        super.notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v = super.getView(position, convertView, parent) as CourseFileView
        val item = material[position]
        /*val extracteddatetime = material[position].name.substring(model.name.length - 24, model.name.length);
       mFileView.text = model.name.substring(0,model.name.length-24)
       postDatehome.text = extracteddatetime*/

        if (fileManager.hasInCache(item.name)) {
            v.setStatus(CourseFileView.FileStatus.Local)
        } else {
            v.setStatus(CourseFileView.FileStatus.Cloud)
        }

        v.setOnClickListener {
            v.setStatus(CourseFileView.FileStatus.Downloading)
            fileManager.download(
                    item.name,
                    OnSuccessListener { file ->
                        v.setStatus(CourseFileView.FileStatus.Local)
                        try {
                            openFile(file)
                            //file.openInExternalApp(context)
                        } catch (ex: IOException) {
                            Snackbar.make(v, ex.message ?: "Could not open file.", Snackbar.LENGTH_SHORT).show()
                        }
                    },
                    OnFailureListener {
                        v.setStatus(CourseFileView.FileStatus.Cloud)
                        Snackbar.make(v, it.message ?: "Could not download file.", Snackbar.LENGTH_SHORT).show()
                    }
            )
        }

        v.setOnLongClickListener {
            // TODO: Delete file in editable mode
            false
        }
        return v
    }
    private fun openFile(url: File) {
        try {
           // val uri: Uri = Uri.fromFile(url)
            val uri = FileProvider.getUriForFile(context,
                    "com.cygnus.fileprovider", url)
            val intent = Intent(Intent.ACTION_VIEW)

            if (url.toString().contains(".pdf")) { // PDF file
                intent.setDataAndType(uri, "application/pdf")
            } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg")
                    || url.toString().contains(".png")) { // JPG file
                intent.setDataAndType(uri, "image/*")
            } else {
                intent.setDataAndType(uri, "*/*")
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No application found which can open the file", Toast.LENGTH_SHORT).show()
        }
    }
}