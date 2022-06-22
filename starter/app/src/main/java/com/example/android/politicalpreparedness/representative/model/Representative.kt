package com.example.android.politicalpreparedness.representative.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.android.politicalpreparedness.network.models.Office
import com.example.android.politicalpreparedness.network.models.Official

data class Representative (
        val official: Official,
        val office: Office,
)