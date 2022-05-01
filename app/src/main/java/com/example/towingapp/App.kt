package com.example.towingapp

import android.app.Application
import com.example.towingapp.Utils.Addresses
import com.example.towingapp.Utils.AddressesUtils
import com.example.towingapp.Utils.SharedPref

class App:Application() {

    override fun onCreate() {
        super.onCreate()
       SharedPref.init(this)
        Addresses.init(this)
    }
}