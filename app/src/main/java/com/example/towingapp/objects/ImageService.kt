package com.example.towingapp.objects

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

import android.app.AlarmManager

import android.app.PendingIntent
import android.app.ActivityManager
import android.os.*
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.towingapp.R
import com.example.towingapp.Services.LocationService
import com.example.towingapp.activitiy.TowingActivity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult


class ImageService : Service() {

    private var lastShownNotificationId = -1
    private var fusedLocationHandler:FusedLocationHandler? = null

    companion object {
        const val START_IMAGE_SERVICE = "START_IMAGE_SERVICE"
        const val STOP_IMAGE_SERVICE = "STOP_IMAGE_SERVICE"
        const val NOTIFICATION_ID = 153
        const val IMAGE_DATE_KEY = "IMAGE_DATE_KEY"




        fun isServiceRunningInForeground(context: Context, serviceClass: Class<*>): Boolean {
            val manager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
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

        val mainHandler = Handler(Looper.getMainLooper())

        mainHandler.post(object : Runnable {
            override fun run() {
                mainHandler.postDelayed(this, 1000)
                // handler the checking new photos in Gallery
                Log.d("FDSfds" , "DSFfsdfd")


            }
        })


    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId);
        val action = intent?.action
        action?.let { ActionName ->
            if (ActionName == START_IMAGE_SERVICE) {
                startRecording()
                notifyToUserForForegroundService()
                getUpdateLocation()
                return START_STICKY

            } else if (ActionName == STOP_IMAGE_SERVICE) {
                stopRecording()
                stopForeground(true)
                stopSelf()
                stopUpdateLocation()
                return START_NOT_STICKY
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
                    Log.d("FDsfdsdsf", "SFdsa" + location.latitude + " " + location.longitude)
                    val intent = Intent(LocationService.BROADCAST_NEW_LOCATION_DETECTED)
                    intent.putExtra("EXTRA_LOCATION", p0)
                    LocalBroadcastManager.getInstance(this@ImageService).sendBroadcast(intent)

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
        val notificationIntent = Intent(this, TowingActivity::class.java)
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