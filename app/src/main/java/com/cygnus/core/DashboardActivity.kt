package com.cygnus.core

import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.cygnus.R
import com.cygnus.view.AccountSwitcher

abstract class DashboardActivity : SecureActivity() {

    private var doubleBackToExitPressedOnce = false

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_action_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_open_account -> {
                AccountSwitcher.Builder(this)
                        .setUser(currentUser)
                        .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Press back button twice to exit.", Toast.LENGTH_SHORT).show()
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

}