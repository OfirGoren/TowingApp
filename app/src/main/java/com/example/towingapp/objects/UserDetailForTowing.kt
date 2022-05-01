package com.example.towingapp.objects

import android.os.Parcel
import android.os.Parcelable


data class UserDetailForTowing(
    var name: String? = "",
    var imageUri:String? = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var Userid:String? = null,
    var requestId:String? = null
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(imageUri)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
        parcel.writeString(Userid)
        parcel.writeString(requestId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserDetailForTowing> {
        override fun createFromParcel(parcel: Parcel): UserDetailForTowing {
            return UserDetailForTowing(parcel)
        }

        override fun newArray(size: Int): Array<UserDetailForTowing?> {
            return arrayOfNulls(size)
        }
    }
}