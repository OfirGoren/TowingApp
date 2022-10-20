package com.example.towingapp.Utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import java.util.*

class AddressesUtils {


    companion object {

        fun getAddress(latLng: LatLng, context: Context): String {
            val geocoder = Geocoder(context, Locale.getDefault())
            val address: Address?
            var addressText = ""

            val addresses: List<Address>? =
                geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    address = addresses[0]
                    addressText = address.getAddressLine(0)
                } else {
                    addressText = "its not appear"
                }
            }
            return addressText
        }


        fun getCountry(latLng: LatLng, context: Context) {

            fun getAddress(latLng: LatLng, context: Context): String {
                val geocoder = Geocoder(context, Locale.getDefault())
                val co: Address?
                var countyName = ""

                val country: List<Address>? =
                    geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

                if (country != null) {
                    if (country.isNotEmpty()) {
                        co = country[0]
                        countyName = co.countryName
                    } else {
                        countyName = "its not appear"
                    }
                }
                return countyName
            }

        }
    }
}