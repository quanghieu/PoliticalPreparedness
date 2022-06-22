package com.example.android.politicalpreparedness.representative.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*

@Entity(tableName = "representative_table", primaryKeys = ["address", "name"])
@Parcelize
data class RepresentativeLocalDB(
    val order: Int,
    val address: String,
    val role: String,
    val name: String,
    val party: String,
    val url: String?,
    val facebook: String?,
    val twitter: String?,
    val imageUrl: String?
) : Parcelable