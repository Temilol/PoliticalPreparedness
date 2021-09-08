package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.util.setDisplayHomeAsUpEnabled

class VoterInfoFragment : Fragment() {

    private lateinit var binding: FragmentVoterInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //TODO: Add ViewModel values and create ViewModel
        val electionId = VoterInfoFragmentArgs.fromBundle(requireArguments()).argElectionId
        val electionDivision = VoterInfoFragmentArgs.fromBundle(requireArguments()).argDivision
        val voterInfoViewModel: VoterInfoViewModel by lazy {
            ViewModelProvider(
                this, VoterInfoViewModelFactory(
                    electionId, electionDivision,
                    requireActivity().application
                )
            ).get(
                VoterInfoViewModel::class.java
            )
        }

        //TODO: Add binding values
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_voter_info, container, false)

        binding.lifecycleOwner = this
        binding.voterInfoViewModel = voterInfoViewModel

        //TODO: Populate voter info -- hide views without provided data.
        /**
         * Hint: You will need to ensure proper data is provided from previous fragment.
         */
        voterInfoViewModel.fetchVoterInfo()

        //TODO: Handle loading of URLs
        //TODO: Handle save button UI state
        //TODO: cont'd Handle save button clicks

        voterInfoViewModel.externalLink.observe(viewLifecycleOwner) { link ->
            loadUrlIntents(link)
            voterInfoViewModel.resetExternalLink()
        }

        voterInfoViewModel.errorOccurred.observe(viewLifecycleOwner) { error ->
            if (error) {
                Toast.makeText(context, "Unable to load the voter info", Toast.LENGTH_SHORT).show()
                voterInfoViewModel.resetErrorState()
                findNavController().popBackStack()
            }
        }
        setDisplayHomeAsUpEnabled(true)

        return binding.root
    }

    //TODO: Create method to load URL intents
    private fun loadUrlIntents(urlIntent: String?) {
        urlIntent?.let { url ->
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
            }
            startActivity(intent)
        }
    }
}