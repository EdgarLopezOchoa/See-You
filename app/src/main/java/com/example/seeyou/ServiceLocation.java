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
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
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

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class ServiceLocation extends Service {

    double longitud,latitud;
    private com.google.android.gms.location.LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 sec */
    SharedPreferences preferences;
    int id_usuario = 0;

    @Override
    public void onCreate(){
        preferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        id_usuario = preferences.getInt("id",0);

    }

    @Override
    public int onStartCommand(Intent intent, int flag, int idProcess){
        final Handler handler2= new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {

               startLocationUpdates();
                handler2.postDelayed(this,2000);
            }
        },2000);
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
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);


        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChanged(locationResult.getLastLocation());


                    }
                },
                Looper.myLooper());
    }

    public void onLocationChanged(Location location) {
        latitud = location.getLatitude();
        longitud = location.getLongitude();


        ActualizarUbicacion();
    }


    public void ActualizarUbicacion() {


        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://mifolderdeproyectos.online/SEEYOU/ubicacion_usuario.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {


                } catch (Exception e) {

                    new SweetAlertDialog(ServiceLocation.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Algo Salio Mal..")
                            .setContentText("Ubo Un Fallo En La App... Contacte Con El Equipo De Soporte....")
                            .show();
                }

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
