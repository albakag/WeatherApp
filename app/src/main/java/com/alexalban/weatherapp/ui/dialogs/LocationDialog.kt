package com.alexalban.weatherapp.ui.dialogs

import android.content.Context
import android.widget.EditText
import androidx.appcompat.app.AlertDialog

object LocationDialog {

    fun getAlertDialog(context: Context, listener: Listener){
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        dialog.setTitle("Location not enabled")
        dialog.setMessage("Do you want enabled location manager?")
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok"){
            _,_, -> dialog.dismiss()
            listener.onClick(null)
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel"){
                _,_, -> dialog.dismiss()
        }
        dialog.show()
    }

    fun getSearchByCityDialog(context: Context, listener: Listener){
        val builder = AlertDialog.Builder(context)
        val edtCityName = EditText(context)
        builder.setView(edtCityName)
        val dialog = builder.create()
        dialog.setTitle("City search:")
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok"){
                _,_, -> dialog.dismiss()
            listener.onClick(edtCityName.text.toString())
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel"){
                _,_, -> dialog.dismiss()
        }
        dialog.show()
    }

    interface Listener{
        fun onClick(cityName: String?)
    }
}