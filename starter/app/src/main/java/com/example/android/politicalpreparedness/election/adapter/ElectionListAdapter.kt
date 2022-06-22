package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.databinding.ElectionItemBinding
import com.example.android.politicalpreparedness.network.models.Election

class ElectionListAdapter(val clickListener: ElectionListener): ListAdapter<Election, ElectionListAdapter.ElectionViewHolder>(ElectionDiffCallback()) {
    class ElectionDiffCallback : DiffUtil.ItemCallback<Election>() {
        override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
            return ((oldItem.name == newItem.name) && (oldItem.electionDay == newItem.electionDay))
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        return ElectionViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
        val election = getItem(position)
        holder.bind(election)
        holder.itemView.setOnClickListener {
            clickListener.onClick(election)
        }
    }

    //TODO: Bind ViewHolder

    //TODO: Add companion object to inflate ViewHolder (from)
    class ElectionViewHolder(val binding: ElectionItemBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup) : ElectionViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val view = ElectionItemBinding.inflate(inflater, parent, false)
                return ElectionViewHolder(view)
            }
        }

        fun bind(election: Election) {
            binding.titleElection.text = election.name
            binding.timeElection.text = election.electionDay.toString()
        }
    }

    class ElectionListener(val function: (Election) -> Unit) {
        fun onClick(election: Election) {
            function(election)
        }
    }
}

//TODO: Create ElectionViewHolder

//TODO: Create ElectionDiffCallback

//TODO: Create ElectionListener