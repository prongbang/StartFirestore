package com.prongbang.startfirestore.views.firestore

import com.google.firebase.firestore.FirebaseFirestoreException

/**
 * Created by prongbang on 10/10/2017 AD.
 */
interface OnFirestoreListener {
    fun onError(e: FirebaseFirestoreException)
    fun onDataChanged()
}