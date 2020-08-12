package sun.trainingcourse.musicapp.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Music(
    val song: String?,
    val author: String?,
    val path: String?
) : Parcelable
