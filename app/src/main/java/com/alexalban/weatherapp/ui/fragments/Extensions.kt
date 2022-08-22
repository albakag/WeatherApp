package com.alexalban.weatherapp.ui.fragments

import android.app.Activity
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun Fragment.isPermissionGranted(parameter: String): Boolean{
    return ContextCompat.checkSelfPermission(activity as AppCompatActivity, parameter) == PackageManager.PERMISSION_GRANTED
}