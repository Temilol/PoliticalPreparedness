package com.example.android.politicalpreparedness.election

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.database.ElectionDatabase.Companion.getInstance
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.util.AsyncOperationState
import kotlinx.coroutines.launch

class VoterInfoViewModel(
    private val electionId: Int,
    private val electionDivision: Division,
    private val application: Application
) : ViewModel() {
    private val database = getInstance(application)
    private val electionRepository by lazy { ElectionsRepository(database) }
    private val dataSource: ElectionDao = database.electionDao
    private val _electionFollowed = MutableLiveData<Boolean>(false)
    val electionFollowed: LiveData<Boolean>
        get() = _electionFollowed
    private val address by lazy { "${electionDivision.state} ${electionDivision.country}" }
    private val _state = MutableLiveData<AsyncOperationState>()
    val state: LiveData<AsyncOperationState>
        get() = _state
    private val _errorOccurred = MutableLiveData<Boolean>(false)
    val errorOccurred: LiveData<Boolean>
        get() = _errorOccurred
    private val _externalLink = MutableLiveData<String>()
    val externalLink: LiveData<String>
        get() = _externalLink

    //TODO: Add live data to hold voter info
    val voterInfo: LiveData<VoterInfoResponse> = electionRepository.voterInfo

    //TODO: Add var and methods to populate voter info
    fun fetchVoterInfo() {
        _state.postValue(AsyncOperationState.LOADING)
        viewModelScope.launch {
            try {
                val election = dataSource.getElectionById(electionId)
                electionRepository.getVotersInfoElections(electionId, address)
                _electionFollowed.postValue(election != null)
                _state.postValue(AsyncOperationState.SUCCESS)
            } catch (error: Throwable) {
                _state.postValue(AsyncOperationState.FAILURE)
                _errorOccurred.postValue(true)
                Log.e(TAG, "Unable to load the voter info", error)
            }
        }
    }

    fun resetErrorState() = _errorOccurred.postValue(false)

    //TODO: Add var and methods to support loading URLs
    fun launchBallotInformation() =
        _externalLink.postValue(voterInfo.value?.state?.first()?.electionAdministrationBody?.ballotInfoUrl)

    fun launchVotingLocation() =
        _externalLink.postValue(voterInfo.value?.state?.first()?.electionAdministrationBody?.votingLocationFinderUrl)

    fun resetExternalLink() = _externalLink.postValue(null)

    //TODO: Add var and methods to save and remove elections to local database
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status
    fun updateElection() {
        voterInfo.value?.election?.let {
            viewModelScope.launch {
                if (_electionFollowed.value == false) {
                    dataSource.saveElection(it)
                    _electionFollowed.postValue(true)
                } else {
                    dataSource.deleteElectionById(it.id)
                    _electionFollowed.postValue(false)
                }
            }
        }
    }

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

    companion object {
        private const val TAG = "VoterInfoViewModel"
    }
}