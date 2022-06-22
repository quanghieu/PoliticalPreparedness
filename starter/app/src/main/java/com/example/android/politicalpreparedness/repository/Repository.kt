package com.example.android.politicalpreparedness.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.WikipediaApiService
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfo
import com.example.android.politicalpreparedness.network.wikipediaAPI
import com.example.android.politicalpreparedness.representative.model.Representative
import com.example.android.politicalpreparedness.representative.model.RepresentativeLocalDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository private constructor(val context: Application) {
    private var electionDatabase: ElectionDatabase = ElectionDatabase.getInstance(context)

    companion object {
        @Volatile
        private var INSTANCE: Repository? = null

        fun getInstance(context: Application): Repository {
            var instance = INSTANCE
            synchronized(this) {
                if (instance == null) {
                    instance = Repository(context)
                    INSTANCE = instance
                }
            }
            return instance!!
        }
    }

    suspend fun refreshElection() {
        withContext(Dispatchers.IO) {
            try {
                val electionList = CivicsApi.retrofitService.getElections().elections
                for (election in electionList) {
                    storeVoterInfo(election)
                    storeElection(election)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun storeElection(election: Election) {
        electionDatabase.electionDao.insertAll(election)
    }

    private suspend fun storeVoterInfo(election: Election) {
        Log.d("HIEU", "Call voter info with address is ${getElectionLocation(election)} and id is ${election.id}")
        val voterInfo = CivicsApi.retrofitService.getVoterInfo(
            getElectionLocation(election),
            election.id.toString()
        )
        val voterInfoLocal = VoterInfo(
            election.id,
            voterInfo.state?.get(0)?.electionAdministrationBody?.votingLocationFinderUrl!!,
            voterInfo.state?.get(0)?.electionAdministrationBody?.ballotInfoUrl!!
        )
        electionDatabase.electionDao.insertVoterInfo(voterInfoLocal)
    }

    suspend fun getLinksVoterInfo(id: Int) : VoterInfo{
        return electionDatabase.electionDao.getVoterInfoById(id)
    }

    fun getElectionLocation(election: Election): String {
        val districtDelimiter = "district:"
        val division = election.division
        val state : String = division.state
        val district : String = division.id.substringAfter(districtDelimiter,"")
            .substringBefore("/")
        val returnState = "ga"
//            if (state == "" && district == "") "ga" else
//                          if (state == "" && district != "") district
//                            else state
        return "state:${returnState}"
    }

    fun getAllElections(): LiveData<List<Election>> {
        return electionDatabase.electionDao.getAllElections()
    }

    suspend fun getElectionById(id: String): Election {
        return electionDatabase.electionDao.getElection(id)
    }

    suspend fun getListOfRepresentative(address: String) {
        try {
            val representativeResponse = CivicsApi.retrofitService.getRepresentatives(address)
            var listOfRepresentatives: MutableList<Representative> =
                emptyList<Representative>().toMutableList()
            for (office in representativeResponse.offices) {
                listOfRepresentatives.addAll(office.getRepresentatives(representativeResponse.officials))
            }

            storeRepresentatives(listOfRepresentatives, address)
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getLocalRepresentatives(address: String) : List<RepresentativeLocalDB>{
        return electionDatabase.electionDao.getRepresentativesByAddress(address)
    }

    private suspend fun storeRepresentatives(
        listOfRepresentatives: MutableList<Representative>,
        address: String
    ) {
        for ((index, person) in listOfRepresentatives.withIndex()) {
            var facebook : String? = null
            var twitter: String? = null
            person.official.channels?.let {
                for (channel in person.official.channels) {
                    if (channel.type == "Facebook") {
                        facebook = channel.id
                    }
                    if (channel.type == "Twitter") {
                        twitter = channel.id
                    }
                }
            }

            // Get url, give more priority to get wikipedia link so as we can get the portrait image
            val personUrl = getPersonalLink(person)

            var imageUrl: String? = getPhotoLink(personUrl)

            val representativeLocalDB = RepresentativeLocalDB(index, address, person.office.name, person.official.name, person.official.party!!,
                personUrl, facebook, twitter, imageUrl)
            electionDatabase.electionDao.insertRepresentatives(representativeLocalDB)
        }
    }

    private suspend fun getPhotoLink(personUrl : String?) : String? {
        var imageUrl : String? = null
        personUrl?.let {
            if (it.contains("wikipedia")){
                val title = it.substringAfter("wiki/")
                Log.d("HIEU", "title is ${title}")
                val wikiResponse = wikipediaAPI.wikipediaApiService.getImageUrl(title)
                val startDelimiter = "\"source\":\""
                val endDelimiter = "\",\"width"
                if (!wikiResponse.contains(startDelimiter)) {
                    return@let
                }
                imageUrl = wikiResponse.substringAfter(startDelimiter).substringBefore(endDelimiter)
            }
        }
        return imageUrl
    }

    private fun getPersonalLink(person: Representative) : String? {
        person.official.urls?.let {
            for ( url in it) {
                if (url.contains("wikipedia")) {
                    return url
                }
            }
            return it.get(0)
        }
        return null
    }
}