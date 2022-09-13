package com.alexalban.weatherapp.ui.dialogs

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.alexalban.weatherapp.databinding.SearchCityDialogBinding

object SearchCityNameDialog {

    fun saveCityName(context: Context, listener: Listener): String{
        var alertDialog: AlertDialog? = null
        var cityName = "nothing"
        val builder = AlertDialog.Builder(context)
        val binding = SearchCityDialogBinding.inflate(LayoutInflater.from(context))
        builder.setView(binding.root)
        binding.btnSaveCity.setOnClickListener {

            if (binding.edtCitySearch.text.toString().isNotEmpty()){
                cityName = binding.edtCitySearch.text.toString().replaceFirstChar { it -> it.uppercaseChar() }
                Log.d("MyLog", cityName)
                listener.onClick(cityName = cityName)
            } else {
                alertDialog?.dismiss()
            }
        }
        alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawable(null)
        alertDialog.show()
        return cityName
    }

    interface Listener{
        fun onClick(cityName: String)
    }
}