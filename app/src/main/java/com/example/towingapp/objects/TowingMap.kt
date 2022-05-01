package com.example.towingapp.objects

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker


class TowingMap(mapFragment: SupportMapFragment, val context: Context) : OnMapReadyCallback,
    FusedLocationHandler.GetLastLocationCallBack {
    private  var mMap: GoogleMap? = null
    private var marker: ArrayList<Marker>
    private var fusedLocationHandler: FusedLocationHandler
    private var currentLat: Double? = null
    private var currentLng: Double? = null
    private val mapFrag = mapFragment


    init {
        mapFrag.getMapAsync(this)
        fusedLocationHandler = FusedLocationHandler(context)
        fusedLocationHandler.initGetLastLocationCallBack(this)
        marker = ArrayList()

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        Log.d("SDFsdf" , "SDFfdssd " + googleMap)


        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney ,18.0f))
    }

   fun funInitMap() {
       mapFrag.getMapAsync(this)
   }

    private fun updateCurrentLocationOnMap(latitude: Double, longitude: Double) {
        val sydney = LatLng(latitude, longitude)
        Log.d("FDSds" , "DFSdfsfds" + mMap)
        mMap?.clear()
        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14.0f))
    }

    @SuppressLint("MissingPermission")
    override fun getLastLocationCallBack(latitude: Double, longitude: Double) {
        currentLat = latitude
        currentLng = longitude
        updateCurrentLocationOnMap(latitude, longitude)

        mMap?.isMyLocationEnabled = true
    }



//    fun updateMapWithCurrentLocation() {
//        fusedLocationHandler.getLastLocation(locationCallback)
//
//    }




}