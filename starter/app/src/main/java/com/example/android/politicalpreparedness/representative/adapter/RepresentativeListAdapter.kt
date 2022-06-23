package com.example.android.politicalpreparedness.representative.adapter

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.RepresentativeItemBinding
import com.example.android.politicalpreparedness.network.models.Channel
import com.example.android.politicalpreparedness.representative.model.Representative
import com.example.android.politicalpreparedness.representative.model.RepresentativeLocalDB
import com.squareup.picasso.Picasso

class RepresentativeListAdapter :
    ListAdapter<RepresentativeLocalDB, RepresentativeViewHolder>(RepresentativeDiffCallback()) {
    class RepresentativeDiffCallback : DiffUtil.ItemCallback<RepresentativeLocalDB>() {
        override fun areItemsTheSame(
            oldItem: RepresentativeLocalDB,
            newItem: RepresentativeLocalDB
        ): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(
            oldItem: RepresentativeLocalDB,
            newItem: RepresentativeLocalDB
        ): Boolean {
            return oldItem.name == newItem.name
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepresentativeViewHolder {
        return RepresentativeViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RepresentativeViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, holder.itemView.context)
    }
}

class RepresentativeViewHolder(val binding: RepresentativeItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun from(parent: ViewGroup): RepresentativeViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = RepresentativeItemBinding.inflate(layoutInflater, parent, false)
            return RepresentativeViewHolder(view)
        }
    }

    class ViewholderRepresentativeBinding {

    }

    fun bind(item: RepresentativeLocalDB, context: Context) {
        binding.role.text = item.role
        binding.name.text = item.name
        binding.party.text = item.party
//        binding.representativePhoto.setImageResource(R.drawable.ic_profile)
        //TODO: Show social links ** Hint: Use provided helper methods
        //TODO: Show www link ** Hint: Use provided helper methods
        val photoUrl = item.imageUrl
        if (photoUrl == null) {
            binding.representativeImage.setImageResource(R.drawable.ic_profile)
        } else {
            Picasso.with(context).cancelRequest(binding.representativeImage)
            Picasso.with(context).load(photoUrl)
                .placeholder(R.drawable.ic_profile)
                .into(binding.representativeImage)
        }

        binding.webBtn.setOnClickListener {
            if (item.url != null) {
                browseWeb(item.url, context)
            }
            else {
                Toast.makeText(context, "There is no URL", Toast.LENGTH_SHORT).show()
            }
        }
        binding.facebookBtn.setOnClickListener {
            if (item.facebook != null) {
                browseWeb("https://facebook.com/${item.facebook}", context)
            }
            else
                Toast.makeText(context, "There is no facebook account", Toast.LENGTH_SHORT).show()
        }
        binding.twitterBtn.setOnClickListener {
            if (item.twitter != null) {
                browseWeb("https://twitter.com/${item.twitter}", context)
            }
            else
                Toast.makeText(context, "There is no twitter account", Toast.LENGTH_SHORT).show()
        }
        binding.executePendingBindings()
    }

    private fun browseWeb(
        url: String?,
        context: Context
    ) {
        val browseIntent = Intent(ACTION_VIEW, Uri.parse(url))
        context.startActivity(browseIntent)
    }

    //TODO: Add companion object to inflate ViewHolder (from)

//    private fun showSocialLinks(channels: List<Channel>) {
//        val facebookUrl = getFacebookUrl(channels)
//        if (!facebookUrl.isNullOrBlank()) { enableLink(binding.facebookIcon, facebookUrl) }
//
//        val twitterUrl = getTwitterUrl(channels)
//        if (!twitterUrl.isNullOrBlank()) { enableLink(binding.twitterIcon, twitterUrl) }
//    }

//    private fun showWWWLinks(urls: List<String>) {
//        enableLink(binding.wwwIcon, urls.first())
//    }

    private fun getFacebookUrl(channels: List<Channel>): String? {
        return channels.filter { channel -> channel.type == "Facebook" }
            .map { channel -> "https://www.facebook.com/${channel.id}" }
            .firstOrNull()
    }

    private fun getTwitterUrl(channels: List<Channel>): String? {
        return channels.filter { channel -> channel.type == "Twitter" }
            .map { channel -> "https://www.twitter.com/${channel.id}" }
            .firstOrNull()
    }

    private fun enableLink(view: ImageView, url: String) {
        view.visibility = View.VISIBLE
        view.setOnClickListener { setIntent(url) }
    }

    private fun setIntent(url: String) {
        val uri = Uri.parse(url)
        val intent = Intent(ACTION_VIEW, uri)
        itemView.context.startActivity(intent)
    }

}

//TODO: Create RepresentativeDiffCallback

//TODO: Create RepresentativeListener