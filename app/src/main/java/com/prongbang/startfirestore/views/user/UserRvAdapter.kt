package com.prongbang.startfirestore.views.user

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import com.prongbang.startfirestore.R
import com.prongbang.startfirestore.model.User
import com.prongbang.startfirestore.utils.ConstantUtil
import com.prongbang.startfirestore.views.firestore.FirestoreAdapter
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.prongbang.startfirestore.views.firestore.OnFirestoreListener


/**
 * Created by prongbang on 10/9/2017 AD.
 * https://github.com/firebase/quickstart-android/blob/master/firestore/app/src/main/java/com/google/firebase/example/fireeats/adapter/RatingAdapter.java
 */
class UserRvAdapter(context: Context, query: Query) : FirestoreAdapter<UserRvAdapter.UserVHolder>(query) {

    private val TAG = UserRvAdapter::class.java.simpleName

    private var users: List<User> = listOf()
    private val context = context
    private var onActionClickListener: OnClickActionListener? = null

    override fun onBindViewHolder(holder: UserVHolder?, position: Int) {

        holder?.bind(context, getSnapshot(position), onActionClickListener)
    }

    fun getItem(position: Int): User {
        val snapshot = getSnapshot(position)
        val user = snapshot.toObject<User>(User::class.java)
        user.id = snapshot.id.toInt()
        return user
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): UserVHolder {

        val v = LayoutInflater.from(parent!!.context).inflate(R.layout.item_user, parent, false)

        return UserVHolder(v)
    }

    fun setOnClickActionListener(listener: OnClickActionListener) {
        onActionClickListener = listener
    }

    class UserVHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        private val TAG = UserVHolder::class.java.simpleName

        val item = itemView
        val ivMore = itemView?.findViewById<ImageView>(R.id.ivMore)
        val tvFullName = itemView?.findViewById<TextView>(R.id.tvFullName)
        val tvEmail = itemView?.findViewById<TextView>(R.id.tvEmail)

        @SuppressLint("SetTextI18n")
        fun bind(context: Context, snapshot: DocumentSnapshot, listener: OnClickActionListener?) {

            Log.i(TAG, snapshot.data.toString())

            var user = snapshot.toObject<User>(User::class.java)
            user.id = snapshot.id.toInt()

            tvFullName?.text = user.first_name + " " + user.last_name
            tvEmail?.text = user.email

            // Click listener
            ivMore!!.setOnClickListener { v ->
                val pm = PopupMenu(context, v)
                pm.menuInflater.inflate(R.menu.poupup_menu, pm.menu)
                pm.setOnMenuItemClickListener { item ->
                    Log.i(TAG, "Click --> ${item.title}")
                    when (item.itemId) {
                        R.id.edit -> listener?.onAction(ConstantUtil.UPDATE, user)
                        R.id.delete -> listener?.onAction(ConstantUtil.DELETE, user)
                        else -> Log.i(TAG, "Not found!")
                    }
                    return@setOnMenuItemClickListener true
                }
                pm.show()
            }

            item?.setOnClickListener { v ->
                Log.i(TAG, "Click --> ${user.id}")
            }

        }

    }

    interface OnClickActionListener {
        fun onAction(action: String, user: User)
    }

}