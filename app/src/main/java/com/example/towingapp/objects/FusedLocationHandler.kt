package com.example.towingapp.objects

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentSender
import android.os.Looper
import android.util.Log

import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.tasks.Task


class FusedLocationHandler(val context: Context) {

    interface GetLastLocationCallBack {
        fun getLastLocationCallBack(latitude: Double, longitude: Double)

    }


    private var fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private lateinit var getLastLocationCallBack: GetLastLocationCallBack
    private var locationResult:LocationCallback? = null


    fun initGetLastLocationCallBack(getLastLocationCallBack: GetLastLocationCallBack) {
        this.getLastLocationCallBack = getLastLocationCallBack

    }

    @SuppressLint("MissingPermission")
    fun getLastLocation(locationCallback: LocationCallback) {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(createLocationRequest())

        val client: SettingsClient = LocationServices.getSettingsClient(context)

        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        this.locationResult = locationCallback
        fusedLocationClient.requestLocationUpdates(
            createLocationRequest(),
            locationCallback,
            Looper.getMainLooper()
        )


//        task.addOnSuccessListener {
//            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
//                if(location != null) {
//                    Log.d("FDsfdsdsf" , "SFdsa" + location.latitude + " " + location.longitude )
//
//                    getLastLocationCallBack.getLastLocationCallBack(
//                        location.latitude,
//                        location.longitude
//
//                    )
//                }
//            }
//
//
//        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().

                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }

    }


    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            p0 ?: return
            for (location in p0.locations) {
                if (location != null) {
                    Log.d("FDsfdsdsf", "SFdsa" + location.latitude + " " + location.longitude)

                    getLastLocationCallBack.getLastLocationCallBack(
                        location.latitude,
                        location.longitude

                    )
                }
            }
        }
    }


    fun stopLocationUpdate() {
        this.locationResult?.let { fusedLocationClient.removeLocationUpdates(it) }
    }

    private fun createLocationRequest(): LocationRequest {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 10000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        return locationRequest

    }
}




