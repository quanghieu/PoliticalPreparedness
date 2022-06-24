package com.example.android.politicalpreparedness.representative

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.repository.Repository
import com.example.android.politicalpreparedness.representative.model.RepresentativeLocalDB
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class RepresentativeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = Repository.getInstance(application)

    //TODO: Establish live data for representatives and address
    private val _listOfRepresentative = MutableLiveData<List<RepresentativeLocalDB>>()

    val listOfRepresentative: LiveData<List<RepresentativeLocalDB>>
        get() = _listOfRepresentative

    //TODO: Create function to fetch representatives from API from a provided address
    private val _status = MutableLiveData<LoadingStatus>()

    val status: LiveData<LoadingStatus>
        get() = _status

    init {
        _status.value = LoadingStatus.DONE
    }

    fun getRepresentatives(binding: FragmentRepresentativeBinding) {
        if (binding.addressLine1.text.toString().isEmpty() || binding.addressLine2.text.toString()
                .isEmpty() || binding.city.text.toString().isEmpty() || binding.zip.toString()
                .isEmpty()
        ) {
            Toast.makeText(getApplication(), "No field should be empty", Toast.LENGTH_SHORT).show()
            return
        }
        val address = binding.zip.text.toString()
        _status.value = LoadingStatus.LOADING
        viewModelScope.launch {
            repository.getListOfRepresentative(address)
            try {
                val representList =
                    ElectionDatabase.getInstance(getApplication()).electionDao.getRepresentativesByAddress(
                        address
                    )
                _listOfRepresentative.postValue(representList)
                if (representList.isEmpty()) {
                    _status.value = LoadingStatus.ERROR
                } else {
                    _status.value = LoadingStatus.DONE
                }
            } catch (e: Exception) {
                _status.value = LoadingStatus.ERROR
                e.printStackTrace()
            }
        }
    }

    fun updateCacheRepresentativeList(cacheList : List<RepresentativeLocalDB>) {
        _listOfRepresentative.value = cacheList
    }

    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */

    //TODO: Create function get address from geo location

    //TODO: Create function to get address from individual fields

    enum class LoadingStatus {
        LOADING,
        DONE,
        ERROR
    }
}
