package com.example.android.politicalpreparedness.election

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.Repository
import kotlinx.coroutines.launch

class VoterInfoViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        val ELECTION_PREFERENCE = "ElectionPreference"
    }
    private val electionDatabase = ElectionDatabase.getInstance(application)
    private val _voterInfoTitle = MutableLiveData<String>()
    private val electionPreference = application.getSharedPreferences(ELECTION_PREFERENCE, Context.MODE_PRIVATE)
    private val repository = Repository.getInstance(application)

    val voterInfoTitle : LiveData<String>
        get() = _voterInfoTitle

    private val _voterInfoTime = MutableLiveData<String>()
    val voterInfoTime : LiveData<String>
        get() = _voterInfoTime

    private val _followed = MutableLiveData<Boolean>()
    val followed : LiveData<Boolean>
        get() = _followed

    private val _votingLocationFinderUrl = MutableLiveData<String>()
    val votingLocationFinderUrl: LiveData<String>
        get() = _votingLocationFinderUrl

    private val _ballotInfoUrl = MutableLiveData<String>()
    val ballotInfoUrl: LiveData<String>
        get() = _ballotInfoUrl

    private lateinit var election: Election

    //TODO: Add live data to hold voter info

    fun getVoterInfo(id : Int){
        viewModelScope.launch {
            election = repository.getElectionById(id.toString())
            _voterInfoTitle.postValue(election.name)
            _voterInfoTime.postValue(election.electionDay.toString())

            val voterInfoLocal = repository.getLinksVoterInfo(id)
            _votingLocationFinderUrl.postValue(voterInfoLocal.votingLocationFinderUrl)
            _ballotInfoUrl.postValue(voterInfoLocal.ballotInfoUrl)
        }

        isFollowed(id)
    }

    private fun isFollowed(id: Int) {
        _followed.value = electionPreference.getBoolean(id.toString(), false)
    }

    fun setFollowElection(id: Int) {
        val curFollowed = electionPreference.getBoolean(id.toString(), false)
        _followed.value = !curFollowed

        with(electionPreference.edit()) {
            when (curFollowed) {
                true -> remove(id.toString())
                false -> putBoolean(id.toString(), true)
            }
            apply()
        }
    }

    //TODO: Add var and methods to populate voter info

    //TODO: Add var and methods to support loading URLs

    //TODO: Add var and methods to save and remove elections to local database
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status


}