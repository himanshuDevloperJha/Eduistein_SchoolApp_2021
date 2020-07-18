package com.cygnus.storage

import com.cygnus.model.Credentials
import com.google.firebase.auth.FirebaseAuth
import com.orhanobut.hawk.Hawk

object AuthManager {

    fun signOut() {
        FirebaseAuth.getInstance().currentUser?.let { remove(it.uid) }
        FirebaseAuth.getInstance().signOut()
    }

    fun add(uid: String, credentials: Credentials) {
        val signedInAccounts = listAll()
        signedInAccounts[uid] = credentials
        Hawk.put("accounts", signedInAccounts)
    }

    fun listAll(): HashMap<String, Credentials> {
        var signedInAccounts = Hawk.get<HashMap<String, Credentials>>("accounts")
        if (signedInAccounts == null) {
            signedInAccounts = HashMap()
        }

        return signedInAccounts
    }

    private fun remove(uid: String) {
        val signedInAccounts = listAll()
        signedInAccounts.remove(uid)
        Hawk.put("accounts", signedInAccounts)
    }

}