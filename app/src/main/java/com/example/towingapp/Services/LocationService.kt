package com.example.towingapp.Services

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.towingapp.R
import com.example.towingapp.activitiy.PermissionActivity
import com.example.towingapp.fragments.MapFragment
import com.example.towingapp.objects.FireStoreHandler
import com.example.towingapp.objects.FusedLocationHandler
import com.example.towingapp.objects.UserDetailForTowing
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult


class LocationService : Service() {

    private var lastShownNotificationId = -1
    private var fusedLocationHandler: FusedLocationHandler? = null
    private var isServiceRunningRightNow = false
    private var towingDetail:UserDetailForTowing?  =null
    private var fireStoreHandler:FireStoreHandler? = null


    companion object {
        const val START_LOCATION_SERVICE = "START_LOCATION_SERVICE"
        const val STOP_LOCATION_SERVICE = "STOP_LOCATION_SERVICE"
        const val NOTIFICATION_ID = 153
        const val BROADCAST_NEW_LOCATION_DETECTED = "BROADCAST_NEW_LOCATION_DETECTED"
        const val ACCEPT_TOWING = "ACCEPT_TOWING"
        const val STOP_FOREGROUND_SERVICE = "STOP_FOREGROUND_SERVICE"
       const val ARRIVED_TOWING = "ARRIVED_TOWING"


        fun isServiceRunningInForeground(context: Context, serviceClass: Class<*>): Boolean {
            val manager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
            Log.d("dsffdsfdssfd", "DFSfdsfds" + manager.getRunningServices(Int.MAX_VALUE).size)
            for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                if (serviceClass.name == service.service.className) {
                    if (service.foreground) {
                        return true
                    }
                }
            }
            return false
        }


    }


    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
        // getUpdateLocation()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId);
        val action = intent?.action
        action?.let { ActionName ->





            if (ActionName == START_LOCATION_SERVICE) {
                if (isServiceRunningRightNow) {
                    return START_STICKY
                }
                isServiceRunningRightNow = true
                startRecording()
                notifyToUserForForegroundService()
                getUpdateLocation()

                return START_STICKY

            } else if (ActionName == STOP_LOCATION_SERVICE) {
                fusedLocationHandler?.stopLocationUpdate()
                Log.d("DGsgsf", "FDSfsdfd")
                stopRecording()
                stopUpdateLocation()
                stopForeground(true)
                stopSelf()
                isServiceRunningRightNow = false
                return START_NOT_STICKY
            }else if(ActionName == ACCEPT_TOWING) {
                fireStoreHandler = FireStoreHandler()
                Log.d("asdasdsaasdwq" , "DSFfdsfds ")
                 towingDetail = intent.getParcelableExtra(MapFragment.DETAIL_OBJECT)
                if(towingDetail != null) {
                    Log.d("DFSdsffd" , "DSFfdsfds " + towingDetail )
                }

            }else if(ActionName == ARRIVED_TOWING) {
                towingDetail = null

            }

        }


        return START_STICKY


    }


    private fun stopUpdateLocation() {
        fusedLocationHandler?.stopLocationUpdate();
    }

    private fun getUpdateLocation() {

        fusedLocationHandler = FusedLocationHandler(this)
        fusedLocationHandler?.getLastLocation(locationCallback)

    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            p0 ?: return
            for (location in p0.locations) {
                if (location != null) {

                    if(towingDetail != null) {
                        towingDetail?.latitude = location.latitude
                        towingDetail?.longitude = location.longitude
                        fireStoreHandler?.sendDetailTowingToUser(towingDetail!!)
                    }


                    Log.d("FDsfdsdsf", "SFdsa" + location.latitude + " " + location.longitude)

                    val intent = Intent(BROADCAST_NEW_LOCATION_DETECTED)
                    intent.putExtra("EXTRA_LOCATION", location)
                  LocalBroadcastManager.getInstance(this@LocationService).sendBroadcast(intent)

                }
            }
        }
    }


    override fun onTaskRemoved(rootIntent: Intent?) {
        val restartServiceIntent = Intent(applicationContext, this.javaClass)
        restartServiceIntent.setPackage(packageName)
        val restartServicePendingIntent = PendingIntent.getService(
            applicationContext,
            1,
            restartServiceIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmService = applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
        alarmService[AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000] =
            restartServicePendingIntent
        super.onTaskRemoved(rootIntent)
    }


    @SuppressLint("WakelockTimeout")
    private fun startRecording() {
        // Keep CPU working
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PassiveApp:tag")
        wakeLock.acquire()


    }

    private fun stopRecording() {


    }


    private fun notifyToUserForForegroundService() {
        // On notification click
        val notificationIntent = Intent(this, PermissionActivity::class.java)
        notificationIntent.action = Intent.ACTION_CAMERA_BUTTON
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(
            this,
            NOTIFICATION_ID,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notificationBuilder = getNotificationBuilder(
            this,
            Intent.ACTION_CAMERA_BUTTON,
            NotificationManagerCompat.IMPORTANCE_LOW
        ) //Low importance prevent visual appearance for this notification channel on top
        notificationBuilder.setContentIntent(pendingIntent) // Open activity
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_round))
            .setContentTitle(getString(R.string.AppInPprogress))
            .setContentText(getString(R.string.content))
        val notification: Notification = notificationBuilder.build()
        startForeground(NOTIFICATION_ID, notification)
        if (NOTIFICATION_ID != lastShownNotificationId) {
            // Cancel previous notification
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(lastShownNotificationId)
        }
        lastShownNotificationId = NOTIFICATION_ID
    }


    private fun getNotificationBuilder(
        context: Context,
        channelId: String,
        importance: Int
    ): NotificationCompat.Builder {
        val builder: NotificationCompat.Builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                prepareChannel(
                    this, channelId, importance
                )
                NotificationCompat.Builder(context, channelId)
            } else {
                NotificationCompat.Builder(context)
            }
        return builder
    }

    @TargetApi(26)
    private fun prepareChannel(context: Context, id: String, importance: Int) {
        val appName = context.getString(R.string.app_name)
        val notificationsChannelDescription = getString(R.string.service)
        val nm = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        var nChannel = nm.getNotificationChannel(id)
        if (nChannel == null) {
            nChannel = NotificationChannel(id, appName, importance)
            nChannel.description = notificationsChannelDescription

            // from another answer
            nChannel.enableLights(true)
            nChannel.lightColor = Color.BLUE
            nm.createNotificationChannel(nChannel)
        }
    }

}