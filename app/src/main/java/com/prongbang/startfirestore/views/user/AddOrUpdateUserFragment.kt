package com.prongbang.startfirestore.views.user


import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.prongbang.startfirestore.R
import com.prongbang.startfirestore.utils.ConstantUtil
import com.prongbang.startfirestore.utils.FragmentUtil
import com.prongbang.startfirestore.views.BaseFragment
import android.util.Log
import android.widget.*
import com.google.gson.Gson
import com.prongbang.startfirestore.model.User
import com.prongbang.startfirestore.utils.KeyboardUtil


/**
 * A simple [Fragment] subclass.
 */
class AddOrUpdateUserFragment : BaseFragment() {

    private var TAG = AddOrUpdateUserFragment::class.java.simpleName

    private var etId: EditText? = null
    private var etFirstName: EditText? = null
    private var etLastName: EditText? = null
    private var etEmail: EditText? = null
    private var btnAdd: Button? = null
    private var btnEdit: Button? = null
    private var lastId: Int = 0
    private var actionType: String = ConstantUtil.ADD
    private var user: User? = null
    private var tvTitle: TextView? = null
    private var loading: View? = null
    private var tvStatus: TextView? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_add_or_update_user, container, false)

        if (arguments != null) {
            lastId = arguments.getInt(ConstantUtil.USER_LAST_ID) + 1
            actionType = arguments.getString(ConstantUtil.ACTION_TYPE)
            user = arguments.getParcelable(ConstantUtil.USER)
        }

        etId = view.findViewById(R.id.etId)
        tvTitle = view.findViewById(R.id.tvTitle)
        etFirstName = view.findViewById(R.id.etFirstName)
        etLastName = view.findViewById(R.id.etLastName)
        etEmail = view.findViewById(R.id.etEmail)
        btnEdit = view.findViewById(R.id.btnEdit)
        btnAdd = view.findViewById(R.id.btnAdd)
        loading = view.findViewById(R.id.loading)
        tvStatus = view.findViewById(R.id.tvStatus)

        val ivBack = view.findViewById<ImageView>(R.id.ivBack)
        ivBack.setOnClickListener { v ->
            KeyboardUtil.hideKeyboard(activity)
            FragmentUtil.popBackStack(activity.supportFragmentManager)
        }

        if (actionType == ConstantUtil.ADD) {
            tvTitle?.text = getString(R.string.add_user)
            btnEdit?.visibility = View.GONE
            btnAdd?.visibility = View.VISIBLE
            etId?.text = Editable.Factory.getInstance().newEditable((lastId).toString())
        } else if (actionType == ConstantUtil.UPDATE) {
            tvTitle?.text = getString(R.string.update_user)
            btnEdit?.visibility = View.VISIBLE
            btnAdd?.visibility = View.GONE
            etId?.text = Editable.Factory.getInstance().newEditable((user?.id).toString())
            etFirstName?.text = Editable.Factory.getInstance().newEditable(user?.first_name)
            etLastName?.text = Editable.Factory.getInstance().newEditable(user?.last_name)
            etEmail?.text = Editable.Factory.getInstance().newEditable(user?.email)
        }

        btnAdd?.setOnClickListener { v -> addUser() }
        btnEdit?.setOnClickListener { v -> editUser() }

        return view
    }

    private fun editUser() {

        KeyboardUtil.hideKeyboard(activity)

        if (validate()) {

            setLoading(View.VISIBLE, R.string.editing)

            val user = HashMap<String, Any>()
            user.put(ConstantUtil.LAST_NAME, etLastName?.text.toString())
            user.put(ConstantUtil.FIRST_NAME, etFirstName?.text.toString())
            user.put(ConstantUtil.EMAIL, etEmail?.text.toString())

            val docRef = db!!.collection(ConstantUtil.COLLECTION_USERS).document(etId?.text.toString())
            docRef.update(user)
                    .addOnCompleteListener {
                        setLoading(View.GONE, R.string.editing)
                        FragmentUtil.popBackStack(activity.supportFragmentManager)
                        Toast.makeText(activity.applicationContext, "Update Successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error update document", e)
                        setLoading(View.GONE, R.string.editing)
                    }
        }
    }

    private fun addUser() {

        KeyboardUtil.hideKeyboard(activity)

        if (validate()) {

            setLoading(View.VISIBLE, R.string.saving)

            // #1
            val data = HashMap<String, Any>()
            data.put(ConstantUtil.FIRST_NAME, etFirstName?.text.toString())
            data.put(ConstantUtil.LAST_NAME, etLastName?.text.toString())
            data.put(ConstantUtil.EMAIL, etEmail?.text.toString())

            Log.i(TAG, "${lastId.toString()} -> " + Gson().toJson(data))

            // #2
            val user = User(
                    id = lastId,
                    first_name = etFirstName?.text.toString(),
                    last_name = etLastName?.text.toString(),
                    email = etEmail?.text.toString()
            )

            db!!.collection(ConstantUtil.COLLECTION_USERS)
                    .document(lastId.toString())
                    // #1
                    // .set(data)
                    // #2
                    .set(user)
                    .addOnSuccessListener {
                        lastId += 1
                        reset(lastId)
                        Toast.makeText(activity.applicationContext, "Add User Successfully", Toast.LENGTH_SHORT).show()
                        Log.i(TAG, "DocumentSnapshot successfully written!")
                        setLoading(View.GONE, R.string.saving)
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error writing document", e)
                        setLoading(View.GONE, R.string.saving)
                    }
        }

    }

    private fun setLoading(visible: Int, msg: Int) {
        loading?.visibility = visible
        tvStatus?.text = getString(msg)
    }

    private fun reset(id: Int) {
        etId?.text = Editable.Factory.getInstance().newEditable((id).toString())
        etFirstName?.text = Editable.Factory.getInstance().newEditable("")
        etLastName?.text = Editable.Factory.getInstance().newEditable("")
        etEmail?.text = Editable.Factory.getInstance().newEditable("")
    }

    private fun validate(): Boolean {

        var result = true

        if (etId!!.text.isEmpty()) {
            etId?.error = "ID is Empty!"
            result = false
            etId?.requestFocus()
        } else if (etFirstName!!.text.isEmpty()) {
            etFirstName?.error = "First Name is Empty!"
            result = false
            etFirstName!!.requestFocus()
        } else if (etLastName!!.text.isEmpty()) {
            etLastName?.error = "Last Name is Empty!"
            result = false
            etLastName?.requestFocus()
        } else if (etEmail!!.text.isEmpty()) {
            etEmail?.error = "Email is Empty!"
            result = false
            etEmail?.requestFocus()
        }

        return result
    }

}
// Required empty public constructor
