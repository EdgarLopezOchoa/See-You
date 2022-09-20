package com.example.seeyou;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class ServiceLocation extends Service {


     com.google.android.gms.location.LocationRequest mLocationRequest;
     long UPDATE_INTERVAL = 5000;  /* 10 secs */
     long FASTEST_INTERVAL = 5000; /* 5 sec */
    SharedPreferences preferences;
    int id_usuario;

    @Override
    public void onCreate(){

        preferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        id_usuario = preferences.getInt("id",0);
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int idProcess){
        startLocationUpdates();
        return START_STICKY;
    }

    public void onDestroy(){

    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @SuppressLint("MissingPermission")
    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new com.google.android.gms.location.LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(ServiceLocation.this);
        settingsClient.checkLocationSettings(locationSettingsRequest);


        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        getFusedLocationProviderClient(ServiceLocation.this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChanged(locationResult.getLastLocation());


                    }
                },
                Looper.myLooper());
    }

    public void onLocationChanged(Location location) {

        ActualizarUbicacion(location.getLatitude(),location.getLongitude());

        /*Ringtone ringtone = RingtoneManager.getRingtone (this, RingtoneManager.getDefaultUri (RingtoneManager.TYPE_RINGTONE));
        ringtone.play ();

        Toast.makeText (this, "Este es mi proceso en segundo plano:", Toast.LENGTH_LONG) .show ();

        SystemClock.sleep (2000);
        ringtone.stop ();*/

    }


    public void ActualizarUbicacion(double latitud, double longitud) {


        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://mifolderdeproyectos.online/SEEYOU/ubicacion_usuario.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


            }

        }, new com.android.volley.Response.ErrorListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onErrorResponse(VolleyError error) {




            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                preferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);
                id_usuario = preferences.getInt("id",0);

                params.put("id", id_usuario+"");
                params.put("latitud", latitud + "");
                params.put("longitud", longitud + "");

                return params;
            }

        };


        RequestQueue requestQueue = Volley.newRequestQueue(ServiceLocation.this);
        requestQueue.add(stringRequest);

    }
}
