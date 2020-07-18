package com.cygnus.storage

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.cygnus.R
import com.cygnus.model.User
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener

object ImageLoader {

    fun with(context: Context): ImageLoadTask {
        return ImageLoadTask(context)
    }

    class ImageLoadTask(val context: Context) {
        fun load(user: User): UserImageTask {
            return UserImageTask(context, user)
        }
    }

    class UserImageTask(val context: Context, val user: User) {
        private var skip: Boolean = false
        private val filename = "photo.png"

        fun skipCache(skip: Boolean): UserImageTask {
            this.skip = skip
            return this
        }

        fun into(target: ImageView) {
            FileManager.newInstance(context, "users/${user.id}/").download(
                    filename,
                    OnSuccessListener {
                        try {
                            Glide.with(context)
                                    .load(it)
                                    .skipMemoryCache(true)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .into(target)
                        } catch (ignored: Exception) {

                        }
                    },
                    OnFailureListener {
                        try {
                            Glide.with(context).load(R.drawable.ph_student).into(target)
                        } catch (ignored: Exception) {

                        }
                    },
                    skip
            )

        }
    }

}