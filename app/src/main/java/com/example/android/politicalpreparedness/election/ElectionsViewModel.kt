package com.example.android.politicalpreparedness.election

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.database.ElectionDatabase.Companion.getInstance
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.util.AsyncOperationState
import kotlinx.coroutines.launch

//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(application: Application, context: Context): ViewModel() {
    private val database = getInstance(application)
    private val electionRepository by lazy { ElectionsRepository(database) }
    private val dataSource: ElectionDao = database.electionDao

    //TODO: Create live data val for upcoming elections
    val upcomingElections: LiveData<List<Election>> = electionRepository.electionList

    //TODO: Create live data val for saved elections
    private val _savedElections = MutableLiveData<List<Election>>()
    val savedElections: LiveData<List<Election>>
        get() = _savedElections

    private val _state = MutableLiveData<AsyncOperationState>()
    val state: LiveData<AsyncOperationState>
        get() = _state

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database
    init {
        viewModelScope.launch {
            if (isOnline(context)) {
                _state.postValue(AsyncOperationState.LOADING)
                try {
                    electionRepository.refreshElections()
                    _state.postValue(AsyncOperationState.SUCCESS)
                } catch (error: Throwable) {
                    _state.postValue(AsyncOperationState.FAILURE)
                    Toast.makeText(context, "Network call failure", Toast.LENGTH_SHORT)
                        .show()
                    Log.e("MainViewModel", "Network call failure", error)
                }
            } else {
                Toast.makeText(context, "No Internet Connection Detected", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    fun fetchSavedElections() {
        viewModelScope.launch {
            try {
                val elections = dataSource.getElectionList()
                _savedElections.postValue(elections)
            } catch (error: Throwable) {
                Log.e("MainViewModel", "Unable to fetch updated list", error)
            }
        }
    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    //TODO: Create functions to navigate to saved or upcoming election voter info
}