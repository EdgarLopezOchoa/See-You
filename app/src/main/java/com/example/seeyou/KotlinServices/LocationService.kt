package com.example.seeyou.KotlinServices

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.seeyou.R
import com.example.seeyou.services.UpdateLocation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class LocationService : Service() {
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

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val timer = Timer()
        LocationHelper().startListeningUserLocation(
            this, object : MyLocationListener {
                override fun onLocationChanged(location: Location?) {
                    mLocation = location
                    mLocation?.let {
                        AppExecutors.instance?.networkIO()?.execute {
                            val apiClient = ApiClient.getInstance(this@LocationService)
                                .create(ApiClient::class.java)
                            val response = apiClient.updateLocation()
                            response.enqueue(object : Callback<LocationResponse> {
                                override fun onResponse(
                                    call: Call<LocationResponse>,
                                    response: Response<LocationResponse>
                                ) {

                                    UpdateLocation(it.latitude, it.longitude, applicationContext)

                                }

                                override fun onFailure(call: Call<LocationResponse>, t: Throwable) {

                                }
                            })
                        }
                    }
                }
            })
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceStarted = false
    }

    companion object {
        var mLocation: Location? = null
        var isServiceStarted = false
    }
}

class BootDeviceReceivers : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            ContextCompat.startForegroundService(it, Intent(it, LocationService::class.java))
        }
    }
}