package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter

class ElectionsFragment: Fragment() {

    //TODO: Declare ViewModel
    private lateinit var binding: FragmentElectionBinding
    private val viewModel: ElectionsViewModel by viewModels()
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //TODO: Add ViewModel values and create ViewModel

        //TODO: Add binding values
        binding = FragmentElectionBinding.inflate(inflater)
        binding.lifecycleOwner = this

        //TODO: Link elections to voter info

        //TODO: Initiate recycler adapters
        val electionList = ElectionListAdapter(ElectionListAdapter.ElectionListener{
            findNavController().navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(it.id, it.division))
        })
        binding.electionList.adapter = electionList
        binding.viewModel = viewModel

        //TODO: Populate recycler adapters
        viewModel.electionList.observe(viewLifecycleOwner, Observer {
            electionList.submitList(it)
        })

        viewModel.initSavedElection()
        val savedElectionList = ElectionListAdapter(ElectionListAdapter.ElectionListener{
            findNavController().navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(it.id, it.division))
        })
        binding.savedElectionList.adapter = savedElectionList
        viewModel.savedElectionList.observe(viewLifecycleOwner, Observer {
            savedElectionList.submitList(it)
        })

        return binding.root
    }

    //TODO: Refresh adapters when fragment loads

}