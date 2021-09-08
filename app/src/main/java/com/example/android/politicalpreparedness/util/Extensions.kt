package com.example.android.politicalpreparedness.util

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment


fun Fragment.setDisplayHomeAsUpEnabled(bool: Boolean) {
    if (activity is AppCompatActivity) {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(
            bool
        )
    }
}

fun Fragment.setTitle(title: String) {
    if (activity is AppCompatActivity) {
        (activity as AppCompatActivity).supportActionBar?.title = title
    }
}