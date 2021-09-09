package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.politicalpreparedness.BuildConfig
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.util.setTitle
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar

class DetailFragment : Fragment() {
    private lateinit var binding: FragmentRepresentativeBinding

    //TODO: Declare ViewModel
    private val representativeViewModel: RepresentativeViewModel by viewModels()
    private var representativeListAdapter: RepresentativeListAdapter? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var arrayStates: Array<String>
    private lateinit var arrayAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        setTitle(getString(R.string.find_my_representatives))
        //TODO: Establish bindings
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_representative, container, false)

        binding.lifecycleOwner = this
        binding.representativeViewModel = representativeViewModel
        arrayStates = resources.getStringArray(R.array.states)
        arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, arrayStates)

        //TODO: Define and assign Representative adapter
        representativeListAdapter = RepresentativeListAdapter()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        //TODO: Populate Representative adapter
        binding.representativeRecycler.adapter = representativeListAdapter
        binding.representativeRecycler.adapter = representativeListAdapter

        //TODO: Establish button listeners for field and location search
        binding.buttonSearch.setOnClickListener {
            representativeViewModel.buildAddressModel(
                binding.addressLine1.text.toString(),
                binding.addressLine2.text.toString(),
                binding.city.text.toString(),
                binding.state.selectedItem as String,
                binding.zip.text.toString()
            )
            hideKeyboard()
            representativeViewModel.fetchRepresentatives()
        }

        binding.buttonLocation.setOnClickListener {
            hideKeyboard()
            if (checkLocationPermissions()) {
                getLocation()
            }
        }

        return binding.root
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //TODO: Handle location permission result to get location on permission granted
        if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED) && (requestCode == REQUEST_LOCATION_PERMISSION)) {
            getLocation()
        } else {
            Snackbar.make(
                binding.root,
                R.string.permission_error,
                Snackbar.LENGTH_INDEFINITE
            ).setAction(R.string.settings) {
                startActivity(Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }.show()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            true
        } else {
            //TODO: Request Location permissions
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
            false
        }
    }

    private fun isPermissionGranted(): Boolean {
        //TODO: Check if permission is already granted and return (true = granted, false = denied/other)
        return (PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ))
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        //TODO: Get location from LocationServices
        //TODO: The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address
        val lastLocation = fusedLocationProviderClient.lastLocation
        lastLocation.addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                task.result?.let { lastKnownLocation ->
                    val address = representativeViewModel.geoCodeLocation(requireContext(), lastKnownLocation)
                    binding.addressLine1.setText(address.line1)
                    binding.state.setSelection(arrayAdapter.getPosition(address.state))
                    binding.city.setText(address.city)
                    binding.zip.setText(address.zip)
                    representativeViewModel.fetchRepresentatives()
                } ?: Snackbar.make(
                    binding.root,
                    R.string.location_error,
                    Snackbar.LENGTH_LONG
                ).show()
            } else {
                Snackbar.make(binding.root, R.string.location_error, Snackbar.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    companion object {
        //TODO: Add Constant for Location request
        private const val REQUEST_LOCATION_PERMISSION = 1002
    }
}