package com.example.android.politicalpreparedness.representative

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RepresentativeRepository() {
    private val _representativeResponse = MutableLiveData<RepresentativeResponse>()
    val representativeResponse: LiveData<RepresentativeResponse>
        get() = _representativeResponse

    @Throws(Throwable::class)
    suspend fun getRepresentatives(address: String) {
        withContext(Dispatchers.IO) {
            val response = CivicsApi.retrofitService.getMyRepresentatives(address)
            Log.d("RepresentativeRepository - getRepresentatives", response.toString())
            _representativeResponse.postValue(response)
        }
    }
}