package co.aspirasoft.util

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object PermissionUtils {

    /**
     * Asks user to grant a [permission].
     *
     * Result of this request with [requestCode] is received in [onRequestPermissionsResult].
     */
    fun requestPermission(activity: Activity, permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
    }

    /**
     * Explains and asks user to grant a [permission].
     *
     * An [explanation] message is shown if required. Result of this request
     * with [requestCode] is received in [onRequestPermissionsResult].
     */
    fun requestPermissionNicely(activity: Activity, permission: String, explanation: String, requestCode: Int) {
        // Permission is not granted, should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            MaterialAlertDialogBuilder(activity)
                    .setTitle("Grant Permission")
                    .setMessage(explanation)
                    .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
                    .setOnDismissListener { requestPermission(activity, permission, requestCode) }
                    .show()
        }

        // No explanation needed, we can request the permission.
        else {
            requestPermission(activity, permission, requestCode)
        }
    }

    /**
     * Asks user to grant a [permission] if not already granted.
     *
     * An [explanation] message is shown if required. Result of this request
     * with [requestCode] is received in [Activity.onRequestPermissionsResult].
     *
     * Returns `true` if permission was already granted.
     */
    fun requestPermissionIfNeeded(activity: Activity, permission: String, explanation: String, requestCode: Int): Boolean {
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionNicely(activity, permission, explanation, requestCode)
            return false
        }

        return true
    }

}