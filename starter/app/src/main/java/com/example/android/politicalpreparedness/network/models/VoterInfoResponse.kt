package com.example.android.politicalpreparedness.network.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class VoterInfoResponse (
    @Json(name = "election")
    val election: Election,
    val pollingLocations: String? = null, //TODO: Future Use
    @Json(name = "contests")
    val contests: List<Any?>? = null, //TODO: Future Use
    @Json(name = "state")
    val state: List<State>? = null,
    val electionElectionOfficials: List<ElectionOfficial>? = null
)