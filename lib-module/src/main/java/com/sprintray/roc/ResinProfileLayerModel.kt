package com.sprintray.roc

import android.os.Parcel
import android.os.Parcelable

data class ResinProfileLayerModel(
    var washTime: Int = 180,
    var rinseTime: Int = 180,
    var dryTime: Int = 180,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(washTime)
        parcel.writeInt(rinseTime)
        parcel.writeInt(dryTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ResinProfileLayerModel> {
        override fun createFromParcel(parcel: Parcel): ResinProfileLayerModel {
            return ResinProfileLayerModel(parcel)
        }

        override fun newArray(size: Int): Array<ResinProfileLayerModel?> {
            return arrayOfNulls(size)
        }
    }
}