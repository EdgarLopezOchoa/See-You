package com.example.seeyou.KotlinServices

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.seeyou.R
import com.google.android.gms.location.*

class RoutesService : Service() {
    private val NOTIFICATION_CHANNEL_ID = "My_Routes_Background"
    private val TAG = "LocationService"
    var requestQueue: RequestQueue? = null

    override fun onCreate() {
        super.onCreate()

    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        try{
            Ubicacion()
            return Service.START_STICKY

        }catch(e: Exception){

        }
        return Service.START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {

        try{
            super.onDestroy()
            isServiceStarted = false

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                var startservice1 = Intent(this@RoutesService, RoutesService::class.java)
                startForegroundService(Intent(startservice1))
                onResume()
            }
        }catch(e: Exception){

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

        try{
            val UPDATE_INTERVAL = (300000 /* 5 min */).toLong()
            val FASTEST_INTERVAL: Long = 15 /* 3 sec */
            val distance_interval = 50.0f
            var mLocationRequest: LocationRequest? = null
            LocationService.isServiceStarted = true
            // Create the location request to start receiving updates
            mLocationRequest = LocationRequest()
            //mLocationRequest.setInterval(UPDATE_INTERVAL)
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
                        guardarruta(locationResult.lastLocation.latitude,
                            locationResult.lastLocation.longitude,
                            this@RoutesService)

                        /*UpdateLocation(
                            locationResult.lastLocation.latitude,
                            locationResult.lastLocation.longitude,
                            this@LocationService
                        )*/
                    }
                },
                Looper.myLooper()!!
            )
        }catch(e: Exception){

        }
    }

    fun guardarruta(latitud: Double, longitud: Double,context: Context) {

        isServiceStarted = true
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            "https://mifolderdeproyectos.online/SEEYOU/puntos_recorridos.php", Response.Listener {

            }, Response.ErrorListener {

            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                val preferences: SharedPreferences
                val id_usuario: Int
                preferences = context.getSharedPreferences("sesion", Context.MODE_PRIVATE)
                id_usuario = preferences.getInt("id", 0)
                params["id"] = id_usuario.toString() + ""
                params["latitud"] = latitud.toString() + ""
                params["longitud"] = longitud.toString() + ""
                return params
            }
        }

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this@RoutesService)
        }
        requestQueue?.add<String>(stringRequest)

    }


}

class BootDeviceReceiversRouts : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            ContextCompat.startForegroundService(it, Intent(it, RoutesService::class.java))
        }
    }


}