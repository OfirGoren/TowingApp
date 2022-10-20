package com.example.towingapp.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startForegroundService
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.towingapp.R
import com.example.towingapp.Services.LocationService
import com.example.towingapp.Utils.SharedPref
import com.example.towingapp.activitiy.MainActivity
import com.example.towingapp.activitiy.PermissionActivity
import com.example.towingapp.databinding.FragmentMapBinding
import com.example.towingapp.databinding.PopUpRequestTowingBinding
import com.example.towingapp.objects.*
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


class MapFragment : Fragment() {

    private lateinit var dialogNewRequestTowingBuilder: AlertDialog.Builder
    private lateinit var dialog: AlertDialog
    private lateinit var binding: FragmentMapBinding
    private lateinit var popUpBinding: PopUpRequestTowingBinding
    private lateinit var towingMapUserLocation: TowingMap
    private lateinit var popUpMap: PopUpMap
    private lateinit var dialogUtils: DialogUtils
    private var towingDetailProfile: User? = null
    private var userDetail: UserDetailForTowing? = null
    private var towingProfileWithLocation: UserDetailForTowing? = null
    private val fireStoreHandler = FireStoreHandler()
    private var towingIsBusy = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)

        //  activity?.theme?.applyStyle(R.style.myThemeActivity, true);

        initValues()

        initListeners()
        initMap()
        isNeedToShowArrivedBtn()
        createPopUpDialog()
        //   shutDownReceiver()
        binding.mapSwitchLocation.isChecked = true
        fireStoreHandler.listenerUserTowingRequest(getNewTowingRequest)

        // checkPermission()

        return binding.root
    }

    private fun isNeedToShowArrivedBtn() {

        val middleDrive: Boolean = SharedPref.getInstance().getBoolean(MIDDLE_DRIVE, false)

        if (middleDrive) {
            binding.mapBTNArrived.visibility = View.VISIBLE

            towingProfileWithLocation =
                SharedPref.getInstance().getObject(DETAIL_OBJECT, UserDetailForTowing::class.java)

        }
    }

    override fun onStart() {
        super.onStart()

    }

    companion object {

        const val DESTROY_APP = "DestroyApp"
        const val DETAIL_OBJECT = "object"
        const val MIDDLE_DRIVE = "MIDDLE_DRIVE"

    }

    private fun initMap() {
        val mapFragment = childFragmentManager
            .findFragmentById(binding.map.id) as SupportMapFragment

        initMapGarages(mapFragment)
    }

    private fun initListeners() {
        initMapSwitchListener()
        initBtnArrivedListener()
        initListenersLogOut()
    }

    private fun initListenersLogOut() {
        binding.mapLogOut.setOnClickListener {
            Firebase.auth.signOut()
            openWelcomeFragment()
        }
    }

    private fun openWelcomeFragment() {

        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
        if (binding.mapSwitchLocation.isChecked) {
            binding.mapSwitchLocation.performClick()
        }

    }


    private fun initBtnArrivedListener() {
        binding.mapBTNArrived.setOnClickListener {
            dialog.dismiss()
            towingDetailProfile = null
            clearSharedPref()
            fireStoreHandler.deleteTowingFromUser(towingProfileWithLocation)
            stopSendingLocationToUserFromServer()
            binding.mapBTNArrived.visibility = View.INVISIBLE
        }
    }

    private fun stopSendingLocationToUserFromServer() {
        actionToService(LocationService.ARRIVED_TOWING)
    }


    private val myReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val location = intent.getParcelableExtra<Location>("EXTRA_LOCATION")


            if (location != null) {

                towingMapUserLocation.getLastLocationCallBack(location.latitude, location.longitude)


            }
        }

    }

    private fun refreshMap(location: Location) {

        towingMapUserLocation.getLastLocationCallBack(location.latitude, location.longitude)

    }


    private fun getDetailToUser(): UserDetailForTowing? {
        towingProfileWithLocation = UserDetailForTowing()
        towingProfileWithLocation?.name = towingDetailProfile?.name
        towingProfileWithLocation?.imageUri = towingDetailProfile?.imageUri
        towingProfileWithLocation?.latitude = 0.0
        towingProfileWithLocation?.longitude = 0.0
        towingProfileWithLocation?.Userid = userDetail?.Userid
        towingProfileWithLocation?.requestId = System.currentTimeMillis().toString()
        return towingProfileWithLocation
    }

    private fun startServiceLocation() {
        actionToService(LocationService.START_LOCATION_SERVICE)

    }

    private fun stopServiceLocation() {
        actionToService(LocationService.STOP_LOCATION_SERVICE)

    }

    private fun actionToService(action: String) {
        val startIntent = Intent(activity, LocationService::class.java)
        startIntent.action = action

        startForegroundService(requireContext(), startIntent)


    }


    override fun onPause() {
        super.onPause()

    }

    override fun onStop() {
        super.onStop()
        context?.let { LocalBroadcastManager.getInstance(it).unregisterReceiver(myReceiver) }
    }

    private fun saveObjectShardPerf(detailUser: UserDetailForTowing?) {
        if (binding.mapBTNArrived.isVisible) {
            SharedPref.getInstance().putBoolean(MIDDLE_DRIVE, true)
            SharedPref.getInstance().saveObject(DETAIL_OBJECT, detailUser)
        } else {
            SharedPref.getInstance().putBoolean(MIDDLE_DRIVE, false)
        }
    }

    override fun onDestroy() {


        super.onDestroy()


    }

    override fun onResume() {
        super.onResume()

        val intentFilter = IntentFilter(LocationService.BROADCAST_NEW_LOCATION_DETECTED)
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(myReceiver, intentFilter)


    }

    //    override fun onPause() {
