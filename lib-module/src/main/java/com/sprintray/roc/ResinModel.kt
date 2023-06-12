package com.sprintray.roc

import android.os.Parcel
import android.os.Parcelable

data class ResinModel(
    var name: String,
    var resinId: String,
    var family: String,
    var profiles: ResinProfileLayerModel
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readParcelable<ResinProfileLayerModel>(ResinProfileLayerModel::class.java.classLoader)!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(resinId)
        parcel.writeString(family)
        parcel.writeParcelable(profiles, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ResinModel> {
        override fun createFromParcel(parcel: Parcel): ResinModel {
            return ResinModel(parcel)
        }

        override fun newArray(size: Int): Array<ResinModel?> {
            return arrayOfNulls(size)
        }
    }
}