package com.example.towingapp.objects

import android.content.Context
import android.graphics.Color
import com.example.towingapp.Utils.AddressesUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions


class PopUpMap(mapFragment: SupportMapFragment, val context: Context) : OnMapReadyCallback {

    private  var mMap: GoogleMap? = null

    init {
        mapFragment.getMapAsync(this)
    }



    override fun onMapReady(p0: GoogleMap) {
        mMap = p0

    }

    private fun updateCurrentLocationOnMap(
        userLatitude: Double,
        userLongitude: Double) {
        val userSydney = LatLng(userLatitude, userLongitude)

        mMap?.clear()
        val marker = mMap?.addMarker(
            MarkerOptions()
                .position(userSydney)
                .title( AddressesUtils.getAddress(userSydney , context)))
        marker?.showInfoWindow()


        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(userSydney, 17.0f))
    }


    fun setLocationOnMap(
        UserLatitude: Double,
        userLongitude: Double) {

        updateCurrentLocationOnMap(UserLatitude, userLongitude)

    }





}