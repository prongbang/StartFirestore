package com.prongbang.startfirestore.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties

/**
 * Created by prongbang on 10/9/2017 AD.
 */

@IgnoreExtraProperties
data class User(
        var id: Int? = null,
        val first_name: String? = null,
        val last_name: String? = null,
        val email: String? = null
) : Parcelable {

    constructor(source: Parcel) : this(
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readString(),
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeValue(id)
        writeString(first_name)
        writeString(last_name)
        writeString(email)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<User> = object : Parcelable.Creator<User> {
            override fun createFromParcel(source: Parcel): User = User(source)
            override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
        }
    }
}