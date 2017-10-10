package com.prongbang.startfirestore.views

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings



/**
 * Created by prongbang on 10/9/2017 AD.
 */
open class BaseActivity: AppCompatActivity() {

    var db: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        db = FirebaseFirestore.getInstance()

        // Configure offline persistence
        db?.firestoreSettings = FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build()
    }

}