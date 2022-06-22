package com.example.android.politicalpreparedness.network.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "voter_info_table")
data class VoterInfo (
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "votingLocationFinderUrl") @Json(name="")
    val votingLocationFinderUrl: String,
    @ColumnInfo(name = "ballotInfoUrl")
    val ballotInfoUrl: String
)