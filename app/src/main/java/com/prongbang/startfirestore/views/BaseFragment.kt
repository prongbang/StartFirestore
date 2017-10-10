package com.prongbang.startfirestore.views

import android.app.AlertDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.prongbang.startfirestore.R

/**
 * Created by prongbang on 10/9/2017 AD.
 */

open class BaseFragment : Fragment() {

    var db: FirebaseFirestore? = null

    override fun onPause() {
        super.onPause()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true)

        db = FirebaseFirestore.getInstance()

        // Configure offline persistence
        db?.firestoreSettings = FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build()
    }

    fun alert(title: String, message: String) {

        AlertDialog.Builder(context).setTitle(title).setMessage(message).setCancelable(true)
                .setPositiveButton(getString(R.string.ok), { dialog, with -> {} }).create().show()
    }

}