package com.example.towingapp.Utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;



import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import kotlin.jvm.JvmStatic;

public class SharedPref {
    private static SharedPref instance;
    private final SharedPreferences pref;
    private final SharedPreferences.Editor prefPut;


    @SuppressLint("CommitPrefEdits")
    private SharedPref(Context context) {
        pref = context.getSharedPreferences("MY_SP", Context.MODE_PRIVATE);
        prefPut = pref.edit();

    }

    public static SharedPref getInstance() {
        return instance;
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new SharedPref(context);
        }
    }

    public void putString(String key, String value) {
        prefPut.putString(key, value);
        prefPut.apply();
    }

    public void putInt(String key, int value) {
        prefPut.putInt(key, value);
        prefPut.apply();


    }

    public void putBoolean(String key, boolean value) {
        prefPut.putBoolean(key, value);
        prefPut.apply();
    }

    public void putFloat(String key, float value) {
        prefPut.putFloat(key, value);
        prefPut.apply();

    }

    public String getString(String key, String def) {
        return pref.getString(key, def);
    }

    public int getInt(String key, int value) {
        return pref.getInt(key, value);


    }

    public boolean getBoolean(String key, boolean value) {
        return pref.getBoolean(key, value);

    }

    public float getFloat(String key, float value) {
        return pref.getFloat(key, value);


    }

    public void ClearCache(String key) {
        prefPut.remove(key);
        prefPut.apply();
    }




    public <T> void saveObject(String key, T object) {

        Gson gson = new Gson();
        String json = gson.toJson(object);
        prefPut.putString(key, json);
        prefPut.commit();
    }

    public <T> T getObject(String key , Class<T> Class) {
        Gson gson = new Gson();
        String json = pref.getString(key, "");
        return gson.fromJson(json,Class);
    }


    /**
     * the method return ArrayList of object(dependent of your type you saved) from shared Preferences
     * using json
     *
     * @param key   set key of shared preferences
     * @param Class set class same as your arrayList type you saved
     * @param <T>
     * @return if there isn't data saved in the key value the method return empty arrayList, otherWise
     * return list from Shared Preferences that you saved
     */
    public <T> ArrayList<T> getListFromPrefJson(String key, Class<T> Class) {
        ArrayList<T> list;
        Gson gson = new Gson();
        String ttJson = SharedPref.getInstance().getString(key, null);
        if (ttJson == null) {
            list = new ArrayList<>();
        } else {
            Type collectionType = TypeToken.getParameterized(ArrayList.class, Class).getType();
            list = gson.fromJson(ttJson, collectionType);
        }
        return list;
    }

}