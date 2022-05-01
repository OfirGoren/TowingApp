package com.example.towingapp.Utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Addresses {
    private static Addresses instance;
    private final Geocoder geocoder;
    private List<Address> addresses;


    private Addresses(Context context) {
        geocoder = new Geocoder(context, Locale.ENGLISH);

    }

    public static Addresses getInstance() {

        return instance;

    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new Addresses(context);
        }
    }


    /**
     * the method get coordinates and return the address
     *
     * @return address in this coordinates if there isn't return null
     */
    public String getAddress(double latitude, double longitude) {


        getFromLocation(latitude, longitude);

        if (addresses != null && addresses.size() > 0) {
            getFromLocation(latitude, longitude);
            return addresses.get(0).getAddressLine(0);

        }
        return null;
    }


    private void getFromLocation(double latitude, double longitude) {

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    /**
     * the method get coordinates and return the city
     *
     * @return the city in this coordinates if there isn't return null
     */
    public String getCity(double latitude, double longitude) {
        getFromLocation(latitude, longitude);
        if (addresses != null && addresses.size() > 0) {

            String a = this.addresses.get(0).getLocality();
            return this.addresses.get(0).getLocality();
        }
        return null;
    }


    /**
     * the method get coordinates and return the State
     *
     * @return the State in this coordinates if there isn't return null
     */
    public String getState(double latitude, double longitude) {
        getFromLocation(latitude, longitude);
        if (addresses != null && addresses.size() > 0) {
            getFromLocation(latitude, longitude);
            return this.addresses.get(0).getAdminArea();

        }
        return null;
    }

    /**
     * the method get coordinates and return the city
     *
     * @return the city in this coordinates if there isn't return null
     */
    public String getCountry(double latitude, double longitude) {
        getFromLocation(latitude, longitude);
        if (addresses != null && addresses.size() > 0) {
            getFromLocation(latitude, longitude);
            return this.addresses.get(0).getCountryName();
        }
        return null;

    }

    /**
     * the method get coordinates and return the Postal Code
     *
     * @return the Postal Code in this coordinates if there isn't return null
     */
    public String getPostAlCode(double latitude, double longitude) {

        getFromLocation(latitude, longitude);
        if (addresses != null && addresses.size() > 0) {
            getFromLocation(latitude, longitude);
            return this.addresses.get(0).getPostalCode();

        }
        return null;
    }

    /**
     * the method get coordinates and return the number house
     *
     * @return the number house in this coordinates if there isn't return null
     */
    public String getKnownName(double latitude, double longitude) {
        getFromLocation(latitude, longitude);
        if (addresses != null && addresses.size() > 0) {
            getFromLocation(latitude, longitude);
            return this.addresses.get(0).getFeatureName();


        }
        return null;
    }


}