//        super.onPause()
//        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(myReceiver)
//    }
    private fun startCurrentLocationService() {
        val startIntent = Intent(activity, LocationService::class.java)
        startIntent.action = "hello"
        startIntent.putExtra(DETAIL_OBJECT, getDetailToUser())
        startForegroundService(requireContext(), startIntent)

    }

    private fun shutDownReceiver() {
        try {
            activity?.unregisterReceiver(myReceiver)
            //Register or UnRegister your broadcast receiver here
        } catch (e: IllegalArgumentException) {

        }
    }

    private fun initMapGarages(mapFragment: SupportMapFragment) {
        towingMapUserLocation = TowingMap(mapFragment, requireContext().applicationContext)
    }

    //get new towing request from DB
    private val getNewTowingRequest = object : FireStoreHandler.UserRequestDetail {
        override fun getTowingDetailCallBack(detail: UserDetailForTowing) {

            if (!binding.mapBTNArrived.isVisible && binding.mapSwitchLocation.isChecked) {
                userDetail = detail
                towingIsBusy = true

                if (activity?.isFinishing == false) {
                    showTowingDialog()

                    insertDetailToPopUp(detail)

                }
            }
        }
    }


    private fun createPopUpDialog() {

        dialogNewRequestTowingBuilder = AlertDialog.Builder(context, R.style.AlertDialog)
        popUpBinding = PopUpRequestTowingBinding.inflate(layoutInflater)
        dialogNewRequestTowingBuilder.setView(popUpBinding.root)
        val mapPopUp = childFragmentManager
            .findFragmentById(popUpBinding.popUpMap.id) as SupportMapFragment

        initMapPopUp(mapPopUp)
        dialog = dialogNewRequestTowingBuilder.create()

        popUpInitListeners()

    }

    private fun initMapPopUp(mapPopUp: SupportMapFragment) {
        popUpMap = PopUpMap(mapPopUp, requireContext().applicationContext)
    }

    private fun popUpInitListeners() {
        popUpBinding.popUpExit.setOnClickListener {
            dialog.dismiss()
            towingIsBusy = false
        }
        popUpBinding.popUpBTNAccept.setOnClickListener {
            binding.mapBTNArrived.visibility = View.VISIBLE
            getTowingDetail()
            openNavigation()
            dialog.dismiss()

        }
        dialog.setOnCancelListener(cancelDialogPressed)

    }

    private fun sendObjectToService() {
        val startIntent = Intent(activity, LocationService::class.java)
        val detailUser = getDetailToUser()
        startIntent.action = LocationService.ACCEPT_TOWING
        startIntent.putExtra(DETAIL_OBJECT, detailUser)
        startForegroundService(requireContext(), startIntent)


        saveObjectShardPerf(detailUser)


    }

    private fun openNavigation() {

        val gmmIntentUri =
            Uri.parse("google.navigation:q=${userDetail?.latitude},${userDetail?.longitude}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

    private fun getTowingDetail() {
        fireStoreHandler.getTowingProfile(object : FireStoreHandler.TowingDetailCallBack {
            override fun towingDetailCallBack(towingDetail: User) {
                towingDetailProfile = towingDetail
                sendObjectToService()

            }

        })


    }


    private val cancelDialogPressed = DialogInterface.OnCancelListener {


    }


    private fun insertDetailToPopUp(userDetailForTowing: UserDetailForTowing) {

        popUpBinding.popUpTXTEmail.text = userDetailForTowing.name
        popUpBinding.popUpTXTCompany.text = userDetailForTowing.companyCar
        popUpBinding.popUpTXTModel.text = userDetailForTowing.modeCar
        popUpMap.setLocationOnMap(userDetailForTowing.latitude, userDetailForTowing.longitude)
        if (userDetailForTowing.imageUri?.isEmpty() == false) {
            Picasso.get()
                .load(Uri.parse(userDetailForTowing.imageUri))
                .into(popUpBinding.popUpIMG)

        }
    }

    private fun showTowingDialog() {
        dialog.show()

    }


    private fun initValues() {
        dialogUtils = DialogUtils()
    }


    private fun initMapSwitchListener() {
        binding.mapSwitchLocation.setOnCheckedChangeListener { buttonView, isChecked ->

            val serviceAlreadyRun = LocationService.isServiceRunningInForeground(
                requireContext(),
                LocationService::class.java
            )
            if (isChecked) {

                if (checkForMissingPermission() == null && !serviceAlreadyRun) {

                    startServiceLocation()
                } else if (checkForMissingPermission() != null) {
                    openPermissionActivity()
                }
            } else {

                clearSharedPref()
                towingDetailProfile = null
                stopServiceLocation()
            }
        }

    }

    private fun clearSharedPref() {
        SharedPref.getInstance().ClearCache(MIDDLE_DRIVE)
        SharedPref.getInstance().ClearCache(DETAIL_OBJECT)
    }

    private fun openPermissionActivity() {
        val intent = Intent(context, PermissionActivity::class.java)
        activity?.startActivity(intent)
        activity?.finish()
    }



    private fun openPermissionSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", activity?.packageName, null)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)


    }



    private fun checkForMissingPermission(): String? {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Manifest.permission.ACCESS_FINE_LOCATION
        }
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Manifest.permission.ACCESS_COARSE_LOCATION
        }
        return if (Build.VERSION.SDK_INT >= 29 && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        } else null
    }

}