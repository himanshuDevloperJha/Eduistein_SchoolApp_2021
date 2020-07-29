package com.cygnus

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import co.aspirasoft.util.InputUtils.isNotBlank
import com.cygnus.dao.SchoolDao
import com.cygnus.dao.UsersDao
import com.cygnus.model.Credentials
import com.cygnus.model.School
import com.cygnus.model.Teacher
import com.cygnus.model.User
import com.cygnus.storage.AuthManager
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_sign_in.*

/**
 * SignInActivity is first displayed to all new users.
 *
 * Purpose of this activity is to let users log into the app
 * using their credentials.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class SignInActivity : AppCompatActivity() {

    private var isNewSignIn: Boolean = false
    lateinit var progressDialog:ProgressDialog
    private val auth = FirebaseAuth.getInstance()
    private var credentials: Credentials? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // Are we adding a new account?
        isNewSignIn = intent.getBooleanExtra(CygnusApp.EXTRA_NEW_SIGN_IN, false)
        credentials = intent.getSerializableExtra(CygnusApp.EXTRA_EXISTING_SIGN_IN) as Credentials?

        // Show a welcome message if this is a new user
        if (intent.getBooleanExtra(CygnusApp.EXTRA_NEW_SIGN_UP, false)) {
            isNewSignIn = true
            Snackbar.make(
                    signInButton,
                    "Congratulations! Use your email/password to sign into your new account now.",
                    Snackbar.LENGTH_LONG
            ).show()
        }

        signInButton.setOnClickListener { signIn() }
    }

    /**
     * Overrides the activity lifecycle [AppCompatActivity.onStart] method
     * to check if an existing user signed in at activity startup.
     */
    override fun onStart() {
        super.onStart()
        auth.signOut()
        if (credentials != null) {
            signInWithCredentials(credentials!!)
        } else if (!isNewSignIn) {
            val signedInUsers = AuthManager.listAll()
            if (signedInUsers.isEmpty()) {
                isNewSignIn = true
            } else {
                val uid = signedInUsers.keys.elementAt(0)
                signedInUsers[uid]?.let { signInWithCredentials(it) }
            }
        }
    }

    /**
     * Starts the sign-in sequence using credentials provided by the user.
     *
     * Authentication is asynchronous, and happens only if all required fields
     * are provided. A blocking UI is displayed while the request is being
     * processed, disallowing all user actions.
     */
    private fun signIn() {
        if (emailField.isNotBlank(true) && passwordField.isNotBlank(true)) {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()
            credentials = Credentials(email, password)
            signInWithCredentials(credentials!!)
        }
    }

    private fun signInWithCredentials(credentials: Credentials) {
         progressDialog = ProgressDialog.show(
                this,
                "Please Wait",
                "Signing you in...",
                true
        )
        auth.signInWithEmailAndPassword(credentials.email, credentials.password)
                .addOnCompleteListener {

                    if (it.isSuccessful) {
                        it.result?.user?.let {
                            firebaseUser -> onAuthSuccess(firebaseUser)
                        }
                    } else {
                        it.exception?.let { ex -> onFailure(ex) }
                    }
                }
    }

    /**
     * Callback for when Firebase authentication succeeds.
     *
     * After user has been successfully authenticated, we need to fetch their
     * details from Firebase database to complete the sign-in process.
     */
    private fun onAuthSuccess(firebaseUser: FirebaseUser) {
        SchoolDao.getSchoolName(firebaseUser.uid, OnSuccessListener { school ->
            // when a `School` signs in
            if (school != null) onSignedInAsSchool(school, firebaseUser)

            // when a regular user signs in, we first need to find out which school
            // they are associated with by retrieving their school's id
            else SchoolDao.getSchoolByUser(firebaseUser.uid, OnSuccessListener {
                it?.let { schoolDetails ->
                    onSchoolDetailsReceived(schoolDetails, firebaseUser)
                } ?: onFailure(null)
            })
        })
    }

    /**
     * Callback for when details of [firebaseUser]'s school are received.
     */
    private fun onSchoolDetailsReceived(schoolDetails: Pair<String, String>, firebaseUser: FirebaseUser) {
        UsersDao.getUserById(schoolDetails.first, firebaseUser.uid, OnSuccessListener {
            it?.let { user -> onSignedIn(user, schoolDetails) }
        })
    }

    /**
     * Callback for when the sign-in completes.
     *
     * User is automatically redirected to the appropriate screen in the app.
     */
    private fun onSignedIn(user: User, schoolDetails: Pair<String, String>) {
        credentials?.let { AuthManager.add(user.id, it) }
        startActivity(Intent(
                applicationContext,
                when (user) {
                    is School -> SchoolDashboardActivity::class.java
                    is Teacher -> TeacherDashboardActivity::class.java
                    else -> StudentDashboardActivity::class.java
                }
        ).apply {
            putExtra(CygnusApp.EXTRA_USER, user)
            putExtra(CygnusApp.EXTRA_SCHOOL, schoolDetails)
        })
        progressDialog.cancel()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    /**
     * Callback for a `School` signs in.
     *
     * @param name name of the school
     * @param account Firebase account of the school
     */
    private fun onSignedInAsSchool(name: String, account: FirebaseUser) {
        onSignedIn(School(
                account.uid,
                name,
                Credentials(account.email!!, "")
        ), Pair(account.uid, name))
    }

    /**
     * Callback for when an error occurs during the sign-in process.
     *
     * This method is called if authentication fails or user's details
     * could not be fetched from the database.
     */
    private fun onFailure(ex: Exception?) {
        Toast.makeText(this, ex?.message ?: "Sign in failed", Toast.LENGTH_LONG).show()
        auth.signOut()
    }

}