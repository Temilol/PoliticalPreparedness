package com.example.android.politicalpreparedness.election

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ElectionsRepository(private val database: ElectionDatabase) {
    private val _electionList = MutableLiveData<List<Election>>()
    val electionList: LiveData<List<Election>>
        get() = _electionList

    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse>
        get() = _voterInfo

    @Throws(Throwable::class)
    suspend fun refreshElections() {
        withContext(Dispatchers.IO) {
            val response = CivicsApi.retrofitService.getUpcomingElections()
            Log.d("ElectionsRepository - refreshElections", response.toString())
            _electionList.postValue(response.elections)
        }
    }

    @Throws(Throwable::class)
    suspend fun getVotersInfoElections(electionId: Int, address: String) {
        withContext(Dispatchers.IO) {
            val response = CivicsApi.retrofitService.getVoterInfo(electionId, address)
            Log.d("ElectionsRepository - getVotersInfoElections", response.toString())
            _voterInfo.postValue(response)
        }
    }
}