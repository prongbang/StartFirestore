package com.prongbang.startfirestore.views.user

import android.app.AlertDialog
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.prongbang.startfirestore.utils.ConstantUtil
import android.util.Log
import com.google.gson.Gson
import com.prongbang.startfirestore.model.User
import com.prongbang.startfirestore.views.BaseFragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestoreException
import com.prongbang.startfirestore.R
import com.prongbang.startfirestore.utils.FragmentUtil
import com.prongbang.startfirestore.views.firestore.OnFirestoreListener


/**
 * A placeholder fragment containing a simple view.
 */
class MainFragment : BaseFragment(), UserRvAdapter.OnClickActionListener, OnFirestoreListener {

    private val TAG = MainFragment::class.java.simpleName

    private var adapter: UserRvAdapter? = null
    private var progressBar: ProgressBar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_main, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        val fab = view.findViewById<View>(R.id.fab)

        val mQuery = db!!.collection(ConstantUtil.COLLECTION_USERS).orderBy("id")

        adapter = UserRvAdapter(activity.applicationContext, mQuery)
        adapter?.setOnClickActionListener(this)
        adapter?.setOnFirestoreListener(this)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity.applicationContext)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = adapter

        fab.setOnClickListener { view ->
            val addFragment = AddOrUpdateUserFragment()
            val bundle = Bundle()
            val id = adapter?.getItem(adapter!!.itemCount - 1)?.id // get last id
            bundle.putInt(ConstantUtil.USER_LAST_ID, id ?: 0)
            bundle.putString(ConstantUtil.ACTION_TYPE, ConstantUtil.ADD)
            addFragment.arguments = bundle
            FragmentUtil.addFragmentToActivity(activity.supportFragmentManager, addFragment, R.id.container)
        }

        return view
    }

    override fun onStart() {
        super.onStart()

        // Start listening for Firestore updates
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()

        // Stop listening for Firestore updates
        adapter?.stopListening()
    }

    override fun onAction(action: String, user: User) {
        Log.i(TAG, "$action <- " + Gson().toJson(user))
        val id = user.id
        when (action) {
            ConstantUtil.UPDATE -> goToAddOrUpdateUser(id, user)
            ConstantUtil.DELETE -> deleteUser(id, user)
        }
    }

    private fun goToAddOrUpdateUser(id: Int?, user: User) {
        val addFragment = AddOrUpdateUserFragment()
        val bundle = Bundle()
        bundle.putInt(ConstantUtil.USER_LAST_ID, id ?: 0) // id == null ? 0 : id
        bundle.putString(ConstantUtil.ACTION_TYPE, ConstantUtil.UPDATE)
        bundle.putParcelable(ConstantUtil.USER, user)
        addFragment.arguments = bundle
        FragmentUtil.addFragmentToActivity(activity.supportFragmentManager, addFragment, R.id.container)
    }

    private fun deleteUser(id: Int?, user: User) {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(getString(R.string.confirm))
        dialog.setMessage(getString(R.string.delete_user, user.first_name + " " + user.last_name))
        dialog.setPositiveButton(getString(R.string.ok), { dialog, with ->
            dialog.dismiss()
            val docRef = db!!.collection(ConstantUtil.COLLECTION_USERS).document(id.toString())
            docRef.delete()
            .addOnCompleteListener { Toast.makeText(activity.applicationContext, "Delete Successfully", Toast.LENGTH_SHORT).show() }
            .addOnFailureListener { e -> Log.w(TAG, "Error delete document", e) }
        })
        dialog.setNegativeButton(getString(R.string.cancel), {dialog, which -> Log.i(TAG, "Cancel delete user $id") })
        dialog.create()
        dialog.show()
    }

    override fun onError(e: FirebaseFirestoreException) {
        Log.e(TAG, "FirebaseFirestoreException: ", e)
    }

    override fun onDataChanged() {
        progressBar?.visibility = View.GONE
    }
}

/**
 * Example:
// #1
//        val userCallRef = db!!.collection(ConstantUtil.COLLECTION_USERS)
//        userCallRef.addSnapshotListener { documentSnapshots: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
//            // Dispatch the event
//            Log.d(TAG, "onEvent:numChanges:" + documentSnapshots?.documentChanges?.size)
//            for (change in documentSnapshots!!.documentChanges) {
////                when (change.type) {
////                    DocumentChange.Type.ADDED -> onDocumentAdded(change)
////                    DocumentChange.Type.MODIFIED -> onDocumentModified(change)
////                    DocumentChange.Type.REMOVED -> onDocumentRemoved(change)
////                }
//                Log.i(TAG, change.toString())
//            }
//        }

// #2
//        userCallRef.get().addOnCompleteListener { task ->
//            val users: ArrayList<User> = arrayListOf()
//            if (task.isSuccessful) {
//                for (document in task.result) {
//                    val user = document.toObject(User::class.java)
//                    user.id = document.id.toInt()
//                    Log.d(TAG, Gson().toJson(user))
//                    users.add(user)
//                }
//                adapter.setData(users)
//                progressBar.visibility = View.GONE
//            } else {
//                Log.d(TAG, "Error getting documents: ", task.exception)
//            }
//        }
 */