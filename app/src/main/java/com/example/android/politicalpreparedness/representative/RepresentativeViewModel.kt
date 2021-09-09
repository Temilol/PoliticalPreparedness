package com.example.android.politicalpreparedness.representative

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import com.example.android.politicalpreparedness.util.AsyncOperationState
import kotlinx.coroutines.launch
import java.util.*

class RepresentativeViewModel : ViewModel() {

    //TODO: Establish live data for representatives and address
    private val representativeRepository by lazy { RepresentativeRepository() }

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    private val _address = MutableLiveData<Address>()
    val address: LiveData<Address>
        get() = _address

    private val _state = MutableLiveData<AsyncOperationState>()
    val state: LiveData<AsyncOperationState>
        get() = _state
    private val _errorOccurred = MutableLiveData<Boolean>(false)
    val errorOccurred: LiveData<Boolean>
        get() = _errorOccurred

    //TODO: Create function to fetch representatives from API from a provided address
    fun fetchRepresentatives() {
        viewModelScope.launch {
            _state.postValue(AsyncOperationState.LOADING)
            _address.value?.let { add ->
                try {
                    representativeRepository.getRepresentatives(add.toFormattedString())
                    representativeRepository.representativeResponse.value?.let { response ->
                        val (offices, officials) = response
                        _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }
                        _state.postValue(AsyncOperationState.SUCCESS)
                    }
                } catch (error: Throwable) {
                    _state.postValue(AsyncOperationState.FAILURE)
                    _errorOccurred.postValue(true)
                    Log.e("RepresentativeViewModel", "Network call failure", error)
                }
            } ?: run {
                _errorOccurred.postValue(true)
            }
        }
    }

    fun resetErrorState() = _errorOccurred.postValue(false)

    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */

    //TODO: Create function get address from geo location
    fun geoCodeLocation(context: Context, location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        val address =  geocoder.getFromLocation(location.latitude, location.longitude, 1)
            .map { address ->
                Address(
                    address.thoroughfare,
                    address.subThoroughfare,
                    address.locality,
                    address.adminArea,
                    address.postalCode
                )
            }
            .first()
        _address.value = address
        return address
    }

    //TODO: Create function to get address from individual fields
    fun buildAddressModel(
        line1: String,
        line2: String?,
        city: String,
        state: String,
        zip: String
    ) {
        val address = Address(line1, line2, city, state, zip)
        _address.value = address
    }
}
