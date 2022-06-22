package com.example.android.politicalpreparedness.election

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.election.VoterInfoViewModel.Companion.ELECTION_PREFERENCE
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.Repository
import com.example.android.politicalpreparedness.representative.RepresentativeViewModel
import kotlinx.coroutines.launch

//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(context: Application): AndroidViewModel(context) {

    private val electionPreference = context.getSharedPreferences(ELECTION_PREFERENCE, Context.MODE_PRIVATE)
    private val repository = Repository.getInstance(context)
    private lateinit var electionDataSource : LiveData<List<Election>>
    private val observer = Observer<List<Election>> {
        _electionList.postValue(it)
        _status.value = RepresentativeViewModel.LoadingStatus.DONE
    }

    private val _status = MutableLiveData<RepresentativeViewModel.LoadingStatus>()

    val status: LiveData<RepresentativeViewModel.LoadingStatus>
        get() = _status

    private val _electionList = MutableLiveData<List<Election>>()
    val electionList: LiveData<List<Election>>
        get() = _electionList

    private val _savedElectionList = MutableLiveData<List<Election>>()
    val savedElectionList: LiveData<List<Election>>
        get() = _savedElectionList

    init {
        viewModelScope.launch {
            _status.value = RepresentativeViewModel.LoadingStatus.LOADING
            repository.refreshElection()
            Log.d("HIEU Init", "INIT")
            electionDataSource = repository.getAllElections()
            electionDataSource.observeForever(observer)
//            _electionList.postValue(repository.getAllElections().value)
            initSavedElection()
        }
    }

    fun initSavedElection() {
        viewModelScope.launch {
            val savedElection = electionPreference.all as Map<String, Boolean>
            val savedList: MutableList<Election> = mutableListOf()
            for (element in savedElection) {
                Log.d("HIEU saved Election", element.key)
                savedList.add(repository.getElectionById(element.key))
            }
            _savedElectionList.postValue(savedList)
        }
    }

    override fun onCleared() {
        electionDataSource.removeObserver(observer)
    }
    //TODO: Create live data val for upcoming elections

    //TODO: Create live data val for saved elections

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database

    //TODO: Create functions to navigate to saved or upcoming election voter info

}