package com.example.seeyou.KotlinServices

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.example.seeyou.R
import com.example.seeyou.services.UpdateLocation
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*

open class LocationService : Service() {
    private val NOTIFICATION_CHANNEL_ID = "My_Location_Background"
    private val TAG = "LocationService"

    override fun onCreate() {
        super.onCreate()
        val iconoNotifica =
            BitmapFactory.decodeResource(getResources(), R.mipmap.icon_round)

        isServiceStarted = true
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setOngoing(false)
                .setSmallIcon(R.mipmap.icon)
                .setLargeIcon(iconoNotifica)
                .setContentTitle("Ubicacion En Segundo Plano")
                .setContentText(
                    "ESTA APLICACION ESTA HACIENDO USO DE LA UBICACION EN SEGUNDO PLANO PARA PODER" +
                            " PROPORCIONARTE A TI Y LOS USUARIOS QUE USAN LA APLICACION UNA MEJOR EXPERIENCIA"
                )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_ID, NotificationManager.IMPORTANCE_MIN
            )



            notificationChannel.description = NOTIFICATION_CHANNEL_ID
            notificationChannel.setSound(null, null)
            notificationManager.createNotificationChannel(notificationChannel)
            startForeground(1, builder.build())
        }
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Ubicacion()
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceStarted = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var startservice1 = Intent(this@LocationService, LocationService::class.java)
            startForegroundService(Intent(startservice1))
            onResume()
        }
    }

    companion object {
        var mLocation: Location? = null
        var isServiceStarted = false
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        onResume()
    }

    @SuppressLint("MissingPermission")
    fun Ubicacion() {
        val UPDATE_INTERVAL = (10 * 1000 /* 10 sec */).toLong()
        val FASTEST_INTERVAL: Long = 3000 /* 3 sec */
        val distance_interval = 20.0f
        var mLocationRequest: LocationRequest? = null
        // Create the location request to start receiving updates
        mLocationRequest = LocationRequest()
        mLocationRequest.setInterval(UPDATE_INTERVAL)
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL)
        mLocationRequest.setSmallestDisplacement(distance_interval)

        // Create LocationSettingsRequest object using location request
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)
        val locationSettingsRequest = builder.build()


        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(
            mLocationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    // do work here
                    UpdateLocation(
                        locationResult.lastLocation.latitude,
                        locationResult.lastLocation.longitude,
                        this@LocationService
                    )
                }
            },
            Looper.myLooper()!!
        )
    }

}


class BootDeviceReceivers : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            ContextCompat.startForegroundService(it, Intent(it, LocationService::class.java))
        }
    }


}