package com.example.android.politicalpreparedness.election.adapter

import android.annotation.SuppressLint
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.State
import com.example.android.politicalpreparedness.util.AsyncOperationState

@BindingAdapter("electionData")
fun bindRecyclerViewToDisplayElectionList(recyclerView: RecyclerView, data: List<Election>?) {
    data?.let { elections ->
        (recyclerView.adapter as ElectionListAdapter).run {
            submitList(elections)
        }
    }
}

@BindingAdapter("recycleViewVisibility")
fun bindRecycleViewVisibility(recyclerView: RecyclerView, state: AsyncOperationState?) {
    recyclerView.visibility = when (state) {
        AsyncOperationState.LOADING -> View.GONE
        else -> View.VISIBLE
    }
}

@BindingAdapter("viewVisible")
fun bindViewVisibility(frameLayout: FrameLayout, state: AsyncOperationState?) {
    frameLayout.visibility = when (state) {
        AsyncOperationState.SUCCESS -> View.VISIBLE
        else -> View.GONE
    }
}

@BindingAdapter("spinnerVisibility")
fun bindSpinnerVisibility(view: View, state: AsyncOperationState?) {
    view.visibility = when (state) {
        AsyncOperationState.LOADING -> View.VISIBLE
        else -> View.GONE
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("correspondenceAddress")
fun bindViewAddress(textView: TextView, state: List<State>?) {
    state?.first()?.electionAdministrationBody?.correspondenceAddress?.let {
        textView.text = "${it.line1} ${it.city}, ${it.state} ${it.zip}"
        textView.visibility = View.VISIBLE
    } ?: run {
        textView.visibility = View.GONE
    }
}

@BindingAdapter("followButtonText")
fun bindFollowButtonText(button: Button, state: Boolean) {
    button.setText(
        if (state) {
            R.string.unfollow_election
        } else {
            R.string.follow_election
        }
    )
}