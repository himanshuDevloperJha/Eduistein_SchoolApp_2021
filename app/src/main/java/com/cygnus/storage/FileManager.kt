package com.cygnus.storage

import android.content.Context
import android.net.Uri
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import java.io.File

/**
 * FileManager class is used to access files from FirebaseStorage.
 *
 * This class provides methods to access files from cloud storage. Accessed
 * files are cached locally to improve latency on subsequent requests.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class FileManager private constructor(context: Context, relativePath: String) {

    private val storage = Firebase.storage.getReference(relativePath)
    private val cache = context.getExternalFilesDir(null)?.let { File(it, relativePath) }

    fun download(
            filename: String,
            successListener: OnSuccessListener<File>,
            failureListener: OnFailureListener,
            invalidate: Boolean = false
    ) {
        when (val cachedFile = downloadFromCache(filename)) {
            null -> {
                val tempFile = createTempFile(filename)
                downloadFromStorage(filename, tempFile)
                        .addOnSuccessListener { successListener.onSuccess(tempFile) }
                        .addOnFailureListener {
                            tempFile.delete()
                            failureListener.onFailure(it)
                        }
            }
            else -> {
                // Invalidate cache if flag set or more than a week since last update
                if (cachedFile.requiresInvalidation() || invalidate) {
                    downloadFromStorage(filename, cachedFile)
                            .addOnSuccessListener { successListener.onSuccess(cachedFile) }
                            .addOnFailureListener {
                                cachedFile.delete()
                                failureListener.onFailure(it)
                            }
                }
                successListener.onSuccess(cachedFile)
            }
        }
    }

    fun upload(filename: String, uri: Uri): UploadTask {
        return storage.child(filename).putFile(uri)
    }

    fun upload(filename: String, bytes: ByteArray): UploadTask {
        return storage.child(filename).putBytes(bytes)
    }

    fun hasInCache(filename: String): Boolean {
        return downloadFromCache(filename) != null
    }

    fun listAll(): Task<ListResult> {
        return storage.listAll()
    }

    private fun createTempFile(filename: String): File {
        val file = File(cache, filename)
        if (!file.exists()) {
            file.parent?.let { File(it).mkdirs() }
            file.createNewFile()
        }

        return file
    }

    private fun downloadFromCache(filename: String): File? {
        val file = File(cache, filename)
        return if (file.exists()) file else null
    }

    private fun downloadFromStorage(filename: String, file: File): FileDownloadTask {
        return storage.child(filename).getFile(file)
    }
    private fun downloadFromUrl(filename: String, file: File): File {
        storage.child(filename).downloadUrl.addOnSuccessListener {
            // Got the download URL for 'users/me/profile.png'
        }.addOnFailureListener {
            // Handle any errors
        }

        return file
    }

    private fun File.requiresInvalidation(): Boolean {
        val modified = this.lastModified()
        val now = System.currentTimeMillis()

        val days = (now - modified) / (1000 * 60 * 60 * 24L)
        return days > 7
    }

    companion object {
        fun newInstance(context: Context, relativePath: String): FileManager {
            return FileManager(context, relativePath)
        }
    }

}