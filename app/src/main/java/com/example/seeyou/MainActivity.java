package com.example.seeyou;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {

    RutasFragment RutasFragment = new RutasFragment();
    MapsFragment mapsFragment = new MapsFragment();
    PerfilFragment perfilFragment = new PerfilFragment();
    MarkersFragment markersFragment = new MarkersFragment();
    SharedPreferences preferences;

    private com.google.android.gms.location.LocationRequest mLocationRequest;

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    SweetAlertDialog Eliminar_Marcador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startLocationUpdates();

        preferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);

        if (preferences.getBoolean("fondo2", false) == true){
            Window window = getWindow();

            window.setBackgroundDrawableResource(R.drawable.fondodegradado2);

            Toolbar toolbar = findViewById(R.id.navegador1);

            setSupportActionBar(toolbar);

        }else if(preferences.getBoolean("fondo", false) == true){
            Window window = getWindow();

            window.setBackgroundDrawableResource(R.drawable.fondodegradado);

            Toolbar toolbar = findViewById(R.id.navegador1);

            setSupportActionBar(toolbar);
        }
        else if(preferences.getBoolean("fondo3", false) == true){
            Window window = getWindow();

            window.setBackgroundDrawableResource(R.drawable.fondodegradado3);

            Toolbar toolbar = findViewById(R.id.navegador1);

            setSupportActionBar(toolbar);
        }
        else if(preferences.getBoolean("fondo4", false) == true){
            Window window = getWindow();

            window.setBackgroundDrawableResource(R.drawable.fondodegradado4);

            Toolbar toolbar = findViewById(R.id.navegador1);

            setSupportActionBar(toolbar);
        }

        ejecutar();

        //llama al fragmento de mapa y lo pone en el FrameLayout
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.FrameLayout,mapsFragment);
        transaction.commit();

        BottomNavigationView navigation = findViewById(R.id.navegador);
        navigation.setOnNavigationItemSelectedListener(mOnNavigacionItemSelectedListener);
    }
    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigacionItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            //la navegacion entre fragmentos de la barra inferior

            switch (item.getItemId()){
                case R.id.RutasFragment:
                    loadFragment(RutasFragment);
                    return true;
                case R.id.MapsFragment:
                    loadFragment(mapsFragment);
                    return true;
                case R.id.PerfilFragment:
                    loadFragment(perfilFragment);
                    return true;
            }
            return false;
        }
    };

    public void loadFragment(Fragment fragment){

        //remplaza el fragmentLayour por los fragmentos
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.FrameLayout,fragment);
        transaction.commit();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == event.KEYCODE_BACK) {

            Eliminar_Marcador = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
            Eliminar_Marcador.setTitleText("¿Estas Seguro?");
            Eliminar_Marcador.setContentText("Estas Apunto De Salir De See You...,\n " +
                    "See You Seguira Trabajando En Segundo Plano.");
            Eliminar_Marcador.setConfirmText("Salir");
            Eliminar_Marcador.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    Eliminar_Marcador.dismiss();
                }
            });
            Eliminar_Marcador.setCancelText("Cancelar");
            Eliminar_Marcador.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    Eliminar_Marcador.dismiss();
                }
            });
            Eliminar_Marcador.show();


        }
        return super.onKeyDown(keyCode, event);
    }




    private void ejecutar(){
        final Handler handler= new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (preferences.getBoolean("fondo2", false) == true){
                    Window window = getWindow();

                    window.setBackgroundDrawableResource(R.drawable.fondodegradado2);

                    Toolbar toolbar = findViewById(R.id.navegador1);

                    setSupportActionBar(toolbar);

                }else if(preferences.getBoolean("fondo", false) == true){
                    Window window = getWindow();

                    window.setBackgroundDrawableResource(R.drawable.fondodegradado);

                    Toolbar toolbar = findViewById(R.id.navegador1);

                    setSupportActionBar(toolbar);
                }
                else if(preferences.getBoolean("fondo3", false) == true){
                    Window window = getWindow();

                    window.setBackgroundDrawableResource(R.drawable.fondodegradado3);

                    Toolbar toolbar = findViewById(R.id.navegador1);

                    setSupportActionBar(toolbar);
                }
                else if(preferences.getBoolean("fondo4", false) == true){
                    Window window = getWindow();

                    window.setBackgroundDrawableResource(R.drawable.fondodegradado4);

                    Toolbar toolbar = findViewById(R.id.navegador1);

                    setSupportActionBar(toolbar);
                }
                handler.postDelayed(this,10);//se ejecutara cada 10 segundos
            }
        },50);
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
        // New location has now been determined
        /*String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());*/
    }
}