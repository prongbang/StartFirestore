package com.prongbang.startfirestore.utils

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction

/**
 * Created by prongbang on 10/9/2017 AD.
 */
object FragmentUtil {

    fun addFragmentToActivity(manager: FragmentManager, fragment: Fragment, frameId: Int) {

        val transaction = manager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        // transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_out_right, R.anim.slide_in_right)
        transaction.add(frameId, fragment)
        transaction.addToBackStack(fragment::class.java.simpleName)
        transaction.commit()

    }

    fun popBackStack(manager: FragmentManager) {
        if (manager.backStackEntryCount > 1) {
            val done = manager.popBackStack()
        }
    }
}