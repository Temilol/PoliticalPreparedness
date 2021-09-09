package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.ElectionItemListBinding
import com.example.android.politicalpreparedness.network.models.Election

class ElectionListAdapter(private val clickListener: ElectionListener): ListAdapter<Election, ElectionViewHolder>(ElectionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        return from(parent)
    }

    //TODO: Bind ViewHolder
    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
        val election = getItem(position)
        holder.bind(election, clickListener)
    }

    //TODO: Add companion object to inflate ViewHolder (from)
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.election_item_list

        fun from(parent: ViewGroup): ElectionViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ElectionItemListBinding.inflate(inflater, parent, false)
            return ElectionViewHolder(binding)
        }
    }
}

//TODO: Create ElectionViewHolder
class ElectionViewHolder(private val viewDataBinding: ElectionItemListBinding) : RecyclerView.ViewHolder(viewDataBinding.root) {
    fun bind(election: Election, clickListener: ElectionListener) {
        viewDataBinding.election = election
        viewDataBinding.electionClick = clickListener

        viewDataBinding.executePendingBindings()
    }
}

//TODO: Create ElectionDiffCallback
class ElectionDiffCallback : DiffUtil.ItemCallback<Election>() {
    override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem.id == newItem.id
    }
}

//TODO: Create ElectionListener
class ElectionListener(val block: (election: Election) -> Unit) {
    fun onClick(election: Election) = block(election)
}