package com.cygnus.core

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cygnus.CygnusApp
import com.cygnus.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/**
 * SecureActivity is an abstract activity which restricts usage to signed in users.
 *
 * Extend this class to make an activity secure. A `secured` activity will only be
 * allowed to open if a [FirebaseUser] is authenticated and the [User] instance of
 * signed in user is passed to the activity intent with [CygnusApp.EXTRA_USER] tag
 * and the unique [schoolId] to which this user is associated is passed with the
 * [CygnusApp.EXTRA_SCHOOL] tag.
 *
 * @author https://github.com/Kushaggra
 * @since 1.0.0
 */
abstract class SecureActivity : AppCompatActivity() {

    private lateinit var schoolDetails: Pair<String, String>
    protected val schoolId: String get() = schoolDetails.first
    protected val school: String get() = schoolDetails.second
     lateinit var currentUser: User

    /**
     * Overrides the onCreate activity lifecycle method.
     *
     * All authentication checks are performed here, and activity is terminated
     * if the checks fail.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val signedInUser = FirebaseAuth.getInstance().currentUser       // firebase auth -> (1)
        val user = intent.getSerializableExtra(CygnusApp.EXTRA_USER) as User? // account details -> (2)
        currentUser = when {
            signedInUser == null || user == null -> return finish() // both (1) and (2) must exist
            user.id == signedInUser.uid -> user                     // and both must belong to same user
            else -> return finish()                                 // else finish activity
        }

        schoolDetails = intent.getSerializableExtra(CygnusApp.EXTRA_SCHOOL) as Pair<String, String>? ?: return finish()
    }

    /**
     * Overrides the onStart activity lifecycle method.
     *
     * This is only called if all authentication checks passed. We can safely
     * use our [currentUser] object here.
     */
    override fun onStart() {
        super.onStart()
        updateUI(currentUser)
    }

    /**
     * Overrides the back navigation button in action bar.
     *
     * Hooks the action bar back button to default back action.
     */
    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    /**
     * Ensures required arguments are forwarded when opening another secure activity.
     */
    fun startSecurely(target: Class<out SecureActivity>, src: Intent? = null) {
        startActivity(Intent(this, target).apply {
            this.putExtra(CygnusApp.EXTRA_USER, currentUser)
            this.putExtra(CygnusApp.EXTRA_SCHOOL, schoolDetails)
            src?.let { this.putExtras(it) }
        })
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    /**
     * Implement this method to update UI for signed in user, if needed.
     */
    abstract fun updateUI(currentUser: User)

}