package com.example.towingapp.Utils

import android.content.Context
import android.widget.Toast
import es.dmoral.toasty.Toasty

class ToastyUtils(private val context: Context) {


    fun setLocationToasty(gravity: Int, x: Int, y: Int) {


        Toasty.Config.getInstance()
            .setGravity(gravity, x, y).apply()


    }

    fun toastySuccess(msg: String) {
        Toasty.success(context, msg, Toast.LENGTH_SHORT, true).show()
    }

    fun toastyError(msg: String) {
        Toasty.error(context, msg, Toast.LENGTH_SHORT, true).show()
    }

    fun toastyWarning(msg: String) {
        Toasty.warning(context, msg, Toast.LENGTH_SHORT, true).show()
    }

    fun toastyInfo(msg: String) {
        Toasty.info(context, msg, Toast.LENGTH_SHORT, true).show();
    }
}