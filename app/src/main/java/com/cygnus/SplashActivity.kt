package com.cygnus

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cygnus.sign_up.SignUpActivity
import com.cygnus.utils.DynamicLinksUtils
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase

/**
 * SplashActivity is first called when the app starts.
 *
 * Purpose of this activity is to display the splash screen of
 * the Cygnus app and to prepare the app for launch.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    override fun onStart() {
        super.onStart()
        checkForDynamicLinks()
    }

    /**
     * Schedules redirection to start page after a short delay.
     */
    private fun startAppDelayed() {
        Handler().postDelayed({ startApp() }, 1500L)
    }

    /**
     * Redirect to the first screen in the app.
     */
    private fun startApp() {
        startActivity(Intent(applicationContext, SignInActivity::class.java))
        finish()
    }

    /**
     * Checks for incoming [FirebaseDynamicLinks] to allow deep linking.
     *
     * Appropriate action is taken if a parseable deep link can be found,
     * else app redirects to first screen.
     */
    private fun checkForDynamicLinks() {
        Firebase.dynamicLinks.getDynamicLink(intent)
                .addOnCompleteListener(this) {
                    // Get deep link from result (may be null if no link is found)
                    if (it.isSuccessful && it.result?.link != null) {
                        try {
                            parseDeepLink(it.result?.link!!)
                        } catch (ex: Exception) {
                            Toast.makeText(this, ex.message ?: "Malformed link", Toast.LENGTH_SHORT).show()
                        }
                    } else startAppDelayed()
                }
    }

    /**
     * Parses the [deepLink] to ascertain and take appropriate action.
     *
     * @throws Exception An exception is raised if the [deepLink] is not parseable.
     */
    private fun parseDeepLink(deepLink: Uri) {
        val target = Uri.parse(deepLink.getQueryParameter(CygnusApp.PARAM_LINK_TARGET))
        when (target.path) {
            DynamicLinksUtils.ACTION_REGISTRATION -> {
                val i = Intent(this, SignUpActivity::class.java)
                i.putExtra(CygnusApp.EXTRA_REFERRAL_CODE, target.getQueryParameter(CygnusApp.PARAM_REFERRAL_CODE))
                i.putExtra(CygnusApp.EXTRA_ACCOUNT_TYPE, target.getQueryParameter(CygnusApp.PARAM_ACCOUNT_TYPE))
                i.putExtra(CygnusApp.EXTRA_STUDENT_CLASS_ID, target.getQueryParameter(CygnusApp.PARAM_STUDENT_CLASS_ID))
                i.putExtra(CygnusApp.EXTRA_STUDENT_ROLL_NO, target.getQueryParameter(CygnusApp.PARAM_STUDENT_ROLL_NO))
                i.data = deepLink

                startActivity(i)
                finish()
            }
        }
    }

}