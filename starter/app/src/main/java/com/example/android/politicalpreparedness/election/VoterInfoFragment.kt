package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.UnderlineSpan
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding

class VoterInfoFragment : Fragment() {

    private val viewModel: VoterInfoViewModel by viewModels()
    private lateinit var binding: FragmentVoterInfoBinding
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //TODO: Add ViewModel values and create ViewModel

        //TODO: Add binding values
        binding = FragmentVoterInfoBinding.inflate(inflater)

        //TODO: Populate voter info -- hide views without provided data.
        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
         */
        val id = VoterInfoFragmentArgs.fromBundle(requireArguments()).argElectionId
        val division = VoterInfoFragmentArgs.fromBundle(requireArguments()).argDivision
        viewModel.getVoterInfo(id)

        viewModel.voterInfoTitle.observe(viewLifecycleOwner, Observer {
            binding.electionName.title = it
        })

        viewModel.voterInfoTime.observe(viewLifecycleOwner, Observer {
            binding.electionDate.text = it
        })

        //TODO: Handle loading of URLs

        //TODO: Handle save button UI state
        viewModel.followed.observe(viewLifecycleOwner, Observer {
            binding.followBtn.text = when(it) {
                true -> getString(R.string.unfollow)
                false -> getString(R.string.follow)
            }
        })

        binding.followBtn.setOnClickListener {
            viewModel.setFollowElection(id)
        }

        setUpVoterInfoLink()
        //TODO: cont'd Handle save button clicks
        return binding.root
    }

    private fun setUpVoterInfoLink() {
        var votingLocationFinder : String? = null
        viewModel.votingLocationFinderUrl.observe(viewLifecycleOwner, Observer {
            votingLocationFinder = it
        })
        val locationInfoText = SpannableString(getString(R.string.voting_locations))
        locationInfoText.setSpan(UnderlineSpan(), 0, locationInfoText.length, 0)
        binding.stateLocations.text = locationInfoText
        binding.stateLocations.setOnClickListener {
            val browseIntent = Intent(Intent.ACTION_VIEW, Uri.parse(votingLocationFinder))
            startActivity(browseIntent)
        }

        var ballotInfoUrl : String? = null
        viewModel.ballotInfoUrl.observe(viewLifecycleOwner, Observer {
            ballotInfoUrl = it
        })
        val ballotInfoText = SpannableString(getString(R.string.ballot_information))
        ballotInfoText.setSpan(UnderlineSpan(), 0, ballotInfoText.length, 0)
        binding.stateBallot.text = ballotInfoText
        binding.stateBallot.setOnClickListener {
            val browseIntent = Intent(Intent.ACTION_VIEW, Uri.parse(ballotInfoUrl))
            startActivity(browseIntent)
        }
    }

    //TODO: Create method to load URL intents

}