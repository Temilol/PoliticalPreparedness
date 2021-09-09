package com.example.android.politicalpreparedness.representative.adapter

import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.representative.model.Representative

@BindingAdapter("representativeData")
fun bindRecyclerViewToDisplayRepresentativeList(
    recyclerView: RecyclerView,
    data: List<Representative>?
) {
    data?.let { representatives ->
        (recyclerView.adapter as RepresentativeListAdapter).run {
            submitList(representatives)
        }
    }
}

@BindingAdapter("profilePhoto")
fun loadImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_broken_image)
            ).into(imgView)
    }
}