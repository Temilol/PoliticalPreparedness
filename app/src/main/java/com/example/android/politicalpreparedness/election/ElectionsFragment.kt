package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.util.setDisplayHomeAsUpEnabled
import com.example.android.politicalpreparedness.util.setTitle

class ElectionsFragment : Fragment() {

    //TODO: Declare ViewModel
    private lateinit var binding: FragmentElectionBinding
    val electionsViewModel: ElectionsViewModel by lazy {
        ViewModelProvider(
            this, ElectionsViewModelFactory(
                requireActivity().application, requireContext()
            )
        ).get(
            ElectionsViewModel::class.java
        )
    }

    private var upcomingElectionAdapter: ElectionListAdapter? = null
    private var savedElectionAdapter: ElectionListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        setTitle(getString(R.string.my_elections))
        //TODO: Add ViewModel values and create ViewModel

        //TODO: Add binding values
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_election, container, false)

        binding.lifecycleOwner = this
        binding.electionViewModel = electionsViewModel

        //TODO: Link elections to voter info

        //TODO: Initiate recycler adapters
        upcomingElectionAdapter = ElectionListAdapter(ElectionListener {
            findNavController().navigate(
                ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                    it.id,
                    it.division
                )
            )
        })
        savedElectionAdapter = ElectionListAdapter(ElectionListener {
            findNavController().navigate(
                ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                    it.id,
                    it.division
                )
            )
        })

        //TODO: Populate recycler adapters
        binding.upcomingElectionRecycler.adapter = upcomingElectionAdapter
        binding.savedElectionRecycler.adapter = savedElectionAdapter
        setDisplayHomeAsUpEnabled(true)

        return binding.root
    }

    override fun onResume() {
        electionsViewModel.fetchSavedElections()
        super.onResume()
    }
}