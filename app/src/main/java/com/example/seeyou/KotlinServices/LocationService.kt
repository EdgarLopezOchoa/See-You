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
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.seeyou.MainActivity
import com.example.seeyou.R
import com.google.android.gms.location.*
import org.json.JSONArray
import org.json.JSONException


open class LocationService : Service() {
    private val NOTIFICATION_CHANNEL_ID = "My_Location_Background"
    private val TAG = "LocationService"
    var requestQueue: RequestQueue? = null
    var requestQueue2: RequestQueue? = null
    var preferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    var pressedkeys = ArrayList<Int>()
    var requestQueue3: RequestQueue? = null

    override fun onCreate() {
        super.onCreate()
        try {


            val iconoNotifica =
                BitmapFactory.decodeResource(resources, R.mipmap.icon_round)


            isServiceStarted = true
            val builder: NotificationCompat.Builder =
                NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setOngoing(false)
                    .setSmallIcon(R.mipmap.icon)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon))
                    .setContentTitle("Ubicacion En Segundo Plano")
                    .setCategory(Notification.CATEGORY_SERVICE)
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

        } catch (e: Exception) {


        }
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        UbicacionRoute()
        Ubicacion()
        ejecutar()
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {

        try {
            super.onDestroy()
            isServiceStarted = false
            var startservice1 = Intent(this@LocationService, LocationService::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            startForegroundService(startservice1)

             }
        } catch (e: Exception) {

        }
    }

    companion object {
        var mLocation: Location? = null
        var isServiceStarted = false
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        Ubicacion()
        UbicacionRoute()
        ejecutar()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        onResume()
    }

    @SuppressLint("MissingPermission")
    fun Ubicacion() {

        try {
            val UPDATE_INTERVAL = (300000 /* 5 min */).toLong()
            val FASTEST_INTERVAL: Long = 3000 /* 3 sec */
            val distance_interval = 20.0f
            var mLocationRequest: LocationRequest? = null
            isServiceStarted = true
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
                        ActualizarUbicacion(
                            locationResult.lastLocation.latitude,
                            locationResult.lastLocation.longitude,
                            this@LocationService
                        )

                        /*UpdateLocation(
                            locationResult.lastLocation.latitude,
                            locationResult.lastLocation.longitude,
                            this@LocationService
                        )*/
                    }
                },
                Looper.myLooper()!!
            )
        } catch (e: Exception) {

        }
    }

    open fun ActualizarUbicacion(latitud: Double, longitud: Double, context: Context) {
        try {
            val stringRequest: StringRequest = object : StringRequest(
                Method.POST,
                "https://mifolderdeproyectos.online/SEEYOU/ubicacion_usuario.php",
                Response.Listener {

                }, Response.ErrorListener { }) {
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
            if (requestQueue2 == null) {
                requestQueue2 = Volley.newRequestQueue(context)
            }
            requestQueue2?.add<String>(stringRequest)
        } catch (e: Exception) {
        }
    }

    @SuppressLint("MissingPermission")
    fun UbicacionRoute() {

        try {
            val UPDATE_INTERVAL = (300000 /* 5 min */).toLong()
            val FASTEST_INTERVAL: Long = 20000 /* 20 sec */
            val distance_interval = 35.0F
            var mLocationRequest: LocationRequest? = null
            RoutesService.isServiceStarted = true
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
                        guardarruta(
                            locationResult.lastLocation.latitude,
                            locationResult.lastLocation.longitude,
                            this@LocationService
                        )
                    }
                },
                Looper.myLooper()!!
            )
        } catch (e: Exception) {

        }
    }

    fun guardarruta(latitud: Double, longitud: Double, context: Context) {
        try {

            val stringRequest: StringRequest = object : StringRequest(
                Method.POST,
                "https://mifolderdeproyectos.online/SEEYOU/puntos_recorridos.php",
                Response.Listener {

                },
                Response.ErrorListener {

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
                requestQueue = Volley.newRequestQueue(this@LocationService)
            }
            requestQueue?.add<String>(stringRequest)

        } catch (e: Exception) {

        }
    }

    /* @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {


        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

       pressedkeys.add(keyCode);

        if(event.isLongPress())
            if(pressedkeys.contains(24) && pressedkeys.contains(25)) {

                EnviarAlerta();

        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        pressedkeys.removeAll(Collections.singleton(keyCode));
        return false;
    }*/
    fun ejecutar() {
        val handler2 = Handler()
        handler2.postDelayed(object : Runnable {
            override fun run() {
                BuscarAlertas()
                handler2.postDelayed(this, 10000)
            }
        }, 10000)
    }

    fun BuscarAlertas() {
        NotificationService.isServiceEnable = true
        val preferences: SharedPreferences
        preferences = getSharedPreferences("sesion", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        val stringRequest = StringRequest(
            Request.Method.GET,
            "https://mifolderdeproyectos.online/SEEYOU/Notificaciones_Alertas.php?iduser=" + preferences.getInt(
                "id",
                0
            )
                    + "&idnotifi=" + preferences.getInt("idNotificacion", 0),
            { response ->
                try {
                    val array = JSONArray(response)
                    var notifi = 2
                    for (i in 0 until array.length()) {
                        val cajas = array.getJSONObject(i)
                        Notificar(
                            notifi, cajas.getString("nombre") + " "
                                    + cajas.getString("apellido"), cajas.getInt("notificacion")
                        )
                        notifi = notifi + 1
                        if (i + 1 == array.length()) {
                            val preferences: SharedPreferences
                            preferences = getSharedPreferences("sesion", MODE_PRIVATE)
                            val editor: SharedPreferences.Editor = preferences.edit()
                            editor.putInt("idNotificacion", cajas.getInt("notificacion"))
                            editor.commit()
                        }
                    }
                } catch (e: JSONException) {
                }
            }
        ) { }
        if (requestQueue3 == null) {
            requestQueue3 = Volley.newRequestQueue(this@LocationService)
        } else {
            requestQueue3!!.add(stringRequest)
        }
    }


    open fun Notificar(notID: Int, Nombre: String, id_notifi: Int) {
        val creador: NotificationCompat.Builder
        val canalID = "MiCanal01"
        val notificador =
            getSystemService(MainActivity.NOTIFICATION_SERVICE) as NotificationManager
        // Si nuestro dispositivo tiene Android 8 (API 26, Oreo) o superior
        creador = NotificationCompat.Builder(this@LocationService, canalID)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canalNombre = "ALERTA"
            val canalDescribe = "$Nombre NECESITA TU AYUDA!!!!"
            val importancia = NotificationManager.IMPORTANCE_MAX
            @SuppressLint("WrongConstant") val miCanal =
                NotificationChannel(canalID, canalNombre, importancia)
            miCanal.description = canalDescribe
            miCanal.enableLights(true)
            //miCanal.lightColor = Color.BLUE // Esto no lo soportan todos los dispositivos
            miCanal.enableVibration(true)
            notificador.createNotificationChannel(miCanal)
        }
        val iconoNotifica = BitmapFactory.decodeResource(resources, R.mipmap.emergency_foreground)
        val iconoSmall = R.mipmap.icon
        creador.setSmallIcon(iconoSmall)
        creador.setLargeIcon(iconoNotifica)
        creador.setContentTitle("ALERTA!!")
        creador.setContentText("$Nombre NECESITA TU AYUDA!!!!")
        creador.setStyle(NotificationCompat.BigTextStyle().bigText("$Nombre NECESITA TU AYUDA!!!!"))
        creador.setChannelId(canalID)
        creador.setColorized(true)
        creador.setDefaults(Notification.DEFAULT_ALL)
        notificador.notify(notID, creador.build())
    }

}

class BootDeviceReceivers : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            ContextCompat.startForegroundService(it, Intent(it, LocationService::class.java))
        }
    }


}