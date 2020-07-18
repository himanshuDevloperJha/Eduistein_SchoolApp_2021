package com.cygnus.view

import android.app.Activity
import android.content.Intent
import com.cygnus.SignInActivity
import com.cygnus.storage.AuthManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object LogoutConfirmationDialog {

    fun show(activity: Activity) {
        MaterialAlertDialogBuilder(activity)
                .setTitle("Sign Out")
                .setMessage("You will be logged out. Continue?")
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    AuthManager.signOut()
                    activity.startActivity(Intent(activity, SignInActivity::class.java))
                    activity.finish()
                }
                .setNegativeButton(android.R.string.no) { dialog, _ ->
                    dialog.cancel()
                }
                .show()
    }

}